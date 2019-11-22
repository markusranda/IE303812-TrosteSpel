package no.ntnu.trostespel.dispatch;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.EvictingQueue;
import no.ntnu.trostespel.PlayerActions;
import no.ntnu.trostespel.Tickable;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.exception.IdentityMismatchException;
import no.ntnu.trostespel.exception.PlayerDisconnectedException;
import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.model.ConnectionStatus;
import no.ntnu.trostespel.model.Connections;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.net.DatagramPacket;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class holding all necessary information to handle a given DatagramPacket
 * each Player should gets its own unique processor object, as the class keeps track of some
 * player-specific information in order to perform lag-compensation
 */
public class DispatchProcessor implements Tickable {

    private Deque<DatagramPacket> tasks; // the packets to process

    private long pid = -1;
    private DatagramPacket packet;
    private PlayerCmdProcessor cmdProcessor;
    private GameState<PlayerState, MovableState> gameState;
    private Connections connections;
    private PacketDeserializer deserializer;
    private Runnable worker;
    private Kryo kryo;

    private AtomicLong currentTick;
    private volatile boolean running = false;
    private long lastTick = -999;

    // dispatch strategy fields
    private boolean step = false;
    private MovingAverage movingAvg = new MovingAverage(CommunicationConfig.TICKRATE);
    private Queue<PlayerActions> excessActions;// hold actions that arrive early for potential later use
    private static final int CACHED_ACTIONS_SIZE = CommunicationConfig.TICKRATE / 10;
    ;
    private static final double MAX_MA_LENIENCY = 1d / CommunicationConfig.TICKRATE;
    private static final int MAX_ACTIONS_AGE = CommunicationConfig.TICKRATE / 10;


    public DispatchProcessor(GameState<PlayerState, MovableState> gameState, AtomicLong tickCounter) {
        this.gameState = gameState;
        this.cmdProcessor = new PlayerCmdProcessor(gameState);
        this.deserializer = new PacketDeserializer();

        this.excessActions = EvictingQueue.create(CACHED_ACTIONS_SIZE);
        this.tasks = new ConcurrentLinkedDeque<>();
        this.kryo = new Kryo();
        this.kryo.register(PlayerActions.class);
        this.worker = getWorker();
        this.currentTick = tickCounter;
        this.connections = Connections.getInstance();
    }

    /**
     * enqueue a task for execution. Starts thread execution if queue goes from empty to non-empty
     * The running thread will run until the task queue is emptied.
     *
     * @param packet   the packet to process
     * @param executor the executor to execute on
     */
    void tryRun(DatagramPacket packet, ExecutorService executor) {
        if (running) {
            enqueueTask(packet);
        } else {
            running = true;
            enqueueTask(packet);
            executor.execute(this.worker);
        }
    }

    /**
     * return the runnable containing the main logic of the Processor class
     *
     * @return
     */
    private Runnable getWorker() {
        return () -> {
            try {
                if (lastTick == -999) lastTick = currentTick.get() - 1;
                while (!tasks.isEmpty()) {
                    work2();
                }
            } catch (PlayerDisconnectedException e) {
                e.printStackTrace();
            } finally {
                lastTick = currentTick.get();
                running = false;
            }
        };
    }

    /**
     * Polls tasks from task queue, and decides if they can be executed
     * Uses a moving average of the amount of executed tasks each tick to decide how to dispatch
     * This method ensures that a player cannot send requests faster than the server tickrate, and also
     * tries to compensate for packets arriving with irregular timing
     * <p>
     * Moving average values should be interpreted the following way:
     * - ma = 0: Amount of executed tasks is the same as expected
     * - ma < 0: Amount of executed tasks is less than expected
     * - ma > 0: Amount of executed tasks is more than expected
     *
     */
    private void work2() throws PlayerDisconnectedException {
        // todo come up with better method name 😂
        if (step) {
            movingAvg.step();
            step = false;
        }

        getPacket();

        PlayerActions actions = deserializer.deserialize(packet);
        actions.time = currentTick.get();
        this.pid = actions.pid;

        if (validateConnection()) {
            createPlayerInstanceIfNotExists();

            double avg = movingAvg.getAverage();
            if (avg == 0) {
                // perfect execution
                processCmd(actions);
            } else if (avg < 0) {
                // executed too little
                processCmd(actions);
                handleLowExecution();
            } else if (avg > MAX_MA_LENIENCY) {
                // executed too much
                excessActions.offer(actions);
            }
        } else {
            handleIllegalConnection();
        }
    }

    private void getPacket() {
        // get packet
        this.packet = tasks.poll();
        if (packet == null) {
            throw new RuntimeException("Fetched NULL task from task queue");
        }
    }

    /**
     * Tries to recover the moving average by executing cached packages
     */
    private void handleLowExecution() {
        while (!excessActions.isEmpty() || movingAvg.getAverage() > MAX_MA_LENIENCY) {
            PlayerActions recoveredAction = excessActions.poll();
            if (recoveredAction != null) {
                long age = currentTick.get() - recoveredAction.time;
                if (age > MAX_ACTIONS_AGE) continue;
                processCmd(recoveredAction);
            }
        }
    }

    private void processCmd(PlayerActions actions) {
        movingAvg.accumulate(1);
        cmdProcessor.run(actions);
    }

    private void createPlayerInstanceIfNotExists() {
        PlayerState playerState = gameState.getPlayers().get(pid);
        if (playerState == null) {
            playerState = new PlayerState(pid);
            gameState.getPlayers().put(pid, playerState);
        }
    }

    private void enqueueTask(DatagramPacket packet) {
        this.tasks.offer(packet);
    }

    private void handleIllegalConnection() {
        gameState.getPlayers().remove(pid);
    }

    private boolean validateConnection() throws PlayerDisconnectedException {
        boolean result = false;
        Connection conn = connections.find(pid);
        if (conn != null) {
            if (!conn.getAddress().equals(packet.getAddress())) {
                System.out.println("PID-InetAddress mismatch: ");
                System.out.println("Stored address: " + conn.getAddress().getHostAddress() + ", received address: " + packet.getAddress().getHostAddress());
                //throw new IdentityMismatchException();
            }
            if (conn.getConnectionStatus() != ConnectionStatus.CONNECTED) {
                throw new PlayerDisconnectedException();
            }
            result = true;
        }
        return result;
    }

    @Override
    public void onTick(long tick) {
        step = true;
    }
}

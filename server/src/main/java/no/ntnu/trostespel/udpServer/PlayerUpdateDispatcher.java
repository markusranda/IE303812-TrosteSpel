package no.ntnu.trostespel.udpServer;


import com.badlogic.gdx.utils.Pool;
import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.*;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import no.ntnu.trostespel.GameServer;
import no.ntnu.trostespel.Tickable;
import no.ntnu.trostespel.PlayerActions;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.game.GameStateMaster;
import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.model.ConnectionStatus;
import no.ntnu.trostespel.model.Connections;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class is responsible for queuing and demultiplexing incoming
 * updates, and dispathching them for processing.
 */
public class PlayerUpdateDispatcher extends ThreadPoolExecutor implements Tickable {

    private GameStateMaster gameStateMaster;
    private Pool<Processor> processorPool;
    private Map<SocketAddress, Processor> workers;
    private Table<SocketAddress, Long, PlayerActions> workers1;
    private volatile long currentTick = 0;
    private Connections connections;

    public PlayerUpdateDispatcher() {
        super(2, CommunicationConfig.MAX_PLAYERS * 3, CommunicationConfig.RETRY_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(64),
                new ThreadFactoryBuilder().setNameFormat("Dispathcher-thread-%d").build());
        gameStateMaster = GameStateMaster.getInstance();
        this.workers = new ConcurrentHashMap<>(8);
        workers1 = HashBasedTable.create();
        connections = Connections.getInstance();
        processorPool = new Pool<Processor>() {
            @Override
            protected Processor newObject() {
                return new Processor(gameStateMaster.getGameState());
            }
        };
        GameServer.observe(this);
    }

    /**
     * dispatch actions for processing and update
     *
     * @param packet the packet to queue
     */
    public void dispatch(DatagramPacket packet) {
        SocketAddress socketAddr = packet.getSocketAddress();
        Processor worker = workers.get(socketAddr);
        if (worker != null) {
            Runnable processor = worker.tryRun(packet);
            if (processor != null) {
                execute(processor);
            }
        } else {
            Processor p = new Processor(gameStateMaster.getGameState());
            workers.put(socketAddr, p);
            execute(p.tryRun(packet));
        }
    }


    private Runnable futureListener() {
        return () -> {

        };
    }

    /**
     * Class holding all necessary information to handle a given DatagramPacket
     */
    class Processor {
        private long pid = -1;
        private DatagramPacket packet;
        private PlayerCmdProcessor cmdProcessor;
        private GameState<PlayerState, MovableState> gameState;
        private PacketDeserializer deserializer;
        private static final int MAX = 3;
        private Kryo kryo;

        private volatile boolean running = false;

        private long lastTick = -999;

        private Queue<PlayerActions> earlyActions;
        private Deque<DatagramPacket> tasks;

        private Runnable worker;

        public Processor(GameState<PlayerState, MovableState> gameState) {
            this.gameState = gameState;
            this.cmdProcessor = new PlayerCmdProcessor(gameState);
            this.deserializer = new PacketDeserializer();
            this.earlyActions = EvictingQueue.create(MAX);
            this.kryo = new Kryo();
            this.tasks = new ConcurrentLinkedDeque<>();
            this.kryo.register(PlayerActions.class);
            this.worker = getWorker();
        }

        /**
         * enqueue a task for execution, and return the runnable only if it is not already running
         * @param packet
         * @return the Runnable if it is available for execution, or null if else
         */
        Runnable tryRun(DatagramPacket packet) {
            if (running) {
                enqueueTask(packet);
                System.out.println("Currently holding " + tasks.size() + " tasks . . .");
                return null;
            } else {
                running = true;
                enqueueTask(packet);
                return this.worker;
            }
        }

        /**
         * return the runnable containing the main logic of the Processor class
         * @return
         */
        private Runnable getWorker() {
            return () -> {
                try {
                    while (!tasks.isEmpty()) {
                        work();
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    handleIllegalConnection();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    handleIllegalConnection();
                } finally {
                    lastTick = currentTick;
                    running = false;
                }
            };
        }

        private void work() throws IllegalAccessException {
            this.packet = tasks.poll();
            if (packet == null) {
                System.out.println("ERROR NPE");
                running = false;
                return;
            }
            PlayerActions actions = deserializer.deserialize(packet);
            this.pid = actions.pid;
            if (validateConnection()) {

                createPlayerInstanceIfNotExists();

                // check if the player has already been handled this tick
                final long timeSinceLastTick = currentTick - lastTick;
                if (timeSinceLastTick > 0) {
                    if (timeSinceLastTick > 1) {
                        // player missed a tick or more
                        tryCompensateForMissedTicks(timeSinceLastTick);
                    }
                    cmdProcessor.run(actions);
                } else {
                    // packet arrived early
                    earlyActions.offer(kryo.copy(actions));
                }
            } else {
                handleIllegalConnection();
            }
        }
        
        private void tryCompensateForMissedTicks(long timeSinceLastTick) {
            final long missedTicks = timeSinceLastTick - 1;
            if (!earlyActions.isEmpty()) {
                long numActionsToRecover = Math.min(timeSinceLastTick, MAX);
                for (int i = 0; i < numActionsToRecover; i++) {
                    PlayerActions recoveredActions = earlyActions.poll();
                    if (recoveredActions != null) {
                        cmdProcessor.run(recoveredActions);
                    }
                }
            }
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

        private boolean validateConnection() throws IllegalAccessException {
            boolean result = false;
            Connection conn = connections.find(pid);
            if (conn != null) {
                if (!conn.getAddress().equals(packet.getAddress())) {
                    System.out.println("PID-InetAddress mismatch: ");
                    System.out.println("Stored address: " + conn.getAddress().getHostAddress() +", received address: " + packet.getAddress().getHostAddress());
                    // throw new IllegalAccessException("Wrong ip !?!");
                }
                if (conn.getConnectionStatus() != ConnectionStatus.CONNECTED) {
                    throw new IllegalStateException("Player is disconnected");
                }
                result = true;
            }
            return result;
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        processorPool.free((Processor) r);
    }

    @Override
    public void onTick(long tick) {
        this.currentTick = tick;
    }
}

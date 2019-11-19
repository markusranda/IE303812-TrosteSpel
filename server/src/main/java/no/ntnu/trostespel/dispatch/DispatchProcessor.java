package no.ntnu.trostespel.dispatch;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.EvictingQueue;
import no.ntnu.trostespel.PlayerActions;
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
public class DispatchProcessor {
        private long pid = -1;
        private DatagramPacket packet;
        private PlayerCmdProcessor cmdProcessor;
        private GameState<PlayerState, MovableState> gameState;
        private Connections connections;
        private PacketDeserializer deserializer;
        private static final int MAX = 3;
        private Kryo kryo;

        private volatile boolean running = false;

        private long lastTick = -999;

        private Queue<PlayerActions> earlyActions;
        private Deque<DatagramPacket> tasks;

        private Runnable worker;

        private AtomicLong currentTick;

        public DispatchProcessor(GameState<PlayerState, MovableState> gameState, AtomicLong tickCounter) {
            this.gameState = gameState;
            this.cmdProcessor = new PlayerCmdProcessor(gameState);
            this.deserializer = new PacketDeserializer();
            this.earlyActions = EvictingQueue.create(MAX);
            this.kryo = new Kryo();
            this.tasks = new ConcurrentLinkedDeque<>();
            this.kryo.register(PlayerActions.class);
            this.worker = getWorker();
            this.currentTick = tickCounter;
            this.connections = Connections.getInstance();
        }

        /**
         * enqueue a task for execution, and return the runnable only if it is not already running
         * @param packet
         * @return the Runnable if it is available for execution, or null if else
         */
        void tryRun(DatagramPacket packet, ExecutorService executor) {
            if (running) {
                enqueueTask(packet);
                System.out.println("Currently holding " + tasks.size() + " tasks . . .");
            } else {
                running = true;
                enqueueTask(packet);
                executor.execute(this.worker);
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
                    lastTick = currentTick.get();
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
                final long timeSinceLastTick = currentTick.get() - lastTick;
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

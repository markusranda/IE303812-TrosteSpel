package no.ntnu.trostespel.udpServer;


import com.badlogic.gdx.utils.Pool;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import no.ntnu.trostespel.GameServer;
import no.ntnu.trostespel.Tickable;
import no.ntnu.trostespel.PlayerActions;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.entity.Movable;
import no.ntnu.trostespel.game.GameStateMaster;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for queuing and demultiplexing incoming
 * updates, and dispathching them for processing.
 */
public class PlayerUpdateDispatcher extends ThreadPoolExecutor implements Tickable {

    private GameStateMaster gameStateMaster;
    private Pool<Processor> processorPool;
    private Map<SocketAddress, Long> workers;
    private long currentTick = 0;

    public PlayerUpdateDispatcher() {
        super(2, CommunicationConfig.MAX_PLAYERS, CommunicationConfig.RETRY_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(8),
                new ThreadFactoryBuilder().setNameFormat("Dispathcher-thread-%d").build());
        gameStateMaster = GameStateMaster.getInstance();
        this.workers = new ConcurrentHashMap<>(8);
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
     * masterGameState
     *
     * @param packet the packet to queue
     */
    public void dispatch(DatagramPacket packet) {
        SocketAddress socketAddr = packet.getSocketAddress();
        if (!workers.containsKey(socketAddr)) {
            workers.put(socketAddr, 0L);
        }
        if (workers.get(socketAddr) < currentTick) {
            workers.put(socketAddr, currentTick);
            execute(doDispatch(packet, currentTick));
        } else {
        }
    }

    private Runnable doDispatch(DatagramPacket packet, long currentTick) {
        return processorPool.obtain().setPacketToHandle(packet, currentTick);
    }

    /**
     * Class holding all necessary information to handle a given DatagramPacket
     */
    class Processor implements Runnable {
        private long pid = -1;
        private DatagramPacket packet;
        private long tick;
        private PlayerCmdProcessor cmdProcessor;
        private GameState<PlayerState, MovableState> gameState;
        private PacketDeserializer deserializer;

        public Processor(GameState<PlayerState, MovableState> gameState) {
            this.gameState = gameState;
            this.cmdProcessor = new PlayerCmdProcessor(gameState);
            this.deserializer = new PacketDeserializer();
        }

        Processor setPacketToHandle(DatagramPacket packet, long currentTick) {
            this.pid = -1;
            this.packet = packet;
            this.tick = currentTick;
            return this;
        }

        @Override
        public void run() {
            PlayerActions actions = deserializer.deserialize(packet);
            this.pid = actions.pid;
            PlayerState playerState = gameState.getPlayers().get(actions.pid); // TODO: Should check if player is connected
            if (playerState == null) {
                playerState = new PlayerState(actions.pid);
                gameState.getPlayers().put(actions.pid, playerState);
            }
            cmdProcessor.run(actions);
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

package no.ntnu.trostespel.udpServer;


import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.PooledLinkedList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import no.ntnu.trostespel.PlayerActions;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.game.MasterGameState;
import no.ntnu.trostespel.state.PlayerState;

import java.io.IOException;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * This class is responsible for queuing and demultiplexing incoming
 * updates, and dispathching them for processing.
 */
public class PlayerUpdateDispatcher extends ThreadPoolExecutor {

    private MasterGameState masterGameState;
    private Pool<PacketDeserializer> deserializerPool;
    private Map<SocketAddress, Long> workers;

    public PlayerUpdateDispatcher() {
        super(1, CommunicationConfig.MAX_PLAYERS, CommunicationConfig.RETRY_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(8),
                new ThreadFactoryBuilder().setNameFormat("Dispathcher-thread-%d").build());
        masterGameState = MasterGameState.getInstance();
        this.workers = new ConcurrentHashMap<>(8);
        deserializerPool = new Pool<PacketDeserializer>() {
            @Override
            protected PacketDeserializer newObject() {
                return new PacketDeserializer();
            }
        };
    }

    /**
     * dispatch actions for processing and update
     * masterGameState
     *
     * @param packet the packet to queue
     */
    public void dispatch(DatagramPacket packet) {
        long currentTick = GameServer.getTickcounter();
        SocketAddress socketAddr = packet.getSocketAddress();
        if (!workers.containsKey(socketAddr)) {
            workers.put(socketAddr, 0L);
        }
        if (workers.get(socketAddr) < currentTick) {
            workers.put(socketAddr, currentTick);
            execute(doDispatch(packet));
        } else {
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (r instanceof Updater) {
            long pid = ((Updater) r).getPid();
            updateMaster(pid);
        }
    }

    private Runnable doDispatch(DatagramPacket packet) {
        return new Updater(packet);
    }

    private void updateMaster(long pid) {
        masterGameState.update(pid);
    }

    class Updater implements Runnable {
        private long pid = -1;
        private DatagramPacket packet;

        public Updater(DatagramPacket packet) {
            this.packet = packet;
        }

        @Override
        public void run() {
            PacketDeserializer deserializer = deserializerPool.obtain();
            PlayerActions actions = deserializer.deserialize(packet);
            deserializerPool.free(deserializer);
            this.pid = actions.pid;
            PlayerState playerState = (PlayerState) masterGameState.getGameState().players.get(actions.pid);
            if (playerState == null) {
                playerState = new PlayerState(actions.pid);
                masterGameState.getGameState().players.put(actions.pid, playerState);
            }
            new PlayerUpdateProcessor(playerState, actions).run();
        }

        public long getPid() {
            return this.pid;
        }
    }
}

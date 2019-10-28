package no.ntnu.trostespel.udpServer;


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

    private Map<SocketAddress, Long> workers;

    public PlayerUpdateDispatcher() {
        super(1, 8, CommunicationConfig.RETRY_CONNECTION_TIMEOUT, TimeUnit.HOURS, new LinkedBlockingQueue<>(8),
                new ThreadFactoryBuilder().setNameFormat("Dispathcher-thread-%d").build());
        masterGameState = MasterGameState.getInstance();
        this.workers = new ConcurrentHashMap<>(8);
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
            System.out.println(workers.size());
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (r instanceof PlayerUpdateProcessor) {
            long pid = ((PlayerUpdateProcessor) r).getPid();
            updateMaster(pid);
        }
    }

    private Runnable doDispatch(DatagramPacket packet) {
        return new Runnable() {
            @Override
            public void run() {
                PlayerActions actions = new PacketDeserializer().deserialize(packet);
                PlayerState playerState = (PlayerState) masterGameState.getGameState().players.get(actions.pid);
                if (playerState == null) {
                    playerState = new PlayerState(actions.pid);
                    masterGameState.getGameState().players.put(actions.pid, playerState);
                }
                new PlayerUpdateProcessor(playerState, actions).run();
            }
        };
    }

    private void updateMaster(long pid) {
        masterGameState.update(pid);
    }
}

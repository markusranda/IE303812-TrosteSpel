package no.ntnu.trostespel.dispatch;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import no.ntnu.trostespel.GameServer;
import no.ntnu.trostespel.Tickable;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.game.GameStateMaster;
import no.ntnu.trostespel.model.Connections;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class is responsible for queuing and demultiplexing incoming
 * updates, and dispathching them for processing.
 */
public class PlayerUpdateDispatcher extends ThreadPoolExecutor {

    private GameStateMaster gameStateMaster;
    private Connections connections;
    private Map<SocketAddress, DispatchProcessor> workers;
    private AtomicLong tickCounter;

    public PlayerUpdateDispatcher() {
        super(2, CommunicationConfig.MAX_PLAYERS * 3, CommunicationConfig.RETRY_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(64),
                new ThreadFactoryBuilder().setNameFormat("Dispathcher-thread-%d").build());
        gameStateMaster = GameStateMaster.getInstance();
        connections = Connections.getInstance();
        this.workers = new ConcurrentHashMap<>(8);
        this.tickCounter = GameServer.getTickCounter();
    }

    /**
     * dispatch actions for processing and update
     *
     * @param packet the packet to queue
     */
    public void dispatch(DatagramPacket packet) {
        SocketAddress socketAddr = packet.getSocketAddress();
        DispatchProcessor worker = workers.get(socketAddr);
        if (worker != null) {
            worker.tryRun(packet, this);
        } else {
            worker = new DispatchProcessor(gameStateMaster.getGameState());
            GameServer.observe(worker);
            workers.put(socketAddr, worker);
            worker.tryRun(packet, this);
        }
    }
}

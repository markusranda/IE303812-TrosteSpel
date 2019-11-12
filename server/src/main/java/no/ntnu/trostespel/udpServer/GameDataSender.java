package no.ntnu.trostespel.udpServer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import no.ntnu.trostespel.GameServer;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.game.GameStateMaster;
import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.state.GameState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static no.ntnu.trostespel.config.CommunicationConfig.MAX_PLAYERS;
import static no.ntnu.trostespel.config.CommunicationConfig.RECEIVED_DATA_TYPE;

public class GameDataSender extends ThreadPoolExecutor{

    private GameServer master;
    private GameStateMaster gameStateMaster;
    private Gson gson = new Gson();
    GameState nextGameState;

    private int connectionsSize = 0;
    private AtomicInteger completedCount;

    public GameDataSender() {
        super(1, MAX_PLAYERS, CommunicationConfig.RETRY_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(16),
                new ThreadFactoryBuilder().setNameFormat("GameDataSender-%d").build());
        this.gameStateMaster = GameStateMaster.getInstance();
        completedCount = new AtomicInteger();
    }


    /**
     * Does the update tasks for the server.
     */
    public void broadcast(List<Connection> connections, long tick) {
        // Send GameState to all clients
        nextGameState = gameStateMaster.getGameState();
        nextGameState.setTick(tick);
        String json = gson.toJson(nextGameState, RECEIVED_DATA_TYPE);
        connectionsSize = connections.size();
        for (Connection con : connections) {
            execute(send(con, json));
        }
    }

    /**
     * Creates a new runnable with a game state and connection to send it to
     *
     * @param connection The Connection
     * @return Returns a runnable
     */
    private Runnable send(Connection connection, String json) {
        return () -> {
            DatagramPacket packet = new DatagramPacket(
                    json.getBytes(),
                    json.getBytes().length,
                    connection.getAddress(),
                    connection.getPort());

            packet.setData(json.getBytes());
            try {
                DatagramSocket socket = connection.getClientSocket();
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        completedCount.incrementAndGet();
        if (completedCount.get() >= connectionsSize) {
            nextGameState.getProjectilesStateUpdates().clear();
            completedCount.set(0);
            connectionsSize = 0;
        }
    }
}

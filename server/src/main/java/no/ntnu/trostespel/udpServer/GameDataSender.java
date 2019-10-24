package no.ntnu.trostespel.udpServer;

import com.google.gson.Gson;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.game.MasterGameState;
import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.model.Connections;
import no.ntnu.trostespel.state.GameState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static no.ntnu.trostespel.config.CommunicationConfig.RECEIVED_DATA_TYPE;
import static no.ntnu.trostespel.config.CommunicationConfig.MAX_PLAYERS;

public class GameDataSender extends ThreadPoolExecutor{

    private GameServer master;
    private MasterGameState masterGameState;
    private Gson gson = new Gson();
    GameState nextGameState;

    private int connectionsSize = 0;
    private AtomicInteger completedCount;

    public GameDataSender() {
        super(8, MAX_PLAYERS, 0, TimeUnit.HOURS, new LinkedBlockingQueue<>(16));
        this.masterGameState = MasterGameState.getInstance();
        completedCount = new AtomicInteger();
    }


    /**
     * Does the update tasks for the server.
     */
    public void broadcast(List<Connection> connections) throws InterruptedException {
        if (completedCount.get() != 0) {
            throw new InterruptedException("Game data broadcast was interrupted");
        }
        // Send GameState to all clients
        nextGameState = masterGameState.getGameState();
        // TODO: 15.10.2019 Add concurrency protection, since we will be modifying connecitons on the fly.
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
                DatagramSocket socket = new DatagramSocket();
                socket.send(packet);
                socket.close();
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

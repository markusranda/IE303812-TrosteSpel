package no.ntnu.trostespel;

import com.google.gson.Gson;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.game.MasterGameState;
import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.model.Connections;
import no.ntnu.trostespel.state.GameState;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.javers.core.diff.ListCompareAlgorithm.LEVENSHTEIN_DISTANCE;

/**
 * This class will do every task the server need to do each tick.
 */
class GameServer {


    private List<Connection> connections = Connections.getInstance().getConnections();
    private final Type RECEIVED_DATA_TYPE = CommunicationConfig.RECEIVED_DATA_TYPE;

    private MasterGameState masterGameState;
    private Javers javers;

    private Gson gson;

    private long tickCounter = 0;
    private long timerCounter = 0;

    private ThreadPoolExecutor executor;

    GameServer() {
        executor = new ThreadPoolExecutor(8, 8, 0, TimeUnit.HOURS, new LinkedBlockingQueue<>());
        masterGameState = MasterGameState.getInstance();
        gson = new Gson();

        masterGameState = MasterGameState.getInstance();

        javers = JaversBuilder.javers()
                .withListCompareAlgorithm(LEVENSHTEIN_DISTANCE)
                .build();

        System.out.println("Server is ready to handle incoming connections!");
        heartbeat();
    }

    /**
     * Runs the servers game engine main loop
     */
    private void heartbeat() {
        double ns = 1000000000.0 / CommunicationConfig.TICKRATE;
        double delta = 0;

        long lastTime = System.nanoTime();

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
        }
    }


    /**
     * Method which will do everything the servers game engine needs to do each tick
     */
    private void tick() {
        if (!connections.isEmpty()) {
            update();
        } else {
            if (tickCounter >= timerCounter) {
                System.out.println("Waiting for at least one connection..");
                timerCounter = tickCounter + 1000;
            }
        }
        tickCounter++;
    }

    /**
     * Does the update tasks for the server.
     */
    private void update() {
        // Send GameState to all clients
        // TODO: 15.10.2019 Add concurrency protection, since we will be modifying connecitons on the fly.
        for (Connection con : connections) {
            executor.execute(submitGameState(con)); //TODO: 15.10.2019 pool these runnables
        }
    }

    /**
     * Creates a new runnable with a game state and connection to send it to
     *
     * @param connection The Connection
     * @return Returns a runnable
     */
    private Runnable submitGameState(Connection connection) {
        GameState nextGameState = masterGameState.getGameState();
        String json = gson.toJson(nextGameState, RECEIVED_DATA_TYPE);
        if (!nextGameState.getProjectileStateUpdates().isEmpty()) {
            System.out.println(json);
        }
        nextGameState.getProjectileStateUpdates().clear();


        return () -> {
            DatagramPacket packet = new DatagramPacket(
                    json.getBytes(),
                    json.getBytes().length,
                    connection.getAddress(),
                    CommunicationConfig.CLIENT_UDP_GAMEDATA_RECEIVE_PORT);

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
}

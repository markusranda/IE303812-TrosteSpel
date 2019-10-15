package no.ntnu.trostespel;

import com.google.gson.Gson;
import no.ntnu.trostespel.config.ConnectionConfig;
import no.ntnu.trostespel.config.ServerConfig;
import no.ntnu.trostespel.game.MasterGameState;
import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.model.Connections;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import static org.javers.core.diff.ListCompareAlgorithm.LEVENSHTEIN_DISTANCE;

/**
 * This class will do every task the server need to do each tick.
 */
class GameServer {


    private final GameState dummySnapshot;
    private List<Connection> connections = Connections.getInstance().getConnections();
    private double time_passed = 0;
    private double tick_start_time = System.currentTimeMillis();
    private double time_per_timestep = ServerConfig.TICKRATE;
    private MasterGameState masterGameState;
    private Javers javers;

    GameServer() {

        // TODO: 15.10.2019 Create a dummySnapshot with only empty values
        dummySnapshot = null;


        // TODO: 15.10.2019 initialize masterGameState
        masterGameState = MasterGameState.getInstance();

        javers = JaversBuilder.javers()
                .withListCompareAlgorithm(LEVENSHTEIN_DISTANCE)
                .build();

        System.out.println("Server is ready to handle incoming connections!");



        long tickCounter = 0;
        long timerCounter = 0;
        while (true) {
            if (time_passed >= time_per_timestep) {
                tick_start_time = System.currentTimeMillis();
                if (!connections.isEmpty()) {
                    update();
                } else {
                    if (tickCounter >= timerCounter) {
                        System.out.println("Waiting for at least one connection..");
                        timerCounter = tickCounter + 1000;
                    }
                }
                tickCounter++;
                time_passed = 0;
            }
            time_passed += System.currentTimeMillis() - tick_start_time;
        }
    }

    /**
     * Does the update tasks for the server.
     */
    private void update() {
        // Send GameState to all clients
        // TODO: 15.10.2019 Add concurrency protection, since we will be modifying connecitons on the fly.
        for (Connection con : connections) {
            Runnable runnable = submitGameState(con);
            runnable.run();
        }
    }

    /**
     * Creates a new runnable with a game state and connection to send it to
     *
     * @param connection The Connection
     * @return Returns a runnable
     */
    private Runnable submitGameState(Connection connection) {

        // Compare previous snapshot to MainGameState
        /*GameState prevGameState = (GameState) connection.getSnapshotArray().getCurrent();
        if (prevGameState == null) {
            prevGameState = dummySnapshot;
        }
        Diff diff = javers.compare(prevGameState, nextGameState);

        // Save the next GameState to SnapshotArray
        //connection.getSnapshotArray().setAtCurrent(nextGameState);

        // Send the difference
        String json = javers.getJsonConverter().toJson(diff);*/

        //TODO: Remove this when snapshots work
        Gson gson = new Gson();
        GameState nextGameState = masterGameState.getGameState();
        String json = gson.toJson(nextGameState);

        return () -> {
            DatagramPacket packet = new DatagramPacket(
                    json.getBytes(),
                    json.getBytes().length,
                    connection.getAddress(),
                    ConnectionConfig.CLIENT_UDP_GAMEDATA_RECEIVE_PORT);

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

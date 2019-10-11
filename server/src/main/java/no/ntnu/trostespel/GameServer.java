package no.ntnu.trostespel;

import com.google.gson.Gson;
import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.model.Connections;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

/**
 * This class will do every task the server need to do each tick.
 */
class GameServer {

    private List<Connection> connections = Connections.getInstance().getConnections();
    private double time_passed = System.currentTimeMillis();
    private double time_per_timestep = 1000.0 / 100;

    GameServer() {
        while (true) {
            while (time_passed >= time_per_timestep) {
                while (!connections.isEmpty()) {
                    update();
                }
                time_passed -= time_per_timestep;
            }
        }
    }

    /**
     * Does the update tasks for the server.
     */
    private void update() {
        // Serialize GameState
        GameState gameState = GameState.getInstance();
        Gson gson = new Gson();
        String json = gson.toJson(gameState);

        // Send GameState to all connections
        for (Connection con : connections) {
            Runnable runnable = submitGameState(json, con);
            runnable.run();
        }
    }

    /**
     * Creates a new runnable with a game state and connection to send it to
     *
     * @param json The Game State
     * @param connection The Connection
     * @return Returns a runnable
     */
    private Runnable submitGameState(String json, Connection connection) {
        return () -> {
            DatagramPacket packet = new DatagramPacket(
                    json.getBytes(),
                    json.getBytes().length,
                    connection.getAddress(),
                    Connection.GAME_DATA_RETRIEVE_PORT);

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

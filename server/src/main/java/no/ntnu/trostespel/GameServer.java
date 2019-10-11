package no.ntnu.trostespel;

import com.badlogic.gdx.utils.Json;
import com.google.gson.Gson;
import no.ntnu.trostespel.config.ServerConnection;

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
            submitGameState(json, con);
        }
    }

    /**
     * Submits the GameState to all listening connections to the server
     *
     * @param json The GameState as json string
     * @param con  The receiving connection
     */
    private void submitGameState(String json, Connection con) {
        try {
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(json.getBytes(),
                    json.getBytes().length, con.getAddress(), 7080);
            socket.send(packet);
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }
}

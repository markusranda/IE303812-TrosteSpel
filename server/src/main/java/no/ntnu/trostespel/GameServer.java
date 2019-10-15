package no.ntnu.trostespel;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.Gson;
import no.ntnu.trostespel.config.Assets;
import no.ntnu.trostespel.config.ConnectionConfig;
import no.ntnu.trostespel.config.ServerConfig;
import no.ntnu.trostespel.controller.NetworkedPlayerController;
import no.ntnu.trostespel.controller.ObjectController;
import no.ntnu.trostespel.entity.Player;
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
    private double time_per_timestep = ServerConfig.TICKRATE;

    GameServer() {

        // Populate the GameState with testData
        populateGameStateTest();

        System.out.println("Server is ready to handle incoming connections!");
        while (time_passed >= time_per_timestep) {

            if (!connections.isEmpty()) {
                update();
            } else {
                System.out.println("Waiting for at least one connection..");
            }

            time_passed -= time_per_timestep;
        }
    }

    private void populateGameStateTest() {
        GameState gameState = GameState.getInstance();
        ObjectController playerController = new NetworkedPlayerController();
        Vector2 spawnLocation = new Vector2(800 / 2 - 64 / 2, 50);
        Player player = new Player(spawnLocation, Assets.lemurImage, playerController);
        gameState.addEntity(player);
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
        // TODO: 15.10.2019 Add concurrency protection, since we will be modifying connecitons on the fly.
        for (Connection con : connections) {
            Runnable runnable = submitGameState(json, con);
            runnable.run();
        }
    }

    /**
     * Creates a new runnable with a game state and connection to send it to
     *
     * @param json       The Game State
     * @param connection The Connection
     * @return Returns a runnable
     */
    private Runnable submitGameState(String json, Connection connection) {
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

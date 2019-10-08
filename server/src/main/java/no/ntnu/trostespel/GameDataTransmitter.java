package no.ntnu.trostespel;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameDataTransmitter {

    public GameDataTransmitter() { }

    /**
     * Executes the sendGameState for every connection known to the server
     */
    public void doSend() {
        ScheduledExecutorService gateKeeper;
        gateKeeper = Executors.newSingleThreadScheduledExecutor();

        // Create one executor for each available connection
        for (Connection connection: Connections.getInstance().getConnections()) {
            if (connection == null) continue;
            gateKeeper.scheduleAtFixedRate(sendGameState(
                    GameState.getInstance(), connection), 0,
                    100000 / ServerConfig.TICKRATE, TimeUnit.MICROSECONDS);
        }
    }

    /**
     * Creates a new runnable with a game state and connection to send it to
     *
     * @param gameState The Game State
     * @param connection The Connection
     * @return Returns a runnable
     */
    private Runnable sendGameState(GameState gameState, Connection connection) {
        return () -> {
            Gson gson = new Gson();
            String jsonStr = gson.toJson(gameState);

            int length = jsonStr.getBytes().length;
            DatagramPacket packet = new DatagramPacket(
                    jsonStr.getBytes(), length, connection.getAddress(), Connection.GAME_DATA_RETRIEVE_PORT);

            String json = new Gson().toJson(gameState);
            packet.setData(json.getBytes());
            try {
                DatagramSocket socket = new DatagramSocket();
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}

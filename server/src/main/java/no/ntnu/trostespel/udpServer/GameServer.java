package no.ntnu.trostespel.udpServer;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.game.MasterGameState;
import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.model.Connections;
import no.ntnu.trostespel.state.GameState;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.javers.core.diff.ListCompareAlgorithm.LEVENSHTEIN_DISTANCE;

/**
 * This class will do every task the server need to do each tick.
 */
public class GameServer implements Runnable{


    private List<Connection> connections = Connections.getInstance().getConnections();
    private MasterGameState masterGameState;

    private static long tickCounter = 0;
    private long timerCounter = 0;


    private List<Connection> connectionsToDrop = new ArrayList<>();

    GameDataReceiver receiver;
    GameDataSender sender;

    public GameServer() {
        masterGameState = MasterGameState.getInstance();
        try {
            this.receiver = new GameDataReceiver(CommunicationConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT);
            this.sender = new GameDataSender();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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


    public void tick() {
        if (!connections.isEmpty()) {
            update();
        } else {
            if (tickCounter >= timerCounter) {
                System.out.println("Waiting for at least one connection..");
                long currentTick = tickCounter;
                timerCounter = currentTick + 1000;
            }
        }
        tickCounter++;
    }

    private void update() {
        dropIdleConnections();
        broadcastUpdate();
    }

    private void broadcastUpdate() {
        try {
            sender.broadcast(Connections.getInstance().getConnections());
        } catch (InterruptedException e) {
            // Server is running too slow
            e.printStackTrace();
        }
    }

    private void dropIdleConnections() {
        for (Connection connection : Connections.getInstance().getConnections()) {
            double currentTime = System.currentTimeMillis();
            double timeArrived = connection.getTimeArrived();
            double timeSinceMillis = currentTime - timeArrived;
            if (timeSinceMillis > CommunicationConfig.RETRY_CONNECTION_TIMEOUT && timeArrived != 0.0) {
                System.out.println(connection.getAddress() + " - Timed out!");
                connectionsToDrop.add(connection);
            }
        }
        if (connectionsToDrop.size() > 0) {
            for (Connection connection : connectionsToDrop) {
                masterGameState.getGameState().players.remove(connection.getPid());
                Connections.getInstance().getConnections().remove(connection);
                System.out.println(connection.getUsername() + " - Got dropped from the game!");
            }
            connectionsToDrop.clear();
        }
    }



    public static long getTickcounter() {
        return tickCounter;
    }

    @Override
    public void run() {
        Thread receiverThread = new Thread(receiver, "GameData Receiver");
        receiverThread.start();

        System.out.println("Server is ready to handle incoming connections!");
        heartbeat();
    }
}
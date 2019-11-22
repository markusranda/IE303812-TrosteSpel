package no.ntnu.trostespel;

import no.ntnu.trostespel.model.ConnectionStatus;
import no.ntnu.trostespel.tcpServer.ConnectionManager;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.game.GameStateMaster;
import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.model.Connections;
import no.ntnu.trostespel.udpServer.GameDataReceiver;
import no.ntnu.trostespel.udpServer.GameDataSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static no.ntnu.trostespel.config.MapConfig.PVP_JUNGLE_ISLAND_FILENAME;
import static no.ntnu.trostespel.model.ConnectionStatus.CONNECTED;


/**
 * This class will do every task the server need to do each tick.
 */
public class GameServer {


    private List<Connection> connections = Connections.getInstance().getConnections();
    private GameStateMaster gameStateMaster;

    private static AtomicLong tickCounter = new AtomicLong(0);
    private long timerCounter = 0;

    private List<Connection> connectionsToDrop = new ArrayList<>();
    private static List<Tickable> observers = new ArrayList<>();

    private GameDataReceiver receiver;
    private GameDataSender sender;
    private ConnectionManager connectionManager;

    public GameServer() {
        gameStateMaster = GameStateMaster.getInstance();

        // Load map to be played
        String mapFileName = PVP_JUNGLE_ISLAND_FILENAME;
        System.out.println("Loading map " + mapFileName);
        try {
            this.receiver = new GameDataReceiver(CommunicationConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT);
            this.sender = new GameDataSender();
            System.out.println("Started GameDataSender " + this.sender.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread receiverThread = new Thread(receiver, "GameDataReceiver");
        receiverThread.start();
        System.out.println("Started GameDataReceiver " + this.receiver.toString());

        try {
            this.connectionManager = new ConnectionManager(CommunicationConfig.SERVER_TCP_CONNECTION_RECEIVE_PORT, mapFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread TcpThread = new Thread(connectionManager);
        TcpThread.setName("ConnectionClient");
        TcpThread.start();
        System.out.println("Started TCPClient with address " + connectionManager.getSocketAddress());


        System.out.println("Server is ready to handle incoming connections!");
        heartbeat();
    }

    private void heartbeat() {
        double ns = 1000000000.0 / CommunicationConfig.TICKRATE;
        double delta = 0;

        long lastTime = System.nanoTime();

        System.out.println("Starting tick-loop with frequecy " + ns + "ns");
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

    private void printDebug() {
        boolean noConnectedPlayers = true;
        for (Connection connection : connections) {
            if (connection.getConnectionStatus() == CONNECTED) {
                noConnectedPlayers = false;
                break;
            }
        }
        if (noConnectedPlayers) {
            System.out.println("Waiting for at least one connection..");
        }
    }

    private void tick() {
        final long lastTick = tickCounter.get();
        if (lastTick >= timerCounter) {
            // print some info every 1000 ticks
            printDebug();
            timerCounter = lastTick + 1000;
        }
        updateClients(lastTick);
        notifyObservers(tickCounter.incrementAndGet());
    }


    private void updateClients(long tick) {
        dropIdleConnections();
        broadcastUpdate(tick);
    }

    private void broadcastUpdate(long tick) {
        sender.broadcast(Connections.getInstance().getConnections(), tick);
    }

    private void dropIdleConnections() {
        for (Connection connection : Connections.getInstance().getConnections()) {
            if (connection.getConnectionStatus() == CONNECTED) {
                double currentTime = System.currentTimeMillis();
                double timeArrived = connection.getTimeArrived();
                double timeSinceMillis = currentTime - timeArrived;
                if (timeSinceMillis > CommunicationConfig.RETRY_CONNECTION_TIMEOUT && timeArrived != 0.0) {
                    System.out.println(connection.getUsername() + " - Timed out!");
                    connection.setDisconnected();
                    gameStateMaster.getGameState().getPlayers().remove(connection.getPid());
                }
            }
        }
    }

    public static synchronized void observe(Tickable tickable) {
        observers.add(tickable);
    }

    public static synchronized void removeObserver(Tickable tickable) {
        observers.remove(tickable);
    }

    private void notifyObservers(long tick) {
        for (Tickable tickable : observers) {
            tickable.onTick(tick);
        }
    }

    public static AtomicLong getTickCounter() {
        return tickCounter;
    }

    private static long getTickcounter() {
        return tickCounter.get();
    }
}

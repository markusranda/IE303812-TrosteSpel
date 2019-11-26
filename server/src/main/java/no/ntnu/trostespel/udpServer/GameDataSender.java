package no.ntnu.trostespel.udpServer;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import no.ntnu.trostespel.GameServer;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.game.GameStateMaster;
import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.state.GameState;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static no.ntnu.trostespel.config.CommunicationConfig.MAX_PLAYERS;
import static no.ntnu.trostespel.config.CommunicationConfig.RECEIVED_DATA_TYPE;
import static no.ntnu.trostespel.model.ConnectionStatus.CONNECTED;

public class GameDataSender extends ThreadPoolExecutor {

    private GameServer master;
    private GameStateMaster gameStateMaster;
    private Gson gson = new Gson();
    private GameState nextGameState;
    private Kryo kryo;

    private static final String tag = "gamedatasender";

    private long time;

    public GameDataSender() {
        super(1, Integer.MAX_VALUE, CommunicationConfig.RETRY_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS, new SynchronousQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("GameDataSender-%d").build());
        this.gameStateMaster = GameStateMaster.getInstance();
        LogManager.getLogger(tag).trace("Created " + this.getClass().getName());
        time = System.currentTimeMillis();
        kryo = new Kryo();
        kryo.setRegistrationRequired(false);
    }


    /**
     * Does the update tasks for the server.
     */
    public void broadcast(List<Connection> connections, long tick) {
        // Send GameState to all clients
        log();
        nextGameState = gameStateMaster.getGameState();
        nextGameState.setTick(tick);
        Queue snapshot = new LinkedList(nextGameState.getProjectileEvents());
        String json = gson.toJson(nextGameState, RECEIVED_DATA_TYPE);
        GameStateMaster.getInstance().onEventsConsumed();
        System.out.println(this.getActiveCount());
        for (Connection con : connections) {
            if (con.getConnectionStatus() == CONNECTED)
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

    private void log() {

    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }
}

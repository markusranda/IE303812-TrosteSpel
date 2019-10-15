package no.ntnu.trostespel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.ntnu.trostespel.config.Assets;
import no.ntnu.trostespel.config.KeyConfig;
import no.ntnu.trostespel.config.ConnectionConfig;
import no.ntnu.trostespel.entity.Session;
import no.ntnu.trostespel.networking.ConnectionClient;
import no.ntnu.trostespel.networking.GameDataReceiver;
import no.ntnu.trostespel.networking.GameDataTransmitter;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static no.ntnu.trostespel.config.Assets.img;

/**
 * Main class
 */
public class TrosteSpel extends Game {
    public SpriteBatch batch;
    public KeyConfig keys;
    private GameDataReceiver gameDataReceiver;

    @Override
    public void create() {
        // load textures
        Assets.load();
        ConnectionConfig.getInstance();
        batch = new SpriteBatch();
        setScreen(new MainGameState(this));

        // TODO: 11.10.2019 Ask the user for username, don't use static String.
        String username = "LemuriumIntegrale";

        // Connect to server
        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();

            ConnectionClient connectionClient = new ConnectionClient(
                    ConnectionConfig.host,
                    ConnectionConfig.SERVER_TCP_CONNECTION_RECEIVE_PORT);

            Callable<Long> connectionThread = () -> connectionClient.initialConnect(username);

            Future<Long> future = executor.submit(connectionThread);
            long playerId = future.get();
            // If no error codes were returned from the connection, go ahead and send data
            if (playerId > 0) new GameDataTransmitter(playerId);

            Session session = Session.getInstance();
            boolean result = session.setPlayerID(playerId);

            System.out.println("My playerID is: " + session.getPlayerID());

            // listen for updates from server
            gameDataReceiver = new GameDataReceiver();
            Thread gameDataReceiverThread = new Thread(gameDataReceiver);
            gameDataReceiverThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GameState getReceivedGameState() {
        return this.gameDataReceiver.getUpdatedGameState();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }
}

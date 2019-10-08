package no.ntnu.trostespel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.ntnu.trostespel.config.Assets;
import no.ntnu.trostespel.config.KeyConfig;
import no.ntnu.trostespel.config.ServerConnection;
import no.ntnu.trostespel.entity.Session;
import no.ntnu.trostespel.networking.ConnectionClient;
import no.ntnu.trostespel.networking.ServerTalker;
import no.ntnu.trostespel.networking.UserInputManager;

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

    @Override
    public void create() {
        // load textures
        Assets.load();
        ServerConnection.loadDefault();
        batch = new SpriteBatch();
        setScreen(new MainGameState(this));

        // Connect to server
        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();

            ConnectionClient connectionClient = new ConnectionClient(ServerConnection.host, 7083);
            String username = "LemuriumIntegrale";

            Callable<Long> connectionThread = () -> connectionClient.initialConnect(username);

            Future<Long> future = executor.submit(connectionThread);
            long playerId = future.get();
            // If no error codes were returned from the connection, go ahead and send data
            if (playerId > 0) new ServerTalker(playerId);

            Session session = Session.getInstance();
            boolean result = session.setPlayerID(playerId);

            System.out.println("I'm at the end of the method, and my playerID is: " + session.getPlayerID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }
}

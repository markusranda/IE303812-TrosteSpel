package no.ntnu.trostespel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.ntnu.trostespel.config.Assets;
import no.ntnu.trostespel.config.KeyConfig;
import no.ntnu.trostespel.config.ServerConnection;
import no.ntnu.trostespel.entity.Session;
import no.ntnu.trostespel.networking.ConnectionClient;
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

//            Solution to the same problem without callable
//            Thread connectionThread = new Thread(() -> {
//                playerID[0] = connectionClient.initialConnect(username);
//                System.out.println("Not at the end, and my playerID is: " + playerID[0]);
//            });

            Thread sendDataToServer = new Thread(() -> {
                // Start sending data to the server
            });

            Future<Long> future = executor.submit(connectionThread);
            Session session = Session.getInstance();
            boolean result = session.setPlayerID(future.get());

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

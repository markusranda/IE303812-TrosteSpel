package no.ntnu.trostespel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.ntnu.trostespel.config.Assets;
import no.ntnu.trostespel.config.KeyConfig;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.entity.Session;
import no.ntnu.trostespel.networking.GameDataReceiver;
import no.ntnu.trostespel.networking.GameDataTransmitter;
import no.ntnu.trostespel.screen.MainMenuScreen;

import java.util.concurrent.*;

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
        CommunicationConfig.getInstance();
        batch = new SpriteBatch();
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }

    public void makeServerConnection(){
            long pid = Session.getInstance().getPid();
            // Start transmitting updates to server
            new GameDataTransmitter(pid);

            Session session = Session.getInstance();
            boolean result = session.setPid(pid);

            // Listen for updates from server
            gameDataReceiver = new GameDataReceiver();
            Thread gameDataReceiverThread = new Thread(gameDataReceiver);
            gameDataReceiverThread.setName("GameDataReceiver");
            gameDataReceiverThread.start();
    }
}

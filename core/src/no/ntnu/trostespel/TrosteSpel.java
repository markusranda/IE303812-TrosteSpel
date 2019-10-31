package no.ntnu.trostespel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import no.ntnu.trostespel.config.Assets;
import no.ntnu.trostespel.config.KeyConfig;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.entity.Movable;
import no.ntnu.trostespel.entity.Player;
import no.ntnu.trostespel.entity.Session;
import no.ntnu.trostespel.networking.GameDataReceiver;
import no.ntnu.trostespel.networking.GameDataTransmitter;
import no.ntnu.trostespel.screen.MainMenuScreen;
import no.ntnu.trostespel.state.GameState;

import java.net.DatagramSocket;

import static no.ntnu.trostespel.config.Assets.img;

/**
 * Main class
 */
public class TrosteSpel extends Game {
    public SpriteBatch batch;
    public KeyConfig keys;
    private GameDataReceiver gameDataReceiver;
    public static Skin skin;

    @Override
    public void create() {
        // load textures
        Assets.load();
        batch = new SpriteBatch();

        // Load skin
        TextureAtlas atlas = new TextureAtlas("skin/star-soldier/skin/star-soldier-ui.atlas");
        skin = new Skin(Gdx.files.internal("skin/star-soldier/skin/star-soldier-ui.json"), atlas);

        // Init config
        CommunicationConfig.getInstance();

        // Set the screen to Main Menu
        setScreen(new MainMenuScreen(this));
    }

    public void startUdpConnection(GameState<Player, Movable> gameState) {
        long pid = Session.getInstance().getPid();
        Session session = Session.getInstance();
        DatagramSocket socket = session.getUdpSocket();
        boolean result = session.setPid(pid);

        // Start transmitting updates to server
        new GameDataTransmitter(socket, pid, gameState);

        // Listen for updates from server
        GameDataReceiver gameDataReceiver = new GameDataReceiver(socket);
        Thread gameDataReceiverThread = new Thread(gameDataReceiver);
        gameDataReceiverThread.setName("GameDataReceiver");
        gameDataReceiverThread.start();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }

    @Override
    public void render() {
        super.render();
    }
}

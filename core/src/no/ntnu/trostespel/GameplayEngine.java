package no.ntnu.trostespel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.Assets;
import no.ntnu.trostespel.config.KeyConfig;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.controller.NetworkedPlayerController;
import no.ntnu.trostespel.entity.Movable;
import no.ntnu.trostespel.entity.Player;
import no.ntnu.trostespel.entity.Session;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

public class GameplayEngine extends ScreenAdapter {


    private GameState<Player, Movable> gameState;

    private TrosteSpel game;
    Rectangle lemur;
    private OrthographicCamera camera;
    private boolean debug = false;
    private BitmapFont font = new BitmapFont();
    private float velocity = 6f;

    private GameState<PlayerState, MovableState> receivedState;

    public GameplayEngine(TrosteSpel game) {
        this.game = game;
        this.gameState = new GameState<>();
        //
        KeyConfig keys = new KeyConfig();
        keys.loadDefault();

        // init camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 800);
        //
    }

    private void drawPlayers(float delta) {
        for (Player player : gameState.players.values()) {
            System.out.println(player);
            player.update(delta);
            player.draw(game.batch);
        }
    }

    private void drawUI() {
        if (Gdx.input.isKeyPressed(KeyConfig.toggleDebug)) {
            debug = true;
        }
        if (debug) {
            long pid = Session.getInstance().getPlayerID();
            PlayerState state = receivedState.players.get(pid);

            int height = 800;
            font.draw(game.batch, "Host: " + CommunicationConfig.host + ":" + CommunicationConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT, 10, height);
            font.draw(game.batch, "Host: " + CommunicationConfig.host + ":" + CommunicationConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT, 10, height - 20);
            font.draw(game.batch, "Tickrate " + CommunicationConfig.TICKRATE, 10, height - 40);
            font.draw(game.batch, "Connected players " + receivedState.players.size(), 10, height - 60);
            font.draw(game.batch, "Position " + state.getPosition(), 10, height - 80);
        }
        game.batch.end();
    }

    private void applyReceivedChanges() {
        // CAUTION the types in GameState must be the same as in GameDataReceiver

        // iterate over received player changes
        for (PlayerState change : receivedState.players.values()) {
            long key = change.getPid();
            if (!gameState.players.containsKey(key)) {
                // add player to the game
                NetworkedPlayerController controller = new NetworkedPlayerController(gameState, key);
                Player newPlayer = new Player(change.getPosition(), Assets.lemurImage, controller);
                gameState.players.put(key, newPlayer);
            }
            // apply changed values
            Player player = gameState.players.get(key);
            Vector2 pos = change.getPosition();
            player.setPos(pos);
            player.setHealth(change.getHealth());
            player.update(0);
            player.draw(game.batch);

        }
    }

    @Override
    public void render(float delta) {
        this.receivedState = Session.getInstance().getReceivedGameState();
        if (this.receivedState != null) {
            game.batch.begin();

            applyReceivedChanges();

            Gdx.gl.glClearColor(1, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            game.batch.setProjectionMatrix(camera.combined);

            //drawPlayers(delta);
            drawUI();
        }

    }
}

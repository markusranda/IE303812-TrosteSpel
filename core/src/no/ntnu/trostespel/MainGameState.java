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
import no.ntnu.trostespel.config.ServerConfig;
import no.ntnu.trostespel.config.ConnectionConfig;
import no.ntnu.trostespel.controller.NetworkedPlayerController;
import no.ntnu.trostespel.controller.ObjectController;
import no.ntnu.trostespel.entity.GameObject;
import no.ntnu.trostespel.entity.Movable;
import no.ntnu.trostespel.entity.Player;
import no.ntnu.trostespel.entity.Session;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.util.Map;

public class MainGameState extends ScreenAdapter {


    private GameState<Player, Movable> gameState;

    private TrosteSpel game;
    Rectangle lemur;
    private OrthographicCamera camera;
    private boolean debug = false;
    private BitmapFont font = new BitmapFont();
    private float velocity = 6f;


    public MainGameState(TrosteSpel game) {
        this.game = game;
        this.gameState = new GameState<>();
        //
        KeyConfig keys = new KeyConfig();
        keys.loadDefault();

        ObjectController playerController = new NetworkedPlayerController(gameState, 0); //TODO:GET REAL PID
        Vector2 spawnLocation = new Vector2(0, 0);
        Player player = new Player(spawnLocation, Assets.lemurImage, playerController);
        long pid = Session.getInstance().getPlayerID();
        gameState.players.put(pid, player);

        // init camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 800);
        //
    }

    private void drawPlayers(float delta) {
        for (Player player : gameState.players.values()) {
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
            int height = 800;
            font.draw(game.batch, "Host: " + ConnectionConfig.host + ":" + ConnectionConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT, 10, height);
            font.draw(game.batch, "Host: " + ConnectionConfig.host + ":" + ConnectionConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT, 10, height-20);
            font.draw(game.batch, "Tickrate " + ServerConfig.TICKRATE, 10, height-40);
            font.draw(game.batch, "Connected players " + game.getReceivedGameState().players, 10, height-60);
            font.draw(game.batch, "StateChange " + game.getReceivedGameState().players.get(pid), 10, height-80);
        }
        game.batch.end();
    }

    private void applyReceivedChanges() {
        GameState<PlayerState, MovableState> receivedGameState = game.getReceivedGameState();
        // iterate over received changes
        for (Map.Entry<Long, PlayerState> change : receivedGameState.players.entrySet()) {
            long key = change.getKey();
            PlayerState state = change.getValue();
            if (!gameState.players.containsKey(key)) {
                // add player to the game
                NetworkedPlayerController controller  = new NetworkedPlayerController(gameState, key);
                Player newPlayer = new Player(state.getPosition(), Assets.lemurImage, controller);
                gameState.players.put(key, newPlayer);
            } else {
                // apply changed values
                Player player = gameState.players.get(key);
                player.setPos(state.getPosition());
                player.setHealth(state.getHealth());
            }
        }
    }

    @Override
    public void render(float delta) {

        applyReceivedChanges();

        game.batch.begin();
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(camera.combined);

        drawPlayers(delta);
        drawUI();
    }
}

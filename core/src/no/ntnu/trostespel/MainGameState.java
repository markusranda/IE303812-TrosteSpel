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
import no.ntnu.trostespel.entity.Player;

public class MainGameState extends ScreenAdapter {


    TrosteSpel game;
    Rectangle lemur;
    private OrthographicCamera camera;

    private boolean debug = false;
    BitmapFont font = new BitmapFont();
    private float velocity = 6f;

    private Player player;

    public MainGameState(TrosteSpel game) {
        this.game = game;

        //
        KeyConfig keys = new KeyConfig();
        keys.loadDefault();

        ObjectController playerController = new NetworkedPlayerController();
        Vector2 spawnLocation = new Vector2(800 / 2 - 64 / 2, 50);
        player = new Player(spawnLocation, Assets.lemurImage, playerController);

        // init camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        //
    }

    private void update(float delta) {
        if (Gdx.input.isKeyPressed(KeyConfig.toggleDebug)) {
            debug = true;
        }
        player.update(delta);
    }

    private void draw() {
        game.batch.begin();
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(camera.combined);
        player.draw(game.batch);
        if (debug) {
            font.draw(game.batch, "Host: " + ConnectionConfig.host +":"+ ConnectionConfig.port, 10, 10);
            font.draw(game.batch, "Tickrate " + ServerConfig.TICKRATE, 10, 20);
        }
        game.batch.end();
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }
}

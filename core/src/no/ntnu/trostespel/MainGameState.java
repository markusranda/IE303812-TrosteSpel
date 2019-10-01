package no.ntnu.trostespel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import no.ntnu.trostespel.config.Assets;
import no.ntnu.trostespel.config.KeyConfig;
import no.ntnu.trostespel.controller.NetworkedPlayerController;
import no.ntnu.trostespel.controller.ObjectController;
import no.ntnu.trostespel.entity.Player;
import no.ntnu.trostespel.networking.UserInputManager;

import java.io.IOException;

public class MainGameState extends ScreenAdapter {


    TrosteSpel game;
    Rectangle lemur;
    private OrthographicCamera camera;


    private float velocity = 6f;

    private Player player;

    public MainGameState(TrosteSpel game) {
        this.game = game;

        //
        KeyConfig keys = new KeyConfig();
        keys.loadDefault();

        ObjectController playerController = new NetworkedPlayerController(keys, 0);
        player = new Player(800 / 2 - 64 / 2, 20, 64, 64, new Rectangle(), Assets.lemurImage, playerController);

        // init camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        //
    }

    private void update(float delta) {
        player.update(delta);
    }

    private void draw() {
        game.batch.begin();
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(camera.combined);
        player.draw(game.batch);
        game.batch.end();
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }
}

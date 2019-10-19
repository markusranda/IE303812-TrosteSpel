package no.ntnu.trostespel.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.*;
import no.ntnu.trostespel.GameplayEngine;
import no.ntnu.trostespel.TrosteSpel;
import no.ntnu.trostespel.config.ScreenConfig;
import no.ntnu.trostespel.entity.Session;

import java.util.concurrent.Callable;

public class MainMenuScreen implements Screen {

    private SpriteBatch batch;
    private Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TextureAtlas atlas;
    private Skin skin;
    private TrosteSpel game;

    public MainMenuScreen(TrosteSpel game) {
        this.game = game;

        atlas = new TextureAtlas("skin/comic/skin/comic-ui.atlas");
        skin = new Skin(Gdx.files.internal("skin/comic/skin/comic-ui.json"), atlas);

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(ScreenConfig.SCREEN_WIDTH, ScreenConfig.SCREEN_HEIGHT, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        stage = new Stage(viewport, batch);
    }

    @Override
    public void show() {
        //Stage should control input:
        Gdx.input.setInputProcessor(stage);

        //Create Table
        Table mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.top();

        //Create buttons
        TextButton playButton = new TextButton("Play", skin);
//        TextButton optionsButton = new TextButton("Options", skin);
        TextButton exitButton = new TextButton("Exit", skin);
        TextButton connect = new TextButton("Connect", skin);

        Input.TextInputListener listener = new Input.TextInputListener() {
            @Override
            public void input(String text) {
                Session.getInstance().setUserName(text);
                game.makeServerConnection();
            }

            @Override
            public void canceled() {

            }
        };

        //Add listeners to buttons
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameplayEngine(game));
            }
        });
//        optionsButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                System.out.println("Options have been pressed!");
//            }
//        });
        connect.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Session.getInstance().getUsername() == null)
                    Gdx.input.getTextInput(listener,
                            "Select a username",
                            "",
                            "Enter username");
                else
                    Gdx.input.getTextInput(listener,
                            "Select a username",
                            Session.getInstance().getUsername(),
                            "");
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });


        //Add buttons to table
        mainTable.add(playButton);
        mainTable.row();
//        mainTable.add(optionsButton);
        mainTable.row();
        mainTable.add(exitButton);
        mainTable.row();
        mainTable.add(connect);

        //Add table to stage
        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        skin.dispose();
        atlas.dispose();
    }
}
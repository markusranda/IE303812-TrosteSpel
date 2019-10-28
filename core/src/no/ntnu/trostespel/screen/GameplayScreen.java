package no.ntnu.trostespel.screen;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import no.ntnu.trostespel.TrosteSpel;
import no.ntnu.trostespel.config.Assets;
import no.ntnu.trostespel.config.KeyConfig;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.config.ScreenConfig;
import no.ntnu.trostespel.entity.Movable;
import no.ntnu.trostespel.entity.Player;
import no.ntnu.trostespel.entity.Projectile;
import no.ntnu.trostespel.entity.Session;
import no.ntnu.trostespel.networking.GameDataReceiver;
import no.ntnu.trostespel.networking.GameDataTransmitter;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.util.Queue;

public class GameplayScreen extends ScreenAdapter {


    private final TiledMap tiledMap;
    private final OrthogonalTiledMapRendererWithSprites tiledMapRenderer;
    private final MapLayer objectLayer;
    private GameState<Player, Movable> gameState;

    private TrosteSpel game;
    private OrthographicCamera camera;
    private boolean debug = false;
    private BitmapFont font;

    private GameState<PlayerState, MovableState> receivedState;

    public GameplayScreen(TrosteSpel game) {
        this.game = game;
        this.gameState = new GameState<>();

        // init camera
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, w/2, h/2);
        camera.update();

        // init keys
        KeyConfig keys = new KeyConfig();
        keys.loadDefault();

        // init world
        tiledMap = new TmxMapLoader().load("map/tutorial_map.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap);

        objectLayer = tiledMap.getLayers().get("objects");

        // start sending and listening for data
        communicate();
    }

    private void communicate() {
        game.startUdpConnection();
    }

    private void drawUI() {
        if (Gdx.input.isKeyPressed(KeyConfig.toggleDebug)) {
            debug = true;
        }
        if (debug) {
            long pid = Session.getInstance().getPid();
            PlayerState state = receivedState.players.get(pid);

            int height = ScreenConfig.SCREEN_HEIGHT;
            font.draw(game.batch, "Host: " + CommunicationConfig.host + ":" + CommunicationConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT, 10, height);
            font.draw(game.batch, "Local Port: " + Session.getInstance().getUdpSocket().getLocalPort(), 10, height - 20);
            font.draw(game.batch, "Framterate: " + Gdx.graphics.getFramesPerSecond(), 10, height - 40);
            font.draw(game.batch, "Tickrate: " + CommunicationConfig.TICKRATE, 10, height - 60);
            font.draw(game.batch, "Connected players " + receivedState.players.size(), 10, height - 80);
            font.draw(game.batch, "Player: " + pid + "  " + state.getPosition(), 10, height - 100);
        }
    }

    private void updatePlayers() {
        // iterate over received player changes
        for (PlayerState change : receivedState.players.values()) {
            long key = change.getPid();
            if (!gameState.players.containsKey(key)) {
                // add player to the game
                Player newPlayer = new Player(change.getPosition(), Assets.lemurImage);
                gameState.players.put(key, newPlayer);
            }
            // apply changed values
            Player player = gameState.players.get(key);
            Vector2 pos = change.getPosition();
            player.setPos(pos);
            player.setHealth(change.getHealth());
            player.setPid(change.getPid());
            player.update(0);

            if (player.getPid() == Session.getInstance().getPid()) {
                camera.position.x = player.getPos().x;
                camera.position.y = player.getPos().y;
            }

            player.draw(game.batch);

            // add player to object layer
//            if (!player.addedToLayer()) {
                TextureMapObject tmo = new TextureMapObject(player.getTextureRegion());
                tmo.setX(player.getPos().x);
                tmo.setY(player.getPos().y);
                objectLayer.getObjects().add(tmo);
                player.setAddedToLayer(true);
                System.out.println(player.getPid() + " - Has been added to the layer!");
//            }
        }
    }

    private void spawnNewProjectiles() {
        Queue<MovableState> queue = receivedState.getProjectilesStateUpdates();
        while (!queue.isEmpty()) {
            MovableState state = queue.poll();
            long eid = state.getId();
            long owner = state.getPid();
            switch (state.getAction()) {
                case CREATE:
                    if (!gameState.getProjectiles().containsKey(eid)) {
                        Player player = gameState.players.get(owner);
                        if (player != null) {
                            Vector2 spawnPos = player.getPos();
                            Projectile newProjectile = new Projectile(spawnPos, Assets.bullet, state.getVelocity(), state.getAngle());
                            gameState.getProjectiles().put(eid, newProjectile);
                        }
                    }
                case KILL:
                    if (!gameState.getProjectiles().containsKey(eid)) {
                        gameState.getProjectiles().remove(eid);
                    }
            }

        }
    }

    private void updateProjectiles() {
        for (Movable projectile : gameState.getProjectiles().values()) {
            projectile.update(Gdx.graphics.getDeltaTime());
            projectile.draw(game.batch);
        }
    }

    @Override
    public void render(float delta) {
        this.receivedState = Session.getInstance().getReceivedGameState();
        if (this.receivedState != null) {
            game.batch.begin();
            Gdx.gl.glClearColor(1, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Update all entities
            updatePlayers();
            updateProjectiles();
            spawnNewProjectiles();
            drawUI();

            game.batch.end();
        }
        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
    }
}

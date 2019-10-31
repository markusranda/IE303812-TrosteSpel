package no.ntnu.trostespel.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.TrosteSpel;
import no.ntnu.trostespel.config.Assets;
import no.ntnu.trostespel.config.KeyConfig;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.config.ScreenConfig;
import no.ntnu.trostespel.entity.Movable;
import no.ntnu.trostespel.entity.Player;
import no.ntnu.trostespel.entity.Projectile;
import no.ntnu.trostespel.entity.Session;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.util.Queue;

public class GameplayScreen extends ScreenAdapter {


    public static final String MAP_OBJECT_ID_PROJECTILE = "projectile";
    public static final String MAP_OBJECT_ID_PLAYER = "player";
    private final TiledMap tiledMap;
    private final ObjectMapRenderer tiledObjectMapRenderer;
    private final MapLayer objectLayer;
    private final MapLayer collisionLayer;
    private GameState<Player, Movable> gameState;

    private TrosteSpel game;
    private OrthographicCamera camera;
    private boolean debug = false;
    private BitmapFont font;

    private GameState<PlayerState, MovableState> receivedState;

    public GameplayScreen(TrosteSpel game) {
        this.game = game;
        this.gameState = new GameState<>();

        this.font = new BitmapFont();

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
        tiledMap = new TmxMapLoader().load(Session.getInstance().getMapFileName());
        tiledObjectMapRenderer = new ObjectMapRenderer(tiledMap);

        objectLayer = tiledMap.getLayers().get("objects");

        collisionLayer = tiledMap.getLayers().get("collisions");
        collisionLayer.setVisible(false);

        // start sending and listening for data
        communicate();
    }

    private void communicate() {
        game.startUdpConnection(tiledMap);
    }

    private void drawUI() {
        if (Gdx.input.isKeyPressed(KeyConfig.toggleDebug)) {
            debug = true;
        }
        if (debug) {
            long pid = Session.getInstance().getPid();
            PlayerState state = receivedState.players.get(pid);

            int height = ScreenConfig.SCREEN_HEIGHT;
            game.batch.begin();
            font.draw(game.batch, "Host: " + CommunicationConfig.host + ":" + CommunicationConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT, 10, height);
            font.draw(game.batch, "Local Port: " + Session.getInstance().getUdpSocket().getLocalPort(), 10, height - 20);
            font.draw(game.batch, "Framterate: " + Gdx.graphics.getFramesPerSecond(), 10, height - 40);
            font.draw(game.batch, "Tickrate: " + CommunicationConfig.TICKRATE, 10, height - 60);
            font.draw(game.batch, "Connected players " + receivedState.players.size(), 10, height - 80);
            font.draw(game.batch, "Player: " + pid + "  " + state.getPosition(), 10, height - 100);
            game.batch.end();
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

            player.draw((SpriteBatch) tiledObjectMapRenderer.getBatch());

            // add player to object layer
            if (!player.addedToLayer()) {
                MapObject mapObject = new MapObject();
                mapObject.getProperties().put(MAP_OBJECT_ID_PLAYER, player);
                objectLayer.getObjects().add(mapObject);
                player.setAddedToLayer(true);
            }
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

                            // Add projectile to object layer
                            MapObject mapObject = new MapObject();
                            mapObject.getProperties().put(MAP_OBJECT_ID_PROJECTILE, newProjectile);
                            objectLayer.getObjects().add(mapObject);
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
            tiledObjectMapRenderer.getBatch().begin();
            Gdx.gl.glClearColor(1, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Update all entities
            updatePlayers();
            updateProjectiles();
            spawnNewProjectiles();
            drawUI();

            tiledObjectMapRenderer.getBatch().end();
        }
        camera.update();
        tiledObjectMapRenderer.setView(camera);
        tiledObjectMapRenderer.render();
    }
}

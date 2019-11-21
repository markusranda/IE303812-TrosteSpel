package no.ntnu.trostespel.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import no.ntnu.trostespel.TrosteSpel;
import no.ntnu.trostespel.config.Assets;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.config.KeyConfig;
import no.ntnu.trostespel.config.ScreenConfig;
import no.ntnu.trostespel.entity.Movable;
import no.ntnu.trostespel.entity.Player;
import no.ntnu.trostespel.entity.Projectile;
import no.ntnu.trostespel.entity.Session;
import no.ntnu.trostespel.networking.UserInputManager;
import no.ntnu.trostespel.state.Action;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

public class GameplayScreen extends ScreenAdapter {


    public static final String MAP_OBJECT_ID_PROJECTILE = "projectile";
    public static final String MAP_OBJECT_ID_PLAYER = "player";
    private final TiledMap tiledMap;
    private final ObjectMapRenderer tiledObjectMapRenderer;
    private final MapLayer objectLayer;
    private final MapLayer collisionLayer;
    private final Stage stage;
    private Table playerListTable;
    private final Table debuggerUiTable;
    private Label.LabelStyle labelStyle;
    private GameState<Player, Movable> gameState;

    private ShapeRenderer lineRenderer = new ShapeRenderer();
    private TrosteSpel game;
    private OrthographicCamera camera;
    private boolean debug = false;
    private BitmapFont font;
    private long tick;

    private ParticleEffect impact;

    private Pool<Projectile> bulletPool;
    private ParticleEffectPool impactEffectPool;
    private Array<ParticleEffectPool.PooledEffect> effects = new Array();

    Skin skin = TrosteSpel.skin;

    private GameState<PlayerState, MovableState> receivedState;
    private BitmapFont fontUserName;
    private Table messageListTable;


    public GameplayScreen(TrosteSpel game) {
        this.game = game;
        this.gameState = new GameState<>();

        this.font = new BitmapFont();

        // init stage
        stage = new Stage();
        initFonts();
        initPlayerList();
        initMessageList();

        // init debuggerUI
        debuggerUiTable = new Table();
        stage.addActor(debuggerUiTable);
        debuggerUiTable.setSkin(skin);
        debuggerUiTable.setPosition(200, Gdx.graphics.getHeight() - 200);

        // init camera
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, w / 2, h / 2);
        camera.update();

        // init keys
        KeyConfig keys = new KeyConfig();
        keys.loadDefault();

        // init world
        tiledMap = new TmxMapLoader().load(Session.getInstance().getMapFileName());
        tiledObjectMapRenderer = new ObjectMapRenderer(tiledMap);

        objectLayer = tiledMap.getLayers().get("objects");

        collisionLayer = tiledMap.getLayers().get("collisions");
        gameState.setCollidables((TiledMapTileLayer) collisionLayer);

        collisionLayer.setVisible(false);

        bulletPool = new Pool() {
            @Override
            protected Object newObject() {
                return new Projectile(Vector2.Zero, Assets.bullet, 0d, 0f, -1);
            }
        };

        impact = Assets.particleEffect;
        impactEffectPool = new ParticleEffectPool(impact, 10, 100);
        // start sending and listening for data
        communicate();
    }

    private void initPlayerList() {
        playerListTable = new Table();
        stage.addActor(playerListTable);
        playerListTable.setSkin(skin);
        playerListTable.setPosition(Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 200);
    }

    private void initMessageList() {
        messageListTable = new Table();
        stage.addActor(messageListTable);
        messageListTable.setSkin(skin);
        messageListTable.setPosition(200, Gdx.graphics.getHeight() - 200);
    }

    private void initFonts() {
        FreeTypeFontGenerator generatorPlayerList
                = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Quicksand.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter
                parameterPlayerList = new FreeTypeFontGenerator.FreeTypeFontParameter();
        generatorPlayerList.scaleForPixelHeight(50);
        parameterPlayerList.size = 28;
        parameterPlayerList.borderWidth = 3;
        parameterPlayerList.color = Color.WHITE;
        parameterPlayerList.minFilter = Texture.TextureFilter.Nearest;
        parameterPlayerList.magFilter = Texture.TextureFilter.MipMapLinearNearest;

        BitmapFont fontPlayerList = generatorPlayerList.generateFont(parameterPlayerList);

        labelStyle = new Label.LabelStyle();
        labelStyle.font = fontPlayerList;
    }

    private void communicate() {
        game.startUdpConnection(gameState);
    }

    private void drawUI() {
        game.batch.setProjectionMatrix(camera.combined);
        gameState.getPlayers().forEach((k, v) -> {
            v.drawOverhead(game.batch);
        });
    }

    private void drawDebug() {
        if (Gdx.input.isKeyPressed(KeyConfig.toggleDebug)) {
            debug = true;
        }
        if (debug) {
            game.batch.begin();
            long pid = Session.getInstance().getPid();
            PlayerState state = receivedState.getPlayers().get(pid);

            int height = ScreenConfig.SCREEN_HEIGHT;
            font.draw(game.batch, "Host: " + CommunicationConfig.host + ":" + CommunicationConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT, 10, height);
            font.draw(game.batch, "Local Port: " + Session.getInstance().getUdpSocket().getLocalPort(), 10, height - 20);
            font.draw(game.batch, "Framterate: " + Gdx.graphics.getFramesPerSecond(), 10, height - 40);
            font.draw(game.batch, "Tickrate: " + CommunicationConfig.TICKRATE, 10, height - 60);
            font.draw(game.batch, "Connected players " + receivedState.getPlayers().size(), 10, height - 80);
            font.draw(game.batch, "Player: " + pid + "  " + state.getPosition(), 10, height - 100);
            game.batch.end();
        }


        if (debug) {
            lineRenderer.setProjectionMatrix(camera.combined);
            lineRenderer.begin(ShapeRenderer.ShapeType.Line);
            gameState.getPlayers().forEach((k, v) -> {
                Rectangle hitbox = v.getHitbox();
                lineRenderer.setColor(1, 1, 0, 1);
                lineRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);

            });
            ArrayList<Rectangle> collide = UserInputManager.getCollideables();
            if (collide != null) {
                for (Rectangle rectangle : collide) {
                    lineRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                }
            }
            lineRenderer.end();
        }
    }

    private void updatePlayers() {
        // iterate over received player changes
        for (PlayerState change : receivedState.getPlayers().values()) {
            long key = change.getPid();
            if (change.getAction() == Action.DEAD) {
                for (MapObject mapObject : objectLayer.getObjects()) {
                    Iterator innerIterator = mapObject.getProperties().getValues();
                    while (innerIterator.hasNext()) {
                        Object obj = innerIterator.next();
                        if (obj instanceof Player) {
                            Player player = (Player) obj;
                            if (player.getPid() == change.getPid()) {
                                innerIterator.remove();
                                if (gameState.getPlayers().containsKey(key)) {
                                    player.removedFromLayer();
                                    player.setHealth(0);
                                }
                            }
                        }
                    }
                }
            } else if (change.getAction() == Action.ALIVE) {
                if (!gameState.getPlayers().containsKey(key)) {
                    // add player to the game
                    Player newPlayer = new Player(change.getPosition(), Assets.lemurImage, change.getHealth(), change.getUsername());
                    gameState.getPlayers().put(key, newPlayer);
                }

                // apply changed values
                Player player = gameState.getPlayers().get(key);

                if (player.getUsername().equals("")) {
                    if (change.getUsername() != null) {
                        player.setUsername(change.getUsername());
                    }
                }
                // setting projected pos
                Vector2 pos = change.getPosition();
                player.interpolatePos(pos);

                player.setHealth(change.getHealth());
                player.setPid(change.getPid());
                player.update(Gdx.graphics.getDeltaTime(), tick);

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
    }

    private void spawnNewProjectiles() {
        Queue<MovableState> queue = receivedState.getProjectilesStateUpdates();
        while (!queue.isEmpty()) {
            MovableState state = queue.poll();
            long eid = state.getId();
            long owner = state.getPid();
            Action action = state.getAction();
            if (action == Action.CREATE) {
                if (!gameState.getProjectiles().containsKey(eid)) {
                    Player player = gameState.getPlayers().get(owner);
                    player.setAttacking(true);
                    if (player != null) {
                        Vector2 spawnPos = player.getPos();
                        Projectile newProjectile = bulletPool.obtain().construct(spawnPos, state.getVelocity(), state.getAngle(), eid);

                        gameState.getProjectiles().put(eid, newProjectile);

                        // Add projectile to object layer
                        MapObject mapObject = new MapObject();
                        mapObject.getProperties().put(MAP_OBJECT_ID_PROJECTILE, newProjectile);
                        objectLayer.getObjects().add(mapObject);
                    }
                }
            } else if (action == Action.KILL) {
                Movable remove = gameState.getProjectiles().remove(eid);
                if (remove != null) {
                    if (remove instanceof Projectile) {
                        Vector2 pos = remove.getCenterPos();
                        // show parrticle effect
                        float angle = ((Projectile) remove).getAngle();
                        ParticleEffectPool.PooledEffect effect = impactEffectPool.obtain();
                        effect.setPosition(pos.x, pos.y);
                        effect.getEmitters().first().getAngle().setLow(angle);
                        effect.getEmitters().first().getAngle().setHigh(angle);
                        effects.add(effect);
                    }
                    for (MapObject mapObject : objectLayer.getObjects()) {
                        if (mapObject.getProperties().containsKey(MAP_OBJECT_ID_PROJECTILE)) {
                            Iterator innerIterator = mapObject.getProperties().getValues();
                            while (innerIterator.hasNext()) {
                                Projectile projectile = (Projectile) innerIterator.next();
                                if (projectile.getId() == eid) {
                                    innerIterator.remove();
                                }
                            }
                        }
                    }
                    ((Projectile) remove).reset();
                    bulletPool.free((Projectile) remove);
                }
            }
        }
    }

    private void updateProjectiles() {
        for (Movable projectile : gameState.getProjectiles().values()) {
            projectile.update(Gdx.graphics.getDeltaTime(), tick);
        }
    }

    private void drawParticle() {
        // Update and draw effects:
        for (int i = effects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = effects.get(i);
            effect.draw(game.batch, Gdx.graphics.getDeltaTime());
            if (effect.isComplete()) {
                effect.free();
                effects.removeIndex(i);
            }
        }
    }


    @Override
    public void render(float delta) {
        if (game.isTimedOut()) disconnect();

        this.receivedState = Session.getInstance().getReceivedGameState();
        if (this.receivedState != null) {

            this.tick = receivedState.getTick();
            tiledObjectMapRenderer.getBatch().begin();
            Gdx.gl.glClearColor(0.1f, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Update all entities
            spawnNewProjectiles();

            updatePlayers();
            updateProjectiles();
            camera.update();
            tiledObjectMapRenderer.setView(camera);
            tiledObjectMapRenderer.getBatch().end();
            tiledObjectMapRenderer.render();

            // Update UI
            updateDebuggerUI();
            updatePlayerListUI();

            // Draw
            game.batch.begin();
            drawParticle();
            drawUI();
            stage.act(delta);
            stage.draw();
            game.batch.end();

            drawDebug();
        }
    }

    /**
     * draws a list which displays useful information when debugging
     */
    private void updateDebuggerUI() {
        debuggerUiTable.clearChildren();
        if (debug) {
            long pid = Session.getInstance().getPid();
            PlayerState state = receivedState.getPlayers().get(pid);
            debuggerUiTable.add(new Label("Host: " + CommunicationConfig.host + ":" + CommunicationConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT, skin));
            debuggerUiTable.row();
            debuggerUiTable.add(new Label("Local Port: " + Session.getInstance().getUdpSocket().getLocalPort(), skin));
            debuggerUiTable.row();
            debuggerUiTable.add(new Label("Framterate: " + Gdx.graphics.getFramesPerSecond(), skin));
            debuggerUiTable.row();
            debuggerUiTable.add(new Label("Tickrate: " + CommunicationConfig.TICKRATE, skin));
            debuggerUiTable.row();
            debuggerUiTable.add(new Label("Connected players " + receivedState.getPlayers().size(), skin));
            debuggerUiTable.row();
            debuggerUiTable.add(new Label("Player: " + pid + "  " + state.getPosition(), skin));
            debuggerUiTable.row();
        }
    }

    /**
     * Retrieves a list of messages, then displays them in a
     * table on the screen.
     */
    private void updateMessageListUI() {
        // Clear table
        messageListTable.clearChildren();

        // Iterate list of all messages
//        for (LarsianMessage larsianMessage: getThoseLarsianMessages()) {
//            // Add new labels with messages
//            messageListTable.add(new Label(currentMessage.getMessage(), labelStyle));
//            messageListTable.row();
//        }
    }

    /**
     * Clear all the children of table and adds all
     * username's from the Game State
     */
    private void updatePlayerListUI() {
        playerListTable.clearChildren();
        for (Map.Entry entry : gameState.getPlayers().entrySet()) {
            if (entry.getValue() instanceof Player) {
                Player currentPlayer = (Player) entry.getValue();
                if (!(currentPlayer.getUsername() == null)) {
                    Label usernameLabel = new Label(currentPlayer.getUsername(), labelStyle);
                    usernameLabel.setName(currentPlayer.getUsername());
                    playerListTable.add(usernameLabel);
                    playerListTable.row();
                }
            }
        }
    }

    private void disconnect() {
        dispose();
        game.stopUdpConnection();
        game.setScreen(new MainMenuScreen(game));
    }
}

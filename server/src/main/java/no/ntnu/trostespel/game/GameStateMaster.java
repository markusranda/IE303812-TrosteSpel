package no.ntnu.trostespel.game;

import com.badlogic.gdx.math.Vector2;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import no.ntnu.trostespel.Tickable;
import no.ntnu.trostespel.config.GameRules;
import no.ntnu.trostespel.entity.ObjectPool;
import no.ntnu.trostespel.state.Action;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;
import no.ntnu.trostespel.GameServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.concurrent.*;

public class GameStateMaster implements Tickable {

    private static final int MIN_NUM_PROJECTILES = 100;
    private static final int MAX_NUM_PROJECTILES = 2000;
    private static final long OBJECT_POOL_REFRESH_INTERVAL_SEC = 1;
    private GameState<PlayerState, MovableState> gameState;
    private static GameStateMaster instance = null;
    private long tick;
    private ExecutorService executor;
    private GlobalUpdater globalUpdater;
    private ObjectPool<MovableState> movablesPool;

    public static synchronized GameStateMaster getInstance() {
        if (instance == null) {
            instance = new GameStateMaster(new GameState<>());
        }
        return instance;
    }

    private GameStateMaster(GameState<PlayerState, MovableState> gameState) {
        initObjectPool();
        executor = new ThreadPoolExecutor(1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactoryBuilder().setNameFormat("GameStateMasterUpdater").build());
        GameServer.observe(this);
        this.gameState = gameState;
        this.gameState.getPlayers().put(9L, new PlayerState(9, new Vector2(0, 400), 300)); // put dummy lemur on the the map
        this.globalUpdater = new GlobalUpdater(gameState, 0);
        Executors.newSingleThreadExecutor();
    }

    /**
     * Populates the Object pool with projectiles once
     */
    private void initObjectPool() {
        movablesPool = new ObjectPool<MovableState>(
                MIN_NUM_PROJECTILES, MAX_NUM_PROJECTILES, OBJECT_POOL_REFRESH_INTERVAL_SEC) {
            @Override
            protected MovableState createObject() {
                MovableState projectile = new MovableState(GameRules.Projectile.SPEED);
                LogManager.getLogger("projectiles").trace(projectile.toString() + " - Was created");
                return projectile;
            }
        };
    }

    public GameState<PlayerState, MovableState> getGameState() {
        return this.gameState;
    }

    private void applyGlobalUpdate() {
        executor.execute(globalUpdater.prepareForUpdate(tick));
    }

    private Comparator<Runnable> getUpdatePrioritizer() {
        return (o1, o2) -> {
            if (o1 instanceof Updater && o2 instanceof Updater) {
                return Integer.compare(((Updater) o1).getImportance(), ((Updater) o2).getImportance());
            }
            return 0;
        };
    }

    public void onEventsConsumed() {
        for (MovableState movableState : gameState.getProjectilesStateUpdates()) {
            if (movableState.getAction() == Action.KILL) {
                movableState.resetObject();
                movablesPool.returnObject(movableState);
            }
        }
        gameState.getProjectilesStateUpdates().clear();

    }


    @Override
    public void onTick(long tick) {
        this.tick = tick;
        applyGlobalUpdate();
    }

    public ObjectPool<MovableState> getMovablesPool() {
        return movablesPool;
    }
}

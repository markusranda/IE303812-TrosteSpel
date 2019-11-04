package no.ntnu.trostespel.game;

import com.badlogic.gdx.math.Vector2;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import no.ntnu.trostespel.Channel;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;
import no.ntnu.trostespel.udpServer.GameServer;

import java.util.Comparator;
import java.util.concurrent.*;

public class GameStateMaster implements Channel {

    private GameState<PlayerState, MovableState> gameState;
    private static GameStateMaster instance = null;
    private long tick;
    private ExecutorService executor;
    private GlobalUpdater globalUpdater;
    private PlayerUpdater playerUpdater;

    public static synchronized GameStateMaster getInstance() {
        if (instance == null) {
            instance = new GameStateMaster(new GameState<>());
        }
        return instance;
    }

    private GameStateMaster(GameState<PlayerState, MovableState> gameState) {
        executor = new ThreadPoolExecutor(1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("GameStateMasterUpdater").build());
        GameServer.observe(this);;
        this.gameState = gameState;
        this.gameState.players.put(9L, new PlayerState(9, new Vector2(0, 400), 300)); // put dummy lemur on the the map
        this.globalUpdater = new GlobalUpdater(gameState, 0);
    }

    public GameState<PlayerState, MovableState> getGameState() {
        return this.gameState;
    }

    /**
     * @param pid the id of the player to update
     */
    public synchronized void submitPlayerUpdate(long pid, long tick) {
        try {
            executor.execute(applyPlayerUpdate(pid, tick));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    private Runnable applyPlayerUpdate(long pid, long tick) {
        return new PlayerUpdater(gameState, pid);
    }

    private void applyGlobalUpdate() {
        globalUpdater.setTick(tick);
            executor.execute(new GlobalUpdater(gameState, tick));

    }

    private Comparator<Runnable> getUpdatePrioritizer() {
        return (o1, o2) -> {
            if (o1 instanceof Updater && o2 instanceof Updater) {
                return Integer.compare(((Updater) o1).getImportance(), ((Updater) o2).getImportance());
            }
            return 0;
        };
    }

    @Override
    public void onTick(long tick) {
        this.tick = tick;
        applyGlobalUpdate();
    }
}

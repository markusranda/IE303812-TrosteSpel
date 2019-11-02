package no.ntnu.trostespel.game;

import com.badlogic.gdx.math.Vector2;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import no.ntnu.trostespel.Channel;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.state.Action;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;
import no.ntnu.trostespel.udpServer.GameServer;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

//TODO: make MasterGameState threadsafe
public class GameStateMaster implements Channel {

    private volatile GameState<PlayerState, MovableState> gameState;

    private static volatile GameStateMaster single_instance = null;

    private ExecutorService executor;
    private final int MAX_TIME_ALIVE = 5 * CommunicationConfig.TICKRATE; // n seconds
    private static final long RESPAWN_TIME = 3000; // millis

    public synchronized static GameStateMaster getInstance() {
        if (single_instance == null) {
            single_instance = new GameStateMaster(new GameState<>());
        }
        return single_instance;
    }

    private GameStateMaster(GameState<PlayerState, MovableState> gameState) {
        this.gameState = gameState;
        //this.gameState.players.put(9L, new PlayerState(9, new Vector2(0, 400), 300)); // put dummy lemur on the the map
        this.executor = new ThreadPoolExecutor(1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue(),
                new ThreadFactoryBuilder().setNameFormat("MasterGameState-Updater").build());
        GameServer.observePostUpdate(this);
    }

    public GameState getGameState() {
        return this.gameState;
    }

    /**
     * @param pid the id of the player to update
     */
    public synchronized void submitPlayerUpdate(long pid, long tick) {
        executor.submit(applyPlayerUpdates(pid, tick));
    }

    private Runnable applyPlayerUpdates(long pid, long tick) {
        return new PlayerUpdater(pid, tick);
    }

    class PlayerUpdater implements Runnable {

        private long pid;
        private long tick;

        public PlayerUpdater(long pid, long tick) {
            this.pid = pid;
            this.tick = tick;
        }

        @Override
        public void run() {
            PlayerState player = gameState.players.get(pid);
            // add new objects spawned by the players
            // to the gamestate

            // apply new projectilestateupdates to the gamestate
            Queue<MovableState> newObjects = player.getSpawnedObjects();
            while (!player.getSpawnedObjects().isEmpty()) {
                MovableState newMovable = newObjects.poll();
                if (newMovable != null) {
                    newMovable.setPosition(player.getPosition());
                    putProjectile(newMovable.getId(), newMovable);
                }
            }
        }
    }

    class GlobalUpdater implements Runnable {
        @Override
        public void run() {

        }
    }


    private void putProjectile ( long k, MovableState v){
        gameState.getProjectilesStateUpdates().add(v);
        gameState.getProjectiles().put(k, v);
    }

    private void removeProjectile ( long key){
        MovableState removed = gameState.getProjectiles().remove(key);
        removed.setAction(Action.KILL);
        gameState.getProjectilesStateUpdates().add(removed);
    }


    private void applyGlobalUpdate(long tick) {
        // update projectiles positions and check collisions
        this.gameState.getProjectiles().forEach((k, v) -> {
            // update the heading vector
            if (v.getTimeAlive() > MAX_TIME_ALIVE) {
                removeProjectile(k);
            } else {
                Vector2 heading = v.getHeading();
                // apply the heading vector
                Vector2 position = v.getPosition();
                Vector2 newPos = position.add(heading);
                v.setPosition(newPos);

                detectCollision(v, tick);
                v.incrementTimeAlive();
            }
        });
        changeActionStatePlayers();
    }

    /**
     * Check if given gameObject collides with any player
     */
    private void detectCollision (MovableState obj,long currentTick){
        // TODO: can be optimized using a quadtree
        for (PlayerState playerState : gameState.players.values()) {
            if (playerState.getPid() == obj.getPid()
                    || playerState.getAction() == Action.DEAD) {
                continue;
            }
            if (playerState.getHitbox().contains(obj.getPosition())) {
                System.out.println("Bullet @" + obj.getPosition() + " HIT " + "Player #" + playerState.getPid() + " @" + playerState.getPosition() + "Current health: " + playerState.getHealth());
                long id = obj.getId();
                playerState.hurt(obj.damage, currentTick);
                removeProjectile(id);
                break;
            }
        }
    }

    private void changeActionStatePlayers() {
        for (Map.Entry mapEntry : gameState.getPlayers().entrySet()) {
            PlayerState playerState = (PlayerState) mapEntry.getValue();
            if (playerState.getAction() == Action.ALIVE) {
                if (playerState.getHealth() <= 0) {
                    playerState.setDead();
                    System.out.println(playerState.getPid() + ": Is dead!");
                }
            } else if (playerState.getAction() == Action.DEAD) {
                if (System.currentTimeMillis() >= playerState.getTimeOfDeath() + RESPAWN_TIME) {
                    playerState.setAlive();
                    System.out.println(playerState.getPid() + " has respawned!");
                }
            }
        }
    }

}

    @Override
    public void update(long tick) {
        // apply global update
    }
}

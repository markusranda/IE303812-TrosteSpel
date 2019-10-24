package no.ntnu.trostespel.game;

import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.entity.GameObject;
import no.ntnu.trostespel.entity.Movable;
import no.ntnu.trostespel.entity.Projectile;
import no.ntnu.trostespel.state.Action;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.util.Collection;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiConsumer;

//TODO: make MasterGameState threadsafe
public class MasterGameState {

    private volatile GameState<PlayerState, MovableState> gameState;

    private static volatile MasterGameState single_instance = null;

    private ExecutorService executor;

    public synchronized static MasterGameState getInstance() {
        if (single_instance == null) {
            single_instance = new MasterGameState(new GameState<>());
        }
        return single_instance;
    }

    private MasterGameState(GameState<PlayerState, MovableState> gameState) {
        this.gameState = gameState;
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * @param pid the id of the player to update
     */
    public void update(long pid) {
        executor.submit(getUpdateRunnable(pid));
    }



    /**
     * Check if given gameObject collides with any player
     */
    private void detectCollision(MovableState obj, long pid) {
        // TODO: can be optimized using a quadtree
        for (PlayerState playerState : gameState.players.values()) {
            if (obj.getHitbox().overlaps(playerState.getHitbox())) {
                if (obj.getPid() != pid) {
                    long id = obj.getId();
                    System.out.println("HIT");
                    int currentHP = playerState.getHealth();
                    playerState.setHealth(currentHP - obj.damage);
                    gameState.getProjectiles().remove(id);

                    obj.setAction(Action.KILL);
                    gameState.getProjectilesStateUpdates().add(obj);
                }
            }
        }
    }

    public void read() {

    }


    public GameState getGameState() {
        return this.gameState;
    }

    private Runnable getUpdateRunnable(long pid) {
        class updater implements Runnable {
            @Override
            public void run() {
                PlayerState player = gameState.players.get(pid);
                // TODO: update the state of the game here, checking collisions with projectiles, updating health etc
                // TODO: make this function not run on the main thread
                // add new objects spawned by the players
                // to the gamestate

                // add new movable updates to the gamestate, which will be sent to the players
                putProjectileStateUpdates(player.getSpawnedObjects());

                // process all new movable updates on the serverside
                for (MovableState update : player.getSpawnedObjects()) {
                    Action action = update.getAction();
                    long key = update.getId();
                    switch (action) {
                        case KILL:
                            //
                        case CREATE:
                            MovableState projectile = new MovableState(pid, GameState.projectileSpeed);
                            projectile.setId(key);
                            this.putProjectile(key, projectile);
                    }
                }
                player.resetSpawnedObjects();

                // update projectiles positions and check collisions
                this.projectileForEach((k, v) -> {
                    // update the heading vector
                    Vector2 heading = v.getHeading();
                    // apply the heading vector
                    Vector2 position = v.getPosition().cpy();
                    Vector2 newPos = position.add(heading);
                    v.setPosition(newPos);
                    detectCollision(v, pid);
                });
            }
            private void putProjectile(long k, MovableState v) {
                gameState.getProjectiles().put(k, v);
            }

            private void removeProjectile(long key) {
                gameState.getProjectiles().remove(key);
            }

            private void putProjectileStateUpdates(Queue<MovableState> updates) {
                gameState.getProjectilesStateUpdates().addAll(updates);
            }

            private void putProjectileStateUpdate(MovableState update) {
                gameState.getProjectilesStateUpdates().add(update);
            }

            private void projectileForEach(BiConsumer<Long, MovableState> action) {
                gameState.getProjectiles().forEach(action);
            }
        }
        return new updater();
    }
}

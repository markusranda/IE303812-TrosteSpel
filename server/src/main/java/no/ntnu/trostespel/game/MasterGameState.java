package no.ntnu.trostespel.game;

import com.badlogic.gdx.math.Vector2;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.entity.Movable;
import no.ntnu.trostespel.state.Action;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

//TODO: make MasterGameState threadsafe
public class MasterGameState {

    private volatile GameState<PlayerState, MovableState> gameState;

    private static volatile MasterGameState single_instance = null;

    private ExecutorService executor;
    private final int MAX_TIME_ALIVE = 5 * CommunicationConfig.TICKRATE; // n seconds

    public synchronized static MasterGameState getInstance() {
        if (single_instance == null) {
            single_instance = new MasterGameState(new GameState<>());
        }
        return single_instance;
    }

    private MasterGameState(GameState<PlayerState, MovableState> gameState) {
        this.gameState = gameState;
        this.gameState.players.put(9L, new PlayerState(9, new Vector2(0, 400), 300)); // put dummy lemur on the the map
        this.executor = new ThreadPoolExecutor(1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue(),
                new ThreadFactoryBuilder().setNameFormat("MasterGameState-Updater").build());
    }

    /**
     * @param pid the id of the player to update
     */
    public synchronized void update(long pid, long currentTick) {
        executor.submit(updateMaster(pid, currentTick));
    }




    public void read() {

    }


    public GameState getGameState() {
        return this.gameState;
    }

    private Runnable updateMaster(long pid, long currentTick) {
        class updater implements Runnable {
            @Override
            public void run() {

                PlayerState player = gameState.players.get(pid);
                // TODO: update the state of the game here, checking collisions with projectiles, updating health etc
                // TODO: make this function not run on the main thread
                // add new objects spawned by the players
                // to the gamestate

                // add new projectilestateupdates to the gamestate
                putProjectileStateUpdates(player.getSpawnedObjects());

                // apply new projectilestateupdates to the gamestate
                for (MovableState update : player.getSpawnedObjects()) {
                    Action action = update.getAction();
                    long key = update.getId();
                    switch (action) {
                        case KILL:
                            //
                        case CREATE:
                            MovableState projectile = new MovableState(pid, GameState.projectileSpeed);
                            projectile.setPosition(player.getPosition());
                            projectile.setAngle(update.getAngle());
                            projectile.setId(key);
                            this.putProjectile(key, projectile);
                    }
                }
                player.resetSpawnedObjects();

                // update projectiles positions and check collisions
                this.projectileForEach((k, v) -> {
                    // update the heading vector
                    if (v.getTimeAlive() > MAX_TIME_ALIVE) {
                        removeProjectile(k);
                    } else {
                        Vector2 heading = v.getHeading();
                        // apply the heading vector
                        Vector2 position = v.getPosition();
                        Vector2 newPos = position.add(heading);
                        v.setPosition(newPos);
                        detectCollision(v, currentTick);
                        changeActionStatePlayers();
                        v.incrementTimeAlive();
                    }
                });
            }

            private void changeActionStatePlayers() {
                for (Map.Entry mapEntry : gameState.getPlayers().entrySet()) {
                    PlayerState playerState = (PlayerState) mapEntry.getValue();
                    if (playerState.getHealth() <= 0) {
                        playerState.setDead();
                    }
                }
            }

            private void putProjectile(long k, MovableState v) {
                gameState.getProjectiles().put(k, v);
            }

            private void removeProjectile(long key) {
                MovableState removed = gameState.getProjectiles().remove(key);
                removed.setAction(Action.KILL);
                putProjectileStateUpdate(removed);
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

            /**
             * Check if given gameObject collides with any player
             */
            private void detectCollision(MovableState obj, long currentTick) {
                // TODO: can be optimized using a quadtree
                for (PlayerState playerState : gameState.players.values()) {
                    if (playerState.getPid() == obj.getPid()) {
                        continue;
                    }
                    if (playerState.getHitbox().contains(obj.getPosition())) {
                        System.out.println("Bullet @" + obj.getPosition() + " HIT " + "Player #" + playerState.getPid() + " @" + playerState.getPosition());
                        long id = obj.getId();
                        playerState.hurt(obj.damage, currentTick);
                        removeProjectile(id);
                        break;
                    }
                }
            }


        }
        return new updater();
    }
}

package no.ntnu.trostespel.game;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.GameRules;
import no.ntnu.trostespel.state.Action;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentMap;

public class GlobalUpdater extends Updater {
    private GameState<PlayerState, MovableState> gameState;
    private long tick;
    private ArrayList<Long> removeList = new ArrayList<>();


    public GlobalUpdater(GameState gameState, long tick) {
        super(2);
        this.gameState = gameState;
        this.tick = tick;
    }

    public GlobalUpdater prepareForUpdate(long tick) {
        this.tick = tick;
        return this;
    }

    private void doUpdate() {
        // update projectiles positions and check collisions
        gameState.getProjectiles().forEach((k, v) -> {
            // update the heading vector
            if (v.getTimeAlive() > GameRules.Projectile.MAX_TIME_ALIVE) {
                removeList.add(k);
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
        for (Long key : removeList) {
            removeProjectile(key);
        }
        removeList.clear();
        changeActionStatePlayers();
    }

    /**
     * Check if given gameObject collides with any player
     */
    private void detectCollision(MovableState obj, long currentTick) {
        // TODO: can be optimized using a quadtree
        ConcurrentMap<Long, PlayerState> players = gameState.getPlayers();
        players.forEach((key, playerState) -> {
            if (playerState.getPid() != obj.getPid() || playerState.getAction() == Action.DEAD) {
                if (players.containsKey(key)) {
                    if (Intersector.overlaps(playerState.getHitboxWithPosition(), obj.getHitboxWithPosition())) {
                        long id = obj.getId();
                        playerState.hurt(obj.damage, currentTick);
                        System.out.println("Bullet @" + obj.getPosition() + " HIT " + "Player #" + playerState.getPid() + " @" + playerState.getPosition() + "Current health: " + playerState.getHealth() + ", Damage: " + obj.damage);
                        removeList.add(id);
                    }
                }
            }
        });
    }

    private void changeActionStatePlayers() {
        ConcurrentMap<Long, PlayerState> players = gameState.getPlayers();

        players.forEach((key, playerState) -> {
            if (players.containsKey(key)) {
                if (playerState.getAction() == Action.ALIVE) {
                    if (playerState.getHealth() <= 0) {
                        playerState.setDead();
                        System.out.println(playerState.getPid() + ": Is dead!");
                    }
                } else if (playerState.getAction() == Action.DEAD) {
                    if (System.currentTimeMillis() >= playerState.getTimeOfDeath() + GameRules.Player.RESPAWN_TIME) {
                        playerState.setAlive();
                        System.out.println(playerState.getPid() + " has respawned!");
                    }
                }
            }
        });
    }

    private void removeProjectile(long key) {
        MovableState removed = gameState.getProjectiles().remove(key);
        if (removed != null) {
            // removed becomes null if a bullet hits multiple players, causing multiple removeProjectile calls
            removed.setAction(Action.KILL);
            gameState.getProjectileEvents().add(removed);
        }
    }

    @Override
    public void run() {
        try {
            doUpdate();
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }
}
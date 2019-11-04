package no.ntnu.trostespel.game;

import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.config.GameRules;
import no.ntnu.trostespel.entity.Movable;
import no.ntnu.trostespel.state.Action;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class GlobalUpdater extends Updater {
    private GameState<PlayerState, MovableState> gameState;
    private long tick;
    private ArrayList<Long> removeList = new ArrayList<>();


    public GlobalUpdater(GameState gameState, long tick) {
        super(2);
        this.gameState = gameState;
        this.tick = tick;
    }

    private void doUpdate() {
        System.out.println("doUpdate " + tick);
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
        for (PlayerState playerState : gameState.players.values()) {
            if (playerState.getPid() == obj.getPid()
                    || playerState.getAction() == Action.DEAD) {
                continue;
            }
            if (playerState.getHitbox().contains(obj.getPosition())) {
                System.out.println("Bullet @" + obj.getPosition() + " HIT " + "Player #" + playerState.getPid() + " @" + playerState.getPosition() + "Current health: " + playerState.getHealth());
                long id = obj.getId();
                playerState.hurt(obj.damage, currentTick);
                removeList.add(id);
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
                if (System.currentTimeMillis() >= playerState.getTimeOfDeath() + GameRules.Player.RESPAWN_TIME) {
                    playerState.setAlive();
                    System.out.println(playerState.getPid() + " has respawned!");
                }
            }
        }
    }

    private void removeProjectile(long key) {
        MovableState removed = gameState.getProjectiles().remove(key);
        removed.setAction(Action.KILL);
        gameState.getProjectilesStateUpdates().add(removed);
    }

    public void setTick(long tick) {
        this.tick = tick;
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
package no.ntnu.trostespel.game;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.GameRules;
import no.ntnu.trostespel.networking.tcp.message.StringMessage;
import no.ntnu.trostespel.networking.tcp.message.TCPEvent;
import no.ntnu.trostespel.networking.tcp.message.TCPMessage;
import no.ntnu.trostespel.state.*;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentMap;


public class GlobalUpdater extends Updater {
    private GameState<PlayerState, MovableState> gameState;
    private long tick;
    private ArrayList<Long> removeList = new ArrayList<>();
    private ArrayList<TCPMessage> globalEvents = new ArrayList<>();
    private StringMessage msg = new StringMessage(TCPEvent.GLOBAL_MESSAGE);


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
                        int newHealth = playerState.hurt(obj, currentTick);
                        if (newHealth < 1) {
                            newEvent(GameEvent.PLAYER_KILLED, playerState);
                        }
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
            gameState.getProjectilesStateUpdates().add(removed);
        }
    }

    private void newEvent(GameEvent event, ObjectState context) {
        switch (event) {
            case KILLSTREAK:
                if (context instanceof PlayerState) {
                    PlayerState player = (PlayerState) context;
                    msg.addMessage(player.getUsername() + " IS ON A BRUTAL " + player.getKillStreak() + " KILL RAMPAGE");
                }
                break;
            case PLAYER_KILLED:
                if (context instanceof PlayerState) {
                    PlayerState player = (PlayerState) context;
                    PlayerState slayer = gameState.getPlayers().get(player.getLastDamager());
                    msg.addMessage(player.getUsername() + " was SLAIN by " + slayer.getUsername());
                    if (slayer.getKillStreak() > 2) {
                        newEvent(GameEvent.KILLSTREAK, slayer);
                    }
                }
                break;
            default:
                System.out.println("Error: Tried to create unsupported event: " + event);;
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

    enum GameEvent {
        KILLSTREAK,
        PLAYER_KILLED
    }

    public StringMessage collectMessage() {
        StringMessage result = new StringMessage(TCPEvent.GLOBAL_MESSAGE, msg.getArgs());
        msg = new StringMessage(TCPEvent.GLOBAL_MESSAGE);
        return result;
    }
}
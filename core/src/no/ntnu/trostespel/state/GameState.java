package no.ntnu.trostespel.state;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.entity.GameObject;
import no.ntnu.trostespel.entity.Movable;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

public class GameState<P, M> {

    private transient static final double BASE_PLAYER_SPEED = 300d;
    public transient static final double playerSpeed = BASE_PLAYER_SPEED / CommunicationConfig.TICKRATE;

    private transient static double BASE_PROJECTILE_SPEED = 300d;
    public transient static double projectileSpeed = BASE_PROJECTILE_SPEED / CommunicationConfig.TICKRATE;

    private transient TiledMapTileLayer collidables;


    private transient HashMap<Long, M> projectiles;

    public HashMap<Long, P> players;
    private Queue<M> projectilesStateUpdates;
    private boolean ack = false;
    private long tick;

    public GameState() {
        projectilesStateUpdates = new ConcurrentLinkedQueue<>();
        projectiles = new HashMap<>();
        players = new HashMap<>();
    }

    public HashMap<Long, M> getProjectiles() {
        return projectiles;
    }

    public Queue<M> getProjectilesStateUpdates() {
        return projectilesStateUpdates;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    public HashMap<Long, P> getPlayers() {
        return players;
    }

    public TiledMapTileLayer getCollidables() {
        return collidables;
    }

    public void setCollidables(TiledMapTileLayer collidables) {
        this.collidables = collidables;
    }

    public long getTick() {
        return tick;
    }

    public void setTick(long tick) {
        this.tick = tick;
    }
}

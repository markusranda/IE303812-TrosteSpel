package no.ntnu.trostespel.state;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class GameState<P, M> {

    private transient TiledMapTileLayer collidables;

    private transient ConcurrentMap<Long, M> projectiles;
    private ConcurrentMap<Long, P> players;

    private Queue<M> projectileEvents;
    private boolean ack = false;
    private long tick;

    public GameState() {
        projectileEvents = new ConcurrentLinkedQueue<>();
        projectiles = new ConcurrentHashMap<>();
        players = new ConcurrentHashMap<>();
    }

    public ConcurrentMap<Long, M> getProjectiles() {
        return projectiles;
    }

    public Queue<M> getProjectileEvents() {
        return projectileEvents;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    public ConcurrentMap<Long, P> getPlayers() {
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

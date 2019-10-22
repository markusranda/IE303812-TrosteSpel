package no.ntnu.trostespel.state;

import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.entity.Movable;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameState<P, M> {

    private transient static double BASE_PLAYER_SPEED = 300d;
    public transient static double playerSpeed = BASE_PLAYER_SPEED / CommunicationConfig.TICKRATE;

    private transient static float BASE_PROJECTILE_SPEED = 900f;
    public transient static float projectileSpeed = BASE_PROJECTILE_SPEED / CommunicationConfig.TICKRATE;

    private transient HashMap<Long, M> projectiles;

    public HashMap<Long, P> players;
    private Queue<M> projectilesStateUpdates;
    private boolean ack = false;



    public GameState() {
        projectilesStateUpdates = new ConcurrentLinkedQueue<>();
        projectiles = new HashMap<>();
        players = new HashMap<>();
    }

    public Queue<M> getProjectileStateUpdates() {
        return projectilesStateUpdates;
    }
    public void setProjectileStateUpdates(Queue<M> map) {
        this.projectilesStateUpdates = map;
    }

    public HashMap<Long, M> getProjectiles() {
        return projectiles;
    }



    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

}

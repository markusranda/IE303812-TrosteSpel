package no.ntnu.trostespel.state;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.annotations.Expose;
import no.ntnu.trostespel.config.CommunicationConfig;
import org.w3c.dom.css.Rect;

import java.beans.Transient;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class PlayerState extends ObjectState{

    private long pid;
    private int health;

    private transient double velocity;
    private transient double maxVelocity;
    public transient int accelrationTimer = 90 / CommunicationConfig.TICKRATE;
    private transient double attackTimer = 0; //
    private transient Queue<MovableState> spawnedObjects = new LinkedList<>();

    public PlayerState(long pid) {
        super(72, 90, new Vector2(55, 55));
        this.pid = pid;
        this.health = 0;
        this.attackTimer = 0;
    }

    public PlayerState(long pid, Vector2 position, int health) {
        super(72, 90, position);
        this.pid = pid;
        this.health = health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setAttackTimer(double attackTimer) {
        this.attackTimer = attackTimer;
    }

    public int getHealth() {
        return health;
    }

    public double getAttackTimer() {
        return attackTimer;
    }

    public long getPid() {
        return pid;
    }

    public double getVelocity() {
        return velocity;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public Queue<MovableState> getSpawnedObjects() {
        return spawnedObjects;
    }

    public void resetSpawnedObjects() {
        this.spawnedObjects.clear();
    }
}

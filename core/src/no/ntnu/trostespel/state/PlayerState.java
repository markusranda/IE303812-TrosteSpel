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
    private transient final short invincibilityFrames = 3;
    private transient long lastTimeDamageTaken = 0;
    private Action action;
    private long timeOfDeath;
    private String username;

    public PlayerState(long pid) {
        super(72, 90, new Vector2(55, 55));
        this.pid = pid;
        this.health = 100;
        this.attackTimer = 0;
        this.action = Action.ALIVE;
    }

    public PlayerState(long pid, Vector2 position, int health) {
        super(72, 90, position);
        this.pid = pid;
        this.health = health;
        this.action = Action.ALIVE;
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

    public void hurt(int damage, long currentTick) {
        if (lastTimeDamageTaken < currentTick - invincibilityFrames) {
            this.health -= damage;
            lastTimeDamageTaken = currentTick;
        }
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

    public void setDead() {
        action = Action.DEAD;
        timeOfDeath = System.currentTimeMillis();
    }

    public Action getAction() {
        return action;
    }

    public boolean isDead() {
        return action == Action.DEAD;
    }

    public long getTimeOfDeath() {
        return timeOfDeath;
    }

    public void setAlive() {
        action = Action.ALIVE;
        health = 100;
        timeOfDeath = 0;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

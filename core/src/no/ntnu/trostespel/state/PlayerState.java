package no.ntnu.trostespel.state;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.annotations.Expose;

import java.beans.Transient;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class PlayerState {

    private long pid;
    private Vector2 position;
    private int health;

    private transient double attackTimer = 0; //
    private transient Queue<MovableState> spawnedObjects = new LinkedList<>();

    public PlayerState(long pid) {
        this.pid = pid;
        this.position = Vector2.Zero;
        this.health = 0;
        this.attackTimer = 0;
    }

    public PlayerState(long pid, Vector2 position, int health) {
        this.pid = pid;
        this.position = position;
        this.health = health;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setAttackTimer(double attackTimer) {
        this.attackTimer = attackTimer;
    }

    public Vector2 getPosition() {
        return position;
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


    public void addPostion(Vector2 displacement) {
        this.position.add(displacement);
    }

    public Queue<MovableState> getSpawnedObjects() {
        return spawnedObjects;
    }

    public void resetSpawnedObjects() {
        this.spawnedObjects.clear();
    }
}

package no.ntnu.trostespel.state;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class PlayerState {

    private long pid;
    private Vector2 position;
    private int health;
    private long attackTimer = 0; //
    private HashMap<Long, MovableState> spawnedObjects = new HashMap<>();

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

    public void setAttackTimer(long attackTimer) {
        this.attackTimer = attackTimer;
    }

    public Vector2 getPosition() {
        return position;
    }

    public int getHealth() {
        return health;
    }

    public long getAttackTimer() {
        return attackTimer;
    }

    public long getPid() {
        return pid;
    }


    public void addPostion(Vector2 displacement) {
        this.position.add(displacement);
    }

    public HashMap<Long, MovableState> getSpawnedObjects() {
        return spawnedObjects;
    }
}

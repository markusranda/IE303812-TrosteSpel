package no.ntnu.trostespel.state;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class PlayerState {

    private long pid;
    private Vector2 position;
    private int health;
    private long attackTimer; //
    private HashMap<Long, MovableState> spawnedObjects;

    public PlayerState(long pid) {
        this.pid = pid;
        this.position = new Vector2();
        this.health = 100;
        this.attackTimer = 0;
        spawnedObjects = new HashMap<>();
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

    public PlayerState update(PlayerState stateChange) {
        this.position.add(stateChange.position);
        this.health += stateChange.health;
        this.attackTimer += stateChange.attackTimer;
        return this;
    }

    public void addPostion(Vector2 displacement) {
        this.position.add(displacement);
    }

    public HashMap<Long, MovableState> getSpawnedObjects() {
        return spawnedObjects;
    }
}

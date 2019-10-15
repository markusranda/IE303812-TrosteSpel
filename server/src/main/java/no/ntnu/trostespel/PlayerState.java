package no.ntnu.trostespel;

import com.badlogic.gdx.math.Vector2;

public class PlayerState {
    private Vector2 displacement;
    private int health;
    private long attackTimer;
    private boolean isNew = false;

    public void setDisplacement(Vector2 displacement) {
        this.displacement = displacement;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setAttackTimer(long attackTimer) {
        this.attackTimer = attackTimer;
    }

    public Vector2 getDisplacement() {
        return displacement;
    }

    public int getHealth() {
        return health;
    }

    public long getAttackTimer() {
        return attackTimer;
    }

    public boolean isNew() {
        return isNew;
    }
}

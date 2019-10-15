package no.ntnu.trostespel.state;

import com.badlogic.gdx.math.Vector2;

import java.util.concurrent.atomic.AtomicLong;

public class MovableState {
    private long id;
    private float velocity;
    private float angle;
    private Vector2 position;

    public MovableState() {
        this.id = createID();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public float getVelocity() {
        return velocity;
    }

    public float getAngle() {
        return angle;
    }

    public Vector2 getPosition() {
        return position;
    }

    private static AtomicLong idCounter = new AtomicLong();
    public static long createID() {
        return idCounter.getAndIncrement();
    }
}

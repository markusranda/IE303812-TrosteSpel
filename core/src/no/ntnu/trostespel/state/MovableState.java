package no.ntnu.trostespel.state;

import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.CommunicationConfig;

import java.util.concurrent.atomic.AtomicLong;

public class MovableState {
    private long id;
    private long pid;
    private float velocity;
    private float angle;

    public MovableState(long pid) {
        this.id = createID();
        this.angle = 0;
        this.velocity = (float) (1000 / CommunicationConfig.TICKRATE);
        this.pid = pid;
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

    public long getId() {
        return id;
    }

    public float getVelocity() {
        return velocity;
    }

    public float getAngle() {
        return angle;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    private static AtomicLong idCounter = new AtomicLong();
    public static long createID() {
        return idCounter.getAndIncrement();
    }
}

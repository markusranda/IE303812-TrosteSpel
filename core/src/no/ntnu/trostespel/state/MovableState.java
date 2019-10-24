package no.ntnu.trostespel.state;

import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.entity.Movable;

import java.util.concurrent.atomic.AtomicLong;

public class MovableState extends ObjectState {

    private long id;
    private long pid;
    private double velocity;
    private float angle;
    private Action action;

    private transient Vector2 heading;
    public final transient int damage = 5;

    public MovableState(long pid, double velocity) {
        super(24f, 24f, Vector2.Zero);
        this.heading = new Vector2(1, 0); // Unit vector
        this.id = createID();
        this.angle = 0;
        this.velocity = velocity;
        this.pid = pid;
        this.action = Action.CREATE;
    }

    private MovableState(long id, long pid) {
        super(24f, 24f, Vector2.Zero);
        this.heading = new Vector2(0, 0); // Unit vector
        this.angle = 0;
        this.velocity = 0;
        this.pid = pid;
        this.action = Action.CREATE;
        this.id = id;
    }

    /**
     * @param pid
     * @return a new movablestate with the kill action
     */
    public static MovableState kill(long id, long pid) {
        MovableState returnVal = new MovableState(id, pid);
        returnVal.setAction(Action.KILL);
        return returnVal;
    }


    public void setAction(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    private void setId(int id) {
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

    public double getVelocity() {
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

    public Vector2 getHeading() {
        heading.setAngle(angle);
        heading.setLength((float) velocity);
        return heading;
    }

    public void setHeading(Vector2 heading) {
        this.heading = heading;
    }
}

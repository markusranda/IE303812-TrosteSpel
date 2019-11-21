package no.ntnu.trostespel.state;

import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.GameRules;

import java.util.concurrent.atomic.AtomicLong;

import static no.ntnu.trostespel.state.Action.KILL;

public class MovableState extends ObjectState {

    private long id;
    private long pid;
    private Action action;
    private Vector2 heading;

    private transient int timeAlive;
    public final transient int damage = 15;
    private static AtomicLong idCounter = new AtomicLong();

    public MovableState(long pid, double velocity) {
        super(24f, 24f, Vector2.Zero);
        this.heading = new Vector2(1, 0); // Unit vector
        this.id = createID();
        this.pid = pid;
        this.action = Action.CREATE;
        this.heading = new Vector2(1, 0);
        this.heading.setLength((float) velocity);
    }

    public MovableState(double velocity) {
        super(24f, 24f, Vector2.Zero);
        this.heading = new Vector2(1, 0); // Unit vector
        this.id = createID();
        this.pid = 0;
        this.action = Action.CREATE;
        this.heading = new Vector2(1, 0);
        this.heading.setLength((float) velocity);
    }

    public void setPositionWithSpawnOffset(Vector2 position) {
        this.setPosition(position.add(GameRules.Projectile.SPAWN_OFFSET));
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
        this.heading.setLength(velocity);
    }

    public void setAngle(float angle) {
        this.heading.setAngle(angle);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getVelocity() {
        return this.heading.len();
    }

    public float getAngle() {
        return this.heading.angle();
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public static long createID() {
        return idCounter.getAndIncrement();
    }

    public Vector2 getHeading() {
        return heading.cpy();
    }

    public void setHeading(Vector2 heading) {
        this.heading = heading;
    }

    public int getTimeAlive() {
        return timeAlive;
    }

    public void incrementTimeAlive() {
        this.timeAlive++;
    }

    /**
     * This method is intented for reseting the object and make it ready for reuse.
     */
    public void resetObject() {
        setPosition(Vector2.Zero);
        id = createID();
        pid = 0;
        action = KILL;
        heading = Vector2.Zero;
        timeAlive = 0;
    }
}

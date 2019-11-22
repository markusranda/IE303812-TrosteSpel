package no.ntnu.trostespel.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.CommunicationConfig;

public abstract class GameObject {
    protected Texture texture;
    protected Rectangle shape;
    protected float width;
    protected float height;

    protected float stateTime;

    private Vector2 pos;



    //interpolation vars
    private Interpolation movementInterpolation = Interpolation.smooth;
    private final float iDuration = 1f / CommunicationConfig.TICKRATE;
    private float iElapsed = 0f;
    private Vector2 startPos = Vector2.Zero;
    private Vector2 targetPos = Vector2.Zero;
    float progress;


    public GameObject(Vector2 pos, float width, float height, Rectangle shape, Texture texture) {
        super();
        this.pos = pos;
        this.width = width;
        this.height = height;
        shape.height = height;
        shape.width = width;
        shape.x = pos.x;
        shape.y = pos.y;
        this.texture = texture;
        this.shape = shape;

        stateTime = 0;

        startPos = pos;
        targetPos = pos;
    }

    public abstract void draw(Batch batch);

    public void setPos(int x, int y) {
        pos.x = x;
        pos.y = y;
    }

    public Vector2 getPos() {
        return pos.cpy();
    }

    public Vector2 getCenterPos() {
        return getHitbox().getCenter(new Vector2());
    }

    public Rectangle getHitbox() {
        this.shape.set(pos.x, pos.y, width, height);
        return this.shape;
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    /**
     * Interpolate to given pos. Starts new interpolations when receiving
     * a target that is different from the last
     * @param targetPos
     */
    public void interpolatePos(Vector2 targetPos) {
        this.startPos = pos.cpy();
        if (targetPos != this.targetPos) {
            // start new interpolation
            progress = Math.min(1f, iElapsed / iDuration);
            iElapsed = 0f + Gdx.graphics.getDeltaTime();
            this.targetPos = targetPos;
            pos.interpolate(targetPos, progress, movementInterpolation);
        } else {
            // continue old interpolation
            iElapsed += Gdx.graphics.getDeltaTime();
            progress = Math.min(1f, iElapsed / iDuration);
            pos.interpolate(targetPos, progress, movementInterpolation);
        }
    }

}
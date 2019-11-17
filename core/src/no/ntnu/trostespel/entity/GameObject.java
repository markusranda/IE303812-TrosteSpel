package no.ntnu.trostespel.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {
    protected Texture texture;
    protected Rectangle shape;
    protected float width;
    protected float height;

    protected float stateTime;

    private Vector2 pos;

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

}
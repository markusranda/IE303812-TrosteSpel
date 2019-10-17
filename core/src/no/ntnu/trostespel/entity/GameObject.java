package no.ntnu.trostespel.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;

public abstract class GameObject extends Sprite {
    Texture texture;
    Rectangle shape;
    float width;
    float height;

    float stateTime;

    private Vector2 pos;

    public GameObject (Vector2 pos, float width, float height, Rectangle shape, Texture texture) {
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

    public abstract void draw(SpriteBatch batch);

    public void setPos(int x, int y) {
        pos.x = x;
        pos.y = y;
    }

    public Vector2 getPos() {
        return pos;
    }

    public void displace(float x, float y) {
        pos.x += x;
        pos.y += y;
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }
}
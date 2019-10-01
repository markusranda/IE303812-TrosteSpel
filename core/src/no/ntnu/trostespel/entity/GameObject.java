package no.ntnu.trostespel.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {
    private Texture texture;
    private Rectangle shape;

    public float x;
    public float y;

    public GameObject (float x, float y, float width, float height, Rectangle shape, Texture texture) {
        this.x = x;
        this.y = y;
        shape.height = height;
        shape.width = width;
        shape.x = x;
        shape.y = y;
        this.texture = texture;
        this.shape = shape;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }
}
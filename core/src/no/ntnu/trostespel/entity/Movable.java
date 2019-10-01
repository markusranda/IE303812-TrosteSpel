package no.ntnu.trostespel.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.controller.ObjectController;

public abstract class Movable extends GameObject {
    public final Vector2 velocity = null;
    public Vector2 displacement;
    public final Vector2 accel = null;

    public ObjectController objectController;

    public Movable(float x, float y, float width, float height, Rectangle rect, Texture texture, ObjectController objectController) {
        super(x, y, width, height, rect, texture);
        this.objectController = objectController;

    }

    public void update(float delta) {
        //Update position
        displacement = objectController.update(delta);
        super.x += displacement.x;
        super.y += displacement.y;
    }


}


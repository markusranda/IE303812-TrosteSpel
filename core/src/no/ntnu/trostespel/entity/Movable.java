package no.ntnu.trostespel.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.controller.ObjectController;

public abstract class Movable extends GameObject {

    public Vector2 displacement;
    public ObjectController objectController;
    boolean moving = false;
    int direction = 1;
    Vector2 previousPos;

    public Movable(Vector2 pos, float width, float height, Rectangle rect, Texture texture, ObjectController objectController) {
        super(pos, width, height, rect, texture);
        previousPos = new Vector2().setZero();
        this.objectController = objectController;

    }

    public void update(float delta) {
        //Update position
        Vector2 position = getPos();
        if (position == null) {
            return;
        }
        if (!position.epsilonEquals(previousPos)) {
            displacement = previousPos.sub(position).scl(-1);
            System.out.println(displacement);
            moving = true;
        } else {
            moving = false;
        }
        previousPos = position;
    }


}


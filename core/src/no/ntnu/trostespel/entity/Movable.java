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
        displacement = objectController.update(delta);
        displace(displacement.x, displacement.y);
        if (!displacement.isZero()) {
            moving = true;
            if (displacement.x > 0) {
                direction = 1;
            } else if (displacement.x < 0){
                direction = -1;
            }
        } else {
            moving = false;
        }
        previousPos = getPos();
    }



}


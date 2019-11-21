package no.ntnu.trostespel.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Movable extends GameObject {

    protected Vector2 displacement;
    boolean moving = false;
    int direction = 1;
    Vector2 previousPos;

    private long previousTick;

    // interpolation var
    float epsilon = .1f;

    public Movable(Vector2 pos, float width, float height, Rectangle rect, Texture texture) {
        super(pos, width, height, rect, texture);
        previousPos = new Vector2().setZero();
    }

    public void update(float delta, long tick) {
        //Update position
        Vector2 position = getPos();
        if (position == null) {
            return;
        }

        if (tick > previousTick) {
            if (!position.epsilonEquals(previousPos, epsilon)) {
                displacement = previousPos.cpy().sub(position).scl(-1);
                moving = true;
            } else {
                moving = false;
            }
            previousPos = position;
            previousTick = tick;
        }
    }

}


package no.ntnu.trostespel.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import no.ntnu.trostespel.controller.ObjectController;

public class Player extends Movable {


    public Player(float x, float y, float width, float height, Rectangle rect, Texture texture, ObjectController objectController) {
        super(x, y, width, height, rect, texture, objectController);
    }

}

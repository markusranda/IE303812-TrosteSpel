package no.ntnu.TrosteSpel.entities;

import org.newdawn.slick.geom.Shape;

class Barrel extends Movable {
    private int hitpoints = 30;

    public Barrel(float x, float y, Shape s) {
        super(x,y,s);
    }

    public boolean hurt(int damage) {
        hitpoints -= damage;
        if(hitpoints <= 0)
            return true;
        return false;
    }
}
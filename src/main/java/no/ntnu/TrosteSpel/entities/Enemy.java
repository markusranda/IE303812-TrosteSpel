package no.ntnu.TrosteSpel.entities;

import org.newdawn.slick.geom.Shape;

class Enemy extends Movable {

    private int hitpoints = 50;
    public int offset = (int)(Math.random()*1000);
    public int deltaCount = offset;

    public Enemy(float x, float y, Shape s) {
        super(x,y,s);
    }

    public boolean hurt(int damage) {
        hitpoints -= damage;
        if(hitpoints <= 0)
            return true;
        return false;
    }

}
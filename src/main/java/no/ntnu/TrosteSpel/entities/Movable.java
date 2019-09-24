package no.ntnu.TrosteSpel.entities;


import org.newdawn.slick.geom.Shape;

public class Movable {

    public float x;
    public float y;
    public float dx;
    public float dy;
    private Shape shape;

    public Movable(float x, float y, Shape s) {
        this.x = x;
        this.y = y;
        shape = s;
    }

    public Shape getShape() {
        shape.setX(x-shape.getWidth()/2);
        shape.setY(y-shape.getHeight()/2);
        return shape;
    }

    public float distance(Movable m) {
        return (float)Math.sqrt(Math.pow(m.x-x,2)+Math.pow(m.y-y,2));
    }

}
package no.ntnu.trostespel.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Projectile extends Movable {

    private float velocity;
    private float angle;
    private Vector2 heading;


    public Projectile(Vector2 pos, Texture texture, float velocity, float angle) {
        super(pos, 24, 24, new Rectangle(), texture);
        this.velocity = velocity;
        this.angle = angle;
        heading = new Vector2(1, 0);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        //Vector2 heading = new Vector2();
        //double rad = Math.toRadians(angle);
        //heading.x = (float) Math.sin(rad);
        //heading.y = (float) Math.cos(rad);
        //heading.nor();
        //heading.scl(velocity * delta);
        //System.out.println(delta);
        heading.setAngle(angle);
        heading.setLength(velocity);
        getPos().add(heading);

    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, getPos().x, getPos().y, 24, 24);
    }
}

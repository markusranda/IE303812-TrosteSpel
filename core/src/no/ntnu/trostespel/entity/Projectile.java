package no.ntnu.trostespel.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.CommunicationConfig;

public class Projectile extends Movable {

    private double velocity;
    private float angle;
    private Vector2 heading;
    private long id;


    public Projectile(Vector2 pos, Texture texture, double velocity, float angle, long id) {
        super(pos, 24, 24, new Rectangle(), texture);
        this.velocity = velocity;
        this.angle = angle;
        heading = new Vector2(1, 0);
        this.id = id;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // update the heading vector
        heading.setAngle(angle);
        heading.setLength((float) velocity * delta * CommunicationConfig.TICKRATE);

        // apply the heading vector
        Vector2 position = getPos();
        Vector2 newPos = position.add(heading);
        setPos(newPos);
    }

    @Override
    public void draw(SpriteBatch batch) {}

    public Texture getTextureRegion() {
        return texture;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public long getId() {
        return id;
    }
}

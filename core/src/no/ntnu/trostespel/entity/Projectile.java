package no.ntnu.trostespel.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.config.GameRules;

public class Projectile extends Movable {

    private double velocity;
    private float angle;
    private Vector2 heading;
    private long id;
    private final Vector2 SPAWN_OFFSET = new Vector2(GameRules.Projectile.SPAWN_POINT_x_OFFSET, GameRules.Projectile.SPAWN_POINT_Y_OFFSET);


    public Projectile(Vector2 pos, Texture texture, double velocity, float angle, long id) {
        super(pos, 24, 24, new Rectangle(), texture);
        Vector2 spawnPos = getPos().add(SPAWN_OFFSET);
        setPos(spawnPos);
        this.velocity = velocity;
        this.angle = angle;
        heading = new Vector2(1, 0);
        this.id = id;
    }

    @Override
    public void update(float delta, long tick) {
        super.update(delta, tick);

        // update the heading vector
        heading.setAngle(angle);
        heading.setLength((float) velocity * delta * CommunicationConfig.TICKRATE);

        // apply the heading vector
        Vector2 position = getPos();
        Vector2 newPos = position.add(heading);
        setPos(newPos);
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(
                getTextureRegion(),
                getPos().x,
                getPos().y,
                getWidth(),
                getHeight());
    }

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

    public float getAngle() {
        return angle;
    }
}

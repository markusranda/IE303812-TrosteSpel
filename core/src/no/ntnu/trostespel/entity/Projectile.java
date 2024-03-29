package no.ntnu.trostespel.entity;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.config.GameRules;

public class Projectile extends Movable implements Pool.Poolable {

    private double velocity;
    private float angle;
    private Vector2 heading;
    private long id;


    public Projectile(Vector2 pos, Texture texture, double velocity, float angle, long id) {
        super(pos, 24, 24, new Rectangle(), texture);
        Vector2 spawnPos = getPos().add(GameRules.Projectile.SPAWN_OFFSET);
        setPos(spawnPos);
        this.velocity = velocity;
        this.angle = angle;
        heading = new Vector2(1, 0);
        this.id = id;
    }

    public Projectile construct(Vector2 pos, double velocity, float angle, long id) {
        Vector2 spawnPos = pos.add(GameRules.Projectile.SPAWN_OFFSET);
        setPos(spawnPos);
        this.velocity = velocity;
        this.angle = angle;
        this.id = id;
        return this;
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

    @Override
    public void reset() {
        setPos(Vector2.Zero);
        this.velocity = 0;
        this.angle = 0;
        heading = new Vector2(1, 0);
        this.id = -1;
    }
}

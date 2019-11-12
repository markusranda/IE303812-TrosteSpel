package no.ntnu.trostespel.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.Assets;

public class HealthBar {

    private float TOTAL_BAR_WIDHT;
    private final float BG_SCALE = 0.95f;
    private float TOTAL_BAR_HEIGHT;
    private Vector2 OFFSET;
    private float WIDTH_PADDING;
    private float healthScaledWidth;

    private float totalHealth;

    private Texture front = Assets.healthbarFront;
    private Texture back = Assets.healthbarBack;

    private Vector2 position = new Vector2(0, 0);

    public HealthBar(float totalHealth, Rectangle owner) {
        this.totalHealth = totalHealth;
        this.position = owner.getPosition(position);
        this.OFFSET = new Vector2(0, 0).add(new Vector2(0, owner.height));
        this.TOTAL_BAR_WIDHT = owner.width;
        this.TOTAL_BAR_HEIGHT = 11;

        // add 10% padding on both sides
        this.WIDTH_PADDING = TOTAL_BAR_WIDHT * 0.2f;
        this.TOTAL_BAR_WIDHT -= this.WIDTH_PADDING;
        this.OFFSET.x += this.WIDTH_PADDING / 2;

    }

    public void update(Vector2 pos, float health) {
        position = pos.add(OFFSET);
        healthScaledWidth = (health / totalHealth) * TOTAL_BAR_WIDHT;
        if (health <= 0) healthScaledWidth = 0;
    }

    public void draw(SpriteBatch batch) {

        // draw outline
        batch.draw(
                back,
                position.x,
                position.y,
                TOTAL_BAR_WIDHT,
                TOTAL_BAR_HEIGHT);

        // draw healthbar
        batch.draw(
                front,
                position.x,
                position.y,
                healthScaledWidth,
                TOTAL_BAR_HEIGHT);
    }

    protected Vector2 getPosition() {
        return this.position.cpy();
    }

    public float getTOTAL_BAR_HEIGHT() {
        return TOTAL_BAR_HEIGHT;
    }
}

package no.ntnu.trostespel.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class OverheadUI {
    BitmapFont font = new BitmapFont();
    private HealthBar healthBar;
    private String name;

    public OverheadUI(float health, Rectangle owner, String name) {
        this.healthBar = new HealthBar(health, owner);
        this.name = name;
        font.getData().setScale(.7f);
    }

    public void update(Vector2 position, float health) {
        this.healthBar.update(position, health);
    }

    public void draw(ShapeRenderer batch, SpriteBatch spriteBatch) {
        healthBar.draw(batch);
        Vector2 postion = healthBar.getPosition();
        font.draw(spriteBatch, name, postion.x, postion.y + healthBar.getTOTAL_BAR_HEIGHT() + 10);
    }
}

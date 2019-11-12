package no.ntnu.trostespel.entity;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class OverheadUI {
    BitmapFont font = new BitmapFont();
    private HealthBar healthBar;
    private String name;
    private boolean nameChanged = false;

    public OverheadUI(float health, Rectangle owner, String name) {
        this.healthBar = new HealthBar(health, owner);
        this.name = name;
        font.getData().setScale(.85f);
    }

    public void update(Vector2 position, float health) {
        this.healthBar.update(position, health);
    }

    public void draw(SpriteBatch spriteBatch) {
        healthBar.draw(spriteBatch);
        Vector2 postion = healthBar.getPosition();
        if (nameChanged) {
            nameChanged = false;
        }
        font.draw(spriteBatch, name, postion.x, postion.y + healthBar.getTOTAL_BAR_HEIGHT() + 10);
    }

    public void setUsername(String name) {
        this.name = name;
        this.nameChanged = true;
    }
}

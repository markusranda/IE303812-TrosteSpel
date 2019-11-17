package no.ntnu.trostespel.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class OverheadUI {
    private BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/username.fnt"));
    private HealthBar healthBar;
    private String name;
    private boolean nameChanged = false;

    public OverheadUI(float health, Rectangle owner, String name) {
        this.healthBar = new HealthBar(health, owner);
        this.name = name;
        font.getData().setScale(.45f);
    }

    public void update(Vector2 position, float health) {
        this.healthBar.update(position, health);
    }

    public void draw(SpriteBatch spriteBatch) {
        healthBar.draw(spriteBatch);
        Vector2 position = healthBar.getPosition();
        if (nameChanged) {
            nameChanged = false;
        }
        font.draw(spriteBatch, name, position.x, position.y + healthBar.getTOTAL_BAR_HEIGHT() + 20);
    }

    public void setUsername(String name) {
        this.name = name;
        this.nameChanged = true;
    }
}

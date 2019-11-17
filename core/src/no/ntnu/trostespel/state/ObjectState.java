package no.ntnu.trostespel.state;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.GameRules;

public class ObjectState {

    private transient Rectangle hitbox;
    private volatile Vector2 position;

    private ObjectState() {
        // kryo requires a no-args constructor to work properly
    }

    public ObjectState(float x, float y, Vector2 position) {
        this.hitbox = new Rectangle(0, 0, x, y);
        this.position = position;
    }

    public Rectangle getHitboxWithPosition() {
        hitbox.x = position.x;
        hitbox.y = position.y;
        return hitbox;
    }

    /**
     * return the raw hitbox object without any specific set values
     * @return
     */
    public Rectangle getHitbox() {
        return hitbox;
    }

    public Vector2 getPosition() {
        return this.position.cpy();
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void addPostion(Vector2 displacement) {
        this.position.add(displacement);
    }

}

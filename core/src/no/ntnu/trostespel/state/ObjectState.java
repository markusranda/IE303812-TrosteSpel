package no.ntnu.trostespel.state;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ObjectState {

    private transient Rectangle hitbox;
    private volatile Vector2 position;

    public ObjectState(float x, float y, Vector2 position) {
        this.hitbox = new Rectangle(0, 0, x, y);
        this.position = position;
    }

    public Rectangle getHitbox() {
        hitbox.x = position.x;
        hitbox.y = position.y;
        return hitbox;
    }

    public Vector2 getPosition() {
        return this.position.cpy();
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public boolean collides(ObjectState otherObject) {
        return this.hitbox.overlaps(otherObject.getHitbox());
    }

    public void addPostion(Vector2 displacement) {
        this.position.add(displacement);
    }

}

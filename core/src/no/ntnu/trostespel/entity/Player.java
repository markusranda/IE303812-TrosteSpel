package no.ntnu.trostespel.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.Assets;

public class Player extends Movable {

    private static final int FRAME_COLS = 1, FRAME_ROWS = 6;

    private Animation<TextureRegion> run;
    private TextureRegion currentFrame;
    private boolean flip = false;
    private Weapon weapon;
    private int health;
    private long pid;
    private boolean addedToLayer = false;

    private final int TEXTURE_HEIGHT = 90;
    private final int TEXTURE_WIDTH = 72;
    private final int HITBOX_HEIGHT = TEXTURE_HEIGHT / 3;
    private final int HITBOX_WIDTH = (TEXTURE_WIDTH / 4) * 2;
    private final int HEIGHT_OFFSET = (TEXTURE_HEIGHT - HITBOX_HEIGHT) / 2;
    private final int WIDTH_OFFSET = (TEXTURE_WIDTH - HITBOX_WIDTH) / 2;

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public boolean addedToLayer() {
        return addedToLayer;
    }

    public void setAddedToLayer(boolean addedToLayer) {
        this.addedToLayer = addedToLayer;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public boolean isMoving() {
        return moving;
    }

    public TextureRegion getCurrentframe() {
        return this.currentFrame;
    }

    public void removedFromLayer() {
        addedToLayer = false;
    }

    private enum Direction {
        right,
        left
    }


    public Player(Vector2 pos, Texture texture) {
        super(pos, 72, 90, new Rectangle(), texture);
        initAnimation();
        this.health = 100;
    }

    private void initAnimation() {
        TextureRegion[][] tmp = TextureRegion.split(
                Assets.lemurRunSheet,
                Assets.lemurRunSheet.getWidth() / FRAME_ROWS,
                Assets.lemurRunSheet.getHeight() / FRAME_COLS);

        TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_COLS; i++) {
            for (int j = 0; j < FRAME_ROWS; j++) {
                frames[index++] = tmp[i][j];
            }
        }
        run = new Animation<>(0.1f, frames);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void draw(SpriteBatch batch) { }

    public Texture getTexture() {
        return this.texture;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    private Direction getDirection() {
        return displacement.x < 0 ? Direction.left : Direction.right;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void animateWalking() {
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = run.getKeyFrame(stateTime, true);
        flip = (getDirection() == Direction.left);
    }

    @Override
    public Rectangle getHitbox() {
        Vector2 pos = getPos();
        super.shape.height = HITBOX_HEIGHT;
        super.shape.width = HITBOX_WIDTH;
        super.shape.x = pos.x + WIDTH_OFFSET;
        super.shape.y = pos.y + HEIGHT_OFFSET;
        return super.shape;
    }

    public boolean getFlip() {
        return this.flip;
    }
}

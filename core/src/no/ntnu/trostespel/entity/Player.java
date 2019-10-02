package no.ntnu.trostespel.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.Assets;
import no.ntnu.trostespel.controller.ObjectController;
import org.w3c.dom.Text;

public class Player extends Movable {

    private static final int FRAME_COLS = 1, FRAME_ROWS = 6;

    public Animation<TextureRegion> runHead;
    public Animation<TextureRegion> runBody;
    private TextureRegion currentFrame;
    private boolean flip = false;

    private enum Direction {
        right,
        left
    }


    public Player(Vector2 pos, Texture texture, ObjectController objectController) {
        super(pos, 72, 90, new Rectangle(), texture, objectController);
        runHead = initAnimation(Assets.lemurHeadRunSheet);
        runBody = initAnimation(Assets.lemurBodyRunSheet);

    }

    private Animation<TextureRegion> initAnimation(Texture texture) {
        TextureRegion[][] tmp = TextureRegion.split(
                texture,
                texture.getWidth() / FRAME_ROWS,
                texture.getHeight() / FRAME_COLS);

        TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_COLS; i++) {
            for (int j = 0; j < FRAME_ROWS; j++) {
                frames[index++] = tmp[i][j];
            }
        }
        return new Animation<TextureRegion>(0.1f, frames);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void draw(SpriteBatch batch) {

        if (moving) {
            stateTime += Gdx.graphics.getDeltaTime();
            currentFrame = runBody.getKeyFrame(stateTime, true);
            flip = (getDirection() == Direction.left);
            batch.draw(currentFrame, flip ? getPos().x+width : getPos().x, getPos().y, flip ? -width : width, height);
            batch.draw(runHead.getKeyFrame(0), getPos().x, getPos().y, width, height);

        } else {
            batch.draw(texture, flip ? getPos().x+width : getPos().x, getPos().y, flip ? -width : width, height);
        }
    }

    private Direction getDirection() {
        return displacement.x < 0 ? Direction.left : Direction.right;
    }
}

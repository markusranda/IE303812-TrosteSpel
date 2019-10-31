package no.ntnu.trostespel.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {

    public static Texture img;
    public static Texture lemurImage;
    public static Texture lemurRunSheet;
    public static Texture bullet;
    public static Texture attack;

    public static void load() {
        img = new Texture("badlogic.jpg");
        lemurImage = new Texture(Gdx.files.internal("lemurSideIdle.png"));
        lemurRunSheet = new Texture(Gdx.files.internal("lemurRunSheet.png"));
        attack = new Texture(Gdx.files.internal("lemur-openmouth.png"));
        bullet = new Texture(Gdx.files.internal("bullet_texture.png"));
    }
}

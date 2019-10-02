package no.ntnu.trostespel.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Assets {

    public static Texture img;
    public static Texture lemurImage;
    public static Texture lemurBodyRunSheet;
    public static Texture lemurHeadRunSheet;

    public static void load() {
        img = new Texture("badlogic.jpg");
        lemurImage = new Texture(Gdx.files.internal("lemurSideIdle.png"));
        lemurHeadRunSheet = new Texture(Gdx.files.internal("lemurHeadRunSheet.png"));
        lemurBodyRunSheet = new Texture(Gdx.files.internal("lemurBodyRunSheet.png"));
    }
}

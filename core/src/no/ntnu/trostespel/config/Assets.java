package no.ntnu.trostespel.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Assets {

    public static Texture img;
    public static Texture lemurImage;

    public static void load() {
        img = new Texture("badlogic.jpg");
        lemurImage = new Texture(Gdx.files.internal("lemur.png"));
    }
}

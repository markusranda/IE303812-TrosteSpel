package no.ntnu.trostespel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.ntnu.trostespel.config.Assets;
import no.ntnu.trostespel.config.KeyConfig;
import no.ntnu.trostespel.config.ServerConnection;
import no.ntnu.trostespel.networking.UserInputManager;

import static no.ntnu.trostespel.config.Assets.img;

/**
 * Main class
 */
public class TrosteSpel extends Game {
	public SpriteBatch batch;
    public KeyConfig keys;



    @Override
    public void create() {
        // load textures
        Assets.load();
        ServerConnection.loadDefault();
        batch = new SpriteBatch();
        setScreen(new MainGameState(this));
    }




    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }
}

package no.ntnu.trostespel.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import no.ntnu.trostespel.TrosteSpel;
import no.ntnu.trostespel.config.ScreenConfig;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Troster i Kamp";
		config.width = ScreenConfig.SCREEN_WIDTH;
		config.height = ScreenConfig.SCREEN_HEIGHT;
		config.foregroundFPS = 144;
		new LwjglApplication(new TrosteSpel(), config);
	}
}

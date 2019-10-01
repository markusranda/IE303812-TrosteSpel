package no.ntnu.trostespel.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import no.ntnu.trostespel.TrosteSpel;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Troster i Kamp";
		config.width = 800;
		config.height = 800;
		new LwjglApplication(new TrosteSpel(), config);
	}
}

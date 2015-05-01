package com.github.piotrkruk.phage_wars.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.piotrkruk.phage_wars.PhageWars;

public class DesktopLauncher {
	public static void main (String[] arg) {
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = PhageWars.WIDTH;
		config.height = PhageWars.HEIGHT;
		config.title = PhageWars.TITLE;
		
		new LwjglApplication(new PhageWars(), config);
	}
}
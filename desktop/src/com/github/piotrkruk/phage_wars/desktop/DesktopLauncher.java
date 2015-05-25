package com.github.piotrkruk.phage_wars.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.piotrkruk.phage_wars.PhageWars;

public class DesktopLauncher {
	private static PhageWars phageWars = new PhageWars();
	
	public static void main (String[] arg) {
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = phageWars.mode.width;
		config.height = phageWars.mode.height;
		config.title = phageWars.title;
		config.resizable = false;
		config.addIcon("icon_launcher.png", Files.FileType.Internal);
		new LwjglApplication(phageWars, config);
	}
}

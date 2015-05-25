package com.github.piotrkruk.phage_wars.desktop;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.piotrkruk.phage_wars.PhageWars;
import com.github.piotrkruk.phage_wars.PhageWars.DisplayMode;

public class DesktopLauncher {
	private static PhageWars phageWars = new PhageWars();
	
	public static void main (String[] arg) {
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		
		System.out.println("Detected screen size: " + dimension.width + "x" + dimension.height + ".");
		
		if (dimension.getWidth() > 1.2 * DisplayMode.HD.width) {
			System.out.println("Choosing HD mode.");
			phageWars.mode = DisplayMode.HD;
		} else {
			System.out.println("Choosing normal mode.");
			phageWars.mode = DisplayMode.NORMAL;
		}
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = phageWars.mode.width;
		config.height = phageWars.mode.height;
		config.title = phageWars.title;
		config.fullscreen = phageWars.mode.fullscreen;
		config.vSyncEnabled = true;
		config.resizable = false;
		config.addIcon("icon_launcher.png", Files.FileType.Internal);
		
		new LwjglApplication(phageWars, config);
	}
}

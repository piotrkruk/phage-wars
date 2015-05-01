package com.github.piotrkruk.phage_wars;


import com.badlogic.gdx.Game;
import com.github.piotrkruk.phage_wars.view.MainMenu;


public class PhageWars extends Game {
	
    public static final String TITLE = "Phage Wars"; 
    public static final int WIDTH = 1200, HEIGHT = 700;
	
	@Override
	public void create () {
		setScreen(new MainMenu());
	}
}

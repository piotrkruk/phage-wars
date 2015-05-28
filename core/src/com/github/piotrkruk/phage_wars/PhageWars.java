package com.github.piotrkruk.phage_wars;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.github.piotrkruk.phage_wars.model.GameStage;
import com.github.piotrkruk.phage_wars.view.*;

import java.awt.*;


public class PhageWars extends Game {    
    public final String title = "Phage Wars";
    
    public DisplayMode mode = DisplayMode.NORMAL;
    public GameMode difficulty = GameMode.MEDIUM;
    
    public Texture texture;
    public Image background;
	
	@Override
	public void create () {
		texture = new Texture(Gdx.files.internal("background.jpg"));
		
		background = new Image(texture);
		background.setSize(mode.width, mode.height);
		
		setScreen(new MainMenu(this));
	}
	
	public void setToGame() {
		GameWindow gameWindow = new GameWindow(this);
		
		gameWindow.startGame();
		setScreen(gameWindow);
	}
	
	public void setToGame(GameStage game) {
		GameWindow gameWindow = new GameWindow(this);
		
		gameWindow.startGame(game);
		setScreen(gameWindow);
	}
	
	public void setToLevels() {
		setScreen(new Levels(this));
	}
	
	public void setToMenu() {
		setScreen(new MainMenu(this));
	}
	
	public void setToSettings() {
		setScreen(new Settings(this));
	}
	
	public void setToMapEditor() {
		setScreen(new MapEditor(this));
	}
	
	public void refreshMode() {
		Gdx.graphics.setDisplayMode(mode.width, mode.height, mode.fullscreen);
		background.setSize(mode.width, mode.height);
	}

	private static Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

	public enum DisplayMode {
		
		NORMAL(960, 540,false),
		HD(1280,720,false),
		FS(dimension.width,dimension.height,true);

		public final int width, height;
		
		public final int blockSize,
						 btnWidth,
						 btnHeight,
						 border;

		public final boolean fullscreen;

		private DisplayMode(int width, int height, boolean fullscreen) {
			this.width = width;
			this.height = height;
			this.fullscreen = fullscreen;

			blockSize = width / 50;
			btnWidth = 12 * blockSize;
			btnHeight = 4 * blockSize;
			border = 2 * blockSize;
		}
	}
	
	public enum GameMode {
		
		EASY(0.6),
		MEDIUM(0.9),
		HARD(1.7);
		
		public final double aiStrength;
		
		private GameMode(double aiStrength) {
			this.aiStrength = aiStrength;
		}
		
	}
}

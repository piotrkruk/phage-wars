package com.github.piotrkruk.phage_wars;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.github.piotrkruk.phage_wars.view.*;


public class PhageWars extends Game {    
    public final String title = "Phage Wars";
    
    public DisplayMode mode = DisplayMode.HD;
    
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
		setScreen(new GameWindow(this));
	}
	
	public void setToMenu() {
		setScreen(new MainMenu(this));
	}
	
	public void setToSettings() {
		setScreen(new Settings(this));
	}
	
	public void refreshMode() {
		Gdx.graphics.setDisplayMode(mode.width, mode.height, false);
		background.setSize(mode.width, mode.height);
	}
	
	public enum DisplayMode {
		
		NORMAL(960, 540),
		HD(1280,720);
		
		public final int width, height;
		
		public final int blockSize,
						 btnWidth,
						 btnHeight,
						 border;
		
		private DisplayMode(int width, int height) {
			this.width = width;
			this.height = height;
			
			blockSize = width / 50;
			btnWidth = 12 * blockSize;
			btnHeight = 4 * blockSize;
			border = 2 * blockSize;
		}
	}
}

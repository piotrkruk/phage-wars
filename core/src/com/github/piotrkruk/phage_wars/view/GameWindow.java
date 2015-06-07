package com.github.piotrkruk.phage_wars.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.github.piotrkruk.phage_wars.Assets;
import com.github.piotrkruk.phage_wars.PhageWars;
import com.github.piotrkruk.phage_wars.model.*;


/**
 * Main window of the game
 * 
 * Controls:
 * 		- left click selects a cell (you must own it)
 * 		- right click on a cell sends units from selected cells to it
 * 			(it can be any cell, in particular one owned by the player)
 * 		- right click on an empty space deselects all selected cells
 *
 */

public class GameWindow extends GameDisplayer {
	private volatile boolean finished = false;
	private volatile int pausedCount = 0;
    
    private Image circExit = new Image(Assets.textureCircExit);
    private Image circPause = new Image(Assets.textureCircPause);
    private Image circResume = new Image(Assets.textureCircResume);
	private Image circSoundOn = new Image(Assets.textureCircSoundOn);
	private Image circSoundOff = new Image(Assets.textureCircSoundOff);

	
    public GameWindow(PhageWars phageWars) {
    	super(phageWars);
    	
    	int block = phageWars.mode.blockSize,
    		circSize = 2 * block;
    	
    	circExit.setSize(circSize, circSize);
    	circPause.setSize(circSize, circSize);
    	circResume.setSize(circSize, circSize);
		circSoundOff.setSize(circSize, circSize);
		circSoundOn.setSize(circSize, circSize);


    	circExit.setPosition(phageWars.mode.width - circSize - block / 4,
				phageWars.mode.height - circSize - block / 4);
    	
    	circPause.setPosition(circExit.getX() - circSize - block / 4,
				phageWars.mode.height - circSize - block / 4);
    	
    	circResume.setPosition(circExit.getX() - circSize - block / 4,
				phageWars.mode.height - circSize - block / 4);
    	
    	circResume.setVisible(false);

		circSoundOn.setPosition(circResume.getX() - circSize - block / 4,
				phageWars.mode.height - circSize - block / 4);

		circSoundOff.setPosition(circResume.getX() - circSize - block / 4,
				phageWars.mode.height - circSize - block / 4);

		if (phageWars.buttonClickVolume > 0)
			circSoundOff.setVisible(false);
		else
			circSoundOn.setVisible(false);

    	Gdx.input.setInputProcessor(this);
    }
    
    /**
     * Creates and starts a game
     * 
     */
    public void startGame() {
    	GameStage temp = new GameStage(phageWars.mode.width,
	   			 phageWars.mode.height,
	   			 phageWars.mode.blockSize,
	   			 phageWars.difficulty.aiStrength);

    	temp.genRandom();
    	
    	startGame(temp);
    }
    
    /**
     * @param game - GameStage that is to be played
     * 
     */
    public void startGame(GameStage game) {
    	this.game = game;
    	
    	initCellImages();
    	game.startGame();
    }

    @Override
    public void render(float delta) {
		synchronized(game) {
			if (pausedCount == 0)
				game.update(delta);
			
			checkGameStatus();
			
			super.render(delta);
		}
    }
    
    private void checkGameStatus() {
    	if (!finished && !game.isRunning()) {
    		finished = true;
    		
    		System.out.println("The game has finished.");
    		
    		new Dialog("Infection complete.", Assets.defaultSkin) {
    			{
    	    		if (game.HUMAN_PLAYER) {
    		    		boolean hasPlayerWon = game.player.isPlaying();
    		    		
    		    		if (hasPlayerWon) {
    		    			System.out.println("The player has won.");
    		    			text("You have won!\nClick anywhere to continue.");    		
    		    		}
    		    		else {
    		    			System.out.println("The player has lost.");
    		    			text("You have lost!\nClick anywhere to continue.");   
    		    		}
    	    		}
    	    		else
    	    			text("Click anywhere to continue.");
    			}
    		}.show(stage);   		
    	}
    }

    @Override
    public void show() {
		stage.addActor(circExit);
		stage.addActor(circPause);
		stage.addActor(circResume);
		stage.addActor(circSoundOn);
		stage.addActor(circSoundOff);
    }

    @Override
    public void pause() { 
    	pausedCount++;
    	
    	if (pausedCount == 1) {
    		game.paused = true;
	    	System.out.println("Game paused.");
	    	
	    	circPause.setVisible(false);
	    	circResume.setVisible(true);
    	}
    }

    @Override
    public void resume() {  
    	pausedCount--;
    	
    	if (pausedCount == 0) {
    		game.paused = false;
	    	System.out.println("Game resumed.");
	    	
	    	circPause.setVisible(true);
	    	circResume.setVisible(false);
    	}
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    	if (finished) {
    		phageWars.setToMenu();
    		return false;
    	}
    	
        int posX = screenX,
            posY = Gdx.graphics.getHeight() - screenY;
        		/* 
        		 * screenY is reversed in respect to the coordinates
        		 * of drawing
        		 */

		if (posX >= circSoundOn.getX() && posX <= circSoundOn.getX() + circSoundOn.getWidth() &&
				posY >= circSoundOn.getY() && posY <= circSoundOn.getY() + circSoundOn.getHeight()) {
			if(phageWars.buttonClickVolume > 0.0f) {
				System.out.println("Sounds turned off.");
				
				phageWars.buttonClickVolume = 0.0f;
				circSoundOff.setVisible(true);
				circSoundOn.setVisible(false);
			}
			else {
				System.out.println("Sounds turned on.");
				
				phageWars.buttonClickVolume = Assets.DEFAULT_CLICK_VOLUME;
				
				phageWars.playSound();
				circSoundOn.setVisible(true);
				circSoundOff.setVisible(false);
			}
			return false;
		}

    	if (posX >= circExit.getX() && posX <= circExit.getX() + circExit.getWidth() &&
    		posY >= circExit.getY() && posY <= circExit.getY() + circExit.getHeight()) {
    			System.out.println("Game exited.");
    			
				phageWars.playSound();
    			phageWars.setToMenu();
    			return false;
    	}
    	
    	if (posX >= circPause.getX() && posX <= circPause.getX() + circPause.getWidth() &&
    		posY >= circPause.getY() && posY <= circPause.getY() + circPause.getHeight()) {
    		
    		if (pausedCount == 0) {
    			phageWars.playSound();
				this.pause();
			}
    		else {
    			phageWars.playSound();
				this.resume();
			}
    		
    		return false;
    	}
    	
    	if (pausedCount > 0 || !game.HUMAN_PLAYER)
    		return false;

		Cell clicked = null;
		
		synchronized (game) {
			for (Cell c: game.cells) {
				if (c.isInside(posX, posY))
					clicked = c;
			}
	    	
	    	if (button == Buttons.LEFT) {
	    		System.out.println("Left mouse click at " + posX + " " + posY);
	    			
	    		if (clicked != null && clicked.owner == game.player)
	    			clicked.select();
	    	}
	    	else if (button == Buttons.RIGHT) {
	    		System.out.println("Right mouse click at " + posX + " " + posY);
	    		
	    		if (clicked == null)
	    			game.deselectAll(game.player);
	    		else
	    			game.send(clicked, game.player);
	    	}
	    	
	    	return false;
		}
    }
}

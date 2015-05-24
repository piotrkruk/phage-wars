package com.github.piotrkruk.phage_wars.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
    
    private Image circExit = new Image( new Texture(Gdx.files.internal("buttons/game_exit.png")) );
    private Image circPause = new Image( new Texture(Gdx.files.internal("buttons/game_pause.png")) );
    private Image circResume = new Image( new Texture(Gdx.files.internal("buttons/game_resume.png")) );
    
    private Texture[] textureBacterias =
    	{
    		new Texture(Gdx.files.internal("bacterias/bacteria_blue.png")),
    		new Texture(Gdx.files.internal("bacterias/bacteria_red.png")),
    		new Texture(Gdx.files.internal("bacterias/bacteria_purple.png"))
    	};
    
    public GameWindow(PhageWars phageWars) {
    	super(phageWars);
    	
    	game = new GameStage(phageWars.mode.width,
				   			 phageWars.mode.height,
				   			 phageWars.mode.blockSize,
				   			 phageWars.difficulty.aiStrength);
    	
    	game.genRandom();
    	
    	initCellImages();
    	
    	int block = phageWars.mode.blockSize,
    		circSize = 2 * block;
    	
    	circExit.setSize(circSize, circSize);
    	circPause.setSize(circSize, circSize);
    	circResume.setSize(circSize, circSize);
    	
    	circExit.setPosition(phageWars.mode.width - circSize - block / 4,
    						 phageWars.mode.height - circSize - block / 4);
    	
    	circPause.setPosition(circExit.getX() - circSize - block / 4,
				 			  phageWars.mode.height - circSize - block / 4);
    	
    	circResume.setPosition(circExit.getX() - circSize - block / 4,
	 			  			   phageWars.mode.height - circSize - block / 4);
    	
    	circResume.setVisible(false);
    	
    	game.startGame();
    	Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {    	
	    stage.act();
	    stage.draw();
	        
	    if (!finished) {
	    	synchronized (game) {
	    		if (pausedCount == 0)
	    			game.update(delta);
	    		
	    		checkGameStatus();
	    		
	    		drawBacterias();
	    		drawSelections();
	    		drawCells();
	    		drawAmountsOfUnits();
	    	}
	    }
    }
    
    private void checkGameStatus() {
    	if (!game.isRunning()) {
    		finished = true;
    		
    		System.out.println("The game has finished.");
    		
    		Dialog msgGameFinished = new Dialog("Infection complete.", defaultSkin);
    		
    		if (game.HUMAN_PLAYER) {
	    		boolean hasPlayerWon = game.player.isPlaying();
	    		
	    		if (hasPlayerWon) {
	    			System.out.println("The player has won.");
	    			msgGameFinished.text("You have won!\nClick anywhere to continue.");    		
	    		}
	    		else {
	    			System.out.println("The player has lost.");
	    			msgGameFinished.text("You have lost!\nClick anywhere to continue.");   
	    		}
    		}
    		else
    			msgGameFinished.text("Click anywhere to continue.");
    		
    		stage.addActor(msgGameFinished);
    		msgGameFinished.show(stage);
    	}
    }
    
    private void drawBacterias() {
    	batch.begin();
    	
    	for (Bacteria b : game.bacterias) {
    		Image img = new Image( textureBacterias[b.from.id] );
    		
    		int diam = b.radius * 2;
    		
    		img.setSize(diam, diam);
    		img.setPosition(b.posX - b.radius, b.posY - b.radius);
    		img.draw(batch, 1);
    	}
    	
    	batch.end();
    }
    
    private void drawSelections() {
    	if (!game.HUMAN_PLAYER)
    		return;
    	
    	shapeRenderer.begin(ShapeType.Line);

        for (Cell c : game.cells)
        	if (c.owner == game.player && c.selected) {        		
        		int posX = Gdx.input.getX(),
        			posY = Gdx.graphics.getHeight() - Gdx.input.getY();
        		
        		Gdx.gl20.glLineWidth(6);
        		
        		shapeRenderer.setColor(Color.WHITE);
        		shapeRenderer.line(c.posX, c.posY, posX, posY);
        	}
        
        shapeRenderer.end();
    
        shapeRenderer.begin(ShapeType.Filled);
        
        for (Cell c : game.cells) {
        	if (c.owner == game.player && c.selected) {
        		shapeRenderer.setColor(Color.YELLOW);
        		shapeRenderer.circle(c.posX, c.posY, c.radius + 5);
        	}
        }
        
        shapeRenderer.end();
    }

    @Override
    public void show() {
    	stage.addActor(phageWars.background);
    	
    	stage.addActor(circExit);
    	stage.addActor(circPause);
    	stage.addActor(circResume);
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
        		/* screenY is reversed in respect to the coordinates
        		 * of drawing
        		 */
    	
    	if (posX >= circExit.getX() && posX <= circExit.getX() + circExit.getWidth() &&
    		posY >= circExit.getY() && posY <= circExit.getY() + circExit.getHeight()) {
    			System.out.println("Game exited.");
    		
    			phageWars.setToMenu();
    			return false;
    	}
    	
    	if (posX >= circPause.getX() && posX <= circPause.getX() + circPause.getWidth() &&
    		posY >= circPause.getY() && posY <= circPause.getY() + circPause.getHeight()) {
    		
    		if (pausedCount == 0)
    			this.pause();
    		else
    			this.resume();
    		
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

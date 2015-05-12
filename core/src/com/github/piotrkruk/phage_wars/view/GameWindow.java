package com.github.piotrkruk.phage_wars.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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

public class GameWindow implements Screen, InputProcessor {
	private final PhageWars phageWars;
	private volatile boolean paused = false;
    
    private Stage stage = new Stage();
    private BitmapFont font = new BitmapFont(Gdx.files.internal("default.fnt"));
    private SpriteBatch batch = new SpriteBatch();
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private Skin defaultSkin = new Skin(Gdx.files.internal("uiskin.json"));
    
    private GameStage game = new GameStage(Gdx.graphics.getWidth(),
    									   Gdx.graphics.getHeight());
    
    public GameWindow(PhageWars phageWars) {
    	this.phageWars = phageWars;
    	
    	game.genRandom();
    	game.startGame();
    	
    	Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {    	
	    stage.act();
	    stage.draw();
	        
	    if (!paused) {
	    	synchronized (game) {
	    		checkGameStatus();
	    		drawCells(delta);
	    	}
	    }
    }
    
    private void checkGameStatus() {
    	if (!game.isRunning()) {
    		paused = true;
    		
    		System.out.println("The game has finished.");
    		
    		Dialog msgGameFinished = new Dialog("Infection complete.", defaultSkin);
    		
    		if (GameStage.HUMAN_PLAYER) {
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

    private void drawCells(float delta) {
        game.update(delta);
        
        shapeRenderer.begin(ShapeType.Filled);
        
        for (Bacteria b : game.bacterias) {
        	shapeRenderer.setColor(b.from.color);        	
        	shapeRenderer.circle(b.posX, b.posY, b.radius);
        }
        
        shapeRenderer.end();

        if (GameStage.HUMAN_PLAYER) {
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
                
        shapeRenderer.begin(ShapeType.Filled);
        
        for (Cell c : game.cells) {
	    	if (c.owner == null)
	    		shapeRenderer.setColor(Color.GRAY);
	    	else
	    		shapeRenderer.setColor(c.owner.color);
	    	
	    	shapeRenderer.circle(c.posX, c.posY, c.radius);
        }
        
        shapeRenderer.end();
	    	
        batch.begin();
        
        for (Cell c : game.cells) {
        	String temp = String.valueOf(c.getUnits());
        	
        	float approxHeight = font.getCapHeight(),
        		  approxWidth = 2.4f * font.getSpaceWidth() * temp.length();
        	
        	font.draw(batch, temp,
        			c.posX - approxWidth / 2, c.posY + approxHeight / 2);
        }
        	
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    	stage.addActor(phageWars.background);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {
    	paused = true;
    }

    @Override
    public void resume() {
    	paused = false;
    }

    @Override
    public void dispose() {
    	stage.dispose();
    }
   
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    	if (paused) {
    		phageWars.setToMenu();
    		return false;
    	}
    	
    	if (!GameStage.HUMAN_PLAYER)
    		return false;
    	
        int posX = screenX,
            posY = Gdx.graphics.getHeight() - screenY;
        		/* screenY is reversed in respect to the coordinates
        		 * of drawing
        		 */
    	
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
   

    @Override
    public boolean keyDown(int keycode) {
    	return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		return false;
	}
	
	@Override
	public boolean keyTyped(char character) {
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}
	
	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}

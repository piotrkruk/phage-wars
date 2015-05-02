package com.github.piotrkruk.phage_wars.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
    private Texture texture = new Texture(Gdx.files.internal("background.jpg"));
    private Image background = new Image(texture);
    
    private Stage stage = new Stage();
    private BitmapFont font = new BitmapFont();
    private SpriteBatch batch = new SpriteBatch();
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    
    private GameStage game = new GameStage();
    
    public GameWindow() {
    	game.genRandom();
    	Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {        
        stage.act();
        stage.draw();
        
        drawCells(delta);
    }
    

    private void drawCells(float delta) {
        game.update(delta);
        
        shapeRenderer.begin(ShapeType.Line);
        
        for (Cell c : game.cells)
        	if (c.selected) {        		
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
        	
        	if (c.owner == null)
        		shapeRenderer.setColor(Color.GRAY);
        	else if (c.owner == game.player)
        		shapeRenderer.setColor(Color.BLUE);
        	else
        		shapeRenderer.setColor(Color.RED);
        	
        	shapeRenderer.circle(c.posX, c.posY, c.radius);
        }
        	
        shapeRenderer.end();
        
        batch.begin();
        
        for (Cell c : game.cells)
        	font.draw(batch, String.valueOf(c.getUnits()), c.posX, c.posY + font.getCapHeight() / 2);
        	
        batch.end();    	
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    	stage.addActor(background);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    	stage.dispose();
    }
   
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    	
        int posX = screenX,
            posY = Gdx.graphics.getHeight() - screenY;
    	
		Cell clicked = null;
		
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

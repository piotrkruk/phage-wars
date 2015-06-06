package com.github.piotrkruk.phage_wars.view;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.github.piotrkruk.phage_wars.Assets;
import com.github.piotrkruk.phage_wars.PhageWars;
import com.github.piotrkruk.phage_wars.model.Bacteria;
import com.github.piotrkruk.phage_wars.model.Cell;
import com.github.piotrkruk.phage_wars.model.GameStage;
import com.github.piotrkruk.phage_wars.model.Player;


/**
 * Class collecting code responsible
 * for displaying the base game
 * common for GameWindow and MapEditor
 *
 */

public class GameDisplayer implements Screen, InputProcessor {
	protected final PhageWars phageWars;
	
    protected Stage stage = new Stage();
    protected SpriteBatch batch = new SpriteBatch();
    protected ShapeRenderer shapeRenderer = new ShapeRenderer();
    
    protected GameStage game;
    
    protected List <Image> imgCells = new LinkedList <Image> ();
    protected List <Integer> imgCellOwners = new LinkedList <Integer> ();
    
    
    public GameDisplayer(PhageWars phageWars) {
    	this.phageWars = phageWars;
    	InputMultiplexer processor = new InputMultiplexer();
    	
    	processor.addProcessor(stage);
    	processor.addProcessor(this);
    	
    	Gdx.input.setInputProcessor(processor);
    }

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		batch.begin();
		phageWars.background.draw(batch, 1);
		batch.end();
		
		synchronized (game) {
			drawBacterias();
			drawSelections();
			drawCells();
			drawAmountsOfUnits();
		}
		
		stage.act();
		stage.draw();
	}
	
	protected void initCellImages() {
    	for (int i = 0; i < game.cells.size(); i++) {
			Player pl = game.cells.get(i).owner;
			
			if (pl != null)
				imgCellOwners.add(pl.imageId);
			else
				imgCellOwners.add(Assets.texturePlayers.length-1);
			
			imgCells.add( new Image( Assets.texturePlayers[ imgCellOwners.get(i) ] ) );
			
			int diam = game.cells.get(i).radius * 2;
			
			imgCells.get(i).setSize(diam, diam);
    	}
	}
	
    protected void drawCells() {    	
        batch.begin();
 
        for (int i = 0; i < game.cells.size(); i++) {
        	Cell c = game.cells.get(i);
        	Image img = imgCells.get(i);
        	
        	int oldOwn = imgCellOwners.get(i),
        		newOwn;
        	
        	if (c.owner != null)
        		newOwn = c.owner.imageId;
        	else
        		newOwn = Assets.texturePlayers.length-1;
        	
        	if (oldOwn != newOwn) {
        		imgCellOwners.set(i, newOwn);
        		imgCells.set(i, new Image( Assets.texturePlayers[newOwn] ));
        		
        		int diam = c.radius * 2;
        		imgCells.get(i).setSize(diam, diam);
        		
        		img = imgCells.get(i);
        	}
        	
        	img.setPosition(c.posX - c.radius, c.posY - c.radius);
        	img.draw(batch, 1);
        }
        
        batch.end();
    }
    
    protected void drawBacterias() {
    	batch.begin();
    	
    	for (Bacteria b : game.bacterias) {
    		Image img = new Image( Assets.textureBacterias[b.from.imageId] );
    		
    		int diam = b.radius * 2;
    		
    		img.setSize(diam, diam);
    		img.setPosition(b.posX - b.radius, b.posY - b.radius);
    		img.draw(batch, 1);
    	}
    	
    	batch.end();
    }
    
    protected void drawSelections() {
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
    
    protected void drawAmountsOfUnits() {
        batch.begin();
        
        for (Cell c : game.cells) {
        	String temp = String.valueOf(c.getUnits());
        	
        	float approxHeight = Assets.font.getCapHeight(),
        		  approxWidth = 2.4f * Assets.font.getSpaceWidth() * temp.length();
        	
        	Assets.font.draw(batch, temp,
        			c.posX - approxWidth / 2, c.posY + approxHeight / 2);
        }
        	
        batch.end();
    }

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		stage.dispose();
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
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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

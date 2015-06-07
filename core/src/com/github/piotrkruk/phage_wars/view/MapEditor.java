package com.github.piotrkruk.phage_wars.view;

import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.piotrkruk.phage_wars.Assets;
import com.github.piotrkruk.phage_wars.PhageWars;
import com.github.piotrkruk.phage_wars.model.Cell;
import com.github.piotrkruk.phage_wars.model.GameStage;
import com.github.piotrkruk.phage_wars.model.Grid;
import com.github.piotrkruk.phage_wars.model.Map;
import com.github.piotrkruk.phage_wars.model.Player;
import com.github.piotrkruk.phage_wars.model.Race;

/**
 * Class for editing the maps
 * 
 * Controls:
 * 		- first left click sets a position for a center of a cell
 * 		- second left click sets the size of the cell
 * 		- right click deletes a cell - the one that was being created
 * 			(if done while creating) or the one that was clicked on (otherwise)
 * 
 */


public class MapEditor extends GameDisplayer {
    
    private TextButton btnBack = new TextButton("Back to menu", Assets.defaultSkin);
    private TextButton btnSave = new TextButton("Save", Assets.defaultSkin);
    private TextButton btnLoad = new TextButton("Load", Assets.defaultSkin);
    private TextButton btnPlay = new TextButton("Play", Assets.defaultSkin);
    
    private Image circHideButtons = new Image(Assets.textureCircHideButtons);
    
    private int centerX, centerY;
    
	/*
	 * When creating is true every render changes the size
	 * of the last cell in game.cells
	 */
    private volatile boolean creating = false;
    
    /*
     * When remapping is true the application is loading
     * a new map
     */
    private volatile boolean remapping = false;
    
	
	public MapEditor(PhageWars phageWars) {
		super(phageWars);
		
		game = new GameStage(phageWars.mode.width,
							 phageWars.mode.height,
							 phageWars.mode.blockSize, 1.0, GameStage.MAX_NO_OF_PLAYERS);
		
    	int btnWidth = phageWars.mode.btnWidth,
        	btnHeight = phageWars.mode.btnHeight,
        	border = phageWars.mode.border,
        	block = phageWars.mode.blockSize,
        	circSize = 2 * block,
        	left = phageWars.mode.width - border - btnWidth;
    	
		circHideButtons.setSize(circSize, circSize);

		circHideButtons.setPosition(phageWars.mode.width - circSize - block / 4,
				phageWars.mode.height - circSize - block / 4);
        	
        btnBack.setBounds(left, border, btnWidth, btnHeight);
        btnSave.setBounds(left, btnBack.getY() + block + btnHeight, btnWidth, btnHeight);
        btnLoad.setBounds(left, btnSave.getY() + block + btnHeight, btnWidth, btnHeight);
        btnPlay.setBounds(left, btnLoad.getY() + block + btnHeight, btnWidth, btnHeight);

        btnBack.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
				MapEditor.this.phageWars.playSound();
				MapEditor.this.phageWars.setToLevels();
            }
        } );
        
        btnSave.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            	MapEditor.this.phageWars.playSound();
            	MapEditor.this.getMapNameAndExecute(MapEditor.this::saveMap);
            }
        } );
        
        btnLoad.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            	MapEditor.this.phageWars.playSound();
            	MapEditor.this.getMapNameAndExecute(MapEditor.this::loadMap);
            }
        });
        
        btnPlay.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            	MapEditor.this.phageWars.playSound();
				MapEditor.this.phageWars.setToGame(game);
            }
        } );
	
        circHideButtons.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            	boolean temp = !MapEditor.this.btnBack.isVisible();
            	
            	MapEditor.this.btnBack.setVisible(temp);
            	MapEditor.this.btnSave.setVisible(temp);
            	MapEditor.this.btnLoad.setVisible(temp);
            	MapEditor.this.btnPlay.setVisible(temp);
            }
        } );
	}

	@Override
	public void show() {    	
    	stage.addActor(btnBack);
    	stage.addActor(btnSave);
    	stage.addActor(btnLoad);
    	stage.addActor(btnPlay);
    	
    	stage.addActor(circHideButtons);
	}

	@Override
	public void render(float delta) {
		if (remapping)
			return ;
		
		synchronized (game) {
		    if (creating) {
	    		int posX = Gdx.input.getX(),
	        		posY = Gdx.graphics.getHeight() - Gdx.input.getY();
		    	
	    		int radius = (int) Math.sqrt( Grid.distSquared(centerX, centerY, posX, posY) ),
	    			diam = radius * 2,
	    			ind = game.cells.size() - 1;
	    		
	    		game.cells.get(ind).radius = radius;
	    		imgCells.get(ind).setSize(diam, diam);
		    }
			
	        super.render(delta);
		}
	}
	
	/**
	 * Deletes everything related to some cell
	 * @param ind - index in game.cells
	 * 
	 */
	private void deleteCell(int ind) {
		synchronized (game) {
			Cell c = game.cells.get(ind);
			
			if (c.owner != null)
				c.owner.ownCount--;
			
			game.cells.remove(ind);
			imgCells.remove(ind);
			imgCellOwners.remove(ind);
		}
	}
	
	/**
	 * Adds a cell and all related objects
	 * to the game
	 * 
	 */
	private void addCell(int imageId, int posX, int posY, int radius, int initUnits, Race race, Player pl) {
		synchronized (game) {
			int ind = game.cells.size();
			
			imgCellOwners.add(imageId);
			imgCells.add( new Image( Assets.texturePlayers[ imgCellOwners.get(ind) ] ) );
			
			game.cells.add(
					new Cell(centerX, centerY, radius, initUnits, race, pl)
				);
			
			if (pl != null)
				pl.ownCount++;
		}
	}
	
	/**
	 * Discards the cell being created
	 * 
	 */
	private void discard() {
		synchronized (game) {
			creating = false;
			deleteCell( game.cells.size() - 1 );
		}
	}
	
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {   
    	
    	synchronized (game) {
	        int posX = screenX,
	            posY = Gdx.graphics.getHeight() - screenY;
	        		/* screenY is reversed in respect to the coordinates
	        		 * of drawing
	        		 */
		    	
	    	if (button == Buttons.LEFT) {
	    		System.out.println("Left mouse click at " + posX + " " + posY);
	    			
	    		if (!creating) {
	    			
	    			centerX = posX;
	    			centerY = posY;
	    			
	    			getCellParamsAndCreate();
	    		}
	    		else {
	    			creating = false;
	    			Cell created = game.cells.get( game.cells.size() - 1 );
	    			
	    			if (!Map.isValid(created, game) || Map.getDummyCell(game).doesCollide(created))
	    				discard();
	    		}
	    	}
	    	else if (button == Buttons.RIGHT) {
	    		System.out.println("Right mouse click at " + posX + " " + posY);
	    		
	    		if (creating)
	    			discard();
	    		else {
	    			for (int i = 0; i < game.cells.size(); i++) {
	    				if (game.cells.get(i).isInside(posX, posY)) {
	    					deleteCell(i);
	    					break;
	    				}
	    			}
	    		}
	    	}
    	}

    	return false;
    }
    
    /**
     * Asks for new cell's parameters
     * 
     */
    private void getCellParamsAndCreate() {
		class CellDialog extends Dialog {
			
			TextField field = new TextField("", Assets.defaultSkin);
			
			CellDialog() {
				super("Enter cell's parameters", Assets.defaultSkin);

				getContentTable()
					.add("Write [owner id] [amount of units]\nFor unoccupied cells leave empty");
				getContentTable().row();
				
    			getContentTable().add(field);
    			
    			button("Ok", field);
    			button("Cancel");
    			
    			key(Input.Keys.ENTER, field);
			}
					
			@Override
			public void result(Object obj) {
				if (obj != null)
					MapEditor.this.createCell( TextField.class.cast(obj).getText() );
			}
			
		};
		
		CellDialog dialog = new CellDialog();
		
		dialog.show(stage);
		stage.setKeyboardFocus(dialog.field);
    }
    
    /**
     * Loads a name for a map and passes it to a function
     * @param func - function to be executed
     * 
     */
    private void getMapNameAndExecute(final Consumer<String> func) {

    	class MapDialog extends Dialog {
    		
    		TextField field = new TextField("", Assets.defaultSkin);
    		
    		MapDialog() {
    			super("Enter map's name", Assets.defaultSkin);
    			
    			getContentTable()
					.add("You don't have to provide any extension");
				getContentTable().row();
				
				getContentTable().add(field);
				
				button("Ok", field);
				button("Cancel");
    		
				key(Input.Keys.ENTER, field);
    		}
    		
			@Override
			public void result(Object obj) {
				if (obj != null)
					func.accept( TextField.class.cast(obj).getText() );
			}
		};
		
		MapDialog dialog = new MapDialog();
		
		dialog.show(stage);
		stage.setKeyboardFocus(dialog.field);
    }
    
    /**
     * Tries to load a map
     * @param text - map's name
     * 
     */
    private void loadMap(String text) {
		GameStage temp = Map.read(game.width, game.height,
								  game.blockSize, game.AI_STRENGTH, text + ".ser", false);

		if (temp != null) {
			remapping = true;
			
			game = temp;
			
			imgCells.clear();
			imgCellOwners.clear();
			
			initCellImages();
			remapping = false;
		}
    }
    
    /**
     * Tries to save a map
     * @param text - map's name
     * 
     */
    private void saveMap(String text) {
    	Map.write(game, text + ".ser");
    }

    /**
     * Tries to create a new cell
     * @param text - user's text input
     * 
     */
    private void createCell(String text) {
		int id, initUnits;
		Race race = null;
		Player pl = null;
		
		if (!text.equals("")) {							
			try {
				String[] temp = text.split(" ");
				
				id = Integer.parseInt(temp[0]);
				initUnits = Integer.parseInt(temp[1]);
				
				race = game.races.get(id);
				pl = game.players.get(id);
			} catch (RuntimeException e) {
				System.out.println("Illegal arguments!");
				return ;
			}
		}
		else {
			id = Assets.texturePlayers.length - 1;
			initUnits = 0;
			
			race = new Race(game);
			pl = null;
		}
		
		addCell(id, centerX, centerY, 0, initUnits, race, pl);
		creating = true;
    }
}

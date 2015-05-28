package com.github.piotrkruk.phage_wars.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
    
    private TextButton btnBack = new TextButton("Back to menu", defaultSkin);
    private TextButton btnSave = new TextButton("Save", defaultSkin);
    private TextButton btnLoad = new TextButton("Load", defaultSkin);
    private TextButton btnPlay = new TextButton("Play", defaultSkin);
    
    private int centerX, centerY;

	private Sound buttonclick = Gdx.audio.newSound(Gdx.files.internal("sounds/click1.wav"));
	private float buttonclickvolume = 0.7f;
    
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
        	left = phageWars.mode.width - border - btnWidth;
        	
        btnBack.setBounds(left, border, btnWidth, btnHeight);
        btnSave.setBounds(left, border + block + btnHeight, btnWidth, btnHeight);
        btnLoad.setBounds(left, border + 2 * (block + btnHeight), btnWidth, btnHeight);
        btnPlay.setBounds(left, border + 3 * (block + btnHeight), btnWidth, btnHeight);

        btnBack.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
				buttonclick.play(buttonclickvolume);
				MapEditor.this.phageWars.setToLevels();
            }
        } );
        
        btnSave.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
				buttonclick.play(buttonclickvolume);
                Input.TextInputListener listener = new Input.TextInputListener() {

					@Override
					public void input(String text) {
						Map.write(game, text);
					}

					@Override
					public void canceled() {}
                	
                };
                Gdx.input.getTextInput(listener, "Write the name of the map", "", "eg. mymap.ser");
            }
        } );
        
        btnLoad.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
				buttonclick.play(buttonclickvolume);
                Input.TextInputListener listener = new Input.TextInputListener() {

					@Override
					public void input(String text) {						
						GameStage temp = Map.read(game.WIDTH, game.HEIGHT,
								game.BLOCK_SIZE, game.AI_STRENGTH, text);
						
						if (temp != null) {
							remapping = true;
							
							game = temp;
						
							imgCells.clear();
							imgCellOwners.clear();
							
							initCellImages();
							remapping = false;
						}
					}

					@Override
					public void canceled() {}
                	
                };
                Gdx.input.getTextInput(listener, "Write the name of the map", "", "eg. mymap.ser");
            }
        });
        
        btnPlay.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
				buttonclick.play(buttonclickvolume);
				MapEditor.this.phageWars.setToGame(game);
            }
        } );
	}

	@Override
	public void show() {
		stage.addActor(phageWars.background);
    	
    	stage.addActor(btnBack);
    	stage.addActor(btnSave);
    	stage.addActor(btnLoad);
    	stage.addActor(btnPlay);
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
			imgCells.add( new Image( texturePlayers[ imgCellOwners.get(ind) ] ) );
			
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
	    			
	                Input.TextInputListener listener = new Input.TextInputListener() {
	
						@Override
						public void input(String text) {							
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
								id = texturePlayers.length - 1;
								initUnits = 0;
								
								race = new Race(game);
								pl = null;
							}
							
							MapEditor.this.addCell(id, centerX, centerY, 0, initUnits, race, pl);
			        		creating = true;
						}
	
						@Override
						public void canceled() {}
	                	
	                };
	                Gdx.input.getTextInput(listener, "[owner id] [number of units]", 
	                		"", "for unoccupied cells leave this empty");
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
}

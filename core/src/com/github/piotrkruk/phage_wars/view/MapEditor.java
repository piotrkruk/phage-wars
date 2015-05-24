package com.github.piotrkruk.phage_wars.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
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
 */


public class MapEditor extends GameDisplayer {
    
    private TextButton btnBack = new TextButton("Back to menu", defaultSkin);
    private TextButton btnSave = new TextButton(String.valueOf("Save"), defaultSkin);
    private TextButton btnLoad = new TextButton(String.valueOf("Load"), defaultSkin);
    
    private int centerX, centerY;
    private boolean creating = false;
    private volatile boolean remapping = false;
	
	public MapEditor(PhageWars phageWars) {
		super(phageWars);
		
		game = new GameStage(phageWars.mode.width,
							 phageWars.mode.height,
							 phageWars.mode.blockSize,
							 1.0);
		
    	int btnWidth = phageWars.mode.btnWidth,
        	btnHeight = phageWars.mode.btnHeight,
        	border = phageWars.mode.border,
        	block = phageWars.mode.blockSize,
        	left = phageWars.mode.width - border - btnWidth;
        	
        btnBack.setBounds(left, border, btnWidth, btnHeight);
        btnSave.setBounds(left, border + block + btnHeight, btnWidth, btnHeight);
        btnLoad.setBounds(left, border + 2 * (block + btnHeight), btnWidth, btnHeight);

        btnBack.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                MapEditor.this.phageWars.setToLevels();
            }
        } );
        
        btnSave.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
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
	}

	@Override
	public void show() {
		stage.addActor(phageWars.background);
    	
    	stage.addActor(btnBack);
    	stage.addActor(btnSave);
    	stage.addActor(btnLoad);
	}

	@Override
	public void render(float delta) {
		if (remapping)
			return ;
		
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
	
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {   
    	
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
						
						int ind = imgCells.size();
						
						int id, initUnits;
						Race race = null;
						Player pl = null;
						
						if (!text.equals("")) {
							String[] temp = text.split(" ");
							
							id = Integer.parseInt(temp[0]);
							initUnits = Integer.parseInt(temp[1]);
							
							race = game.races.get(id);
							pl = game.players.get(id);
						}
						else {
							id = texturePlayers.length - 1;
							initUnits = 0;
							
							race = new Race(game.BLOCK_SIZE);
							pl = null;
						}
						
						imgCellOwners.add(id);
		        		imgCells.add( new Image( texturePlayers[ imgCellOwners.get(ind) ] ) );
		        		
		        		game.cells.add(
		        				new Cell(centerX, centerY, 0, initUnits, race, pl)
		        			);
		        		
		        		if (pl != null)
		        			pl.ownCount++;
		        		
		        		creating = true;
					}

					@Override
					public void canceled() {}
                	
                };
                Gdx.input.getTextInput(listener, "Write [owner id] [number of units]", 
                		"", "for unoccupied cells leave this empty");
    		}
    		else
    			creating = false;
    	}
    	else if (button == Buttons.RIGHT) {
    		System.out.println("Right mouse click at " + posX + " " + posY);
    		
    		creating = false;
    	}
    	
    	return false;
    }
}

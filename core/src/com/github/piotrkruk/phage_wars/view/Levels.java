package com.github.piotrkruk.phage_wars.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.piotrkruk.phage_wars.Assets;
import com.github.piotrkruk.phage_wars.PhageWars;
import com.github.piotrkruk.phage_wars.model.GameStage;
import com.github.piotrkruk.phage_wars.model.Map;

/**
 * Class allowing the player
 * to select some level for playing
 * 
 */

public class Levels implements Screen {
	private final PhageWars phageWars;
    
    private Stage stage = new Stage();
    
    private TextButton btnBack = new TextButton("Back to menu", Assets.defaultSkin);
    private TextButton btnEditor = new TextButton("Open editor", Assets.defaultSkin);
    
    private Button[] btnLevels;
    
    public Levels(PhageWars phageWars) {
    	this.phageWars = phageWars;
        
        initButtons();
        initLevelButtons();
        Gdx.input.setInputProcessor(stage);
    }
    
    private void initButtons() {
    	int btnWidth = phageWars.mode.btnWidth,
        	btnHeight = phageWars.mode.btnHeight,
        	border = phageWars.mode.border,
        	left = phageWars.mode.width - border - btnWidth;
        	
    	btnBack.setBounds(left, border, btnWidth, btnHeight);
    	btnEditor.setBounds(border, border, btnWidth, btnHeight);
    	
        btnBack.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            	Levels.this.phageWars.playClickSound();
                Levels.this.phageWars.setToMenu();
            }
        });
        
        btnEditor.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Levels.this.phageWars.playClickSound();
                Levels.this.phageWars.setToMapEditor();
            }
        });
    }
    
    private void initLevelButtons() {
    	int btnWidth = phageWars.mode.btnWidth,
        	btnHeight = phageWars.mode.btnHeight,
        	border = phageWars.mode.border;
    	
    	int buttonsPerRow = 4;
    	
    	btnLevels = new Button[ Assets.strLevels.length ];
    	
    	for(int i = 0; i < btnLevels.length; i++) {
    		btnLevels[i] = new Button(Assets.defaultSkin);
    		
    		String strImgPath = "map_images/" + Assets.strLevels[i] + ".png",
    			   strMapPath = "maps/" + Assets.strLevels[i] + ".ser";
    		
    	    Button.ButtonStyle style = new Button.ButtonStyle();
    		
    		style.up = style.down =
    			new Image( new Texture(Gdx.files.internal(strImgPath)) ).getDrawable();
    		
    		btnLevels[i].setStyle(style);
    		
    		if (i == 0)
    			btnLevels[i].setPosition(1.58f * border, phageWars.mode.height - 2 * border - btnHeight);
    		else {
    			Button predecessor;
    			
    			if (i%buttonsPerRow != 0)
    				predecessor = btnLevels[i-1];
    			else
    				predecessor = btnLevels[i-buttonsPerRow];
    			
    			if (i%buttonsPerRow != 0) {
        			btnLevels[i].setX(predecessor.getRight() + 3 * border);
        			btnLevels[i].setY(predecessor.getY());
        		}
        		else {
        			btnLevels[i].setX(predecessor.getX() + 3.18f * border);
        			btnLevels[i].setY(predecessor.getY() - predecessor.getHeight() - border);
        		}
    		}
    	
    		btnLevels[i].setSize(btnWidth * phageWars.mode.height / phageWars.mode.width, btnHeight);
    		
    		btnLevels[i].addListener( new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    Levels.this.phageWars.playClickSound();
                    
                    GameStage game =
                    	Map.read(phageWars.mode.width, phageWars.mode.height,
                    			 phageWars.mode.blockSize, phageWars.difficulty.aiStrength, strMapPath, true);
                    
                    if (game != null)
                    	Levels.this.phageWars.setToGame(game);
                }
            });    		
    	}
    }
    
    @Override
    public void render(float delta) {        
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    	stage.addActor(phageWars.background);
    	
    	stage.addActor(btnBack);
    	stage.addActor(btnEditor);
    	
    	for(int i = 0; i < btnLevels.length; i++)
    		stage.addActor(btnLevels[i]);
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
}
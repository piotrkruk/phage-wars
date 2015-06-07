package com.github.piotrkruk.phage_wars.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.piotrkruk.phage_wars.Assets;
import com.github.piotrkruk.phage_wars.PhageWars;


/**
 * Main menu of the game
 *
 */

public class MainMenu implements Screen {
	private final PhageWars phageWars;
    
    private Stage stage = new Stage();
    
    private TextButton btnNewGame = new TextButton("Quick game", Assets.defaultSkin);
    private TextButton btnLevSelect = new TextButton("Levels", Assets.defaultSkin);
    private TextButton btnSettings = new TextButton("Settings", Assets.defaultSkin);
    private TextButton btnExit = new TextButton("Exit", Assets.defaultSkin);

    
    public MainMenu(PhageWars phageWars) {
    	this.phageWars = phageWars;
    	
    	initButtons();
    	Gdx.input.setInputProcessor(stage);
    }
    
    private void initButtons() {
    	int btnWidth = phageWars.mode.btnWidth,
    		btnHeight = phageWars.mode.btnHeight,
    		border = phageWars.mode.border,
    		block = phageWars.mode.blockSize,
    		left = phageWars.mode.width - border - btnWidth;
    	
    	btnExit.setBounds(left, border, btnWidth, btnHeight);
    	btnSettings.setBounds(left, btnExit.getY() + block + btnHeight, btnWidth, btnHeight);
    	btnLevSelect.setBounds(left, btnSettings.getY() + block + btnHeight, btnWidth, btnHeight);
    	btnNewGame.setBounds(left, btnLevSelect.getY() + block + btnHeight, btnWidth, btnHeight);   	
    	
        btnNewGame.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                MainMenu.this.phageWars.playSound();
                MainMenu.this.phageWars.setToGame();
            }
        } );
        
        btnLevSelect.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            	MainMenu.this.phageWars.playSound();
                MainMenu.this.phageWars.setToLevels();
            }
        } );
        
        btnSettings.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            	MainMenu.this.phageWars.playSound();
                MainMenu.this.phageWars.setToSettings();
            }
        } );
        
        btnExit.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            	MainMenu.this.phageWars.playSound();
                Gdx.app.exit();
            }
        } );
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
    	
    	stage.addActor(btnNewGame);
    	stage.addActor(btnLevSelect);
    	stage.addActor(btnSettings);
    	stage.addActor(btnExit);
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

package com.github.piotrkruk.phage_wars.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.piotrkruk.phage_wars.PhageWars;


/**
 * Main menu of the game
 *
 */

public class MainMenu implements Screen {
	private final PhageWars phageWars;
    
    private Stage stage = new Stage();
    
    private Skin defaultSkin = new Skin(Gdx.files.internal("uiskin.json"));
    private TextButton btnNewGame = new TextButton(String.valueOf("New Game"), defaultSkin);
    private TextButton btnSettings = new TextButton(String.valueOf("Settings"), defaultSkin);
    private TextButton btnExit = new TextButton(String.valueOf("Exit"), defaultSkin);
    
    public MainMenu(PhageWars phageWars) {
    	this.phageWars = phageWars;
    	
    	int btnWidth = phageWars.mode.btnWidth,
    		btnHeight = phageWars.mode.btnHeight,
    		border = phageWars.mode.border,
    		block = phageWars.mode.blockSize,
    		left = phageWars.mode.width - border - btnWidth;
    	
    	btnNewGame.setBounds(left, border + 2 * (block + btnHeight), btnWidth, btnHeight);
    	btnSettings.setBounds(left, border + block + btnHeight, btnWidth, btnHeight);
    	btnExit.setBounds(left, border, btnWidth, btnHeight);
    	
        btnNewGame.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                MainMenu.this.phageWars.setToGame();
            }
        } );
        
        btnSettings.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                MainMenu.this.phageWars.setToSettings();
            }
        } );
        
        btnExit.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Gdx.app.exit();
            }
        } );
    	
    	Gdx.input.setInputProcessor(stage);
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

package com.github.piotrkruk.phage_wars.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.piotrkruk.phage_wars.PhageWars;
import com.github.piotrkruk.phage_wars.PhageWars.DisplayMode;


/**
 * Settings window
 *
 */

public class Settings implements Screen {
	private final PhageWars phageWars;
    
    private Stage stage = new Stage();
    private Skin defaultSkin = new Skin(Gdx.files.internal("uiskin.json"));
    
    private TextButton btnBack = new TextButton(String.valueOf("Back to menu"), defaultSkin);
    
    private TextButton btnModeNormal = new TextButton(
    		String.valueOf("Normal resolution (" + DisplayMode.NORMAL.width
    						+ "x" + DisplayMode.NORMAL.height + ")"), defaultSkin);
    
    private TextButton btnModeHD = new TextButton(
    		String.valueOf("HD resolution (" + DisplayMode.HD.width
    						+ "x" + DisplayMode.HD.height + ")"), defaultSkin);
    
    public Settings(PhageWars phageWars) {
    	this.phageWars = phageWars;
    	
    	btnBack.setBounds(800, 100, 300, 100);
    	btnModeNormal.setBounds(100, 600, 300, 100);
    	btnModeHD.setBounds(100, 450, 300, 100);
    	
        btnBack.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Settings.this.phageWars.setToMenu();
            }
        } );
        
        btnModeNormal.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Settings.this.phageWars.mode = DisplayMode.NORMAL;
                Settings.this.phageWars.refreshMode();
                Settings.this.phageWars.setToSettings();
            }
        } );
        
        btnModeHD.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Settings.this.phageWars.mode = DisplayMode.HD;
                Settings.this.phageWars.refreshMode();
                Settings.this.phageWars.setToSettings();
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
    	
    	stage.addActor(btnBack);
    	stage.addActor(btnModeNormal);
    	stage.addActor(btnModeHD);
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
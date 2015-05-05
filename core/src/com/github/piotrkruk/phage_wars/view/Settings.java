package com.github.piotrkruk.phage_wars.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.piotrkruk.phage_wars.PhageWars;


/**
 * Settings window
 *
 */

public class Settings implements Screen {
	private final PhageWars phageWars;
	
    private Texture texture = new Texture(Gdx.files.internal("background.jpg"));
    private Image background = new Image(texture);
    
    private Stage stage = new Stage();
    private Skin defaultSkin = new Skin(Gdx.files.internal("uiskin.json"));
    
    private TextButton btnBack = new TextButton(String.valueOf("Back to menu"), defaultSkin);
    
    public Settings(PhageWars phageWars) {
    	this.phageWars = phageWars;
    	
    	btnBack.setBounds(800, 100, 300, 100);
    	
        btnBack.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Settings.this.phageWars.setToMenu();
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
    	stage.addActor(background);
    	
    	stage.addActor(btnBack);
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
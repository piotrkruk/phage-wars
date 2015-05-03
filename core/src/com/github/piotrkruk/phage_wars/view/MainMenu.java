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
 * Main menu of the game
 *
 */

public class MainMenu implements Screen {
	private final PhageWars phageWars;
	
    private Texture texture = new Texture(Gdx.files.internal("background.jpg"));
    private Image background = new Image(texture);
    
    private Stage stage = new Stage();
    
    private Skin btnSkin = new Skin(Gdx.files.internal("uiskin.json"));
    private TextButton btnNewGame = new TextButton(String.valueOf("New Game"), btnSkin);
    private TextButton btnSettings = new TextButton(String.valueOf("Settings"), btnSkin);
    private TextButton btnExit = new TextButton(String.valueOf("Exit"), btnSkin);
    
    public MainMenu(PhageWars phageWars) {
    	this.phageWars = phageWars;
    	
    	btnNewGame.setBounds(800, 360, 300, 100);
    	btnSettings.setBounds(800, 230, 300, 100);
    	btnExit.setBounds(800, 100, 300, 100);
    	
        btnNewGame.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                MainMenu.this.phageWars.startGame();
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
    	stage.addActor(background);
    	
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

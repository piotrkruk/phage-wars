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
import com.github.piotrkruk.phage_wars.PhageWars.GameMode;


/**
 * Settings window
 *
 */

public class Settings implements Screen {
	private final PhageWars phageWars;
    
    private Stage stage = new Stage();
    private Skin defaultSkin = new Skin(Gdx.files.internal("skins/uiskin.json"));
    
    private TextButton btnBack = new TextButton("Back to menu", defaultSkin);
    
    private TextButton btnModeNormal = new TextButton(
    		"Normal resolution (" + DisplayMode.NORMAL.width
    		+ "x" + DisplayMode.NORMAL.height + ")", defaultSkin);
    
    private TextButton btnModeHD = new TextButton(
    		"HD resolution (" + DisplayMode.HD.width
    		+ "x" + DisplayMode.HD.height + ")", defaultSkin);

    private TextButton btnModeFS = new TextButton(
            "Full screen", defaultSkin);
    
    private TextButton btnEasy = new TextButton("Easy", defaultSkin),
    				   btnMedium = new TextButton("Medium", defaultSkin),
    				   btnHard = new TextButton("Hard", defaultSkin);
    
    public Settings(PhageWars phageWars) {
    	this.phageWars = phageWars;
    	
    	int btnWidth = phageWars.mode.btnWidth,
        	btnHeight = phageWars.mode.btnHeight,
        	border = phageWars.mode.border,
        	block = phageWars.mode.blockSize,
        	left = phageWars.mode.width - border - btnWidth,
        	upper = phageWars.mode.height - border - btnHeight,
        	center = phageWars.mode.width / 2;
    	
    	btnBack.setBounds(left, border, btnWidth, btnHeight);
    	
    	btnModeNormal
    		.setBounds(center - block - 1.5f * btnWidth, upper, btnWidth, btnHeight);
    	btnModeHD
    		.setBounds(center - 0.5f * btnWidth, upper, btnWidth, btnHeight);
        btnModeFS
                .setBounds(center + block + 0.5f * btnWidth, upper, btnWidth, btnHeight);
    	
    	upper -= border + btnHeight;
    	
    	btnEasy
			.setBounds(center - block - 1.5f * btnWidth, upper, btnWidth, btnHeight);
    	btnMedium
			.setBounds(center - 0.5f * btnWidth, upper, btnWidth, btnHeight);
    	btnHard
			.setBounds(center + block + 0.5f * btnWidth, upper, btnWidth, btnHeight);
    	
    	btnModeNormal.getStyle().checked = btnModeNormal.getStyle().down;
    	btnModeHD.getStyle().checked = btnModeHD.getStyle().down;
        btnModeFS.getStyle().checked = btnModeFS.getStyle().down;
    	
    	btnEasy.getStyle().checked = btnEasy.getStyle().down;
    	btnMedium.getStyle().checked = btnMedium.getStyle().down;
    	btnHard.getStyle().checked = btnHard.getStyle().down;
    	
    	if (phageWars.mode == DisplayMode.NORMAL)
    		btnModeNormal.setChecked(true);
    	else if (phageWars.mode == DisplayMode.HD)
    		btnModeHD.setChecked(true);
        else
            btnModeFS.setChecked(true);
    	
    	if (phageWars.difficulty == GameMode.EASY)
    		btnEasy.setChecked(true);
    	else if (phageWars.difficulty == GameMode.MEDIUM)
    		btnMedium.setChecked(true);
    	else if (phageWars.difficulty == GameMode.HARD)
    		btnHard.setChecked(true);
    	
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

        btnModeFS.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Settings.this.phageWars.mode = DisplayMode.FS;
                Settings.this.phageWars.refreshMode();
                Settings.this.phageWars.setToSettings();
            }
        } );

        
        btnEasy.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Settings.this.phageWars.difficulty = GameMode.EASY;
                Settings.this.phageWars.setToSettings();
            }
        } );
        
        btnMedium.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Settings.this.phageWars.difficulty = GameMode.MEDIUM;
                Settings.this.phageWars.setToSettings();
            }
        } );
        
        btnHard.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Settings.this.phageWars.difficulty = GameMode.HARD;
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
        stage.addActor(btnModeFS);
    	
    	stage.addActor(btnEasy);
    	stage.addActor(btnMedium);
    	stage.addActor(btnHard);
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
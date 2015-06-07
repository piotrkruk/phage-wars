package com.github.piotrkruk.phage_wars.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.piotrkruk.phage_wars.Assets;
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


    private TextButton btnBack = new TextButton("Back to menu", Assets.defaultSkin);
    
    private TextButton btnModeNormal = new TextButton(
    		"Normal resolution (" + DisplayMode.NORMAL.width
    		+ "x" + DisplayMode.NORMAL.height + ")", Assets.defaultSkin);
    
    private TextButton btnModeHD = new TextButton(
    		"HD resolution (" + DisplayMode.HD.width
    		+ "x" + DisplayMode.HD.height + ")", Assets.defaultSkin);

    private TextButton btnModeFS = new TextButton(
            "Full screen", Assets.defaultSkin);
    
    private TextButton btnEasy = new TextButton("Easy", Assets.defaultSkin),
    				   btnMedium = new TextButton("Medium", Assets.defaultSkin),
    				   btnHard = new TextButton("Hard", Assets.defaultSkin);
    
	private Image circSoundOn = new Image(Assets.textureCircSoundOn);
	private Image circSoundOff = new Image(Assets.textureCircSoundOff);
    
    public Settings(PhageWars phageWars) {
    	this.phageWars = phageWars;
    	
    	int btnWidth = phageWars.mode.btnWidth,
        	btnHeight = phageWars.mode.btnHeight,
        	border = phageWars.mode.border,
        	block = phageWars.mode.blockSize,
        	circSize = 2 * block,
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
    	
		circSoundOff.setSize(circSize, circSize);
		circSoundOn.setSize(circSize, circSize);
    	
		circSoundOn.setPosition(phageWars.mode.width - circSize - block / 4,
				phageWars.mode.height - circSize - block / 4);

		circSoundOff.setPosition(phageWars.mode.width - circSize - block / 4,
				phageWars.mode.height - circSize - block / 4);

		if (phageWars.buttonClickVolume > 0)
			circSoundOff.setVisible(false);
		else
			circSoundOn.setVisible(false);

        btnBack.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Settings.this.phageWars.playSound();
                Settings.this.phageWars.setToMenu();
            }
        } );

        btnModeNormal.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            	Settings.this.phageWars.playSound();
                Settings.this.phageWars.mode = DisplayMode.NORMAL;
                Settings.this.phageWars.refreshMode();
                Settings.this.phageWars.setToSettings();
            }
        } );
        
        btnModeHD.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            	Settings.this.phageWars.playSound();
                Settings.this.phageWars.mode = DisplayMode.HD;
                Settings.this.phageWars.refreshMode();
                Settings.this.phageWars.setToSettings();
            }
        } );

        btnModeFS.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            	Settings.this.phageWars.playSound();
                Settings.this.phageWars.mode = DisplayMode.FS;
                Settings.this.phageWars.refreshMode();
                Settings.this.phageWars.setToSettings();

            }
        } );

        
        btnEasy.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            	Settings.this.phageWars.playSound();
                Settings.this.phageWars.difficulty = GameMode.EASY;
                Settings.this.phageWars.setToSettings();
            }
        } );
        
        btnMedium.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            	Settings.this.phageWars.playSound();
                Settings.this.phageWars.difficulty = GameMode.MEDIUM;
                Settings.this.phageWars.setToSettings();
            }
        } );
        
        btnHard.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            	Settings.this.phageWars.playSound();
                Settings.this.phageWars.difficulty = GameMode.HARD;
                Settings.this.phageWars.setToSettings();
            }
        } );
        
        
        circSoundOn.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
				System.out.println("Sounds turned off.");
				
				Settings.this.phageWars.buttonClickVolume = 0.0f;
				circSoundOff.setVisible(true);
				circSoundOn.setVisible(false);
            }
        } );
        
        circSoundOff.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
				System.out.println("Sounds turned on.");
				
				Settings.this.phageWars.buttonClickVolume = Assets.DEFAULT_CLICK_VOLUME;
				
				Settings.this.phageWars.playSound();
				circSoundOn.setVisible(true);
				circSoundOff.setVisible(false);
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
    	
		stage.addActor(circSoundOn);
		stage.addActor(circSoundOff);
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
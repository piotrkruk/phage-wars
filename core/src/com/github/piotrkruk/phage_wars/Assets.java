package com.github.piotrkruk.phage_wars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {
	
	public static Sound buttonClick = Gdx.audio.newSound(Gdx.files.internal("sounds/click1.wav"));
    public static final float DEFAULT_CLICK_VOLUME = 0.7f;
    
    public static BitmapFont font = new BitmapFont(Gdx.files.internal("skins/default.fnt"));
    public static Skin defaultSkin = new Skin(Gdx.files.internal("skins/uiskin.json"));
    
    public static Texture[] texturePlayers =
    	{
    		new Texture(Gdx.files.internal("cells/cell_blue.png")),
    		new Texture(Gdx.files.internal("cells/cell_red.png")),
    		new Texture(Gdx.files.internal("cells/cell_purple.png")),
			new Texture(Gdx.files.internal("cells/cell_yellow.png")),
			new Texture(Gdx.files.internal("cells/cell_green.png")),
			new Texture(Gdx.files.internal("cells/cell_orange.png")),
			new Texture(Gdx.files.internal("cells/cell_turquoise.png")),
    		new Texture(Gdx.files.internal("cells/cell_empty.png"))
    	};
    
    public static Texture[] textureBacterias =
    	{
    		new Texture(Gdx.files.internal("bacterias/bacteria_blue.png")),
    		new Texture(Gdx.files.internal("bacterias/bacteria_red.png")),
    		new Texture(Gdx.files.internal("bacterias/bacteria_purple.png")),
			new Texture(Gdx.files.internal("bacterias/bacteria_yellow.png")),
			new Texture(Gdx.files.internal("bacterias/bacteria_green.png")),
			new Texture(Gdx.files.internal("bacterias/bacteria_orange.png")),
			new Texture(Gdx.files.internal("bacterias/bacteria_turquoise.png"))
    	};
}

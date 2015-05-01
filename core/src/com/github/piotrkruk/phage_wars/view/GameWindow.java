package com.github.piotrkruk.phage_wars.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.github.piotrkruk.phage_wars.model.*;

/**
 * Main window of the game
 *
 */

public class GameWindow implements Screen {
    private Texture texture = new Texture(Gdx.files.internal("background.jpg"));
    private Image background = new Image(texture);
    
    private Stage stage = new Stage();
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    
    private GameStage game = new GameStage();
    
    public GameWindow() {
    	game.genRandom();
    }

    @Override
    public void render(float delta) {        
        stage.act();
        stage.draw();
        
        shapeRenderer.begin(ShapeType.Filled);
        
        for (Cell c : game.cells) {
        	if (c.owner.id == 0)
        		shapeRenderer.setColor(Color.BLUE);
        	else
        		shapeRenderer.setColor(Color.RED);
        	
        	shapeRenderer.circle(c.posX, c.posY, c.radius);
        }
        	
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    	stage.addActor(background);
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

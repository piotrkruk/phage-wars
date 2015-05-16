package com.github.piotrkruk.phage_wars.model;

import java.util.Random;

/**
 * Class responsible for generating random maps
 * and (in future) reading generated ones from files
 * 
 */

public class Map {
	
	private final static Random rand = new Random();
	private final int WIDTH, HEIGHT;
	
	private final GameStage game;

	
	public Map(int width, int height, GameStage game) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.game = game;
	}
	
	public void genRandom(int cellsPerPlayer, int emptyCells) {
		
		for (int i = 0; i < game.players.size(); i++) {
			for (int j = 0; j < cellsPerPlayer;) {
				Cell c = randCell(game.races.get(i), game.players.get(i));
				
				if (isValid(c)) {
					game.cells.add(c);
					j++;
				}
			}
			
			game.players.get(i).ownCount = cellsPerPlayer;
		}
		
		for (int j = 0; j < emptyCells;) {
			Cell c = randCell(new Race(), null);
			
			if (isValid(c)) {
				game.cells.add(c);
				j++;
			}
		}
	}
		
	/**
	 * Checks if c is a valid cell to be added
	 * 
	 */
	private boolean isValid(Cell c) {
		if (c.posX < c.radius || c.posX + c.radius > WIDTH ||
			c.posY < c.radius || c.posY + c.radius > HEIGHT)
			return false;
			
		for (Cell cl : game.cells)
			if (c.doesCollide(cl))
				return false;
		
		return true;
	}
	
	private Cell randCell(Race race, Player owner) {
		return new Cell(randX(), randY(), randRad(race), randUnits(race), race, owner);
	}
	
	private int randX() {
		return rand.nextInt(WIDTH);
	}
	
	private int randY() {
		return rand.nextInt(HEIGHT);
	}
	
	private int randRad(Race race) {
		return race.minRadius + rand.nextInt(race.maxRadius - race.minRadius + 1);
	}
	
	private int randUnits(Race race) {
		return race.minInitUnits + rand.nextInt(race.maxInitUnits - race.minInitUnits + 1);
	}	
}

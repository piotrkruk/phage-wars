package com.github.piotrkruk.phage_wars.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Class responsible for generating random maps
 * and (in future) reading generated ones from files
 * 
 */

public class Map {
	
	private final static int NUM_OF_TRIALS = 200;
	
	private final static Random rand = new Random();
	private final int WIDTH, HEIGHT;
	private final int BLOCK_SIZE;
	
	private final GameStage game;

	
	public Map(int width, int height, int blockSize, GameStage game) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.BLOCK_SIZE = blockSize;
		this.game = game;
	}
	
	public void genRandom(int cellsPerPlayer, int emptyCells) {
		
		int sumBest = 0;
		List <Cell> cellsBest = null;
	
		/*
		 *  Dummy cell located at the upper-right corner
		 *  to prevent cells from overlapping with buttons
		 */
		Cell dummy = new Cell(WIDTH - 2 * BLOCK_SIZE, HEIGHT, 4 * BLOCK_SIZE, 0, null, null);
		
		/*
		 * Generate some maps (only non-empty cells)
		 * and choose the best one
		 * 
		 */
		for (int trial = 0; trial < NUM_OF_TRIALS; trial++) {
			
			int sumTemp = Integer.MAX_VALUE;
			List <Cell> cellsTemp = new LinkedList <Cell> ();

			cellsTemp.add(dummy);
		
			for (int i = 0; i < game.players.size(); i++) {
				for (int j = 0; j < cellsPerPlayer;) {
					Cell c = randCell(game.races.get(i), game.players.get(i));
					
					if (isValid(c, cellsTemp)) {
						cellsTemp.add(c);
						j++;
					}
				}
			}
			
			for (Cell a : cellsTemp) if (a != dummy)
				for (Cell b : cellsTemp) if (b != dummy && a != b)
					sumTemp = Math.min(sumTemp, Cell.distSquared(a, b));
			
			if (sumTemp > sumBest) {
				sumBest = sumTemp;
				cellsBest = cellsTemp;
			}
		}
		
		/*
		 * Add empty cells
		 */
		for (int j = 0; j < emptyCells;) {
			Cell c = randCell(new Race(BLOCK_SIZE), null);
			
			if (isValid(c, cellsBest)) {
				cellsBest.add(c);
				j++;
			}
		}
		
		
		cellsBest.remove(0); // remove dummy cell
		
		for (int i = 0; i < game.players.size(); i++)
			game.players.get(i).ownCount = cellsPerPlayer;
		
		game.cells = cellsBest;
	}
	
	/**
	 * @param path - path containing a file from which a map is to be read
	 * 
	 */
	public void read(String path) {
		// TODO
	}
		
	/**
	 * Checks if c is a valid cell to be added
	 * to existing list of cells
	 * 
	 */
	private boolean isValid(Cell c, List <Cell> list) {
		if (c.posX < c.radius || c.posX + c.radius > WIDTH ||
			c.posY < c.radius || c.posY + c.radius > HEIGHT)
			return false;
			
		for (Cell cl : list)
			if (c.doesCollide(cl))
				return false;
		
		return true;
	}
	
	private Cell randCell(Race race, Player owner) {
		
		if (owner == null) // empty cell
			return new Cell(randX(), randY(), randRad(race), randUnits(race), race, owner);
		else // non-empty cells are less random to insure fairness
			return new Cell(randX(), randY(), race.maxRadius, race.maxInitUnits, race, owner);
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

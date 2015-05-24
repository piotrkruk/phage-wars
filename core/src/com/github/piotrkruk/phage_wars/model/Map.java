package com.github.piotrkruk.phage_wars.model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Class responsible for generating random maps
 * and reading generated ones from *.ser files
 * which are just GameStage's state serialized
 * 
 */

public class Map implements Serializable {

	private static final long serialVersionUID = 1L;

	private final static int NUM_OF_TRIALS = 200;
	private final static Random rand = new Random();
	
	private final GameStage game;

	
	public Map(GameStage game) {
		this.game = game;
	}
	
	public void genRandom(int cellsPerPlayer, int emptyCells) {
		
		int sumBest = 0;
		List <Cell> cellsBest = null;
	
		/*
		 *  Dummy cell located at the upper-right corner
		 *  to prevent cells from overlapping with buttons
		 */
		Cell dummy =
			new Cell(game.WIDTH - 2 * game.BLOCK_SIZE, game.HEIGHT, 4 * game.BLOCK_SIZE, 0, null, null);
		
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
			Cell c = randCell(new Race(game.BLOCK_SIZE), null);
			
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
	public static GameStage read(int width, int height, int blockSize,
			double aiStrength, String path) {
		
		FileInputStream fileIn;
		ObjectInputStream in;
		
		try {
			fileIn = new FileInputStream(path);
			in = new ObjectInputStream(fileIn);
			
			try {
				GameStage temp = (GameStage) in.readObject();
				
				/*
				 * Set transient variables
				 * to values given from outside 
				 */				
				temp.WIDTH = width;
				temp.HEIGHT = height;
				temp.BLOCK_SIZE = blockSize;
				temp.AI_STRENGTH = aiStrength;
				
				temp.grid = new Grid(width, height, blockSize, temp);
				
				System.out.println("Map read successfully.");
				return temp;
			}
			finally {
				in.close();
				fileIn.close();
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		} catch (IOException e) {
			System.out.println("IO failed! " + e);
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found! " + e);
		}
		
		return null;
	}
	
	/**
	 * @param path - path containing a file into which a map is to be written
	 * 
	 */
	public static void write(GameStage game, String path) {
		FileOutputStream fileOut;
		ObjectOutputStream out;
		
		try {
			fileOut = new FileOutputStream(path);
			out = new ObjectOutputStream(fileOut);
			
			try {
				out.writeObject(game);
			}
			finally {
				out.close();
				fileOut.close();
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		} catch (IOException e) {
			System.out.println("IO failed! " + e);
		}
		
		System.out.println("Map saved successfully.");
	}
		
	/**
	 * Checks if c is a valid cell to be added
	 * to existing list of cells
	 * 
	 */
	private boolean isValid(Cell c, List <Cell> list) {
		if (c.posX < c.radius || c.posX + c.radius > game.WIDTH ||
			c.posY < c.radius || c.posY + c.radius > game.HEIGHT)
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
		return rand.nextInt(game.WIDTH);
	}
	
	private int randY() {
		return rand.nextInt(game.HEIGHT);
	}
	
	private int randRad(Race race) {
		return race.minRadius + rand.nextInt(race.maxRadius - race.minRadius + 1);
	}
	
	private int randUnits(Race race) {
		return race.minInitUnits + rand.nextInt(race.maxInitUnits - race.minInitUnits + 1);
	}	
}

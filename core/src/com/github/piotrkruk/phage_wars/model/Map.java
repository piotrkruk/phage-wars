package com.github.piotrkruk.phage_wars.model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
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

		Cell dummyCell = getDummyCell(game);
		
		/*
		 * Generate some maps (only non-empty cells)
		 * and choose the best one
		 */
		for (int trial = 0; trial < NUM_OF_TRIALS; trial++) {
			
			int sumTemp = Integer.MAX_VALUE;
			List <Cell> cellsTemp = new LinkedList <Cell> ();

			cellsTemp.add(dummyCell);
		
			for (int i = 0; i < game.players.size(); i++) {
				for (int j = 0; j < cellsPerPlayer;) {
					Cell c = randCell(game.races.get(i), game.players.get(i));
					
					if (isValid(c, cellsTemp, game.WIDTH, game.HEIGHT)) {
						cellsTemp.add(c);
						j++;
					}
				}
			}
			
			for (Cell a : cellsTemp) if (a != dummyCell)
				for (Cell b : cellsTemp) if (b != dummyCell && a != b)
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
			Cell c = randCell(new Race(game), null);
			
			if (isValid(c, cellsBest, game.WIDTH, game.HEIGHT)) {
				cellsBest.add(c);
				j++;
			}
		}
		
		
		cellsBest.remove(0); // remove dummy cell
		
		/*
		 * Assign images randomly to opponent players
		 * but the human playing the game is always blue
		 */
		
		List <Integer> idList = new LinkedList <Integer> ();
		
		for (int i = 1; i < game.NO_OF_IMAGES_TO_CHOOSE_FROM; i++)
			idList.add(i);
		
		Collections.shuffle(idList);
			
		idList.add(0, 0);
		
		for (int i = 0; i < game.NO_OF_PLAYERS; i++) {
			Player pl = game.players.get(i);
			
			pl.ownCount = cellsPerPlayer;
			pl.imageId = idList.get(i);
		}
		
		game.cells = cellsBest;
	}
	
	public static Cell getDummyCell(GameStage game) {
		/*
		 *  Dummy cell located at the upper-right corner
		 *  to prevent cells from overlapping with buttons
		 */
		
		return new Cell(game.WIDTH - 2 * game.BLOCK_SIZE, game.HEIGHT, 4 * game.BLOCK_SIZE, 0, null, null);
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
				
				temp.resize(width, height, blockSize);
				
				/*
				 * Set transient fields
				 */
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
	 * If cell c is itself present on the list
	 * the function doesn't take that as an invalid state
	 * 
	 */
	public static boolean isValid(Cell c, List <Cell> list, int width, int height) {
		if (c.posX < c.radius || c.posX + c.radius > width ||
			c.posY < c.radius || c.posY + c.radius > height)
			return false;
		
		for (Cell cl : list) if (c != cl)
			if (c.doesCollide(cl))
				return false;
		
		return true;
	}
	
	/**
	 * Checks if c is a valid cell to be added
	 * to some game stage
	 * 
	 */
	public static boolean isValid(Cell c, GameStage game) {
		return isValid(c, game.cells, game.WIDTH, game.HEIGHT);
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

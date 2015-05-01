package com.github.piotrkruk.phage_wars.model;

import java.util.*;
import com.github.piotrkruk.phage_wars.PhageWars;

/**
 * Models the game,
 * that is all the cells
 * and players controlling them
 *
 */

public class GameStage {
	
	final private Random rand = new Random();
	
	// for positioning of the cells:
	final int HEIGHT = PhageWars.HEIGHT;
	final int WIDTH = PhageWars.WIDTH;
	
	// for cell's generating:
	private final int MIN_RADIUS = 10;
	private final int MAX_RADIUS = 50;
	private final int MAX_INIT_UNITS = 100;
	private final int CELLS_PER_PLAYER = 3;
	
	final int NO_OF_PLAYERS = 2;
	
	
	List <Player> players = new ArrayList <Player> ();
	List <Race> races = new ArrayList <Race> ();
	
	List <Cell> cells = new ArrayList <Cell> ();
	
	// player playing the game and his race:
	Player player;
	Race race;
	
	
	public GameStage() {
		
		for (int i = 0; i < NO_OF_PLAYERS; i++) {
			Player p = new Player();
			
			players.add(p);
			races.add( new Race(p) );
		}
		
		player = players.get(0);
		race = races.get(0);
	}
	
	/**
	 * Generates a random stage filled with cells
	 */
	public void genRandom() {
		
		for (int i = 0; i < NO_OF_PLAYERS; i++) {
			for (int j = 0; j < CELLS_PER_PLAYER;) {
				Cell c = randCell(races.get(i), players.get(i));
				
				boolean collision = false;
				
				for (Cell cl : cells)
					if (c.doesCollide(cl))
						collision = true;
				
				if (!collision) {
					cells.add(c);
					j++;
				}
			}
		}
		
	}
	
	
	/**
	 * Grows all the cells on the board
	 */
	public void update() {
		for (Cell c : cells)
			c.grow();
	}
	
	public Cell randCell(Race race, Player owner) {
		return
			new Cell(randX(), randY(), randRad(), randUnits(), race, owner);
	}
	
	private int randX() {
		return rand.nextInt(WIDTH);
	}
	
	private int randY() {
		return rand.nextInt(HEIGHT);
	}
	
	private int randRad() {
		return MIN_RADIUS + rand.nextInt(MAX_RADIUS - MIN_RADIUS);
	}
	
	private int randUnits() {
		return rand.nextInt(MAX_INIT_UNITS);
	}
	
	
	/**
	 * Sends units from all selected cells
	 * to destination cell
	 */
	public void send(Cell destination) {
		int sum = 0;
		
		for (Cell c : cells)
			if (c.selected)
				sum += c.sendUnits();
		
		if (sum > 0)
			destination.addUnits(sum, player);
	}
	

	public void deselectAll() {
		for (Cell c : cells)
			c.deselect();
	}

}

package model;

import java.util.*;

/**
 * Models the game,
 * that is all the cells
 * and players controlling them
 *
 */

public class Game {
	
	final private Random rand = new Random();
	
	// for positioning of the cells:
	final int HEIGHT = 800;
	final int WIDTH = 1200;
	
	// for cell's generating:
	final int MIN_RADIUS = 10;
	final int MAX_RADIUS = 50;
	final int MAX_INIT_UNITS = 100;
	
	final int NO_OF_AI = 1;
	
	
	List <Player> players = new ArrayList <Player> ();
	List <Race> races = new ArrayList <Race> ();
	
	List <Cell> cells = new ArrayList <Cell> ();
	
	// player playing the game and his race:
	Player player;
	Race race;
	
	
	public Game() {
		player = new Player();
		race = new Race(player);

		players.add(player);
		races.add(race);
		
		for (int i = 1; i <= NO_OF_AI; i++) {
			Player p = new Player();
			
			players.add(p);
			races.add( new Race(p) );
		}
		
		/**
		 * TODO: create random cells
		 * all over the board
		 * using randCell()
		 * and checking if the generated cell
		 * doesn't collide with others
		 */
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

package com.github.piotrkruk.phage_wars.model;

import java.util.*;

import com.badlogic.gdx.graphics.Color;
import com.github.piotrkruk.phage_wars.PhageWars;

/**
 * Models the game,
 * that is all the cells
 * and players controlling them
 *
 */

public class GameStage {
	
	private final static Random rand = new Random();
	
	// for positioning of the cells:
	final int WIDTH = PhageWars.WIDTH;
	final int HEIGHT = PhageWars.HEIGHT;
	
	// for generating the stage:
	private static final int CELLS_PER_PLAYER = 1;
	private static final int EMPTY_CELLS = 4;
	
	public static final int NO_OF_PLAYERS = 3;
	
	// objects present on the stage:
	public List <Player> players = new ArrayList <Player> ();
	public List <Race> races = new ArrayList <Race> ();
	
	Color[] colors =
		{
			Color.BLUE,
			Color.RED,
			Color.PURPLE,
			Color.CYAN,
			Color.GREEN
		};
	
	public List <Cell> cells = new ArrayList <Cell> ();
	public List <Bacteria> bacterias = new ArrayList <Bacteria> ();
	
	public Grid grid;
	
	// player playing the game and his race:
	public Player player;
	public Race race;
	
	public GameStage() {
		
		for (int i = 0; i < NO_OF_PLAYERS; i++) {
			Player p = new Player();
			p.color = colors[i];
			
			players.add(p);
			races.add( new Race(p) );
		}
		
		player = players.get(0);
		race = races.get(0);
		grid = new Grid(WIDTH, HEIGHT, this);
		
		player.setActive();
	}
	
	public void startGame() {
		for (int i = 1; i < NO_OF_PLAYERS; i++)
			new Thread( new AI(this, players.get(i)) ).start();
	}
	
	public synchronized boolean isRunning() {
		/*
		 * The game is still running
		 * if there is at least active player still alive
		 * and at least one other player
		 */
		
		int playingCount = 0;
		
		for (Player p : players)
			if (p.isPlaying())
				playingCount++;
		
		if (playingCount <= 1)
			return false;
		
		for (Player p : players)
			if (p.isActive && p.isPlaying())
				return true;
		
		return false;
	}
	
	/**
	 * Generates a random stage filled with cells
	 */
	public void genRandom() {
		
		for (int i = 0; i < NO_OF_PLAYERS; i++) {
			for (int j = 0; j < CELLS_PER_PLAYER;) {
				Cell c = randCell(races.get(i), players.get(i));
				
				boolean collision = false;
				
				if (c.posX < c.radius || c.posX + c.radius > WIDTH ||
					c.posY < c.radius || c.posY + c.radius > HEIGHT)
					collision = true;
				
				for (Cell cl : cells)
					if (c.doesCollide(cl))
						collision = true;
				
				if (!collision) {
					cells.add(c);
					j++;
				}
			}
			
			players.get(i).ownCount = CELLS_PER_PLAYER;
		}
		
		for (int j = 0; j < EMPTY_CELLS;) {
			Cell c = randCell(new Race(), null);
			
			boolean collision = false;
			
			if (c.posX < c.radius || c.posX + c.radius > WIDTH ||
				c.posY < c.radius || c.posY + c.radius > HEIGHT)
				collision = true;
			
			for (Cell cl : cells)
				if (c.doesCollide(cl))
					collision = true;
			
			if (!collision) {
				cells.add(c);
				j++;
			}
		}
		
	}
	
	
	/**
	 * Updates:
	 * 		- sizes of all the cells on the board
	 * 			after delta second have passed
	 * 			but omits empty ones (with no owner)
	 * 		- positions of the bacterias
	 * 
	 */
	public void update(float delta) {
		for (Cell c : cells)
			if (c.owner != null)
				c.grow(delta);
		
		Iterator <Bacteria> it = bacterias.iterator();
		
		while (it.hasNext()) {
			Bacteria b = it.next();
	
			if (b.move(delta)) {
				// the bacteria has reached it's destination
				
				b.destination.addUnits(b.units, b.from);
				it.remove();
			}
		}
	}
	
	public Cell randCell(Race race, Player owner) {
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
		return rand.nextInt(race.maxInitUnits + 1);
	}
	
	
	/**
	 * Sends units from all selected cells
	 * to destination cell
	 */
	public synchronized void send(Cell destination, Player p) {
		
		for (Cell c : cells)
			if (c.owner == p && c.selected) {
				grid.runSearch(c, destination);
				
				double units = c.sendUnits();		
				double unitsPerBacteria = units / Bacteria.BACTERIAS_PER_SHOT;
				
				for (int i = 0; i < Bacteria.BACTERIAS_PER_SHOT; i++)
					bacterias.add( new Bacteria(unitsPerBacteria, p, destination, grid) );
			}
	}
	

	public synchronized void deselectAll(Player p) {
		for (Cell c : cells)
			if (c.owner == p)
				c.deselect();
	}

}

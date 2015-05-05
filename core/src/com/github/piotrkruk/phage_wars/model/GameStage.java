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
	
	private final static Random rand = new Random();
	
	// for positioning of the cells:
	final int HEIGHT = PhageWars.HEIGHT;
	final int WIDTH = PhageWars.WIDTH;
	
	// for generating the stage:
	private final int CELLS_PER_PLAYER = 1;
	private final int EMPTY_CELLS = 4;
	
	final int NO_OF_PLAYERS = 2;
	
	// objects present on the stage:
	public List <Player> players = new ArrayList <Player> ();
	public List <Race> races = new ArrayList <Race> ();
	
	public List <Cell> cells = new ArrayList <Cell> ();
	
	// player playing the game and his race:
	public Player player;
	public Race race;
	
	public boolean isRunning() {
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
	
	public GameStage() {
		
		for (int i = 0; i < NO_OF_PLAYERS; i++) {
			Player p = new Player();
			
			players.add(p);
			races.add( new Race(p) );
		}
		
		player = players.get(0);
		race = races.get(0);
		
		player.setActive();
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
	 * Grows all the cells on the board
	 * after delta second have passed
	 * but omits empty ones (with no owner)
	 */
	public void update(float delta) {
		for (Cell c : cells)
			if (c.owner != null)
				c.grow(delta);
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
	public void send(Cell destination, Player p) {
		int sum = 0;
		
		for (Cell c : cells)
			if (c.owner == p && c.selected)
				sum += c.sendUnits();
		
		destination.addUnits(sum, p);
	}
	

	public void deselectAll(Player p) {
		for (Cell c : cells)
			if (c.owner == p)
				c.deselect();
	}

}

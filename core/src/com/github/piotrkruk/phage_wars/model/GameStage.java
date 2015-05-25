package com.github.piotrkruk.phage_wars.model;


import java.io.Serializable;
import java.util.*;


/**
 * Models the game,
 * that is all the cells
 * and players controlling them
 *
 */

public class GameStage implements Serializable {

	private static final long serialVersionUID = 1L;

	// for positioning of the cells:
	public transient int WIDTH, HEIGHT, BLOCK_SIZE;
	
	// for generating the stage:
	private static final int DEFAULT_CELLS_PER_PLAYER = 1;
	private static final int DEFAULT_EMPTY_CELLS = 2;
	
	public transient double PLAYER_STRENGTH = 1.0;
	public transient double AI_STRENGTH = 1.0;
	
	public final boolean HUMAN_PLAYER = true;
	public final int NO_OF_PLAYERS = 7;
	
	private final Map mapHandler;
	
	public volatile boolean paused = false;
	
	// objects present on the stage:
	public List <Player> players = new ArrayList <Player> ();
	public List <Race> races = new ArrayList <Race> ();
	
	public List <Cell> cells = new ArrayList <Cell> ();
	public List <Bacteria> bacterias = new ArrayList <Bacteria> ();
	
	public transient Grid grid;
	
	// if there is a human player - his player class and race:
	public Player player = null;
	public Race race = null;
	
	public GameStage(int width, int height, int blockSize, double aiStrength) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.BLOCK_SIZE = blockSize;
		this.AI_STRENGTH = aiStrength;
		
		this.mapHandler = new Map(this);
		
		Player.idCnt = 0;
		
		for (int i = 0; i < NO_OF_PLAYERS; i++) {
			Player p = new Player();
			
			double strength;
			
			if (HUMAN_PLAYER && i == 0)
				strength = PLAYER_STRENGTH;
			else
				strength = AI_STRENGTH;
			
			players.add(p);
			races.add( new Race(BLOCK_SIZE, p, strength) );
		}
		
		grid = new Grid(WIDTH, HEIGHT, BLOCK_SIZE, this);
		
		if (HUMAN_PLAYER) {
			player = players.get(0);
			race = races.get(0);
			
			player.setActive();
		}
		else
			for (Player p : players)
				p.setActive();
	}
	
	public void startGame() {
		int i;
		
		if (HUMAN_PLAYER)
			i = 1;
		else
			i = 0;
		
		for (; i < NO_OF_PLAYERS; i++)
			new Thread( new AI(this, players.get(i), AI_STRENGTH) ).start();
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
		mapHandler.genRandom(DEFAULT_CELLS_PER_PLAYER, DEFAULT_EMPTY_CELLS);		
	}
	
	
	/**
	 * Updates:
	 * 		- sizes of all the cells on the board
	 * 			after delta second have passed
	 * 			but omits empty ones (with no owner)
	 * 		- positions of the bacterias
	 * 
	 */
	public synchronized void update(float delta) {
		for (Cell c : cells)
			if (c.owner != null)
				c.grow(delta);
		
		Iterator <Bacteria> it = bacterias.iterator();
		
		while (it.hasNext()) {
			Bacteria b = it.next();
	
			if (b.move(delta)) {
				// the bacteria has reached it's destination
				
				b.destination.addUnits(b.units, b.from);
				b.from.bacteriaCount--;
				
				it.remove();
			}
		}
	}	
	
	/**
	 * Sends units from all selected cells
	 * to destination cell
	 */
	public synchronized void send(Cell destination, Player p) {
		
		for (Cell c : cells)
			if (c.owner == p && c.selected) {
				if (!grid.runSearch(c, destination)) {
					deselectAll(p);
					return ; // returns if path not found
				}
					
				double units = c.sendUnits();	
				
				int numberOfBacterias = (int) (units / Bacteria.UNITS_PER_BACTERIA);
				double unitsPerBacteria = units / numberOfBacterias;
				
				for (int i = 0; i < numberOfBacterias; i++)
					bacterias.add( new Bacteria(unitsPerBacteria, p, destination, grid) );
				
				p.bacteriaCount += numberOfBacterias;
			}
	}
	
	public synchronized void deselectAll(Player p) {
		for (Cell c : cells)
			if (c.owner == p)
				c.deselect();
	}
}

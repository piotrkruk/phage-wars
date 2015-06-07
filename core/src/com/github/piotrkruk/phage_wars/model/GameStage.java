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
	public int width, height, blockSize;
	
	// for generating the stage:
	private static final int DEFAULT_CELLS_PER_PLAYER = 1;
	private static final int DEFAULT_EMPTY_CELLS = 5;
	private static final int DEFAULT_NO_OF_PLAYERS = 3;
	public static final int MAX_NO_OF_PLAYERS = 7;
	
	public transient double PLAYER_STRENGTH = 1.0;
	public transient double AI_STRENGTH = 1.0;
	
	public final boolean HUMAN_PLAYER = true;
	
	public final int NO_OF_PLAYERS;
	public final int NO_OF_IMAGES_TO_CHOOSE_FROM = 6;
	
	public final Map mapHandler;
	
	public volatile boolean paused = false;
	private volatile boolean shutdown = false;
	
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
		this(width, height, blockSize, aiStrength, DEFAULT_NO_OF_PLAYERS);
	}
	
	public GameStage(int width, int height, int blockSize, double aiStrength, int noOfPlayers) {
		this.width = width;
		this.height = height;
		this.blockSize = blockSize;
		this.AI_STRENGTH = aiStrength;
		this.NO_OF_PLAYERS = noOfPlayers;
		
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
			races.add( new Race(this, p, strength) );
		}
		
		grid = new Grid(this.width, this.height, this.blockSize, this);
		
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
		
		/*
		 * Run threads for all the AI's, omitting
		 * the dead ones - during map edition all players
		 * are created, but some may have not been given any cells
		 */
		
		for (; i < NO_OF_PLAYERS; i++)
			if (players.get(i).isPlaying())
				new Thread( new AI(this, players.get(i), AI_STRENGTH) ).start();
	}
	
	/**
	 * Forces game's shutdown
	 * 
	 */
	public synchronized void shutdown() {
		shutdown = true;
	}
	
	public synchronized boolean isRunning() {
		if (shutdown)
			return false;
		
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
	 * 
	 * @return true if anything was sent
	 * 
	 */
	public synchronized boolean send(Cell destination, Player p) {
		
		boolean result = false;
		
		for (Cell c : cells)
			if (c.owner == p && c.selected && c.units > Cell.MIN_UNITS_FOR_SENDING) {
				if (!grid.runSearch(c, destination)) {
					continue;
					/*
					 * Cells that couldn't find their way
					 * to the destination stay selected
					 * so the player can try sending units
					 * to some other place
					 */
				}
				else
					result = true;
					
				double units = c.sendUnits();	
				
				int numberOfBacterias = (int) (units / Bacteria.UNITS_PER_BACTERIA);
				
				numberOfBacterias = Math.min(numberOfBacterias, Bacteria.MAX_BACTERIAS);
				numberOfBacterias = Math.max(numberOfBacterias, 1);
				
				double unitsPerBacteria = units / numberOfBacterias;
				
				for (int i = 0; i < numberOfBacterias; i++)
					bacterias.add( new Bacteria(unitsPerBacteria, p, destination, this) );
				
				p.bacteriaCount += numberOfBacterias;
			}
		
		return result;
	}
	
	public synchronized void deselectAll(Player p) {
		for (Cell c : cells)
			if (c.owner == p)
				c.deselect();
	}
	
	/**
	 * Resize all coordinates-dependent objects
	 */	
	public void resize(int width, int height, int blockSize) {
		double ratio = ((double) blockSize) / this.blockSize;
		
		for (Cell c : cells) {
			c.posX *= ratio;
			c.posY *= ratio;
			c.radius *= ratio;
		}
		
		for (Bacteria b : bacterias) {
			b.posX *= ratio;
			b.posY *= ratio;
		}
		
		this.width = width;
		this.height = height;
		this.blockSize = blockSize;
	}
}

package com.github.piotrkruk.phage_wars.model;

import java.util.*;
import com.badlogic.gdx.graphics.Color;

/**
 * Models the game,
 * that is all the cells
 * and players controlling them
 *
 */

public class GameStage {
	
	// for positioning of the cells:
	private final int WIDTH, HEIGHT, BLOCK_SIZE;
	
	// for generating the stage:
	private static final int DEFAULT_CELLS_PER_PLAYER = 1;
	private static final int DEFAULT_EMPTY_CELLS = 4;
	
	private static final double DEFAULT_PLAYER_STRENGTH = 1.0;
	private static final double DEFAULT_OPPONENT_STRENGTH = 1.4;
	
	public static final boolean HUMAN_PLAYER = true;
	public static final int NO_OF_PLAYERS = 3;
	
	private final Map mapHandler;
	
	public volatile boolean paused = false;
	
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
	
	// if there is a human player - his player class and race:
	public Player player = null;
	public Race race = null;
	
	public GameStage(int width, int height, int blockSize) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.BLOCK_SIZE = blockSize;
		
		this.mapHandler = new Map(width, height, BLOCK_SIZE, this);
		
		Player.idCnt = 0;
		
		for (int i = 0; i < NO_OF_PLAYERS; i++) {
			Player p = new Player();
			p.color = colors[i];
			
			double strength;
			
			if (HUMAN_PLAYER && i == 0)
				strength = DEFAULT_PLAYER_STRENGTH;
			else
				strength = DEFAULT_OPPONENT_STRENGTH;
			
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
				double unitsPerBacteria = units / Bacteria.BACTERIAS_PER_SHOT;
				
				for (int i = 0; i < Bacteria.BACTERIAS_PER_SHOT; i++)
					bacterias.add( new Bacteria(unitsPerBacteria, p, destination, grid) );
				
				p.bacteriaCount += Bacteria.BACTERIAS_PER_SHOT;
			}
	}
	

	public synchronized void deselectAll(Player p) {
		for (Cell c : cells)
			if (c.owner == p)
				c.deselect();
	}

}

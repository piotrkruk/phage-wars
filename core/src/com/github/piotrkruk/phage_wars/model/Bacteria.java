package com.github.piotrkruk.phage_wars.model;


import java.io.Serializable;
import java.util.List;
import java.util.Random;

/**
 * Class modelling one of the bacterias
 * sent from one cell to another
 *
 */


public class Bacteria implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Random rand = new Random();
	
	public static final int DEFAULT_RADIUS = 8;
	public static final double UNITS_PER_BACTERIA = 1.5;
	private static final double MAX_WAIT_TIME = 0.6;
	private static final double TIME_PER_STEP = 0.07;
	
	public int posX, posY;
	
	public final int radius = DEFAULT_RADIUS;
	
	private double timer = -rand.nextDouble() * MAX_WAIT_TIME;
		// time since started moving
	
	/*
	 * this bacteria has 'units' units
	 * sent from a bacteria owned by player 'from'
	 * and the destination is cell 'destination'
	 */
	public double units;
	public Player from;
	public Cell destination;
	
	public final GameStage gameStage;
	
	private List <Grid.Point> path;
	
	public Bacteria(double units, Player from, Cell destination, GameStage gameStage) {
		this.units = units;
		this.from = from;
		this.destination = destination;
		this.gameStage = gameStage;
		
		path = gameStage.grid.getPath();
	}
	
	/**
	 * Moves the bacteria according to it's internal path
	 * returns true if the bacteria has reached it's destination
	 * and therefore should be deleted
	 * 
	 */
	public boolean move(float delta) {
		timer += delta;
		
		int pos = (int) (timer / TIME_PER_STEP);
		double between = (timer / TIME_PER_STEP) - pos;
		
			// the bacteria is between path.get(pos) and path.get(pos+1)
		
		pos = Math.max(pos, 0);
		pos = Math.min(pos, path.size() - 1);
		
		Grid.Point start = path.get(pos);
		
		posX = start.posX;
		posY = start.posY;
		
		if (pos < path.size() - 1) {
			Grid.Point end = path.get(pos + 1);
			
			posX += between * (end.posX - start.posX);
			posY += between * (end.posY - start.posY);
		}
		
		return destination.isInside(posX, posY);		
	}
}

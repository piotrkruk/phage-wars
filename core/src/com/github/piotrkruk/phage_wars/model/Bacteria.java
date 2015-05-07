package com.github.piotrkruk.phage_wars.model;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Class modelling one of the bacterias
 * sent from one cell to another
 *
 */

/**
 * TODO:
 * 
 * - for now, every render moves a bacteria by one step,
 * 		but the goal is to move it smoothly between points on the path
 * 
 */

public class Bacteria {
	private static final Random rand = new Random();
	
	public static final int DEFAULT_RADIUS = 8;
	public static final int BACTERIAS_PER_SHOT = 10;
	private static final int MAX_WAIT_TIME = 600;
	
	public int posX, posY,
			   radius = DEFAULT_RADIUS;
	
	private double waiting = rand.nextInt(MAX_WAIT_TIME);
		// time before starting to move
	
	/*
	 * this bacteria has 'units' units
	 * sent from a bacteria owned by player 'from'
	 * and the destination is cell 'destination'
	 */
	public double units;
	public Player from;
	public Cell destination;
	
	private Iterator <Grid.Point> it;
	private List <Grid.Point> path;
	
	public Bacteria(double units, Player from, Cell destination, Grid g) {
		this.units = units;
		this.from = from;
		this.destination = destination;
		
		path = g.getPath();
		it = path.iterator();
	}
	
	/**
	 * Moves the bacteria according to it's internal path
	 * returns true if the bacteria has reached it's destination
	 * and therefore should be deleted
	 * 
	 */
	public boolean move(float delta) {
		if (waiting > 0)
			waiting -= 1000 * delta;
		else if (it.hasNext()) {
			// move the bacteria to the next point
			Grid.Point pt = it.next();
		
			posX = pt.posX;
			posY = pt.posY;
		}
		
		return destination.isInside(posX, posY);		
	}
}

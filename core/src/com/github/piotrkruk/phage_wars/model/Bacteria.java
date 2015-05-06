package com.github.piotrkruk.phage_wars.model;

import java.util.Iterator;
import java.util.List;

/**
 * Class modelling one of the bacterias
 * sent from one cell to another
 *
 */

public class Bacteria {
	public static final int DEFAULT_RADIUS = 5;
	
	public int posX, posY,
			   radius = DEFAULT_RADIUS;
	
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
	 * and should be deleted
	 * 
	 */
	public boolean move() {
		
		if (it.hasNext()) {
			Grid.Point pt = it.next();
		
			posX = pt.posX;
			posY = pt.posY;
		}
		
		return destination.isInside(posX, posY);		
	}
}

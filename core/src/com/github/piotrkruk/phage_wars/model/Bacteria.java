package com.github.piotrkruk.phage_wars.model;


import java.util.List;
import java.util.Random;

/**
 * Class modelling one of the bacterias
 * sent from one cell to another
 *
 */


public class Bacteria {
	private static final Random rand = new Random();
	
	public static final int DEFAULT_RADIUS = 8;
	public static final int BACTERIAS_PER_SHOT = 10;
	private static final double MAX_WAIT_TIME = 0.6;
	private static final double TIME_PER_PIXEL = 0.005;
	
	public int posX, posY;
	public final int radius = DEFAULT_RADIUS;
	private final double timePerStep;
	
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
	
	private List <Grid.Point> path;
	
	public Bacteria(double units, Player from, Cell destination, Grid g) {
		this.units = units;
		this.from = from;
		this.destination = destination;
		this.timePerStep = TIME_PER_PIXEL * g.pointDist;
		
		path = g.getPath();
	}
	
	/**
	 * Moves the bacteria according to it's internal path
	 * returns true if the bacteria has reached it's destination
	 * and therefore should be deleted
	 * 
	 */
	public boolean move(float delta) {
		timer += delta;
		
		int pos = (int) (timer / timePerStep);
		double between = (timer / timePerStep) - pos;
		
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

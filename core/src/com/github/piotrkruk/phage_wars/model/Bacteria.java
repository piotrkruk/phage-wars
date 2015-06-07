package com.github.piotrkruk.phage_wars.model;


import java.io.Serializable;
import java.util.List;
import java.util.Random;

/**
 * Class modeling one of the bacterias
 * which are sent from one cell to another
 *
 */


public class Bacteria implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Random rand = new Random();
	
	public static final double DEFAULT_RADIUS = 0.35;
	public static final double UNITS_PER_BACTERIA = 1.5;
	
	public static final int MAX_BACTERIAS = 100;
	
	private static final double MAX_WAIT_TIME = 0.6;
	private static final double TIME_PER_BLOCK = 0.35;
	
	public int posX, posY;
	
	public final int radius;
	
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
		
		this.radius = (int) (DEFAULT_RADIUS * gameStage.blockSize);
		
		path = gameStage.grid.getPath();
		
		this.posX = gameStage.grid.ptSrc.posX;
		this.posY = gameStage.grid.ptSrc.posY;
	}
	
	/**
	 * Moves the bacteria according to it's internal path
	 * @return true if the bacteria has reached it's destination
	 * and therefore should be deleted
	 * 
	 */
	public boolean move(float delta) {
		timer += delta;
		
		if (timer < 0)
			return false;
		
		double timePerStep = (TIME_PER_BLOCK / gameStage.blockSize) * gameStage.grid.pointDist;
		
		int pos = (int) (timer / timePerStep);
		double betweenRatio = (timer / timePerStep) - pos;
		
		/*
		 * This bacteria is always somewhere
		 * between path.get(pos) and path.get(pos+1)
		 */
		
		pos = Math.max(pos, 0);
		pos = Math.min(pos, path.size() - 1);
		
		Grid.Point start = path.get(pos);
		
		posX = start.posX;
		posY = start.posY;
		
		if (pos < path.size() - 1) {
			Grid.Point end = path.get(pos + 1);
			
			posX += betweenRatio * (end.posX - start.posX);
			posY += betweenRatio * (end.posY - start.posY);
		}
		
		return ( destination.isInside(posX, posY) || pos == path.size() - 1 );		
	}
}

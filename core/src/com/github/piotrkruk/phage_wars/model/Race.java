package com.github.piotrkruk.phage_wars.model;

import java.io.Serializable;

/**
 * Class containing information
 * about some particular race of cells
 * 
 */

public class Race implements Serializable {

	private static final long serialVersionUID = 1L;

	public final double givesAway;
	public final int minRadius;
	public final int maxRadius;
	public final int minInitUnits;
	public final int maxInitUnits;
	
	private final GameStage gameStage;
	
	/* 
	 * Radius and growth_for_area constants are given in respect to screen's block
	 * to provide portability for different screen sizes
	 * 
	 * However, all values anywhere else (including radius for a cell)
	 * are in respect to some particular grid size -
	 * using coord's as doubles would in the end require conversion in GameWindow
	 * and also a lot of changes in the code structure.
	 * The way it is, all conversions are done in Race class
	 * 
	 */
	private final static double DEFAULT_GIVES = 0.4;
	private final static double DEFAULT_MIN_RADIUS = 1.8;
	private final static double DEFAULT_MAX_RADIUS = 4.0;
	private final static int DEFAULT_MIN_INIT_UNITS = 20;
	private final static int DEFAULT_MAX_INIT_UNITS = 30;
	private final static double GROWTH_FOR_AREA = 0.005;
	
	// each race apart from an empty one has it's owner
	public final Player owner;
	
	public Race(GameStage gameStage, double gives, int minRad, int maxRad,
			int minInitUnits, int maxInitUnits, Player owner) {
		
		this.gameStage = gameStage;
		this.givesAway = gives;
		this.minRadius = minRad;
		this.maxRadius = maxRad;
		this.minInitUnits = minInitUnits;
		this.maxInitUnits = maxInitUnits;
		
		this.owner = owner;
	}
	
	/**
	 * @param strength - multiplier for creating harder opponents
	 * 
	 */
	public Race(GameStage gameStage, Player owner, double strength) {
		this(gameStage, DEFAULT_GIVES,
				(int) (gameStage.blockSize * DEFAULT_MIN_RADIUS), 
				(int) (gameStage.blockSize * DEFAULT_MAX_RADIUS),
				(int) (strength * DEFAULT_MIN_INIT_UNITS),
				(int) (strength * DEFAULT_MAX_INIT_UNITS), owner);
	}
	
	// constructs an empty race
	public Race(GameStage gameStage) {
		this(gameStage, DEFAULT_GIVES,
				(int) (gameStage.blockSize * DEFAULT_MIN_RADIUS), 
				(int) (gameStage.blockSize * DEFAULT_MAX_RADIUS), 0, 0, null);
	}
	
	public double givesAway(double units) {
		return givesAway * units;
	}
	
	public double growthRate(int radius) {
		return GROWTH_FOR_AREA * ( ((double) radius * radius) / gameStage.blockSize );
	}
}

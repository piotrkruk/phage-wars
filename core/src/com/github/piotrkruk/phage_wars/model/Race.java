package com.github.piotrkruk.phage_wars.model;

/**
 * Class containing information
 * about some particular race of cells
 * 
 */

public class Race {
	
	final double givesAway;
	final int minRadius;
	final int maxRadius;
	final int minInitUnits;
	final int maxInitUnits;
	
	final int blockSize;
	
	/* 
	 * Radius and growth_for_radius constants are given in respect to screen's block
	 * to provide portability for different screen sizes
	 * 
	 * However, all values anywhere else (including radius for a cell)
	 * are in respect to some particular grid size -
	 * using coord's as doubles would in the end require conversion in GameWindow
	 * and also a lot of changes in the code structure.
	 * The way it is, all conversions are done in Race class
	 * 
	 */
	final static double DEFAULT_GIVES = 0.4;
	final static double DEFAULT_MIN_RADIUS = 1.8;
	final static double DEFAULT_MAX_RADIUS = 4.0;
	final static int DEFAULT_MIN_INIT_UNITS = 20;
	final static int DEFAULT_MAX_INIT_UNITS = 30;
	final static double GROWTH_FOR_RADIUS = 0.6;
	
	// each race apart from an empty one has it's owner
	final Player owner;
	
	public Race(int blockSize, double gives, int minRad, int maxRad,
			int minInitUnits, int maxInitUnits, Player owner) {
		
		this.blockSize = blockSize;
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
	public Race(int blockSize, Player owner, double strength) {
		this(blockSize, DEFAULT_GIVES,
				(int) (blockSize * DEFAULT_MIN_RADIUS), 
				(int) (blockSize * DEFAULT_MAX_RADIUS),
				(int) (strength * DEFAULT_MIN_INIT_UNITS),
				(int) (strength * DEFAULT_MAX_INIT_UNITS), owner);
	}
	
	// constructs an empty race
	public Race(int blockSize) {
		this(blockSize, DEFAULT_GIVES,
				(int) (blockSize * DEFAULT_MIN_RADIUS), 
				(int) (blockSize * DEFAULT_MAX_RADIUS), 0, 0, null);
	}
	
	public double givesAway(double units) {
		return givesAway * units;
	}
	
	public double growthRate(int radius) {
		return GROWTH_FOR_RADIUS * ( ((double) radius) / blockSize );
	}
}

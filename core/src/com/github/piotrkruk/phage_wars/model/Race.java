package com.github.piotrkruk.phage_wars.model;

/**
 * Class containing information
 * about some particular race of cells
 * 
 */

public class Race {
	
	final double growthRate;
	final double givesAway;
	final int minRadius;
	final int maxRadius;
	final int maxInitUnits;
	
	// default values for race's parameters
	final static double DEFAULT_GROWTH = 5;
	final static double DEFAULT_GIVES = 0.3;
	final static int DEFAULT_MIN_RADIUS = 40;
	final static int DEFAULT_MAX_RADIUS = 120;
	final static int DEFAULT_MAX_INIT_UNITS = 100;
	
	// each race apart from an empty one has it's owner
	final Player owner;
	
	public Race(double growth, double gives, int minRad, int maxRad, int maxInitUnits, Player owner) {
		this.growthRate = growth;
		this.givesAway = gives;
		this.minRadius = minRad;
		this.maxRadius = maxRad;
		this.maxInitUnits = maxInitUnits;
		
		this.owner = owner;
	}
	
	public Race(Player owner) {
		this(DEFAULT_GROWTH, DEFAULT_GIVES, DEFAULT_MIN_RADIUS, DEFAULT_MAX_RADIUS,
				DEFAULT_MAX_INIT_UNITS, owner);
	}
	
	// constructs an empty race
	public Race() {
		this(DEFAULT_GROWTH, DEFAULT_GIVES, DEFAULT_MIN_RADIUS, DEFAULT_MAX_RADIUS, 0, null);
	}
	
	double givesAway(double units) {
		return givesAway * units;
	}
}

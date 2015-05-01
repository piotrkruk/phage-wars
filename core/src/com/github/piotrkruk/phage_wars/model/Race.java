package com.github.piotrkruk.phage_wars.model;

/**
 * Class containing information
 * about some particular race of cells
 * 
 */

public class Race {
	
	final double growthRate;
	final double givesAway;
	
	// default values for race's parameters
	final static double DEFAULT_GROWTH = 5;
	final static double DEFAULT_GIVES = 0.3;
	
	// each race has it's owner
	final Player owner;
	
	public Race(double growth, double gives, Player owner) {
		growthRate = growth;
		givesAway = gives;
		
		this.owner = owner;
	}
	
	public Race(Player owner) {
		this(DEFAULT_GROWTH, DEFAULT_GIVES, owner);
	}
	
	double givesAway(double units) {
		return givesAway * units;
	}
}

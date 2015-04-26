package model;

/**
 * Class containing information
 * about some particular race of cells
 * 
 */

public class Race {
	
	final int growthRate;
	final double givesAway;
	
	// default values for race's parameters
	final static int DEFAULT_GROWTH = 5;
	final static double DEFAULT_GIVES = 0.3;
	
	// each race has it's owner
	final Player owner;
	
	public Race(int growth, double gives, Player owner) {
		growthRate = growth;
		givesAway = gives;
		
		this.owner = owner;
	}
	
	public Race(Player owner) {
		this(DEFAULT_GROWTH, DEFAULT_GIVES, owner);
	}
	
	int givesAway(int units) {
		return (int) givesAway * units;
	}
}

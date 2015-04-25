package model;

/**
 * Class containing information
 * about some particular race of cells
 * 
 */

public class Race {
	
	/**
	 * Initial number of units
	 * a cell of this race has
	 * if it's size is s
	 * 
	 */
	int initUnits(int s) {
		return 10 * s;
	}
	
	/**
	 * Growth rate per time unit
	 * 
	 */
	int growthRate() {
		return 3;
	}
}

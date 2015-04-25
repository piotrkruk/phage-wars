package model;

/**
 * Class representing an abstract model
 * of one cell
 * 
 */

public class Cell {
	public int posX, posY, size;
	public int units;
	
	public final Race race;
	
	
	public Cell(Race race) {
		this.race = race;
		units = race.initUnits(size);
	}
	
	public void grow() {
		units += race.growthRate();
	}
}

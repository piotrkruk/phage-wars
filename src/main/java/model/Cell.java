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
	
	
	public Cell(int size, Race race) {
		this.size = size;
		this.race = race;
		
		units = race.initUnits(size);
	}
	
	public void grow() {
		units += race.growthRate();
	}
	
	public int giveUnits() {
		int toGive = units / 3;
		
		units -= toGive;
		return toGive;
	}
	
	public static int square(int x) {
		return x * x;
	}
	
	public static int distSquared(int x1, int y1, int x2, int y2) {
		return square(x1 - x2) + square(y1 - y2);
	}
	
	/**
	 * A function checking if some event
	 * (ie a mouse click)
	 * happened inside a cell
	 * 
	 */
	public boolean isInside(int x, int y) {
		return distSquared(x, y, this.posX, this.posY) <= square(size);
	}
	
	public boolean doesCollide(Cell c) {
		int dist = distSquared(posX, posY, c.posX, c.posY);
		
		return dist <= square(size + c.size);
	}
}

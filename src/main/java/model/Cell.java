package model;


/**
 * Class representing an abstract model
 * of one cell
 * 
 */

public class Cell {
	private static int idCnt = 0;
	public final int id;
	
	public int posX, posY, radius;
	public int units;
	
	public final Race race;
	public Player owner;
	
	public boolean selected = false;
	
	
	public Cell(int x, int y, int radius, int initUnits, Race race, Player owner) {
		this.id = idCnt++;
		
		this.posX = x;
		this.posY = y;
		this.radius = radius;
		
		this.race = race;
		this.owner = owner;
		this.units = initUnits;
	}
	
	public void grow() {
		units += race.growthRate;
	}
	
	public void addUnits(int units, Player from) {
		if (from.id == owner.id)
			this.units += units;
		else {
			this.units -= units;
			
			if (this.units <= 0) {
				this.units *= -1;
				this.owner = from;
			}
		}
	}
	
	public int sendUnits() {
		int toGive = race.givesAway(units);
		
		units -= toGive;
		deselect();
		
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
		return distSquared(x, y, this.posX, this.posY) <= square(radius);
	}
	
	public boolean doesCollide(Cell c) {
		int dist = distSquared(posX, posY, c.posX, c.posY);
		
		return dist <= square(radius + c.radius);
	}
	
	public void select() {
		selected = true;
	}
	
	public void deselect() {
		selected = false;
	}
}

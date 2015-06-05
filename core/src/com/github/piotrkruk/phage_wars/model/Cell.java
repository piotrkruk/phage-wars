package com.github.piotrkruk.phage_wars.model;

import java.io.Serializable;


/**
 * Class representing an abstract model
 * of one cell
 * 
 */

public class Cell implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int MAX_UNITS = 100000000;
	
	private static int idCnt = 0;
	public final int id;
	
	public int posX, posY, radius;	
	public double units;
	
	public Race race;
	public volatile Player owner;
	
	public volatile boolean selected = false;
	
	
	public Cell(int x, int y, int radius, double initUnits, Race race, Player owner) {
		this.id = idCnt++;
		
		this.posX = x;
		this.posY = y;
		this.radius = radius;
		
		this.race = race;
		this.owner = owner;
		this.units = Math.min(initUnits, MAX_UNITS);
	}
	
	public synchronized int getUnits() {
		return (int) units;
	}
	
	/**
	 * Grows the cell
	 * @param delta - time since last grow
	 * 
	 */
	public void grow(float delta) {
		units += delta * race.growthRate(radius);
		
		fixUnits();
	}
	
	/**
	 * Absorbs units
	 * 
	 */
	public synchronized void addUnits(double units, Player from) {
		if (from == owner)
			this.units += units;
		else {
			this.units -= units;
			
			if (this.units <= 0) {
				this.deselect();
				Player previousOwner = this.owner;
				
				this.units *= -1;
				this.owner = from;
				
				if (previousOwner != null)
					previousOwner.ownCount--;
				
				this.owner.ownCount++;
			}
		}
		
		fixUnits();
	}
	
	public synchronized void fixUnits() {
		units = Math.min(units, MAX_UNITS);
	}
	
	/**
	 * @return - amount of units sent
	 * 
	 */
	public synchronized double sendUnits() {
		double toGive = race.givesAway(units);
		
		units -= toGive;
		deselect();
		
		return toGive;
	}
	
	/**
	 * A function checking if some event
	 * (ie a mouse click)
	 * happened inside a cell
	 * 
	 */
	public boolean isInside(int x, int y) {
		return Grid.distSquared(x, y, this.posX, this.posY) <= Math.pow(radius, 2);
	}
	
	public static int distSquared(Cell a, Cell b) {
		return Grid.distSquared(a.posX, a.posY, b.posX, b.posY);
	}
	
	public boolean doesCollide(Cell c) {
		int dist = Grid.distSquared(posX, posY, c.posX, c.posY);
		
		return dist <= Math.pow(radius + c.radius, 2);
	}
	
	public synchronized void select() {
		selected = true;
	}
	
	public synchronized void deselect() {
		selected = false;
	}
}

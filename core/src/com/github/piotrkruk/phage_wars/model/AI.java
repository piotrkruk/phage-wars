package com.github.piotrkruk.phage_wars.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Class handling the AI
 * which controls one of the players
 * by making appropriate moves and sleeping in between
 *
 */

public class AI implements Runnable {
	
	private static final int DEFAULT_MIN_MOVE_DELAY = 3000;
	private static final int DEFAULT_MAX_MOVE_DELAY = 4000;
	private static final int DEFAULT_RETRIAL_TIME = 500;
	
	private final int maxMoveDelay;
	private final int minMoveDelay;
	private static final Random rand = new Random();
	
	private final GameStage game;
	public final Player player;
	public double strength;
	
	public AI(GameStage game, Player player, double strength) {
		this.game = game;
		this.player = player;
		this.strength = strength;
		
		minMoveDelay = (int) (DEFAULT_MIN_MOVE_DELAY / strength);
		maxMoveDelay = (int) (DEFAULT_MAX_MOVE_DELAY / strength);
	}
	
	/**
	 * Selects subset of the cells
	 * owned by this player
	 * suitable for a given target
	 * 
	 */
	private void selectSources(Cell target) {

		int minDistance = Integer.MAX_VALUE;
		double avgDistance = 0.0;
		
		for (Cell c : game.cells)
			if (c.owner == player) {
				int distance = Cell.distSquared(c, target);
				
				minDistance = Math.min(minDistance, distance);
				avgDistance += distance;
			}
		
		avgDistance /= player.ownCount;
				
		if (target.owner == null) {
			/*
			 * If target is an empty cell
			 * then just send from the closest source
			 */
			
			for (Cell c : game.cells)
				if (c.owner == player && Cell.distSquared(c, target) == minDistance) {
					c.select();
					return ;
				}
		}
		else {
			/*
			 * If target is non-empty
			 * choose sources based on distance and amount of units
			 */
			
			for (Cell c : game.cells)
				if (c.owner == player) {
					double probability = 0.5;
					
					probability *= avgDistance / Cell.distSquared(c, target);
					probability *= c.units / target.units;
					
					if (rand.nextDouble() < probability)
						c.select();
				}
		}
	}
	
	/**
	 * Chooses a target cell
	 * taking into account radius of the cell
	 * the amount of units need to take control of it 
	 * and position on the board
	 * 
	 */
	private Cell selectTarget() {
		class Target {
			Cell c;
			double weight;
			
			Target(Cell c, double weight) {
				this.c = c;
				this.weight = weight;
			}
		}
		
		List <Target> targets = new LinkedList <Target> ();
		
		double allOwnedUnits = 0,
			   avgDist = 0;
		
		Cell largestOwned = null;
		
		for (Cell c : game.cells)
			if (c.owner == player) {
				allOwnedUnits += c.units;
				
				if (largestOwned == null || c.units > largestOwned.units)
					largestOwned = c;
			}
		
		if (largestOwned == null)
			return null;
		
		for (Cell c : game.cells) if (c.owner != player)
			avgDist += Cell.distSquared(c, largestOwned);
		
		int targetCount = game.cells.size() - player.ownCount;
		
		if (targetCount == 0) // no targets
			return null;
		else if (targetCount >= player.ownCount) {
			/*
			 * add possibility of not making a move
			 * (but only if we haven't overrun the stage yet)
			 */
			
			targets.add( new Target(null, 2.0) );
		}
		
		avgDist /= (double) targetCount;
		
		for (Cell c : game.cells)
			if (c.owner != player) {
				
				double weight,
					   radius = c.radius / game.blockSize,
					   unitsRatio = c.units / allOwnedUnits,
					   distSum = Cell.distSquared(c, largestOwned);
						
				weight = radius;
				weight /= 1 + unitsRatio;
				weight *= avgDist / distSum;
				
				if (c.owner == null)
					weight += 0.6;
				
				weight = Math.max(weight, 0.1);
				
				targets.add( new Target(c, weight) );
			}
		
		double sumOfWeights = 0;
		
		for (Target t : targets)
			sumOfWeights += t.weight;
		
		double w = sumOfWeights * rand.nextDouble();
		
		for (Target t : targets) {
			w -= t.weight;
			
			if (w < 0)
				return t.c;
		}
		
		return null;
	}
	
	/**
	 * Makes a random move of this player,
	 * @return true if the move was made successfully
	 * 			(decision to make no moves counts as a successful attempt)
	 * 
	 */
	public boolean move() {
		Cell target = selectTarget();
		
		if (target != null) {
			selectSources(target);
			return game.send(target, player);
		}
		else
			return true;
	}

	@Override
	public void run() {		
		synchronized (game) {
			try {
				game.wait(minMoveDelay);
			} catch (InterruptedException e) {}
			
			while (game.isRunning() && player.isPlaying()) {
				boolean result = true;
				
				if (!game.paused && player.ownCount > 0)
					result = move();
				
				try {
					int waitTime;
					
					if (result)
						waitTime = minMoveDelay + rand.nextInt(maxMoveDelay - minMoveDelay);
					else
						waitTime = DEFAULT_RETRIAL_TIME;
					
					game.wait(waitTime);
				} catch (InterruptedException e) {}
			}
		}
	}

}

package com.github.piotrkruk.phage_wars.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Class handling the AI
 * controlling some player
 *
 */

public class AI implements Runnable {
	
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
		
		minMoveDelay = (int) (2000 / strength);
		maxMoveDelay = (int) (3000 / strength);
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
			   avgDistSum = 0;
		
		for (Cell c : game.cells)
			if (c.owner == player)
				allOwnedUnits += c.units;
		
		for (Cell c : game.cells) if (c.owner != player)
			for (Cell d : game.cells) if (d.owner == player)
				avgDistSum += Cell.distSquared(c, d);
		
		int targetCount = game.cells.size() - player.ownCount;
		
		if (targetCount == 0) // no targets
			return null;
		else if (targetCount >= player.ownCount) {
			/*
			 * add possibility of not making a move
			 * but only if we haven't overrun the stage yet
			 * 
			 */
			
			targets.add( new Target(null, 2.0) );
		}
		
		avgDistSum /= (double) targetCount;
		
		for (Cell c : game.cells)
			if (c.owner != player) {
				
				double weight,
					   radius = c.radius / game.BLOCK_SIZE,
					   unitsRatio = c.units / allOwnedUnits,
					   distSum = 0;
				
				for (Cell d : game.cells)
					if (d.owner == player)
						distSum += Cell.distSquared(c, d);
						
				weight = radius;
				weight /= 1 + unitsRatio;
				weight *= avgDistSum / distSum;
				
				if (c.owner == null)
					weight += 1.0;
				
				weight = Math.max(weight, 0.2);
				
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
	 * Makes a random move of this player
	 */
	public void move() {
		Cell target = selectTarget();
		
		if (target != null) {
			selectSources(target);
			game.send(target, player);
		}
	}

	@Override
	public void run() {		
		synchronized (game) {
			try {
				game.wait(minMoveDelay);
			} catch (InterruptedException e1) {}
			
			while (game.isRunning() && player.isPlaying()) {
				if (!game.paused)
					move();
				
				try {
					game.wait( minMoveDelay + rand.nextInt(maxMoveDelay - minMoveDelay) );
				} catch (InterruptedException e) {}
			}
		}
	}

}

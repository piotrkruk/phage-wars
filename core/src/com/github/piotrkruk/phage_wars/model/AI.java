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
	
	private static final int MAX_MOVE_DELAY = 2500;
	private static final int MIN_MOVE_DELAY = 1500;
	private static final Random rand = new Random();
	
	private final GameStage game;
	public final Player player;
	
	public AI(GameStage game, Player player) {
		this.game = game;
		this.player = player;
	}
	
	/**
	 * Selects random subset of the cells
	 * owned by this player
	 * 
	 */
	private void selectSources() {
		for (Cell c : game.cells) {
			if (c.owner == player && rand.nextBoolean())
				c.select();
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
		
		// add possibility of not making a move
		targets.add( new Target(null, 2.0) );
		
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
			selectSources();
			game.send(target, player);
		}
	}

	@Override
	public void run() {		
		synchronized (game) {
			try {
				game.wait(MIN_MOVE_DELAY);
			} catch (InterruptedException e1) {}
			
			while (game.isRunning() && player.isPlaying()) {
				if (!game.paused)
					move();
				
				try {
					game.wait( MIN_MOVE_DELAY + rand.nextInt(MAX_MOVE_DELAY - MIN_MOVE_DELAY) );
				} catch (InterruptedException e) {}
			}
		}
	}

}

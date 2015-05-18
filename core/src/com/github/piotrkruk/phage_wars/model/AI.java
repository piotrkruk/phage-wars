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
	
	private static final int MAX_MOVE_DELAY = 3000;
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
	 */
	private void select() {
		for (Cell c : game.cells) {
			if (c.owner == player && rand.nextBoolean())
				c.select();
		}
	}
	
	/**
	 * Makes a random move of this player
	 */
	public void move() {
		select();
		
		List <Cell> targets = new LinkedList <Cell> ();
		
		for (Cell c : game.cells)
			if (c.owner != player)
				targets.add(c);
		
		if (!targets.isEmpty()) {
			Cell target = targets.get( rand.nextInt(targets.size()) );
			game.send(target, player);
		}
	}

	@Override
	public void run() {		
		synchronized (game) {
			while (game.isRunning() && player.isPlaying()) {				
				try {
					game.wait( MIN_MOVE_DELAY + rand.nextInt(MAX_MOVE_DELAY - MIN_MOVE_DELAY) );
				} catch (InterruptedException e) {}
				
				if (!game.paused)
					move();
			}
		}
	}

}

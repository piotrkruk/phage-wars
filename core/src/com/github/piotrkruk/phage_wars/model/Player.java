package com.github.piotrkruk.phage_wars.model;

/**
 * Represents a player -
 * human playing the game is one,
 * AI's are others
 *
 */

public class Player {
	private static int idCnt = 0;
	
	public final int id;
	public int ownCount = 0; // number of owned cells at some moment
	public boolean isActive = false; // does this player keep the game running while being alive
							 		 // (only human players by default)
	
	public Player() {
		id = idCnt++;
	}
	
	public void setActive() {
		isActive = true;
	}
	
	public boolean isPlaying() {
		return (ownCount != 0);
	}
}

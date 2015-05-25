package com.github.piotrkruk.phage_wars.model;

import java.io.Serializable;




/**
 * Represents a player -
 * human playing the game is one,
 * AI's are others
 *
 */

public class Player implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static int idCnt = 0;
	
	public final int id;
	public int imageId;
	
	public volatile int ownCount = 0; // number of owned cells at some moment
	public volatile int bacteriaCount = 0; // number of owned bacterias
	
	public boolean isActive = false; // does this player keep the game running while being alive
							 		 // (only human players by default)
	
	public Player() {
		this(idCnt);
	}
	
	public Player(int imageId) {
		id = idCnt++;
		this.imageId = imageId;
	}
	
	public void setActive() {
		isActive = true;
	}
	
	public synchronized boolean isPlaying() {
		return (ownCount != 0 || bacteriaCount != 0);
	}
}

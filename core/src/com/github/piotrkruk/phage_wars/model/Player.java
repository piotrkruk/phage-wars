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
	
	public Player() {
		id = idCnt++;
	}
}

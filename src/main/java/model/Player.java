package model;

/**
 * Represents a player -
 * human playing the game is one,
 * AI's are others
 *
 */

public class Player {
	private static int idCnt = 0;
	
	final int id;
	
	public Player() {
		id = idCnt++;
	}
}

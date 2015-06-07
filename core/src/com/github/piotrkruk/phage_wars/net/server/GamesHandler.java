package com.github.piotrkruk.phage_wars.net.server;

import java.util.List;
import java.util.UUID;

public interface GamesHandler {
	UUID createNewGame(Player host);
	boolean joinGame(UUID gameId, Player guest);
	void leaveGame(UUID gameId, Player guest);
	Player getHost(UUID gameId);
	List<Player> getGuests(UUID gameId);
	List<UUID> getGames();
}

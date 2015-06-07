package com.github.piotrkruk.phage_wars.net.server;

import java.io.IOException;
import java.util.UUID;

public interface Player {
	UUID getPlayerId();
	void send(byte[] data) throws IOException;
}

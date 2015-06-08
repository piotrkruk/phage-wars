package com.github.piotrkruk.phage_wars.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Server implements GamesHandler {
	Set<UUID> games = new HashSet<UUID>();
	private List<UUID> playersList = new ArrayList<UUID>();
	@SuppressWarnings("unused")
	private Map<UUID, ClientHandler> players = new HashMap<UUID, ClientHandler>();
	@SuppressWarnings("unused")
	private Map<UUID, ClientHandler> hosts = new HashMap<UUID, ClientHandler>();

	public static void main(String[] args) {
		Server server = new Server();
		try {
			server.start(4444);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	public void start(int port) throws IOException {
		ServerSocket serverSocket;
		serverSocket = new ServerSocket(port);

		while (true) {
			try {
				Socket socket = serverSocket.accept();
				ClientHandler clientHandler = new ClientHandler(socket, this);
				clientHandler.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public UUID createNewGame(Player host) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean joinGame(UUID gameId, Player guest) {
		return false;
	}

	@Override
	public void leaveGame(UUID gameId, Player guest) {
		// TODO Auto-generated method stub
	}

	@Override
	public Player getHost(UUID gameId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Player> getGuests(UUID gameId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UUID> getGames() {
		return playersList;
	}
	
}

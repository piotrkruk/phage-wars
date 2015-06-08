package com.github.piotrkruk.phage_wars.net.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientHandler extends Thread implements Player {
	private static final String FAIL = "FAIL";
	private static final String OK = "OK";
	private static final String JOIN = "JOIN";
	private static final String CREATE = "CREATE";
	private BufferedInputStream inputStream;
	private BufferedOutputStream outputStream;
	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;
	private GamesHandler gamesHandler;

	// TODO set playerId
	private UUID playerId;
	private UUID roomId;
	private boolean host;

	public ClientHandler(Socket socket, GamesHandler gamesHandler)
			throws IOException {
		System.out.println("new client handler");
		this.gamesHandler = gamesHandler;
		inputStream = new BufferedInputStream(socket.getInputStream());
		outputStream = new BufferedOutputStream(socket.getOutputStream());

		objectInputStream = new ObjectInputStream(socket.getInputStream());
		objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

	}

	@Override
	public void run() {
		try {
			handleClient();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			cleanUp();
		}
	}

	private void handleClient() throws Exception {
		System.out.println("Sending Games List");
		sendGamesList();
		setUpGame();

		byte[] data = new byte[1024];

		while (true) {
			try {
				inputStream.read(data);
			} catch (IOException e) {
				break;
			}

			if (host) {
				List<Player> playersList = gamesHandler.getGuests(roomId);
				for (Player player : playersList) {
					try {
						player.send(data);
					} catch (IOException e) {
						e.printStackTrace();
						// Remove player when communication lost
						gamesHandler.leaveGame(roomId, player);
					}
				}
			} else {
				try {
					gamesHandler.getHost(roomId).send(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void cleanUp() {
		try {
			inputStream.close();
			outputStream.close();
			
			objectInputStream.close();
			objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean setUpGame() {
		String command;
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		try {
			command = reader.readLine();
		} catch (IOException e) {
			return false;
		}

		if (CREATE.equals(command)) {
			return createGame();
		} else if (JOIN.equals(command)) {
			try {
				String roomId = reader.readLine();
				return joinGame(command, roomId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private void sendGamesList() throws IOException {
		// TODO omit creating two lists
		ArrayList<UUID> games = new ArrayList<UUID>(gamesHandler.getGames());

		objectOutputStream.writeObject(games);
	}

	private boolean joinGame(String command, String roomIdString) {
		roomId = UUID.fromString(roomIdString);
		try {
			if (gamesHandler.joinGame(roomId, this)) {
				write(OK);
				return true;
			} else {
				write(FAIL);
				return false;
			}
		} catch (IOException e) {
			gamesHandler.leaveGame(roomId, this);
			return false;
		}
	}

	private boolean createGame() {
		UUID gameId = gamesHandler.createNewGame(this);
		try {
			write(gameId.toString());
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private void write(String data) throws IOException {
		write(data.getBytes());
	}

	private synchronized void write(byte[] bytes) throws IOException {
		outputStream.write(bytes);
		outputStream.flush();
	}

	@Override
	public UUID getPlayerId() {
		return playerId;
	}

	@Override
	public void send(byte[] data) throws IOException {
		outputStream.write(data);
	}

}

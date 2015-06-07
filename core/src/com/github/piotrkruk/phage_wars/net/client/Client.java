package com.github.piotrkruk.phage_wars.net.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Client {
	private Socket socket;
	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;

	// private BufferedInputStream inputStream;
	// private BufferedOutputStream outputStream;

	public void connect() throws UnknownHostException, IOException {
		socket = new Socket("localhost", 4444);

		// inputStream = new BufferedInputStream(socket.getInputStream());
		// outputStream = new BufferedOutputStream(socket.getOutputStream());

		System.out.println("socket created");
		// InputStream socketInput = socket.getInputStream();
		// objectInputStream = new ObjectInputStream(socketInput);
		objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		System.out.println("output stream created");
		objectInputStream = new ObjectInputStream(socket.getInputStream());
		System.out.println("input stream created");
	}

	public List<UUID> getGamesList() throws ClassNotFoundException, IOException {
		 List<UUID> games = (List<UUID>) objectInputStream.readObject();
		 return games;
	}

	public static void main(String[] args) {
		System.out.println("client");
		Client client = new Client();
		try {
			client.connect();

			System.out.println("connected");
			List<UUID> games = client.getGamesList();
			System.out.println(Arrays.toString(games.toArray()));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("other thing");

	}

}

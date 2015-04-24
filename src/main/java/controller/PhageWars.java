package controller;

import view.MainWindow;

/**
 * Class controlling basic UI
 * and having a main() method
 * starting the game
 *
 */

public class PhageWars {
	
	private MainWindow mainWindow;
	
	public void start() {
		mainWindow = new MainWindow(this);
		mainWindow.start();
	}

	public static void main(String[] args) {
		new PhageWars().start();
	}
}

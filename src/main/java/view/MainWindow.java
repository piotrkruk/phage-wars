package view;


import controller.PhageWars;

import javax.swing.JFrame;

/**
 * Class representing the main window
 * of the game
 * 
 */

public class MainWindow {

	private JFrame frame;
	
	@SuppressWarnings("unused")
	private PhageWars wars;
	
	public MainWindow(PhageWars wars) {
		this.wars = wars;
	}
	
	public void start() {
		frame = new JFrame();
		
		frame.setTitle("Baczus");
		frame.setSize(1000, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {}
		
		frame.dispose();
	}
}

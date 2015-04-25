package view;


import controller.PhageWars;

import javax.swing.*;


/**
 * Class representing the main window
 * of the game
 * 
 */

public class MainWindow implements Runnable {

	private volatile boolean running;
	
	private JFrame frmPhageWars;
	private PhageWars wars;
	
	public MainWindow(PhageWars wars) {
		this.wars = wars;
	}
	
	public void start() {
		running = true;
		new Thread(this).start();
	}
	
	public void stop() {
		running = false;
	}
	

	public void run() {
		frmPhageWars = new JFrame();
		frmPhageWars.setResizable(false);
		
		frmPhageWars.setTitle("Phage Wars");
		frmPhageWars.setSize(1200, 800);
		frmPhageWars.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel lblBackground = new JLabel();
		lblBackground.setIcon(new ImageIcon(MainWindow.class.getResource("/background.jpg")));
		frmPhageWars.getContentPane().add(lblBackground);

		frmPhageWars.setVisible(true);
				
		int time = 0;
		while (running) {
			frmPhageWars.setTitle("Phage Wars " + " time: " + time++);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		
		frmPhageWars.dispose();
	}
}

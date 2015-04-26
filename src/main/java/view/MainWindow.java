package view;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;


/**
 * Class representing the main window
 * of the game
 * 
 */

public class MainWindow implements Runnable {

	private volatile boolean running;
	
	private JFrame frame;
	
	public void start() {
		running = true;
		new Thread(this).start();
	}
	
	public void stop() {
		running = false;
	}
	

	@SuppressWarnings("serial")
	public void run() {
		frame = new JFrame();
		frame.setResizable(false);
		
		frame.setTitle("Phage Wars");
		frame.setSize(1200, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final Image background =
			new ImageIcon(MainWindow.class.getResource("/background.jpg")).getImage();
		
        JPanel panel = new JPanel() {
        	protected void paintComponent(Graphics g){
        		super.paintComponent(g);
        		g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
        	}        	
        };
        
        GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 1008, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
		);
		
        JButton btnNewGame = new JButton("New Game");
		JButton btnSettings = new JButton("Settings");
		
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				running = false;				
			}			
		});
		
		
		
		GroupLayout layout = new GroupLayout(panel);
		
		layout.setHorizontalGroup(
			layout.createParallelGroup(Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap(844, Short.MAX_VALUE)
					.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnExit, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnSettings, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnNewGame, GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE))
					.addGap(84))
		);
		
		layout.setVerticalGroup(
			layout.createParallelGroup(Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGap(373)
					.addComponent(btnNewGame, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
					.addGap(18)
					.addComponent(btnSettings, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
					.addGap(18)
					.addComponent(btnExit, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
					.addGap(125))
		);
		panel.setLayout(layout);
		

		frame.getContentPane().setLayout(groupLayout);		
		frame.setVisible(true);
				
		int time = 0;
		while (running) {
			frame.setTitle("Phage Wars " + " time: " + time++);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		
		frame.dispose();
	}
}

package com.github.piotrkruk.phage_wars.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Class modelling the grid
 * in which bacterias move
 * and responsible for setting paths for them
 * 
 * Usage:
 * 		- execute runSearch(source, destination)
 * 		- for each bacteria get path from getPath()
 *
 */


public class Grid {
	
	private final int avoidBy;
		// avoid the border of a cell by this margin
	
	public final int width, height;
	public final int widthInPoints, heightInPoints;
	
	public final int pointDist;
	
	private final GameStage game;
	
	// parameters from the last run BFS
	private Point destination;
	private int distance[][];
	private Point src[][];
	private boolean locked[][];
	
	public Grid(int width, int height, int blockSize, GameStage game) {
		this.game = game;
		this.width = width;
		this.height = height;
		this.pointDist = blockSize / 5;
		this.avoidBy = blockSize / 2;
		
		this.widthInPoints = width / pointDist;
		this.heightInPoints = height / pointDist;
		
		distance = new int[this.widthInPoints][this.heightInPoints];
		src = new Point[this.widthInPoints][this.heightInPoints];
		locked = new boolean[this.widthInPoints][this.heightInPoints];
		
		clear();
	}
	
	public static int distSquared(int x1, int y1, int x2, int y2) {
		return (int) Math.pow(x1 - x2, 2) + (int) Math.pow(y1 - y2, 2);
	}
	
	/**
	 * Gets a randomly disturbed version
	 * of the shortest path from last run search
	 */
	public List <Point> getPath() {
		List <Point> list = new LinkedList <Point> ();
		Point temp = destination;
		
		while (temp.getDistance() != 0) {
			list.add(temp.clone()); // clones the points for safety
			temp = temp.getSource();
		}
		
		Collections.reverse(list);
		
		Random rand = new Random();
		
		for (Point pt : list)
			pt.moveRandomly(rand);
		
		return list;
	}
	
	/**
	 * Clears the search params
	 */
	private void clear() {
		for (int i = 0; i < this.widthInPoints; i++)
			for (int j = 0; j < this.heightInPoints; j++) {
				distance[i][j] = -1;
				src[i][j] = null;
				
				locked[i][j] = false;
			}
	}
	
	/**
	 * Runs BFS to find the path
	 * from 'source' to 'destination'
	 * 
	 * @return true if the path was found
	 */
	public boolean runSearch(Cell source, Cell destination) {
		clear();
		
		this.destination = new Point(destination.posX, destination.posY).roundToGrid();
		lock(Arrays.asList(source, destination));
		
		Queue <Point> queue = new LinkedList <Point> ();
		queue.offer( new Point(source.posX, source.posY).roundToGrid() );
		
		/*
		 * The allowed moves are as follows:
		 * 		- two steps in any of the four directions
		 * 		- chess knight moves in any of the eight directions
		 * 
		 * This set of moves experimentally proved to generate the smoothest paths
		 * 
		 */
		
		int movesX[] = {-2, 0, 2, 0,
						-2, -2, -1, 1,
						2, 2, 1, -1},
		
			movesY[] = {0, 2, 0, -2,
						-1, 1, 2, 2,
						1, -1, -2, -2};
		
		while (!queue.isEmpty()) {
			Point pt = queue.poll();
			
			if (pt.equals(this.destination))
				break;
			
			int idX = pt.posX / pointDist,
				idY = pt.posY / pointDist;
			
			for (int move = 0; move < movesX.length; move++) {
				int newX = idX + movesX[move],
					newY = idY + movesY[move];
				
				if (newX < 0 || newX >= widthInPoints || newY < 0 || newY >= heightInPoints)
					continue;
				
				if (!locked[newX][newY] && (distance[newX][newY] == -1)) {
					
					distance[newX][newY] = distance[idX][idY] + 1;
					src[newX][newY] = pt;
					
					queue.offer( new Point(pointDist * newX, pointDist * newY) );
				
				}
			}
		}
		
		return (this.destination.getSource() != null);
	}
	
	/**
	 * Locks points
	 * that are currently inside some cell
	 * not counting the Cells in 'except'
	 */
	public void lock(List <Cell> except) {
		for (Cell c : game.cells) if (!except.contains(c)) {
		
			for (int i = 0; i < widthInPoints; i++)
				for (int j = 0; j < heightInPoints; j++) {
					int dist = distSquared(i * pointDist, j * pointDist, c.posX, c.posY);
					
					if (dist <= Math.pow(c.radius + avoidBy, 2))
						locked[i][j] = true;
				}
		}
	}

	/**
	 * Represents one point on the board
	 * not necessarily lying on the grid
	 */
	public class Point implements Cloneable {
		public static final int DISTURB_BY = 7;
		public int posX, posY;
		
		public Point(int posX, int posY) {
			this.posX = posX;
			this.posY = posY;
		}
		
		public Point roundToGrid() {
			posX = pointDist * (posX / pointDist);
			posY = pointDist * (posY / pointDist);
			
			return this;
		}
		
		public int getDistance() {
			return distance[posX/pointDist][posY/pointDist];
		}
		
		public Point getSource() {
			return src[posX/pointDist][posY/pointDist];
		}
		
		@Override
		public Point clone() {
			return new Point(posX, posY);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null || !obj.getClass().equals(Point.class))
				return false;
			else {
				Point pt = (Point) obj;
				
				return (pt.posX == this.posX &&
						pt.posY == this.posY);
			}
		}
		
		public void moveRandomly(Random rand) {
			posX += rand.nextInt(DISTURB_BY);
			posX -= pointDist / 2;
			
			posY += rand.nextInt(DISTURB_BY);
			posY -= pointDist / 2;
		}
	}
	
}

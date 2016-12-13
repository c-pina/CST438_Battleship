package battleship;

import java.util.Hashtable;
import java.util.Set;

public class Player {
	/*
	 * Guess tracking
	 */
	protected static final int GRID_WIDTH = 10; // the number of squares in a row/column
	private Point[] guesses;
	private int numGuesses;
	
	/*
	 * Ships
	 */
	private Hashtable<String, Ship> ships;
	
	public Player() {
		guesses = new Point[GRID_WIDTH * GRID_WIDTH];
		numGuesses = 0;
		ships = new Hashtable<String, Ship>();
	}
	
	public int getNumGuesses() {
		return numGuesses;
	}
	
	public Point[] getGuesses() {
		return guesses;
	}
	
	public boolean addGuess(Point p) {
		if (numGuesses < (GRID_WIDTH * GRID_WIDTH)) {
			guesses[numGuesses] = p;
			numGuesses++;
			return true;
		} else {
			// too many guesses
			return false;
		}
	}
	
	public Hashtable<String, Ship> getShips() {
		return ships;
	}
	
	public void setShips(Hashtable<String, Ship> ships) {
		this.ships = ships;
	}
	
	public boolean hasShipOnPoint(Point point) {
		Ship ship = getShipOnPoint(point);
		
		if (ship == null) { 
			return false;
		} else {
			return true;
		}
	}
	
	public Ship getShipOnPoint(Point point) {
		// Create a set of keys to iterate through the ships
		Set<String> keys = ships.keySet();
		for(String key : keys) {
			Ship ship = ships.get(key);
			
			if (ship.getLocation() != null) {
				Point[] shipPoints = ship.getLocation().getPoints();
			
				for (Point p : shipPoints) {
					if (point.equals(p)) return ship;
				}
			}
		}

		// No ship on point
		return null;
	}
}

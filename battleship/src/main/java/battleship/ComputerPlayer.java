package battleship;

import java.util.Random;

public class ComputerPlayer extends Player{
	private Point lastGuess;
	
	public ComputerPlayer() {		
		super();
	}
	
	public Point makeGuess() {
		lastGuess = makeRandomGuess();
		this.addGuess(lastGuess);
		return lastGuess;
	}
	
	public Point getLastGuess() {
		// Returns null if no guesses have been made
		return lastGuess;
	}
	
	private Point makeRandomGuess() {
		// Loop until we find a random unguessed point
		while (true) {
			// Pick random coordinates to create a Point
			Random rand = new Random();			
			int x = rand.nextInt(GRID_WIDTH);
			int y = rand.nextInt(GRID_WIDTH);
			Point guess = new Point(x,y,false);
			
			// Check if this Point has already been guessed
			boolean alreadyGuessed = false;
			if(this.getNumGuesses() != 0) {
   			for (int i = 0; i < this.getNumGuesses(); i++) {
   				if (this.guesses[0].equals(guess)) {
   					alreadyGuessed = true;
   					break;
   				}
   			}
			}
			
			// Return the point if not already guessed, otherwise loop again
			if (alreadyGuessed == false) {
				return guess;
			}
		}
		
	}
}
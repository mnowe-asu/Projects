package core;

import java.util.Random;


public class Connect4ComputerPlayer {
	Connect4 game = new Connect4();
	String player;
	
	/** Returns the column for the computer move based on random number
	 * 
	 * @return column for computer move 
	 */
	public int compMove() {
		System.out.println();
		System.out.println("Player " + getCompPlayer() + " please enter a column to drop your piece\n");
		System.out.println("Thinking....");
		int col = createRandom();
		return col;
	}
	/** Creates a random number for compMove
	 * 
	 * @return random number that is used for a column
	 */
	public int createRandom() {
		Random rand = new Random();
		int randInt = rand.nextInt(7);
		return randInt;
}
	
	public void setCompPlayer(String player) {
		this.player = player;
	}
	public String getCompPlayer() {
		return player;
	}
}

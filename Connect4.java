package core;
/**
 * @author Matthew Nowe
 * @version 1.0 September 4, 2019
 * 
 */

import java.util.*;

public class Connect4 {
	private String[][] board = new String[6][9];
	private String player;
	private int count = 0;

	/**
	 * Fill the board with default values - Creates the board
	 * 
	 * @param board    is the game board
	 * @param myString is the default value that will be placed in the board
	 */
	public String[][] fillBoard(String[][] board, String myString) {
		// fill board with the given String
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				board[row][col] = myString;
			}
			System.out.println();
		}
		return board;
	}

	/**
	 * Displays the game board
	 * 
	 * @param strings is the game board
	 */
	public void showGameBoard(String[][] strings) {
		System.out.println();
		System.out.println(" 1 2 3 4 5 6 7");
		for (int row = 0; row < strings.length; row++) {
			for (int col = 1; col < strings[row].length; col++) {
				System.out.print(strings[row][col]);
			}
			System.out.print("\n");
		}
	}

	/**
	 * Logic for placing a move
	 * 
	 * @param row   is the current row on the board
	 * @param c     is the column the player selects
	 * @param count keeps track of who's move it is
	 */
	public boolean dropPiece(String[][] board, int col, String player) { // <----Start here
		boolean result = false;

		if (board[0][col] != "| ") {
			System.out.println("That column is full");
			return false;
		}
		for (int row = board.length - 1; row >= 0; row--) {
			if (board[row][col].equals("| ")) {
				board[row][col] = "|" + player;
				count++;
				return true;
			}
		}

		return result;
	}

	/**
	 * Method for a move by the player
	 * 
	 * @return column number to be used in game loop
	 */
	public int playerMove() throws InputMismatchException, ArrayIndexOutOfBoundsException {
		int col;

		Scanner in = new Scanner(System.in);
		System.out.println("Player " + getPlayer() + " please enter a column to drop your piece.");
		col = in.nextInt();
		if (col < 1 || col >7) {
			throw new ArrayIndexOutOfBoundsException();
		}

		return col;

	}

	
	

	/**
	 * This checks for a win in all the possible ways
	 * 
	 * @return boolean that either stops or keeps the game loop going
	 */
	public boolean checkForWin(String[][] board) {
		
	
			
		
		/** Check for Win Horizontally */
		boolean result = false;
		for (int row = 0; row < board.length; row++) {
			for (int col = 1; col < board[row].length - 3; col++) {
				if(board[row][col] != "| " && board[row][col] == board[row][col + 1]
						&& board[row][col] == board[row][col + 2] && board[row][col] == board[row][col + 3]); {
					
				}
			}
		}
		/** Check for a win Vertically */
		for (int col = 1; col < board[0].length; col++) {
			for (int row = 0; row < board.length - 3; row++) {
				if (board[row][col] != "| " && board[row][col] == board[row + 1][col]
						&& board[row][col] == board[row + 2][col] && board[row][col] == board[row + 3][col]) {
					return true;
				}
			}
		}
		/** Check for a win from top right */
		for (int row = 0; row < board.length - 3; row++) {
			for (int col = 3; col < board[row].length; col++) {
				if (board[row][col] != "| " && board[row][col] == board[row + 1][col - 1]
						&& board[row][col] == board[row + 2][col - 2] && board[row][col] == board[row + 3][col - 3]) {
					return true;
				}
			}
		}
		/** Check for a win from top left */
		for (int row = 0; row < board.length - 3; row++) {
			for (int col = 0; col < board[row].length - 3; col++) {
				if (board[row][col] != "| " && board[row][col] == board[row + 1][col + 1]
						&& board[row][col] == board[row + 2][col + 2] && board[row][col] == board[row + 3][col + 3]) {
					return true;
				}
			}
		}
		
		return result;
	}

	/**
	 * Method that after a move is complete - switches the player
	 * 
	 * @param currentPlayer - symbol of the player who's turn it is
	 * @return char that is tested later to determine and switch players
	 * 
	 */
	public String switchPlayer(String currentPlayer) {
		if (currentPlayer.equals("X")) {
			setPlayer("O");
			return currentPlayer;
		} else {
			setPlayer("X");
			return currentPlayer;
		}
	}

	

	

	/**
	 * getter for the current player
	 * 
	 * @return returns the current player
	 */
	public String getPlayer() {
		return player;
	}

	/**
	 * Sets the current player
	 * 
	 * @param player - takes in symbol of current player
	 */
	public void setPlayer(String player) {
		this.player = player;
	}

	/**
	 * returns the position of piece in the board
	 * 
	 * @param row - row the game piece is in
	 * @param col - column the game piece is in
	 * @return the character located in that spot
	 */

	public String[][] getBoard() {
		return board;
	}
	 public int getCount() {
		 return count;
	 }

}

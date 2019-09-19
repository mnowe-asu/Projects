package ui;

import core.Connect4;
import core.Connect4ComputerPlayer;
import javafx.application.Application;


import java.util.*;
/** This class creates the connect4 instance and is also where the game loop takes place
 * 
 * @author Matt Nowe
 *
 */
public class Connect4TextConsole extends Connect4GUI {
	static Scanner in = new Scanner(System.in);
	static Connect4 gameBoard = new Connect4();
	static Connect4ComputerPlayer comp = new Connect4ComputerPlayer();
	static Connect4TextConsole game = new Connect4TextConsole();
	
	int col;

	public static void main(String[] args) throws Exception {
		String choice;
		gameBoard.fillBoard(gameBoard.getBoard(), "| ");
		gameBoard.setPlayer("X");
		comp.setCompPlayer("O");
		
		boolean valid;
		do {
			System.out.println("Play as a GUI or text based game? Enter G or T");
			choice = in.next();
			valid = choice.equalsIgnoreCase("G") || choice.equalsIgnoreCase("T");
			if (!valid) {
				System.out.println("Please enter either a G or T");
			} else if (choice.equalsIgnoreCase("G")) {
				
				Application.launch(args);
				break;
			} else {
			System.out.println("Would you like to play against a player or the computer? P/C");
			choice = in.next();
			valid = choice.equalsIgnoreCase("P") || choice.equalsIgnoreCase("C");
			if (!valid) {
				System.out.println("Enter either a P or C\n");
			} else {
				game.gameLoop(choice);
			} 
			}
		} while (!valid);
		

	}

	/** Method responsible for the loop which allows the game to be played
	 * 
	 * @param choice - Either 'P' or 'C' depending on who you want to play
	 */
	public void gameLoop(String choice){
		try {
		if (choice.equalsIgnoreCase("P")) {
			while (true) {
				gameBoard.showGameBoard(gameBoard.getBoard());
				if (gameBoard.dropPiece(gameBoard.getBoard(), gameBoard.playerMove(), gameBoard.getPlayer())) {
					if (gameBoard.checkForWin(gameBoard.getBoard())) {
						System.out.println("Player " + gameBoard.getPlayer() + " Wins!!");
						break;

					}
				}
				gameBoard.switchPlayer(gameBoard.getPlayer());
			}
			
			
		} else if (choice.equalsIgnoreCase("C")) {
			while (true) {
				gameBoard.showGameBoard(gameBoard.getBoard());
				if (gameBoard.getPlayer().equals("X")) {
					col = gameBoard.playerMove();
					gameBoard.dropPiece(gameBoard.getBoard(), col, gameBoard.getPlayer());
				} else {
					col = comp.compMove();
					gameBoard.dropPiece(gameBoard.getBoard(), col, comp.getCompPlayer());
				}
				if (gameBoard.checkForWin(gameBoard.getBoard())) {
					System.out.println("Player " + gameBoard.getPlayer() + " Wins!");
					break;
					// Add Play Again Functionality?
				}
				if (gameBoard.getCount() == 46) {
					System.out.println("Tie Game!");
				}else {
				gameBoard.switchPlayer(gameBoard.getPlayer());
			}
			}
		}
		} catch(InputMismatchException e) {
			System.out.println("Please enter a NUMBER");
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Please enter a number between 1 and 7");
			
		}
	}
}

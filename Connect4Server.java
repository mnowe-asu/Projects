package core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javax.swing.JScrollPane;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Connect4Server extends Application {
	public static final int PLAYER1 = 1; // Indicates player1
	public static final int PLAYER2 = 2; // Indicates player2
	public static final int PLAYER1_WON = 1; // Indicates player1 won
	public static final int PLAYER2_WON = 2; // Indicates player2 won
	public static final int DRAW = 3; // Indicates a draw
	public static final int CONTINUE = 4; // Indicates game continues
	public static final char PLAYER1_TOKEN = 'X';
	public static final char PLAYER2_TOKEN = 'O';

	
	ServerSocket socket;
	int sessionNum = 1;

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Implement partial GUI here?
		TextArea connectionLog = new TextArea();
		Scene scene = new Scene(new ScrollPane(connectionLog), 300, 300);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Connect 4 Server");
		primaryStage.show();

		new Thread(() -> {
			try {
				// Create a Server socket connection
				socket = new ServerSocket(8000);
				Platform.runLater(
						() -> connectionLog.appendText(new Date() + "\n Server connection established at port 8000"));

				// handle 2 players connecting
				while (true) {

					Platform.runLater(() -> {
						connectionLog.appendText("\nWaiting for 2 players to connect to: " + sessionNum + "\n");
					});
					Socket player1 = socket.accept();

					Platform.runLater(() -> {
						connectionLog.appendText(new Date() + "Player 1 is connected to " + sessionNum + "\n");
						connectionLog.appendText(
								"\nPlayer1's IP address: " + player1.getInetAddress().getHostAddress() + "\n");

					});
					// Indicate the new player is player1
					new DataOutputStream(player1.getOutputStream()).writeInt(PLAYER1);

					// Connect to player2
					Socket player2 = socket.accept();

					Platform.runLater(() -> {
						connectionLog.appendText("\nPlayer 2 is now connection to " + sessionNum + "\n");
						connectionLog.appendText("Player2's IP address: " + player2.getInetAddress().getHostAddress());

					});
					new DataOutputStream(player2.getOutputStream()).writeInt(PLAYER2);

					// Display session and increment session number
					Platform.runLater(() -> {
						connectionLog.appendText(new Date() + "Let's start a new session! Session " + sessionNum++);
					});
					// Launch the new Thread
					new Thread(new currentSession(player1, player2)).start();

				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();

	}

	class currentSession implements Runnable {

		private Socket player1;
		private Socket player2;
		DataInputStream fromPlayer1, fromPlayer2;
		DataOutputStream toPlayer1, toPlayer2;
		private char[][] board = new char[6][7];

		// continue play
		private boolean continueToPlay = true;

		/** Construct Thread */
		public currentSession(Socket player1, Socket player2) {
			this.player1 = player1;
			this.player2 = player2;

			// Initialize board
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[i].length; j++) {
					board[i][j] = ' ';
				}
			}
		}

		/** Implement the run() method */
		public void run() {
			try {
				fromPlayer1 = new DataInputStream(player1.getInputStream());
				fromPlayer2 = new DataInputStream(player2.getInputStream());
				toPlayer1 = new DataOutputStream(player1.getOutputStream());
				toPlayer2 = new DataOutputStream(player2.getOutputStream());

				toPlayer1.writeInt(1);

				// Server loop to continually play the game
				while (true) {
					int row = fromPlayer1.readInt();
					int column = fromPlayer1.readInt();
					board[row][column] = PLAYER1_TOKEN;

					// check if Player1 wins
					if (isWon(PLAYER1_TOKEN)) {
						toPlayer1.writeInt(PLAYER1_WON);
						toPlayer2.writeInt(PLAYER1_WON);
						sendMove(toPlayer2, row, column);
						break;
					} else if (isFull()) {
						toPlayer1.writeInt(DRAW);
						toPlayer2.writeInt(DRAW);
						sendMove(toPlayer2, row, column);
						break;
					} else {
						// prompt player2 to make a move
						toPlayer2.writeInt(CONTINUE);

						// send player1's move to player2
						sendMove(toPlayer2, row, column);

						row = fromPlayer2.readInt();
						column = fromPlayer2.readInt();
						board[row][column] = PLAYER2_TOKEN;

						// check if Player 2 wins
						if (isWon(PLAYER2_TOKEN)) {
							toPlayer1.writeInt(PLAYER2_WON);
							toPlayer2.writeInt(PLAYER2_WON);
							sendMove(toPlayer1, row, column);
							break;

						} else {
							toPlayer1.writeInt(CONTINUE);

							// Send player 2's move to player1
							sendMove(toPlayer1, row, column);
						}

					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void sendMove(DataOutputStream out, int row, int column) throws IOException {
			out.writeInt(row);
			out.writeInt(column);
		}

		private boolean isFull() {
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 8; j++) {
					if (board[i][j] == ' ')
						;
					return false;
				}
			}
			return true;
		}

		private boolean isWon(char token) {
			// Check all columns
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[i].length; j++) {
					if (board[i][j] == token && board[i][j] == board[i + 1][j] && board[i][j] == board[i + 2][j]
							&& board[i][j] == board[i + 3][j]) {
						return true;
					}
				}
			}
			// Check all rows
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[i].length; j++) {
					if (board[i][j] == token && board[i][j] == board[i][j + 1] && board[i][j] == board[i][j + 2]
							&& board[i][j] == board[i][j + 3]) {
						return true;
					}
				}
			}
			// Check Diagonal - top Left
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[i].length; j++) {
					if (board[i][j] == token && board[i][j] == board[i+1][j + 1] && board[i][j] == board[i+2][j + 2]
							&& board[i][j] == board[i+3][j + 3]) {
						return true;
					}
				}

			}
			//check diagonal top right
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[i].length; j++) {
					if (board[i][j] == token && board[i][j] == board[i+1][j - 1] && board[i][j] == board[i+2][j-2]
							&& board[i][j] == board[i+3][j-3]) {
						return true;
					}
				}

			}
			return false;
		}
	}
	public static void main(String[] args) {
		launch(args);
	}
}


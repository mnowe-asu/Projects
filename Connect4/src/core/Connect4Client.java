package core;



	import java.io.DataInputStream;
	import java.io.DataOutputStream;
	import java.io.IOException;
	import java.net.Socket;

	import javafx.application.Application;
	import javafx.application.Platform;
	import javafx.scene.Scene;
	import javafx.scene.control.Label;
	import javafx.scene.layout.BorderPane;
	import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

	public class Connect4Client extends Application {
		public static final int PLAYER1 = 1; // Indicates player1
		public static final int PLAYER2 = 2; // Indicates player2
		public static final int PLAYER1_WON = 1; // Indicates player1 won
		public static final int PLAYER2_WON = 2; // Indicates player2 won
		public static final int DRAW = 3; // Indicates a draw
		public static final int CONTINUE = 4; // Indicates game continues
		public static final char PLAYER1_TOKEN = 'X';
		public static final char PLAYER2_TOKEN = 'O';
		
		//Indicate if player has the turn
		private boolean myTurn = false;
		//Token for both players
		private char myToken = ' ';
		private char otherToken = ' ';
		//Board players play on
		private BoardSpace [][] space = new BoardSpace[6][7];
		//Labels for the GUI
		private Label lblTitle = new Label();
		private Label lblStatus = new Label();
		//row and column numbers for player moves
		private int whatRow;
		private int whatColumn;
		private DataInputStream fromServer;
		private DataOutputStream toServer;
		//Streams to and from the server
		//Flags to track status of gameplay
		private boolean continuePlay = true;
		private boolean waitForMove = true;
		private String host = "localhost";
		
		
		@Override
		public void start(Stage stage) throws Exception {
			//Pane to hold the board
			GridPane pane = new GridPane();
			pane.gridLinesVisibleProperty();
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 7; j++) {
					pane.add(new BoardSpace(i,j), j, i);
				}
			}
			BorderPane bPane = new BorderPane();
			bPane.setTop(lblTitle);
			bPane.setCenter(pane);
			bPane.setBottom(lblStatus);
			
			stage.setTitle("Connect 4");
			stage.setScene(new Scene(bPane, 500,500));
			stage.show();
			connectToServer();
			
		}
		
		private void connectToServer() {
			try {
				//Connection to server
				Socket socket = new Socket(host,8004);
				//Create Streams
				 fromServer = new DataInputStream(socket.getInputStream());
				 toServer = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Create Thread to control game
			new Thread(() -> {
				try {
					int player = fromServer.readInt();
					if (player == PLAYER1) {
						myToken = 'X';
						otherToken = 'O';
						Platform.runLater(() -> {
							lblTitle.setText("Player 1: You are the black disks");
							lblStatus.setText("Waiting for Player 2 to join");
						});
						//startup message from the server
						fromServer.readInt();
						
						//Other player joins
						Platform.runLater(() -> 
							lblStatus.setText("Player 2 has joined. I start first"));
						myTurn = true;
								
					} else if(player == PLAYER2) {
						myToken = 'O';
						otherToken = 'X';
						Platform.runLater(() -> {
							lblTitle.setText("Player 2: You are the red disks");
							lblStatus.setText("Waiting for Player 1 to make a move");
						});
					}
					while(continuePlay) {
						if (player == PLAYER1) {
							waitForPlayerMove();
							sendMove();
							receiveFromServer();
						} else if (player == PLAYER2) {
							receiveFromServer(); //Receive Move from Player 1
							waitForPlayerMove(); //wait for player2 to move
							sendMove(); //send Move to server
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		}
		
		private void waitForPlayerMove() throws InterruptedException {
			while (waitForMove){
				Thread.sleep(100);
			}
			waitForMove = true;
		}
		
		private void sendMove() throws IOException {
			toServer.writeInt(whatColumn);
		}
		
		private void receiveFromServer() throws IOException {
			
			// Get status from Server
			int status = fromServer.readInt();
			if(status == PLAYER1_WON) {
				continuePlay = false;
				if(myToken == 'X') {
					Platform.runLater(() -> lblStatus.setText("I won! (Black)"));
					} else if (myToken == 'O') {
						Platform.runLater(() -> lblStatus.setText("Player 1 (Black) has won!"));
						receiveMove();
					} else if (status == PLAYER2_WON) {
						continuePlay = false;
						if(myToken == 'O') {
							Platform.runLater(() -> lblStatus.setText("I Won! (Red)"));
						} else if(myToken == 'X') {
							Platform.runLater(() -> lblStatus.setText("Player 2 (Red) has won!"));
							receiveMove();
						}
					} else if (status == DRAW) {
						continuePlay = false;
						Platform.runLater(() -> lblStatus.setText("Game is over: No Winner"));
						if (myToken == 'O') {
							receiveMove();
						}
					} else {
						receiveMove();
						Platform.runLater(() -> lblStatus.setText("My Turn!"));
						myTurn = true;
					}
				}
			}
		private void receiveMove() throws IOException {
			//Get other players move
			int row = fromServer.readInt();
			int column = fromServer.readInt();
			Platform.runLater(() -> space[row][column].setToken(otherToken));
		}
		public class BoardSpace extends Pane{
			private int row, column;
			private char token = ' ';
			
			public BoardSpace(int row, int column) {
				this.row = row;
				this.column = column;
				this.setPrefSize(2000,2000);
				setStyle("-fx-border-color: black");
				//this.setOnMouseClicked(e -> handleMouseClick());
			}
			public char getToken() {
				return token;
			}
			public void setToken(char c) {
				token = c;
				//repaint();
			}
		}
		public static void main(String[] args) {
			launch(args);
		}
		}


	


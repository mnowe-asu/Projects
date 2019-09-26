package core;
import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
/**
 * The server class of the Connect4 game
 * 
 * @author Matthew Nowe
 * @version version1
 *
 */
public class Connect4Server extends Application implements Connect4Constants 
{
  /** number of the session */
  private int sessionNo = 1;
  /**
   * The start of the program where the stage will be set up
   * 
   * @param stage the stage of the program
   */
  @Override 
  public void start(Stage stage) {
    TextArea taLog = new TextArea();
    Scene scene = new Scene(new ScrollPane(taLog), 450, 200);
    stage.setTitle("Connect4Server"); 
    stage.setScene(scene);
    stage.show();
    new Thread( () -> 
    {
      try {
        ServerSocket serverSocket = new ServerSocket(8004);
        Platform.runLater(() -> taLog.appendText(new Date() + ": Server started at socket 8004\n"));
        while (true) 
        {
          Platform.runLater(() -> taLog.appendText(new Date() + ": Wait for players to join session " + sessionNo + '\n'));
          Socket player1 = serverSocket.accept();
          Platform.runLater(() -> {
        	  taLog.appendText(new Date() + ": Player 1 joined session " + sessionNo + '\n');
        	  taLog.appendText("Player 1's IP address" + player1.getInetAddress().getHostAddress() + '\n');
          });
          new DataOutputStream(
            player1.getOutputStream()).writeInt(PLAYER1);
          Socket player2 = serverSocket.accept();
          Platform.runLater(() -> {
            taLog.appendText(new Date() + ": Player 2 joined session " + sessionNo + '\n');
            taLog.appendText("Player 2's IP address" + player2.getInetAddress().getHostAddress() + '\n');
          });
          new DataOutputStream(player2.getOutputStream()).writeInt(PLAYER2);
          Platform.runLater(() -> 
          taLog.appendText(new Date() + ": Start a thread for session " + sessionNo++ + '\n'));
          new Thread(new HandleASession(player1, player2)).start();
        }
      }
      catch(IOException ex)
      {
        ex.printStackTrace();
      }
    }).start();
  }
  /**
   * Class that will handle session
   * 
   * @author Matthew Nowe
   *
   */
  class HandleASession implements Runnable, Connect4Constants 
  {
	/** an instance variable that will resemble player 1 */
    private Socket player1;
    /** an instance variable that will resemble player 2 */
    private Socket player2;
    /** an instance variable that will resemble the Connect4 board*/
    private char[][] board =  new char[6][7];
    /** 
     * Construct a thread
     * 
     * @param player1 the first player of the game
     * @param player2 the second player of the game
     */
    public HandleASession(Socket player1, Socket player2) 
    {
      this.player1 = player1;
      this.player2 = player2;
      for (int row = 0; row < 6; row++)
      {
        for (int col = 0; col < 7; col++)
        {
          board[row][col] = ' ';
        }
      }
    }
    /** 
     * Implements the run() method for the thread 
     * 
     */
    public void run() 
    {
      try {
        DataInputStream fromPlayer1 = new DataInputStream(player1.getInputStream());
        DataOutputStream toPlayer1 = new DataOutputStream(player1.getOutputStream());
        DataInputStream fromPlayer2 = new DataInputStream(player2.getInputStream());
        DataOutputStream toPlayer2 = new DataOutputStream(player2.getOutputStream());
        toPlayer1.writeInt(1);
        while (true) 
        {
          int row = fromPlayer1.readInt();
          int column = fromPlayer1.readInt();
          board[row][column] = 'X';
          if (winner('X')) 
          {
            toPlayer1.writeInt(PLAYER1_WON);
            toPlayer2.writeInt(PLAYER1_WON);
            sendMove(toPlayer2, row, column);
            break;
          }
          else 
          {
            toPlayer2.writeInt(CONTINUE);
            sendMove(toPlayer2, row, column);
          }
          row = fromPlayer2.readInt();
          column = fromPlayer2.readInt();
          board[row][column] = 'O';
          if (winner('O')) 
          {
            toPlayer1.writeInt(PLAYER2_WON);
            toPlayer2.writeInt(PLAYER2_WON);
            sendMove(toPlayer1, row, column);
            break;
          }
          else if (isFull()) 
          {
              toPlayer1.writeInt(DRAW);
              toPlayer2.writeInt(DRAW);
              sendMove(toPlayer2, row, column);
              break;
          }
          else 
          {
            toPlayer1.writeInt(CONTINUE);
            sendMove(toPlayer1, row, column);
          }
        }
      }
      catch(IOException ex) {
        ex.printStackTrace();
      }
    }
    /** 
     * Sends the move to other player 
     * 
     * @param out the data output stream that will send the row and column indexes
     * @param row the row that will be sent
     * @param column the column that will be sent
     */
    private void sendMove(DataOutputStream out, int row, int column) throws IOException 
    {
      out.writeInt(row);
      out.writeInt(column);
    }
    /** 
     * Determines if the cells are all occupied 
     * 
     * @returns true/if full, false/if not full
     */
    private boolean isFull() 
    {
    	int row = 6;
    	int column = 7;
    	for (int i = 0; i < row; i++)
    	{
    		for (int j = 0; j < column; j++)
    		{
    			if (board[i][j] == ' ')
    			{
    				return false; 
    			}
    		}
    	}
    	return true;
    }
    /**
     *  Determines if the player with the specified token wins
     *  
     *  @param token the character that will determine if the piece is red or yellow
     *  
     *  @return true/if winner is found, false/if not
     *  
     */
	private boolean winner(char token) 
	{
		for (int j = 0; j < 4; j++)
		{
			for (int i = 0; i < 6; i++) 
			{
				if (board[i][j] == token && board[i][j + 1] == token && board[i][j + 2] ==  token && board[i][j + 3] == token) 
				{
					return true;
		        }
		    }
		}
		//checking for horizontal
		for (int i = 0; i < 3; i++) 
		{
			for (int j = 0; j < 7; j++) 
			{
				if (board[i][j] == token && board[i + 1][j] == token && board[i + 2][j] == token && board[i + 3][j] == token)
				{
					return true;
		        }
			}
		}
		//checking for vertical
		for (int i = 3; i < 6; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				if (board[i][j] == token && board[i - 1][j + 1] == token && board[i - 2][j + 2] == token && board[i - 3][j + 3] == token)
				{        
					return true;
		        }
		    }
		}
		//checking for diagonal ascending
		for (int i = 3; i < 6; i++) 
		{
			for (int j = 3; j < 7; j++) 
			{
				if (board[i][j] == token && board[i - 1][j - 1] == token && board[i - 2][j - 2] == token && board[i - 3][j - 3] == token)
				{
					return true;
				}
			}
		} 
		//checking for diagonal descending
		for (int j = 0; j < 4; j++) 
		{
			for (int i = 0; i < 6; i++)
			{
				if (board[i][j] == token && board[i][j + 1] == token && board[i][j + 2] == token && board[i][j + 3] == token) 
				{
					return true;
				}
			}
		}
		return false;
	}	
  }
  /**
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   * 
   * @param args a string array
   */
  public static void main(String[] args) 
  {
    launch(args);
  }
}

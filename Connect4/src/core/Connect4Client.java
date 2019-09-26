package core;
import java.io.*;
import java.net.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;

/**
 * The client class of the Connect4 game
 * 
 * @author Matthew Nowe
 * @version version1
 *
 */
public class Connect4Client extends Application implements Connect4Constants 
{
  /** A boolean instance variable that will indicate whose move it is */
  private boolean move = false;

  /** the token for the first player */
  private char firstToken = ' ';

  /** the token for the second player */
  private char secondToken = ' ';

  /** the board for the Connect4 game */
  private Connect4Cell[][] board =  new Connect4Cell[6][7];

  /** will show the title during the game */
  private Label lblTitle = new Label();

  /** will show the status of the game */
  private Label lblStatus = new Label();

  /** the current row for the selected move */
  private int selectRow;
  
  /** the current column for the selected move */
  private int selectColumn;

  /** the data input streamer from server */
  private DataInputStream fromServer;
  
  /** the data output streamer to server */
  private DataOutputStream toServer;

  /** a boolean to decide if user wants to continue to play */
  private boolean continueToPlay = true;

  /** a boolean to decide if user is still waiting for the other user to make a move */
  private boolean waiting = true;

  /** name of the host */
  private String host = "localhost";

  /**
   * The start of the method that will set the stage and connect to the server
   * 
   * @param the stage of the program
   */
  @Override 
  public void start(Stage stage)
  {
    GridPane gridPane = new GridPane(); 
    for (int row = 0; row < 6; row++)
    {
      for (int column = 0; column < 7; column++)
      {
        gridPane.add(board[row][column] = new Connect4Cell(row, column), column, row);
      }
    }
    BorderPane borderPane = new BorderPane();
    borderPane.setTop(lblTitle);
    borderPane.setCenter(gridPane);
    borderPane.setBottom(lblStatus);
    Scene scene = new Scene(borderPane, 400, 450);
    stage.setTitle("Connect4");
    stage.setScene(scene);
    stage.show();   
    connectToServer();
  }
  /**
   * A method that will connect to the server
   */
  private void connectToServer() 
  {
    try 
    {
      Socket socket = new Socket(host, 8004);
      fromServer = new DataInputStream(socket.getInputStream());
      toServer = new DataOutputStream(socket.getOutputStream());
    }
    catch (Exception ex) 
    {
      ex.printStackTrace();
    }
    new Thread(() -> 
    {
      try
      {
        int player = fromServer.readInt();
        if (player == PLAYER1) 
        {
          firstToken = 'R';
          secondToken = 'Y';
          Platform.runLater(() -> 
          {
            lblTitle.setText("Player 1 with token 'R'");
            lblStatus.setText("Waiting for player 2 to join");
          });
          fromServer.readInt();
          Platform.runLater(() -> 
          lblStatus.setText("Player 2 has joined. I start first"));
          move = true;
        }
        else if (player == PLAYER2) 
        {
          firstToken = 'Y';
          secondToken = 'R';
          Platform.runLater(() -> 
          {
            lblTitle.setText("Player 2 with token 'Y'");
            lblStatus.setText("Waiting for player 1 to move");
          });
        }
        while (continueToPlay)
        {      
          if (player == PLAYER1) 
          {
            waitForPlayerAction(); // Wait for player 1 to move
            sendMove(); // Send the move to the server
            receiveInfoFromServer(); // Receive info from the server
          }
          else if (player == PLAYER2) 
          {
            receiveInfoFromServer(); // Receive info from the server
            waitForPlayerAction(); // Wait for player 2 to move
            sendMove(); // Send player 2's move to the server
          }
        }
      }
      catch (Exception ex) 
      {
        ex.printStackTrace();
      }
    }).start();
  }
  /** 
   * Wait for the player to mark a cell 
   * 
   */
  private void waitForPlayerAction() throws InterruptedException 
  {
    while (waiting) 
    {
      Thread.sleep(100);
    }
    waiting = true;
  }
  /**
   *  Send this player's move to the server 
   *  
   *  @throws IOException
   */
  private void sendMove() throws IOException {
    toServer.writeInt(selectRow);
    toServer.writeInt(selectColumn); 
  }
  /** Receive info from the server 
   *
   */
  private void receiveInfoFromServer() throws IOException {
    int status = fromServer.readInt();

    if (status == PLAYER1_WON) 
    {
      continueToPlay = false;
      if (firstToken == 'R') 
      {
        Platform.runLater(() -> lblStatus.setText("I won! (R)"));
      }
      else if (firstToken == 'Y')
      {
        Platform.runLater(() -> 
          lblStatus.setText("Player 1 (R) has won!"));
        receiveMove();
      }
    }
    else if (status == PLAYER2_WON) 
    {
      continueToPlay = false;
      if (firstToken == 'Y') 
      {
        Platform.runLater(() -> lblStatus.setText("I won! (Y)"));
      }
      else if (firstToken == 'R') 
      {
        Platform.runLater(() -> 
        lblStatus.setText("Player 2 (Y) has won!"));
        receiveMove();
      }
    }
    else if (status == DRAW) 
    {
      continueToPlay = false;
      Platform.runLater(() -> 
      lblStatus.setText("Game is over, no winner!"));
      if (firstToken == 'Y')
      {
        receiveMove();
      }
    }
    else 
    {
      receiveMove();
      Platform.runLater(() -> lblStatus.setText("My turn"));
      move = true;
    }
  }
  /**
   * this will receive move from server
   * 
   * @throws IOException
   */
  private void receiveMove() throws IOException 
  {
    int row = fromServer.readInt();
    int column = fromServer.readInt();
    Platform.runLater(() -> board[row][column].setToken(secondToken));
  }
  /**
   * A class that will make the inside shells of the Connect4 Board
   * 
   * @author Matthew Nowe
   *
   */
  public class Connect4Cell extends Pane {
    /** the corresponding row of the connect4board */
    private int row;
    /** the corresponding column of the connect4board */
    private int column;
    /** the boolean instance variable that will determine if there is a char within and what kind of char it is*/
    private char token = ' ';
    /**
     * Constructor method
     * 
     * @param row the corresponding row
     * @param column the corresponding column
     */
    public Connect4Cell(int row, int column) 
    {
      this.row = row;
      this.column = column;
      this.setPrefSize(2000, 2000);
      setStyle("-fx-border-color: blue");
      this.setOnMouseClicked(e -> handleMouseClick());  
    }
    /** 
     * An accessor method that will return token 
     */
    public char getToken() {
      return token;
    }
    /** 
     * A mutator method that will set token
     */
    public void setToken(char c) 
    {
      token = c;
      paint();
    }
    /**
     * A method that will paint the board with the red or yellow circle
     */
    protected void paint()
    {
      if (token == 'R') 
      {
    	 Ellipse ellipse = new Ellipse(this.getWidth() / 2, 
    	 this.getHeight() / 2, this.getWidth() / 2 - 10, 
    	 this.getHeight() / 2 - 10);
    	 ellipse.centerXProperty().bind(
    	 this.widthProperty().divide(2));
    	 ellipse.centerYProperty().bind(
    	 this.heightProperty().divide(2));
    	 ellipse.radiusXProperty().bind(
    	 this.widthProperty().divide(2).subtract(10));        
    	 ellipse.radiusYProperty().bind(
    	 this.heightProperty().divide(2).subtract(10));   
    	 ellipse.setStroke(Color.BLACK);
    	 ellipse.setFill(Color.RED);
    	 getChildren().add(ellipse);
      }
      else if (token == 'Y') 
      {
    	  Ellipse ellipse = new Ellipse(this.getWidth() / 2, 
          this.getHeight() / 2, this.getWidth() / 2 - 10, 
          this.getHeight() / 2 - 10);
    	  ellipse.centerXProperty().bind(
          this.widthProperty().divide(2));
    	  ellipse.centerYProperty().bind(
          this.heightProperty().divide(2));
    	  ellipse.radiusXProperty().bind(
          this.widthProperty().divide(2).subtract(10));        
    	  ellipse.radiusYProperty().bind(
          this.heightProperty().divide(2).subtract(10));   
    	  ellipse.setStroke(Color.BLACK);
    	  ellipse.setFill(Color.YELLOW);
    	  getChildren().add(ellipse);
      }
    }
    /** 
     * The mouse click event that will place the circle on the Connect4 board
     */
    private void handleMouseClick()
    {
      if ((row == 5 || board[row+1][column].getToken() != ' ')  && token == ' ' && move) 
      {
    	  setToken(firstToken);
    	  move = false;
    	  waiting = false;
    	  lblStatus.setText("Waiting for the other player to move");
    	  selectRow = row;
    	  selectColumn = column;
      }
    }
  }
  /**
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   */
  public static void main(String[] args)
  {
    launch(args);
  }
}
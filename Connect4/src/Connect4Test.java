package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import core.Connect4;

public class Connect4Test {
	private Connect4 game;
	String player;
	String [][] board;
	
	
	@Before
	public void setUp() {
		game = new Connect4();
		 board = new String[6][7];
		 
	}
	
	@After
	public void tearDown() {
		game = null;
	}

	@Test
	public void testFillBoard() {
		game.fillBoard(board, "|");
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				assertEquals(board[i][j], "|");
			}
		}
		
	}
	
	
	@Test
	public void testShowGameBoard() {
		fail("Not yet implemented");
	}

	@Test
	public void testDropPiece() {
		game.fillBoard(board, "| ");
		assertTrue(game.dropPiece(board, 2, "X"));
		assertTrue(game.dropPiece(board, 2, "O"));
		game.fillBoard(board, "X");
		assertFalse(game.dropPiece(board, 2, "O"));
		assertFalse(game.dropPiece(board, 2, "X"));
		
	}

	@Test
	public void testPlayerMove() {
		assertEquals(game.playerMove(), 4); // test within range 1 - 7
		
	}

	@Test
	public void testCheckForWin() {
		fail("Not yet implemented");
	}

	@Test
	public void testSwitchPlayer() {
		
		assertEquals("X",game.switchPlayer("O"));
		assertEquals("O", game.switchPlayer("X"));
	
	}

	@Test
	public void testGetPlayer() {
		game.setPlayer("X");
		assertEquals(game.getPlayer(), "X");
		game.setPlayer("O");
		assertEquals(game.getPlayer(), "O");
	}

	@Test
	public void testSetPlayer() {
		Connect4 game = new Connect4();
		game.setPlayer("X");
		assertEquals(game.getPlayer(), "X");
	}

	@Test
	public void testGetBoard() {
		assertNotNull(game.getBoard());
	}

	@Test
	public void testGetSetCount() {
		game.setCount(40);;
		assertEquals(game.getCount(), 40);
		game.setCount(90);
		assertEquals(game.getCount(), 90);
				
	}

}

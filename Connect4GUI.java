package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import core.Connect4;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
/** Beginning of the class for the GUI - create instances of variables to be used 
 * 
 * @author Matthew Nowe
 *
 */
public class Connect4GUI extends Application {
	Connect4 game = new Connect4();
	Pane pane = new Pane();
	Connect4Piece[][] board = new Connect4Piece[6][7];
	boolean move = true;

	@Override
	public void start(Stage stage) throws Exception {
		Shape shape = new Rectangle((9) * 100, (7) * 100);

		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {

				Circle circle = new Circle(100 / 2);
				circle.setCenterX(100 / 2);
				circle.setCenterY(100 / 2);
				circle.setFill(Color.TRANSPARENT);
				circle.setTranslateX(i * (100 + 5) + 100 / 4);
				circle.setTranslateY(j * (100 + 5) + 100 / 4);
				shape.setFill(Color.YELLOW);
				shape = Shape.subtract(shape, circle);

			}
		}
		List<Circle> list = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 7; j++) {
				Circle circle = new Circle(100 / 2);
				circle.setCenterX(100 / 2);
				circle.setCenterY(100 / 2);
				circle.setFill(Color.TRANSPARENT);
				circle.setTranslateX(i * (100 + 5) + 100 / 4);
				circle.setTranslateY(j * (100 + 5) + 100 / 4);
				final int column = i;
				circle.setOnMouseClicked(e -> move(new Connect4Piece(move), column));

				list.add(circle);
			}
		}
		pane.getChildren().addAll(list);

		shape.setFill(Color.YELLOW);
		pane.getChildren().add(shape);
		stage.setScene(new Scene(pane));
		stage.show();

	}

	private void move(Connect4Piece circle, int column) {
		int row = 5;
		while (row >= 0) {
			if (!getBoardAt(column, row).isPresent()) {
				break;
			}
			row--;
		}
		if (row < 0) {
			return;
		}

		board[row][column] = circle;
		pane.getChildren().add(circle);
		circle.setTranslateX(column * (90) + 100 / 2);
		final int currentRow = row;
		TranslateTransition transition = new TranslateTransition(Duration.millis(10), circle);
		transition.setToY(row * (100) + 100 / 2);
		transition.setOnFinished(e -> {
			if (winner(column, currentRow)) {
				if (move == true) {
					System.out.println("Red Wins");
				} else {
					System.out.println("Black Wins");
				}
				try {
					TimeUnit.SECONDS.sleep(3);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				Scanner in = new Scanner(System.in);
				System.out.println("Thanks for playing! Press 0 to exit");
				int exit = in.nextInt();
				if (exit == 0) {
					System.exit(-1);
				}
				in.close();
			}

			move = !move;
		});
		transition.play();
	}

	private boolean winner(int column, int row) {
		List<Point2D> vertical = IntStream.rangeClosed(row - 3, row + 3).mapToObj(r -> new Point2D(column, r))
				.collect(Collectors.toList());
		List<Point2D> horizontal = IntStream.rangeClosed(column - 3, column + 3).mapToObj(c -> new Point2D(c, row))
				.collect(Collectors.toList());
		Point2D topLeft = new Point2D(column - 3, row - 3);
		List<Point2D> diagonal1 = IntStream.rangeClosed(0, 6).mapToObj(i -> topLeft.add(i, i))
				.collect(Collectors.toList());
		Point2D botLeft = new Point2D(column - 3, row + 3);
		List<Point2D> diagonal2 = IntStream.rangeClosed(0, 6).mapToObj(i -> botLeft.add(i, -i))
				.collect(Collectors.toList());
		int chain = 0;
		for (Point2D p : vertical) {
			int c = (int) p.getX();
			int r = (int) p.getY();
			Connect4Piece circle = getBoardAt(c, r).orElse(new Connect4Piece(!move));
			if (circle.token == move) {
				chain++;
				if (chain == 4) {
					return true;
				}
			} else {
				chain = 0;
			}
		}
		for (Point2D p : horizontal) {
			int c = (int) p.getX();
			int r = (int) p.getY();

			Connect4Piece circle = getBoardAt(c, r).orElse(new Connect4Piece(!move));
			if (circle.token == move) {
				chain++;
				if (chain == 4) {
					return true;
				}
			} else {
				chain = 0;
			}
		}
		for (Point2D p : diagonal1) {
			int c = (int) p.getX();
			int r = (int) p.getY();

			Connect4Piece circle = getBoardAt(c, r).orElse(new Connect4Piece(!move));
			if (circle.token == move) {
				chain++;
				if (chain == 4) {
					return true;
				}
			} else {
				chain = 0;
			}
		}
		for (Point2D p : diagonal2) {
			int c = (int) p.getX();
			int r = (int) p.getY();

			Connect4Piece circle = getBoardAt(c, r).orElse(new Connect4Piece(!move));
			if (circle.token == move) {
				chain++;
				if (chain == 4) {
					return true;
				}
			} else {
				chain = 0;
			}
		}
		return false;
	}

	private Optional<Connect4Piece> getBoardAt(int column, int row) {
		if (column < 0 || column >= 7 || row < 0 || row >= 6) {
			return Optional.empty();
		}

		return Optional.ofNullable(board[row][column]);
	}

	public class Connect4Piece extends Circle {
		private final boolean token;

		public Connect4Piece(boolean token) {
			super(100 / 2, token ? Color.RED : Color.BLACK);
			this.token = token;
			setCenterX(100 / 2);
			setCenterY(100 / 2);
		}
	}
}
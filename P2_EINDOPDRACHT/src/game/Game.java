package game;

import java.util.ArrayList;

/**
 * Main Game class for the RINGZ game.
 * @author martijnbruning
 *
 */
public class Game {

	//---- Instance Variables

	/**
	 * Array of players playing this game.
	 */
	private ArrayList<Player> players;
	/**
	 * The Board that this game is played on.
	 */
	private Board board;
	/**
	 * The turn this game is on.
	 */
	private int turn;
	/**
	 * The amount of players playing this game.
	 */
	private int playerCount;

	//---- Constructor ------------------------------------------

	/**
	 * Constructs a new game using the four given strings
	 * as names for the players. Then, assigns colors to the
	 * newly created players and runs the setUp method.
	 * @param p0 First player.
	 * @param p1 Second player.
	 * @param p2 Third player.
	 * @param p3 Fourth player.
	 */
	public Game(final String p0, final String p1,
			final String p2, final String p3) {
		playerCount = 4;
		if (p3 == null) {
			playerCount--;
			if (p2 == null) {
				playerCount--;
			}
		}

		board = new Board();
		//TODO spelers niet aanmaken als null
		players.add(new Player(p0, PlayerColor.COLOR_0));
		players.add(new Player(p1, PlayerColor.COLOR_1));
		players.add(new Player(p2, PlayerColor.COLOR_2));
		players.add(new Player(p3, PlayerColor.COLOR_3));

		setUpGame(playerCount);
		turn = 0;
	}

	//---- Queries ------------------------------------------
	/**
	 * Returns the player with the indicated index from the array.
	 * @param p Index of the player
	 * @return player
	 */
		public Player getPlayer(final int p) {
			return players.get(p);
		}
	//---- Methods ------------------------------------------
	/**
	 * Sets up the game by adding the Pieces to all the players inventory
	 * Determines how many Pieces each Player gets depending on the number
	 * of players playing this game.
	 * @param playerCount The number of players playing
	 */
	protected void setUpGame(final int playerCount) { // 4 Player Game
		if (playerCount == 4) {
			for (int pl = 0; pl < 4; pl++) { // Player loop
				for (int t = 0; t < 5; t++) { // Piece type loop
					for (int pc = 0; pc < 3; pc++) { // Piece amount loop
						players.get(pl).addPiece(new Piece(t, players.get(pl).getColor()));
					}
				}
			}
		} else if (playerCount == 3) { // 3 Player Game
			for (int pl = 0; pl < 3; pl++) { // Player loop
				for (int t = 0; t < 5; t++) { // Piece type loop
					for (int pc = 0; pc < 3; pc++) { // Piece amount loop
						players.get(pl).addPiece(new Piece(t, players.get(pl).getColor()));
					}
					players.get(pl).addPiece(new Piece(t, players.get(3).getColor()));
				}
			}
		} else if (playerCount == 2) { // 2 Player Game
			for (int pl = 0; pl < 2; pl++) { // Player loop
				for (int t = 0; t < 5; t++) { // Piece type loop
					for (int pc = 0; pc < 3; pc++) { // Piece amount loop
						players.get(pl).addPiece(new Piece(t, players.get(pl).getColor()));
						// The Xtra piece, players get their color + color of 1 other player
						players.get(pl).addPiece(new Piece(t, players.get(pl).getColor() + 2));
					}
				}
			}
		}
		//TODO else invalid player number exception
	}

	/**
	 * Places a piece on the selected cell
	 * returns an exception if the move is invalid.
	 * @param x Coordinate of the cell
	 * @param y Coordinate of the cell
	 * @param piece Piece to be placed
	 */
	public void move(final int x, final int y, final Piece piece) {
		board.move(x, y, piece);
		//TODO Bewerken, wat als 3 spelers? moet dit hier?
		if (turn == players.size()) {
			turn = 0;
		} else {
			turn++;
		}
	}
	//TODO gameOver, hasWinner, methods
}

package game;

import java.util.ArrayList;
import java.util.Observable;

import exceptions.InvalidMoveException;

/**
 * Main Game class for the RINGZ game.
 * @author martijnbruning
 *
 */
public class Game extends Observable {

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
	 * @param playernames List with the name of players
	 */
	//TODO observable maken
	public Game(final int x, final int y, final ArrayList<String> playernames) {

		playerCount = playernames.size();

		board = new Board();
		players = new ArrayList<Player>();
		//Creates players depending on the playerCount
		players.add(new Player(playernames.get(0), PlayerColor.COLOR_0));
		players.add(new Player(playernames.get(1), PlayerColor.COLOR_1));
		if (playerCount > 2) {
			players.add(new Player(playernames.get(2), PlayerColor.COLOR_2));
		}
		if (playerCount > 3) {
			players.add(new Player(playernames.get(3), PlayerColor.COLOR_3));
		}
		setUpGame(x, y, playerCount);
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
	/**
	 * Returns the current turn.
	 * @return turn
	 */
	public int getTurn() {
		return turn;
	}
	/**
	 * Returns the board that is currently used by this game.
	 * @return
	 */
	public Board getBoard() {
		return board;
	}

	//---- Methods ------------------------------------------
	/**
	 * Sets up the game by adding the Pieces to all the players inventory
	 * Determines how many Pieces each Player gets depending on the number
	 * of players playing this game.
	 * @param playercount The number of players playing
	 */
	protected void setUpGame(final int x, final int y, final int playercount) { // 4 Player Game
		// Creates the starting stone
		board.startStone(x, y, new Piece(0, 0));
		board.startStone(x, y, new Piece(1, 1));
		board.startStone(x, y, new Piece(2, 2));
		board.startStone(x, y, new Piece(3, 3));
		if (playercount == 4) {
			for (int pl = 0; pl < 4; pl++) { // Player loop
				for (int t = 0; t < 5; t++) { // Piece type loop
					for (int pc = 0; pc < 3; pc++) { // Piece amount loop
						Player player = players.get(pl);
						player.addPiece(new Piece(t, player.getColor()));
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
	 * @param type of the piece
	 * @param color of the piece
	 */
	public void move(final int x, final int y, final int type, final int color) throws InvalidMoveException {
		//TODO hier canMove laten doen, crashed als speler gewenst stuk niet heeft, InvalidMoveException throwen
		Piece movpc = players.get(turn).getPiece(type, color);
		board.move(x, y, movpc);
		int pindex = players.get(turn).getPieces().indexOf(movpc);
		players.get(turn).getPieces().remove(pindex);
		//TODO moet dit hier? pieces verwijderen, setPlaced gebruiken?
		if (turn == players.size()) {
			turn = 0;
		} else {
			turn++;
		}
		
		
		setChanged();
		notifyObservers();
	}
	// Game over idee:
	// Iteratieve loop die alle velden afgaat met canMove, check of inventory leeg is.
	// als beide voor alle spelers of als eerste voor alle dan gameover.
	//TODO gameOver, hasWinner, methods
}

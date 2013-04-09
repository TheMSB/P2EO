package ai;

import game.Board;
import game.Cell;
import game.Game;
import game.Player;

/**
 * A custom AI, extends upon the SmartAI This AI is customizable by given 3
 * values to it on construction, rating the importance of the 3 different
 * aspects checked: effortToWin, connections, blocking
 * 
 * @author I3anaan
 * 
 */
public class CustomAI extends SmartAI {

	/**
	 * The rating of effortToWin, the higher this is in comparison to
	 * connections and blocking, the more this AI will focus on this aspect
	 */
	private double effortToWin = 1;
	/**
	 * The rating of connections
	 */
	private double connections = 1;
	/**
	 * The rating of blocking
	 */
	private double blocking = 1;

	/**
	 * Constructs a CustomAI
	 * 
	 * @param game
	 *            the game in which this AI is playing
	 * @param player
	 *            the player linked to this AI
	 * @param effortToWin
	 *            How much to focus on effortToWin
	 * @param connections
	 *            How much to focus on connections
	 * @param blocking
	 *            How much to focus on blocking
	 */
	public CustomAI(Game game, Player player, double effortToWin,
			double connections, double blocking) {
		super(game, player);
		this.effortToWin = effortToWin;
		this.connections = connections;
		this.blocking = blocking;
	}

	/**
	 * Almost the same as in SmartAI, except that it now takes into account the
	 * different ratings for effortToWin, connections and blocking
	 */
	@Override
	public CellPoint calculateWorth(int x, int y) {
		CellPoint cellReturn = new CellPoint(x, y, 0, 0, 0);
		Cell cell = board.getCell(x, y);

		if (!board.getCell(x, y).isFull()) {
			cellReturn = new CellPoint(x, y, effortToWin * effortToWin(cell),
					connections * connections(x, y), blocking * blocking(x, y));
		}
		return cellReturn;
	}
}

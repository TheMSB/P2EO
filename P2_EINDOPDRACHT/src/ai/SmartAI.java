package ai;

import exceptions.InvalidPieceException;
import game.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * This AI calculates the best set to do. It does this by first assigning points
 * to each cell, based on: EffortToWin, how easy it is to get the point for this
 * cell connectedness, How much this cell will improve the connection this color
 * has Blocking, How much this move will hinder the enemy.
 * 
 * After calculating those values, it will calculate the best series of moves to
 * make, saved in a Path. This is done by calculating the best path (by checking
 * all paths) to the point with the highest score. If the average score from
 * this path is higher than the next best point, it will make these moves
 * (starting with one) If not, it will check the average worth of the next best
 * point, recursively going further.
 * 
 * While doing so it makes use of the CellPoint and Path classes.
 * 
 * @author I3anaan
 * 
 */
public class SmartAI implements AI {
	/**
	 * The game this ai is playing in
	 */
	protected Game game;
	/**
	 * The number of players in this game
	 */
	private int playerCount;
	/**
	 * The color of the player (can change based on who it is simulating)
	 */
	private int playerColor;
	/**
	 * The board of this game, in its current state (gotten from game
	 */
	protected Board board;
	/**
	 * The player this AI belongs to
	 */
	private Player player;
	/**
	 * A list of pieces from a given color, used to decide which moves are
	 * possible
	 */
	private ArrayList<Piece> pieces;
	/**
	 * A matrix of 5x5 simulating the board, containing CellPoints
	 */
	private CellPoint[][] cellPoints;
	/**
	 * A sorted set of CellPoints
	 */
	private TreeSet<CellPoint> cellPointsList;
	/**
	 * The cells the given playerColor has acces to
	 */
	private ArrayList<CellPoint> cellsAvailable;
	/**
	 * All possible paths, without detour, from point A to B (all paths have A
	 * en B equal)
	 */
	private TreeSet<Path> currentPaths;
	/**
	 * Cells the ai checked for blocking purposes (ie, contains the current
	 * wall)
	 */
	private ArrayList<Point> checkedCells;
	/**
	 * Pieces used for the current path, will reset when calculating a new path
	 */
	private ArrayList<Piece> piecesUsed;

	/**
	 * Constructor
	 * 
	 * @param game
	 *            Game to which the ai belongs
	 * @param player
	 *            Player to which the ai belongs
	 */
	public SmartAI(Game game, Player player) {
		this.game = game;
		this.player = player;
		this.playerCount = game.getPlayerCount();
		board = game.getBoard();
		cellPoints = new CellPoint[Board.DIM][Board.DIM];
	}

	/**
	 * Calculates the best move to make, then returns it in an
	 * ArrayList<Integer>
	 * 
	 * @return ArrayList<Integer> of data representing a move (x,y,type,color)
	 */
	@Override
	public ArrayList<Integer> getMove() {
		Path bestPath = null;
		Path bestPath1 = null;
		Path bestPath2 = null;
		/*
		 * For 2 and 3 player games the AI needs to check 2 colors, those are
		 * assigned here. 4 Player games only let each player have one color.
		 */
		if (playerCount == 2) {
			bestPath1 = getBestMove(player.getColor());
			bestPath2 = getBestMove(player.getColor() + 1);
			bestPath = BestPath(bestPath1, bestPath2);
		} else if (playerCount == 3) {
			bestPath1 = getBestMove(player.getColor());
			bestPath2 = getBestMove(3);
			bestPath = BestPath(bestPath1, bestPath2);
		} else if (playerCount == 4) {
			bestPath = getBestMove(player.getColor());
		} else {
			System.out.println("PlayerCount invalid");
		}

		System.out.println(bestPath);
		if (bestPath == null) {
			/*
			 * It should never come to this, nonetheless it will print some
			 * information about the board. In case it goes wrong.
			 */
			System.out.println("No more moves possible");
			System.out.println(cellsAvailable);
			System.out.println(bestPath1);
			System.out.println(bestPath2);
			System.out.println(player.getPieces());
			System.out.println(game.isGameOver());
			System.out.println(game.canDoMove(player));
		}

		Piece piece = null;
		int x = bestPath.get(0).x;
		int y = bestPath.get(0).y;

		try {
			piece = player.getPiece(bestPath.get(0).getBestType(), bestPath
					.get(0).getBestColor());
		} catch (InvalidPieceException e) {
			e.printStackTrace();
			System.out
					.println("SMART AI BUG, wil niet bestaand Piece gebruiken");
		}

		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(x);
		arr.add(y);
		arr.add(piece.getType());
		arr.add(piece.getColor());
		return arr;
	}

	/**
	 * Checks which Path is better
	 * 
	 * @param p1
	 *            Path 1
	 * @param p2
	 *            Path 2
	 * @return The best Path of the 2 given.
	 * @ensure result is p1 or p2
	 */
	public Path BestPath(Path p1, Path p2) {
		Path output = p1;
		if (p1 == null
				|| (p2 != null && playerCount != 3 && p2.getAverageWorth() > p1
						.getAverageWorth())) {
			output = p2;
		} else if (p2 != null
				&& p2.getAverageWorthNeutral() > p1.getAverageWorth()) {
			output = p2;
		}

		return output;
	}

	/**
	 * Checks which moves to do for the given color, to do so this method will
	 * first set some variables, and then call other methods to build the actual
	 * Path
	 * 
	 * @param playerColor
	 *            The color to get the best path for
	 * @return The best path of moves for the given color
	 */
	public Path getBestMove(int playerColor) {
		pieces = new ArrayList<Piece>();
		cellsAvailable = new ArrayList<CellPoint>();
		cellPointsList = new TreeSet<CellPoint>();
		this.playerColor = playerColor;

		for (Piece p : player.getPieces()) {
			if (p.getColor() == playerColor) {
				pieces.add(p);
			}
		}// pieces now only contains the pieces of the given color

		for (int x = 0; x < Board.DIM; x++) {
			for (int y = 0; y < Board.DIM; y++) {
				CellPoint cell = calculateWorth(x, y);
				cellPointsList.add(cell);
				cellPoints[x][y] = cell;

				boolean available = false;
				for (int i = 0; i < pieces.size() && !available; i++) {
					Piece piece = pieces.get(i);
					if (board.canMove(x, y, piece)) {
						cellsAvailable.add(cell);
						available = true;
						// There is a move possible on the current coordinates
					}
				}
			}
		}
		// cellsAvailable contains all the CellPoints on which the given color
		// can be put

		// System.out.println("CellsAvaible:  " + cellsAvailable);
		Iterator<CellPoint> it = cellPointsList.descendingIterator();
		return getBestPath(it);
		// Set all the arrays and stuff to match the color given,
		// will now find the best path with that color.
	}

	/**
	 * Will calculate the best Path based upon several variables set in
	 * getBestMove() and the given iterator.
	 * 
	 * @param it
	 *            Iterator that goes through all the Cells descending (starting
	 *            with the highest)
	 * @return The best path of moves
	 * @require it!=null
	 */
	private Path getBestPath(Iterator<CellPoint> it) {
		Path output = null;
		if (it.hasNext()) {
			CellPoint point = it.next();
			output = getBestPathOfTwo(getBestPathTo(point), it);
		}
		return output;
		// null als er geen nieuwe zet mogelijk is

	}

	/**
	 * Recursively compares Paths until one Paths average worth per move is
	 * higher than the next best CellPoint's total worth. If this returns null,
	 * it means there is no path possible to one of the point in the iterator
	 * (and the path argument was null aswell)
	 * 
	 * 
	 * @param path
	 *            The current best path
	 * @param it
	 *            The iterator which contains all the cellPoints sorted from
	 *            best to worst
	 * @return The best path of the current path and all the remaining paths to
	 *         one of the CellPoints in the iterator
	 * @ensure Compares all paths to the point where it is impossible to get a
	 *         better path
	 * @require it!=null
	 * */
	private Path getBestPathOfTwo(Path path, Iterator<CellPoint> it) {
		Path output = path;
		if (it.hasNext()) {
			CellPoint nextPoint = it.next();
			if (path != null && path.getAverageWorth() > nextPoint.getW()) {
				output = path;
				// The average points per move of the current Path is higher
				// then the next best Point
			} else {
				output = BestPath(path,
						getBestPathOfTwo(getBestPathTo(nextPoint), it));
				// output is now the best Path of the current Path (path) and
				// the best Path to the next point in the iterator.
			}
		} else {
			output = path;
			// If there are no other Paths to check
		}
		return output;
	}

	/**
	 * Gets the best path to the given Cell, from all the possible starting
	 * points possible (cellsAvailable)
	 * 
	 * @param point
	 *            Which destination the path should have
	 * @return Best Path available to that Cell
	 */
	private Path getBestPathTo(CellPoint point) {
		Path bestPath = null;
		for (int i = 0; i < cellsAvailable.size(); i++) {
			Path newPath = getPath(cellsAvailable.get(i), point);
			if (newPath != null
					&& (bestPath == null || newPath.getAverageWorth() > bestPath
							.getAverageWorth())) {
				bestPath = newPath;
				// bestPath is always the path with the best average worth
				// starting in a point in cellsAvailable.get(x) where 0<=x<=i
			}
		}
		return bestPath;
		// Can return null, meaning there is no path possible
	}

	/**
	 * Calculates the best path from point1 to point 2, based upon worth of
	 * CellPoints in between This is the method that will actually build a path.
	 * If a full Cell or a Cell which requires an unavailable type is detected,
	 * it will cancel the path
	 * 
	 * @param point1
	 *            Cell start
	 * @param point2
	 *            Cell destination
	 * @return The best Path between those or null if there is a better path
	 *         from another point (all paths go through an available cell)
	 * @require point1, point2 !=null
	 * @require point1, point2 have valid coordinates
	 */
	private Path getPath(CellPoint point1, CellPoint point2) {
		currentPaths = new TreeSet<Path>();
		piecesUsed = new ArrayList<Piece>();

		if (getBestType(point1) != -1) {
			point1.setBestType(getBestType(point1));
			point1.setBestColor(playerColor);
			Path currentPath = new Path(point1);

			continuePath(currentPath, point2);
		}

		Path output = null;
		System.out.println("Path returned = " + output + "  CurrentPaths  "
				+ currentPaths.size());

		if (currentPaths.size() > 0) {
			output = currentPaths.last();
		}
		return output;
	}

	/**
	 * Recursive help method for getPath, continuing a given path to a given
	 * destination.
	 * 
	 * @param currentPath
	 *            A path so far
	 * @param point2
	 *            The destination of the path
	 * @require currentPath,point2 !=null
	 * @ensure if currentPath.get(currentPath.size() - 1)==point 2, currentPath
	 *         will be added to currentPaths
	 * @ensure Will check all the permutations towards the destination (all the
	 *         possible sequences of X and Y moves)
	 */
	private void continuePath(Path currentPath, CellPoint point2) {

		// Calculate the x and y difference from where the path is to the
		// destination
		CellPoint point1 = currentPath.get(currentPath.size() - 1);
		int dx = point2.x - point1.x;
		int dy = point2.y - point1.y;

		int x = point1.x;
		int y = point1.y;

		int bestType = getBestType(point1);
		point1.setBestType(getBestType(point1)); // Best type for the cell
		point1.setBestColor(playerColor);
		boolean pieceFound = false;

		for (int i = 0; i < pieces.size() && !pieceFound; i++) {
			if (pieces.get(i).getType() == bestType) {
				piecesUsed.add(pieces.get(i));
				// invariant: piecesUsed.size() == currentPath.size();
				pieceFound = true;
			}
		} // Get a piece with that type and reserve it from further moves

		if (pieceFound) { // If there is a piece available to place on the last
							// Cell of the currentPath

			// if there still needs to be done a step on the X axis it will do
			// so and reinvoke this method
			if (Math.abs(dx) > 0) {
				Path pathX = currentPath.copy();
				CellPoint cell = cellPoints[x + (int) (Math.signum(dx))][y];
				if (!cellsAvailable.contains(cell)
						&& !board.getCell(cell.x, cell.y).isFull()) {
					// if cellsAvaible has the same cell it means this path is
					// taking a detour, thus its not worth taking up the path
					pathX.add(cell);
					continuePath(pathX, point2);
				}
			} else if (dy == 0) {
				// if both dx and dy are 0 it means the path has reached the
				// destination, it will then be added to a list of valid paths.
				currentPaths.add(currentPath); // BaseCase
			}
			if (Math.abs(dy) > 0) { // same as X but then for Y
				Path pathY = currentPath.copy();
				CellPoint cell = cellPoints[x][y + (int) (Math.signum(dy))];
				if (!cellsAvailable.contains(cell)
						&& !board.getCell(cell.x, cell.y).isFull()) {
					pathY.add(cell);
					continuePath(pathY, point2);
				}
			}
		}
	}

	/**
	 * Returns the best type to use on the given cell, returns -1 if there is no
	 * move possible
	 * 
	 * @param cell
	 *            The Cell to get the best type for
	 * @return Best type to place on cell
	 * @ensure returns -1 if there is no move possible
	 */
	private synchronized int getBestType(CellPoint cell) {
		ArrayList<Piece> possiblePieces = new ArrayList<Piece>();
		for (Piece a : pieces) {
			if (piecesUsed.indexOf(a) == -1) {
				possiblePieces.add(a);
			}
		}
		// Check for each Piece in pieces if they are in piecesUsed, if not will
		// add to possiblePieces
		// invariant: possiblePieces.size() = pieces.size()-piecesUsed.size()

		int type = -1;

		boolean typeFound = false;
		while (!typeFound && possiblePieces.size() > 0) {
			if (board.getCell(cell.x, cell.y).isEmpty()
					&& cell.getBW() > cell.getWW()
					&& getPieceOfType(possiblePieces, 4) != null) {
				type = 4;
			} else {
				type = getMostCommonType(possiblePieces);
			}// Determine the best type

			possiblePieces.remove(getPieceOfType(possiblePieces, type));
			// removes a piece with the just found type from the possiblePieces.
			typeFound = board.canMove(cell.x, cell.y, new Piece(type,
					playerColor));
			// Checks if the found type is a valid move, if not will retry to
			// found a best type
		}

		return type;
	}

	/**
	 * Gets a piece with the given type from the array of Pieces
	 * 
	 * @param arr
	 *            Array to get the Piece from
	 * @param type
	 *            The type of the piece wanted
	 * @return a piece with the given type from the array of Pieces
	 */
	private Piece getPieceOfType(ArrayList<Piece> arr, int type) {
		Piece pieceFound = null;
		for (int i = 0; i < arr.size() && pieceFound == null; i++) {
			if (arr.get(i).getType() == type) {
				pieceFound = arr.get(i);
			}
		}

		return pieceFound;
	}

	/**
	 * Gets the most common type in the given ArrayList of Pieces
	 * 
	 * @param arr
	 *            The array to check
	 * @return The most common type in that array
	 */
	private int getMostCommonType(ArrayList<Piece> arr) {
		int[] result = new int[5];
		for (int i = 0; i < arr.size(); i++) {
			result[arr.get(i).getType()] = result[arr.get(i).getType()] + 1;
		}

		int max = 0;
		for (int i = 1; i < result.length; i++) {
			if (result[i] > result[max]) {
				max = i;
			}
		}

		return max;
	}

	/**
	 * Calculates the worth of the given coordinates. Will return a CellPoint
	 * containing the given coordinates and the calculated worth given on
	 * various aspects of the game Because we (currently) have 3 aspects to get
	 * points on, it is possible to customize the AI and let it specialize on a
	 * given aspect
	 * 
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @return CellPoint containing the different values
	 * @require x,y are valid coordinates
	 * @ensure result!=null
	 * @ensure result has same coordinates as the given coordinates
	 */
	public CellPoint calculateWorth(int x, int y) {
		CellPoint cellReturn = new CellPoint(x, y, 0, 0, 0);
		Cell cell = board.getCell(x, y);

		if (!board.getCell(x, y).isFull()) {
			cellReturn = new CellPoint(x, y, effortToWin(cell), connections(x,
					y), blocking(x, y));
		}
		return cellReturn;
	}

	/**
	 * Calculates the worth the given Cell has based upon how easy it is to win
	 * this Cell
	 * 
	 * @param cell
	 *            The Cell to check
	 * @return The Worth assigned based on effort to win
	 */
	protected double effortToWin(Cell cell) {
		double points = 0;
		ArrayList<Integer> list = cell.getOwnerList();
		int own = list.remove(player.getColor());
		// list is now a list containing the amount of pieces other players have
		// on the cell
		// own is the amount of pieces this color has on the cell

		if (list.indexOf(3) != -1 || own == 3
				|| (list.indexOf(2) != -1 && list.indexOf(1) != -1)) {
			points = -10;
			// somebody (not you) has 3 pieces on this cell.
			// you have 3 pieces
			// someone has 2 pieces, someone else 1 piece.
			// All these scenarios are unable to win, or already won
		} else if (list.indexOf(2) != -1
				&& util.Util.sumArray(list) == 2
				|| (own == 0 && list.indexOf(1) != -1 && util.Util
						.sumArray(list) == 2)) {
			points = 1;
			// somebody has 1 piece, someone else 1 piece, you have 0 pieces
			// someone has 2 pieces, no other pieces
		} else if (cell.isEmpty() || util.Util.sumArray(list) == 1
				|| (list.indexOf(2) != -1 && own == 1)) {
			points = 2;
			// empty
			// Someone has 1 piece
			// Someone has 2 pieces, you have 1
		} else if (own == 1 && util.Util.sumArray(list) == 0) {
			points = 3;
			// you have 1 piece, nothing else
		} else if (own == 2 && util.Util.sumArray(list) == 0) {
			points = 5;
			// you have 2 pieces, nothign else
		} else if ((own == 2 && util.Util.sumArray(list) == 1)
				|| (own == 1 && list.indexOf(1) != -1 && util.Util
						.sumArray(list) == 2)) {
			points = 8;
			// you have 2 pieces, someone else 1
			// you have 1 piece, and 2 other colors have 1 piece aswell
		} else {
			// Should not happen
			System.out.println("Unknown situation detected:  (effortToWin)");
			System.out.println("Own:  " + own + " List:  " + list);
		}
		if (playerCount == 3 && playerColor == 3) {
			points = 0; // In case of neutral color, not able to win, thus no
						// points in how easy it is to win the given point
		}

		return points;
	}

	/**
	 * Calculates worth based on connections
	 * 
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @return The value assigned based on connections
	 */
	protected double connections(int x, int y) {
		// 1 point for each Cell it adds to the available cells of the current
		// color

		double points = 4;
		points = points + (player.getPieces().size() / 15) * 5;
		// Extra points based on how many pieces you have left
		if (player.getPieces().size() == 15) {
			points = points + 25;
			// Major bonus points on the first move with this piece (to avoid
			// being blocked of at the spawn)
		}

		// Subtract 1 point for each neighbouring cell with the same color
		if (board.isCell(x + 1, y)
				&& board.getCell(x + 1, y).hasPieces(
						new Piece(0, player.getColor()))) {
			points--;
		}
		if (board.isCell(x - 1, y)
				&& board.getCell(x - 1, y).hasPieces(
						new Piece(0, player.getColor()))) {
			points--;
		}
		if (board.isCell(x, y + 1)
				&& board.getCell(x, y + 1).hasPieces(
						new Piece(0, player.getColor()))) {
			points--;
		}
		if (board.isCell(x, y - 1)
				&& board.getCell(x, y - 1).hasPieces(
						new Piece(0, player.getColor()))) {
			points--;
		}

		points = points + 2 - Math.max(Math.abs(2 - x), Math.abs(2 - y));
		// Middle Cells are more centralized, thus worth more

		return points;
	}

	/**
	 * Gives points based on how much this move will block other players from
	 * doing sets. Checks recursively to be able to detect walls, although this
	 * has some flaws in it.
	 * 
	 * @param x
	 *            X coordinate of cell to start checking
	 * @param y
	 *            Y coordinate of cell to start checking
	 * @return Points based on how many cells it blocks
	 * @require board.isCell(x,y)==true
	 */
	protected double blocking(int x, int y) {
		// 1 point for each neighboring color who cannot pass this Cell anymore
		// 2 extra points for each neighboring cell which also blocks the same
		// color
		double points = 0;
		ArrayList<Integer> list2 = board.getCell(x, y).getOwnerList();
		int own = list2.remove(player.getColor());

		// Victory block, somebody has 2 pieces, you have 1, thus you can block
		// that player from getting a point
		if ((list2.indexOf(2) != -1 && own == 1 && playerCount != 2)
				|| (playerCount == 2
						&& list2.indexOf(2) != ((playerColor + 1) % 2 + 1)
						// Make sure to not block your second color
						&& list2.indexOf(2) != -1 && own == 1)) {
			points = points + 3;
		}

		ArrayList<Integer> list = board.getCell(x, y).getOwnerList();
		checkedCells = new ArrayList<Point>();
		checkedCells.add(new Point(x, y));

		if ((x == 4 || x == 0) && (y == 4 || y == 0)) {
			points = points + 0;
			// Cells in a corner dont block, so they dont give points
		} else {
			// This Cell gets 1 points for each neighbour Cell who blocks a
			// color, 2 extra for each neighbouring Cell the neighbouring Cell
			// has that also blocks (and further)

			Point[] arr = { new Point(x + 1, y), new Point(x, y + 1),
					new Point(x - 1, y), new Point(x, y - 1) };
			for (int a = 0; a < arr.length; a++) { // ga alle naburige vakken na
				Point p = arr[a];
				if (board.isCell(p.x, p.y)) {
					for (int i = 0; i < list.size(); i++) {
						if (blocksColor(p, x, y, i, a, list)) {
							points = points + blocking(p, i);
							// Check if the point next to this also blocks the
							// given color
						}
					}
				} else {
					// Neighbouring cell is a wall, can have 1 wall at max;
					points = points + 2;
				}
			}

		}

		if (board.getCell(x, y).getOwnerList().size() < 3
				&& board.getCell(x, y).getOwnerList().size() > 0) {
			points = points * 0.3;
			// If it takes more than 1 move to fill this Cell, it severely
			// lowers the block worth
		}

		return points;
	}

	/**
	 * Checks whether or not filling up this Cell will block a given color
	 * 
	 * @param p
	 *            The offset for the X and Y
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @param i
	 *            color to check
	 * @param a
	 *            index of the points array
	 * @param list
	 *            List wich contains how many stones each color has on the
	 *            current Cell (x,y)
	 * @return True if the given cell complies to our rules of blocking a color
	 */
	public boolean blocksColor(Point p, int x, int y, int i, int a,
			ArrayList<Integer> list) {
		Point[] arr = { new Point(x + 1, y), new Point(x, y + 1),
				new Point(x - 1, y), new Point(x, y - 1) };

		return board.getCell(p.x, p.y).isFull()
				// Cell is full
				&& i != (player.getColor())
				// the color being checked is not the ai self
				&& (playerCount != 2 || (i != ((player.getColor() + 1) % 2 + player
						.getColor())))
				// The color being checked is not the ai's second color (in 2p
				// game)
				&& list.get(i) == 0
				// the given player has no pieces in this cell
				&& board.getCell(p.x, p.y).getOwnerList().get(i) == 0
				// the given player has no pieces in the neighbouring cell
				&& board.isCell(arr[(a + 2) % 4].x, arr[(a + 2) % 4].y)
				// the cell across the neighbouring cell exists
				&& board.getCell(arr[(a + 2) % 4].x, arr[(a + 2) % 4].y)
						.getOwnerList().get(i) == 0;
		// The cell across the neighbouring cell also doesnt have a piece from
		// the given player.
	}

	/**
	 * Recursive help method for blocking, does alot the same, except some
	 * different values and this will always return something higher than 0 to
	 * allow multiplication
	 * 
	 * 
	 * @param point
	 *            The point to check around
	 * @param playerN
	 *            The player to check for
	 * @return Points based on how many cells it blocks
	 * @ensure result>0
	 * @require board.isCell(point.x,point.y)==true
	 */
	public double blocking(Point point, int playerN) {
		double points = 1;
		ArrayList<Integer> list = board.getCell(point.x, point.y)
				.getOwnerList();
		checkedCells.add(new Point(point.x, point.y));

		if ((point.x == 4 || point.x == 0) && (point.y == 4 || point.y == 0)) {
			points = 1;
			// Cells in a corner dont block, so they dont give points
		} else {
			Point[] arr = { new Point(point.x + 1, point.y),
					new Point(point.x, point.y + 1),
					new Point(point.x - 1, point.y),
					new Point(point.x, point.y - 1) };
			for (int a = 0; a < arr.length; a++) {
				Point p = arr[a];
				// Check if this neighbouring cell is not already checked (would
				// end up in an infinite loop otherwise)
				boolean alreadyChecked = false;
				for (Point checked : checkedCells) {
					if (checked.x == p.x && checked.y == p.y) {
						alreadyChecked = true;
					}
				}

				if (!alreadyChecked) {
					if (board.isCell(p.x, p.y)) {
						if (blocksColor(p, point.x, point.y, playerN, a, list)) {
							points = points + blocking(p, playerN);
							// Give points and continue recursively checking for
							// walls
						}
					} else {
						// Neighbouring cell is a wall;
						points = points + 1;
					}
				}
			}

		}
		return points;
	}

	/**
	 * Checks the player belonging to the AI for pieces of a certain color
	 * @param color	the color wanted
	 * @return	An ArrayList<Piece> containing only pieces of the given color
	 */
	public ArrayList<Piece> getPiecesOfColor(int color) {
		ArrayList<Piece> output = new ArrayList<Piece>();
		for (Piece p : player.getPieces()) {
			if (p.getColor() == color) {
				output.add(p);
			}
		}
		return output;
	}
}

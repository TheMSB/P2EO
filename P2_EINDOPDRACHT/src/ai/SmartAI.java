package ai;

import game.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class SmartAI implements AI {
	private Game game;
	private Board board;
	private Player player;
	private ArrayList<Piece> pieces;
	private CellPoint[][] cellPoints;
	private TreeSet<CellPoint> cellPointsList;
	private ArrayList<CellPoint> cellsAvailable;
	private TreeSet<Path> currentPaths;
	private ArrayList<Point> checkedCells;

	public SmartAI(Game game, Player player) {
		this.game = game;
		this.player = player;
		board = game.getBoard();
		cellPoints = new CellPoint[Board.DIM][Board.DIM];
	}

	@Override
	public ArrayList<Integer> getMove() {
		pieces = player.getPieces();
		cellsAvailable = new ArrayList<CellPoint>();
		cellPointsList = new TreeSet<CellPoint>();

		for (int x = 0; x < Board.DIM; x++) {
			for (int y = 0; y < Board.DIM; y++) {
				CellPoint cell = new CellPoint(x, y, calculateWorth(x, y));
				cellPointsList.add(cell);
				cellPoints[x][y] = cell;

				boolean available = false;
				for (int i = 0; i < pieces.size() && !available; i++) { // TODO
																		// optimaliseren
					Piece piece = pieces.get(i);
					if (board.canMove(x, y, piece)) {
						cellsAvailable.add(cell);
						available = true;
					}
				}

			}
		}

		System.out.println("CellsAvaible:  " + cellsAvailable);
		Iterator<CellPoint> it = cellPointsList.descendingIterator();
		Path bestPath = getBestPath(it);
		System.out.println("CellsAvaible:  " + cellsAvailable);
		System.out.println(bestPath);
		// TODO wat te doen als bestPath null is = geen zetten meer mogelijk

		// Ga van hoogste punt naar laagste punt
		// Bereken de beste manier om er te komen, is punt nu lager dan zet
		// eronder, check die zet.
		boolean validMoveFound = false;

		int x;
		int y;
		Piece piece;

		System.out.println("Starting AI loop");
		do {
			x = bestPath.get(0).getX();
			y = bestPath.get(0).getY();
			piece = player.getPieces().get(
					(int) (Math.random() * player.getPieces().size()));
			validMoveFound = board.canMove(x, y, piece);
		} while (!validMoveFound);
		System.out.println("AI loop done");

		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(x);
		arr.add(y);
		arr.add(piece.getType());
		arr.add(piece.getColor());
		return arr;
	}

	private Path getBestPath(Iterator<CellPoint> it) {
		Path output = null;
		if (it.hasNext()) {
			CellPoint point = it.next();
			if (it.hasNext()) {
				CellPoint nextPoint = it.next();
				Path path = getBestPathTo(point);// Start met het beste path
													// naar hoogste punt
				if (path != null && path.getAverageWorth() > nextPoint.getW()) {
					output = path;
					// Punt per zet van Path is groter dan daarna beste zet
					// (zonder pad), base case
				} else {
					output = getBestPathOfTwo(path, it);
					// wil het beste path van dat wat je nu hebt, en het path
					// naar het volgende beste punt(zonder path)
				}
			}
		}
		return output;
		// null als er geen nieuwe zet mogelijk is

	}

	private Path getBestPathOfTwo(Path path, Iterator<CellPoint> it) {
		Path output = path;
		if (it.hasNext()) {
			CellPoint nextPoint = it.next();
			if (path != null && path.getAverageWorth() > nextPoint.getW()) {
				output = path;
				// Punt per zet van Path is groter dan daarna beste zet (zonder
				// pad), base case
			} else {
				output = getBestPathOfTwo(path, it);
				// wil het beste path van dat wat je nu hebt, en het path naar
				// het volgende beste punt(zonder path)
			}
		}
		return output;
	}

	private Path getBestPathTo(CellPoint point) {
		// lijst met cells waar je mag leggen
		// vanuit elk punt een path bouwen
		// beste path teruggeven
		// TODO kan cellsAvailable 0 zijn?
		Path bestPath = getPath(cellsAvailable.get(0), point);
		for (int i = 1; i < cellsAvailable.size(); i++) {
			Path newPath = getPath(cellsAvailable.get(i), point);
			if (newPath != null
					&& (bestPath == null || newPath.getAverageWorth() > bestPath
							.getAverageWorth())) {
				bestPath = newPath;
			}
		}
		return bestPath;
		// null als er geen path naar die plek mogelijk is
	}

	/**
	 * Calculates the best path from point1 to point 2, based upon worth of
	 * CellPoints inbetween
	 * 
	 * @param point1
	 *            From
	 * @param point2
	 *            To
	 * @return The best Path between those or null if there is a better path
	 *         from another point (all paths go through an available cell)
	 */
	private Path getPath(CellPoint point1, CellPoint point2) {
		currentPaths = new TreeSet<Path>();
		int dx = point2.getX() - point1.getX();
		int dy = point2.getY() - point1.getY();
		Path currentPath = new Path(point1);

		// System.out.println("GetPath from:  "+ point1+"  TO: "+point2);
		int x = point1.getX();
		int y = point1.getY();

		if (Math.abs(dx) > 0) {
			Path pathX = currentPath.copy();
			CellPoint cell = cellPoints[x + (int) (Math.signum(dx))][y];
			if (!cellsAvailable.contains(cell)
					&& !board.getCell(cell.getX(), cell.getY()).isFull()) {
				pathX.add(cell);
				continuePath(pathX, point2);
			}// else{ System.out.println("Detour detected");}
		} else if (dy == 0) {
			// System.out.println("BASECASE:  "+currentPath);
			currentPaths.add(currentPath); // BaseCase
		}
		if (Math.abs(dy) > 0) {
			Path pathY = currentPath.copy();
			CellPoint cell = cellPoints[x][y + (int) (Math.signum(dy))];
			if (!cellsAvailable.contains(cell)
					&& !board.getCell(cell.getX(), cell.getY()).isFull()) {
				pathY.add(cell);
				continuePath(pathY, point2);
			}// else{ System.out.println("Detour detected");}
		}

		Path output = null;

		if (currentPaths.size() > 0) {
			output = currentPaths.last();
		}
		return output;
	}

	/**
	 * Recursive help method.
	 * 
	 * @param currentPath
	 * @param point2
	 */
	private void continuePath(Path currentPath, CellPoint point2) {

		// Calculate the x and y difference from where the path is to the
		// destination
		CellPoint point1 = currentPath.get(currentPath.size() - 1);
		int dx = point2.getX() - point1.getX();
		int dy = point2.getY() - point1.getY();

		int x = point1.getX();
		int y = point1.getY();

		// if there still needs to be done a step on the X axis it will do so
		// and
		// reinvoke this method
		if (Math.abs(dx) > 0) {
			Path pathX = currentPath.copy();
			CellPoint cell = cellPoints[x + (int) (Math.signum(dx))][y];
			if (!cellsAvailable.contains(cell)
					&& !board.getCell(cell.getX(), cell.getY()).isFull()) {
				// if cellsAvaible has the same cell it means this path is
				// taking a detour, thus its not worth taking up the path
				pathX.add(cell);
				continuePath(pathX, point2);
			}// else{ System.out.println("Detour detected");}
		} else if (dy == 0) {
			// if both dx and dy are 0 it means the path has reached the
			// destination, it will then be added to a list of valid paths.
			// System.out.println("BASECASE:  "+currentPath);
			currentPaths.add(currentPath); // BaseCase
		}
		if (Math.abs(dy) > 0) { // same for Y
			Path pathY = currentPath.copy();
			CellPoint cell = cellPoints[x][y + (int) (Math.signum(dy))];
			if (!cellsAvailable.contains(cell)
					&& !board.getCell(cell.getX(), cell.getY()).isFull()) {
				pathY.add(cell);
				continuePath(pathY, point2);
			}
		}// else{ System.out.println("Detour detected");}
	}

	public double calculateWorth(int x, int y) {
		double points = 0;
		Cell cell = board.getCell(x, y);

		if (!board.getCell(x, y).isFull()) {
			points = effortToWin(cell) + connections(x, y) + blocking(x, y);
			// TODO deze waarden balanceren
		}
		return points;
	}

	private double effortToWin(Cell cell) {
		double points = 0;
		ArrayList<Integer> list = cell.getOwnerList();
		int own = list.remove(player.getColor()); // TODO belangrijk, dit
													// checken

		if (list.indexOf(3) != -1
				|| (list.indexOf(2) != -1 && list.indexOf(1) != -1)) {
			points = 0;// 0 punten als:
			// iemand 3 punten.
			// iemand 2 punten, iemand 1 punt.
		} else if (list.indexOf(2) != -1
				&& util.Util.sumArray(list) == 2
				|| (own == 0 && list.indexOf(1) != -1 && util.Util
						.sumArray(list) == 2)) {
			points = 1;
			// 1 punt als:
			// iemand 1, iemand 1, eigen 0
			// iemand 2 punten, verder leeg.

			// TODO:
			// 1 stuk om te winnen, dat niemand meer heeft. (als goed is moet
			// dit
			// uiteindelijk wel gedaan worden, omdat niet veel anders avaible
			// is)
		} else if (cell.isEmpty() || util.Util.sumArray(list) == 1
				|| (list.indexOf(2) != -1 && own == 1)) {
			points = 2;
			// 2 punten als:
			// leeg
			// 1 stuk iemand
			// 2 stukken iemand, 1 stuk eigen
		} else if (own == 1 && util.Util.sumArray(list) == 0) {
			points = 3;
			// 3 punten als:
			// 1 stuk eigen
		} else if (own == 2 && util.Util.sumArray(list) == 0) {
			points = 5;
			// 2 eigen, verder leeg
		} else if ((own == 2 && util.Util.sumArray(list) == 1)
				|| (own == 1 && list.indexOf(1) != -1 && util.Util
						.sumArray(list) == 2)) {
			points = 10;
			// 10 puntent als:
			// 2 eigen stukken, 1 ander stuk.
			// 1 eigen, 1 iemand, 1 iemand
		} else {
			System.out.println("Unknown situation detected:  (effortToWin)");
			System.out.println("Own:  " + own + " List:  " + list);
		}
		return points;
	}

	private double connections(int x, int y) {
		// 0 punten als:
		// niks toevoegd
		// 1 punten als:
		// 1 extra stuk
		// 2 punten als:
		// 2 extra stukken
		// 3 punten als:
		// 3 extra stukken

		double points = 4;
		// TODO wat te doen met extra kleur?
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
		// + 2-afstand tot (2,2), hoe meer in het midden, hoe meer waard.

		return points;
	}

	/**
	 * Gives points based on how much this move will block other players from
	 * doing sets Checks recursively to be able to detect walls
	 * 
	 * @param x
	 *            Xcoordinate of cell to start checking
	 * @param y
	 *            Ycoordinate of cell to start checking
	 * @return Points based on how many cells it blocks
	 * @require board.isCell(x,y)==true
	 */
	private double blocking(int x, int y) {
		// 1 punt als het vak dicht gooit voor elke aangrenzende speler
		// 2 punt extra voor elke naastgelegen vak ook dicht zonder die speler.

		double points = 0;
		ArrayList<Integer> list = board.getCell(x, y).getOwnerList();
		checkedCells = new ArrayList<Point>();
		checkedCells.add(new Point(x, y));

		if ((x == 4 || x == 0) && (y == 4 || y == 0)) {
			points = 0; // 0 punten als het een hoek vak is
		} else {
			// voor elk omliggend vak, 1 punt als het een muur is of een speler
			// is die niet op huidig ligt.
			// voor elke naburig vol vak die dezelfde speler als dit vak niet
			// heeft methode recursief uitvoeren, result x2

			Point[] arr = { new Point(x + 1, y), new Point(x, y + 1),
					new Point(x - 1, y), new Point(x, y - 1) };
			for (int a = 0; a < arr.length; a++) { // ga alle naburige vakken na
				Point p = arr[a];
				if (board.isCell(p.x, p.y)) {
					for (int i = 0; i < list.size(); i++) {
						// ga alle spelers na
						// TODO mogelijkheid: ook vol rekenen als tegenstander
						// bepaalde stukken niet meer heeft
						if (board.getCell(p.x, p.y).isFull()// Cell is full
								&& i != (player.getColor())// the color being
															// checked is not
															// the ai self
								&& list.get(i) == 0// the given player has no
													// pieces in this cell
								&& board.getCell(p.x, p.y).getOwnerList()
										.get(i) == 0// the given player has no
													// pieces in the
													// neighbouring cell
								&& board.isCell(arr[(a + 2) % 4].x,
										arr[(a + 2) % 4].y)// the cell across
															// the neighbouring
															// cell exists
								&& board.getCell(arr[(a + 2) % 4].x,
										arr[(a + 2) % 4].y).getOwnerList()
										.get(i) == 0) {// The cell across the
														// neighbouring cell
														// also doesnt have a
														// piece there.
							points = points + 2 * blocking(p, i);
						}
					}
				} else {
					// Neighbouring cell is a wall;
					points = points + 1;
				}
			}

		}

		return points;
	}

	/**
	 * Recursive help method for blocking
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
			points = 1; // 0 punten als het een hoek vak is
		} else {
			Point[] arr = { new Point(point.x + 1, point.y),
					new Point(point.x, point.y + 1),
					new Point(point.x - 1, point.y),
					new Point(point.x, point.y - 1) };
			for (int a = 0; a < arr.length; a++) { // ga alle naburige vakken na
				Point p = arr[a];
				// Check if this neighbouring cell isnt already checked
				boolean alreadyChecked = false;
				for (Point checked : checkedCells) {
					if (checked.x == p.x && checked.y == p.y) {
						alreadyChecked = true;
					}
				}

				if (!alreadyChecked) {
					if (board.isCell(p.x, p.y)) {
						if (board.getCell(p.x, p.y).isFull()// Cell is full
								&& board.getCell(p.x, p.y).getOwnerList()
										.get(playerN) == 0// the given player
															// has no pieces in
															// the neighbouring
															// cell
								&& board.isCell(arr[(a + 2) % 4].x,
										arr[(a + 2) % 4].y)// the cell across
															// the neighbouring
															// cell exists
								&& board.getCell(arr[(a + 2) % 4].x,
										arr[(a + 2) % 4].y).getOwnerList()
										.get(playerN) == 0) {// The cell across
																// the
																// neighbouring
																// cell also
																// doesnt have a
																// piece there.
							points = 2 * blocking(p, playerN);
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
}

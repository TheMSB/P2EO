package ai;

import exceptions.InvalidPieceException;
import game.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * This AI calculates the best set to do
 * It does this by first assigning points to each cell, based on:
 * EffortToWin, how easy it is to get the point for this cell
 * Connectiveness, How much this cell will improve the connection this color has
 * Blocking, How much this move will hinder the enemy.
 * 
 * After calculating those values, it will calculate the best series of moves to make, saved in a Path.
 * This is done by calculating the best path (by checking all paths) to the ponit with the highest score.
 * If the average score from this path is higher than the next best point, it will make these moves (starting with one)
 * If not, it will check the average worth of the next best point, recursively going further
 * @author I3anaan
 *
 */
public class SmartAI implements AI {
	/**
	 * The game this ai is playing in
	 */
	private Game game;
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
	private Board board;
	/**
	 * The player this AI belongs to
	 */
	private Player player;
	/**
	 * A list of pieces from a given color, used to decide which moves are possible
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
	 * All possible paths, without detour, from point A to B (all paths have A en B equal)
	 */
	private TreeSet<Path> currentPaths;
	/**
	 * Cells the ai checked for blocking purposes (ie, contains the current wall)
	 */
	private ArrayList<Point> checkedCells;
	/**
	 * Pieces used for the current path, will reset when calculating a new path
	 */
	private ArrayList<Piece> piecesUsed;

	/**
	 * Constructor
	 * @param game		Game to which the ai belongs
	 * @param player	Player to which the ai belongs
	 */
	public SmartAI(Game game, Player player) {
		this.game = game;
		this.player = player;
		this.playerCount = game.getPlayerCount();
		board = game.getBoard();
		cellPoints = new CellPoint[Board.DIM][Board.DIM];
	}

	@Override
	public ArrayList<Integer> getMove() {
		Path bestPath = null;
		Path bestPath1 = null;
		Path bestPath2 = null;
		//TODO	blokkeert eigen 2e kleur
		//TODO moet ook punten krijgen voor victory blok
		//TODO bij blokken moet overkant niet avaible zijn, ipv stuk hebben
		
		if(playerCount==2){
		//TODO voor andere spelers
			bestPath1 = getBestMove(player.getColor());
			bestPath2 = getBestMove(player.getColor()+2);
			bestPath = BestPath(bestPath1,bestPath2);
		}else if(playerCount ==3){
			bestPath1 = getBestMove(player.getColor());
			bestPath2 = getBestMove(3);
			bestPath = BestPath(bestPath1,bestPath2);
		}else if(playerCount==4){
			bestPath = getBestMove(player.getColor());
		}else{
			System.out.println("PlayerCount invalid");
		}
		//getBestMove((player.getColor() + 2) % 4);
		System.out.println(bestPath);
		if(bestPath==null){
			System.out.println("No more moves possible");
			System.out.println(bestPath1);
			System.out.println(bestPath2);
			System.out.println(player.getPieces());
			System.out.println(game.isGameOver());
		}
		// TODO wat te doen als bestPath null is = geen zetten meer mogelijk

		boolean validMoveFound = false;

		int x;
		int y;
		Piece piece = null;

		System.out.println("Starting AI loop");
		do {
			x = bestPath.get(0).getX();
			y = bestPath.get(0).getY();
			try {
				piece = player.getPiece(bestPath.get(0).getBestType(), player.getColor());
			} catch (InvalidPieceException e) {
				e.printStackTrace();
				System.out.println("SMART AI BUG, wil niet bestaand Piece gebruiken");
			}
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

	public Path BestPath(Path p1, Path p2){
		Path output = p1;
		if(p1==null || (p2!=null && playerCount!=3 && p2.getAverageWorth()>p1.getAverageWorth())){
			output = p2;
		}else if(p2!=null && p2.getAverageWorthNeutral()>p1.getAverageWorth()){
			output = p2;
		}
		
		return output;
	}
	
	/**
	 * Will return the best path of moves for the given color
	 * 
	 * @param playerColor
	 * @return
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
		System.out.println(pieces);
		// TODO wat te doen met neutrale kleur

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
					}
				}
			}
		}
		// cellsAvailable contains all the CellPoints on which the given color
		// can be put

		System.out.println("CellsAvaible:  " + cellsAvailable);
		Iterator<CellPoint> it = cellPointsList.descendingIterator();
		return getBestPath(it);
		// Set all the arrays and stuff to match the color given,
		// will now find the best path with that color.
	}

	/**
	 * Will return the best path of moves based upon several arrays set in
	 * getBestMove and the given iterator
	 * 
	 * @param it
	 * @return
	 */
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

	/**
	 * Recursively checks until a path average is better than the next highest
	 * solo avaible point, Will return the same path given if there are no other
	 * options
	 * 
	 * @param path
	 * @param it
	 * @return
	 */
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

	/**
	 * Gets the best path to the given point, from all the possible starting
	 * points possible (cellsAvailable)
	 * 
	 * @param point
	 * @return
	 */
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
	 * CellPoints inbetween This is the method that will actually build a path.
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
		piecesUsed = new ArrayList<Piece>();
		
		//TODO piecesUsed zou teveel Pieces kunnen bevatten, moet mogelijk gereset worden
		int dx = point2.getX() - point1.getX();
		int dy = point2.getY() - point1.getY();

		Path currentPath = new Path(point1);
		
		int bestType = getBestType(point1);
		point1.setBestType(bestType); // Best type for the cell
		boolean pieceFound = false;

		for (int i = 0; i < pieces.size() && !pieceFound; i++) {
			if (pieces.get(i).getType() == bestType) {
				piecesUsed.add(pieces.get(i));
				pieceFound = true;
			}
		} // Get a piece with that type and reserve it from further moves
		
		if (pieceFound) { // If there is a piece available to place on that cell
							// System.out.println("GetPath from:  "+
							// point1+"  TO: "+point2);
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
				System.out.println(piecesUsed);
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
		}

		Path output = null;

		if (currentPaths.size() > 0) {
			output = currentPaths.last();
		}
		return output;
	}

	/**
	 * Recursive help method for getPath.
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

		int bestType = getBestType(point1);
		point1.setBestType(bestType); // Best type for the cell
		boolean pieceFound = false;
		
		for (int i = 0; i < pieces.size() && !pieceFound; i++) {
			if (pieces.get(i).getType() == bestType) {
				piecesUsed.add(pieces.get(i));
				pieceFound = true;
			}
		} // Get a piece with that type and reserve it from further moves
		if (pieceFound) { // If there is a piece available to place on that cell
			
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
	}

	/**
	 * Returns the best type to use on the given cell, returns -1 if there is no
	 * move possible
	 * 
	 * @param cell
	 * @return
	 */
	private synchronized int getBestType(CellPoint cell) {
		// Dikke als blocking belangrijk is en leeg is.
		// daarna steen die ai nog veel heeft
		// TODO daarna steen die tegenstanders meest hebben
		ArrayList<Piece> possiblePieces = new ArrayList<Piece>();
		for (Piece a : pieces) {
			if (piecesUsed.indexOf(a) == -1) {
				possiblePieces.add(a);
			}
		}
		// Check for each Piece in pieces if they are in piecesUsed, if not will add to possiblePieces

		int type = -1;

		boolean typeFound = false;
		while (!typeFound && possiblePieces.size() > 0) {
			if (board.getCell(cell.getX(), cell.getY()).isEmpty()
					&& cell.getBW() > cell.getWW() && getPieceOfType(possiblePieces,4)!=null) {
				type = 4;
			} else {
				type = getMostCommonType(possiblePieces);
			}
			//System.out.println(type);
			
			possiblePieces.remove(getPieceOfType(possiblePieces, type));
			// Removes the just used piece from possible pieces so that in the
				// next iteration it will not try the same Piece

			typeFound = board.canMove(cell.getX(), cell.getY(), new Piece(type,
					playerColor));
		}

		return type;
	}
	/**
	 * Gets a piece with the given type from the array
	 * @param arr		Array to get the Piece from
	 * @param type		The type of the piece wanted
	 * @return
	 */
	private Piece getPieceOfType(ArrayList<Piece> arr, int type){
		Piece pieceFound = null;
		for (int i = 0; i < arr.size() && pieceFound==null; i++) {
			if (arr.get(i).getType() == type) {
				pieceFound = arr.get(i);
			}
		}
		
		return pieceFound;
	}
	/**
	 * Gets the most common type in the given ArrayList
	 * @param arr
	 * @return
	 */
	private int getMostCommonType(ArrayList<Piece> arr){
		int[] result = new int[5];
		for(int i=0;i<arr.size();i++){
			result[arr.get(i).getType()] = result[arr.get(i).getType()]+1;
		}
		
		int max = 0;
		for(int i=1;i<result.length;i++){
			if(result[i]>result[max]){
				max = i;
			}
		}
		
		return max;
	}

	/**
	 * Calculates the worth of the given coordinates
	 * @param x
	 * @param y
	 * @return	CellPoint containing the different values
	 */
	public CellPoint calculateWorth(int x, int y) {
		CellPoint cellReturn = new CellPoint(x, y, 0, 0, 0);
		Cell cell = board.getCell(x, y);

		if (!board.getCell(x, y).isFull()) {
			cellReturn = new CellPoint(x, y, effortToWin(cell), connections(x,
					y), blocking(x, y));
			// TODO deze waarden balanceren
		}
		return cellReturn;
	}

	private double effortToWin(Cell cell) {
		double points = 0;
		ArrayList<Integer> list = cell.getOwnerList();
		int own = list.remove(player.getColor());
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
		if (playerCount == 3 && playerColor == 3) {
			points = 0; // Incase of neutral color, not able to win, thus no
						// points in how easy it is to win the given point
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
		//TODO lategame veel te veel waard

		double points = 0;
		ArrayList<Integer> list = board.getCell(x, y).getOwnerList();
		checkedCells = new ArrayList<Point>();
		checkedCells.add(new Point(x, y));

		if ((x == 4 || x == 0) && (y == 4 || y == 0)) {
			points = 0; // 0 punten als het een hoek vak is
		} else {
			// voor elk omliggend vak, 2 punten als het een muur is of een
			// speler
			// is die niet op huidig ligt.
			// voor elke naburig vol vak die dezelfde speler als dit vak niet
			// heeft methode recursief uitvoeren, result x2

			Point[] arr = { new Point(x + 1, y), new Point(x, y + 1),
					new Point(x - 1, y), new Point(x, y - 1) };
			for (int a = 0; a < arr.length; a++) { // ga alle naburige vakken na
				Point p = arr[a];
				if (board.isCell(p.x, p.y)) {
					for (int i = 0; i < list.size(); i++) {
						// ga alle spelers na en kijk of ze al in een naburig
						// vak geblocked worden
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
														// piece from the given
														// player.
							points = points + 2 * blocking(p, i);
						}
					}
				} else {
					// Neighbouring cell is a wall;
					points = points + 2;
				}
			}

		}

		if (board.getCell(x, y).getOwnerList().size() < 3
				&& board.getCell(x, y).getOwnerList().size() > 0) {
			points = points * 0.5; // Als er meerdere zetten nodig zijn om dit
									// vak dicht te gooien zijn de punten minder
									// waard
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

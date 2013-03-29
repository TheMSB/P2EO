package ai;

import game.*;

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
				for(int i=0;i<pieces.size() && !available;i++){ // TODO optimaliseren
					Piece piece = pieces.get(i);
					if (board.canMove(x, y, piece)) {
						cellsAvailable.add(cell);
						available = true;
					}
				}

			}
		}

		System.out.println("CellsAvaible:  "+cellsAvailable);
		//TODO dit gaat blijkbaar fout
		Iterator<CellPoint> it = cellPointsList.descendingIterator();
		Path bestPath = getBestPath(it);
		System.out.println("CellsAvaible:  "+cellsAvailable);
		System.out.println(bestPath);
		//TODO wat te doen als bestPath null is = geen zetten meer mogelijk
		
		
		// Ga van hoogste punt naar laagste punt
		// Bereken de beste manier om er te komen, is punt nu lager dan zet
		// eronder, check die zet. RECURSIE
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
				Path path = getBestPathTo(point);//Start met het beste path naar hoogste punt
				if (path!=null && path.getAverageWorth() > nextPoint.getW()) {
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
		//null als er geen nieuwe zet mogelijk is

	}

	private Path getBestPathOfTwo(Path path, Iterator<CellPoint> it) {
		Path output = path;
		if (it.hasNext()) {
			CellPoint nextPoint = it.next();
			if (path!=null && path.getAverageWorth() > nextPoint.getW()) {
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
		//TODO kan cellsAvailable 0 zijn?
		Path bestPath = getPath(cellsAvailable.get(0), point);
		for (int i = 1; i < cellsAvailable.size(); i++) {
			Path newPath = getPath(cellsAvailable.get(i), point);
			if (newPath!=null && (bestPath==null || newPath.getAverageWorth() > bestPath.getAverageWorth())) {
				bestPath = newPath;
			}
		}
		return bestPath;
		//null als er geen path naar die plek mogelijk is
	}

	/**
	 * Calculates the best path from point1 to point 2, based upon worth of
	 * CellPoints inbetween
	 * 
	 * @param point1
	 *            From
	 * @param point2
	 *            To
	 * @return The best Path between those or null if there is a better path from another point (all paths go through an available cell)
	 */
	private Path getPath(CellPoint point1, CellPoint point2) {
		currentPaths = new TreeSet<Path>();
		int dx = point2.getX() - point1.getX();
		int dy = point2.getY() - point1.getY();
		Path currentPath = new Path(point1);

		//System.out.println("GetPath from:  "+ point1+"  TO: "+point2);
		int x = point1.getX();
		int y = point1.getY();

		if (Math.abs(dx) > 0) {
			Path pathX = currentPath.copy();
			CellPoint cell = cellPoints[x+(int)(Math.signum(dx))][y];
			if (!cellsAvailable.contains(cell) && !board.getCell(cell.getX(), cell.getY()).isFull()) {
				pathX.add(cell);
				continuePath(pathX, point2);
			}//else{ System.out.println("Detour detected");}
		} else if (dy == 0) {
			//System.out.println("BASECASE:  "+currentPath);
			currentPaths.add(currentPath); // BaseCase
		}
		if (Math.abs(dy) > 0) {
			Path pathY = currentPath.copy();
			CellPoint cell = cellPoints[x][y+(int)(Math.signum(dy))];
			if (!cellsAvailable.contains(cell) && !board.getCell(cell.getX(), cell.getY()).isFull()) {
				pathY.add(cell);
				continuePath(pathY, point2);
			}//else{ System.out.println("Detour detected");}
		}

		Path output = null;
		
		if(currentPaths.size()>0){
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

		// if there still needs to be done a step on the X axis it will do so and
		// reinvoke this method
		if (Math.abs(dx) > 0) {
			Path pathX = currentPath.copy();
			CellPoint cell = cellPoints[x+(int)(Math.signum(dx))][y];
			if (!cellsAvailable.contains(cell) && !board.getCell(cell.getX(), cell.getY()).isFull()) {
				// if cellsAvaible has the same cell it means this path is
				// taking a detour, thus its not worth taking up the path
				pathX.add(cell);
				continuePath(pathX, point2);
			}//else{ System.out.println("Detour detected");}
		} else if (dy == 0) {
			// if both dx and dy are 0 it means the path has reached the
			// destination, it will then be added to a list of valid paths.
			//System.out.println("BASECASE:  "+currentPath);
			currentPaths.add(currentPath); // BaseCase
		}
		if (Math.abs(dy) > 0) { // same for Y
			Path pathY = currentPath.copy();
			CellPoint cell = cellPoints[x][y+(int)(Math.signum(dy))];
			if (!cellsAvailable.contains(cell) && !board.getCell(cell.getX(), cell.getY()).isFull()) {
				pathY.add(cell);
				continuePath(pathY, point2);
			}
		}//else{ System.out.println("Detour detected");}
	}

	public double calculateWorth(int x, int y) {
		double points = 0;
		Cell cell = board.getCell(x, y);
		
		if(!board.getCell(x, y).isFull()){
			points = effortToWin(cell) + connections() + blocking() + (int)(Math.random() * 10);
		}
		return points;
	}

	private double effortToWin(Cell cell) {
		double points = 0;
		ArrayList<Integer> list = cell.getOwnerList();
		//0 punten als:
		//	iemand 3 punten.
		// 	iemand 2 punten, iemand 1 punt.
		//1 punt als:
		//	iemand 2 punten, verder leeg.
		//	1 stuk om te winnen, dat niemand meer heeft. (als goed is moet dit uiteindelijk wel gedaan worden, omdat niet veel anders avaible is)
		//2 punten als:
		//	leeg
		//	1 stuk iemand
		//	2 stukken iemand, 1 stuk eigen
		//3 punten als:
		//	1 stuk eigen
		//10 puntent als:
		//	2 eigen stukken, 1 ander stuk.
		//	1 eigen, 1 iemand, 1 iemand
		
		return 0;
	}

	private double connections() {
		
		//0 punten als:
		//	niks toevoegd
		//1 punten als:
		//	1 extra stuk
		//2 punten als:
		//	2 extra stukken
		//3 punten als:
		//	4 extra stukken
		
		// + 2-afstand tot (2,2),     hoe meer in het midden, hoe meer waard.
		
		return 0;
	}

	private double blocking() {
		//	1 punt als het vak dicht gooit voor elke aangrenzende speler
		//	1 punt extra voor elke naastgelegen vak ook dicht zonder die speler.
		
		
		
		return 0;
	}
}

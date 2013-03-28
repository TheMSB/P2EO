package ai;

import game.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class SmartAI implements AI {
	private Game game;
	private Board board;
	private Player player;
	private CellPoint[][] cellPoints;
	private TreeSet<CellPoint>	cellPointsList;
	private ArrayList<CellPoint> cellsAvailable;
	private TreeSet<Path>	currentPaths;
	
	
	public SmartAI(Game game, Player player){
		this.game = game;
		this.player = player;
		board = game.getBoard();
		cellPoints = new CellPoint[Board.DIM][Board.DIM];
		cellPointsList = new TreeSet<CellPoint>();
		cellsAvailable = new ArrayList<CellPoint>();
	}
	
	@Override
	public ArrayList<Integer> getMove() {
		for(int x=0; x<Board.DIM;x++){
			for(int y=0; y<Board.DIM;y++){
				CellPoint cell =  new CellPoint(x,y,calculateWorth(x,y));
				cellPointsList.add(cell);
				cellPoints[x][y] = cell;
				
				for(Piece piece :player.getPieces()){ //TODO optimaliseren
					if(board.canMove(x, y, piece)){
						cellsAvailable.add(new CellPoint(x,y,-1));
						break; //TODO klopt dit
					}
				}
				
				
			}
		}
		
		Iterator<CellPoint> it = cellPointsList.descendingIterator();
		getBestPath(it);
		
		
		//Ga van hoogste punt naar laagste punt
		//Bereken de beste manier om er te komen, is punt nu lager dan zet eronder, check die zet. RESURSIE
		boolean validMoveFound = false;
		
		int x;
		int y;
		Piece piece;
		
		System.out.println("Starting AI loop");
		do{
			x = (int)(Math.random()*5);
			y = (int)(Math.random()*5);
			piece = player.getPieces().get((int)(Math.random()*player.getPieces().size()));
			validMoveFound = board.canMove(x,y,piece);
			//System.out.println(x+","+y+"  "+piece);
		}while(!validMoveFound);		
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
				Path path = getBestPathTo(point);
				if (path.getAverageWorth() > nextPoint.getW()) {
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

	}

	private Path getBestPathOfTwo(Path path, Iterator<CellPoint> it) {
		Path output = path;
		if (it.hasNext()) {
			CellPoint nextPoint = it.next();
			if (path.getAverageWorth() > nextPoint.getW()) {
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

	private Path getBestPathTo(CellPoint point){
		//lijst met cells waar je mag leggen
		//vanuit elk punt een path bouwen
		//beste path teruggeven
		Path bestPath = getPath(cellsAvailable.get(0),point);
		for(int i = 1;i<cellsAvailable.size();i++){
			Path newPath = getPath(cellsAvailable.get(i),point);
			if(newPath.getAverageWorth()>bestPath.getAverageWorth()){
				bestPath = newPath;
			}
		}
		
		return bestPath;
	}
	
	/**
	 * Calculates the best path from point1 to point 2, based upon worth of CellPoints inbetween
	 * @param point1	From
	 * @param point2	To
	 * @return	The best Path between those
	 */
	private Path getPath(CellPoint point1, CellPoint point2){
		currentPaths = new TreeSet<Path>();
		int dx = point2.getX() - point1.getX();
		int dy = point2.getY() - point1.getY();
		Path currentPath = new Path(point1);
		// X en Y verschil berekenen
		//dan het aantal zetten op elke manier indelen
		
		int x = point1.getX();
		int y = point1.getY();
		
		if(Math.abs(dx)>0){
			Path pathX = currentPath.copy();
			CellPoint cell = cellPoints[(int)(x+Math.signum(dx))][y];
			if(!cellsAvailable.contains(cell)){
				pathX.add(cell);
				dx = (int)(dx + Math.signum(dx)*-1);
				continuePath(pathX,point2);
			}
		}else if(dy==0){
			currentPaths.add(currentPath); //BaseCase
		}
		if(Math.abs(dy)>0){
			Path pathY = currentPath.copy();
			CellPoint cell = cellPoints[x][(int)(y+Math.signum(dy))];
			if(!cellsAvailable.contains(cell)){
				pathY.add(cell);
				dy = (int)(dy + Math.signum(dy)*-1);
				continuePath(pathY,point2);
			}
		}
		
		return currentPaths.last();
	}
	
	/**
	 * Recursive help method.
	 * @param currentPath
	 * @param point2
	 */
	private void continuePath(Path currentPath,CellPoint point2){
		
		//Calculate the x and y difference from where the path is to the destination
		CellPoint point1 = currentPath.get(currentPath.size()-1);
		int dx = point2.getX() - point1.getX();
		int dy = point2.getY() - point1.getY();
		
		int x = point1.getX();
		int y = point1.getY();
		
		//if there still needs to be a step on the X axis it will do so and reinvoke this method
		if(Math.abs(dx)>0){
			Path pathX = currentPath.copy();
			dx = (int)(dx + Math.signum(dx)*-1);
			CellPoint cell = cellPoints[x][y];
			if(!cellsAvailable.contains(cell) && !board.getCell(x,y).isFull()){ //if cellsAvaible has the same cell it means this path is taking a detour, thus deleting the path
				pathX.add(cell);
				continuePath(pathX,point2);
			}
		}else if(dy==0){ //if both dx and dy are 0 it means the path has reached the destination, it will then be added to a list of valid paths.
			currentPaths.add(currentPath); //BaseCase
		}
		if(Math.abs(dy)>0){ //same for Y
			Path pathY = currentPath.copy();
			dy = (int)(dy + Math.signum(dy)*-1);
			CellPoint cell = cellPoints[x][y];
			if(!cellsAvailable.contains(cell) && !board.getCell(x,y).isFull()){
				pathY.add(cell);
				continuePath(pathY,point2);
			}
		}
	}

	public double calculateWorth(int x, int y){
		return effortToWin() + connections() + blocking() + Math.random()*10;
	}
	
	private double effortToWin(){
		return 0;
	}
	
	private double connections(){
		return 0;
	}
	
	private double blocking(){
		return 0;
	}
}

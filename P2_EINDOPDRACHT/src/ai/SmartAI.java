package ai;

import game.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class SmartAI implements AI {
	private Game game;
	private Board board;
	private Player player;
	private double[][] cellPoints;
	private TreeSet<CellPoint>	cellPointsList;
	private ArrayList<CellPoint> cellsAvailable;
	
	
	public SmartAI(Game game, Player player){
		this.game = game;
		this.player = player;
		board = game.getBoard();
		cellPoints = new double[Board.DIM][Board.DIM];
		cellPointsList = new TreeSet<CellPoint>();
		cellsAvailable = new ArrayList<CellPoint>();
	}
	
	@Override
	public ArrayList<Integer> getMove() {
		for(int x=0; x<Board.DIM;x++){
			for(int y=0; y<Board.DIM;y++){
				cellPoints[x][y] = calculateWorth(x,y);
				for(Piece piece :player.getPieces()){ //TODO optimaliseren
					if(board.canMove(x, y, piece)){
						cellsAvailable.add(new CellPoint(x,y,-1));
						break; //TODO klopt dit
					}
				}
				
				cellPointsList.add(new CellPoint(x,y,cellPoints[x][y]));
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
	
	private Path getPath(CellPoint point1, CellPoint point2){
		//TODO path bouwen naar die cel.
		// X en Y verschil berkenen
		//dan het aantal zetten op elke manier indelen
		return null;
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

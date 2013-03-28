package ai;

import game.Board;
import game.Game;
import game.Piece;
import game.Player;
import game.*;

import java.util.ArrayList;

public class SmartAI implements AI {
	private Game game;
	private Board board;
	private Player player;
	private Integer[][] cells;
	
	
	public SmartAI(Game game, Player player){
		this.game = game;
		this.player = player;
		board = game.getBoard();
		cells = new Integer[Board.DIM][Board.DIM];
	}
	
	@Override
	public ArrayList<Integer> getMove() {
		
		
		
		for(int x=0; x<Board.DIM;x++){
			for(int y=0; y<Board.DIM;y++){
				cells[x][y] = calculateWorth(x,y);
			}
		}
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
	
	
	public int calculateWorth(int x, int y){
		return 0;
	}
}

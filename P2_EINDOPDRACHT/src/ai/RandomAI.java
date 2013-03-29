package ai;

import game.*;

import java.util.ArrayList;

public class RandomAI implements AI {
	
	private Game game;
	private Board board;
	private Player player;
	
	
	public RandomAI(Game game, Player player){
		this.game = game;
		this.player = player;
		board = game.getBoard();
	}
	
	@Override
	public ArrayList<Integer> getMove() {
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

}

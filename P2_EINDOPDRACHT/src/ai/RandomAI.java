package ai;

import game.*;

import java.util.ArrayList;

public class RandomAI implements AI {

	//TODO client een ai laten gebruiken
	
	private Game game;
	private Board board;
	private int playerNumber;
	private Player player;
	
	
	public void RandomAI(Game game, int playerNumber){
		this.game = game;
		this.player = player;
		board = game.getBoard();
		player = game.getPlayer(playerNumber);
	}
	
	@Override
	public ArrayList<Integer> getMove() {
		boolean validMoveFound = false;
		
		int x;
		int y;
		Piece piece;
		
		do{
			x = (int)(Math.random()*5);
			y = (int)(Math.random()*5);
			piece = player.getPieces().get((int)(Math.random()*player.getPieces().size()));
			validMoveFound = board.canMove(x,y,piece);
		}while(!validMoveFound);		
		
		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(x);
		arr.add(y);
		arr.add(piece.getColor());
		arr.add(piece.getType());
		return arr;
	}

}

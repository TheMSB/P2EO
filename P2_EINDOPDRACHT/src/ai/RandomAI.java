package ai;

import game.*;

import java.util.ArrayList;

/**
 * An AI who is only able to produce a random valid move.
 * @author I3anaan
 *
 */
public class RandomAI implements AI {
	
	/**
	 * The game this AI is playing in
	 */
	private Board board;
	/**
	 * The player this AI belongs to
	 */
	private Player player;
	
	/**
	 * Constructor
	 * @param game	The game on which this AI is playing
	 * @param player	The player to which this AI belongs
	 */
	public RandomAI(Game game, Player player){
		this.player = player;
		board = game.getBoard();
	}
	
	/**
	 * Checks if a random move is possible, if not it will recheck after randoming again, if the move is valid, it will return it.
	 * @return an ArrayList<Integer> containing the data of the move the AI wants to do. (x,y,type,color)
	 * @ensure the returned move is valid.
	 */
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
		arr.add(piece.getType());
		arr.add(piece.getColor());
		return arr;
	}

}

package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Cell {

	//---- Instance Variables
	private int X;
	private int Y;
	private boolean full;
	private Piece[] pieces;
	//private int owner;

	//---- Constructor ------------------------------------------

	public Cell(int x, int y) {
		this.X = x;
		this.Y = y;
		full = false;
		pieces = new Piece[4];
	}

	//---- Querries ------------------------------------------

	/**
	 * Returns the X coordinate on the Board
	 * @return X
	 */
	public int getX(){
		return X;
	}

	/**
	 * Returns the Y coordinate on the Board
	 * @return Y
	 */
	public int getY(){
		return Y;
	}

	/**
	 * Returns whether or not the Cell is full
	 * @return true if full else false
	 */
	public boolean isFull(){
		return full;
	}
	/**
	 * Returns the list of Pieces present on this cell
	 * @return pieces
	 */
	public Piece[] getPieces(){
		return pieces;
	}

	/**
	 * Returns true if the player has a piece on this cell
	 * @return true or false
	 */
	public boolean hasPieces(Piece piece){
		boolean hasPiece = false;
		for (int i = 0; i <=5; i++){
			if (pieces[i].getColor()==piece.getColor()){
				hasPiece = true;
			}
		}

		return hasPiece;
	}

	//---- Methods ------------------------------------------

	/**
	 * adds a Piece to this Cell
	 * @ensure pieces[i] does not contain piece.getType()
	 * @param piece The Piece to be placed
	 */
	protected void addPiece(Piece piece){
		if (piece.getType()==Piece.RING_0 && pieces[0]==null){
			pieces[0] = piece;
		}else if (piece.getType()==Piece.RING_1 && pieces[0]==null){
			pieces[1] = piece;
		}else if (piece.getType()==Piece.RING_2 && pieces[0]==null){
			pieces[2] = piece;
		}else if (piece.getType()==Piece.RING_3 && pieces[0]==null){
			pieces[3] = piece;
		}else if (piece.getType()==Piece.RING_4 && pieces[0]==null){
			for (int i=0;i<=4;i++){
				pieces[i]= piece;
			}
		}
	}
	/**
	 * Determines the owner of the Cell by determining the owner of
	 * each individual Piece placed in the Cell.
	 * @return Player with most Pieces in Cell
	 */
	public int determOwner(){
		int P0 =0;
		int P1 =0;
		int P2 =0;
		int P3 =0;

		for (int i=0;i<=4;i++){
			if (pieces[i].getColor()==Piece.COLOR_0){
				P0++;
			}else if (pieces[i].getColor()==Piece.COLOR_1){
				P1++;
			}else if (pieces[i].getColor()==Piece.COLOR_2){
				P2++;
			}else if (pieces[i].getColor()==Piece.COLOR_3){
				P3++;
			}
		}
		List<Integer> list = new ArrayList<Integer>();
		list.add(P0);
		list.add(P1);
		list.add(P2);
		list.add(P3);
		Collections.sort(list);

		return (list.get(list.size()-1));
	}

}

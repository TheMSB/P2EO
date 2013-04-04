package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exceptions.InvalidMoveException;
import exceptions.InvalidRingException;

/**
 * One Cell on the board of the RINGZ game.
 * Contains it's own location and an array list
 * of pieces placed on this cell.
 * Can determine the owner(winner) of this cell.
 * @author martijnbruning
 *
 */
public class Cell {

	//---- Instance Variables
	/**
	 * X coordinate of this cell.
	 */
	private int X;
	/**
	 * Y coordinate of this cell.
	 */
	private int Y;
	/**
	 * Indicator if this cell is full or not.
	 */
	private boolean full;
	/**
	 * Array with pieces that this cell is holding.
	 */
	private Piece[] pieces;

	//---- Constructor ------------------------------------------

	/**
	 * Constructs a new Cell to be placed on the board.
	 * @param x coordinate of this cell.
	 * @param y coordinate of this cell.
	 */
	public Cell(final int x, final int y) {
		this.X = x;
		this.Y = y;
		full = false;
		pieces = new Piece[5];
	}

	//---- Querries ------------------------------------------

	/**
	 * Returns the X coordinate on the Board.
	 * @return X
	 */
	public int getX() {
		return X;
	}

	/**
	 * Returns the Y coordinate on the Board.
	 * @return Y
	 */
	public int getY() {
		return Y;
	}

	/**
	 * Returns whether or not the Cell is full.
	 * @return true if full else false
	 */
	public boolean isFull() {
		return full;
	}
	/**
	 * Returns whether or not the Cell is empty.
	 * @return	true if empty else false
	 */
	public boolean isEmpty() {
		return pieces[0] == null && pieces[1] == null && pieces[2] == null && pieces[3] == null;
	}
	
	/**
	 * Returns the list of Pieces present on this cell.
	 * @return pieces
	 */
	public Piece[] getPieces() {
		return pieces;
	}
	
	/**
	 * Returns true if there is place for the given piece on this cell.
	 * @param piece
	 * @return
	 */
	public boolean pieceAllowed(final Piece piece) {
		return !isFull() && piece.getType() >= 0 && piece.getType() <= 4 && pieces[piece.getType()] == null && (piece.getType() != 4 || isEmpty());
	}

	/**
	 * Returns true if the player has a piece on this cell.
	 * @return true or false
	 */
	public boolean hasPieces(final Piece piece) {
		boolean hasPiece = false;
		for (int i = 0; i < 5; i++) {
			if (pieces[i] != null && pieces[i].getColor() == piece.getColor()) {
				hasPiece = true;
			}
		}
		//System.out.println("HasPiece:  "+hasPiece +"   C: "+piece.getColor());
		return hasPiece;
	}
	
	/**
	 * Checks if the given piece is allowed to place himself next to this Cell.
	 * (checking for megastones)
	 * @arg piece	The piece you want to place next to this cell
	 * @return  True if it is allowed to place next to this cell
	 */
	public boolean megaStoneCheck(final Piece piece) {
		boolean output = true;
		if (piece.getType() == util.Protocol.RING_4) {
			if (pieces[4] != null && pieces[4].getColor() == piece.getColor()) {
				output = false;
			}
		}
		
		return output;
	}


	//---- Methods ------------------------------------------

	/**
	 * adds a Piece to this Cell.
	 * @ensure pieces[i] does not contain piece.getType()
	 * @require piece!=null
	 * @param piece The Piece to be placed
	 */
	protected void addPiece(final Piece piece) throws InvalidMoveException {
		
		if (piece.getType() < 4 && pieces[piece.getType()] == null) {
			pieces[piece.getType()] = piece;
		} else if (piece.getType() == Piece.RING_4 && isEmpty()) {
			pieces[4] = piece;
		} else {
			throw new InvalidRingException();
		}
		if (pieces[4] != null || (pieces[0] != null && pieces[1] != null && pieces[2] != null && pieces[3] != null)) {
			full = true;
		}
	}
	/**
	 * Determines the owner of the Cell by determining the owner of
	 * each individual Piece placed in the Cell.
	 * @return ArrayList with scores (going p1>p4)
	 */
	public ArrayList<Integer> getOwnerList() {
		int p0 = 0;
		int p1 = 0;
		int p2 = 0;
		int p3 = 0;

		for (int i = 0; i < 4; i++) {
			if (pieces[i] != null) { 
				if (pieces[i].getColor() == PlayerColor.COLOR_0) {
					p0++;
				} else if (pieces[i].getColor() == PlayerColor.COLOR_1) {
					p1++;
				} else if (pieces[i].getColor() == PlayerColor.COLOR_2) {
					p2++;
				} else if (pieces[i].getColor() == PlayerColor.COLOR_3) {
					p3++;
				}
			}
		}
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(p0);
		list.add(p1);
		list.add(p2);
		list.add(p3);

		return list;
	}
	
	/**
	 * Determines the Owner or 'winner' of this cell.
	 * Method is used to calculate scores during a
	 * GameOver event.
	 * @return
	 */
	public int determOwner() {
		int winner = util.Util.getIndexOfMax(getOwnerList());
		//	ties result in no score
		System.out.println("DetOwner: " + this.X + "," + this.Y + "  :  " + getOwnerList() + "  " + winner);
		return winner;
	}
	
	/**
	 * Method used for debugging
	 * purposes mainly. returns
	 * a textual description of the
	 * contents of this cell.
	 */
	public String toString() {
		String output = "";
		for (Piece p : pieces) {
			output = output + p + " | ";
		}
		
		return output;
	}
	
}

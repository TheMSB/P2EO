package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	//private int owner;

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
		pieces = new Piece[4];
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
	 * Returns the list of Pieces present on this cell.
	 * @return pieces
	 */
	public Piece[] getPieces() {
		return pieces;
	}

	/**
	 * Returns true if the player has a piece on this cell.
	 * @return true or false
	 */
	public boolean hasPieces(final Piece piece) {
		boolean hasPiece = false;
		for (int i = 0; i < 4; i++) {
			//System.out.println("Piece: "+pieces[i]);
			if (pieces[i] != null && pieces[i].getColor() == piece.getColor()) {
				hasPiece = true;
			}
		}

		return hasPiece;
	}

	//---- Methods ------------------------------------------

	/**
	 * adds a Piece to this Cell.
	 * @ensure pieces[i] does not contain piece.getType()
	 * @param piece The Piece to be placed
	 */
	protected void addPiece(final Piece piece) {
		if (piece.getType() == Piece.RING_0 && pieces[0] == null) {
			pieces[0] = piece;
		} else if (piece.getType() == Piece.RING_1 && pieces[1] == null) {
			pieces[1] = piece;
		} else if (piece.getType() == Piece.RING_2 && pieces[2] == null) {
			pieces[2] = piece;
		} else if (piece.getType() == Piece.RING_3 && pieces[3] == null) {
			pieces[3] = piece;
		} else if (piece.getType() == Piece.RING_4 && pieces[0] == null) {
			for (int i = 0; i < 4; i++) {
				pieces[i] = piece;
			}
		}
	}
	/**
	 * Determines the owner of the Cell by determining the owner of
	 * each individual Piece placed in the Cell.
	 * @return Player with most Pieces in Cell
	 */
	public int determOwner() {
		int p0 = 0;
		int p1 = 0;
		int p2 = 0;
		int p3 = 0;

		for (int i = 0; i < 4; i++) {
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
		List<Integer> list = new ArrayList<Integer>();
		list.add(p0);
		list.add(p1);
		list.add(p2);
		list.add(p3);
		Collections.sort(list);

		return list.get(list.size() - 1);
	}

}

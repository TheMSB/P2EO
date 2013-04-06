package game;

import java.util.ArrayList;

import exceptions.InvalidCellException;
import exceptions.InvalidMoveException;

/**
 * The Board used to play the RINGZ game. Contains dimensions of the board and
 * an arraylist with all the cells. Can determine if a move is valid for a
 * certain player.
 * 
 * @author martijnbruning
 * 
 */
public class Board {

	// ---- Constants

	/**
	 * The DIMension of the board.
	 */
	public static final int DIM = 5;
	/**
	 * The X coordinate length of the board.
	 */
	public static final int X = DIM;
	/**
	 * The Y coordinate length of the board.
	 */
	public static final int Y = DIM;

	// -- Instance variables -----------------------------------------

	/**
	 * The DIM*DIM Cells that make up the board.
	 */
	private Cell[][] cells;

	// -- Constructor -----------------------------------------

	/**
	 * Constructs the board that is used by this game.
	 */
	public Board() {
		cells = new Cell[X][Y];
		for (int x = 0; x < X; x++) {
			for (int y = 0; y < Y; y++) {
				cells[x][y] = new Cell(x, y);
				// System.out.println(x+" "+y);
			}
		}
	}

	// -- Queries -----------------------------------------

	/**
	 * Returns true if (x,y)-address is a valid Cell on the Board.
	 * 
	 * @require cells[x][y] != null
	 * @ensure <code>result == 0 <= x,y < DIM</code>
	 * @return true if <code>0 <= x < DIM && 0 <= y < DIM</code>
	 */
	public boolean isCell(final int x, final int y) {
		return 0 <= x && x < X && 0 <= y && y < Y;
	}

	/**
	 * Returns Cell <code>X</code><code>Y</code>.
	 * 
	 * @require <code>this.isCell(X,Y)</code>
	 * @param x
	 * @param y
	 *            Valid coordinates of the Cell.
	 * @return the state of the Cell
	 */
	public Cell getCell(final int x, final int y) {
		return cells[x][y];
	}

	/**
	 * Returns the array of cells for this board.
	 * 
	 * @return cells Array
	 */
	public Cell[][] getCells() {
		return cells;
	}

	// -- Methods -----------------------------------------

	/**
	 * Method used to place the starting stone.
	 * 
	 * @param x
	 *            coordinate of the cell
	 * @param y
	 *            coordinate of the cell
	 */
	protected void startStone(final int x, final int y) throws InvalidMoveException {
		cells[x][y].addPiece(new Piece(0, 0));
		cells[x][y].addPiece(new Piece(1, 1));
		cells[x][y].addPiece(new Piece(2, 2));
		cells[x][y].addPiece(new Piece(3, 3));
	}

	/**
	 * Finds the designated cell and places the piece.
	 * 
	 * @param x
	 *            Coordinate of the cell
	 * @param y
	 *            Coordinate of the cell
	 * @param piece
	 *            The piece to place
	 */
	protected void move(final int x, final int y, final Piece piece) throws InvalidMoveException{
		if (0 <= x && x <= Board.DIM && 0 <= y && y <= Board.DIM && canMove(x, y, piece)) {
			cells[x][y].addPiece(piece);
		} else {
			throw new InvalidCellException();
		}
	}

	/**
	 * Checks if the piece can be placed in the selected cell.
	 * 
	 * @param x
	 *            Coordinate of the cell
	 * @param y
	 *            Coordinate of the cell
	 * @param piece
	 *            Piece to place
	 * @return true if valid move else false
	 */
	public boolean canMove(final int x, final int y, final Piece piece) {
		boolean pieceAllowed = isCell(x, y) && getCell(x, y).pieceAllowed(piece);
		boolean canMove = false;
		boolean megaStoneAllowed = true;

		for (int off = -1; off <= 1; off += 2) {
			if ((isCell(x + off, y) && getCell(x + off, y).hasPieces(piece))
					|| (isCell(x, y + off) && getCell(x, y + off).hasPieces(piece))) {
				canMove = true;
			}
			if ((isCell(x + off, y) && !getCell(x + off, y).megaStoneCheck(piece))
					|| (isCell(x, y + off) && !getCell(x, y + off).megaStoneCheck(piece))) {
				megaStoneAllowed = false;
			}
		}

		return pieceAllowed && canMove && megaStoneAllowed;
	}

	/**
	 * Returns the score where index is equal to color.
	 * @return
	 * @ensure	result.length==4;
	 */
	public Integer[] getScore() {
		Integer[] scores = new Integer[4];
		scores[0] = 0;
		scores[1] = 0;
		scores[2] = 0;
		scores[3] = 0;
		for (int x = 0; x < X; x++) {
			for (int y = 0; y < Y; y++) {
				if (cells[x][y].determOwner() != -1) {
					scores[cells[x][y].determOwner()] = scores[cells[x][y].determOwner()] + 1;
				}

			}
		}

		return scores;
	}
}

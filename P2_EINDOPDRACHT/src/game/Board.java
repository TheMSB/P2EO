package game;

/**
 * The Board used to play the RINGZ game.
 * @author martijnbruning
 *
 */
public class Board {

	//---- Constants

	/**
	 * The DIMension of the board.
	 */
	public static final int DIM = 4;
	//private static final int AREA = DIM*DIM;
	/**
	 * The X coordinate length of the board.
	 */
	private static final int	  X = DIM;
	/**
	 * The Y coordinate length of the board.
	 */
	private static final int	  Y = DIM;

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
			}
		}
	}

	// -- Queries -----------------------------------------

	/**
	 * Returns true if (x,y)-address is a valid Cell
	 * on the Board.
	 * @require cells[x][y] != null
	 * @ensure  <code>result == 0 <= x,y < DIM</code>
	 * @return  true if <code>0 <= x < DIM && 0 <= y < DIM</code>
	 */
	public boolean isCell(final int x, final int y) {
		return 0 <= x && x < X && 0 <= y && y < Y;
	}


	/**
	 * Returns Cell <code>X</code><code>Y</code>.
	 * @require <code>this.isCell(X,Y)</code>
	 * @param   x
	 * @param   y Valid coordinates of the Cell.
	 * @return  the state of the Cell
	 */
	public Cell getCell(final int x, final int y) {
		return cells[x][y];
	}

	// -- Methods -----------------------------------------
	
	/**
	 * Method used to place the starting stone.
	 * @param x coordinate of the cell
	 * @param y coordinate of the cell
	 * @param piece to be placed
	 */
	protected void startStone(final int x, final int y, final Piece piece) {
		cells[x][y].addPiece(piece);
	}
	/**
	 * Finds the designated cell and places the piece.
	 * @param x Coordinate of the cell
	 * @param y Coordinate of the cell
	 * @param piece The piece to place
	 */
	protected void move(final int x, final int y, final Piece piece) {
		if (canMove(x, y, piece)) {
			cells[x][y].addPiece(piece);
		} //TODO else invalid move exception
	}
	
	/**
	 * Checks if the piece can be placed in the selected cell.
	 * @param x Coordinate of the cell
	 * @param y Coordinate of the cell
	 * @param piece Piece to place
	 * @return true if valid move else false
	 */
	public boolean canMove(final int x, final int y, final Piece piece) {
		boolean canMove = false;
		if (isCell(x - 1, y)) {
			if (getCell(x - 1, y).hasPieces(piece)) {
				canMove = true;
			}
		} 
		if (isCell(x + 1, y)) {
			if (getCell(x + 1, y).hasPieces(piece)) {
				canMove = true;
			}
		}
		if (isCell(x, y - 1)) {
			if (getCell(x, y - 1).hasPieces(piece)) {
				canMove = true;
			}
		}
		if (isCell(x, y + 1)) {
			if (getCell(x, y + 1).hasPieces(piece)) {
				canMove = true;
			}
		}
		
		return canMove;
	}
}

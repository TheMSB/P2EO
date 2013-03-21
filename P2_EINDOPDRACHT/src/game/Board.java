package game;


public class Board {

	//---- Constants

	public static final int DIM = 4;
	//private static final int AREA = DIM*DIM;
	private static final int	  X = DIM;
	private static final int	  Y = DIM;

	// -- Instance variables -----------------------------------------

	/**
	 * The DIM*DIM Cells that make up the board
	 */
	private Cell[][] cells;

	// -- Constructor -----------------------------------------

	public Board() {
		cells = new Cell[X][Y];
		for (int x = 0;x<X;x++){
			for (int y = 0;y<Y;y++){
				cells[x][y]=new Cell(x,y);
			}
		}
	}

	// -- Queries -----------------------------------------

	/**
	 * Returns true if (x,y)-address is a valid Cell
	 * on the Board
	 * @ensure  <code>result == 0 <= x,y < DIM</code>
	 * @return  true if <code>0 <= x < DIM && 0 <= y < DIM</code>
	 */
	public boolean isCell(int x, int y) {
		return (0<=x && x<=X && 0<= y && y<=Y && cells[x][y] != null);
	}


	/**
	 * Returns Cell <code>X</code><code>Y</code>.
	 * @require <code>this.isCell(X,Y)</code>
	 * @param   X and Y are vallid coordinates of the Cell
	 * @return  the state of the Cell
	 */
	public Cell getCell(int x,int y) {
		return cells[x][y];
	}

	// -- Methods -----------------------------------------
	
	/**
	 * Finds the designated cell and places the piece
	 * @param x Coordinate of the cell
	 * @param y Coordinate of the cell
	 * @param piece The piece to place
	 */
	protected void move(int x, int y, Piece piece){
		if (canMove(x,y,piece)){
			cells[x][y].addPiece(piece);
		}//TODO else invalid move exception
	}
	
	/**
	 * Checks if the piece can be placed in the selected cell
	 * @param x Coordinate of the cell
	 * @param y Coordinate of the cell
	 * @param piece Piece to place
	 * @return true if valid move else false
	 */
	public boolean canMove(int x, int y, Piece piece){
		boolean canMove = false;
		if(isCell(x-1,y)){
			if (getCell(x-1,y).hasPieces(piece)){
				canMove = true;
			}
		}if(isCell(x+1,y)){
			if (getCell(x+1,y).hasPieces(piece)){
				canMove = true;
			}
		}if(isCell(x,y-1)){
			if (getCell(x,y-1).hasPieces(piece)){
				canMove = true;
			}
		}if(isCell(x,y+1)){
			if (getCell(x,y+1).hasPieces(piece)){
				canMove = true;
			}
		}
		
		return canMove;
	}
	//TODO moved method
}

package game;


public class Board {

	//---- Constants

	public static final int DIM = 4;
	private static final int AREA = DIM*DIM;
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
}

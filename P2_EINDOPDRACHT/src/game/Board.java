package game;


public class Board {

	//---- Constants

	public static final int DIM = 4;
	private static final int	  X = DIM;
	private static final int	  Y = DIM;

	// -- Instance variables -----------------------------------------

	/**
	 * The DIM*DIM Cells that make up the board
	 */
	private Cell[][] cells;

	// -- Constructor -----------------------------------------

	public Board() {
		for (int x = 0;x<X;x++){
			for (int y = 0;y<Y;y++){
				cells = new Cell[x][y];
			}
		}
	}

	// -- Methods -----------------------------------------

	/**
	 * Returns true if (x,y)-addres is a vallid Cell
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

	
}

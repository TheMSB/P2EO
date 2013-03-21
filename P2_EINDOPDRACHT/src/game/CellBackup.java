package game;

public class Cell {

	//---- Instance Variables
	private int X;
	private int Y;
	private boolean full;
	private Piece[] pieces;
	//private Color owner;

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

	//---- Methods ------------------------------------------

	/**
	 * adds a Piece to this Cell
	 * @ensure pieces[i] does not contain piece.getType()
	 * @param piece The Piece to be placed
	 */
	public void addPiece(Piece piece){
		if (piece.getType()==Piece.RING_0){
			pieces[0] = piece;
		}else if (piece.getType()==Piece.RING_1){
			pieces[1] = piece;
		}else if (piece.getType()==Piece.RING_2){
			pieces[2] = piece;
		}else if (piece.getType()==Piece.RING_3){
			pieces[3] = piece;
		}else if (piece.getType()==Piece.RING_4){
			for (int i=0;i<=4;i++){
				pieces[i]= piece;
			}
		}
	}
	
	public void determOwner(){
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
		for (int i=0;i<=4;i++){
			
		}
		
	}

}

package game;

public class Cell {

	//---- Instance Variables
	private int X;
	private int Y;
	private boolean full;
	private Piece[] pieces;
	
	//---- Constructor ------------------------------------------
	
	public Cell(int x, int y) {
		this.X = x;
		this.Y = y;
		full = false;
	}

	//---- Methods ------------------------------------------
	
	public int getX(){
		return X;
	}
	
	public int getY(){
		return Y;
	}
	
	public boolean isFull(){
		return full;
	}
	
}

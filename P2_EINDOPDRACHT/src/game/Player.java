package game;


import java.util.ArrayList;

public class Player {

	//---- Instance Variables ---------
	private String name;
	private PlayerColor color;
	private ArrayList<Piece> inventory;
	
	//---- Constructor ------------------------------------------
	public Player(String name, int color) {
		this.name = name;
		this.color = new PlayerColor(this,color);
		
	}

	// -- Queries -----------------------------------------
	public String getName(){
		return name;
	}
	
	public int getColor(){
		return color.getColor(this);
	}
	public ArrayList<Piece> getPieces(){
		return inventory;
	}
	
	// -- Methods -----------------------------------------
	
	/**
	 * Adds specified piece to the players inventory, only pieces in inventory
	 * can be used by the player.
	 * @param piece The Piece to add.
	 */
	public void addPiece(Piece piece){
		inventory.add(piece);
	}

}

package game;


import java.util.ArrayList;

import exceptions.InvalidPieceException;

/**
 * The Player class for the RINGZ.
 * contains name, playerColor and inventory instance variables.
 * Inventory holds all Pieces this player can use, when placed
 * a piece is removed from the inventory.
 * 
 * @author martijnbruning
 *
 */
public class Player {

	//---- Instance Variables ---------
	/**
	 * The Players name.
	 */
	private String name;
	/**
	 * The Color associated with this player,
	 * only this Color grants him points.
	 */
	private PlayerColor color;
	/**
	 * The inventory of this Player, contains
	 * all the Pieces he can use.
	 */
	private ArrayList<Piece> inventory;
	
	//---- Constructor ------------------------------------------
	/**
	 * Constructs a new Player using the given name and color.
	 * @param pname The name of this Player.
	 * @param pcolor The color of this Player.
	 */
	public Player(final String pname, final int pcolor) {
		this.name = pname;
		this.color = new PlayerColor(this, pcolor);
		inventory = new ArrayList<Piece>(); // Dit was ik vergeten te init daardoor null err
		
	}

	// -- Queries -----------------------------------------
	/**
	 * Returns the name of this Player.
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Returns the color of this Player.
	 * @return color
	 */
	public int getColor() {
		return color.getColor(this);
	}
	/**
	 * Returns the inventory of this Player.
	 * @return inventory
	 */
	public ArrayList<Piece> getPieces() {
		return inventory;
	}
	/**
	 * Returns a piece from the inventory.
	 * @param typ Type of the piece to return
	 * @param colr Color of the piece to return
	 * @return Piece from inventory with sleceted type and color
	 */
	public Piece getPiece(final int typ, final int colr) throws InvalidPieceException{
		Piece output = null;
		for (Piece p : inventory) {
			if (p.getType() == typ && p.getColor() == colr) {
				output = p;
			}
		}
		if(output==null){
			throw new InvalidPieceException();
		}
		return output;
	}
	
	/**
	 * Returns the availability for each type, in an array where index = type
	 * @param color
	 * @return
	 */
	public Integer[] getAvailability(int color){
		Integer[] output = new Integer[5];
		for(int i=0;i<output.length;i++){
			output[i] = 0;
		}
		for(Piece p : inventory){
			 if(p.getColor()==color){
				 //System.out.println(p+"   "+p.getType());
				 //System.out.println(output);
				 //System.out.println(output[p.getType()]);
				 output[p.getType()] = output[p.getType()] + 1;
			 }
		}
		
		return output;
	}
	
	// -- Methods -----------------------------------------
	
	/**
	 * Adds specified piece to the players inventory, only pieces in inventory
	 * can be used by the player.
	 * @param piece The Piece to add.
	 */
	public void addPiece(final Piece piece) {
		inventory.add(piece);
	}
	
	

}

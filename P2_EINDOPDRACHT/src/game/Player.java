package game;


import java.util.ArrayList;

/**
 * The Player class for the RINGZ.
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
	public Piece getPiece(final int typ, final int colr) {
		//if (inventory.contains(new Piece(typ, colr))) {
			return inventory.get(inventory.indexOf(new Piece(typ, colr)));
		//}//TODO else no such piece exception
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

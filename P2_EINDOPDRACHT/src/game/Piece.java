package game;

/**
 * The Piece class used by the RINGZ game.
 * @author martijnbruning
 *
 */
public class Piece {

	//---- Constants ----------------
	//TODO evt updaten naar protocol referentie
	//CHECKSTYLE:OFF
	static final int RING_0 = 0;
	static final int RING_1 = 1;
	static final int RING_2 = 2;
	static final int RING_3 = 3;
	static final int RING_4 = 4;
	//CHECKSTYLE:ON
	
	//---- Instant Variables ----------
	/**
	 * The type of this Piece.
	 */
	private int type;
	/**
	 * The color of this Piece.
	 */
	private int color;
	/**
	 * Has this Piece been placed?
	 */
	private boolean placed;
	
	
	/**
	 * Creates a new unplaced Piece using the <code>color</code> <code>c</code>
	 * and <code>type</code> <code>t</code>.
	 * @ensure <code>placed</code> = false
	 * @param t The type of the <code>Piece</code>
	 * @param c The color of the <code>Piece</code>
	 */
	public Piece(final int t, final int c) {
		
		placed = false;
		//TODO type is t
		if (0 <= t && t < 5) {
			type = t;
		}
		if (0 <= c && c < 4) {
			color = c;
		}
	}
	
	// -- Queries -----------------------------------------
	/**
	 * Used to return the type of the Piece.
	 * @ensure type == RING_0 | RING_1 | RING_2 | RING_3 | RING_4
	 * @return type
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Used to return the color of the Piece.
	 * @ensure color == COLOR_0 | COLOR_1 | COLOR_2 | COLOR_3
	 * @return color
	 */
	
	public int getColor() {
		return color;
	}
	
	// -- Methods -----------------------------------------
	
	/**
	 * used to determine if this Piece has been placed already,
	 * if set to true the Piece will become unusable by the player.
	 */
	
	public void setPlaced() {
		placed = true;
	}
	
	public String toString(){
		return "T "+this.type + " C "+color;
	}

}

package game;

public class Piece {

	//---- Constants ----------------
	
	final static int RING_0 =0;
	final static int RING_1 =1;
	final static int RING_2 =2;
	final static int RING_3 =3;
	final static int RING_4 =4;
	
	final static int COLOR_0 =0;
	final static int COLOR_1 =1;
	final static int COLOR_2 =2;
	final static int COLOR_3 =3;
	
	//---- Instant Variables ----------
	int type;
	int color;
	boolean placed;
	
	
	/**
	 * Creates a new unplaced Piece using the <code>color</code> <code>c</code>
	 * and <code>type</code> <code>t</code>
	 * @ensure <code>placed</code> = false
	 * @param t The type of the <code>Piece</code>
	 * @param c The color of the <code>Piece</code>
	 */
	public Piece(int t, int c) {
		
		placed = false;
		
		if(t == 0){
			type = RING_0;
		}else if(t == 1){
			type = RING_1;
		}else if(t == 2){
			type = RING_2;
		}else if(t == 3){
			type = RING_3;
		}else if(t == 4){
			type = RING_4;
		} if(c == 0){
			color = COLOR_0;
		}else if(c == 1){
			color = COLOR_1;
		}else if(c == 2){
			color = COLOR_2;
		}else if(c == 3){
			color = COLOR_3;
		}
	}
	
	// -- Queries -----------------------------------------
	/**
	 * Used to return the type of the Piece
	 * @ensure type == RING_0 | RING_1 | RING_2 | RING_3 | RING_4
	 * @return type
	 */
	public int getType(){
		return type;
	}
	
	/**
	 * Used to return the color of the Piece
	 * @ensure color == COLOR_0 | COLOR_1 | COLOR_2 | COLOR_3
	 * @return color
	 */
	
	public int getColor(){
		return color;
	}
	
	// -- Methods -----------------------------------------
	
	/**
	 * used to determine if this Piece has been placed already,
	 * if set to true the Piece will become unusable by the player
	 */
	
	public void setPlaced(){
		placed = true;
	}

}

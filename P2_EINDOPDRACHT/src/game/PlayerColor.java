package game;


public class PlayerColor {

	//---- Constants ----------------
	static final int COLOR_0 = 0;
	static final int COLOR_1 = 1;
	static final int COLOR_2 = 2;
	static final int COLOR_3 = 3;
	
	//---- Instance Variables ---------
	private Player PLAYER_0;
	private Player PLAYER_1;
	private Player PLAYER_2;
	private Player PLAYER_3;

	
	//---- Constructor ------------------------------------------
	//TODO make a definite design on how to handle colors
	public PlayerColor(){
		
	}
	
	/**
	 * Creates a new PlayerColor, determined by the input int color
	 * and translated into a color from this game
	 * @param color
	 */
	public PlayerColor(Player player,int color) {
		if(color == COLOR_0 &&PLAYER_0==null){
			PLAYER_0 = player;
		}else if(color == COLOR_1 &&PLAYER_1==null){
			PLAYER_1 = player;
		}else if(color == COLOR_2 &&PLAYER_2==null){
			PLAYER_2 = player;
		}else if(color == COLOR_3 &&PLAYER_3==null){
			PLAYER_3 = player;
		}
	}
	
	// -- Queries -----------------------------------------
	/**
	 * Returns the Player that belongs to the numbered color
	 * @param color The int value indicating a color
	 * @return Player belonging to color
	 */
	public Player getPlayer(int color){
		Player player = null;
		if(color == COLOR_0){
			player = PLAYER_0;
		}else if(color == COLOR_1){
			player = PLAYER_1;
		}else if(color == COLOR_2){
			player = PLAYER_2;
		}else if(color == COLOR_3){
			player = PLAYER_3;
		}
		return player;
	}
	
	/**
	 * Returns the Color that belongs to the Player
	 * @param player The player who's color should be returned
	 * @return Color Belonging to the Player
	 */
	public int getColor(Player player){
		int color = 5;
			if(player == PLAYER_0){
				color = COLOR_0;
			}else if(player == PLAYER_1){
				color = COLOR_1;
			}else if(player == PLAYER_2){
				color = COLOR_2;
			}else if(player == PLAYER_3){
				color = COLOR_3;
			}
		return color;
	}

}

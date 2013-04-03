package game;

import java.util.ArrayList;
import java.util.Observable;

import exceptions.InvalidMoveException;
import exceptions.InvalidPieceException;

/**
 * Main Game class for the RINGZ game.
 * Assigns the players to a color and greates a new
 * board for the game to be played on.
 * Can determine who's turn it is and relay moves
 * for a player to board.
 * @author martijnbruning
 *
 */
public class Game extends Observable {

	//---- Instance Variables

	/**
	 * Array of players playing this game.
	 */
	private ArrayList<Player> players;
	private ArrayList<Player>	playersConnected;
	/**
	 * The Board that this game is played on.
	 */
	private Board board;
	/**
	 * The turn this game is on.
	 */
	private int turn;
	/**
	 * The amount of players playing this game.
	 */
	private int playerCount;

	//---- Constructor ------------------------------------------

	/**
	 * Constructs a new game using the four given strings
	 * as names for the players. Then, assigns colors to the
	 * newly created players and runs the setUp method.
	 * @param playernames List with the name of players
	 * @throws InvalidMoveException when the x,y from the startstone are wrong
	 */
	public Game(final int x, final int y, final ArrayList<String> playernames) throws InvalidMoveException {

		playerCount = playernames.size();

		board = new Board();
		players = new ArrayList<Player>();
		//Creates players depending on the playerCount
		//TODO dit beter maken
		if(playerCount==2){
			players.add(new Player(playernames.get(0), PlayerColor.COLOR_0));
			players.add(new Player(playernames.get(1), PlayerColor.COLOR_2));
		}else if(playerCount==3){
			players.add(new Player(playernames.get(0), PlayerColor.COLOR_0));
			players.add(new Player(playernames.get(1), PlayerColor.COLOR_1));
			players.add(new Player(playernames.get(2), PlayerColor.COLOR_2));
		}else if(playerCount==4){
			players.add(new Player(playernames.get(0), PlayerColor.COLOR_0));
			players.add(new Player(playernames.get(1), PlayerColor.COLOR_1));
			players.add(new Player(playernames.get(2), PlayerColor.COLOR_2));
			players.add(new Player(playernames.get(3), PlayerColor.COLOR_3));
		}
		playersConnected = players;
		
		setUpGame(x, y, playerCount);
		turn = 0;
	}

	//---- Queries ------------------------------------------
	/**
	 * Returns the player with the indicated index from the array.
	 * @param p Index of the player
	 * @return player
	 */
	public Player getPlayer(final int p) {
		return players.get(p);
	}
	
	public int getPlayerCount(){
		return playerCount;
	}
	/**
	 * Returns index of the player with given name from the array.
	 * @param p Player
	 * @return Index of player
	 */
	public Player getPlayer(final String n) {
		Player output = null;
		for (Player p : players) {
			if (p.getName().equals(n)) {
				output = p;
			}
		}
		return output;
	}
	/**
	 * Returns the current turn.
	 * @return turn
	 */
	public int getTurn() {
		return turn;
	}
	/**
	 * Returns the board that is currently used by this game.
	 * @return
	 */
	public Board getBoard() {
		return board;
	}

	//---- Methods ------------------------------------------
	/**
	 * Sets up the game by adding the Pieces to all the players inventory
	 * Determines how many Pieces each Player gets depending on the number
	 * of players playing this game.
	 * @param playercount The number of players playing
	 */
	protected void setUpGame(final int x, final int y, final int playercount) throws InvalidMoveException { // 4 Player Game
		// Creates the starting stone
		
		board.startStone(x, y);

		if (playercount == 4) {
			for (int pl = 0; pl < 4; pl++) { // Player loop
				for (int t = 0; t < 5; t++) { // Piece type loop
					for (int pc = 0; pc < 3; pc++) { // Piece amount loop
						Player player = players.get(pl);
						player.addPiece(new Piece(t, player.getColor()));
					}
				}
			}
		} else if (playerCount == 3) { // 3 Player Game
			for (int pl = 0; pl < 3; pl++) { // Player loop
				for (int t = 0; t < 5; t++) { // Piece type loop
					for (int pc = 0; pc < 3; pc++) { // Piece amount loop
						players.get(pl).addPiece(new Piece(t, players.get(pl).getColor()));
					}
					players.get(pl).addPiece(new Piece(t, players.get(3).getColor()));
				}
			}
		} else if (playerCount == 2) { // 2 Player Game
			for (int pl = 0; pl < 2; pl++) { // Player loop
				for (int t = 0; t < 5; t++) { // Piece type loop
					for (int pc = 0; pc < 3; pc++) { // Piece amount loop
						players.get(pl).addPiece(new Piece(t, players.get(pl).getColor()));
						// The Xtra piece, players get their color + color of 1 other player
						players.get(pl).addPiece(new Piece(t, players.get(pl).getColor() + 1));
					}
				}
			}
		}
		
		//System.out.println(players.get(0).getPieces());
		//System.out.println(players.get(1).getPieces());
		//TODO else invalid player number exception
	}

	/**
	 * Places a piece on the selected cell
	 * returns an exception if the move is invalid.
	 * @param x Coordinate of the cell
	 * @param y Coordinate of the cell
	 * @param type of the piece
	 * @param color of the piece
	 * @throws InvalidMoveException
	 */
	public void move(final int x, final int y, final int type, final int color) throws InvalidMoveException {

		Piece movpc = players.get(turn).getPiece(type, color);
		board.move(x, y, movpc);
		int pindex = players.get(turn).getPieces().indexOf(movpc);
		players.get(turn).getPieces().remove(pindex).setPlaced();
		//TODO kijken naar setPlaced(),mogelijk in cell doen
		turn = (turn + 1) % players.size();
	}
	
	// methode doet verder niets met conclusie, dit bespreken met Derk wat
	// te doen.
	/**
	 * Checks if the game has ended, this is achieved
	 * by running an iterative loop over all the cells
	 * in the game and checking if a player canMove
	 * there. Additionally it also checks if a players
	 * inventory is empty, meaning he can no longer perform a move
	 * either. Lastly this method also checks if all fields are full
	 * so that no player can perform a move. SHOULD be impossible with
	 * current playercount and field size but is still included for scale ability.
	 * 
	 * @require		The Player who has the turn (turn is his name) has not yet made a move
	 */
	public boolean isGameOver(){
		boolean gameOver = false;
		ArrayList<Player> playersToRemove = new ArrayList<Player>();
		
		//Make a new list of players, without those who cant do a move
		for(Player player : players){
			if(!canDoMove(player)){
				playersToRemove.add(player);
			}
			System.out.println(player.getPieces());
		}
		//Checks if any of those players in the new list indexes are before or equal to the one having the turn, if so lower turn by 1 for each
		for(int i=0;i<playersToRemove.size();i++){
			if(players.indexOf(playersToRemove.get(i))<turn){
				turn--;				
			}
			players.remove(playersToRemove.get(i));
		}
		turn = turn%players.size();
		
		return players.size()==1;
	}
	
	/**
	 * Checks for each cell if the player has a piece that he can place
	 * If there is 1 or more move available result = true;
	 * @param player	Player to check if he can do a move
	 * @return
	 */
	public boolean canDoMove(Player player){
		boolean canMove = false;
		for (int x = 0; x < Board.X; x++) {
			for (int y = 0; y < Board.Y; y++) {
				for(Piece p : player.getPieces()){
					if(board.canMove(x, y,p)){
						canMove = true;
						break;
						//System.out.println(board.getCell(x,y));
					}
				}
			}
		}
		
		return canMove;
	}
	
	/**
	 * Returns an array of integers, representing the score and pieces available.
	 * 
	 * @return
	 * @ensure	result.size() = 2n
	 * 			result.get(n) = score
	 * 			result.get(n+1) = stones left
	 * 			(n being an integer)
	 */
	public ArrayList<Integer> getStats(){
		ArrayList<Integer> results = new ArrayList<Integer>();
		Integer[] score = board.getScore();
		for(int i =0;i<playersConnected.size();i++){
			if(playersConnected.size()==2){
				results.add(score[i*2] + score[i*2+1]);
				results.add(playersConnected.get(i).getPieces().size());
			}else{
				results.add(score[i]);
				results.add(playersConnected.get(i).getPieces().size());
			}
			
		}
		return results;
	}
}

package server;

import java.util.ArrayList;
import game.*;
import java.util.Collections;

import exceptions.InvalidMoveException;

/**
 * A Lobby is basically a group of clients put together, to wait for or play a
 * game. Once the Lobby has filled up enough slots (given on construction) it
 * will start a Game and inform the clients. The game will the be played out
 * where ClientHandler will receive the commands from the client and parse them
 * to Lobby, which will check the game if those commands are valid and if so
 * will send it to the other clients in the Lobby.
 * 
 * @author I3anaan
 * 
 */
public class Lobby {

	/**
	 * Array of clients in this lobby
	 * 
	 * @invariant ClientHandlers cannot be null
	 */
	private ArrayList<ClientHandler> clients;
	/**
	 * Maximum Number of slots this lobby has
	 */
	private int slots;
	/**
	 * The server to which this Lobby belongs
	 */
	private Server server;
	/**
	 * The status of the lobby
	 * 
	 * @invariant Each ClientHandler in clients will have the same status as
	 *            this lobby
	 */
	private int status;
	/**
	 * The Game which the lobby uses to check things like moves and winners
	 */
	private Game game;
	/**
	 * Name of the player whos turn it is
	 */
	private String turn;

	/**
	 * Makes a new lobby, and adds the first client
	 * 
	 * @param slots
	 * @param client
	 *            first client to be connected
	 * @require 2<=slots<=4 client != null
	 * @ensure clients.size()>0
	 */
	public Lobby(int slots, ClientHandler client, Server server) {
		this.server = server;
		this.slots = slots;
		Server.out.println("Made new Lobby with " + slots + " slots");
		clients = new ArrayList<ClientHandler>();
		addClient(client);
		status = ClientHandler.INLOBBY;
	}

	/**
	 * Forwards the move command into the game, which will check if it is a
	 * valid move and if so will update its board accordingly
	 * 
	 * @param args
	 *            the move to be done (x,y,type,color)
	 * @throws InvalidMoveException
	 * @require This is a real move coming from someone whos turn it is
	 *          args.size()==4
	 * @ensure If the move is valid, every client in the lobby will be informed
	 *         of that move, and the next turn will be given out
	 * 
	 */
	public void move(ArrayList<Integer> args) {
		try{
			game.move(args.get(0), args.get(1), args.get(2), args.get(3));
			broadcastMessage(util.Protocol.CMD_MOVED + " "
					+ util.Util.concatArrayList(args));
			giveTurn();
		}catch (InvalidMoveException e) {
			System.out.println("InvalidMoveDetected");
			e.printStackTrace();
			endLobby();
		}
	}

	/**
	 * Gives the turn over to the next person, meaning it will send a TURN
	 * command to everyone
	 */
	private void giveTurn() {
		//System.out.println("GameOver:  "+game.isGameOver());
		if(!game.isGameOver()){
			turn = clients.get(game.getTurn()).getClientName();
			broadcastMessage(util.Protocol.CMD_TURN + " " + turn);
		}else{
			endLobby();
		}
	}

	/**
	 * Adds a client to this lobby, ie letting him join.
	 * 
	 * @param client
	 * @return true if succesful
	 * @require client!=null
	 * @ensure client is now in clients clients.size()++ client.getLobby() ==
	 *         this if clients.size()==slots > will start the game
	 */
	public synchronized boolean addClient(ClientHandler client) {
		boolean out = false;
		if (!isFull()) {
			clients.add(client);
			client.joinLobby(this);
			out = true;

			Server.out.println(client + " has joined the lobby, " + slotsLeft()
					+ " slots left.");
			Server.out.println("Clients:  " + clients.size() + "  Max slots: "
					+ slots);
		}

		if (out == true && isFull()) {
			startLobby();
		}
		return out;
	}

	/**
	 * Removes the given client from this lobby
	 * 
	 * @param ch
	 *            client
	 * @ensure if clients.size()==0 > will remove this lobby
	 */
	public synchronized void removeClientFromLobby(ClientHandler ch) {
		clients.remove(ch);
		ch.leaveLobby();
		
		if (clients.size() == 0 || this.status==ClientHandler.INGAME) {
			endLobby();
		}
	}

	/**
	 * Starts the lobby, meaning the lobby has al its slots filled up, and the
	 * game is starting
	 * 
	 * @ensure this.game!=null status = ClientHandler.INGAME
	 * @require clients.size()==slots status = ClientHandler.INLOBBY
	 */
	private void startLobby() {
		// TODO startsteen positie bepalen;

		Collections.shuffle(clients);

		game = new Game(2, 2, util.Util.makePlayerNameList(clients));

		for (ClientHandler i : clients) {
			i.lobbySTART(util.Protocol.CMD_START + " 2 2 "
					+ util.Util.concatArrayList(clients));
		}

		status = ClientHandler.INGAME;
		Server.out.println("Starting Lobby Game");
		giveTurn();
	}

	/**
	 * Immediately ends this lobby by removing all clients from it
	 */
	private synchronized void endLobby() {
		System.out.println("Ending lobby");
		
		broadcastMessage(util.Protocol.CMD_END+" "+util.Util.concatArrayList(game.getStats()));
		for (ClientHandler ch : clients) {
			ch.leaveLobby();
		}
		server.removeLobby(this); //TODO dit testen
		game = null;
		
		
		System.out.println("Removed lobby");
		//TODO testen of lobby enden goed gaat
	}

	/**
	 * Sends a message to everyone in this lobby
	 * 
	 * @param message
	 */
	protected synchronized void broadcastMessage(String message) {
		for (ClientHandler i : clients) {
			i.sendCommand(message);
		}
	}

	/**
	 * @return whether the lobby is full or not
	 * @ensure result = slots==clients.size()
	 */
	public synchronized boolean isFull() {
		return slotsLeft() == 0;
	}

	/**
	 * @return Amount of slots left in this lobby
	 * @ensure 0<=result<=slots
	 */
	public synchronized int slotsLeft() {
		return slots - clients.size();
	}

	/**
	 * @return The name of the player whos turn it currently is
	 */
	public String getTurn() {
		return turn;
	}

	/**
	 * @return The status the lobby is in
	 */
	public int getStatus() {
		return status;
	}
}

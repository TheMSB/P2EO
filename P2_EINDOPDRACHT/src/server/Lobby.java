package server;

import java.util.ArrayList;
import game.*;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

import exceptions.InvalidMoveException;

public class Lobby extends Thread implements Observer{

	private ArrayList<ClientHandler> clients;
	private int slots;
	private Server server;
	private int status;
	private Game game;
	private String turn;
	
	/**
	 * @param slots
	 * @param player
	 * @require 2<=slots<=4
	 * 			player != null
	 */
	public Lobby(int slots, ClientHandler client,Server server)
	{
		this.server = server;
		this.slots = slots;
		Server.out.println("Made new Lobby with "+slots +" slots");
		clients = new ArrayList<ClientHandler>();
		addClient(client);
		status = ClientHandler.INLOBBY;
	}
	
	/**
	 * Geeft een zet door aan de game bijgehouden door de server, en geeft vervolgens het MOVED command door.
	 * @param args
	 * @throws InvalidMoveException
	 * @require		Deze move is gedaan door degene die aan de beurt is
	 */
	public void move(ArrayList<Integer> args) throws InvalidMoveException
	{
		try{
			//TODO move exceptie laten gooien als niet kan
			game.move(args.get(0), args.get(1), args.get(2), args.get(3));
			broadcastMessage(util.Protocol.CMD_MOVED +" " + util.Util.concatArrayList(args));
		}catch(InvalidMoveException e){
			throw new exceptions.InvalidMoveException();
		}
	}
	
	public void run()
	{
		//TODO iets doen
		while(true){}
	}
	
	private void startLobby()
	{
		//TODO startsteen positie bepalen;
		
		Collections.shuffle(clients);
		
		game = new Game(2,2,util.Util.makePlayerNameList(clients));
		
		for(ClientHandler i : clients){
			i.lobbySTART(util.Protocol.CMD_START+" 2 2 "+util.Util.concatArrayList(clients));
		}
		
		status = ClientHandler.INGAME;
		Server.out.println("Starting Lobby Game");
		giveTurn();
	}
	
	private void giveTurn(){
		turn = clients.get(game.getTurn()).getClientName();
		broadcastMessage(util.Protocol.CMD_TURN + " "+turn);
	}
	
	public String getTurn(){
		return turn;
	}
	
	
	protected synchronized void broadcastMessage(String command){
		for(ClientHandler i : clients){
			i.sendCommand(command);
		}
	}
	
	public synchronized boolean addClient(ClientHandler client)
	{
		boolean out = false;
		if(!isFull()){
			clients.add(client);
			client.joinLobby(this);
			out = true;
			
			Server.out.println(client+ " has joined the lobby, "+slotsLeft() + " slots left.");
			Server.out.println("Clients:  "+clients.size()+"  Max slots: "+slots);
		}
		
		if(out==true && isFull()){
			startLobby();
		}
		return out;
	}
	
	public synchronized void removeClientFromLobby(ClientHandler ch)
	{
		clients.remove(ch);
		if(clients.size()==0){
			server.removeLobby(slots,this);
			game = null;
			//TODO gameover stats sturen?
			System.out.println("Removed lobby");
		}
	}
	
	private synchronized void endLobby(){
		for(ClientHandler ch : clients){
			removeClientFromLobby(ch);
		}
		//TODO hoe zit het met onverwacht disconnects tijdens de game?
	}
	
	public synchronized boolean isFull()
	{
		return slotsLeft()==0;
	}
	
	public synchronized int slotsLeft()
	{
		return slots - clients.size();
	}

	@Override
	public void update(Observable o, Object arg) {
		//TODO daadwerkelijke gameOver boolean uit game halen
		boolean gameOver = false;
		giveTurn();
		
		if(gameOver){
			this.broadcastMessage(util.Protocol.CMD_END+" 1 2"); //TODO score implementeren
			endLobby();
		}
	}
}

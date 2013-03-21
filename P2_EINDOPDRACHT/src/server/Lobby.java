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
	
	public void move() throws InvalidMoveException
	{
		throw new exceptions.InvalidMoveException();
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
		makePlayerNameList(clients);
		
		game = new Game(makePlayerNameList(clients));
		
		for(ClientHandler i : clients){
			i.lobbySTART(util.Protocol.CMD_START+" 2 2 "+Server.concatArrayList(clients));
		}
		
		status = ClientHandler.INGAME;
		Server.out.println("Starting Lobby Game");
	}
	
	//TODO dit naar andere class doen
	public static ArrayList<String> makePlayerNameList(ArrayList<ClientHandler> arr){
		ArrayList<String> names = new ArrayList();
		for(ClientHandler ch : arr){
			names.add(ch.getClientName());
		}
		return names; 
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
		}
		//TODO game stoppen;
	}
	
	private synchronized void endLobby(){
		for(ClientHandler ch : clients){
			removeClientFromLobby(ch);
		}
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
		//TODO syncen met game
		boolean gameOver = true;
		if(gameOver){
			this.broadcastMessage(util.Protocol.CMD_END+" 1 2"); //TODO score implementeren
			endLobby();
		}
	}
}

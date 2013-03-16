package server;

import java.util.ArrayList;

import exceptions.InvalidMoveException;

public class Lobby extends Thread {

	private ArrayList<ClientHandler> clients;
	private int slots;
	private Server server;
	private int status;
	
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
		while(status==ClientHandler.INLOBBY)
		{
			if(isFull()){
				//TODO dit niet continue checken?
				startLobby();
				//TODO: wordt gespammed
				//TODO: naam checken
			}
		}
		
		while(true){}
	}
	
	private void startLobby()
	{
		//TODO startsteen positie bepalen;
		//TODO game maken
		for(ClientHandler i : clients){
			i.lobbySTART(util.Protocol.CMD_START+" 2 2 "+Server.concatArrayList(clients));
		}
		status = ClientHandler.INGAME;
		Server.out.println("Starting Lobby Game");
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
		
		return out;
	}
	
	public synchronized void endGame(ClientHandler ch)
	{
		clients.remove(ch);
		if(clients.size()==0){
			server.removeLobby(slots,this);
		}
		//TODO game stoppen;
	}
	
	public synchronized boolean isFull()
	{
		return slotsLeft()==0;
	}
	
	public synchronized int slotsLeft()
	{
		return slots - clients.size();
	}
}

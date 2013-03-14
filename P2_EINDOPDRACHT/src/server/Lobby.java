package server;

import java.util.ArrayList;

import exceptions.InvalidMoveException;

public class Lobby extends Thread {

	private ArrayList<ClientHandler> clients;
	private int slots;
	private Server server;
	
	/**
	 * @param slots
	 * @param player
	 * @require 2<=slots<=4
	 * 			player != null
	 */
	public Lobby(int slots, ClientHandler client,Server server)
	{
		Server.out.println("Made new Lobby with "+slots +" slots");
		clients = new ArrayList<ClientHandler>();
		addClient(client);
		this.server = server;
		this.slots = slots;
	}
	
	public void move() throws InvalidMoveException
	{
		throw new exceptions.InvalidMoveException();
	}
	
	public void run()
	{
		while(clients.size()>0)
		{
			if(clients.size()==slots){
				//TODO dit niet continue checken?
				startLobby();
			}
		}
		while(true)
		{}
	}
	
	private void startLobby()
	{
		//TODO startsteen positie bepalen;
		//TODO game maken
		for(ClientHandler i : clients){
			i.lobbySTART(util.Protocol.CMD_START+" 2 2 "+Server.concatArrayList(clients));
		}
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
		}
		Server.out.println(client+ " has joined the lobby.");
		return out;
	}
	
	public synchronized boolean isFull()
	{
		return clients.size()==slots;
	}
}

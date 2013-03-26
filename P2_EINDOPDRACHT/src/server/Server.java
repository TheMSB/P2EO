package server;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread{

	/**
	 * De te gebruiken encoding voor de server;
	 */
	public static final String ENCODING = "UTF-8";
	
	/**
	 * De ServerSocket waarna clients kunnen connecten
	 */
	private ServerSocket ssock;
	/**
	 * Geeft aan of de server het moet blijven doen
	 */
	private boolean running = true;
	/**
	 * ArrayList van ClientHandlers die het protocol ook hebben
	 */
	private ArrayList<ClientHandler> clientHandlers;
	/**
	 * ArrayList van ClientHandlers die het protocol NOG NIET hebben
	 */
	private ArrayList<ClientHandler> newlyConnected;
	/**
	 * Waar het debug spul te printen
	 */
	public static final PrintStream out = System.out;
	
	private int port;
	private String name;
	
	/** 
	 * Houd lobbies bij
	 */
	private static ArrayList<Lobby> lobbies2;
	private static ArrayList<Lobby> lobbies3;
	private static ArrayList<Lobby> lobbies4;
	public static ArrayList<ArrayList<Lobby>> lobbies;
	
	
	
	
	
	/**
	 * Start server op
	 * @param port	Poort om server op te starten
	 * @param name	Naam van server
	 * @throws IOException	Wordt gethrowed als het maken van de ServerSocket mislukt
	 */
	public Server(int port, String name) throws IOException{
		this.port = port;
		this.name = name;
		
		ssock = new ServerSocket(port);
		clientHandlers = new ArrayList<ClientHandler>();
		newlyConnected = new ArrayList<ClientHandler>();
		

		lobbies2 = new ArrayList<Lobby>();
		lobbies3 = new ArrayList<Lobby>();
		lobbies4 = new ArrayList<Lobby>();
		lobbies = new ArrayList<ArrayList<Lobby>>();
		lobbies.add(lobbies2);
		lobbies.add(lobbies3);
		lobbies.add(lobbies4);
	}
	/**
	 * De start methode van dit thread, blijft kijken of er nieuwe dingen connecten,
	 * voegt het dan toe aan newlyConnected
	 */
	public void run()
	{
		while(running)
		{
			try {
				Socket sock = ssock.accept();
				ClientHandler ch = new ClientHandler(this, sock);
				ch.start();
				out.println("Iets heeft verbinding gemaakt");
				newlyConnected.add(ch);
				//TODO kunt nu nog eeuwing in newlyCOnnected blijven
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				out.println(Server.ENCODING+" encoding required.");
			} catch (IOException e) {
				// TODO: check protocol wat te doen als iets faalt te connecten
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	/**
	 * Sluit de server af
	 */
	public void shutDown()
	{
		running = false;
	}
	
	/**
	 * Upgraded de verbonden clienthandler van 'iets' naar iemand met hetzelfde protocol
	 * @param ch	De te upgraden ClientHandler
	 * @ensure	Protocol van ch is gelijk aan dat van de server.
	 */
	protected synchronized void approve(ClientHandler ch)
	{
		if(ch!=null)
		{
			if(newlyConnected.remove(ch)) //TODO eerst adden?
			{
				clientHandlers.add(ch);
				out.println("ClientHandler approved:  "+ch);
				//out.println(this.getLobbies());
			}
		}
	}
	
	/**
	 * Haalt een client weg uit de lijst met verbonden clients
	 * @param ch	De clienthandler van de client
	 */
	protected synchronized void removeClient(ClientHandler ch){
		clientHandlers.remove(ch);
		newlyConnected.remove(ch);
	}
	
	/**
	 * Haalt een lobby weg uit de lijst met lobbies
	 * @param slots		Hoeveel slots de weg te halen lobby heeft
	 * @param lobby		Welke lobby weg te halen
	 */
	protected synchronized void removeLobby(int slots, Lobby lobby){
		lobbies.get(slots-2).remove(lobby);
	}
	
	/**
	 * Laat een ClientHandler een lobby joinen, met het meegegeven aantal slots.
	 * Als er geen openstaande lobby is van het aantal slots wordt een nieuwe lobby aangemaakt
	 * @param slots		Hoeveel spelers de lobby moet toestaan
	 * @param ch		de toe te voegen ClientHandler
	 * @return			De lobby waar de ClientHandler aan toegevoegd is
	 */
	public synchronized Lobby getLobby(int slots, ClientHandler ch){
		ArrayList<Lobby> queue = lobbies.get(slots-2);
		Lobby lobby = null;
		if (queue!=null && !queue.isEmpty()) {
			lobby = queue.get(queue.size() - 1);
			if (!lobby.addClient(ch)) {
				Server.out.println("No empty Lobby, making new one");
				lobby = new Lobby(slots, ch, this);
				lobby.start();
				queue.add(lobby);
			}
		}else{
			Server.out.println("No current lobbies, making new one");
			lobby = new Lobby(slots, ch, this);
			lobby.start();
			queue.add(lobby);
		}
		
		return lobby;
	}
	
	protected synchronized void broadcastMessage(String command){
		for(ClientHandler i : clientHandlers){
			i.sendCommand(command);
		}
	}
	
	//TODO overleggen of martijn eens is met static maken server, rest ook static maken
	/**
	 * @return het aantal spelers waarvoor de lobby het meest gevorderd is
	 */
	public static synchronized int getBestLobby(){
		int output = 2;
		for(int i=0;i<3;i++){
			if(lobbies.get(i).size()>0 && lobbies.get(i).get(lobbies.get(i).size()-1).slotsLeft()<=(i+1) && !lobbies.get(i).get(lobbies.get(i).size()-1).isFull())
			{
				output = i+2;
				break;
			}
		}
		
		return output;
	}
	
	
	/**
	 * @return Een lijst met ondersteunde features door de server
	 */
	public ArrayList<String> getFeatures()
	{
		return new ArrayList<String>();
	}
	
	public boolean nameInUse(String name){
		int count = 0;
		for(ClientHandler ch : newlyConnected){
			if(ch.getClientName().equals(name)){
				count++;
			}
		}
		for(ClientHandler ch : clientHandlers){
			if(ch.getClientName().equals(name)){
				count = 2;
			}
		}
		
		//System.out.println(count);
		return count!=1;
	}
	
	
	//TODO dit naar andere class
	
	
	
	public boolean isRunning(){
		return running;
	}
	public int getPort(){
		return port;
	}
	public String getServerName(){
		return name;
	}
	
	public ArrayList<ClientHandler> getClients()
	{
		return clientHandlers;
	}
	
	public ArrayList<ArrayList<Lobby>> getLobbies()
	{
		return lobbies;
	}
}

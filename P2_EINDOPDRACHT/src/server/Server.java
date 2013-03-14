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
	
	/**
	 * Start server op
	 * @param port	Poort om server op te starten
	 * @param name	Naam van server
	 * @throws IOException	Wordt gethrowed als het maken van de ServerSocket mislukt
	 */
	public Server(int port, String name) throws IOException{
		ssock = new ServerSocket(port);
		clientHandlers = new ArrayList<ClientHandler>();
		newlyConnected = new ArrayList<ClientHandler>();
		
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
			if(newlyConnected.remove(ch))
			{
				clientHandlers.add(ch);
				out.println("ClientHandler approved:  "+ch);
			}
		}
	}
	
	protected synchronized void broadcastMessage(String command){
		for(ClientHandler i : clientHandlers){
			i.sendCommand(command);
		}
	}
	
	
	/**
	 * @return Een lijst met ondersteunde features door de server
	 */
	public ArrayList<String> getFeatures()
	{
		return new ArrayList<String>();
	}
	
	public static <Elem> String concatArrayList(ArrayList<Elem> arr)
	{
		String output = "";
		for (Elem s : arr) {
			output = output + s + " "; // TODO
																	// navragen
																	// of
																	// arraylist
																	// hier
																	// functie
																	// voor
																	// heeft
		}
		return output;
	}
}

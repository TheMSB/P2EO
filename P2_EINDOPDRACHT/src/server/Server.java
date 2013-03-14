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
	private boolean running = true;
	private ArrayList<ClientHandler> clientHandlers;
	private ArrayList<ClientHandler> newlyConnected;
	private PrintStream out = System.out;
	
	
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
}

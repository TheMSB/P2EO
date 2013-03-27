package server;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Server is after start up constantly checking for new connections to his
 * appointed socket, once a connection has been established, it will be given a
 * ClientHandler to handle any further communication. Server also keeps track of
 * the lobbies in which clients will be put to play games.
 * 
 * @author I3anaan
 * 
 */
public class Server extends Thread {

	/**
	 * The encoding used by the protocol
	 */
	public static final String ENCODING = util.Protocol.ENCODING;

	/**
	 * The ServerSocket used for the server
	 */
	private ServerSocket ssock;
	/**
	 * Whether the server is on or off
	 */
	private boolean running = true;
	/**
	 * ArrayList of ClientHandlers, who are confirmed to have the protocol
	 */
	private ArrayList<ClientHandler> clientHandlers;
	/**
	 * ArrayList of ClientHandlers who havent handshaken yet
	 */
	private ArrayList<ClientHandler> newlyConnected;
	/**
	 * Output of debug stuff
	 */
	public static final PrintStream out = System.out;

	/**
	 * Which port the Server should be on
	 */
	private int port;
	/**
	 * Name of the server
	 */
	private String name;

	/**
	 * ArrayList with lobbies
	 */
	private ArrayList<Lobby> lobbies2;
	private ArrayList<Lobby> lobbies3;
	private ArrayList<Lobby> lobbies4;
	public ArrayList<ArrayList<Lobby>> lobbies;

	/**
	 * Starts up the server
	 * 
	 * @param port
	 *            Port to start the server on
	 * @param name
	 *            Name of the server
	 * @throws IOException
	 *             Throwed if making the socket fails
	 */
	public Server(int port, String name) throws IOException {
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
	 * Aslong as running==true it will continue to check if clients want to
	 * connect If it gets a connection, it will make a new ClientHandler for it
	 * and adds it to newlyConnected
	 */
	public void run() {
		while (running) {
			try {
				Socket sock = ssock.accept();
				ClientHandler ch = new ClientHandler(this, sock);
				ch.start();
				out.println("Iets heeft verbinding gemaakt");
				newlyConnected.add(ch);

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				out.println(Server.ENCODING + " encoding required.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Shutsdown the server
	 */
	public void shutDown() {
		running = false;
	}

	/**
	 * Starts up the server again
	 */
	public void startUp() {
		running = true;
	}

	/**
	 * Moves a ClientHandler from newlyConnected to clientHandlers, meaning the
	 * ClientHandler has completed the handshake
	 * 
	 * @param ch
	 *            the ClientHandler to upgrade
	 */
	protected synchronized void approve(ClientHandler ch) {
		if (ch != null) {
			if (newlyConnected.remove(ch)) {
				clientHandlers.add(ch);
				out.println("ClientHandler approved:  " + ch);
			}
		}
	}

	/**
	 * Removes a ClientHandler from the server
	 * 
	 * @param ch
	 *            ClientHandler to remove
	 * @ensure ch is not in clientHandlers or newlyConnected
	 */
	protected synchronized void removeClient(ClientHandler ch) {
		clientHandlers.remove(ch);
		newlyConnected.remove(ch);
	}

	/**
	 * Removes a Lobby from the server
	 * 
	 * @param lobby
	 *            which Lobby to remove
	 * @ensure the lobby is not in any of the lobbies lists
	 */
	protected synchronized void removeLobby(Lobby lobby) {
		lobbies.get(0).remove(lobby);
		lobbies.get(1).remove(lobby);
		lobbies.get(2).remove(lobby);
	}

	/**
	 * Lets a ClientHandler join a lobby, if there is no lobby with the wanted
	 * slots yet, it will make one
	 * 
	 * @param slots
	 *            max amount of players in the lobby
	 * @param ch
	 *            the joining ClientHandler
	 * @return The Lobby in which the ClientHandler is placed
	 * @ensure ch is placed in a lobby
	 * @require ch!=null
	 */
	public synchronized Lobby getLobby(int slots, ClientHandler ch) {
		ArrayList<Lobby> queue = lobbies.get(slots - 2);
		Lobby lobby = null;
		if (queue != null && !queue.isEmpty()) {
			lobby = queue.get(queue.size() - 1);
			if (!lobby.addClient(ch)) {
				Server.out.println("No empty Lobby, making new one");
				lobby = new Lobby(slots, ch, this);
				queue.add(lobby);
			}
		} else {
			Server.out.println("No current lobbies, making new one");
			lobby = new Lobby(slots, ch, this);
			queue.add(lobby);
		}

		return lobby;
	}

	/**
	 * Sends a message to every client (from clientHandlers) on the server
	 * 
	 * @param message
	 */
	protected synchronized void broadcastMessage(String message) {
		for (ClientHandler i : clientHandlers) {
			i.sendCommand(message);
		}
	}

	/**
	 * @return Gives the amount of slots for the best lobby type to join
	 * @ensure 2<=result<=4
	 */
	public synchronized int getBestLobby() {
		int output = 2;
		for (int i = 0; i < 3; i++) {
			if (lobbies.get(i).size() > 0
					&& lobbies.get(i).get(lobbies.get(i).size() - 1)
							.slotsLeft() <= (i + 1)
					&& !lobbies.get(i).get(lobbies.get(i).size() - 1).isFull()) {
				output = i + 2;
				break;
			}
		}

		return output;
	}

	/**
	 * @return ArrayList with Features from the server
	 */
	public ArrayList<String> getFeatures() {
		return new ArrayList<String>();
	}

	/**
	 * Checks whether or not the given name is already in use
	 * 
	 * @param name
	 * @return true if the name is already in use on this server
	 */
	public boolean nameInUse(String name) {
		int count = 0;
		for (ClientHandler ch : newlyConnected) {
			if (ch.getClientName().equals(name)) {
				count++;
			}
		}
		for (ClientHandler ch : clientHandlers) {
			if (ch.getClientName().equals(name)) {
				count = 2;
			}
		}
		return count != 1;
	}

	/**
	 * @return if the server is running or not
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @return the port this server is hosted on
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the name of the server
	 */
	public String getServerName() {
		return name;
	}

	/**
	 * @return ArrayList of ClientHandlers who have completed the handshake
	 */
	public ArrayList<ClientHandler> getClients() {
		return clientHandlers;
	}

	/**
	 * @return ArrayList of ArrayList of lobbies
	 */
	public ArrayList<ArrayList<Lobby>> getLobbies() {
		return lobbies;
	}
}

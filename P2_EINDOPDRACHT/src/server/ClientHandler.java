package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * ClientHandler extends Thread and is running for each separate connection to
 * the server. It will perform the handshake as described in the protocol with
 * the client, and after that be waiting for other commands from the client such
 * as joining games, or moves. Upon receiving such commands, ClientHandler first
 * checks if it recognizes the commands and if they are valid, if not it will
 * send the correct error message. If the command gets validated it will then
 * proceed to call the neccessary methods for that command. Those methods
 * usually are within ClientHandler itself for connection/disconnection, in
 * Server to join a lobby, or in Lobby if they have to do with a (running) Game.
 * 
 * @author I3anaan
 */
public class ClientHandler extends Thread {

	/**
	 * This clients name
	 */
	private String name;
	/**
	 * The socket which holds the connection to the client
	 */
	private Socket sock;
	/**
	 * The server to which this ClientHandler belongs
	 */
	private Server server;
	/**
	 * The inputstream received from the client
	 */
	private BufferedReader in;
	/**
	 * The output which is used to send data to the client
	 */
	private BufferedWriter out;
	/**
	 * The last received message
	 */
	private String lastInput;

	/**
	 * Features for client and server, client can only has features which the
	 * server also has
	 */
	private ArrayList<String> clientFeatures;
	private ArrayList<String> serverFeatures;

	/**
	 * The lobby this client has joined, no lobby joined if lobby==null
	 */
	private Lobby lobby;
	/**
	 * Whether or not this ClientHandler is in a state of shutting down;
	 */
	private boolean shuttingDown;

	/*
	 * Status indicator are used to determine how far the handshake has come,
	 * and what commands are expected (and thus which commands to ignore) These
	 * are also used by Lobby
	 */
	/**
	 * The initial state, in which the client has yet to send the CONNECT cmd,
	 * telling the server it is aware of the protocol
	 */
	public static final int EXPECTING_CONNECT = 0;
	/**
	 * Deprecated
	 */
	public static final int EXPECTING_FEATURED = 1;
	/**
	 * Status in which the client is in after confirming it has the protocol,
	 * this is the main status, from which it is able to join a game.
	 */
	public static final int HANDSHAKE_SUCCESFULL = 2;
	/**
	 * The client has joined a lobby and is awaiting it to start
	 * 
	 * @invariant lobby!=null
	 */
	public static final int INLOBBY = 3;
	/**
	 * The lobby the client is in has started its game, thus the client is now
	 * playing
	 * 
	 * @invariant lobby!=null
	 */
	public static final int INGAME = 4;

	/**
	 * The current status of this ClientHandler, starting at EXPECTING_CONNECT
	 * as that is the first command as defined by our protocol
	 */
	private int status = EXPECTING_CONNECT;

	/**
	 * Makes the ClientHandler for the given socket
	 * 
	 * @param server
	 *            The Server to which this ClientHandler belongs
	 * @param sock
	 *            The Socket which contains the connection to the client
	 * @throws UnsupportedEncodingException
	 *             If the encoding is not supported
	 * @throws IOException
	 *             Incase something goes wrong with the connection
	 */
	public ClientHandler(Server server, Socket sock)
			throws UnsupportedEncodingException, IOException {
		this.sock = sock;
		this.server = server;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream(),
				Server.ENCODING));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(),
				Server.ENCODING));

		serverFeatures = server.getFeatures();
	}

	/**
	 * Keeps checking if a client sends a command, if so it will send the
	 * command to a method to read it.
	 */
	public void run() {
		try {
			while (true) {
				lastInput = in.readLine();
				System.out.println("Command gelezen: " + lastInput);
				if (lastInput != null) {
					readCommand(new Scanner(lastInput));
				} else {
					sendError(util.Protocol.ERR_INVALID_COMMAND);
					unexpectedDisconnect("Expecting_input");
					break;
				}
			}
		} catch (SocketException e) {
			// if it gets to this part of the code, we can assume there has been
			// a disconnect

			unexpectedDisconnect("SocketException");
		} catch (IOException e) {
			sendError(util.Protocol.ERR_UNDEFINED);
			e.printStackTrace();
		}
		System.out.println("ClientHandler for: " + this.name + " stopped");
	}

	/**
	 * Stops this thread
	 * 
	 * @ensure thread will be closed
	 */
	public synchronized void stopThread() {
		shuttingDown = true;
		System.out.println("Stopping thread...  " + name);
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads a command from the scanner
	 * 
	 * @param scanner
	 *            The scanner which contains the line to read
	 * @require scanner!=null
	 */
	private void readCommand(Scanner scanner) {
		if (scanner.hasNext()) {
			String command = scanner.next();
			ArrayList<String> args = new ArrayList<String>();
			while (scanner.hasNext()) {
				args.add(scanner.next());
			}
			checkCommand(command, args);
		} else {
			sendError(util.Protocol.ERR_INVALID_COMMAND);
			if (status == EXPECTING_CONNECT) {
				unexpectedDisconnect("Wrong_protocol");
			}
		}
	}

	/**
	 * Checks if the received command is recognized
	 * 
	 * @param command
	 *            The command received from the client
	 * @param args
	 *            Possible arguments for the command
	 * 
	 * @require command!=null
	 * @require args!=null (args.size() can be 0)
	 */
	public void checkCommand(String command, ArrayList<String> args) {
		if (command.equals(util.Protocol.CMD_CONNECT)) {
			cmdCONNECT(args);
		} else if (command.equals(util.Protocol.CMD_FEATURED)) {
			cmdFEATURED(args);
		} else if (command.equals(util.Protocol.CMD_JOIN)) {
			cmdJOIN(args);
		} else if (command.equals(util.Protocol.CMD_MOVE)) {
			cmdMOVE(args);
		} else if (command.equals(util.Protocol.CMD_DISCONNECT)) {
			cmdDISCONNECT(args);
		} else if (command.equals(util.Protocol.CMD_SAY)) {
			cmdSAY(args);
		} else if (command.equals(util.Protocol.CMD_ERROR)) {
			// Server should not be receiving errors from the client, but it
			// wont
			// react to them with a command not found error, as that could
			// create an endless loop of errors
		} else {
			sendError(util.Protocol.ERR_COMMAND_NOT_FOUND);
		}
	}

	/**
	 * First command to be received from the client (according to protocol) This
	 * also means the client had successfully finished the handshake.
	 * 
	 * @param args
	 *            args.get(0) = the name of the client
	 */
	private void cmdCONNECT(ArrayList<String> args) {
		/*
		 * As for the general cmdCMD methods: It will first check if the status
		 * in which the client is allows such command Then it will check if
		 * args.size() is valid Then comes the actual code the method is
		 * supposed to do (can be more if commands, as this is an example of) In
		 * all cases this ClientHandler will send the appropriate error if does
		 * not comply to the expectations
		 */
		if (status == EXPECTING_CONNECT) {
			if (args.size() == 1) {
				this.name = args.get(0);

				if (!server.nameInUse(name)) {
					sendCommand(util.Protocol.CMD_CONNECTED
							+ " "
							+ "Welcome to the aperture science enrichment center.");
					sendCommand(util.Protocol.CMD_FEATURES + " "
							+ util.Util.concatArrayList(serverFeatures));
					status = HANDSHAKE_SUCCESFULL;
					server.approve(this);
				} else {
					sendError(util.Protocol.ERR_NAME_IN_USE);
				}

			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	/**
	 * Command used to set or update the features the connected client supports
	 * 
	 * @param args
	 *            Features the client has, can be infinitely many
	 */
	private void cmdFEATURED(ArrayList<String> args) {
		if (status >= EXPECTING_FEATURED) {
			if (args.size() >= 0) {
				clientFeatures = new ArrayList<String>();
				for (String a : args) {
					for (String b : serverFeatures) {
						if (a.equals(b)) {
							clientFeatures.add(a);
						}
					}
				}
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	/**
	 * Puts the client in a lobby, to play a game
	 * 
	 * @param args
	 *            Size of game the client wants
	 */
	private void cmdJOIN(ArrayList<String> args) {
		if (status == HANDSHAKE_SUCCESFULL) {
			if (args.size() >= 0 && args.size() <= 1) {
				int slots = server.getBestLobby();
				try {
					if (args.size() == 1) {
						slots = Integer.parseInt(args.get(0));
					}
				} catch (NumberFormatException e) {
					// if args.get(0) cannot be casted to an Integer, it will
					// leave the slots variable to 0, meaning this client has no
					// Preferences
				}
				server.getLobby(slots, this);
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	/**
	 * Called when the client wants to do a move
	 * 
	 * @param args
	 *            Data for the move (x,y,type,color)
	 * 
	 * @ensure lobby.move() is only called if its this client's turn
	 */
	private void cmdMOVE(ArrayList<String> args) {
		if (status == INGAME && lobby.getTurnName().equals(getClientName())) {
			if (args.size() == 4) {
				try {
					lobby.move(util.Util.ConvertToInt(args));
				} catch (NumberFormatException e) {
					sendError(util.Protocol.ERR_INVALID_COMMAND);
				}
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	/**
	 * This command indicates that the client wants to do a correct disconnect,
	 * will be broadcasted to every client having the CHAT or CHALLENGE
	 * features, and everyone in the same lobby
	 * 
	 * @param args
	 *            Possible message from the client
	 * @ensure client gets removed from the server and lobby
	 */
	private void cmdDISCONNECT(ArrayList<String> args) {
		if (status >= HANDSHAKE_SUCCESFULL) {
			if (args.size() >= 0) {
				server.broadcastMessage(util.Protocol.CMD_DISCONNECTED + " "
						+ this.name + " " + util.Util.concatArrayList(args));
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendCommand(util.Protocol.CMD_DISCONNECTED + " " + this.name
					+ "Closing connection");
		}
		if (this.status >= INLOBBY) {
			lobby.removeClientFromLobby(this);
		}
		server.removeClient(this);
		
		stopThread();
	}

	/**
	 * This will send the message received from the client to every client
	 * having chat, lobby-only if this client is in the lobby
	 * 
	 * @param args
	 *            Message
	 */
	private void cmdSAY(ArrayList<String> args) {
		if (status >= HANDSHAKE_SUCCESFULL) {
			if (args.size() >= 1) {
				if (this.lobby != null) {
					lobby.broadcastMessage(util.Protocol.CMD_SAID + " "
							+ this.getClientName() + " "
							+ util.Util.concatArrayList(args));
				} else {
					server.broadcastMessage(util.Protocol.CMD_SAID + " "
							+ this.getClientName() + " "
							+ util.Util.concatArrayList(args));
				}
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		}
	}

	/**
	 * Method to call when a unexpected disconnect is detected.
	 * 
	 * @param message
	 *            Possible message to be included
	 * @ensure Lets others know of this disconnect, through the cmdDISCONNECT()
	 *         fuction
	 */
	private void unexpectedDisconnect(String message) {
		if (!shuttingDown) {
			shuttingDown = true;
			System.out.println("UNEXPECTED DISCONNECT: " + name);
			ArrayList<String> arr = new ArrayList<String>();
			arr.add(message);
			cmdDISCONNECT(arr);
		}
	}

	/**
	 * Joins the given lobby
	 * 
	 * @param lobby
	 * @require lobby!=null
	 */
	public void joinLobby(Lobby lobby) {
		this.lobby = lobby;
		status = INLOBBY;
		System.out.println(name + " has joined: " + lobby);
	}

	/**
	 * Let this client leaves the lobby he is currently in
	 * 
	 * @ensure this.getLobby()==null this.getStatus()==HANDSHAKE_SUCCESFULL
	 */
	public void leaveLobby() {
		this.lobby = null;
		this.status = HANDSHAKE_SUCCESFULL;
	}

	/**
	 * Invoked by the lobby to let this clienthandler know the game will start
	 * 
	 * @param command
	 *            The start command
	 * @ensure this.getStatus()==INGAME
	 */
	public void lobbySTART(String command) {
		sendCommand(command);
		status = INGAME;
		System.out.println("Game has started");
	}

	/**
	 * Sends an error to the client
	 * 
	 * @param errorCode
	 *            as defined by the protocol
	 */
	public void sendError(int errorCode) {
		System.out.println("STATUS: " + status);
		System.out.println("Last input: " + lastInput);
		sendCommand(util.Protocol.CMD_ERROR + " " + errorCode);
	}

	/**
	 * Sends a command to the client
	 * 
	 * @param command
	 *            The command to be send
	 * @ensure Only sends commands to the client if the feature to which the
	 *         command belongs is supported by the client
	 */
	public void sendCommand(String command) {
		System.out.println("command " + command + "| cientFeatures "
				+ clientFeatures + "| lobby " + lobby);
		try {
			if ((!command.startsWith(util.Protocol.CMD_SAID) || (clientFeatures != null && clientFeatures
					.contains(util.Protocol.FEAT_CHAT)))
					&& (!command.startsWith(util.Protocol.CMD_DISCONNECTED) || (this.lobby != null || (clientFeatures != null && (clientFeatures
							.contains(util.Protocol.FEAT_CHAT) || clientFeatures
							.contains(util.Protocol.FEAT_CHALLENGE)))))) {
				// TODO betere oplossing voor dit
				out.write(command + "\n");
				out.flush();
			}
		} catch (IOException e) {
			System.out.println("Failed to send message to:  " + this.name);
		}
	}

	/**
	 * The standard to string fuction
	 */
	public String toString() {
		return "[ClientHandler: " + this.name + " Status: " + status + "]";
	}

	/**
	 * @return The name this client has given himself
	 */
	public String getClientName() {
		return this.name;
	}

	/**
	 * @return The status this client is in
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @return The Lobby this client is in (null = not in lobby)
	 */
	public Lobby getLobby() {
		return lobby;
	}

}
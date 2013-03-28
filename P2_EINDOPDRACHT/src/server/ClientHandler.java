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
 * usually are withing ClientHandler itself for connection/disconnection, in
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
	 * Possible statusses
	 */
	public static final int EXPECTING_CONNECT = 0;
	public static final int EXPECTING_FEATURED = 1;
	public static final int HANDSHAKE_SUCCESFULL = 2;
	public static final int INLOBBY = 3;
	public static final int INGAME = 4;

	/**
	 * The current status of this ClientHandler
	 */
	private int status = EXPECTING_CONNECT;

	/**
	 * Makes CLientHandler
	 * 
	 * @param server
	 * @param sock
	 *            The socket which contains the connection to the client
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
	 * command to a method to read it. Will attempt a reconnect incase of 1
	 * disconnect, stops trying to reconnect if this also fails
	 */
	public void run() {
		try {
			while (true) {
				lastInput = in.readLine();
				System.out.println(lastInput);
				if (lastInput != null) {
					readCommand(new Scanner(lastInput));
				} else {
					sendError(util.Protocol.ERR_INVALID_COMMAND);
					unexpectedDisconnect("Expecting_input");
				}
			}
		} catch (SocketException e) {
			// TODO mag ik er vanuitgaan dat dit een disconnect is

			unexpectedDisconnect("SocketException");
		} catch (IOException e) {
			sendError(util.Protocol.ERR_UNDEFINED);
			e.printStackTrace();
			// TODO hier afluisten?
		}

		System.out.println("Shutting down");
	}

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
	 * Checked of het meegegeven command herkent wordt
	 * 
	 * @param command
	 * @param args
	 *            De argumenten die met het command meekwamen
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
		} else if (command.equals(util.Protocol.CMD_ERROR)) {
			// TODO doe iets?
		} else {
			sendError(util.Protocol.ERR_COMMAND_NOT_FOUND);
		}
	}

	/**
	 * Eerste command verstuurd vanaf de client, server reageert
	 * 
	 * @param args
	 *            de argumenten meegegeven met het command
	 */
	public void cmdCONNECT(ArrayList<String> args) {
		if (status == EXPECTING_CONNECT) {
			if (args.size() == 1) {
				this.name = args.get(0);

				if (!server.nameInUse(name)) {
					sendCommand(util.Protocol.CMD_CONNECTED + " "
							+ "Goedendag, welkom op onze server");
					sendCommand(util.Protocol.CMD_FEATURES + " "
							+ util.Util.concatArrayList(serverFeatures));
					status = EXPECTING_FEATURED;
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
	 * 2e command verstuurd vanaf de client verwacht na CONNECT, server reageert
	 * 
	 * @param args
	 *            de argumenten meegegeven met het command
	 */
	public void cmdFEATURED(ArrayList<String> args) {
		if (status == EXPECTING_FEATURED) {
			if (args.size() >= 0) {
				for (String a : args) {
					for (String b : serverFeatures) {
						if (a.equals(b)) // TODO: kan dit ook met contains?
						{
							clientFeatures.add(a);
							// TODO: iets over het accepteren van features in
							// protocol?
						}
					}
				}
				server.approve(this);
				status = HANDSHAKE_SUCCESFULL;
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	public void cmdJOIN(ArrayList<String> args) {
		if (status == HANDSHAKE_SUCCESFULL) {
			if (args.size() >= 0 && args.size() <= 1) {
				int slots = server.getBestLobby();
				try {
					if (args.size() == 1) {
						slots = Integer.parseInt(args.get(0));
					}
				} catch (NumberFormatException e) {
					// slots zo laten;
				}
				server.getLobby(slots, this);
				// TODO controleren of multithread issue gefixed is;
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	public void cmdMOVE(ArrayList<String> args) {
		if (status == INGAME && lobby.getTurn().equals(getClientName())) {
			if (args.size() == 4) {
				try {
					lobby.move(util.Util.ConvertToInt(args));
					// TODO wordt de exception automatisch doorgegeven?
				} catch (exceptions.InvalidMoveException e) {
					sendError(util.Protocol.ERR_INVALID_MOVE);
				} catch (NumberFormatException e) {
					sendError(util.Protocol.ERR_INVALID_COMMAND);
				}
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			System.out.println(lobby.getTurn() + "   " + getClientName());
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	/**
	 * Uit te voeren als de disconnect niet aangegeven is door de client, maar
	 * opgemerkt door een error in de verbinding
	 * 
	 * @param message
	 *            Mogelijk bericht om mee te geven bij het melden van de
	 *            disconnect
	 */
	public void unexpectedDisconnect(String message) {
		System.out.println("UNEXPECTED DISCONNECT");
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(message);
		cmdDISCONNECT(arr);
	}

	public void stopThread() {
		System.out.println("Stopping thread...  " + name);
		try {
			sock.close();
			System.out.println("Should be closing4");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Geeft aan dat de client wil disconnecting, als de client de handshake
	 * heeft gedaan wordt de disconnect gebroadcast als de client nog niet de
	 * handshake heeft gedaan wordt alleen een bericht naar de client zelf
	 * gestuurd
	 * 
	 * @param args
	 *            Een mogelijk bericht
	 */
	public void cmdDISCONNECT(ArrayList<String> args) {
		if (status >= HANDSHAKE_SUCCESFULL) {
			if (args.size() >= 0) {
				lobby.broadcastMessage(util.Protocol.CMD_DISCONNECTED + " "
						+ this.name + " " + util.Util.concatArrayList(args));
				// TODO moet naar iedereen in lobby EN iedereen die
				// chat/challenge feature ondersteund
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendCommand(util.Protocol.CMD_DISCONNECTED + " " + this.name
					+ "Closing connection");
		}

		try {
			sock.close(); // TODO klopt dit?
		} catch (IOException e) {
			e.printStackTrace();
		}
		server.removeClient(this);
		if (this.status >= INLOBBY) {
			lobby.removeClientFromLobby(this);
		}// TODO testen of dit alles is;
	}

	public void leaveLobby() {
		this.lobby = null;
		this.status = HANDSHAKE_SUCCESFULL;
	}

	public void lobbySTART(String command) {
		sendCommand(command);
		status = INGAME;
		System.out.println("Status: INGAME     " + this.getClientName());
	}

	public void joinLobby(Lobby lobby) {
		this.lobby = lobby;
		status = INLOBBY;
		System.out.println("Status: INLOBBY     " + this.getClientName());
	}

	public void sendError(int errorCode) {
		System.out.println("STATUS: " + status);
		System.out.println("Last input: " + lastInput);
		sendCommand(util.Protocol.CMD_ERROR + " " + errorCode);
	}

	public void sendCommand(String command) {
		try {
			out.write(command + "\n");
			out.flush();
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("Failed to send message to:  " + this.name);
			// TODO dit oplossen? mogelijk met retry na seconde ofzo
		}
	}

	public String toString() {
		return this.name;
	}

	public String getClientName() {
		return this.name;
	}

	public int getStatus() {
		return status;
	}

	public Lobby getLobby() {
		return lobby;
	}

}
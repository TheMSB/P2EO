package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler extends Thread {

	private String name;
	private Socket sock;
	private Server server;
	private BufferedReader in;
	private BufferedWriter out;
	private String lastInput;

	/**
	 * Features van clients/servers, clientFeatures kan alleen features bevaten
	 * die ook in serverFeatures zitten
	 */
	private ArrayList<String> clientFeatures;
	private ArrayList<String> serverFeatures;

	/**
	 * De lobby waar een client in zit, indien niet in lobby = null
	 */
	private Lobby lobby;

	

	/**
	 * Status van handshake
	 */
	public static final int EXPECTING_CONNECT = 0;
	public static final int EXPECTING_FEATURED = 1;
	public static final int HANDSHAKE_SUCCESFULL = 2;
	public static final int INLOBBY = 3;
	public static final int INGAME = 4;

	private int status = EXPECTING_CONNECT;

	/**
	 * Maakt CLientHandler
	 * 
	 * @param server
	 *            De Server
	 * @param sock
	 *            De Socket waarmee de Client verbonden is met de Server
	 * @throws UnsupportedEncodingException
	 *             Als de encoding niet gesupport is
	 * @throws IOException
	 *             Als er iets anders mis gaat
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
	 * Blijft klaarstaan om commands te ontvangen
	 */
	public void run() {
		while (true) {
			try {
				lastInput = in.readLine();
				if (lastInput != null) {
					Scanner scanner = new Scanner(lastInput);
					if (scanner.hasNext()) { // TODO: navragen of input != null
												// betekent dat
												// scanner.hasNext() true is
						String command = scanner.next();
						ArrayList<String> args = new ArrayList<String>();
						while (scanner.hasNext()) {
							args.add(scanner.next());
						}
						checkCommand(command, args);
					} else {
						sendError(util.Protocol.ERR_INVALID_COMMAND);
					}
				} else {
					sendError(util.Protocol.ERR_INVALID_COMMAND);
				}
			} catch (IOException e) {
				sendError(util.Protocol.ERR_UNDEFINED);
				e.printStackTrace();
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
				sendCommand(util.Protocol.CMD_CONNECTED + " "
						+ "Goedendag, welkom op onze server");
				sendCommand(util.Protocol.CMD_FEATURES + " "
						+ server.concatArrayList(serverFeatures));
				status = EXPECTING_FEATURED;
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
				}
				joinLobby(server.joinLobby(slots, this));
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	public void cmdMOVE(ArrayList<String> args) {
		if (status == INGAME) {
			if (args.size() == 4) {
				// TODO dit verbeteren:
				try {
					lobby.move();
				} catch (exceptions.InvalidMoveException e) {
					sendError(util.Protocol.ERR_INVALID_MOVE);
				}
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	public void cmdDISCONNECT(ArrayList<String> args) {
		if (status == INGAME) {
			if (args.size() >= 0) {
				server.broadcastMessage(util.Protocol.CMD_DISCONNECTED + " "
						+ this.name + " " + server.concatArrayList(args));
				// TODO protocol vaag over of dit naar iedereen van de server,
				// of iedereen van de huidige game (max 4) moet
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	public void lobbySTART(String command) {
		sendCommand(command);
		status = INGAME;
	}

	public void joinLobby(Lobby lobby) {
		this.lobby = lobby;
		status = INLOBBY;
	}

	public void sendError(int errorCode) {
		System.out.println("STATUS: " + status);
		System.out.println("Last input: " + lastInput);
		sendCommand(util.Protocol.CMD_ERROR + errorCode);
	}

	public void sendCommand(String command) {
		try {
			out.write(command + "\n");
			out.flush();
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("Failed to send message to:  " + this.name);
			// TODO dit oplossen? mogelijk met retry na seconde ofzo
		}
	}

	public String toString() {
		return this.name;
	}

}
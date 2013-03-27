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

public class ClientHandler extends Thread {

	private String name;
	private Socket sock;
	private Server server;
	private BufferedReader in;
	private BufferedWriter out;
	private String lastInput;
	private boolean alive = true; // TODO kijken of dit beter kan

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
		try {
			while (alive) {

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
					//slots zo laten;
				}
				server.getLobby(slots, this);
				//TODO controleren of multithread issue gefixed is;
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
					//TODO wordt de exception automatisch doorgegeven?
				} catch (exceptions.InvalidMoveException e) {
					sendError(util.Protocol.ERR_INVALID_MOVE);
				} catch (NumberFormatException e) {
					sendError(util.Protocol.ERR_INVALID_COMMAND);
				}
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			System.out.println(lobby.getTurn()+"   "+getClientName());
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
	
	public void stopThread(){
		System.out.println("Stopping thread...  "+name);
		alive = false;
		try {
			System.out.println("Should be closing1");
			in.close(); //TODO hoe dit te stoppen?
			System.out.println("Should be closing2");
			out.close();
			System.out.println("Should be closing3");
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
				//TODO moet naar iedereen in lobby EN iedereen die chat/challenge feature ondersteund
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendCommand(util.Protocol.CMD_DISCONNECTED + " " + this.name
					+ "Closing connection");
		}

		try {
			in.close();
			out.close();
			sock.close(); //TODO klopt dit?
		} catch (IOException e) {
			e.printStackTrace();
		}
		alive = false;
		server.removeClient(this);
		if (this.status >= INLOBBY) {
			lobby.removeClientFromLobby(this);
		}// TODO testen of dit alles is;
	}
	
	public void leaveLobby(){
		this.lobby = null;
		this.status = HANDSHAKE_SUCCESFULL;
	}

	public void lobbySTART(String command) {
		sendCommand(command);
		status = INGAME;
		System.out.println("Status: INGAME     "+this.getClientName());
	}

	public void joinLobby(Lobby lobby) {
		this.lobby = lobby;
		status = INLOBBY;
		System.out.println("Status: INLOBBY     "+this.getClientName());
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
	
	public int getStatus(){
		return status;
	}
	
	public Lobby getLobby(){
		return lobby;
	}

}
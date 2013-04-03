package client;

import java.io.BufferedReader;

import exceptions.InvalidMoveException;
import game.*;
import server.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.Scanner;

import ai.*;

import server.Server;
import util.CrapTalker;

public class Client extends Thread {
	// TODO Veel dubbele command leescode, mogelijkheid tot nieuwe klasse

	private String clientName;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private boolean connected;
	private String lastInput;
	private boolean serverAlive;
	private int status;
	private Game game;
	private Player player;
	private AI ai;
	private int selectedAI;
	private boolean humanIsPlaying;
	private MessageUI mui;
	private boolean autoCrapTalk = false;
	private boolean convertToCyrillic = false;
	private boolean myTurn;

	/**
	 * Features van clients/servers, serverFeatures kan alleen features bevaten
	 * die ook in clientFeatures zitten
	 */
	private ArrayList<String> clientFeatures;
	private ArrayList<String> serverFeatures;

	public static final int DISCONNECTED = 0;
	public static final int HANDSHAKE_PENDING_1 = 10;
	public static final int HANDSHAKE_PENDING_2 = 11;
	public static final int HANDSHAKE_SUCCESFULL = 20;
	public static final int INLOBBY = 30;
	public static final int INGAME = 40;

	/**
	 * Maakt nieuwe client aan met een naam, meestal aangeroepen door clientGUI,
	 * connect ook al vast
	 * 
	 * @param name
	 */
	public Client(final String name, final InetAddress adr, final int prt,
			final MessageUI mui) throws IOException {
		InetAddress addr = adr;
		int port = prt;
		System.out.println("[Client]   " + name);
		this.clientName = name;
		this.mui = mui;

		clientFeatures = new ArrayList<String>();
		clientFeatures.add("CHAT");

		// Ga er vanuit dat de server aan staat in het begin
		serverAlive = true;
		status = DISCONNECTED;

		try {
			connectToServer(port, addr);
		} catch (UnknownHostException e) {
			System.out.println("IP not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Failed to make Socket");
			e.printStackTrace();
			serverAlive = false;
		}
	}

	public void run() {
		while (true) { // TODO clientGUI moet client kunnen afsluiten
			try {

				while (connected) {
					lastInput = in.readLine();
					System.out.println(lastInput);
					if (lastInput != null) {
						readCommand(new Scanner(lastInput));
					} else {
						// Verbinding gebroken als hij hier komt.
						// TODO kreeg TURN, toern null, toen error.
						System.out.println("socket closing");
						sock.close();
						serverAlive = false;
						connected = false;
						System.out.println("Socket closed");
						// sendError(util.Protocol.ERR_INVALID_COMMAND);
					}

				}
			} catch (IOException e) {
				sendDisconnect("Reconnecting");
				connected = false;
			}

			// Als code hierkomt is er een disconnect
			if (serverAlive) {
				try {
					connectToServer(4242, InetAddress.getByName("localhost"));
					// TODO poort + ip variabel maken
				} catch (IOException e) {
					System.out
							.println("Server not reaction, stopping reconnect attempt...");
					serverAlive = false;
				}
			}
		}
	}

	/**
	 * Leest een command met argumenten uit de scanner.
	 * 
	 * @param scanner
	 */
	private void readCommand(final Scanner scanner) {
		if (scanner.hasNext()) {
			String command = scanner.next();
			ArrayList<String> args = new ArrayList<String>();
			while (scanner.hasNext()) {
				args.add(scanner.next());
			}
			checkCommand(command, args);
		} else {
			sendError(util.Protocol.ERR_INVALID_COMMAND);
		}
	}

	/**
	 * Checked het command en voert de methode die daarbij hoort uit (indien
	 * aanwezig).
	 * 
	 * @param command
	 * @param args
	 */
	private void checkCommand(final String command, final ArrayList<String> args) {
		if (command.equals(util.Protocol.CMD_CONNECTED)) {
			cmdCONNECTED(args);
		} else if (command.equals(util.Protocol.CMD_FEATURES)) {
			cmdFEATURES(args);
		} else if (command.equals(util.Protocol.CMD_START)) {
			cmdSTART(args);
		} else if (command.equals(util.Protocol.CMD_TURN)) {
			cmdTURN(args);
		} else if (command.equals(util.Protocol.CMD_MOVED)) {
			cmdMOVED(args);
		} else if (command.equals(util.Protocol.CMD_END)) {
			cmdEND(args);
		} else if (command.equals(util.Protocol.CMD_SAID)) {
			cmdSAID(args);
		} else if (command.equals(util.Protocol.CMD_DISCONNECTED)) {
			cmdDISCONNECTED(args);
		} else if (command.equals(util.Protocol.CMD_ERROR)) {
			cmdERROR(args);
		} else {
			sendError(util.Protocol.ERR_COMMAND_NOT_FOUND);
		}
	}

	// TODO kijken naar public/private van zowel client als clienthandler
	/**
	 * Behandelt het CONNECTED command van de server, door de handshake verder
	 * uit te voeren.
	 * 
	 * @param args
	 */
	private void cmdCONNECTED(final ArrayList<String> args) {
		if (status == HANDSHAKE_PENDING_1) {
			if (args.size() >= 0) {
				status = HANDSHAKE_PENDING_2;
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	/**
	 * Behandelt het FEATURES command van de server door de features te checken
	 * en eigen features terug te sturen.
	 * 
	 * @param args
	 */
	private void cmdFEATURES(final ArrayList<String> args) {
		if (status == HANDSHAKE_PENDING_2) {
			if (args.size() >= 0) {
				serverFeatures = new ArrayList<String>();
				for (String a : args) {
					for (String b : clientFeatures) {
						if (a.equals(b)) {
							serverFeatures.add(a);
						}
					}
				}
				sendCommand(util.Protocol.CMD_FEATURED + " "
						+ util.Util.concatArrayList(clientFeatures));
				status = HANDSHAKE_SUCCESFULL;
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	/**
	 * Behandeld het start command van de server, stelt de game zodanig in.
	 * 
	 * @param args
	 */
	private void cmdSTART(ArrayList<String> args) {
		if (status == INLOBBY) {
			if (args.size() >= 4 && args.size() <= 6) {
				try {
					startGame(Integer.parseInt(args.remove(0)),
							Integer.parseInt(args.remove(0)), args);
					// LET OP: args is hier aangepast
					status = INGAME;
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
	 * Start een game
	 * 
	 * @param x
	 *            X coordinaat van startsteen
	 * @param y
	 *            Y coordinaat van startsteen
	 * @param args
	 *            Lijst met namen van spelers
	 */
	private void startGame(int x, int y, ArrayList<String> args) {
		if (autoCrapTalk) {
			sendMessage(util.CrapTalker.insult(util.CrapTalker.INSULTS_START));
		}
		try {
			game = new Game(x, y, args);
			player = game.getPlayer(args.indexOf(clientName));
			((ConnectionWindow) mui).setGame(game, player); //TODO dit kan netter
		} catch (InvalidMoveException e) {
			this.sendDisconnect("Invalid startstone position");
		}
		System.out.println("PlayerNumber:  " + args.indexOf(clientName));
		if (!humanIsPlaying) {
			if (selectedAI == 1) {
				ai = new SmartAI(game, player); // TODO mogelijk ingame aan te laten
				// passen
			}
			else if (selectedAI == 2) {
				ai = new RandomAI(game, player); // TODO mogelijk ingame aan te laten
				// passen
			}
		}
		
	}

	/**
	 * Behandelt het turn command van de server, kijkt of deze client aan de
	 * beurt is.
	 * 
	 * @param args
	 */
	private void cmdTURN(final ArrayList<String> args) {
		if (status == INGAME) {
			if (args.size() == 1) {
				if (args.get(0).equals(this.clientName)) {
					askMove();
				}
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	/**
	 * Deze methode wordt aangeroepen als deze client aan de beurt is, vragend
	 * aan mens of ai om een zet door te geven.
	 */
	private void askMove() {
		myTurn = true;
		System.out.println("Asking move..");

		// TODO laat GUI aangeven dat het jou beurt is
		ArrayList<Integer> arr;
		// arr: 0 = x, 1 = y, 2 = type, 3 = color
		if (humanIsPlaying) {
			arr = ai.getMove(); // TODO dit door mens laten doen
		} else {
			arr = ai.getMove();
		}
		sendCommand(util.Protocol.CMD_MOVE + " "
				+ util.Util.concatArrayList(arr));
	}

	/**
	 * Verwerkt het MOVED command, verkregen van de server.
	 * 
	 * @param args
	 */
	private void cmdMOVED(ArrayList<String> args) {
		if (status == INGAME) {
			if (args.size() == 4) {
				try {
					if (autoCrapTalk) {
						if (myTurn) {
							sendMessage(util.CrapTalker
									.insult(util.CrapTalker.INSULTS_ME_MOVE));
						} else {
							sendMessage(util.CrapTalker
									.insult(util.CrapTalker.INSULTS_OPPONENT_MOVE));
						}
					}
					ArrayList<Integer> arr = util.Util.ConvertToInt(args);
					processMove(arr.get(0), arr.get(1), arr.get(2), arr.get(3));
				} catch (NumberFormatException e) {
					sendError(util.Protocol.ERR_INVALID_COMMAND);
				} catch (InvalidMoveException e) {
					sendError(util.Protocol.ERR_INVALID_MOVE);
					sendDisconnect("Desync detected, disconnecting");
				}
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	private void cmdEND(ArrayList<String> args) {
		if (status == INGAME) {
			if (args.size() >= 2 && args.size() <= 4) {
				displayGameOverScreen(args);
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	private void cmdDISCONNECTED(ArrayList<String> args) {
		if (status == INGAME) {
			if (args.size() == 1) {
				if (autoCrapTalk) {
					sendMessage(util.CrapTalker
							.insult(util.CrapTalker.INSULTS_OPPONENT_DISCONNECT));
				}
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	private void cmdSAID(ArrayList<String> args) {
		if (status >= HANDSHAKE_SUCCESFULL) {
			if (args.size() >= 2) {
				if (serverFeatures.contains(util.Protocol.FEAT_CHAT)) {
					String name = args.remove(0);
					mui.addMessage(name, util.Util.concatArrayList(args));
					//System.out.println("addMessaged");
					//System.out.println(name +" | "+clientName);
					if (!name.equals(clientName)) {
						if (autoCrapTalk && Math.random()<0.5) {
							sendMessage(util.CrapTalker
									.insult(util.CrapTalker.INSULTS_OPPONENT_CHATS));
						}
					}
				}
				// WARNING args gets changed here.
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	private void displayGameOverScreen(ArrayList<String> args) {
		// TODO game over screen displayen
	}

	/**
	 * Behandeld een error.
	 * 
	 * @param args
	 */
	private void cmdERROR(final ArrayList<String> args) {
		int errorCode = 0;
		if (args.size() == 1) {
			try {
				errorCode = Integer.parseInt(args.get(0));
			} catch (NumberFormatException e) {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_INVALID_COMMAND);
		}

		if (status == HANDSHAKE_PENDING_1) {
			if (errorCode == util.Protocol.ERR_NAME_IN_USE) {
				sendDisconnect("Name in use");
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
		if (status == INGAME) {
			if (errorCode == util.Protocol.ERR_INVALID_MOVE) {
				sendDisconnect("Desync detected");
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}

		// TODO meer situaties toevoegen?
	}

	/**
	 * Verwerkt een zet op het spel bord.
	 * 
	 * @throws InvalidMoveException
	 */
	private void processMove(int x, int y, int type, int color)
			throws InvalidMoveException {
		game.move(x, y, type, color);
		myTurn = false;
		if(mui instanceof ActionWindow){
			((ActionWindow) mui).doMove(x,y,type,color);
		}else{
			System.out.println("mui fail");
		}
				
		// System.out.println("Adding ring at: "+ x+" , "+y);
	}
	
	
	void setMUI(MessageUI mui){
		this.mui =mui;
	}

	/**
	 * Stuurt een join command naar de server, vragend om in een lobby geplaatst
	 * te worden.
	 * 
	 * @param slots
	 */
	
	public void joinLobby(final int slots) {
		// System.out.println("Joining lobby);
		if (status == HANDSHAKE_SUCCESFULL) {
			sendCommand(util.Protocol.CMD_JOIN + " " + slots);
			status = INLOBBY;
		}
	}
	/**
	 * Used by the GUI to select an AI.
	 * 1 being Smart
	 * 2 being Random
	 * @param i
	 */
	public void setAI(final int i){
		selectedAI = i;
	}
	
	/**
	 * Used by the GUI to determine if
	 * a player plays himself.
	 * @param b
	 */
	public void setIsPlaying(final boolean b) {
		humanIsPlaying = b;
	}
	/**
	 * Used by the GUI to enable/disable the
	 * flame bot crap talker.
	 * @param f
	 */
	public void setFlame(final boolean f) {
		autoCrapTalk = f;
	}

	/** Stuurt een bericht over de socketverbinding naar de ClientHandler. */
	public void sendMessage(String msg) {
		try {
			String input = msg;
			if (convertToCyrillic) {
				input = util.CrapTalker.toCyrillic(input);
			}
			out.write(util.Protocol.CMD_SAY + " " + input + "\n");
			out.flush();

		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	/**
	 * Stuurt een error naar de server.
	 * 
	 * @param errorCode
	 */
	public void sendError(final int errorCode) {
		System.out.println(">>>>>>>ERROR, Last input: " + lastInput);
		System.out.println("##>STATUS<##  " + status);
		System.out.println(util.Protocol.CMD_ERROR + " " + errorCode);
	}

	/**
	 * Stuurt een command naar de server.
	 * 
	 * @param command
	 */
	public void sendCommand(final String command) {
		System.out.println("Send command: " + command);
		try {
			out.write(command + "\n");
			out.flush();
		} catch (IOException e) {
			System.out.println("Failed to send message to:  " + this.clientName);
		}
	}

	/**
	 * Stuurt de server een DISCONNECT command, met de meegegeven message.
	 * 
	 * @param msg
	 */
	public void sendDisconnect(final String msg) {
		status = DISCONNECTED;
		System.out.println(msg);
		try {
			out.write(util.Protocol.CMD_DISCONNECT + " " + msg);
			sock.close();

		} catch (IOException e) {
			System.out.println("Could not send disconnect message");
		}
	}

	/**
	 * Maakt verbinding met een server, stuurt eerste command voor handshake.
	 * 
	 * @param port
	 * @param ip
	 * @throws IOException
	 *             Als het verbinden fout gaat
	 * @require Of geen verbinding, Of het eerst versturen van een disconnect
	 */
	public void connectToServer(final int port, final InetAddress ip)
			throws IOException {
		if (connected) {
			sendDisconnect("Connecting to a (new) server");
		}

		sock = new Socket(ip, port);
		connected = sock != null;
		if (connected) {

			try {
				in = new BufferedReader(new InputStreamReader(
						sock.getInputStream(), Server.ENCODING));
				out = new BufferedWriter(new OutputStreamWriter(
						sock.getOutputStream(), Server.ENCODING));

				serverAlive = true;
				sendCommand(util.Protocol.CMD_CONNECT + " " + clientName);
				status = HANDSHAKE_PENDING_1;
			} catch (UnsupportedEncodingException e1) {
				System.out
						.println("Not Supported Encoding, this program requires UTF-8");
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
package client;

import java.io.BufferedReader;

import exceptions.InvalidMoveException;
import exceptions.InvalidPieceException;
import game.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

import ai.*;

import server.Server;
import util.SoundPlayer;

/**
 * This class serves as the controller for the client. It receives commands from
 * the player through the clientGUI, and then sends them to either the server or
 * game which will then do the actions wanted. Client automatically connects to
 * the given ip + port according to protocol. Client will always have an AI,
 * even when a human is playing, this is to make getting an hint easier
 * 
 * @author I3anaan
 * 
 */
public class Client extends Thread {

	/**
	 * The name this Client has
	 */
	private String clientName;
	/**
	 * The socket of the server to which this Client is connected
	 */
	private Socket sock;
	/**
	 * The inputstream this Client receives
	 */
	private BufferedReader in;
	/**
	 * The outputstream this Client sends.
	 */
	private BufferedWriter out;
	/**
	 * Whether this Client is connected to a server or not
	 */
	private boolean connected;
	/**
	 * The last input (command) this Client has received from the server
	 */
	private String lastInput;
	/**
	 * The status this Client is in
	 */
	private int status;
	/**
	 * The Game this Client keeps synchronized with the server, game = null
	 * means this Client is not in a game
	 */
	private Game game;
	/**
	 * The Player that belongs to this Client (from the game)
	 */
	private Player player;
	/**
	 * The AI this Client asks for moves, also used for Hints, cannot be null
	 * when starting a game
	 */
	private AI ai;
	/**
	 * Represents which AI to use, 1 = smart, 2 = random 3 = E-Wall
	 */
	private int selectedAI = 1;
	/**
	 * Whether or not there is an human playing
	 */
	private boolean humanIsPlaying = true; // TODO Omdat standaard geen AI geselecteerd is.
	/**
	 * The mui of the ClientGUI to which this CLient sends things like chat
	 * messages
	 */
	private MessageUI mui;
	/**
	 * Whether or not this Client should automaticly craptalk (always in
	 * cyrillic)
	 */
	private boolean autoCrapTalk = false;
	/**
	 * Whether or not to convert this Client's chat messages to cyrillic
	 */
	private boolean convertToCyrillic = false;
	/**
	 * Whether or not it is this Client's turn
	 */
	private boolean myTurn;
	/**
	 * The move the chosen AI would make
	 */
	private ArrayList<Integer> aiMove;

	/**
	 * Features van clients/servers, serverFeatures kan alleen features bevaten
	 * die ook in clientFeatures zitten
	 */
	private ArrayList<String> clientFeatures;
	private ArrayList<String> serverFeatures;

	/*
	 * Statusses this Client can be in.
	 */
	/**
	 * The start status, means that this Client has not connected to a server
	 * yet.
	 */
	public static final int DISCONNECTED = 0;
	/**
	 * The initial handshake has been initiated
	 */
	public static final int HANDSHAKE_PENDING_1 = 10;
	/**
	 * Not used anymore
	 */
	public static final int HANDSHAKE_PENDING_2 = 11;
	/**
	 * The handshake has successfully been completed, from here the Client can
	 * join games
	 */
	public static final int HANDSHAKE_SUCCESFULL = 20;
	/**
	 * Means this Client is in a lobby, awaiting a game
	 */
	public static final int INLOBBY = 30;
	/**
	 * Means this Client is in a game, playing.
	 */
	public static final int INGAME = 40;

	/**
	 * Creates a new Client, automatically tries to connect to a server, with
	 * the given IP and port.
	 * 
	 * @param name
	 *            The name this Client has
	 * @param adr
	 *            The IP address of the server
	 * @param prt
	 *            The Port number of the server
	 * @param mui
	 *            The MessageUI of the ClientGUI
	 * 
	 * @ensure status == DISCONNECTED
	 * @require name, adr, prt, mui !=null
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

		status = DISCONNECTED;

		try {
			connectToServer(port, addr);
		} catch (UnknownHostException e) {
			System.out.println("IP not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Failed to make Socket");
			e.printStackTrace();
		}
	}

	/**
	 * Constantly checks for input from the server, if so sends the command to
	 * readCommand() Will disconnect when an error occurs
	 */
	public void run() {
		while (true) {
			try {
				while (connected) {
					lastInput = in.readLine();
					System.out.println(lastInput);
					if (lastInput != null) {
						readCommand(new Scanner(lastInput));
					} else {
						sock.close();
						connected = false;
						System.out.println("Socket closed");
						sendError(util.Protocol.ERR_INVALID_COMMAND);
					}

				}
			} catch (IOException e) {
				connected = false;
			}

			// TODO hier iets naar GUI sturen zodat je opnieuw kunt connecten
		}
	}

	/**
	 * Sets up a connection with a (new) server
	 * 
	 * @param port	The port to connect to
	 * @param ip	The ip address to connect to
	 * @throws IOException
	 *             If something goes wrong with setting up the socket
	 * @require port, ip !=null
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

				sendCommand(util.Protocol.CMD_CONNECT + " " + clientName);
				status = HANDSHAKE_PENDING_1;
			} catch (UnsupportedEncodingException e1) {
				System.out
				.println("Not Supported Encoding, this program requires UTF-8");
				e1.printStackTrace();
			}
		}else{
			System.out.println("Connecting has failed");
		}
	}

	/**
	 * Reads a command from the scanner
	 * 
	 * @param scanner
	 *            The scanner which contains the line to read
	 * @require scanner!=null
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
	public void checkCommand(final String command, final ArrayList<String> args) {
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

	/**
	 * Reads the CONNECTED command from the server, updating the status
	 * 
	 * @param args
	 *            Possible arguments for the command, being a welcome message
	 *            from the server
	 */
	private void cmdCONNECTED(final ArrayList<String> args) {
		/*
		 * As for the general cmdCMD methods: It will first check if the status
		 * in which the client is allows such command Then it will check if
		 * args.size() is valid Then comes the actual code the method is
		 * supposed to do (can be more if commands, as this is an example of) In
		 * all cases this ClientHandler will send the appropriate error if does
		 * not comply to the expectations
		 * 
		 * @require args!=null
		 */
		if (status == HANDSHAKE_PENDING_1) {
			if (args.size() >= 0) {
				status = HANDSHAKE_PENDING_2;
				if (args.size() >= 2) {
					String msg = "";
					for (String s : args) {
						msg = msg + s + " ";
					}
					mui.addMessage("Server", msg);
				}
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	/**
	 * Checks the features from the server, and saves those it has in common,
	 * also sends a FEATURED comand, letting the server know of the features
	 * this Client has. If the command was received correctly, it will complete
	 * the handshake
	 * 
	 * @param args
	 *            Possible arguments for the command, being the features the
	 *            server has.
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
				((ConnectionWindow) mui).enableMenu();
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	/**
	 * The server sends this command to let the clients know the game starts,
	 * the Client then sets up his own game and updates its status to INGAME
	 * 
	 * @param args
	 *            Possible arguments for the command, being the startstone
	 *            coordinates and the player names
	 */
	private void cmdSTART(ArrayList<String> args) {
		if (status == INLOBBY) {
			if (args.size() >= 4 && args.size() <= 6) {
				try {
					startGame(Integer.parseInt(args.remove(0)),
							Integer.parseInt(args.remove(0)), args);
					// WARNING: args is changed here
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
	 * Starts a game
	 * 
	 * @param x
	 *            X coordinate of the startstone
	 * @param y
	 *            Y coordinate of the starstone
	 * @param args
	 *            ArrayList of player names, the args.size() == gametype
	 * 
	 * @ensure game!=null
	 * @ensure ai!=null
	 */
	private void startGame(int x, int y, ArrayList<String> args) {
		if (autoCrapTalk) {
			sendMessage(util.CrapTalker.insult(util.CrapTalker.INSULTS_START));
		}
		try {
			game = new Game(x, y, args);
			player = game.getPlayer(args.indexOf(clientName));
			if (mui instanceof ConnectionWindow) {
				((ConnectionWindow) mui).setGame(game, player);
			} else {
				System.out.println("mui should be a ConnectionWindow now!!");
			}
		} catch (InvalidMoveException e) {
			this.sendDisconnect("Invalid startstone position");
		}
		System.out.println("PlayerNumber:  " + args.indexOf(clientName));
		if (selectedAI == 1) {
			ai = new SmartAI(game, player);
		} else if (selectedAI == 2) {
			ai = new RandomAI(game, player);
		} else if (selectedAI == 3) {
			ai = new CustomAI(game, player,0,0,1);
		}

		status = INGAME;
	}

	/**
	 * Checks if this Client has the turn, if so, calls askMove().
	 * 
	 * @param args
	 *            The name of the Client whos turn it is
	 * @ensure askMove() is only called if the Client has the turn
	 */
	private void cmdTURN(final ArrayList<String> args) {
		if (status == INGAME) {
			if (args.size() == 1) {
				if (!game.getTurnSet()) {
					if (game.getPlayerCount() != 2) {
						game.setTurn(game.getPlayer(args.get(0)).getColor());
					} else {
						game.setTurn(game.getPlayer(args.get(0)).getColor() / 2);
					}
				}
				if (args.get(0).equals(this.clientName)) {
					myTurn = true;
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
	 * Calls processMove() to update game. Will send an insult if
	 * autoCraptalk==true;
	 * 
	 * @param args
	 *            The information of the move: args.get(n) 0 = x, 1 = y, 2 =
	 *            type, 3 = color
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
					e.printStackTrace();
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

	/**
	 * Activates displayGameOverScreen()
	 * 
	 * @param args
	 *            The scores
	 */
	private void cmdEND(ArrayList<String> args) {
		if (status == INGAME) {
			if (args.size() >= 4 && args.size() <= 8) {
				displayGameOverScreen(args);

			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}

	/**
	 * Someone from the server disconnected, currently does nothing except
	 * sending an insult if autoCraptalk==true
	 * 
	 * @param args
	 *            Possible message that comes with the disconnect
	 */
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

	/**
	 * The server has send a chat message. Might send an insult if autoCraptalk
	 * is on
	 * 
	 * @param args
	 *            args.get(0) it the senders name, the following are the message
	 * 
	 */
	private void cmdSAID(ArrayList<String> args) {
		if (status >= HANDSHAKE_SUCCESFULL) {
			if (args.size() >= 2) {
				if (serverFeatures.contains(util.Protocol.FEAT_CHAT)) {
					String name = args.remove(0);
					mui.addMessage(name, util.Util.concatArrayList(args));
					if (!name.equals(clientName)) {
						if (autoCrapTalk && Math.random() < 0.5) {
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

	/**
	 * Reacts to errors received from the server. Will disconnect when the name
	 * is in use. Will disconnect when it detects a desync in the board
	 * 
	 * @param args
	 *            The error code
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
	}

	/**
	 * Sends a message to the server
	 * Will convert to cyrillic if that is on
	 * @param msg	The chat message this Client wants to send
	 * @require msg!=null
	 */
	public void sendMessage(String msg) {
		try {
			String input = msg;
			if (convertToCyrillic) {
				input = util.CrapTalker.toCyrillic(input);
			}
			out.write(util.Protocol.CMD_SAY + " " + input + "\n");
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called when the client detects an error.
	 * Implanted to make it easier to debug, also possible to send errors to the server
	 * 
	 * @param errorCode	What error has occurred
	 */
	public void sendError(final int errorCode) {
		System.out.println(">>>>>>>ERROR, Last input: " + lastInput);
		System.out.println("##>STATUS<##  " + status);
		System.out.println(util.Protocol.CMD_ERROR + " " + errorCode);
	}

	/**
	 * Sends a command to the server
	 * Will add a command separator at the end, will not send commands the server does not support
	 * 
	 * @param command	The command to send
	 * @require	command!=null
	 */
	public void sendCommand(final String command) {

		try {
			if ((!command.startsWith(util.Protocol.CMD_SAY) || serverFeatures
					.contains(util.Protocol.FEAT_CHAT))) {
				System.out.println("Send command: " + command);
				out.write(command + "\n");
				out.flush();
			}
		} catch (IOException e) {
			System.out
			.println("Failed to send message to:  " + this.clientName);
		}
	}

	/**
	 * Sends a DISCONNECT command to the server, telling him this Client wants to disconnect
	 * 
	 * @param msg	possible message to be included
	 */
	public void sendDisconnect(final String msg) {

		mui.addMessage("Server", "Disconnecting:  " + msg);
		System.out.println("Disconnecting:  " + msg);
		try {
			out.write(util.Protocol.CMD_DISCONNECT + " " + msg);
			sock.close();
			status = DISCONNECTED;
		} catch (IOException e) {
			System.out.println("Could not send disconnect message");
		}

	}

	/**
	 * Sends a join command to the server, telling it this Client wants to join
	 * a lobby
	 * 
	 * @param slots
	 *            How many slots the lobby should have
	 */
	public void joinLobby(final int slots) {
		if (status == HANDSHAKE_SUCCESFULL) {
			sendCommand(util.Protocol.CMD_JOIN + " " + slots);
			status = INLOBBY;
		}
	}

	/**
	 * This method is called when its the clients turn. Will immediately send
	 * back an AI move if there is no human playing, otherwise informs the GUI
	 * to do a move.
	 * 
	 * @require myTurn==true;
	 */
	private void askMove() {

		System.out.println("Asking move..");

		// TODO laat GUI aangeven dat het jou beurt is
		// arr: 0 = x, 1 = y, 2 = type, 3 = color
		System.out.println(ai);
		aiMove = ai.getMove();
		if (!humanIsPlaying) {
			sendCommand(util.Protocol.CMD_MOVE + " "
					+ util.Util.concatArrayList(aiMove));
		}
	}

	/**
	 * Sends a (human) move to the server
	 * 
	 * @param arr
	 *            arr.get(n) 0 = x, 1 = y, 2 = type, 3 = color
	 * @return True if succesfull (if it was indeed your turn)
	 */
	public boolean doHumanMove(final int x, final int y, final int type,
			final int color) {
		boolean output = false;
		if (myTurn) {
			try {
				output = game.getBoard().canMove(x, y,
						player.getPiece(type, color));
				if (output) {
					sendCommand(util.Protocol.CMD_MOVE + " " + x + " " + y
							+ " " + type + " " + color);
				}
			} catch (InvalidPieceException e) {
				output = false;
			}

		}

		return output;
	}

	/**
	 * Processes a move on the board (and tells it to the GUI aswell)
	 * 
	 * @throws InvalidMoveException
	 *             if the move is not valid
	 * @param x
	 *            The X coordinate of the move
	 * @param y
	 *            The Y coordinate of the move
	 * @param type
	 *            The type of the Piece
	 * @param color
	 *            The color of the Piece
	 */
	private void processMove(int x, int y, int type, int color)
			throws InvalidMoveException {
		if (mui instanceof ActionWindow) {
			((ActionWindow) mui).doMove(x, y, type, color);
		} else {
			System.out.println("mui fail");
		}

		game.move(x, y, type, color);
		game.isGameOver(); // Updates its own version of the game (ie, remove
		// players if those cant do a move anymnore)
		myTurn = false;
	}

	/**
	 * Checks the gameOver stats for the winner, will print if you have won,
	 * plays victory sound and will send 'GG ez' if autoCraptalk is on and you
	 * won
	 * 
	 * @param args
	 *            The game stats
	 */
	private void displayGameOverScreen(ArrayList<String> args) {
		// TODO Score omzetten naar menselijke taal
		try {
			boolean won = hasWon(util.Util.ConvertToInt(args));

			// TODO dit geeft de popup weer met de score uitslag
			// TODO won boolean blijkt niet te kloppen, kijk dit ff na.
			JOptionPane.showMessageDialog(null, 
					"AI TYPE:" + ai.getClass() + "\n"
							+ "WON: " + won + "\n"
							+ "END " + args);

			((ActionWindow) mui).playAgainDialog();
			//Server kicked iedereen na de game, hoe te fixen?
			if (won) {
				if (autoCrapTalk) {
					sendMessage("CHAT GG ez");
				}
				SoundPlayer.playSound("resources/sounds2/VictoryMusic.wav");
				SoundPlayer.upVolume();

			} else {
				SoundPlayer.playSound("");
			}

		} catch (NumberFormatException e) {
			System.out.println("Wrong stats received");
		}
	}

	/**
	 * Sets the mui variable this Client has.
	 * 
	 * @param mui
	 *            The new mui
	 */
	void setMUI(final MessageUI mui) {
		this.mui = mui;
	}
	/**
	 * Returns the mui variable this Client has.
	 */
	MessageUI getMUI() {
		return mui;
	}
	/**
	 * Returns if the Client is connected or not.
	 */
	protected boolean isConnected(){
		return connected;
	}



	/**
	 * Used by the GUI to select an AI. 1 being Smart 2 being Random 3 being EWall
	 * 
	 * @param i
	 */
	public void setAI(final int i) {
		selectedAI = i;
	}

	/**
	 * Used by the GUI to determine if a human is playing.
	 * 
	 * @param b	whether or not a human is playing
	 */
	public void setIsPlaying(final boolean b) {
		humanIsPlaying = b;
	}

	/**
	 * Used by the GUI to enable/disable the flame bot crap talker.
	 * 
	 * @param f	whether or not autoCraptalk should be on
	 */
	public void setFlame(final boolean f) {
		autoCrapTalk = f;
		System.out.println("Set autoCrapTalk to: " + autoCrapTalk);
	}

	/**
	 * Used by the GUI to enable/disable the cyrillic converter.
	 * 
	 * @param c	whether or not to convert the messages send to cyrillic
	 */
	public void setCyrillic(final boolean c) {
		convertToCyrillic = c;
		System.out.println("Set convertToCyrillic to: " + convertToCyrillic);
	}

	/**
	 * @return the move the ai would do (from the most recent turn given to you)
	 */
	public ArrayList<Integer> getAIMove() {
		return aiMove;
	}

	/**
	 * Checks if you have won according to the given stats
	 * @param arr	The statistics of the game (typically received from an END command)
	 * @return	True if you have won (false on tie or lose)
	 * @require arr.size() == 2*game.getPlayerCount()
	 */
	public boolean hasWon(ArrayList<Integer> arr) {
		boolean won = true;
		int ownScore = 0;
		if (game.getPlayerCount() != 2) {
			ownScore = arr.get(player.getColor() * 2);
		} else {
			if (player.getColor() == 0) {
				ownScore = arr.get(0);
			} else {
				ownScore = arr.get(2);
			}
		}
		ArrayList<Integer> otherScores = new ArrayList<Integer>();
		for (int i = 0; i < arr.size(); i = i + 2) {
			if (i != player.getColor() * 2) {
				otherScores.add(arr.get(i));
			}
		}

		for (int i : otherScores) {
			if (i >= ownScore) {
				won = false;
			}
		}
		return won;
	}
}
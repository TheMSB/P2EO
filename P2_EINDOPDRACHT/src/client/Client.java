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

import ai.*;

import server.Server;
import util.SoundPlayer;

public class Client extends Thread {

	private String clientName;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private boolean connected;
	private String lastInput;
	private int status;
	private Game game;
	private Player player;
	private AI ai;
	/**
	 * Represents which AI to use, 1 = smart, 2 = random 3 = E-Wall
	 */
	private int selectedAI = 1;
	private boolean humanIsPlaying = true;
	private MessageUI mui;
	private boolean autoCrapTalk = false;
	private boolean convertToCyrillic = false;
	private boolean myTurn;
	private ArrayList<Integer> aiMove;

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

	public void run() {
		while (true) {
			try {
				while (connected) {
					lastInput = in.readLine();
					System.out.println(lastInput);
					if (lastInput != null) {
						readCommand(new Scanner(lastInput));
					} else {
						System.out.println("socket closing");
						sock.close();
						connected = false;
						System.out.println("Socket closed");
						sendError(util.Protocol.ERR_INVALID_COMMAND);
					}

				}
			} catch (IOException e) {
				connected = false;
			}
			
			//TODO hier iets naar GUI sturen zodat je opnieuw kunt connecten
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
			((ConnectionWindow) mui).setGame(game, player); // TODO dit kan
															// netter
		} catch (InvalidMoveException e) {
			this.sendDisconnect("Invalid startstone position");
		}
		System.out.println("PlayerNumber:  " + args.indexOf(clientName));
		if (selectedAI == 1) {
			ai = new SmartAI(game, player); // TODO mogelijk ingame aan te
											// laten
			// passen
		} else if (selectedAI == 2) {
			ai = new RandomAI(game, player); // TODO mogelijk ingame aan te
												// laten
			// passen
		} else if (selectedAI == 3) {
			ai = new EWallAI(game, player); // TODO mogelijk ingame aan te
			// laten
			// passen
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
				if (!game.getTurnSet()) {
					if (game.getPlayerCount() != 2) {
						game.setTurn(game.getPlayer(args.get(0)).getColor());
					} else {
						game.setTurn(game.getPlayer(args.get(0)).getColor() / 2);
					}
				}
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
		// arr: 0 = x, 1 = y, 2 = type, 3 = color
		System.out.println(ai);
		aiMove = ai.getMove(); // TODO dit door mens laten doen
		if (!humanIsPlaying) {
			sendCommand(util.Protocol.CMD_MOVE + " "
					+ util.Util.concatArrayList(aiMove));
		}
	}

	/**
	 * Returns the move the ai would do (from the most recent turn given to you)
	 * 
	 * @return
	 */
	public ArrayList<Integer> getAIMove() {
		return aiMove;
	}

	/**
	 * Sends a move to the server
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
					// System.out.println("addMessaged");
					// System.out.println(name +" | "+clientName);
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

	private void displayGameOverScreen(ArrayList<String> args) {
		// TODO game over screen displayen
		try {
			boolean won = true;
			int ownScore = 0;
			ArrayList<Integer> arr = util.Util.ConvertToInt(args);
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
				if (i > ownScore) {
					won = false;
				}
			}
			System.out.println("AI TYPE:" + ai.getClass());
			System.out.println("WON: " + won);
			System.out.println("END " + args);

			if (won) {
				sendMessage("CHAT GG ez");
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
		if (mui instanceof ActionWindow) {
			((ActionWindow) mui).doMove(x, y, type, color);
		} else {
			System.out.println("mui fail");
		}

		game.move(x, y, type, color);
		game.isGameOver();
		myTurn = false;

		// System.out.println("Adding ring at: "+ x+" , "+y);
	}

	void setMUI(MessageUI mui) {
		this.mui = mui;
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
	 * Used by the GUI to select an AI. 1 being Smart 2 being Random
	 * 
	 * @param i
	 */
	public void setAI(final int i) {
		selectedAI = i;
	}

	/**
	 * Used by the GUI to determine if a player plays himself.
	 * 
	 * @param b
	 */
	public void setIsPlaying(final boolean b) {
		humanIsPlaying = b;
	}

	/**
	 * Used by the GUI to enable/disable the flame bot crap talker.
	 * 
	 * @param f
	 */
	public void setFlame(final boolean f) {
		autoCrapTalk = f;
		System.out.println("Set autoCrapTalk to: " + autoCrapTalk);
	}

	/**
	 * Used by the GUI to enable/disable the cyrillic converter.
	 * 
	 * @param c
	 */
	public void setCyrillic(final boolean c) {
		convertToCyrillic = c;
		System.out.println("Set convertToCyrillic to: " + convertToCyrillic);
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
	 * Stuurt de server een DISCONNECT command, met de meegegeven message.
	 * 
	 * @param msg
	 */
	public void sendDisconnect(final String msg) {

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
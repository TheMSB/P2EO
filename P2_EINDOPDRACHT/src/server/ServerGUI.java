package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * commandline application of the Server
 * @author I3anaan
 *
 */
public class ServerGUI {

	/**
	 * The Server
	 */
	private static Server server;
	/**
	 * Input for the serverGUI
	 */
	private static BufferedReader in;
	/**
	 * Output for the serverGUI (command line)
	 */
	private static PrintStream out;
	/**
	 * Indicates if the server should be running
	 */
	private static boolean running = true;

	/**
	 * Makes a new Server, then proceeds to be able to receive input from the
	 * default inputstream
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			in = new BufferedReader(new InputStreamReader(System.in,
					Server.ENCODING));
			out = System.out;
			newServer(4242);
		} catch (IOException e) {
			retryNewServer();
		}

		while (running) {
			String lastInput = null;
			try {
				lastInput = in.readLine();
			} catch (IOException e) {
			}
			if (lastInput != null) {
				readCommand(new Scanner(lastInput));
			}
		}

		server = null;
		out.println("Server closing");
	}

	/**
	 * Reads a command from the scanner
	 * 
	 * @param scanner
	 *            The scanner which contains the line to read
	 */
	private static void readCommand(Scanner scanner) {
		if (scanner.hasNext()) {
			String command = scanner.next();
			ArrayList<String> args = new ArrayList<String>();
			while (scanner.hasNext()) {
				args.add(scanner.next());
			}
			checkCommand(command, args);
		}
	}

	/**
	 * Checks if the received command is recognized
	 * 
	 * @param command
	 * @param args
	 *            Possible arguments for the command
	 */
	public static void checkCommand(String command, ArrayList<String> args) {
		if (command.equals("RESTART")) {
			cmdRESTART(args);
		} else if (command.equals("SHUTDOWN")) {
			cmdSHUTDOWN(args);
		} else {
			out.println("Command not found. (known:  RESTART, PAUSE, SHUTDOWN)");
		}
	}

	/**
	 * Restarts the server
	 * 
	 * @param args
	 *            first argument is the new port, Second is the name
	 */
	private static void cmdRESTART(ArrayList<String> args) {
		if (args.size() == 1) {
			try {
				newServer(Integer.parseInt(args.get(0)));
			} catch (NumberFormatException e) {
				out.println("Dat is geen nummer. Syntax: RESTART [port]");
			}
		} else {
			out.println("Foute parameters. Syntax: RESTART [port]");
		}
	}

	/**
	 * Shuts the server down
	 * 
	 * @param args
	 *            Does nothing
	 */
	private static void cmdSHUTDOWN(ArrayList<String> args) {
		server.shutDown();
		//TODO controleren of server goed aflsuit
		running = false;
	}

	/**
	 * Makes a new server, overwrites the older one
	 * 
	 * @param port
	 *            The new port for the server
	 * @param name
	 *            Name of the server
	 */
	private static void newServer(int port) {
		try {
			if(server!=null){
				server.shutDown();
			}
			server = new Server(port,"P2EO server Martijn en Derk");
			server.start();
			in = new BufferedReader(new InputStreamReader(System.in,
					Server.ENCODING));
			out = System.out;
			System.out.println("Server started at port:  " + port +"\n IP: "+InetAddress.getLocalHost());
		} catch (IOException e) {
			retryNewServer();
		}
	}
	
	private static void retryNewServer(){
		System.out.println("Error, ServerSocket could not be made, please enter a different port:");
		String lastInput = null;
		try {
			lastInput = in.readLine();
		} catch (IOException e2) {}
		if (lastInput != null) {
			readCommand(new Scanner("RESTART "+lastInput));
		}
	}
}

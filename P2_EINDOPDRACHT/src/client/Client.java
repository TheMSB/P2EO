package client;

import java.io.BufferedReader;
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

import server.Server;

public class Client extends Thread{
	//TODO Veel dubbele command leescode, mogelijkheid tot nieuwe klasse
	//client een status geven
	//commands invoeren
	
	/**
	 * 
	 * 
	 * >>>>>>>>>>>>>>>> hoe weet je dat de lobby gestopt is?
	 * 
	 */

	private String name;
	private Socket sock;
	private BufferedReader  in;
    private BufferedWriter  out;
    private boolean connected;
    private String lastInput;
    private boolean serverAlive;
    private int status;
    
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
    //public static final int CONNECTED = 30;
    
    
	
	/**
	 * Maakt nieuwe client aan met een naam, meestal aangeroepen door clientGUI, connect ook al vast
	 * @param name
	 */
	public Client(String name){
		this.name = name;
        
		clientFeatures = new ArrayList<String>();
		
		//Ga er vanuit dat de server aan staat in het begin
		serverAlive = true;
		
		try {
			connectToServer(4242,InetAddress.getByName("localhost"));
		} catch (UnknownHostException e) {
			System.out.println("IP not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Failed to make Socket");
			e.printStackTrace();
			serverAlive = false;
		}
		
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream(),Server.ENCODING));
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(),Server.ENCODING));
		} catch (UnsupportedEncodingException e1) {
			System.out.println("Not Supported Encoding, this program requires UTF-8");
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		status = DISCONNECTED;
	}
	
	public void run() {
		while(true){ //TODO clientGUI moet client kunnen afsluiten
			try {
				String lastInput;
				while (connected) {
					lastInput = in.readLine();
					if (lastInput != null) {
						readCommand(new Scanner(lastInput));
					} else {
						sendError(util.Protocol.ERR_INVALID_COMMAND);
					}
					
				}
			} catch (IOException e) {
				sendDisconnect("Reconnecting");
				connected = false;
			}
			
			
			//Als code hierkomt is er een disconnect
			if (serverAlive) {
				try {
					connectToServer(4242, InetAddress.getByName("localhost"));
					//TODO poort + ip variabel maken					
				} catch (IOException e) {
					System.out
							.println("Server not reaction, stopping reconnect attempt...");
					serverAlive = false;
				}
			}
		}
	}
	
	
	private void readCommand(Scanner scanner)
	{
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
	
	public void checkCommand(String command, ArrayList<String> args) {
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
		} else {
			sendError(util.Protocol.ERR_COMMAND_NOT_FOUND);
		}
	}
	
	//TODO kijken naar public/private van zowel client als clienthandler
	private void cmdCONNECTED(ArrayList<String> args) {
		if (status == HANDSHAKE_PENDING_1) {
			if (args.size() == 0) {
				status = HANDSHAKE_PENDING_2;
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}
	
	private void cmdFEATURES(ArrayList<String> args) {
		if (status == HANDSHAKE_PENDING_2) {
			if (args.size() >= 0) {
				for (String a : args) {
					for (String b : clientFeatures) {
						if (a.equals(b))
						{
							serverFeatures.add(a);
						}
					}
				}
				sendCommand(util.Protocol.CMD_FEATURED+" "+Server.concatArrayList(clientFeatures));
				status = HANDSHAKE_SUCCESFULL;
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}
	
	private void cmdSTART(ArrayList<String> args) {
		if (status == INLOBBY) {
			if (args.size() >= 4 && args.size()<=6) {
				status = INGAME;
				//TODO spul doorgeven aan de game
				startGame();
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}
	
	private void startGame(){
		//TODO implementeren
	}
	
	private void cmdTURN(ArrayList<String> args) {
		if (status == INGAME) {
			if (args.size() == 1) {
				if(args.equals(this.name)){
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
	 * Deze methode wordt aangeroepen als deze client aan de beurt is, vragend aan mens of ai om een zet door te geven
	 */
	private void askMove(){
		//TODO laat GUI aangeven dat het jou beurt is, kies zet, doe die zet dan.
		//TODO hoe zit het met de tijd die je hiervoor hebt?
		int x = 1;
		int y = 1;
		int type = 1;
		int color = 1; //TODO deze verkrijgen van gui of ai
		
		sendCommand(util.Protocol.CMD_MOVE+ " "+x+" "+y+" "+type+" "+color);
	}
	
	private void cmdMOVED(ArrayList<String> args) {
		if (status == INGAME) {
			if (args.size() == 1) {
				processMove();
			} else {
				sendError(util.Protocol.ERR_INVALID_COMMAND);
			}
		} else {
			sendError(util.Protocol.ERR_COMMAND_UNEXPECTED);
		}
	}
	
	private void processMove(){
		//TODO bordt bijwerken
	}
	
	/**
	 * Stuurt een join command naar de server, vragend om in een lobby geplaatst te worden
	 * @param slots
	 */
	public void joinLobby(int slots){
		if(status == HANDSHAKE_SUCCESFULL){ //TODO zou dit ook INLOBBY kunnen zijn?, hoe weet je dat de lobby gestopt is?
			sendCommand(util.Protocol.CMD_JOIN+" "+slots);
			status = INLOBBY;
		}
		
	}
	
	
	
	public void sendError(int errorCode) {
		System.out.println(">>>>>>>ERROR, Last input: " + lastInput);
		System.out.println("##>STATUS<##  " + status);
		sendCommand(util.Protocol.CMD_ERROR + errorCode);
	}

	public void sendCommand(String command) {
		try {
			out.write(command + "\n");
			out.flush();
		} catch (IOException e) {
			System.out.println("Failed to send message to:  " + this.name);
			// TODO dit oplossen? mogelijk met retry na seconde ofzo
			// TODO of hier al reconnect proberen?
		}
	}
	
	
	
	public void sendDisconnect(String msg){
		status = DISCONNECTED;
		try{
			out.write(util.Protocol.CMD_DISCONNECT+" "+msg);
			
		}catch(IOException e){
			System.out.println("Could not send disconnect message");
		}
	}
	
	/**
	 * Maakt verbinding met een server, stuurt eerste command voor handshake
	 * @param port
	 * @param ip
	 * @throws IOException	Als het verbinden fout gaat
	 * @require Of geen verbinding, Of het eerst versturen van een disconnect
	 */
	public void connectToServer(int port, InetAddress ip) throws IOException{
		sendDisconnect("Connecting to a (new) server");
		
		sock = new Socket(ip,port);
		connected = sock!=null;
		if(connected){
			serverAlive = true;
			sendCommand(util.Protocol.CMD_CONNECT +" "+name);
			status = HANDSHAKE_PENDING_1;
		}
	}
}

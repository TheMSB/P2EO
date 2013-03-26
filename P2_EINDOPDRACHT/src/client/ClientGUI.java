package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JPanel;

import server.Server;



public class ClientGUI {

	//CHECKSTYLE:OFF
	//---- Instance Variables --------------------------


	// Windows and Panels
	private JFrame window = new JFrame("RINGZ");
	private JPanel menu = new JPanel();
	private JPanel chatbox = new JPanel();
	
	private JPanel board = new JPanel();
	private JPanel cells[] = new JPanel[25];
	//CHECKSTYLE:ON
	
	//---- Constructor ---------------------------------
	
	public ClientGUI(){
		
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Client client = new Client("Derk"+Math.random());
		client.start();

	}

}

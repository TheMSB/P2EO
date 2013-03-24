package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import server.Server;

public class ClientGUI {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Client client = new Client("Derk");
		client.start();
		client.joinLobby(4);
	}

}

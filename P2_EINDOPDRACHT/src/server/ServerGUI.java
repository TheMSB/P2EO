package server;

import java.io.IOException;

public class ServerGUI {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Server server = new Server(util.Protocol.PORT,"Super Server!");
			server.start();
			System.out.println("Server started at port:  "+util.Protocol.PORT);
		} catch (IOException e) {
			System.out.println("Error Socket could not be made");
			e.printStackTrace();
		}
	}

}

package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler extends Thread {
	
	private String name;
	private Socket sock;
	private Server server;
	private BufferedReader   in;
    private BufferedWriter   out;
    
    private boolean ClientHasProtocol;
	
    /**
     * Maakt CLientHandler
     * @param server	De Server
     * @param sock		De Socket waarmee de Client verbonden is met de Server
     * @throws UnsupportedEncodingException			Als de encoding niet gesupport is
     * @throws IOException							Als er iets anders mis gaat
     */
	public ClientHandler(Server server, Socket sock) throws UnsupportedEncodingException, IOException{
		this.sock = sock;
        this.server = server;
        in = new BufferedReader(new InputStreamReader(sock.getInputStream(),Server.ENCODING));
        out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(),Server.ENCODING));
	}
	/**
	 * Blijft klaarstaan om commands te ontvangen
	 */
	public void run()
	{
		String input;
		while(!ClientHasProtocol){
			try {
				input = in.readLine();
				if(input!=null){
					Scanner scanner = new Scanner(input);
					if(scanner.hasNext())
					{
						//Mogelijke input: CONNECT felix
						if(scanner.next().equals(util.Protocol.CMD_CONNECT))
						{
							if(scanner.hasNext())
							{
								this.name = scanner.next();
								ClientHasProtocol = true;
								//TODO Features nog checken;
								server.approve(this);
								//TODO checken of scanner.next() null kan teruggeven;
							}
						}
					}
				}
			} catch (IOException e) {
				// TODO Send general error
				e.printStackTrace();
			}
		}
		while(ClientHasProtocol){
			//TODO: Doe spul
		}
	}
	
	
	public String toString()
	{
		return this.name;
	}
}
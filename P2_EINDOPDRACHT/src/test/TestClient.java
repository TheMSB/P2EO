package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import server.Server;

public class TestClient extends Thread{

	private String			name;
	private int				slots;
	private Socket          sock;
    private BufferedReader  in;
    private BufferedWriter  out;
	
    public static void main(String[] args){
    	new TestClient("TestClientVanMain3",4).start();
    }
    
	public TestClient(String name, int slots){
		try{
			this.name = name;
			this.slots = slots;
			sock = new Socket(InetAddress.getByName("localhost"), 4242);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream(),"UTF-8"));
	        out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(),"UTF-8"));
			
	        doHandshake();
			joinLobby();
	        
		}catch(IOException e){
		}
	}
	
	public TestClient(String[] args){
		try{
			sock = new Socket(InetAddress.getByName("localhost"), 4242);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream(),"UTF-8"));
	        out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(),"UTF-8"));
			
	        for(String i : args){
	        	sendCommand(i);
	        }
	        
		}catch(IOException e){
		}
	}
	
	public void sendCommand(String command){
		try {
			out.write(command + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void doHandshake(){
		try {
			out.write(util.Protocol.CMD_CONNECT+" "+name + "\n");
			out.write(util.Protocol.CMD_FEATURED+" "+"\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void joinLobby(){
		try{
		if(slots!=0){
			out.write(util.Protocol.CMD_JOIN+" "+slots+"\n");
		}else{
			out.write(util.Protocol.CMD_JOIN+" \n");
		}
		out.flush();
		}catch(IOException e){
		}
	}
	
	public void run(){
		try {
			String input;
			while(true){
				input = in.readLine();
				System.out.println(name+":   "+input);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}

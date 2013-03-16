package test;

import static org.junit.Assert.*;

import java.io.IOException;

import server.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ServerTest {

	private static Server server;
	
	@Before
	public static void createServer(){
		try {
			server = new Server(4242,"Deze server");
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRun() {
		fail("Not yet implemented");
	}

	@Test
	public void testServer() {
		assertEquals("Poort = 4242",4242,server.getPort());
		assertEquals("Naam = 'Deze server'","Deze server",server.getServerName());
	}

	
	//TODO synchronisatie fixen
	@Test
	public void testGetBestLobby() {
		new TestClient("c1.1",2).start();
		assertEquals("Best lobby = 2",2,Server.getBestLobby());
		new TestClient("c2.1",3).start();
		assertEquals("Best lobby = 2",2,Server.getBestLobby());
		new TestClient("c2.2",3).start();
		assertEquals("Best lobby = 2",2,Server.getBestLobby());
		new TestClient("c3.1",0).start();
		//TODO Zorgen dat hij hier wacht op server ofzo
		assertEquals("Best lobby = 3",3,Server.getBestLobby());
		new TestClient("c4.1",4).start();
		assertEquals("Best lobby = 3",3,Server.getBestLobby());
		new TestClient("c5.1",0).start();
		new TestClient("c5.2",4).start();
		new TestClient("c5.3",4).start();
		assertEquals("Best lobby = 4",4,Server.getBestLobby());
	}

	@Test
	public void testApprove() {
		//server.approve(new ClientHandler(4242,new Socket())); //TODO hoe protected methoden te testen
	}

	@Test
	public void testJoinLobby() {
		fail("Not yet implemented");
	}

	@Test
	public void testBroadcastMessage() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFeatures() {
		fail("Not yet implemented");
	}

	@Test
	public void testConcatArrayList() {
		fail("Not yet implemented");
	}

}

package test;

import static org.junit.Assert.*;

import java.io.IOException;

import server.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class ServerTest {

	private static Server server;
	
	@BeforeClass
	public static void createServer(){
		try {
			server = new Server(4242,"Deze server");
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

	@Test
	public void testShutDown() {
		fail("Not yet implemented");
	}

	@Test
	public void testApprove() {
		fail("Not yet implemented");
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

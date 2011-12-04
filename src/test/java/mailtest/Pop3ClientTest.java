package mailtest;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;


/**
 * Unit test for simple App.
 */
public class Pop3ClientTest {
	
	private static GreenMail server;
	
	@BeforeClass
	public static void setUpClass() {
		server = new GreenMail(new ServerSetup(10110, null, ServerSetup.PROTOCOL_POP3));
		
		server.start();
	}
	
	@AfterClass
	public static void tearDownClass() {
		server.stop();
	}
	
	@Test
	public void getMails() throws IOException {
		server.setUser("gensan", "mypassword");
		
		Pop3Client client = new Pop3Client();
		try {
			client.connect("localhost", 10110, "gensan", "mypassword");
		} finally {
			client.close();
		}
	}

}

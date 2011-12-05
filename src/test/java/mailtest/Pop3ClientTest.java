package mailtest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import javax.mail.Message;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;


/**
 * Unit test for simple App.
 */
public class Pop3ClientTest {
	
	private static GreenMail server;
	
	private static final int POP3_PORT = 10110;
	private static final int SMTP_PORT = 10025;
	
	private static final ServerSetup POP3 = new ServerSetup(POP3_PORT, null, ServerSetup.PROTOCOL_POP3);
	private static final ServerSetup SMTP = new ServerSetup(SMTP_PORT, null, ServerSetup.PROTOCOL_SMTP);
	
	@BeforeClass
	public static void setUpClass() {
		server = new GreenMail(new ServerSetup[]{POP3, SMTP});
		server.start();
	}
	
	@AfterClass
	public static void tearDownClass() {
		server.stop();
	}
	
	@Test
	public void getMails() throws IOException {
		//create user
		server.setUser("gensan@localhost", "gensan", "mypassword");
		
		//send mails
		GreenMailUtil.sendTextEmail("gensan@localhost", "gensan-from@localhost", "my subject(1)", "Hello world 1!!", SMTP);
		GreenMailUtil.sendTextEmail("gensan@localhost", "gensan-from@localhost", "my subject(2)", "Hello world 2!!", SMTP);
		
		Pop3Client client = new Pop3Client();
		try {
			client.connect("localhost", POP3_PORT, "gensan", "mypassword");
			
			List<Message> messages = client.getMails();
			
			assertThat(messages.size(), is(2));
		} finally {
			client.close();
		}
	}

}

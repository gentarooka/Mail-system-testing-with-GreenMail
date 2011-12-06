package mailtest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.user.UserException;
import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;

/**
 * Unit test for simple App.
 */
public class Pop3ClientTest {
	private static GreenMail server;
	
	private InputStream mailStream = null;

	@BeforeClass
	public static void setUpClass() {
		Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());

		server = new GreenMail(new ServerSetup[] { ServerSetupTest.POP3S });
		server.start();
	}

	@AfterClass
	public static void tearDownClass() {
		server.stop();
	}
	
	@Before
	public void setUp() {
		// do nothing
	}
	
	@After
	public void tearDown() {
		if (mailStream != null) {
			try {
				mailStream.close();
			} catch (IOException e) {
				// do nothing.
				e.printStackTrace();
			}
		}
	}
	
	private MimeMessage createMimeMessage(String resourceName) throws MessagingException {
		mailStream = this.getClass().getResourceAsStream(resourceName);
		MimeMessage message = new MimeMessage(null, mailStream);
		return message;
	}

	@Test
	public void getMails() throws IOException, MessagingException, UserException {
		// create user
		GreenMailUser user = server.setUser("gensan@localhost", "gensan", "mypassword");
		
		MimeMessage message = createMimeMessage("/messages/001_message.txt");

		user.deliver(message);

		Pop3Client client = new Pop3Client();
		client.setSecure(true);

		try {
			client.connect("localhost", ServerSetupTest.POP3S.getPort(), "gensan", "mypassword");

			System.out.println("connected");
			List<Message> messages = client.getMails();

			assertThat(messages.size(), is(1));
		} finally {
			client.close();
		}

	}

}

package mailtest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

/**
 * Pop3Client
 */
public class Pop3Client {

	private Store store = null;
	private Folder folder = null;
	
	private boolean isSecure = false;
	
	protected void setSecure(boolean secure) {
		isSecure = secure;
	}

	public void connect(String host, int port, String user, String password) throws IOException {
		String protocol = "pop3";
		URLName urln = new URLName(protocol, host, port, null, user, password);

		Properties props = new Properties();
		
		if (isSecure) {
			props.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.setProperty("mail.pop3.socketFactory.fallback", "false");
		}

		Session session = Session.getDefaultInstance(props, null);

		try {
			store = session.getStore(urln);
			store.connect();
			
			folder = store.getFolder("INBOX");
			folder.open(Folder.READ_ONLY);
		} catch (NoSuchProviderException e) {
			throw new IOException(e);
		} catch (MessagingException e) {
			throw new IOException(e);
		}
	}

	public List<Message> getMails() throws IOException {
		Message[] messages;
		try {
			// receive messages
			messages = folder.getMessages();
		} catch (MessagingException e) {
			throw new IOException(e);
		}

		return Arrays.asList(messages);
	}

	public void close() throws IOException {
		MessagingException fail = null;
		
		if (folder != null) {
			try {
				folder.close(false);
			} catch (MessagingException e) {
				fail = e;
			}
		}
		
		if (store != null) {
			try {
				store.close();
			} catch (MessagingException e) {
				if (fail != null) {
					fail = e;
				} else {
					e.printStackTrace();
				}
			}
		}
		
		if (fail != null) {
			throw new IOException(fail);
		}
		
	}

}

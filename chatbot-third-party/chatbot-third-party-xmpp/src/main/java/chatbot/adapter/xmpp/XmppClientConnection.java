package chatbot.adapter.xmpp;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import chatbot.client.ClientConnection;
import chatbot.client.ConnectionListener;
import chatbot.client.MessageChannel;

public class XmppClientConnection implements ClientConnection {

	private final String server;
	private final String username;
	private final String password;
	private final String nickname;
	private final Connection connection;
	private final XmppKeepAliveThread keepAlive;

	public XmppClientConnection(String server, int port, String username,
			String password, String nickname) {
		ConnectionConfiguration configuration = new ConnectionConfiguration(
				server, port);

		this.server = server;
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.connection = new XMPPConnection(configuration);
		this.keepAlive = new XmppKeepAliveThread();
	}

	@Override
	public void connect(ConnectionListener listener) {
		try {
			this.connection.connect();
		} catch (XMPPException e) {
			throw new IllegalStateException("Unable to connect to XMPP server",
					e);
		}

		try {
			this.connection.login(this.username, this.password);
		} catch (XMPPException e) {
			throw new IllegalStateException("Unable to login to XMPP server", e);
		}
		
		this.keepAlive.start();

		listener.onConnectionSuccessful();
	}

	@Override
	public MessageChannel channel(String name) {
		MultiUserChat chatRoom = new MultiUserChat(connection, name
				+ "@conference." + this.server);
		try {
			chatRoom.join(this.nickname);
		} catch (XMPPException e) {
			throw new IllegalStateException("Unable to join chatroom: " + name, e);
		}
		drainExistingMessages(chatRoom);
		return new XmppMessageChannelAdapter(chatRoom);
	}
	
	private void drainExistingMessages(MultiUserChat chatRoom) {
		while (chatRoom.nextMessage(100) != null) {
			// do nothing
		}
	}
}
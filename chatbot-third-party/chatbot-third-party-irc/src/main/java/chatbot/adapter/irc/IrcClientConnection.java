package chatbot.adapter.irc;

import org.relayirc.chatengine.Channel;
import org.relayirc.chatengine.ChatEngine;
import org.relayirc.chatengine.IChatEngine;

import chatbot.client.ClientConnection;
import chatbot.client.ConnectionListener;
import chatbot.client.MessageChannel;

public class IrcClientConnection implements ClientConnection {

	private final IChatEngine chatEngine;

	public IrcClientConnection(String server, int port, String username,
			String password) {
		this.chatEngine = new ChatEngine(server, port, username, username,
				username, username);
	}

	@Override
	public void connect(ConnectionListener listener) {
		registerConnectionListener(listener);
		this.chatEngine.connect();

	}

	@Override
	public MessageChannel channel(String name) {
		Channel bareIrcChannel = new Channel(name, this.chatEngine);
		bareIrcChannel.connect();
		return new IrcMessageChannelAdapter(bareIrcChannel);
	}
	
	private void registerConnectionListener(ConnectionListener listener) {
		IrcConnectionListenerAdapter connectionListenerAdapter = new IrcConnectionListenerAdapter(listener);
		this.chatEngine.addChatEngineListener(connectionListenerAdapter);
	}
}
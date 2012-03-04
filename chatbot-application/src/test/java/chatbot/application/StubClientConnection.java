package chatbot.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;

import chatbot.client.ClientConnection;
import chatbot.client.ConnectionListener;

@Ignore("Not a test, this is a stub client connection used to test the chatbot")
public class StubClientConnection implements ClientConnection {

	private final Map<String, StubMessageChannel> messageChannels;

	public StubClientConnection() {
		this.messageChannels = new HashMap<String, StubMessageChannel>();
	}

	@Override
	public void connect(ConnectionListener listener) {
		listener.onConnectionSuccessful();
	}

	@Override
	public StubMessageChannel channel(String name) {
		StubMessageChannel messageChannel = new StubMessageChannel();
		this.messageChannels.put(name, messageChannel);
		return messageChannel;
	}

	public boolean hasChannel(String channelName) {
		return this.messageChannels.containsKey(channelName);
	}
	
	public void simulateClientMessage(String channelName, String user, String message) {
		this.messageChannels.get(channelName).simulateMessageFromUser(user, message);
	}

	public List<String> responses(String channelName) {
		return this.messageChannels.get(channelName).sentMessages();
	}
}
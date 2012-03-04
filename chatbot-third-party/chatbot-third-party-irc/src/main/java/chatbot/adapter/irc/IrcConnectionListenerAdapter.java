package chatbot.adapter.irc;

import org.relayirc.chatengine.ChatEngineAdapter;
import org.relayirc.chatengine.ChatEngineEvent;

import chatbot.client.ConnectionListener;

public class IrcConnectionListenerAdapter extends ChatEngineAdapter {

	private final ConnectionListener chatbotListener;

	public IrcConnectionListenerAdapter(ConnectionListener listener) {
		this.chatbotListener = listener;
	}
	
	@Override
	public void onConnection(ChatEngineEvent event) {
		this.chatbotListener.onConnectionSuccessful();
	}
}
package chatbot.application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chatbot.client.Message;
import chatbot.client.MessageChannel;
import chatbot.client.MessageListener;

public class StubMessageChannel implements MessageChannel {

	private final Set<MessageListener> messageListeners;
	private final List<String> sentMessages;

	public StubMessageChannel() {
		this.messageListeners = new HashSet<MessageListener>();
		this.sentMessages = new ArrayList<String>();
	}

	@Override
	public void registerListener(MessageListener listener) {
		this.messageListeners.add(listener);
	}

	@Override
	public void sendMessage(String message) {
		this.sentMessages.add(message);
	}

	public void simulateMessageFromUser(String user, String payload) {
		Message message = new Message(user, payload);
		for (MessageListener listener : this.messageListeners) {
			listener.onMessageReceived(message);
		}
	}

	public List<String> sentMessages() {
		return this.sentMessages;
	}
}
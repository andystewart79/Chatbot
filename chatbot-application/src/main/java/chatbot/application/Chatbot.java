package chatbot.application;

import chatbot.client.ClientConnection;
import chatbot.client.ConnectionListener;
import chatbot.client.MessageChannel;
import chatbot.client.MessageListener;
import chatbot.phrase.Phrases;
import chatbot.response.RespondingMessageListener;
import chatbot.response.ResponseStrategy;

public class Chatbot implements ConnectionListener {

	private final ClientConnection clientConnection;
	private final String channelName;
	private final ResponseStrategy responseStrategy;
	private final Phrases phrases;

	public Chatbot(ClientConnection connection, String channelName,
			ResponseStrategy responseStrategy, Phrases phrases) {
		this.clientConnection = connection;
		this.channelName = channelName;
		this.responseStrategy = responseStrategy;
		this.phrases = phrases;
	}

	public void connect() {
		this.clientConnection.connect(this);
	}

	@Override
	public void onConnectionSuccessful() {
		MessageChannel channel = this.clientConnection
				.channel(this.channelName);
		MessageListener messageListener = new RespondingMessageListener(
				this.responseStrategy, this.phrases, channel);
		channel.registerListener(messageListener);
	}
}
package chatbot.adapter.irc;

import org.relayirc.chatengine.Channel;

import chatbot.client.MessageChannel;
import chatbot.client.MessageListener;

public class IrcMessageChannelAdapter implements MessageChannel {

	private final Channel ircChannel;

	public IrcMessageChannelAdapter(Channel channel) {
		this.ircChannel = channel;
	}

	@Override
	public void registerListener(MessageListener listener) {
		IrcMessageListenerAdapter listenerAdapter = new IrcMessageListenerAdapter(
				listener);
		this.ircChannel.addChannelListener(listenerAdapter);
	}

	@Override
	public void sendMessage(String message) {
		this.ircChannel.sendMessage(message);
	}
}
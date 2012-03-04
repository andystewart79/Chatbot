package chatbot.adapter.irc;

import org.relayirc.chatengine.ChannelAdapter;
import org.relayirc.chatengine.ChannelEvent;

import chatbot.client.Message;
import chatbot.client.MessageListener;

public class IrcMessageListenerAdapter extends ChannelAdapter {

	private MessageListener messageListener;
	
	public IrcMessageListenerAdapter(MessageListener listener) {
		this.messageListener = listener;
	}
	
	@Override
	public void onMessage(ChannelEvent event) {
		String payload = (String) event.getValue();
		Message message = new Message(event.getOriginNick(),
				payload);
		this.messageListener.onMessageReceived(message);
	}
}
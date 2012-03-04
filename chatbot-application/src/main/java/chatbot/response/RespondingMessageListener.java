package chatbot.response;

import chatbot.client.Message;
import chatbot.client.MessageChannel;
import chatbot.client.MessageListener;
import chatbot.phrase.Phrase;
import chatbot.phrase.Phrases;

public class RespondingMessageListener implements MessageListener {

	private final ResponseStrategy responseStrategy;
	private final Phrases phrases;
	private final MessageChannel messageChannel;

	public RespondingMessageListener(ResponseStrategy responseStrategy,
			Phrases phrases, MessageChannel channel) {
		this.responseStrategy = responseStrategy;
		this.phrases = phrases;
		this.messageChannel = channel;
	}

	@Override
	public void onMessageReceived(Message message) {
		if (this.responseStrategy.shouldRespond(message)) {
			Phrase phrase = this.phrases.next();
			this.messageChannel.sendMessage(phrase.toString());
		} 
	}
}
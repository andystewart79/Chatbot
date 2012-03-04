package chatbot.response;

import chatbot.client.Message;
import chatbot.client.Message.User;

public abstract class AbstractResponseStrategy implements ResponseStrategy {

	private static final boolean DEFAULT_BOT_RESPOND_TO_ITSELF = false;
	
	private final String botUsername;
	private final boolean shouldBotRespondToItself;
	
	public AbstractResponseStrategy(String username) {
		this(username, DEFAULT_BOT_RESPOND_TO_ITSELF);
	}
	
	public AbstractResponseStrategy(String username, boolean respondToItself) {
		this.botUsername = username;
		this.shouldBotRespondToItself = respondToItself;
	}
	
	@Override
	public boolean shouldRespond(Message message) {
		if (message.fromBot(botUsername) == User.BOT) {
			return this.shouldBotRespondToItself;
		}
		
		return shouldRespondToOtherUserMessage(message);
	}
	
	protected abstract boolean shouldRespondToOtherUserMessage(Message message);
}
package chatbot.response;

import chatbot.client.Message;

public class RespondEveryNTimesStrategy extends AbstractResponseStrategy {

	private int currentCount;
	private final int respondOnNthTime;

	public RespondEveryNTimesStrategy(String botUsername, int respondOnNthTime) {
		super(botUsername);
		this.currentCount = 0;
		this.respondOnNthTime = respondOnNthTime;
	}

	@Override
	protected boolean shouldRespondToOtherUserMessage(Message message) {
		if (this.currentCount >= this.respondOnNthTime) {
			this.currentCount = 0;
		}
		return ++this.currentCount == this.respondOnNthTime;
	}
}
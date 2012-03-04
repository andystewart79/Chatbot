package chatbot.response;

import chatbot.client.Message;

public class NeverRespondStrategy implements ResponseStrategy {

	@Override
	public boolean shouldRespond(Message message) {
		return false;
	}
}
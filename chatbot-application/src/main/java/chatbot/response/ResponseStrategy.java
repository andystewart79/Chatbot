package chatbot.response;

import chatbot.client.Message;

public interface ResponseStrategy {

	boolean shouldRespond(Message message);
}
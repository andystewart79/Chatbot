package chatbot.client;

public interface MessageChannel {
	
	void registerListener(MessageListener listener);
	
	void sendMessage(String message);
}
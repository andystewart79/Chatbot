package chatbot.client;

public interface ClientConnection {

	void connect(ConnectionListener listener);
	
	MessageChannel channel(String name);
}
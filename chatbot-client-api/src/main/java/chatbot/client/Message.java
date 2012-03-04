package chatbot.client;

public class Message {

	public enum User {
		BOT, 
		OTHER;
	}
	
	private final String originatingUsername;
	private final String payload;
	
	public Message(String username, String payload) {
		this.originatingUsername = username;
		this.payload = payload;
	}
	
	public String getOriginatingUsername() {
		return originatingUsername;
	}
	
	public String getPayload() {
		return this.payload;
	}
	
	public User fromBot(String botUsername) {
		if (this.originatingUsername.equals(botUsername)) {
			return User.BOT;
		}
		return User.OTHER;
	}
	
	@Override
	public String toString() {
		return String.format("Msg [originatingUsername:%s, payload: %s]", this.originatingUsername, this.payload);
	}
}
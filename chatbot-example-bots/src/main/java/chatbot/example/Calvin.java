package chatbot.example;

import chatbot.adapter.xmpp.XmppClientConnection;
import chatbot.application.Chatbot;
import chatbot.client.ClientConnection;
import chatbot.phrase.PhraseLoader;
import chatbot.phrase.Phrases;
import chatbot.response.RespondToSpecificUsersStrategy;
import chatbot.response.ResponseStrategy;

public class Calvin {

	private static final String NICKNAME = "Calvin";

	private final Chatbot chatbot;

	public static void main(String[] args) throws Exception {
		ClientConnection clientConnection = new XmppClientConnection(
				"localhost", 5222, "chatbot.user", "chatbot.user", NICKNAME);
		Calvin calvin = new Calvin(clientConnection, "chatroom");
		calvin.start();
	}

	public Calvin(ClientConnection connection, String channel) {
		Phrases phrases = calvinPhrases();
		ResponseStrategy responseStrategy = new RespondToSpecificUsersStrategy(
				"Andy Stewart");
		this.chatbot = new Chatbot(connection, channel, responseStrategy,
				phrases);
	}

	public void start() {
		this.chatbot.connect();
	}

	private static Phrases calvinPhrases() {
		return PhraseLoader.load("You go sit time out",
				"I no go naughty corner",
				"Daddy daddy daddy daddy daddy, look at me",
				"My best friend mummy",
				"Zach go like this 'WWWWWAAAAAAAHHHHHHHH!!'",
				"Daddy you smelly, you go shower", "I love toast",
				"You have to share", "I no like it", "I can't complain",
				"I want BBBBIIIIGGGGGGG one", "Mummy can I have ice cream?",
				"Later hair, later hair");
	}
}
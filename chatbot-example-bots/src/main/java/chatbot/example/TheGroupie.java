package chatbot.example;
import chatbot.adapter.xmpp.XmppClientConnection;
import chatbot.application.Chatbot;
import chatbot.client.ClientConnection;
import chatbot.phrase.PhraseLoader;
import chatbot.phrase.Phrases;
import chatbot.response.RespondEveryNTimesStrategy;
import chatbot.response.ResponseStrategy;

public class TheGroupie {

	private static final String NICKNAME = "TheGroupie";
	
	private final Chatbot chatbot;
	
	public static void main(String[] args) throws Exception {
		ClientConnection clientConnection = new XmppClientConnection(
				"localhost", 5222, "chatbot.user", "chatbot.user", NICKNAME);
		TheGroupie theGroupie = new TheGroupie(clientConnection, "chatroom");
		theGroupie.start();
	}
	
	public TheGroupie(ClientConnection connection, String channel) {
		Phrases phrases = groupiePhrases();
		ResponseStrategy responseStrategy = new RespondEveryNTimesStrategy(
				NICKNAME, 5);
		this.chatbot = new Chatbot(connection, channel,
				responseStrategy, phrases);
	}
	
	public void start() {
		this.chatbot.connect();
	}

	private static Phrases groupiePhrases() {
		return PhraseLoader.load("+1", "You are so right",
				"I couldn't agree more", "Absolutely", "Very insightful");
	}
}
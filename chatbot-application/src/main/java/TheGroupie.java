import java.util.ArrayList;
import java.util.List;

import chatbot.phrase.PhraseLoader;
import chatbot.phrase.Phrases;
import chatbot.response.RespondToSpecificUsersStrategy;
import chatbot.response.ResponseStrategy;

public class TheGroupie {

	public static void main(String[] args) {
		String username = "TheGroupie";
		Phrases phrases = groupiePhrases();
		ResponseStrategy responseStrategy = new RespondToSpecificUsersStrategy(
				"Gav");
		// new IrcChatbot(username, phrases, responseStrategy);
	}

	private static Phrases groupiePhrases() {
		return PhraseLoader.load("+1", "You are so right",
				"I couldn't agree more", "Absolutely", "Very insightful");
	}
}
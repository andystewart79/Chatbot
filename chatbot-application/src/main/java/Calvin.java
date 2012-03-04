import chatbot.phrase.PhraseLoader;
import chatbot.phrase.Phrases;
import chatbot.response.RespondToSpecificUsersStrategy;
import chatbot.response.ResponseStrategy;

public class Calvin {

	public static void main(String[] args) {
		String username = "Calvin";
		Phrases phrases = calvinPhrases();
		ResponseStrategy responseStrategy = new RespondToSpecificUsersStrategy(
				"Toby");
		// new IrcChatbot(username, phrases, responseStrategy);
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
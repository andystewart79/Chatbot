package chatbot.phrase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhraseLoader {
	private PhraseLoader() {
		super();
	}
	
	public static Phrases load(String... phrases) {
		return load(Arrays.asList(phrases));
	}
	
	public static Phrases load(List<String> rawPhrases) {
		List<Phrase> phrases = new ArrayList<Phrase>();
		for (String phrase : rawPhrases) {
			phrases.add(new Phrase(phrase));
		}
		return new Phrases(phrases);
	}
}
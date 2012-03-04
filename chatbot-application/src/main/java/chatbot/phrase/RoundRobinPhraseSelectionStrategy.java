package chatbot.phrase;


public class RoundRobinPhraseSelectionStrategy implements
		PhraseSelectionStrategy {

	private int currentIndex;
	
	public RoundRobinPhraseSelectionStrategy() {
		this.currentIndex = 0;
	}
	
	@Override
	public Phrase next(Phrases phrases) {
		if (this.currentIndex >= phrases.size()) {
			this.currentIndex = 0;
		}
		return phrases.getPhraseAt(this.currentIndex++);
	}
}
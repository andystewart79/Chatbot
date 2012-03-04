package chatbot.phrase;

import java.util.List;

public class Phrases {

	private final List<Phrase> phrases;

	private PhraseSelectionStrategy selectionStrategy = new RoundRobinPhraseSelectionStrategy();
	
	public Phrases(List<Phrase> phrases) {
		this.phrases = phrases;
	}
	
	public int size() {
		return this.phrases.size();
	}
	
	public Phrase getPhraseAt(int index) {
		return this.phrases.get(index);
	}
	
	public Phrases usingSelectionStrategy(PhraseSelectionStrategy strategy) {
		this.selectionStrategy = strategy;
		return this;
	}
	
	public Phrase next() {
		return this.selectionStrategy.next(this);
	}
}
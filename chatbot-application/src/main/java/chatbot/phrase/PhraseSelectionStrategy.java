package chatbot.phrase;

public interface PhraseSelectionStrategy {

	Phrase next(Phrases phrases);
}
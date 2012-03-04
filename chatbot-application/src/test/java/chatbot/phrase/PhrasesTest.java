package chatbot.phrase;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import chatbot.phrase.PhraseLoader;
import chatbot.phrase.Phrases;

public class PhrasesTest {

	private static final String PHRASE_1 = "Hello";
	private static final String PHRASE_2 = "World";
	
	private Phrases phrases;
	
	@Before
	public void before() {
		this.phrases = PhraseLoader.load(PHRASE_1, PHRASE_2);
	}

	@Test
	public void testNumberOfPhrasesIsCorrect() {
		assertThat(this.phrases.size(), is(2));
	}
	
	@Test
	public void testOrderingOfPhrasesIsCorrect() {
		assertThat(this.phrases.getPhraseAt(0).toString(), is(PHRASE_1));
		assertThat(this.phrases.getPhraseAt(1).toString(), is(PHRASE_2));
	}
}
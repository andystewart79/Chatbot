package chatbot.phrase;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class RoundRobinPhraseSelectionStrategyTest {

	private static final String PHRASE_1 = "Hello";
	private static final String PHRASE_2 = "World";
	
	private Phrases phrases;
	
	@Before
	public void before() {
		this.phrases = PhraseLoader.load(PHRASE_1, PHRASE_2);
	}
	
    @Test
    public void testRoundRobinStrategyWorksCorrectly() {
        assertThat(nextPhrase(), is(PHRASE_1));
        assertThat(nextPhrase(), is(PHRASE_2));
        assertThat(nextPhrase(), is(PHRASE_1));
        assertThat(nextPhrase(), is(PHRASE_2));
    }
    
	private String nextPhrase() {
		return this.phrases.next().toString();
	}
}
package chatbot.response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import chatbot.client.Message;

public class RespondEveryNTimesStrategyTest {

	private static final String BOT_NAME = "Myself";
	private static final String OTHER_USER = "Other";
	private static final int NUMBER_TO_RESPOND_ON = 5;
	private static final String PAYLOAD = "";

	private ResponseStrategy strategy;

	@Before
	public void before() {
		this.strategy = new RespondEveryNTimesStrategy(BOT_NAME,
				NUMBER_TO_RESPOND_ON);
	}

	@Test
	public void testShouldNotRespondIfNumberOfMessagesHasNotBeenReached() {
		for (int i = 0, max = NUMBER_TO_RESPOND_ON - 1; i < max; i++) {
			assertThat(this.strategy.shouldRespond(messageFrom(OTHER_USER)),
					is(false));
		}
	}

	@Test
	public void testShouldRespondIfNumberOfMessagesHasBeenReached() {
		testShouldNotRespondIfNumberOfMessagesHasNotBeenReached();
		assertThat(this.strategy.shouldRespond(messageFrom(OTHER_USER)),
				is(true));
	}

	@Test
	public void testShouldNotRespondToOwnMessages() {
		for (int i = 0, max = NUMBER_TO_RESPOND_ON; i < max; i++) {
			assertThat(this.strategy.shouldRespond(messageFrom(BOT_NAME)),
					is(false));
		}
	}

	private Message messageFrom(String username) {
		return new Message(username, PAYLOAD);
	}
}

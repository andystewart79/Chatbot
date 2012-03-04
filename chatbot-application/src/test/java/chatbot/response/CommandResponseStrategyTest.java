package chatbot.response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import chatbot.client.Message;

public class CommandResponseStrategyTest {

	private static final String OTHER_USERNAME = "OtherUser";
	
	private static final String BOT_NAME = "Bot";
	private static final String COMMAND_PREFIX = "!" + BOT_NAME;
	
	private ResponseStrategy strategy;
	
	@Before
	public void before() {
		this.strategy = new CommandResponseStrategy(BOT_NAME);
	}
	
	@Test
	public void testShouldRespondToExactCommand() {
		assertThat(shouldRespondToMessage(COMMAND_PREFIX), is(true));
	}
	
	@Test
	public void testShouldNotRespondToCommandWithTrailingText() {
		assertThat(shouldRespondToMessage(COMMAND_PREFIX + COMMAND_PREFIX), is(false));
	}
	
	@Test
	public void testShouldNotRespondToBotNameAlone() {
		assertThat(shouldRespondToMessage(BOT_NAME), is(false));
	}
	
	@Test
	public void testShouldNotRespondWithDifferentCase() {
		assertThat(shouldRespondToMessage(COMMAND_PREFIX.toUpperCase()), is(false));
	}
	
	private boolean shouldRespondToMessage(String payload) {
		Message message = new Message(OTHER_USERNAME, payload);
		return this.strategy.shouldRespond(message);
	}
}
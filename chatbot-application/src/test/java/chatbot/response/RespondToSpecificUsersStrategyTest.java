package chatbot.response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import chatbot.client.Message;

public class RespondToSpecificUsersStrategyTest {

	private static final String PAYLOAD = "";
	
	private static final String USER_TO_RESPOND_TO_1 = "First User";
	private static final String USER_TO_RESPOND_TO_2 = "Second User";
	private static final String USER_NOT_TO_RESPOND_TO = "Ignore Me";
	
	private ResponseStrategy strategy;
	
	@Before
	public void before() {
		this.strategy = new RespondToSpecificUsersStrategy(USER_TO_RESPOND_TO_1, USER_TO_RESPOND_TO_2);
	}
	
	@Test
	public void testShouldRespondToFirstUserInTheList() {
		assertThat(shouldRespondToUser(USER_TO_RESPOND_TO_1), is(true));
	}
	
	@Test
	public void testShouldRespondToSecondUserInTheList() {
		assertThat(shouldRespondToUser(USER_TO_RESPOND_TO_2), is(true));
	}
	
	@Test
	public void testShouldNotRespondToUsersNotInTheList() {
		assertThat(shouldRespondToUser(USER_NOT_TO_RESPOND_TO), is(false));
	}
	
	private boolean shouldRespondToUser(String user) {
		return this.strategy.shouldRespond(messageFrom(user));
	}
	
	private Message messageFrom(String user) {
		return new Message(user, PAYLOAD);
	}
}
package chatbot.application;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import chatbot.phrase.PhraseLoader;
import chatbot.phrase.Phrases;
import chatbot.response.RespondToSpecificUsersStrategy;
import chatbot.response.ResponseStrategy;

public class ChatbotTest {

	private static final String USER_TO_RESPOND_TO = "Talk to me";
	private static final String USER_TO_IGNORE = "Ignore me";
	private static final String CHANNEL_NAME = "developers";
	
	private static final String USER_MESSAGE = "This is a message from someone";
	private static final String PHRASE = "This ain't a love song";
	
	private StubClientConnection clientConnection;
	private Chatbot connectedChatbot;
	
	@Before
	public void before() {
		Phrases phrases = PhraseLoader.load(PHRASE);
		ResponseStrategy responseStrategy = new RespondToSpecificUsersStrategy(USER_TO_RESPOND_TO);
		
		this.clientConnection = new StubClientConnection();
		this.connectedChatbot = new Chatbot(this.clientConnection, CHANNEL_NAME, responseStrategy, phrases);
		this.connectedChatbot.connect();
	}
	
	@Test
	public void testChannelIsNotCreatedIfConnectHasNotBeenCalled() {
		StubClientConnection connection = new StubClientConnection();
		new Chatbot(connection, CHANNEL_NAME, null, null);
		assertThat(connection.hasChannel(CHANNEL_NAME), is(false));
	}
	
	@Test
	public void testChannelIsCreatedIfChatbotHasConnected() {
		assertThat(this.clientConnection.hasChannel(CHANNEL_NAME), is(true));
	}
	
	@Test
	public void testChatbotUsesStrategyToDetermineNotToRespondToIgnoredUser() {
		simulateMessageFromUser(USER_TO_IGNORE);
		assertThat(responses().size(), is(0));
	}
	
	@Test
	public void testChatbotRespondsToMessageFromUser() {
		simulateMessageFromUser(USER_TO_RESPOND_TO);
		List<String> responses = responses();
		assertThat(responses.size(), is(1));
		assertThat(responses.get(0), is(PHRASE));
	}
	
	private List<String> responses() {
		return this.clientConnection.responses(CHANNEL_NAME);
	}
	
	private void simulateMessageFromUser(String user) {
		this.clientConnection.simulateClientMessage(CHANNEL_NAME, user, USER_MESSAGE);
	}
}
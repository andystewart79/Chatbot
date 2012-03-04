package chatbot.response;

import java.util.Arrays;
import java.util.List;

import chatbot.client.Message;

public class RespondToSpecificUsersStrategy implements ResponseStrategy{

	private final List<String> usersToRespondTo;
	
	public RespondToSpecificUsersStrategy(String... usersToRespondTo) {
		this.usersToRespondTo = Arrays.asList(usersToRespondTo);
	}
	
	@Override
	public boolean shouldRespond(Message message) {
		return this.usersToRespondTo.contains(message.getOriginatingUsername());
	}
}
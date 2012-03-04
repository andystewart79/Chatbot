package chatbot.response;

import chatbot.client.Message;

public class CommandResponseStrategy implements ResponseStrategy {

	private final String botUsername;

	public CommandResponseStrategy(String username) {
		this.botUsername = username;
	}

	@Override
	public boolean shouldRespond(Message message) {
		return isCommand(message.getPayload());
	}

	private boolean isCommand(String payload) {
		return payload.equals(commandPrefix());
	}

	private String commandPrefix() {
		return String.format("!%s", this.botUsername);
	}
}
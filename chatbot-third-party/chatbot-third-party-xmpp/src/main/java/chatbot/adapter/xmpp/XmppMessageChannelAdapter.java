package chatbot.adapter.xmpp;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import chatbot.client.MessageChannel;
import chatbot.client.MessageListener;

public class XmppMessageChannelAdapter implements MessageChannel {

	private final MultiUserChat chatRoom;
	
	public XmppMessageChannelAdapter(MultiUserChat chat) {
		this.chatRoom = chat;
	}
	
	@Override
	public void registerListener(MessageListener listener) {
		XmppMessageListenerAdapter listenerAdapter = new XmppMessageListenerAdapter(
				listener);
		this.chatRoom.addMessageListener(listenerAdapter);
	}

	@Override
	public void sendMessage(String message) {
		try {
			this.chatRoom.sendMessage(message);
		} catch (XMPPException e) {
			throw new IllegalStateException("Unable to send message", e);
		}
	}
}
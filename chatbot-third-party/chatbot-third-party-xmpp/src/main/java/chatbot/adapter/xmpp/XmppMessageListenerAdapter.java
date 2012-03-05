package chatbot.adapter.xmpp;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import chatbot.client.MessageListener;

public class XmppMessageListenerAdapter implements PacketListener {

	private final MessageListener messageListener;
	
	public XmppMessageListenerAdapter(MessageListener listener) {
		this.messageListener = listener;
	}

	@Override
	public void processPacket(Packet packet) {
		Message xmppMessage = (Message)packet;
		String from = xmppMessage.getFrom();
		String nickname = from.split("/")[1];
		chatbot.client.Message chatbotMessage = new chatbot.client.Message(nickname, xmppMessage.getBody());
		this.messageListener.onMessageReceived(chatbotMessage);
	}
}
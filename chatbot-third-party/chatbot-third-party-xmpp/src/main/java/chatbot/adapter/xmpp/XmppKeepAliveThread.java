package chatbot.adapter.xmpp;

public class XmppKeepAliveThread implements Runnable {

	private final Thread thread;

	public XmppKeepAliveThread() {
		this.thread = new Thread(this);
	}
	
	public void start() {
		this.thread.start();
	}

	@Override
	public void run() {
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}
}
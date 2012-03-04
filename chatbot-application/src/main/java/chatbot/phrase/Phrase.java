package chatbot.phrase;

public class Phrase {

	private final String template;
	
	public Phrase(String template) {
		this.template = template;
	}
	
	@Override
	public String toString() {
		return this.template;
	}
}
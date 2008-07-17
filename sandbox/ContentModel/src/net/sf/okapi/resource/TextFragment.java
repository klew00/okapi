package net.sf.okapi.resource;

public class TextFragment implements IContent{
	
	// TODO we could expose a StringBuilder like interface for TextFragments to make them more efficient
	// and easier to use.
	// we can't subclass StringBuilder directly as it is declared final..
	private StringBuilder builder;
	
	private int id;

	public TextFragment() {
		builder = new StringBuilder();
	}

	public TextFragment(String text) {
		builder = new StringBuilder(text);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TextFragment(CharSequence text) {
		builder = new StringBuilder(text);
	}
	
	public void append(TextFragment other){
		builder.append(other.toString());
	}
	
	public String getEquivText() {
		return builder.toString();
	}

	@Override
	public String toString() {
		return builder.toString();
	}
	
	public TextFragment splitAt(int charIndex){
		TextFragment other = new TextFragment(builder.substring(charIndex));
		builder.delete(charIndex, builder.length());
		return other;
	}
	
}

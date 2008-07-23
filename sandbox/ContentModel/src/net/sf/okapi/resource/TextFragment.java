package net.sf.okapi.resource;

public class TextFragment implements IContent, CharSequence, Appendable{
	
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
		builder.append(other.builder);
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

	public char charAt(int index) {
		return builder.charAt(index);
	}

	public int length() {
		return builder.length();
	}

	public TextFragment subSequence(int start, int end) {
		TextFragment other = new TextFragment(builder.subSequence(start, end));
		return other;
	}

	public TextFragment append(CharSequence csq){
		builder.append(csq);
		return this;
	}

	public TextFragment append(char c){
		builder.append(c);
		return this;
	}

	public TextFragment append(CharSequence csq, int start, int end){
		builder.append(csq, start, end);
		return this;
	}
	
}

package net.sf.okapi.common.resource;

public class TextFragment implements IFragment {

	public StringBuilder     text;

	public TextFragment () {
		text = new StringBuilder();
	}
	
	@Override
	public String toString () {
		return text.toString();
	}
	
	public boolean isText () {
		return true;
	}
}

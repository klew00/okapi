package net.sf.okapi.common.resource;

import net.sf.okapi.common.Util;

public class TextFragment implements IFragment {

	public StringBuilder     text;

	public TextFragment () {
		text = new StringBuilder();
	}
	
	public TextFragment (String text) {
		this.text = new StringBuilder(text);
	}
	
	@Override
	public String toString () {
		return text.toString();
	}

	@Override
	public IFragment clone () {
		return new TextFragment(text.toString());
	}
	
	public String toXML (IPart parent) {
		// parent is not used here
		return Util.escapeToXML(text.toString(), 0, false);
	}

	public boolean isText () {
		return true;
	}
}

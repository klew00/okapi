package net.sf.okapi.common.resource;

public class LocaleData extends LocaleProperties {

	TextContainer       container;
	
	public LocaleData (TextUnit parent) {
		container = new TextContainer(parent);
	}

	@Override
	public String toString () {
		return container.toString();
	}
	
	public TextContainer getContent () {
		return container;
	}
	
}


package net.sf.okapi.common.encoder;

public class CDATAEncoder extends DefaultEncoder {

	public CDATAEncoder(String encoding, String lineBreak) {
		super();
		setOptions(null, encoding, lineBreak);
	}
	
	public CDATAEncoder() {
		super();
	}

	@Override
	public String encode(String text, int context) {
		return context == 0 ? 
				String.format("<![CDATA[%s]]>", super.encode(text, context)) :					
				super.encode(text, context);
	}
}

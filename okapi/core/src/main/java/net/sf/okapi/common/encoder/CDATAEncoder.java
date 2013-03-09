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
	public String encode(String text, EncoderContext context) {
		return context == EncoderContext.TEXT ? 
				String.format("<![CDATA[%s]]>", super.encode(text, context)) :					
				super.encode(text, context);
	}
}

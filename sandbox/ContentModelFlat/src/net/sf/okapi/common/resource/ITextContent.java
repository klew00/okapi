package net.sf.okapi.common.resource;

import java.util.List;


public interface ITextContent {

	public static final int MARKER_OPENING  = 0xE101;
	public static final int MARKER_CLOSING  = 0xE102;
	public static final int MARKER_ISOLATED = 0xE103;
	public static final int CHARBASE        = 0xE110;

	public static final String SFMARKER_START = "{@#$";
	public static final String SFMARKER_END   = "}";
	
	public static enum TagType {
		OPENING,
		CLOSING,
		PLACEHOLDER
	};

	
	public String toString ();

	public TextUnit getParent ();
	
	public void setParent (TextUnit value);
	
	public String getID ();
	
	public void setID (String value);
	
	public String getCodedText ();
	
	public String getCodedText (int start, int end);
	
	public List<Code> getCodes ();
	
	public List<Code> getCodes (int start, int end);

	public void setCodedText (String codedText);

	public void setCodedText (String codedText, List<Code> codes);
	
	public void remove (int start, int end);
	
	public void append (char value);
	
	public void append (String text);
	
	public void append (ITextContent content);
	
	public Code append (TagType tagType, String type, String data);
	
	public void append (int markerType, String layerType);
	
	public void clear ();

	public boolean isEmpty ();

}

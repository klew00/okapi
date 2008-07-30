package net.sf.okapi.common.resource3.string;

import java.util.List;

import net.sf.okapi.common.resource3.Code;

public interface IContent {

	public static final int CODE_OPENING    = 0xE101;
	public static final int CODE_CLOSING    = 0xE102;
	public static final int CODE_ISOLATED   = 0xE103;
	public static final int SEG_OPENING     = 0xE104;
	public static final int SEG_CLOSING     = 0xE105;
	public static final int CHARBASE        = 0xE110;
	
	public int getID ();
	
	public void setID (int id);
	
	public String getEquivText ();

	public String getCodedText ();
	
	public String getCodedText (int start, int end);
	
	public List<Code> getCodes ();
	
	public List<Code> getCodes (int start, int end);

	public void setCodedText (String codedText);

	public void setCodedText (String codedText, List<Code> codes);
	
	public void removeContent (int start, int end);
	
	public void append (CharSequence sequence);
	
	public void append (char value);
	
	public void append (String text);
	
	public void append (IContent content);
	
	public void append (int codeType, String label, String data);
	
	public void clear ();

	public boolean isEmpty ();

	public boolean isSegment ();
	
	public int getLength ();

	// Maybe specific for flat model
	public StringRootContainer getParent ();
	
	// Maybe specific for flat model
	public void setParent (StringRootContainer parent);
	
	// Maybe specific for flat model 
	public void balanceCodes ();

}

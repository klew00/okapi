package net.sf.okapi.common.resource3.string;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.resource3.Code;

public class Content implements IContent {
	
	protected StringBuilder       text;
	protected ArrayList<Code>     codes;
	protected boolean             isSegment;
	protected StringRootContainer parent;
	protected boolean             isBalanced = true;
	protected int                 id;
	

	static char toChar (int index) {
		return (char)(index+CHARBASE);
	}

	static int toIndex (char index) {
		return ((int)index)-CHARBASE;
	}

	/**
	 * Add a given value to the index value of every code in the coded text.
	 * @param codedText The coded text to change.
	 * @param addition The value to add.
	 */
	public static String updateCodeIndices (String codedText,
		int addition)
	{
		StringBuilder buffer = new StringBuilder(codedText);
		for ( int i=0; i<buffer.length(); i++ ) {
			switch ( codedText.codePointAt(i) ) {
			case CODE_OPENING:
			case CODE_CLOSING:
			case CODE_ISOLATED:
				int n = Content.toIndex(buffer.charAt(i+1));
				buffer.setCharAt(++i, Content.toChar(n+addition));
				break;
			case SEG_OPENING:
			case SEG_CLOSING:
				i++;
				break;
			}
		}
		return buffer.toString();
	}

	protected Content (StringRootContainer parent,
		boolean isSegment)
	{
		this.parent = parent;
		this.isSegment = isSegment;
	}
	
	protected Content (StringRootContainer parent,
		CharSequence sequence)
	{
		this.parent = parent;
		text = new StringBuilder(sequence);
	}
	
	protected Content (StringRootContainer parent,
		char value)
	{
		this.parent = parent;
		// Add "" or the character is not taken into account (bug?)
		text = new StringBuilder(""+value);
	}
	
	protected Content (StringRootContainer parent,
		String text)
	{
		this.parent = parent;
		this.text = new StringBuilder(text);
	}
	
	public void append (CharSequence sequence) {
		if ( text == null ) text = new StringBuilder(sequence);
		else text.append(sequence);
	}

	public void append (char value) {
		if ( text == null ) text = new StringBuilder(""+value);
		else text.append(value);
	}

	public void append (String text) {
		if ( this.text == null ) this.text = new StringBuilder(text);
		else this.text.append(text);
	}
	
	public void append (IContent content) {
		// Prepare the text
		if ( text == null ) text = new StringBuilder();
		// Check the codes
		List<Code> list = content.getCodes();
		if ( list.size() == 0 ) {
			text.append(content.getCodedText());
			return; // No code to add, we're done.
		}
		else {
			if ( codes == null ) codes = new ArrayList<Code>();
			text.append(updateCodeIndices(content.getCodedText(), codes.size()));
			codes.addAll(list); // Must be copies of the codes!
		}
	}

	public void append (int codeType,
		String label,
		String data)
	{
		if (( codeType == SEG_OPENING ) || ( codeType == SEG_CLOSING ))
			throw new RuntimeException("Cannot set segments this way.");
		if ( codes == null ) codes = new ArrayList<Code>();
		append(""+((char)codeType)+toChar(codes.size()));
		codes.add(new Code(codeType, label, data));
		if ( codeType != CODE_ISOLATED ) isBalanced = false;
		if ( codeType != CODE_CLOSING ) {
			codes.get(codes.size()-1).setID(parent.setLastCodeID(parent.getLastCodeID()+1));
		}
	}

	public void clear() {
		text = null;
		codes = null;
		isBalanced = true;
		// isSegment stays unchanged
	}

	public String getCodedText () {
		if ( text == null ) return "";
		else return text.toString();
	}

	public String getCodedText (int start,
		int end)
	{
		return text.substring(start, end);
	}

	public List<Code> getCodes () {
		//TODO: should it be copy or self?
		if ( codes == null ) codes = new ArrayList<Code>();
		if ( !isBalanced ) balanceCodes();
		return codes;
	}

	public List<Code> getCodes (int start,
		int end)
	{
		List<Code> list = new ArrayList<Code>();
		for ( int i=start; i<end; i++ ) {
			switch ( text.codePointAt(i) ) {
			case CODE_OPENING:
			case CODE_CLOSING:
			case CODE_ISOLATED:
			case SEG_OPENING:
			case SEG_CLOSING:
				//TODO: Do we need to clone the item copied???
				list.add(codes.get(toIndex(text.charAt(++i))));
				break;
			}
		}
		return list;
	}

	public String getEquivText () {
		if ( text == null ) return "";
		if (( codes == null ) || ( codes.size() == 0 )) return text.toString();
		if ( !isBalanced ) balanceCodes();
		StringBuilder tmp = new StringBuilder();
		Code code;
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.codePointAt(i) ) {
			//TODO: remove test markup when done
			case CODE_OPENING:
				code = codes.get(toIndex(text.charAt(i+1)));
				tmp.append(String.format("[%d=%s]", code.getID(), code.getData()));
				i++; // Skip index
				break;
			case CODE_CLOSING:
				code = codes.get(toIndex(text.charAt(i+1)));
				tmp.append(String.format("[/%d=%s]", code.getID(), code.getData()));
				i++; // Skip index
				break;
			case CODE_ISOLATED:
				code = codes.get(toIndex(text.charAt(i+1)));
				tmp.append(String.format("[%d=%s/]", code.getID(), code.getData()));
				i++; // Skip index
				break;
			case SEG_OPENING:
			case SEG_CLOSING:
				i++; // We should not have any here, but just in case
				break;
			default:
				tmp.append(text.charAt(i));
				break;
			}
		}
		return tmp.toString();
	}

	public int getID () {
		return id;
	}

	public boolean isEmpty () {
		
		return false;
	}

	public void setCodedText (String codedText) {
		setCodedText(codedText, codes);
		// TODO: validation
	}

	public void setCodedText (String codedText,
		List<Code> codes)
	{
		text = new StringBuilder(codedText);
		this.codes = new ArrayList<Code>(codes);
		isBalanced = false; // Just to make sure it is checked
		// TODO: validation
	}

	public void removeContent (int start, int end) {
		//TODO: should we check if start/end points to code indices?
		for ( int i=start; i<end; i++ ) {
			switch ( text.codePointAt(i) ) {
			case CODE_OPENING:
			case CODE_CLOSING:
			case CODE_ISOLATED:
			case SEG_OPENING:
			case SEG_CLOSING:
				codes.remove(toIndex(text.charAt(++i)));
				break;
			}
		}
		text.replace(start, end, "");
	}

	public void setID (int id) {
		this.id = id;
	}
	
	public boolean isSegment () {
		return isSegment;
	}
	
	public int getLength () {
		if ( text == null ) return 0;
		else return getEquivText().length();
	}

	public StringRootContainer getParent () {
		return parent;
	}
	
	public void setParent (StringRootContainer parent) {
		if ( parent == null )
			throw new IllegalArgumentException("Cannot set a parent to null.");
		this.parent = parent;
	}
	
	public void balanceCodes () {
		if ( codes == null ) return;
		Code code;
		for ( int i=0; i<codes.size(); i++ ) {
			switch ( codes.get(i).getType() ) {
			case Content.CODE_OPENING:
				code = codes.get(i);
				boolean found = false;
				int stack = 1;
				//TODO: need stack!!!
				for ( int j=i+1; j<codes.size(); j++ ) {
					if ( codes.get(j).getLabel().equals(code.getLabel()) ) {
						if ( codes.get(j).getType() == Content.CODE_OPENING ) {
							stack++;
						}
						else if ( codes.get(j).getType() == Content.CODE_CLOSING ) {
							if ( --stack == 0 ) {
								codes.get(j).setID(code.getID());
								found = true;
								break;
							}
						}
					}
				}
				if ( !found ) {
					changeCodeType(i, Content.CODE_ISOLATED);
				}
				break;
			case Content.CODE_CLOSING:
				code = codes.get(i);
				if ( code.getID() == -1 ) {
					changeCodeType(i, Content.CODE_ISOLATED);
					code.setID(parent.setLastCodeID(parent.getLastCodeID()+1));
				}
				break;
			}
		}
		isBalanced = true;
	}
	
	private void changeCodeType (int index,
		int newType )
	{
		// Update the code
		codes.get(index).setType(newType);

		// Update the coded text marker
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.codePointAt(i) ) {
			case CODE_OPENING:
			case CODE_CLOSING:
			case CODE_ISOLATED:
			case SEG_OPENING:
			case SEG_CLOSING:
				if ( toIndex(text.charAt(++i)) == index ) {
					text.setCharAt(i-1, toChar(newType));
					return; // Done
				}
			}
		}
	}

}

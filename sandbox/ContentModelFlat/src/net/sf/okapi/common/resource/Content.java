package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.List;

public class Content implements IContent {
	
	protected StringBuilder       text;
	protected ArrayList<Code>     codes;
	protected boolean             isSegment;
	protected IContainer          parent;
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
			}
		}
		return buffer.toString();
	}

	protected Content (IContainer parent,
		boolean isSegment)
	{
		this.parent = parent;
		this.isSegment = isSegment;
	}
	
	protected Content (IContainer parent,
		CharSequence sequence)
	{
		this.parent = parent;
		text = new StringBuilder(sequence);
	}
	
	protected Content (IContainer parent,
		char value)
	{
		this.parent = parent;
		// Add "" or the character is not taken into account (bug?)
		text = new StringBuilder(""+value);
	}
	
	protected Content (IContainer parent,
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
		if ( codes == null ) codes = new ArrayList<Code>();
		append(""+((char)codeType)+toChar(codes.size()));
		codes.add(new Code(codeType, label, data));
		if ( codeType != CODE_ISOLATED ) isBalanced = false;
		if ( codeType != CODE_CLOSING ) {
			codes.get(codes.size()-1).id = parent.setLastCodeID(parent.getLastCodeID()+1);
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
			case CODE_OPENING:
			case CODE_CLOSING:
			case CODE_ISOLATED:
				code = codes.get(toIndex(text.charAt(i+1)));
				tmp.append(String.format("{(%d)=%s}", code.id, code.data));
				i++; // Skip index
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

	public IContainer getParent () {
		return parent;
	}
	
	public void setParent (IContainer parent) {
		if ( parent == null )
			throw new IllegalArgumentException("Cannot set a parent to null.");
		this.parent = parent;
	}
	
	public void balanceCodes () {
		if ( codes == null ) return;
		Code code;
		for ( int i=0; i<codes.size(); i++ ) {
			switch ( codes.get(i).type ) {
			case IContainer.CODE_OPENING:
				code = codes.get(i);
				boolean found = false;
				int stack = 1;
				//TODO: need stack!!!
				for ( int j=i+1; j<codes.size(); j++ ) {
					if ( codes.get(j).label.equals(code.label) ) {
						if ( codes.get(j).type == IContainer.CODE_OPENING ) {
							stack++;
						}
						else if ( codes.get(j).type == IContainer.CODE_CLOSING ) {
							if ( --stack == 0 ) {
								codes.get(j).id = code.id;
								found = true;
								break;
							}
						}
					}
				}
				if ( !found ) {
					changeCodeType(i, IContainer.CODE_ISOLATED);
				}
				break;
			case IContainer.CODE_CLOSING:
				code = codes.get(i);
				if ( code.id == -1 ) {
					changeCodeType(i, IContainer.CODE_ISOLATED);
					code.id = parent.setLastCodeID(parent.getLastCodeID()+1);
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
		codes.get(index).type = newType;;

		// Update the coded text marker
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.codePointAt(i) ) {
			case CODE_OPENING:
			case CODE_CLOSING:
			case CODE_ISOLATED:
				if ( toIndex(text.charAt(++i)) == index ) {
					text.setCharAt(i-1, toChar(newType));
					return; // Done
				}
			}
		}
	}

}

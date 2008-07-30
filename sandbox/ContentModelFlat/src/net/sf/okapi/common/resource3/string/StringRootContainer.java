package net.sf.okapi.common.resource3.string;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import net.sf.okapi.common.resource3.Code;
import net.sf.okapi.common.resource3.IRootContainer;
import net.sf.okapi.common.resource3.tree.IContainer;
import net.sf.okapi.common.resource3.tree.MarkupContainer;
import net.sf.okapi.common.resource3.tree.SegmentContainer;
import net.sf.okapi.common.resource3.tree.TextFragment;
import net.sf.okapi.common.resource3.tree.TreeRootContainer;

public class StringRootContainer extends ArrayList<IContent> implements IRootContainer, IContent {

	private static final long     serialVersionUID = 1L;
	
	private int                   lastID;
	private int                   id;


	public IContent addContent (boolean isSegment) {
		add(new Content(this, isSegment));
		return get(size()-1);
	}
	
	public IContent addContent (boolean isSegment,
		String text)
	{
		Content tmp = new Content(this, text);
		tmp.isSegment = isSegment;
		add(tmp);
		return get(size()-1);
	}
	
	public int getLastCodeID () {
		return lastID;
	}
	
	public int setLastCodeID (int value) {
		lastID = value;
		return lastID;
	}
	
	public void joinAll () {
		if ( size() < 2 ) return;
		// Create temporary holders for text and codes
		List<Code> newCodes = new ArrayList<Code>();
		StringBuilder newText = new StringBuilder();
		// Gather the segments data
		int addition = 0;
		for ( IContent part : this ) {
			// Fix the code indices and add the coded text 
			newText.append(Content.updateCodeIndices(part.getCodedText(), addition));
			// Add codes (no need for cloning here)
			newCodes.addAll(part.getCodes());
			addition = newCodes.size();
		}
		clear();
		// Set the new data
		setCodedText(newText.toString(), newCodes);
	}
	
	public void joinSegments (int first, int last) {
		first = getPartIndexFromSegmentIndex(first);
		last = getPartIndexFromSegmentIndex(last);
		if (( last < first ) || ( first < 0 ) || ( last > size()-1 ))
			throw new IllegalArgumentException("Invalid first or last index.");
		if ( first == last ) return; // Nothing to do
		
		//TODO: a faster way would be to start concatenation on get(first)
		// Create temporary holders for text and codes
		StringBuilder newText = new StringBuilder();
		List<Code> newCodes = new ArrayList<Code>();
		
		// Join the segments and anything in-between
		int addition = 0;
		for ( int i=first; i<=last; i++ ) {
			// Fix the code indices and add the coded text 
			newText.append(Content.updateCodeIndices(get(first).getCodedText(), addition));
			// Add codes (no need for cloning here)
			newCodes.addAll(get(first).getCodes());
			addition = newCodes.size();
			remove(first);
		}
		// Create the new content and add it
		IContent newSeg = new Content(this, true);
		newSeg.setCodedText(newText.toString(), newCodes);
		add(first, newSeg);
	}

	public void createSegment (int start, int end) {
		int[] startSeg = getIndicesFromCodedTextPosition(start, true);
		int[] endSeg = getIndicesFromCodedTextPosition(end, false);
		if (( startSeg == null ) || ( endSeg == null ))
			throw new IllegalArgumentException("Invalid start or end position.");
		
		String tmpText;
		List<Code> tmpCodes;
		if (( startSeg[0] == endSeg[0] ) && ( startSeg[1] == endSeg[1] )) {
			// start == end: split that segment into two parts
			//tmpText = get(startSeg[0]).getCodedText(startSeg[1], );
			//tmpCodes = get(startSeg[0]).getCodes(0, end)
		}
		else { // Split into three parts
			
		}
		//TODO
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	public List<IContent> getSegments () {
		ArrayList<IContent> list = new ArrayList<IContent>();
		for ( IContent part : this ) {
			if ( part.isSegment() ) {
				list.add(part);
			}
		}
		return Collections.unmodifiableList(list);
	}
	
	public IContent getSegment (int index) {
		int i = 0;
		for ( IContent part : this ) {
			if ( part.isSegment() ) {
				if ( i == index ) return part;
				i++;
			}
		}
		throw new IllegalArgumentException(
			String.format("No segment found at index %d.", index));
	}
	
	public void setSegment (int index,
		IContent content)
	{
		content.setParent(this);
		set(getPartIndexFromSegmentIndex(index), content);
		updateCodes();
	}

	public void removeSegment (int index) {
		remove(getPartIndexFromSegmentIndex(index));
		updateCodes();
	}
	
	@Override
	public void add (int index, IContent content) {
		if ( content == null )
			throw new IllegalArgumentException("Cannot add a null content.");
		content.setParent(this);
		super.add(index, content);
		updateLastCodeID();
	}
	
	@Override
	public boolean add (IContent content) {
		if ( content == null )
			throw new IllegalArgumentException("Cannot add a null content.");
		content.setParent(this);
		boolean result = super.add(content);
		updateLastCodeID();
		return result;
	}

	@Override
	public boolean addAll (Collection collection) {
		//TODO
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	@Override
	public boolean addAll (int index,
		Collection collection)
	{
		//TODO
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	@Override
	public IContent set (int index,
		IContent content)
	{
		if ( content == null )
			throw new IllegalArgumentException("Cannot set a null content.");
		content.setParent(this);
		return super.set(index, content);
	}

	@Override
	public boolean isEmpty () {
		for ( IContent part : this ) {
			if ( !part.isEmpty() ) return false;
		}
		return true;
	}

	@Override
	public void clear () {
		super.clear();
		lastID = 0;
		id = 0;
	}

	public void append (CharSequence sequence) {
		if ( size() == 0 ) add(new Content(this, sequence));
		else get(size()-1).append(sequence);
	}

	public void append (char value) {
		if ( size() == 0 ) add(new Content(this, value));
		else get(size()-1).append(value);
	}

	public void append (String text) {
		if ( size() == 0 ) add(new Content(this, text));
		else get(size()-1).append(text);
	}

	public void append (IContent content) {
		if ( size() == 0 ) add(content);
		else {
			get(size()-1).append(content);
			updateLastCodeID();
		}
		
	}

	public void append (int codeType,
		String label,
		String data)
	{
		if ( size() == 0 ) add(new Content(this, true));
		get(size()-1).append(codeType, label, data);
	}

	public String getCodedText () {
		if ( size() == 0 ) return "";
		if ( size() == 1 ) return get(size()-1).getCodedText();
		StringBuilder tmp = new StringBuilder();
		int addition = 0;
		for ( IContent part : this ) {
			if ( part.isSegment() ) {
				addition++;
				tmp.append(""+((char)Content.SEG_OPENING)+Content.toChar(addition));
			}
			// Fix the code indices and append the coded text
			tmp.append(Content.updateCodeIndices(part.getCodedText(), addition));
			addition += part.getCodes().size();
			if ( part.isSegment() ) {
				addition++;
				tmp.append(""+((char)Content.SEG_CLOSING)+Content.toChar(addition));
			}
		}
		return tmp.toString();
	}

	public String getCodedText (int start, int end) {
		return getCodedText().substring(start, end);
	}

	public List<Code> getCodes () {
		ArrayList<Code> list = new ArrayList<Code>();
		if ( size() == 0 ) return list;
		int segID = 0;
		for ( IContent part : this ) {
			if ( part.isSegment() ) {
				list.add(new Code(Content.SEG_OPENING, "$s", null));
				list.get(list.size()-1).setID(++segID);
			}
			list.addAll(part.getCodes());
			if ( part.isSegment() ) {
				list.add(new Code(Content.SEG_CLOSING, "$s", null));
				list.get(list.size()-1).setID(segID);
			}
		}
		return list;
	}

	public List<Code> getCodes (int start, int end) {
		//TODO: Improve the code, this is quite costly to 
		String tmp = getCodedText().substring(start, end);
		List<Code> codes = getCodes();
		List<Code> list = new ArrayList<Code>();
		for ( int i=start; i<end; i++ ) {
			switch ( tmp.codePointAt(i) ) {
			case CODE_OPENING:
			case CODE_CLOSING:
			case CODE_ISOLATED:
			case SEG_OPENING:
			case SEG_CLOSING:
				//TODO: Do we need to clone the item copied???
				list.add(codes.get(Content.toIndex(tmp.charAt(++i))));
				break;
			}
		}
		return list;
	}

	public String getEquivText () {
		if ( size() == 0 ) return "";
		if ( size() == 1 ) return get(size()-1).getEquivText();
		StringBuilder tmp = new StringBuilder();
		for ( IContent part : this ) {
			tmp.append(part.getEquivText());
		}
		return tmp.toString();
	}

	public int getID () {
		return id;
	}

	public void setCodedText (String codedText) {
		setCodedText(codedText, getCodes());
	}

	public void setCodedText (String codedText,
		List<Code> codes)
	{
		clear();
		Content tmp = new Content(this, true);
		tmp.setCodedText(codedText, codes);
		tmp.isBalanced = false;
		add(tmp);
		updateCodes();
	}

	public void removeContent (int start, int end) {
		//TODO
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	public void setID (int id) {
		this.id = id;
	}
	
	public boolean isSegment () {
		if ( size() == 0 ) return false;
		for ( IContent part : this ) {
			if ( !part.isSegment() ) return false;
		}
		return true;
	}
	
	public int getLength () {
		return getEquivText().length();
	}

	public StringRootContainer getParent () {
		return null;
	}
	
	public void setParent (StringRootContainer parent) {
		if ( parent != null )
			throw new IllegalArgumentException("Cannot set the parent of a container.");
	}
	
	public void balanceCodes () {
		if ( size() == 0 ) return;
		for ( IContent part : this ) {
			part.balanceCodes();
		}
	}
	
	private int getPartIndexFromSegmentIndex (int index) {
		int i = 0;
		for ( int j=0; j<size(); j++ ) {
			if ( get(j).isSegment() ) {
				if ( i == index ) return j;
				else i++;
			}
		}
		throw new IllegalArgumentException(
			String.format("No segment part found at index %d.", index));
	}
	
	/**
	 * Gets the index of the part where a position is locate as well as
	 * its relative position in that part. For example, given a container made 
	 * of two segments: [abcd][efgh], the position 6 (g) returns [1,2] that is:
	 * second element of the list and third character in that element.
	 * @param pos The absolute position in the complete coded text.
	 * @param ending Indicates if this is an ending position.
	 * @return An array of 2 integers: 0=index of the part, 1=relation position.
	 * The method returns null if the position could not be found.
	 */
	private int[] getIndicesFromCodedTextPosition (int pos,
		boolean ending)
	{
		int result[] = new int[2];
		int correction = 0;
		int len;
		// Make sure ending position falls in itemN if it is itmeN(length)+1
		int end = (ending ? 1 : 0);
		// Look for the item where the position is
		for ( int i=0; i<size(); i++ ) {
			len = get(i).getCodedText().length();
			if (( pos >= correction ) && ( pos < correction+len+end )) {
				result[0] = i;
				// Result in out of bound by 1 for ending, and that's what we want
				result[1] = len-((correction+len)-pos);
				return result;
			}
			correction += len;
		}
		return null; // Not found
	}

	/**
	 * Updates the last code ID value. This assumes every part has balanced codes,
	 * otherwise the call to balance codes is triggered and wrong IDs may be set as
	 * the operation of balancing codes depend of a correct value for the last code ID.
	 */
	private void updateLastCodeID () {
		List<Code> list;
		lastID = 0;
		for ( IContent part : this ) {
			list = part.getCodes(); // This may trigger balanceCodes()
			for ( Code code : list ) {
				if ( code.getID() > lastID ) lastID = code.getID();
			}
		}
	}
	
	private void updateCodes () {
		boolean needBalance = false;
		int last = 0;
		List<Code> codes = getCodes();
		if ( codes.size() == 0 ) {
			lastID = 0;
			return;
		}
		
		// Re-number the IDs
		int i = 0;
		for ( Code code : codes ) {
			if ( code.getID() == -1 ) {
				needBalance = true;
				i++;
				continue; // Skip unbalanced codes for now
			}
			
			int stack;
			boolean found;
			
			switch ( code.getType() ) {
			case CODE_OPENING:
				code.setID(++last);
				// Search for corresponding closing code
				stack = 1;
				found = false;
				for ( int j=i+1; j<codes.size(); j++ ) {
					if ( codes.get(j).getType() == CODE_OPENING ) stack++;
					else if ( codes.get(j).getType() == CODE_CLOSING ) {
						if ( --stack == 0 ) {
							codes.get(j).setID(code.getID());
							found = true;
							break;
						}
					}
				}
				if ( !found ) {
					code.setID(-1);
					needBalance = true;
				}
				break;
			case CODE_CLOSING:
				// Should have a match already, as the opening should be before
				stack = 1;
				found = false;
				for ( int j=i-1; j>-1; j-- ) {
					if ( codes.get(j).getType() == CODE_CLOSING ) stack++;
					else if ( codes.get(j).getType() == CODE_OPENING ) {
						if ( --stack == 0 ) {
							if ( codes.get(j).getID() != code.getID() ) {
								throw new RuntimeException("updateCodes() bug!");
							}
							found = true;
							break;
						}
					}
				}
				if ( !found ) {
					code.setID(-1);
					needBalance = true;
				}
				break;
			case CODE_ISOLATED:
				code.setID(++last);
				break;

			case SEG_OPENING:
			case SEG_CLOSING:
				i++;
				break;
			}
			i++;
		}
		
		// Reset the last code ID
		lastID = last;
		// Now balance if needed
		if ( needBalance ) balanceCodes();
	}

	public StringRootContainer getStringView () {
		// Make a clone of this object
		StringRootContainer tmp = new StringRootContainer();
		tmp.setCodedText(this.getCodedText(), this.getCodes());
		return tmp;
	}

	public TreeRootContainer getTreeView () {
		// Create the new tree
		TreeRootContainer root = new TreeRootContainer();
		
		// Get the coded text and codes for the complete item
		String text = getCodedText();
		List<Code> tmpCodes = getCodes();
		Code code;
		TextFragment tf = new TextFragment();
		Stack<IContainer> stack = new Stack<IContainer>();
		stack.push(root);
		IContainer cont = root;
		
		// Process the coded text
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.codePointAt(i) ) {
			case Content.CODE_OPENING:
				if ( tf.length() > 0 ) {
					cont.add(tf);
					tf = new TextFragment();
				}
				code = tmpCodes.get(Content.toIndex(text.charAt(++i)));
				cont.add(new MarkupContainer(code.getData()));
				// New current container is the new mark-up container
				cont = (IContainer)cont.get(cont.size()-1);
				((MarkupContainer)cont).setId(code.getID());
				stack.push(cont);
				break;
				
			case Content.CODE_CLOSING:
				if ( tf.length() > 0 ) {
					cont.add(tf);
					tf = new TextFragment();
				}
				code = tmpCodes.get(Content.toIndex(text.charAt(++i)));
				((MarkupContainer)cont).setEndCode(code.getData());
				// New container is the parent of the current one
				stack.pop();
				cont = stack.peek();
				break;
				
			case Content.CODE_ISOLATED:
				if ( tf.length() > 0 ) {
					cont.add(tf);
					tf = new TextFragment();
				}
				code = tmpCodes.get(Content.toIndex(text.charAt(++i)));
				((MarkupContainer)cont).setEndCode(code.getData());
				// Current container stays the same
				break;
		
			case SEG_OPENING:
				if ( tf.length() > 0 ) {
					cont.add(tf);
					tf = new TextFragment();
				}
				code = tmpCodes.get(Content.toIndex(text.charAt(++i)));
				cont.add(new SegmentContainer());
				// New current container is the new segment container
				cont = (IContainer)cont.get(cont.size()-1);
				((SegmentContainer)cont).setId(code.getID());
				stack.push(cont);
				break;
				
			case SEG_CLOSING:
				if ( tf.length() > 0 ) {
					cont.add(tf);
					tf = new TextFragment();
				}
				i++;
				stack.pop();
				cont = stack.peek();
				break;
				
			default:
				tf.append(text.charAt(i));
				break;
			}
		}
		if ( tf.length() > 0 ) root.add(tf);
		return root;
	}

}

package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Container extends ArrayList<IContent> implements IContainer {

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
			// Fix the code indices and append the coded text
			tmp.append(Content.updateCodeIndices(part.getCodedText(), addition));
			addition += part.getCodes().size();
		}
		return tmp.toString();
	}

	public String getCodedText (int start, int end) {
		return getCodedText().substring(start, end);
	}

	public List<Code> getCodes () {
		ArrayList<Code> list = new ArrayList<Code>();
		if ( size() == 0 ) return list;
		for ( IContent part : this ) {
			list.addAll(part.getCodes());
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

	public IContainer getParent () {
		return null;
	}
	
	public void setParent (IContainer parent) {
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
				if ( code.id > lastID ) lastID = code.id;
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
			if ( code.id == -1 ) {
				needBalance = true;
				i++;
				continue; // Skip unbalanced codes for now
			}
			
			int stack;
			boolean found;
			
			switch ( code.type ) {
			case CODE_OPENING:
				code.id = ++last;
				// Search for corresponding closing code
				stack = 1;
				found = false;
				for ( int j=i+1; j<codes.size(); j++ ) {
					if ( codes.get(j).type == CODE_OPENING ) stack++;
					else if ( codes.get(j).type == CODE_CLOSING ) {
						if ( --stack == 0 ) {
							codes.get(j).id = code.id;
							found = true;
							break;
						}
					}
				}
				if ( !found ) {
					code.id = -1;
					needBalance = true;
				}
				break;
			case CODE_CLOSING:
				// Should have a match already, as the opening should be before
				stack = 1;
				found = false;
				for ( int j=i-1; j>-1; j-- ) {
					if ( codes.get(j).type == CODE_CLOSING ) stack++;
					else if ( codes.get(j).type == CODE_OPENING ) {
						if ( --stack == 0 ) {
							if ( codes.get(j).id != code.id ) {
								throw new RuntimeException("updateCodes() bug!");
							}
							found = true;
							break;
						}
					}
				}
				if ( !found ) {
					code.id = -1;
					needBalance = true;
				}
				break;
			case CODE_ISOLATED:
				code.id = ++last;
				break;
			}
			i++;
		}
		
		// Reset the last code ID
		lastID = last;
		// Now balance if needed
		if ( needBalance ) balanceCodes();
	}
}

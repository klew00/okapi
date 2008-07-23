package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Container extends ArrayList<IContent> implements IContainer {

	private static final long     serialVersionUID = 1L;
	
	private int                   lastID;
	private int                   id;


	public IContent addPart (boolean isSegment) {
		add(new Content(this, isSegment));
		return get(size()-1);
	}
	
	public IContent addPart (boolean isSegment,
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
	
	public void joinParts () {
		if ( size() < 2 ) return;
		// Create temporary holders for text and codes
		List<Code> newCodes = new ArrayList<Code>();
		StringBuilder newText = new StringBuilder();
		// Gather the segments data
		int addition = 0;
		for ( IContent part : this ) {
			// Fix the code indices and add the coded text 
			newText.append(updateCodeIndices(part.getCodedText(), addition));
			// Add codes (no need for cloning here)
			newCodes.addAll(part.getCodes());
			addition = newCodes.size();
		}
		clear();
		// Set the new data
		setCodedText(newText.toString(), newCodes);
	}

	public List<IContent> getSegments () {
		ArrayList<IContent> list = new ArrayList<IContent>();
		for ( IContent part : this ) {
			if ( part.isSegment() ) {
				list.add(part);
			}
		}
		return list;
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
	}
	
	@Override
	public boolean add (IContent content) {
		if ( content == null )
			throw new IllegalArgumentException("Cannot add a null content.");
		content.setParent(this);
		return super.add(content);
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
			tmp.append(updateCodeIndices(part.getCodedText(), addition));
			addition += part.getCodes().size();
		}
		return tmp.toString();
	}

	public String getCodedText (int start, int end) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet.");
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
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	public String getEquivText () {
		if ( size() == 0 ) return "";
		if ( size() == 1 ) return get(size()-1).getEquivText();
		StringBuilder tmp = new StringBuilder();
		for ( IContent part : this ) {
			//TODO: need to update the code indices!!!
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
	 * Add a given value to the index value of every code in the coded text.
	 * @param codedText The coded text to change.
	 * @param addition The value to add.
	 */
	private String updateCodeIndices (String codedText,
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

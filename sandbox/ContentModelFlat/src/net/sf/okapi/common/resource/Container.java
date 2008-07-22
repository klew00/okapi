package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.List;

public class Container implements IContainer {

	private Content               lastPart;
	private ArrayList<IContent>   parts;
	private int                   lastID;
	private int                   id;


	public Container () {
		parts = new ArrayList<IContent>();
	}
	
	public IContent addPart (boolean isSegment) {
		lastPart = new Content(this, isSegment);
		parts.add(lastPart);
		return lastPart;
	}
	
	public IContent addPart (boolean isSegment,
		String text)
	{
		lastPart = new Content(this, text);
		lastPart.isSegment = isSegment;
		parts.add(lastPart);
		return lastPart;
	}
	
	public int getLastCodeID () {
		return lastID;
	}
	
	public int setLastCodeID (int value) {
		lastID = value;
		return lastID;
	}
	
	public void joinParts () {
		if (( lastPart == null ) || ( parts.size() == 1 )) return;
		// Create temporary holders for text and codes
		List<Code> newCodes = new ArrayList<Code>();
		StringBuilder newText = new StringBuilder();
		// Gather the segments data
		for ( IContent part : parts ) {
			// Add coded text
			newText.append(part.getCodedText());
			// Add codes (no need for cloning here)
			newCodes.addAll(part.getCodes());
		}
		// Make sure setCodedText() won't be called recursively
		// by setting lastPart to null
		lastPart = null;
		// Set the new data
		setCodedText(newText.toString(), newCodes);
	}

	public List<IContent> getSegments () {
		ArrayList<IContent> list = new ArrayList<IContent>();
		for ( IContent part : parts ) {
			if ( part.isSegment() ) {
				list.add(part);
			}
		}
		return list;
	}
	
	public List<IContent> getParts () {
		//TODO: should it be copy or self?
		return parts;
	}
	
	public IContent getSegment (int index) {
		int i = 0;
		for ( IContent part : parts ) {
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
		parts.set(getPartIndexFromSegmentIndex(index), content);
		updateCodes();
	}

	public void removeSegment (int index) {
		parts.remove(getPartIndexFromSegmentIndex(index));
		updateCodes();
	}
	
	public IContent getPart (int index) {
		return parts.get(index);
	}
	
	public void setPart (int index,
		IContent content)
	{
		content.setParent(this);
		parts.set(index, content);
		updateCodes();
	}

	public void removePart (int index) {
		parts.remove(index);
		updateCodes();
	}

	public void append (CharSequence sequence) {
		if ( lastPart == null ) {
			lastPart = new Content(this, sequence);
			parts.add(lastPart);
		}
		else lastPart.append(sequence);
	}

	public void append (char value) {
		if ( lastPart == null ) {
			lastPart = new Content(this, value);
			parts.add(lastPart);
		}
		else lastPart.append(value);
	}

	public void append (String text) {
		if ( lastPart == null ) {
			lastPart = new Content(this, text);
			parts.add(lastPart);
		}
		else lastPart.append(text);
	}

	public void append (int codeType,
		String label,
		String data)
	{
		if ( lastPart == null ) {
			lastPart = new Content(this, false);
			parts.add(lastPart);
		}
		lastPart.append(codeType, label, data);
	}

	public void clear () {
		lastPart = null;
		parts = new ArrayList<IContent>();
		lastID = 0;
		id = 0;
	}

	public String getCodedText () {
		if ( lastPart == null ) return "";
		if ( parts.size() == 1 ) return lastPart.getCodedText();
		StringBuilder tmp = new StringBuilder();
		for ( IContent part : parts ) {
			tmp.append(part.getCodedText());
		}
		return tmp.toString();
	}

	public String getCodedText (int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Code> getCodes () {
		ArrayList<Code> list = new ArrayList<Code>();
		if ( lastPart == null ) return list;
		for ( IContent part : parts ) {
			list.addAll(part.getCodes());
		}
		return list;
	}

	public List<Code> getCodes (int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEquivText () {
		if ( lastPart == null ) return "";
		if ( parts.size() == 1 ) return lastPart.getEquivText();
		StringBuilder tmp = new StringBuilder();
		for ( IContent part : parts ) {
			tmp.append(part.getEquivText());
		}
		return tmp.toString();
	}

	public int getID () {
		return id;
	}

	public boolean isEmpty () {
		if ( lastPart == null ) return true;
		if ( parts.size() == 1 ) return lastPart.isEmpty();
		for ( IContent part : parts ) {
			if ( !part.isEmpty() ) return false;
		}
		return true;
	}

	public void setCodedText (String codedText) {
		setCodedText(codedText, getCodes());
	}

	public void setCodedText (String codedText,
		List<Code> codes)
	{
		if ( lastPart == null ) {
			lastPart = new Content(this, false);
			parts.add(lastPart);
		}
		else joinParts();
		//TODO: check for call from setcodedtext(text) if codes survive
		lastPart.codes = new ArrayList<Code>(codes);
		lastPart.text = new StringBuilder(codedText);
		updateCodes();
	}

	public void setID (int id) {
		this.id = id;
	}
	
	public boolean isSegment () {
		if ( lastPart == null ) return false;
		if ( parts.size() == 1 ) return lastPart.isSegment;
		for ( IContent part : parts ) {
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
		if ( lastPart == null ) return;
		for ( IContent part : parts ) {
			part.balanceCodes();
		}
	}
	
	private int getPartIndexFromSegmentIndex (int index) {
		int i = 0;
		for ( int j=0; j<parts.size(); j++ ) {
			if ( parts.get(j).isSegment() ) {
				if ( i == index ) return j;
				else i++;
			}
		}
		throw new IllegalArgumentException(
			String.format("No segment part found at index %d.", index));
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

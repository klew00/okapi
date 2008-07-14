package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.List;


/**
 * Reference implementation of the IContainer interface.
 * <p>In this implementation the codes are encoded into 2-char sequence:
 * the type of code, then its index in the list of codes. Note that the map 
 * returned by getCodes() is mapped on indices not IDs (it could be an array).
 */
public class Container implements IContainer {

	private ArrayList<IPart> parts;
	private IPart            lastPart;


	static public char ItoC (int index) {
		return (char)(index+CHARBASE);
	}

	static public int CtoI (char index) {
		return ((int)index)-CHARBASE;
	}

	static public boolean isCodePrefix (int codePoint) {
		switch ( codePoint ) {
		case CODE_ISOLATED:
		case CODE_OPENING:
		case CODE_CLOSING:
			return true;
		}
		return false;
	}
	
	public Container () {
		reset();
	}
	
	/**
	 * Creates a Container object and initializes it with a given plain text string.
	 * @param text The text to set.
	 */
	public Container (String text) {
		reset();
		append(text);
	}
	
	@Override
	public String toString () {
		// Shortcut for empty
		if ( lastPart == null ) return "";

		// Shortcut for only one part
		if ( parts.size() == 1 ) {
			return lastPart.toString();
		}
		
		// Otherwise
		StringBuilder text = new StringBuilder();
		for ( IPart part : parts ) {
			text.append(part.toString());
		}
		return text.toString();
	}
	
	public boolean isSegment () {
		if ( lastPart == null ) return false;
		if ( parts.size() == 1 ) return lastPart.isSegment();
		// Otherwise: multiple parts is not a segment
		return false;
	}
	
	public void setIsSegment (boolean value) {
		if ( lastPart == null ) return;
		joinParts();
		lastPart.setIsSegment(value);
	}
	
	public boolean isEmpty () {
		for ( IPart part : parts ) {
			if ( !part.isEmpty() ) return false;
		}
		return true; // Found only empty parts
	}
	
	public boolean hasText (boolean whiteSpacesAreText) {
		for ( IPart part : parts ) {
			if ( part.hasText(whiteSpacesAreText) ) return true;
		}
		return false;
	}
	
	public void reset () {
		parts = new ArrayList<IPart>();
		lastPart = null;
	}
	
	public void append (String text) {
		if ( lastPart == null ) {
			lastPart = new Part(true);
			lastPart.append(text);
			parts.add(lastPart);
		}
		else {
			lastPart.append(text);
		}
	}

	public void append (char ch) {
		if ( lastPart == null ) {
			lastPart = new Part(true);
			lastPart.append(ch);
			parts.add(lastPart);
		}
		else {
			lastPart.append(ch);
		}
	}

	public void append (IFragment fragment) {
		if ( lastPart == null ) {
			lastPart = new Part(true);
			lastPart.append(fragment);
			parts.add(lastPart);
		}
		else {
			lastPart.append(fragment);
		}
	}

	public void append (IContainer container) {
		parts.addAll(container.getParts());
		if ( parts.size() > 0 ) lastPart = parts.get(parts.size()-1);
	}

	public void append (IPart part) {
		lastPart = part;
		parts.add(lastPart);
	}

	public void setContent (String codedText) {
		joinParts();
		lastPart.setContent(codedText);
	}

	public void setContent (String codedText,
		List<IFragment> codes)
	{
		joinParts();
		lastPart.setContent(codedText, codes);
	}

	public void setContent (IFragment fragment) {
		reset();
		append(fragment);
	}
	
	public void setContent (IPart part) {
		reset();
		append(part);
	}

	public String getCodedText () {
		// Shortcut for empty
		if ( lastPart == null ) return "";

		// Shortcut for only one part
		if ( parts.size() == 1 ) {
			return lastPart.getCodedText();
		}
		
		// Otherwise
		StringBuilder text = new StringBuilder();
		for ( IPart part : parts ) {
			text.append(part.getCodedText());
		}
		return text.toString();
	}

	public List<IFragment> getCodes () {
		ArrayList<IFragment> list = new ArrayList<IFragment>();
		for ( IPart part : parts ) {
			list.addAll(part.getCodes());
		}
		return list;
	}

	public List<IFragment> getFragments () {
		ArrayList<IFragment> list = new ArrayList<IFragment>();
		for ( IPart part : parts ) {
			list.addAll(part.getFragments());
		}
		return list;
	}

	public List<IPart> getParts () {
		return parts;
	}
	
	public List<IPart> getSegments () {
		// Make a list of the parts that are segments
		ArrayList<IPart> tmpList = new ArrayList<IPart>();
		for ( IPart part : parts ) {
			if ( part.isSegment() ) tmpList.add(part);
		}
		return tmpList;
	}

	public String getCodeForID (int id,
		int type)
	{
		// For empty
		if ( lastPart == null ) return null;
		
		// For single part container
		if ( parts.size() == 1 ) {
			return lastPart.getCodeForID(id, type);
		}

		// Otherwise:
		return getTemporarySinglePart().getCodeForID(id, type);
	}

	public String toXML () {
		// Shortcut for empty
		if ( lastPart == null ) return "";

		// Shortcut for only one part
		if ( parts.size() == 1 ) {
			return lastPart.toXML();
		}
		
		// Otherwise
		StringBuilder text = new StringBuilder();
		for ( IPart part : parts ) {
			text.append(part.toXML());
		}
		return text.toString();
	}
	
	public IPart copy (int start) {
		return copy(start, -1);
	}

	public IPart copy (int start,
		int end)
	{
		if ( lastPart == null ) {
			throw new IndexOutOfBoundsException("No part available.");
		}
		
		// Shortcut for container with one part only.
		if ( parts.size() == 1 ) {
			return lastPart.copy(start, end);
		}
		
		// Otherwise:
		return getTemporarySinglePart().copy(start, end);
	}

	public void changeToCode (int start,
		int end,
		int type,
		int id)
	{
		if ( lastPart == null ) {
			throw new IndexOutOfBoundsException("No part available.");
		}
		
		// Shortcut for container with one part only.
		if ( parts.size() == 1 ) {
			lastPart.changeToCode(start, end, type, id);
		}
		
		// Otherwise:
		joinParts();
		lastPart.changeToCode(start, end, type, id);
	}
	
	public void joinParts () {
		IPart tmp = getTemporarySinglePart();
		reset();
		append(tmp);
	}
	
	public boolean isSegmented () {
		for ( IPart part : parts ) {
			if ( part.isSegment() ) return true;
		}
		return false;
	}
	
	private IPart getTemporarySinglePart () {
		Part tmp = new Part(true);
		List<IFragment> fragList;
		for ( IPart part : parts ) {
			fragList = part.getFragments();
			for ( IFragment frag : fragList ) {
				tmp.append(frag);
			}
		}
		return tmp;
	}

}

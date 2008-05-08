package net.sf.okapi.filters;

import java.util.ArrayList;

/**
 * Reference implementation of IExtractionItem.
 */
public class ExtractionItem implements IExtractionItem {
	
	private Segment               lastSeg;
	private ArrayList<Segment>    segList;
	private boolean               isSegmented;

	public ExtractionItem () {
		segList = new ArrayList<Segment>();
		segList.add(new Segment());
		lastSeg = segList.get(0);
		isSegmented = false;
	}
	
	public int getSegmentCount () {
		return segList.size();
	}

	public ISegment[] getSegments () {
		return (ISegment[])segList.toArray();
	}

	public boolean isSegmented () {
		return isSegmented;
	}

	public void append (String text) {
		lastSeg.append(text);
	}

	public void append (char value) {
		lastSeg.append(value);
	}

	public void append (int type,
		String label,
		String codeData)
	{
		lastSeg.append(type, label, codeData);
	}

	public void copyFrom (ISegment original) {
		// TODO Auto-generated method stub
		
	}

	public int getCodeCount () {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getCodedText () {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCodes () {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasCode () {
		// TODO Auto-generated method stub
		return false;
	}

	public void setTextFromCoded (String codedText) {
		// TODO Auto-generated method stub
		
	}

	public void setCodes (String data) {
		// TODO Auto-generated method stub
		
	}

	public String toString (int textType) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCodeID (int index) {
		if ( !isSegmented )
			return lastSeg.getCodeID(index);
		return 0; //TODO
	}

	public String getCodeData (int index) {
		if ( !isSegmented )
			return lastSeg.getCodeData(index);
		return null; // TODO
	}

	public int getCodeIndex(int id, int type) {
		if ( !isSegmented )
			return lastSeg.getCodeIndex(id, type);
		return 0;
	}

	public String getCodeLabel(int index) {
		if ( !isSegmented )
			return lastSeg.getCodeLabel(index);
		return null;
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public void setText(String text) {
		// TODO Auto-generated method stub
		
	}

	public void setTextFromGeneric(String genericText) {
		// TODO Auto-generated method stub
		
	}

}

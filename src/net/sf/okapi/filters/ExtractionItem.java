/*===========================================================================*/
/* Copyright (C) 2008 Yves savourel (at ENLASO Corporation)                  */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

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
		reset();
	}

	public void reset () {
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
		if ( isSegmented ) buildItem();
		lastSeg.append(value);
	}

	public void append (int type,
		String label,
		String codeData)
	{
		if ( isSegmented ) buildItem();
		lastSeg.append(type, label, codeData);
	}

	public void append (Segment segment) {
		if ( isSegmented ) buildItem();
		lastSeg.append(segment);
	}

	public void copyFrom (ISegment original) {
		// TODO
	}

	public int getCodeCount () {
		if ( isSegmented ) buildItem();
		return lastSeg.getCodeCount();
	}

	public String getCodeData (int index) {
		if ( isSegmented ) buildItem();
		return lastSeg.getCodeData(index);
	}

	public int getCodeID (int index) {
		if ( isSegmented ) buildItem();
		return lastSeg.getCodeID(index);
	}

	public int getCodeIndex (int id, int type) {
		if ( isSegmented ) buildItem();
		return lastSeg.getCodeIndex(id, type);
	}

	public String getCodeLabel (int index) {
		if ( isSegmented ) buildItem();
		return lastSeg.getCodeLabel(index);
	}

	public String getCodedText () {
		if ( isSegmented ) buildItem();
		return lastSeg.getCodedText();
	}

	public String getCodes () {
		if ( isSegmented ) buildItem();
		return lastSeg.getCodes();
	}

	public int getLength (int textType) {
		if ( isSegmented ) buildItem();
		return lastSeg.getLength(textType);
	}

	public boolean hasCode () {
		if ( isSegmented ) buildItem();
		return lastSeg.hasCode();
	}

	public boolean hasText (boolean whiteSpaceIsText) {
		if ( isSegmented ) buildItem();
		return lastSeg.hasText(whiteSpaceIsText);
	}

	public boolean isEmpty () {
		if ( isSegmented ) buildItem();
		return lastSeg.isEmpty();
	}

	public void setCodes (String data) {
		if ( isSegmented ) buildItem();
		lastSeg.setCodes(data);
	}

	public void setText (String text) {
		if ( isSegmented ) buildItem();
		lastSeg.setText(text);
	}

	public void setTextFromCoded (String codedText) {
		if ( isSegmented ) buildItem();
		lastSeg.setTextFromCoded(codedText);
	}

	public void setTextFromGeneric (String genericText) {
		if ( isSegmented ) buildItem();
		lastSeg.setTextFromGeneric(genericText);
	}

	public String toString (int textType) {
		if ( isSegmented ) buildItem();
		return lastSeg.toString(textType);
	}
	
	private void buildItem () {
		if ( !isSegmented ) return;
		Segment workSeg = new Segment();
		for ( Segment seg : segList ) {
	//		workSeg.append(seg);
		}
		lastSeg = workSeg;
		isSegmented = false; //????
	}

}

/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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

package net.sf.okapi.common.filters;

import java.util.ArrayList;

/**
 * Reference implementation of IExtractionItem.
 */
public class ExtractionItem implements IExtractionItem {
	
	private ISegment              lastSeg;
	private ISegment              compiledSeg;
	private ArrayList<ISegment>   segList;
	private boolean               isSegmented;

	public ExtractionItem () {
		reset();
	}

	public ISegment clone () {
		ExtractionItem newItem = new ExtractionItem();
		newItem.copyFrom(this);
		return (ISegment)newItem;
	}
	
	public void reset () {
		segList = new ArrayList<ISegment>();
		segList.add(new Segment());
		lastSeg = segList.get(0);
		compiledSeg = lastSeg;
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
	
	public void addSegment (ISegment newSegment) {
		segList.add(newSegment);
		lastSeg = segList.get(segList.size()-1);
		isSegmented = true;
	}
	
	public void makeOneSegment () {
		buildCompiledSegment(true);
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

	public void append (ISegment segment) {
		lastSeg.append(segment);
	}

	public void copyFrom (ISegment original) {
		if ( isSegmented ) {
			buildCompiledSegment(true);
		}
		lastSeg.copyFrom(original);
	}

	public int getCodeCount () {
		if ( isSegmented ) {
			buildCompiledSegment(false);
			return compiledSeg.getCodeCount();
		}
		else {
			return lastSeg.getCodeCount();
		}
	}

	public String getCodeData (int index) {
		if ( isSegmented ) {
			buildCompiledSegment(false);
			return compiledSeg.getCodeData(index);
		}
		else {
			return lastSeg.getCodeData(index);
		}
	}

	public int getCodeID (int index) {
		if ( isSegmented ) {
			buildCompiledSegment(false);
			return compiledSeg.getCodeID(index);
		}
		else {
			return lastSeg.getCodeID(index);
		}
	}

	public int getCodeIndex (int id, int type) {
		if ( isSegmented ) {
			buildCompiledSegment(false);
			return compiledSeg.getCodeIndex(id, type);
		}
		else {
			return lastSeg.getCodeIndex(id, type);
		}
	}

	public String getCodeLabel (int index) {
		if ( isSegmented ) {
			buildCompiledSegment(false);
			return compiledSeg.getCodeLabel(index);
		}
		else {
			return lastSeg.getCodeLabel(index);
		}
	}

	public String getCodedText () {
		if ( isSegmented ) {
			buildCompiledSegment(false);
			return compiledSeg.getCodedText();
		}
		else {
			return lastSeg.getCodedText();
		}
	}

	public String getCodes () {
		if ( isSegmented ) {
			buildCompiledSegment(false);
			return compiledSeg.getCodes();
		}
		else {
			return lastSeg.getCodes();
		}
	}

	public int getLength (int textType) {
		if ( isSegmented ) {
			buildCompiledSegment(false);
			return compiledSeg.getLength(textType);
		}
		else {
			return lastSeg.getLength(textType);
		}
	}

	public boolean hasCode () {
		if ( isSegmented ) {
			buildCompiledSegment(false);
			return compiledSeg.hasCode();
		}
		else {
			return lastSeg.hasCode();
		}
	}

	public boolean hasText (boolean whiteSpaceIsText) {
		if ( isSegmented ) {
			buildCompiledSegment(false);
			return compiledSeg.hasText(whiteSpaceIsText);
		}
		else {
			return lastSeg.hasText(whiteSpaceIsText);
		}
	}

	public boolean isEmpty () {
		if ( isSegmented ) {
			buildCompiledSegment(false);
			return compiledSeg.isEmpty();
		}
		else {
			return lastSeg.isEmpty();
		}
	}

	public void setCodes (String data) {
		if ( isSegmented ) {
			buildCompiledSegment(true);
		}
		lastSeg.setCodes(data);
	}

	public void setText (String text) {
		if ( isSegmented ) {
			buildCompiledSegment(true);
		}
		lastSeg.setText(text);
	}

	public void setTextFromCoded (String codedText) {
		if ( isSegmented ) {
			buildCompiledSegment(true);
		}
		lastSeg.setTextFromCoded(codedText);	
	}

	public void setTextFromGeneric (String genericText) {
		if ( isSegmented ) {
			buildCompiledSegment(true);
		}
		lastSeg.setTextFromGeneric(genericText);
	}

	@Override
	public String toString () {
		if ( isSegmented ) {
			buildCompiledSegment(false);
			return compiledSeg.toString();
		}
		else {
			return lastSeg.toString();	
		}
	}
	
	public String toString (int textType) {
		if ( isSegmented ) {
			buildCompiledSegment(false);
			return compiledSeg.toString(textType);
		}
		else {
			return lastSeg.toString(textType);	
		}
	}
	
	public ISegment subSegment (int start,
		int length,
		boolean addMissingCodes)
	{
		if ( isSegmented ) {
			buildCompiledSegment(true);
		}
		return lastSeg.subSegment(start, length, addMissingCodes);
	}

	private void buildCompiledSegment (boolean removeSegmentation) {
		if ( !isSegmented ) return;
		// Check if any change occurred in the segments
		//TODO
		
		// If needed: compile them into one
		compiledSeg = new Segment();
		for ( ISegment seg : segList ) {
			compiledSeg.append(seg);
		}
		
		// If requested go back to a non-segmented state
		if ( removeSegmentation ) {
			segList = new ArrayList<ISegment>();
			segList.add(compiledSeg);
			lastSeg = segList.get(0);
			isSegmented = false;
		}
	}

}

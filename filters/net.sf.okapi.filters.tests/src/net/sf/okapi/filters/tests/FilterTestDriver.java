/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.filters.tests;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.ISkeleton;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextUnit;

/**
 * Driver to test filter output.
 */
public class FilterTestDriver {

	private boolean showSkeleton = true;
	private boolean showOnlyTextUnits = false;
	private boolean ok;

	/**
	 * Indicates to this driver to display the skeleton data.
	 * @param value True to display the skeleton, false to not display the skeleton.
	 */
	public void setShowSkeleton (boolean value) {
		showSkeleton = value;
		
	}
	
	/**
	 * Indicates to this driver that only the TEXT_UNIT event should be displayed.
	 * @param value True to show only the text units, false to show all events. 
	 */
	public void setShowOnlyTextUnits (boolean value) {
		showOnlyTextUnits = value;
		
	}

	/**
	 * Process the input document. You must have called the setOptions() and open() methods 
	 * of the filter before calling this method.
	 * @param filter Filter to process.
	 * @return False if an error occurred, true if all was OK.
	 */
	public boolean process (IFilter filter) {
		ok = true;
		int start = 0;
		int finished = 0;
		int startDoc = 0;
		int endDoc = 0;
		int startGroup = 0;
		int endGroup = 0;
		
		System.out.println("================================================");
		FilterEvent event;
		while ( filter.hasNext() ) {
			event = filter.next();
			switch ( event.getEventType() ) {
			case START:
				start++;
				if ( showOnlyTextUnits ) break;
				System.out.println("---Start");
				break;
			case FINISHED:
				finished++;
				if ( showOnlyTextUnits ) break;
				System.out.println("---Finished");
				break;
			case START_DOCUMENT:
				startDoc++;
				checkStartDocument((StartDocument)event.getResource());
				if ( showOnlyTextUnits ) break;
				System.out.println("---Start Document");
				printSkeleton(event.getResource());
				break;
			case END_DOCUMENT:
				endDoc++;
				if ( showOnlyTextUnits ) break;
				System.out.println("---End Document");
				printSkeleton(event.getResource());
				break;
			case START_SUBDOCUMENT:
				if ( showOnlyTextUnits ) break;
				System.out.println("---Start Sub Document");
				printSkeleton(event.getResource());
				break;
			case END_SUBDOCUMENT:
				if ( showOnlyTextUnits ) break;
				System.out.println("---End Sub Document");
				printSkeleton(event.getResource());
				break;
			case START_GROUP:
				startGroup++;
				if ( showOnlyTextUnits ) break;
				System.out.println("---Start Group");
				printSkeleton(event.getResource());
				break;
			case END_GROUP:
				endGroup++;
				if ( showOnlyTextUnits ) break;
				System.out.println("---End Group");
				printSkeleton(event.getResource());
				break;
			case TEXT_UNIT:
				System.out.println("---Text Unit");
				TextUnit tu = (TextUnit)event.getResource();
				printResource(tu);
				System.out.println("S=["+tu.toString()+"]");
				for ( String lang : tu.getTargetLanguages() ) {
					System.out.println("T=["+tu.getTarget(lang).toString()+"]");
				}
				printSkeleton(tu);
				break;
			case DOCUMENT_PART:
				if ( showOnlyTextUnits ) break;
				System.out.println("---Document Part");
				printResource((INameable)event.getResource());
				printSkeleton(event.getResource());
				break;
			}
		}
		
		if ( start != 1 ) {
			System.out.println(String.format("START = %d", start));
			ok = false;
		}
		if ( startDoc != 1 ) {
			System.out.println(String.format("START_DOCUMENT = %d", startDoc));
			ok = false;
		}
		if ( endDoc != 1 ) {
			System.out.println(String.format("END_DOCUMENT = %d", endDoc));
			ok = false;
		}
		if ( finished != 1 ) {
			System.out.println(String.format("FINISHED = %d", finished));
			ok = false;
		}
		if ( startGroup != endGroup ) {
			System.out.println(String.format("START_GROUP=%d, END_GROUP=%d", startGroup, endGroup));
			ok = false;
		}
		
		return ok;
	}
	
	private void printResource (INameable res) {
		if ( res == null ) {
			System.err.println("NULL resource.");
			ok = false;
		}
		System.out.println("  id="+res.getId());
		System.out.println("  name="+res.getName());
		System.out.println("  type="+res.getType());
		System.out.println("  mimeType="+res.getMimeType());
	}

	private void printSkeleton (IResource res) {
		if ( !showSkeleton ) return;
		ISkeleton skel = res.getSkeleton();
		if ( skel != null ) {
			System.out.println("---");
			System.out.println(skel.toString());
			System.out.println("---");
		}
	}
	
	private void checkStartDocument (StartDocument startDoc) {
		String tmp = startDoc.getEncoding();
		if (( tmp == null ) || ( tmp.length() == 0 )) {
			System.err.println("No encoding specified in StartDocument.");
			ok = false;
		}
		else System.err.println("StartDocument encoding = "+tmp);
		
		tmp = startDoc.getLanguage();
		if (( tmp == null ) || ( tmp.length() == 0 )) {
			System.err.println("No language specified in StartDocument.");
			ok = false;
		}
		else System.err.println("StartDocument language = "+tmp);
		
		tmp = startDoc.getName();
		if (( tmp == null ) || ( tmp.length() == 0 )) {
			System.err.println("No name specified in StartDocument.");
			ok = false;
		}
		else System.err.println("StartDocument name = "+tmp);

		System.err.println("StartDocument MIME type = "+startDoc.getMimeType());
		System.err.println("StartDocument MIME type = "+startDoc.getType());
	}
	
}

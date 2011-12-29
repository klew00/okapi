/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.paraaligner;

import java.util.Comparator;
import java.util.regex.Pattern;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.IReferenceable;

/**
 * Compare two {@link DocumentPart}s. Remove whitespace before comparison. If the strings are less
 * than a minimum length then do not use them for matching 
 * 
 * @author HARGRAVEJE
 * 
 */
public class EventComparator implements Comparator<Event> {
	private static final int MIN_LENGTH = 20;
	private static final String WHITESPACE_REGEX = "[ \t\r\n\f\u200B]+";
	private static final Pattern WHITESPACE_PATTERN = Pattern.compile(WHITESPACE_REGEX);
	
	private int minLength;
	
	public EventComparator() {
		this.minLength = MIN_LENGTH;
	}
	
	public EventComparator(int minLength) {
		this.minLength = minLength;
	}
	
	@Override
	public int compare(final Event srcEvent, final Event trgEvent) {	
		if (srcEvent == null || trgEvent == null) {
			return -1;
		}
		
		if (srcEvent.getEventType() != trgEvent.getEventType()) {
			return -1;
		}
		
		if (srcEvent.getEventType() == EventType.DOCUMENT_PART && 
				trgEvent.getEventType() == EventType.DOCUMENT_PART) {
			String src = WHITESPACE_PATTERN.matcher(srcEvent.getDocumentPart().toString()).replaceAll(" ");
			String trg = WHITESPACE_PATTERN.matcher(trgEvent.getDocumentPart().toString()).replaceAll(" ");
			if (src.length() <= minLength || trg.length() <= minLength) {
				return -1;
			}
			return src.compareTo(trg);
		}
		
		// any other Event types never match
		return 1;
	}
}

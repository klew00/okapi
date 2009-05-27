/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.plaintext;

import java.util.LinkedList;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment.TagType;

public class SplicedLinesFilter extends PlainTextFilter {

	public char splicer = '\\'; // Char at the end of a line to be continued on the next line (normally "\" or "_")
	public boolean createPlaceholders = true; // If in-line codes should be created for splicers and linebreaks of spliced lines 
	
	private LinkedList<TextContainer> splicedLines;
	private boolean merging = false;
		
	@Override
	protected void filter_init() {
		
		super.filter_init();
		
		if (splicedLines == null) 
			splicedLines = new LinkedList<TextContainer>();
		else
			splicedLines.clear();		
	}
	
	@Override
	protected TextProcessingResult filter_exec(TextContainer lineContainer) {
	
		if (Util.isEmpty(lineContainer)) return super.filter_exec(lineContainer);
		if (Util.isEmpty(splicedLines)) return super.filter_exec(lineContainer);
		
		if (TextFragmentUtils.getLastChar(lineContainer) == splicer) {		
			
			merging = true;
			splicedLines.add(lineContainer);
			
			return TextProcessingResult.DELAYED_DECISION;
		}
		else {			
			if (merging) {
				
				merging = false;
				splicedLines.add(lineContainer);
				
				return (mergeLines()) ? TextProcessingResult.ACCEPTED : TextProcessingResult.REJECTED;
			}
				
			return super.filter_exec(lineContainer); // Plain text filter's line processing
		}								 								
	}
	
	@Override
	protected void filter_done() {
		
		if (merging) mergeLines();		
		splicedLines.clear();
		merging = false;
		
		super.filter_done();
	}

	private boolean mergeLines() {
		
		if (splicedLines == null) return false; 
		if (splicedLines.isEmpty()) return false;
						
		TextContainer mergedLine = new TextContainer();
		
		while (splicedLines.size() > 0) {
			
			TextContainer curLine = splicedLines.poll();
					
			String s = "";
						
			int pos = TextFragmentUtils.lastIndexOf(curLine, s+= splicer);
			if (pos > -1)
				if (createPlaceholders) 
					curLine.changeToCode(pos, pos + 1, TagType.PLACEHOLDER, "line splicer");
				else
					curLine.remove(pos, pos + 1);
			
			if (!mergedLine.isEmpty())
				if (createPlaceholders)
					mergedLine.append(new Code(TagType.PLACEHOLDER, "line break", getLineBreak()));
			
			mergedLine.append(curLine);
		}
		
		sendText(mergedLine);
				
		return true;		
	}	
}

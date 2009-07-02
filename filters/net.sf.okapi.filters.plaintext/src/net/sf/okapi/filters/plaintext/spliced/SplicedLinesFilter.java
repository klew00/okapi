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

package net.sf.okapi.filters.plaintext.spliced;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.filters.plaintext.base.BasePlainTextFilter;
import net.sf.okapi.filters.plaintext.common.AbstractLineFilter;
import net.sf.okapi.filters.plaintext.common.TextProcessingResult;
import net.sf.okapi.filters.plaintext.common.TextUnitUtils;

/**
 * 
 * 
 * @version 0.1, 09.06.2009
 * @author Sergei Vasilyev
 */

public class SplicedLinesFilter extends BasePlainTextFilter {

	public static final String FILTER_NAME				= "okf_plaintext_spliced";
	
	public static final String FILTER_CONFIG			= "okf_plaintext_spliced";
	public static final String FILTER_CONFIG_UNDERSCORE	= "okf_plaintext_spliced_underscore";
	public static final String FILTER_CONFIG_BACKSLASH	= "okf_plaintext_spliced_backslash";
	public static final String FILTER_CONFIG_CUSTOM		= "okf_plaintext_spliced_custom";
	
	private Parameters params; 	
	private List<TextContainer> splicedLines;
	private boolean merging = false;

	public SplicedLinesFilter() {
		
		super();
		setName(FILTER_NAME);
		setParameters(new Parameters());	// Spliced Lines Filter parameters

		addConfiguration(true, 
				FILTER_CONFIG,
				"Spliced Lines",
				"Extracts as one line the consecutive lines with a predefined splicer character at the end.", 
				"okf_plaintext_spliced.fprm"); // Default, the same as FILTER_CONFIG_BACKSLASH
		
		addConfiguration(false, 
				FILTER_CONFIG_BACKSLASH,
				"Spliced Lines (Backslash)",
				"Sliced lines filter with the backslash character (\\) used as the splicer.", 
				"okf_plaintext_spliced_backslash.fprm");

		addConfiguration(false, 
				FILTER_CONFIG_UNDERSCORE,
				"Spliced Lines (Underscore)",
				"Sliced lines filter with the underscore character (_) used as the splicer.", 
				"okf_plaintext_spliced_underscore.fprm");
		
		addConfiguration(false, 
				FILTER_CONFIG_CUSTOM,
				"Spliced Lines (Custom)",
				"Sliced lines filter with a user-defined splicer.", 
				"okf_plaintext_spliced_custom.fprm");
	}
	
	@Override
	protected void filter_init() {
		
		// Commons, should be included in all descendants introducing own params
		params = getParameters(Parameters.class);	// Throws OkapiBadFilterParametersException		
		super.filter_init();		// Have the ancestor initialize its part in params 

		// Specifics		
		if (splicedLines == null) 
			splicedLines = new ArrayList<TextContainer>();
		else
			splicedLines.clear();		
	}

	@Override
	protected TextProcessingResult filter_exec(TextContainer lineContainer) {
	
		if (lineContainer == null) return super.filter_exec(lineContainer);
		if (splicedLines == null) return super.filter_exec(lineContainer);
		
		//if (TextUnitUtils.getLastChar(lineContainer) == params.splicer) {
		if (TextUnitUtils.endsWith(lineContainer, params.splicer)) {
			
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
	protected void filter_idle(boolean lastChance) {
		
		if (merging) mergeLines();
				
		super.filter_idle(lastChance);
	}

	@Override
	protected void filter_done() {
		
		if (splicedLines != null) 
			splicedLines.clear();
			
		merging = false;
		
		super.filter_done();
	}

	private boolean mergeLines() {
		
		if (splicedLines == null) return false; 
		if (splicedLines.isEmpty()) return false;
		if (params == null) return false;
		if (Util.isEmpty(params.splicer)) return false;
						
		TextContainer mergedLine = new TextContainer();
		int len = params.splicer.length();
		
		for (TextContainer curLine : splicedLines) {
			
			//TextContainer curLine = splicedLines.poll();
					
//			String s = "";
						
//			int pos = TextUnitUtils.lastIndexOf(curLine, s+= params.splicer);
			int pos = TextUnitUtils.lastIndexOf(curLine, params.splicer);
			if (pos > -1)
				if (params.createPlaceholders) 
					curLine.changeToCode(pos, pos + len, TagType.PLACEHOLDER, "line splicer");
				else
					curLine.remove(pos, pos + len);
			
			if (mergedLine.isEmpty())  // Paragraph's first line
				mergedLine.setProperty(curLine.getProperty(AbstractLineFilter.LINE_NUMBER));
			else 
				if (params.createPlaceholders)
					mergedLine.append(new Code(TagType.PLACEHOLDER, "line break", getLineBreak()));
			
			mergedLine.append(curLine);
		}
		
		sendAsSource(mergedLine);
		splicedLines.clear();
				
		return true;		
	}	
}

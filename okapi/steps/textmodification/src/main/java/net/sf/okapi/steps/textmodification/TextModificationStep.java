/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.textmodification;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.TextUnitUtil;

@UsingParameters(Parameters.class)
public class TextModificationStep extends BasePipelineStep {

	private static final char STARTSEG = '[';
	private static final char ENDSEG = ']';
	private static final String OLDCHARS = "AaEeIiOoUuYyCcDdNn";
	private static final String NEWCHARS = "\u00c2\u00e5\u00c9\u00e8\u00cf\u00ec\u00d8\u00f5\u00db\u00fc\u00dd\u00ff\u00c7\u00e7\u00d0\u00f0\u00d1\u00f1";

	private Parameters params;
	private LocaleId targetLocale;
	

	public TextModificationStep () {
		params = new Parameters();
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}
	
	@Override
	public String getName () {
		return "Text Modification";
	}

	@Override
	public String getDescription () {
		return "Apply various modifications to the text units content of a document."
			+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}
 
	@Override
	protected Event handleTextUnit (Event event) {
		ITextUnit tu = event.getTextUnit();
		// Skip non-translatable
		if ( !tu.isTranslatable() ) return event;
		// Skip if already translate (only if required)
		if ( !params.applyToExistingTarget && tu.hasTarget(targetLocale) ) return event;
		// Check if we need to apply to blank entries
		if ( !params.applyToBlankEntries ) {
			TextContainer tc = tu.getTarget(targetLocale);
			if ( tc == null ) tc = tu.getSource();
			if ( !tc.hasText() ) return event;
		}

		// Create the target if needed
		tu.createTarget(targetLocale, false, IResource.COPY_ALL);
		// If the target is empty we use the source
		if ( tu.getTarget(targetLocale).isEmpty() ) {
			tu.createTarget(targetLocale, true, IResource.COPY_ALL);
		}

		// Perform the main modification
		switch ( params.type ) {
		case Parameters.TYPE_XNREPLACE:
			replaceWithXN(tu);
			break;
		case Parameters.TYPE_EXTREPLACE:
			replaceWithExtendedChars(tu);
			break;
		case Parameters.TYPE_KEEPINLINE:
			removeText(tu);
			break;
		}
		
		// Expand if needed
		if ( params.expand ) {
			expand(tu);
		}
		
		// Add segment marks if needed
		if ( params.markSegments ) {
			addSegmentMarks(tu);
		}

		// Add prefixes and suffixes to the paragraph if needed
		if ( params.addPrefix || params.addSuffix || params.addName || params.addID ) {
			addText(tu);
		}
		
		return event;
	}

	/**
	 * Removes the text but leaves the inline code.
	 * @param tu the text unit to process.
	 */
	private void removeText (ITextUnit tu) {
		for ( TextPart part : tu.getTarget(targetLocale) ) {
			StringBuilder sb = new StringBuilder();
			// Remove the text inside the part
			String text = part.text.getCodedText();
			for ( int i=0; i<text.length(); i++ ) {
				if ( TextFragment.isMarker(text.charAt(i)) ) {
					// Add the code markers
					sb.append(text.charAt(i));
					sb.append(text.charAt(++i));
				}
				// Else: text, so do nothing
			}
			part.text.setCodedText(sb.toString());
		}
	}	
	
	/**
	 * Replaces letters with Xs and digits with Ns.
	 * @param tu the text unit to process.
	 */
	private void replaceWithXN (ITextUnit tu) {
		String tmp = null;
		for ( TextPart part : tu.getTarget(targetLocale) ) {
			tmp = part.text.getCodedText().replaceAll("\\p{Lu}|\\p{Lo}", "X");
			tmp = tmp.replaceAll("\\p{Ll}", "x");
			tmp = tmp.replaceAll("\\d", "N");
			part.text.setCodedText(tmp);
		}
	}
	
	private void replaceWithExtendedChars (ITextUnit tu) {
		int n;
		for ( TextPart part : tu.getTarget(targetLocale) ) {
			StringBuilder sb = new StringBuilder(part.text.getCodedText());
			for ( int i=0; i<sb.length(); i++ ) {
				if ( TextFragment.isMarker(sb.charAt(i)) ) {
					i++; // Skip codes
				}
				else {
					if ( (n = OLDCHARS.indexOf(sb.charAt(i))) > -1 ) {
						sb.setCharAt(i, NEWCHARS.charAt(n));
					}
				}
			}
			part.text.setCodedText(sb.toString());
		}
	}

	private void addSegmentMarks (ITextUnit tu) {
		for ( Segment seg : tu.getTarget(targetLocale).getSegments() ) {
			seg.text.setCodedText(STARTSEG+seg.text.getCodedText()+ENDSEG);
		}
	}
	
	/**
	 * Adds prefix and/or suffix to the target. This method assumes that
	 * the item has gone through the first transformation already.
	 * @param tu The text unit to process.
	 */
	private void addText (ITextUnit tu) {
		if ( params.addPrefix ) {
			TextFragment firstFrag = tu.getTarget(targetLocale).getFirstContent();
			firstFrag.setCodedText(params.prefix + firstFrag.getCodedText());
		}
		TextFragment lastFrag = tu.getTarget(targetLocale).getLastContent();
		if ( params.addName ) {
			String name = tu.getName();
			if ( !Util.isEmpty(name) ) {
				lastFrag.setCodedText(lastFrag.getCodedText() + "_"+name);
			}
			else {
				lastFrag.setCodedText(lastFrag.getCodedText() + "_"+tu.getId());
			}
		}
		if ( params.addID ) {
			lastFrag.setCodedText(lastFrag.getCodedText() + "_"+tu.getId());
		}
		if ( params.addSuffix ) {
			lastFrag.setCodedText(lastFrag.getCodedText() + params.suffix);
		}
	}

	private void expand (ITextUnit tu) {
		// Get the total length of the original
		int length = getLength(tu.getSource());
		// Calculate the number of characters to add
		int addition = length; // 100% for long strings
		if ( length <= 20 ) { // 50% (or at least 1 char) for short strings
			addition = (addition+1) / 2; 
		}
		
		// Create the string to add
		StringBuilder extra = new StringBuilder();
		for ( int i=0; i<addition; i++ ) {
			if (( i % 6 == 0 ) && ( i != addition-1 )) {
				extra.append(' ');
			}
			else {
				extra.append('z');
			}
		}
		
		// Add the expansion
		TextFragment frag = tu.getTarget(targetLocale).getLastContent();
		frag.append(extra);
	}
	
	private int getLength (TextContainer tc) {
		TextFragment tf;
		if ( tc.contentIsOneSegment() ) {
			tf = tc.getFirstContent();
		}
		else {
			tf = tc.getUnSegmentedContentCopy();
		}
		return TextUnitUtil.getText(tf).length();
	}
}

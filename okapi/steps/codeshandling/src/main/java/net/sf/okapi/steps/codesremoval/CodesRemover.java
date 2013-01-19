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

package net.sf.okapi.steps.codesremoval;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.resource.ITextUnit;

public class CodesRemover {

	private Parameters params;
	private LocaleId targetLocale;

	public CodesRemover (Parameters params,
		LocaleId targetLocale)
	{
		this.params = params;
		this.targetLocale = targetLocale;
	}

	public void processTextUnit (ITextUnit tu) {
		// Skip non-translatable if requested
		if ( !tu.isTranslatable() ) {
			if ( !params.getIncludeNonTranslatable() ) return;
		}

		// Process source if needed
		if ( params.getStripSource() ) {
			processContainer(tu.getSource());
		}
		
		// Process target if needed
		if ( params.getStripTarget() ) {
			if ( tu.hasTarget(targetLocale) ) {
				processContainer(tu.getTarget(targetLocale));
			}
		}
	}

	public void processContainer (TextContainer tc) {
		for ( TextPart part : tc ) {
			processFragment(part.text);
		}
	}
	
	public void processFragment (TextFragment tf) {
		String text = tf.getCodedText();
		List<Code> codes = tf.getCodes();
		StringBuilder tmp = new StringBuilder();
		ArrayList<Code> remaining = new ArrayList<Code>();

		// Go through the content
		Code code;
		for ( int i=0; i<text.length(); i++) {
			switch ( text.charAt(i) ) {
			case TextFragment.MARKER_OPENING:
			case TextFragment.MARKER_CLOSING:
			case TextFragment.MARKER_ISOLATED:
				// Process codes
				switch ( params.getMode() ) {
				case Parameters.KEEPCODE_REMOVECONTENT:
					code = codes.get(TextFragment.toIndex(text.charAt(++i)));
					code.setData(""); // Remove the code's content
					remaining.add(code); // But keep the code
					tmp.append(text.charAt(i-1));
					tmp.append(TextFragment.toChar(remaining.size()-1));
					break;
				case Parameters.REMOVECODE_KEEPCONTENT:
					code = codes.get(TextFragment.toIndex(text.charAt(++i)));
					tmp.append(code.getData()); // Keep the code's content
					// But remove the code
					break;
				case Parameters.REMOVECODE_REMOVECONTENT:
				default:
					i++; // Skip over index
					break;
				}
				break;
			default:
				// Always preserve other characters
				tmp.append(text.charAt(i));
				break;
			}
		}
		// Set the fragment with the new content
		tf.setCodedText(tmp.toString(), remaining);
	}

}

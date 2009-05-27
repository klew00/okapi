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

import java.util.logging.Level;

import net.sf.okapi.common.EventType;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;

/**
 * <code>PlainTextFilter</code> extracts lines of input text, separated by line terminators.
 * The filter is aware of the following line terminators:
 * <ul><li>Carriage return character followed immediately by a newline character ("\r\n")
 * <li>Newline (line feed) character ("\n")
 * <li>Stand-alone carriage return character ("\r")</ul><p> 
 */

public class PlainTextFilter extends AbstractPlainTextFilter {	
		
	private Parameters params; // Plain Text Filter's parameters
	private TextUnit tuRes;
	private GenericSkeleton skel;
	
	public PlainTextFilter() {
		
		super();
		
		setName("okf_plaintext");
		setMimeType(MimeTypeMapper.PLAIN_TEXT_MIME_TYPE);
		setParameters(new Parameters());	// Plain Text Filter parameters			
	}
	
	@Override
	protected void filter_init() {
		
		// Update filter-specific params field
		if (getParameters() instanceof Parameters) params = (Parameters) getParameters();		
		checkParameters(params);

		// Initialization
		if (params.useCodeFinder && params.codeFinder != null) {
			params.codeFinder.addRule(params.regularExpressionForEmbeddedMarkup);
			params.codeFinder.compile();						
		}
	}
	
	protected TextProcessingResult sendText(TextContainer textContainer) {
		
		if (textContainer == null) return TextProcessingResult.REJECTED;		
		
		skel = new GenericSkeleton();		

		if (params.unescapeSource) _unescape(textContainer);
		if (params.trimLeft) TextFragmentUtils.trimLeft(textContainer, skel);		
		
		if (!checkTU(textContainer)) return TextProcessingResult.REJECTED;
		if (textContainer.isEmpty() && !params.sendEmptySourceTU) return TextProcessingResult.REJECTED;		
											
		//tuRes = new TextUnit(String.valueOf(++tuId));
		tuRes = new TextUnit("");
		tuRes.setName(tuRes.getId());
		tuRes.setMimeType(getMimeType());		
		tuRes.setSkeleton(skel);		
		
		skel.addContentPlaceholder(tuRes);
		if (params.trimRight) TextFragmentUtils.trimRight(textContainer, skel);
		
		tuRes.setSource(textContainer.clone());
		tuRes.setPreserveWhitespaces(params.preserveWS);
		
		// Automatically replace text fragments with in-line codes (based on regex rules of codeFinder)
		if (params.useCodeFinder) 
			params.codeFinder.process(tuRes.getSourceContent());
		
		sendEvent(EventType.TEXT_UNIT, tuRes);
		
		return TextProcessingResult.ACCEPTED;		
	}
	
	protected boolean sendSkeleton(GenericSkeleton skel) {
		
		if (skel == null) return false;
		
		GenericSkeleton activeSkel = getActiveSkeleton();
		if (activeSkel == null) return false;
		
		activeSkel.add(skel);
		
		return true;
	}
	
	protected boolean checkTU(TextContainer tuSource) {
		// Can be overridden in descendant classes
		
		return true;		
	}	
	
	@Override
	protected TextProcessingResult filter_exec(TextContainer lineContainer) {
				
		return sendText(lineContainer);		
	}
	
// Helpers	

	/**
	 * Unescapes slash-u+HHHH characters in a string.
	 * @param text The string to convert.
	 * @return The converted string.
	 */
	private void _unescape (TextContainer textContainer) {
		// Cannot be static because of the logger
		
		final String INVALID_UESCAPE = "Invalid Unicode escape sequence '%s'";
		
		if (Util.isEmpty(textContainer)) return;
		String text = textContainer.getCodedText(); 
		if (Util.isEmpty(text)) return;
		
		if ( text.indexOf('\\') == -1 ) return; // Nothing to unescape
		
		StringBuilder tmpText = new StringBuilder();
		
		for ( int i = 0; i < text.length(); i++ ) {
			if ( text.charAt(i) == '\\' ) {
				switch (Util.getCharAt(text, i+1)) {
				
				case 'u':
					if ( i+5 < text.length() ) {
						try {
							int nTmp = Integer.parseInt(text.substring(i+2, i+6), 16);
							tmpText.append((char)nTmp);
						}
						catch ( Exception e ) {
							logMessage(Level.WARNING,
								String.format(INVALID_UESCAPE, text.substring(i+2, i+6)));
						}
						i += 5;
						continue;
					}
					else {
						logMessage(Level.WARNING,
							String.format(INVALID_UESCAPE, text.substring(i+2)));
					}
					break;
				case '\\':
					tmpText.append("\\\\");
					i++;
					continue;
				case 'n':
					tmpText.append("\n");
					i++;
					continue;
				case 't':
					tmpText.append("\t");
					i++;
					continue;
				}
			}
			else tmpText.append(text.charAt(i));
		}
		
		textContainer.setCodedText(tmpText.toString());
	}

}	
	
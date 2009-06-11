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

package net.sf.okapi.filters.plaintext.base;

import java.util.Set;
import java.util.logging.Level;

import net.sf.okapi.common.EventType;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.plaintext.common.AbstractLineFilter;
import net.sf.okapi.filters.plaintext.common.TextProcessingResult;
import net.sf.okapi.filters.plaintext.common.TextUnitUtils;

/**
 * <code>PlainTextFilter</code> extracts lines of input text, separated by line terminators.
 * 
 * @version 0.1, 09.06.2009
 * @author Sergei Vasilyev
 */

public class BasePlainTextFilter extends AbstractLineFilter {	
	
	public static final String FILTER_NAME				= "okf_plaintext";
	public static final String FILTER_MIME				= MimeTypeMapper.PLAIN_TEXT_MIME_TYPE;	
	public static final String FILTER_CONFIG			= "okf_plaintext";
	
	private Parameters params; // Base Plain Text Filter parameters	
		
	public BasePlainTextFilter() {
		
		super();
		
		setName(FILTER_NAME);
		setMimeType(FILTER_MIME);
		
		addConfiguration(true, 
				FILTER_CONFIG,
				"Plain Text Filter",
				"Text files; ANSI, Unicode, UTF-8, UTF-16 are supported.", 
				null);
		
		setParameters(new Parameters());	// Base Plain Text Filter parameters
	}
	
	@Override
	protected void filter_init() {
		
		// Commons, should be included in all descendants introducing own params
		params = getParameters(Parameters.class);	// Throws OkapiBadFilterParametersException		
		super.filter_init();		// Have the ancestor initialize its part in params  
		
		// Initialization
		if (params.useCodeFinder && params.codeFinder != null) {
			params.codeFinder.addRule(params.regularExpressionForEmbeddedMarkup);
			params.codeFinder.compile();						
		}
	}
	
	protected TextProcessingResult sendContent(TextUnit textUnit) {
		
		if (textUnit == null) return TextProcessingResult.REJECTED;
		
		TextContainer source = textUnit.getSource();
		if (source == null) return TextProcessingResult.REJECTED;		
		
		GenericSkeleton skel = TextUnitUtils.forseSkeleton(textUnit);
		
		if (!checkTU(source)) return TextProcessingResult.REJECTED;
		if (source.isEmpty()) return TextProcessingResult.REJECTED;
		
		if (params.unescapeSource) _unescape(source);
		
		if (params.trimLeft) TextUnitUtils.trimLeft(source, skel);							
		skel.addContentPlaceholder(textUnit);		
		if (params.trimRight) TextUnitUtils.trimRight(source, skel);
		
		Set<String> languages = textUnit.getTargetLanguages();

		textUnit.setMimeType(getMimeType());
		textUnit.setPreserveWhitespaces(params.preserveWS);
		
		if (!params.preserveWS ) {
			// Unwrap the content
			TextFragment.unwrap(source);
			
			for (String language : languages)
				TextFragment.unwrap(textUnit.getTargetContent(language));				
		}
		
		// Automatically replace text fragments with in-line codes (based on regex rules of codeFinder)
		if (params.useCodeFinder) {
			
			params.codeFinder.process(source);
			
			for (String language : languages)
				params.codeFinder.process(textUnit.getTargetContent(language));
		}
		
		sendEvent(EventType.TEXT_UNIT, textUnit);
		
		return TextProcessingResult.ACCEPTED;
	}
	
	protected TextProcessingResult sendContent(TextContainer textContainer) {
		
		if (textContainer == null) return TextProcessingResult.REJECTED;
		
		return sendContent(TextUnitUtils.buildTU(null, "", textContainer, null, "", ""));
	}
	
	protected boolean sendSkeletonPart(GenericSkeleton skelPart) {
		
		if (skelPart == null) return false;
		
		GenericSkeleton activeSkel = getActiveSkeleton();
		if (activeSkel == null) return false;
		
		activeSkel.add(skelPart);
		
		return true;
	}
	
	protected boolean sendSkeletonPart(String skelPart) {
		
		if (skelPart == null) return false;
		
		GenericSkeleton activeSkel = getActiveSkeleton();
		if (activeSkel == null) return false;
		
		activeSkel.add(skelPart);
		
		return true;
	}
	
	protected boolean checkTU(TextContainer tuSource) {
		// Can be overridden in descendant classes
		
		return true;		
	}	
	
	@Override
	protected TextProcessingResult filter_exec(TextContainer lineContainer) {
				
		return sendContent(lineContainer);		
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
		
		if (textContainer == null) return;
		
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
	
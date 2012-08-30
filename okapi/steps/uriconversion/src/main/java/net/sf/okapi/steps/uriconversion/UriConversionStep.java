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

package net.sf.okapi.steps.uriconversion;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;

@UsingParameters(Parameters.class)
public class UriConversionStep extends BasePipelineStep {

	private static final String FORCEESCAPE = " * -,.";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Parameters params;
	private LocaleId trgLang;
	
	public UriConversionStep () {
		params = new Parameters();
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLanguage (LocaleId targetLanguage) {
		trgLang = targetLanguage;
	}

	@Override
	public String getDescription () {
		return "Encodes or Decodes URI escape sequences."
			+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getName () {
		return "URI Conversion";
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
		if ( !tu.isTranslatable() ) {
			return event;
		}
		// Else: do the requested modifications
		
		// Make sure we have a target where to set data
		TextContainer cont = tu.createTarget(trgLang, false, IResource.COPY_ALL);
		String res;

		try {
			for ( TextPart part : cont ) {
				if ( params.conversionType == Parameters.UNESCAPE ) {
					res = unescape(part.text.getCodedText());
				}
				else {
					res = escape(part.text.getCodedText());
				}
				part.text.setCodedText(res); // No change of the inline codes
			}
		}
		catch ( Exception e ) {
			logger.error(String.format("Error when updating content: '%s'", cont.toString()), e);
		}
		
		return event;
	}		

	private String unescape (String text) {
		StringBuilder sb = new StringBuilder();
		StringBuilder sbTemp = new StringBuilder();
		try {
			for ( int i=0; i<text.length(); i++ ) {
				switch ( text.charAt(i) ) {
				case TextFragment.MARKER_OPENING:
				case TextFragment.MARKER_CLOSING:
				case TextFragment.MARKER_ISOLATED:
					sb.append(URLDecoder.decode(sbTemp.toString(),"UTF-8"));
					sb.append(text.charAt(i));
					i++;
					sb.append(text.charAt(i));
					sbTemp = new StringBuilder();
					break;
	
				case '+':
					sb.append(URLDecoder.decode(sbTemp.toString(),"UTF-8"));
					sb.append(text.charAt(i));
					sbTemp = new StringBuilder();
					break;
					
				default:
					sbTemp.append(text.charAt(i));
					break;
				}
			}
			sb.append(URLDecoder.decode(sbTemp.toString(),"UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			logger.error(String.format("Error when unescaping: '%s'", text), e);
		}
		return sb.toString();
	}

	private String escape (String text) {
		StringBuilder sb = new StringBuilder();
		try {
			for ( int i=0; i<text.length(); i++ ) {
				String subStr = text.substring(i, i+1);
				// Process if extended character if selected
				if (( text.charAt(i) > '\u007f' ) && ( params.updateAll )) {
					switch ( text.charAt(i) ) {
					case TextFragment.MARKER_OPENING:
					case TextFragment.MARKER_CLOSING:
					case TextFragment.MARKER_ISOLATED:
						sb.append(text.charAt(i));
						i++;
						sb.append(text.charAt(i));
						break;
					default:
						sb.append(URLEncoder.encode(subStr,"UTF-8"));
						break;
					}
				}
				else if ( params.escapeList.contains(subStr) ) {
					if ( FORCEESCAPE.contains(subStr) ) {
						byte bytes[] = subStr.getBytes();
						sb.append("%"+Integer.toHexString(bytes[0]));
					}
					else {
						sb.append(URLEncoder.encode(subStr,"UTF-8"));							
					}
				}
				else {
					sb.append(text.charAt(i));
				}
			}
		}
		catch (UnsupportedEncodingException e) {
			logger.error(String.format("Error when escaping: '%s'", text), e);
		}
		return sb.toString();
	}

}

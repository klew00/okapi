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

package net.sf.okapi.steps.uriconversion;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

@UsingParameters(Parameters.class)
public class UriConversionStep extends BasePipelineStep {

	static final int UNESCAPE  = 0;
	static final int ESCAPE    = 1;

	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private Parameters params;
	private LocaleId trgLang;
	
	public UriConversionStep () {
		params = new Parameters();
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLanguage (LocaleId targetLanguage) {
		trgLang = targetLanguage;
	}
	
	public String getDescription () {
		return "Encode/Decode URI content.";
	}

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
		TextUnit tu = (TextUnit)event.getResource();
		// Skip non-translatable
		if ( !tu.isTranslatable()) 
			return event;

		String forceEscape = " * -,.";
		String tmp = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sbTemp = new StringBuilder();

		// Else: do the requested modifications
		// Make sure we have a target where to set data
		tu.createTarget(trgLang, false, IResource.COPY_ALL);

		try {
			String result = tu.getTarget(trgLang).getCodedText();
			
			if ( params.conversionType == UNESCAPE ){
				//--do unescape
				
				//--loop all characters--
				for ( int i=0; i<result.length(); i++ ) {
					switch ( result.charAt(i) ) {
					case TextFragment.MARKER_OPENING:
					case TextFragment.MARKER_CLOSING:
					case TextFragment.MARKER_ISOLATED:
					case TextFragment.MARKER_SEGMENT:
						sb.append(URLDecoder.decode(sbTemp.toString(),"UTF-8"));
						sb.append(result.charAt(i));
						i++;
						sb.append(result.charAt(i));
						sbTemp = new StringBuilder();
						break;

					case '+':
						sb.append(URLDecoder.decode(sbTemp.toString(),"UTF-8"));
						sb.append(result.charAt(i));
						sbTemp = new StringBuilder();
						break;
						
					default:
						sbTemp.append(result.charAt(i));
						break;
					}
				}				
				sb.append(URLDecoder.decode(sbTemp.toString(),"UTF-8"));

			}else{
				//--do escape
			
				//--loop all characters--
				for ( int i=0; i<result.length(); i++ ) {
			
					String subStr = result.substring(i, i+1);
					
					//--process if extended character if selected--
					if (( result.charAt(i) > '\u007f' ) && (params.updateAll )){
					
						switch ( result.charAt(i) ) {
						case TextFragment.MARKER_OPENING:
						case TextFragment.MARKER_CLOSING:
						case TextFragment.MARKER_ISOLATED:
						case TextFragment.MARKER_SEGMENT:
							sb.append(result.charAt(i));
							i++;
							sb.append(result.charAt(i));
							break;
						default:
							sb.append(URLEncoder.encode(subStr,"UTF-8"));
							break;
						}
					}else if (params.escapeList.contains(subStr)){
						
						if(forceEscape.contains(subStr)){
							byte bytes[] = subStr.getBytes();
							sb.append("%"+Integer.toHexString(bytes[0]));
						}else{
							sb.append(URLEncoder.encode(subStr,"UTF-8"));							
						}

					}else{
						sb.append(result.charAt(i));
					}
				}	
			}
			
			TextContainer cnt = tu.getTarget(trgLang);
			cnt.setCodedText(sb.toString(), tu.getSourceContent().getCodes(), false);
		}
		catch ( Exception e ) {
			logger.log(Level.WARNING, "Error when updating content: '"+tmp+"'", e);
		}
		
		return event;
	}		
}

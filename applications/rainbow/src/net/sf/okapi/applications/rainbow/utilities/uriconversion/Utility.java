/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.applications.rainbow.utilities.uriconversion;

import java.net.URLDecoder;
import java.net.URLEncoder;
import net.sf.okapi.applications.rainbow.utilities.BaseFilterDrivenUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

public class Utility extends BaseFilterDrivenUtility { 
	
	static final int UNESCAPE  = 0;
	static final int ESCAPE    = 1;
	
	private Parameters params;
	
	public Utility () {
		params = new Parameters();
	}
	
	public String getName () {
		return "oku_uriconversion";
	}
	
	public void preprocess () {
		// Nothing do to
	}
	
	public void postprocess () {
		// Nothing to do
	}

	public IParameters getParameters () {
		return params;
	}
	
	public boolean hasParameters () {
		return true;
	}

	public boolean needsRoots () {
		return false;
	}
	
	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
	}
	
	public boolean isFilterDriven () {
		return true;
	}

	public int requestInputCount () {
		return 1;
	}
	
	public FilterEvent handleEvent (FilterEvent event) {
		switch ( event.getEventType() ) {
		case TEXT_UNIT:
			processTU((TextUnit)event.getResource());
		}
		return event;
	}	
	
	private void processTU (TextUnit tu) {
		
		String forceEscape = " * -,.";
		
		String tmp = null;
		
		StringBuilder sb = new StringBuilder();
		StringBuilder sbTemp = new StringBuilder();
		
		// Skip non-translatable
		if ( !tu.isTranslatable()) 
			return;

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
			System.out.println("Error");
			logger.warn("Error when updating content: '"+tmp+"'", e);
		}
	}

}

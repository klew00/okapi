/*===========================================================================*/
/* Copyright (C) 2008 Fredrik Liden                                          */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.applications.rainbow.utilities.uriconversion;

import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.swing.event.EventListenerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.utilities.BaseUtility;
import net.sf.okapi.applications.rainbow.utilities.CancelListener;
import net.sf.okapi.applications.rainbow.utilities.IFilterDrivenUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

public class Utility extends BaseUtility implements IFilterDrivenUtility { 
	
	private final Logger     logger = LoggerFactory.getLogger("net.sf.okapi.logging");

	private Parameters            params;
	private String                commonFolder;
	private EventListenerList     listenerList = new EventListenerList();
	
	static final int UNESCAPE  = 0;
	static final int ESCAPE    = 1;
	
	public Utility () {
		params = new Parameters();
	}
	
	public void resetLists () {
		// Not used in this utility
		// Not sure when to use this
	}
	
	public String getID () {
		return "oku_uriconversion";
	}
	
	public void doProlog (String sourceLanguage, String targetLanguage){
		commonFolder = null; // Reset
	}
	
	public void doEpilog () {
		// Not sure when to use this
	}

	public IParameters getParameters () {
		return params;
	}
	
	public String getInputRoot () {
		return null;
	}

	public String getOutputRoot () {
		return null;
	}

	public boolean hasParameters () {
		return true;
	}

	public boolean needsRoots () {
		return false;
	}
	
	public boolean needsOutputFilter() {
		return true;
	}	

	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
	}
	
	public void setRoots (String inputRoot, String outputRoot){
		// Not used in this utility.
	}
	
	public boolean isFilterDriven () {
		return true;
	}

	public String getFolderAfterProcess () {
		return commonFolder;
	}
	
	public int getInputCount() {
		return 1;
	}
	
	public void addInputData (String path, String encoding, String filterSettings){
	}

	public void addOutputData (String path, String encoding){
		// Compute the longest common folder
		commonFolder = Util.longestCommonDir(commonFolder,
			Util.getDirectoryName(path), !Util.isOSCaseSensitive());
	}

	public void setFilterAccess (FilterAccess filterAccess,
		String paramsFolder)
	{
		// Not used
	}

	public void setContextUI (Object contextUI) {
		// Not used
	}
	
	public void addCancelListener (CancelListener listener) {
		listenerList.add(CancelListener.class, listener);
	}

	public void removeCancelListener (CancelListener listener) {
		listenerList.remove(CancelListener.class, listener);
	}

	/*private void fireCancelEvent (CancelEvent event) {
		Object[] listeners = listenerList.getListenerList();
		for ( int i=0; i<listeners.length; i+=2 ) {
			if ( listeners[i] == CancelListener.class ) {
				((CancelListener)listeners[i+1]).cancelOccurred(event);
			}
		}
	}*/

	public void endExtractionItem (TextUnit item) {
		try {
			processTU(item);
			if ( item.hasChild() ) {
				for ( TextUnit tu : item.childTextUnitIterator() ) {
					processTU(tu);
				}
			}
		}
		finally {
			super.endExtractionItem(item);
		}		
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
		if ( !tu.hasTarget() ) {
			tu.setTargetContent(tu.getSourceContent().clone());
		}

		try {
			String result = tu.getTargetContent().getCodedText();
			
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
			
			TextContainer cnt = tu.getTargetContent();
			cnt.setCodedText(sb.toString(), tu.getSourceContent().getCodes(), false);
		}
		catch ( Exception e ) {
			System.out.println("Error");
			logger.warn("Error when updating content: '"+tmp+"'", e);
		}
	}	
	
}

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

package net.sf.okapi.applications.rainbow.utilities.searchandreplace;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.EventListenerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.utilities.BaseUtility;
import net.sf.okapi.applications.rainbow.utilities.CancelListener;
import net.sf.okapi.applications.rainbow.utilities.IFilterDrivenUtility;
import net.sf.okapi.applications.rainbow.utilities.ISimpleUtility;
import net.sf.okapi.common.BOMAwareInputStream;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;

public class Utility extends BaseUtility implements ISimpleUtility, IFilterDrivenUtility { 
	
	private final Logger logger = LoggerFactory.getLogger("net.sf.okapi.logging");
	private Parameters params;
	private String commonFolder;
	private String inputPath;
	private String outputPath;
	private String inputEncoding;
	private EventListenerList listenerList = new EventListenerList();	
	
	public Utility () {
		params = new Parameters();
	}
	
	public void resetLists () {
		// Not used in this utility
		// Not sure when to use this
	}
	
	public String getName () {
		return "oku_searchandreplace";
	}
	
	public void processInput () {
		
		BufferedReader reader = null;
		OutputStreamWriter oWriter = null;
		BufferedWriter writer = null;

		try {
	        FileInputStream fis = new FileInputStream(inputPath);
			BOMAwareInputStream bis = new BOMAwareInputStream(new FileInputStream(inputPath), inputEncoding);
			inputEncoding = bis.detectEncoding();	        
			logger.info("Input encoding: " + inputEncoding);	
	        FileChannel fc = fis.getChannel();
	    
	        // Create a read-only CharBuffer on the file
	        ByteBuffer bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0, (int)fc.size());
	        CharBuffer cbuf = Charset.forName(inputEncoding).newDecoder().decode(bbuf);
	        String result = cbuf.toString();

	        for ( String[] s : params.rules ) {
	        	if ( s[0].equals("true") ) {
		        	int flags = 0;
		        	if ( params.dotAll ) flags |=  Pattern.DOTALL;
		        	if ( params.ignoreCase ) flags |= Pattern.CASE_INSENSITIVE;
		        	if ( params.multiLine ) flags |= Pattern.MULTILINE;
		        	
		        	if ( params.regEx ){
		        		Pattern pattern = Pattern.compile(s[1], flags);
		        		Matcher matcher = pattern.matcher(result);
		        		result = matcher.replaceAll(s[2]);
		        	}else{
		        		result = result.replace(s[1],s[2]);
		        	}
	        	}
	        }
	        
			Util.createDirectories(outputPath);
			oWriter = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outputPath)), inputEncoding);
			writer = new BufferedWriter(oWriter);
			logger.info("Output encoding: " + inputEncoding);
			Util.writeBOMIfNeeded(writer, true, inputEncoding);
			writer.write(result);
			
			fc.close();
			fis.close();
			writer.close();
			oWriter.close();
		}
		catch ( FileNotFoundException e ) {
			throw new RuntimeException(e);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if ( writer != null ) {
					writer.close();
					writer = null;
				}
				if ( reader != null ) {
					reader.close();
					reader = null;
				}
			}
			catch ( IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void setOptions (String sourceLanguage,
		String targetLanguage)
	{
		commonFolder = null; // Reset
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
		return !params.plainText;
	}

	public String getFolderAfterProcess () {
		return commonFolder;
	}
	
	public int requestInputCount() {
		return 1;
	}
	
	public void addInputData (String path, String encoding, String filterSettings){
		// Not sure when to use this
		inputPath = path;
		inputEncoding = encoding;
	}

	public void addOutputData (String path, String encoding){
		// Compute the longest common folder
		commonFolder = Util.longestCommonDir(commonFolder,
			Util.getDirectoryName(path), !Util.isOSCaseSensitive());
		outputPath = path;
		// Encoding stays the same as the input
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
		}
		finally {
			super.endExtractionItem(item);
		}		
	}
	
	private void processTU (TextUnit tu) {
		String tmp = null;
		try {
			// Skip non-translatable
			if ( !tu.isTranslatable() ) return;

			// Else: do the requested modifications
			// Make sure we have a target where to set data
			tu.createTarget(trgLang, false, IResource.COPY_ALL);

			String result = tu.getTargetContent(trgLang).getCodedText();
	        for ( String[] s : params.rules ) {
	        	if ( s[0].equals("true") ) {
		        	int flags = 0;
		        	if ( params.dotAll ) flags |=  Pattern.DOTALL;
		        	if ( params.ignoreCase ) flags |= Pattern.CASE_INSENSITIVE;
		        	if ( params.multiLine ) flags |= Pattern.MULTILINE;
		        	
		        	if ( params.regEx ){
		        		Pattern pattern = Pattern.compile(s[1], flags);
		        		Matcher matcher = pattern.matcher(result);
		        		result = matcher.replaceAll(s[2]);
		        	}else{
		        		result = result.replace(s[1],s[2]);
		        	}
	        	}
	        }			
			
			TextContainer cnt = tu.getTarget(trgLang); 
			cnt.setCodedText(result);
		}
		catch ( Exception e ) {
			logger.warn("Error when updating content: '"+tmp+"'", e);
		}
	}	
	
}

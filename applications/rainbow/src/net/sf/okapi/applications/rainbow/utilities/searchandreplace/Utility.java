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
import net.sf.okapi.applications.rainbow.utilities.CancelListener;
import net.sf.okapi.applications.rainbow.utilities.ISimpleUtility;
import net.sf.okapi.common.BOMAwareInputStream;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;

public class Utility implements ISimpleUtility {

	private final Logger     logger = LoggerFactory.getLogger("net.sf.okapi.logging");

	private Parameters            params;
	private String                commonFolder;
	private String                inputPath;
	private String                outputPath;
	private EventListenerList     listenerList = new EventListenerList();
	private String			      inputEncoding;
	
	public Utility () {
		params = new Parameters();
	}
	
	public void resetLists () {
		// Not used in this utility
	}
	
	public String getID () {
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
		        	if ( params.ignoreCase ) {
		        		flags = flags | Pattern.CASE_INSENSITIVE;
		        	}
		        	if ( params.multiLine ) {
		        		flags = flags | Pattern.MULTILINE;
		        	}
		        	Pattern pattern = Pattern.compile(s[1], flags);
		            Matcher matcher = pattern.matcher(result);
		            result = matcher.replaceAll(s[2]);	        		
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

	public void doEpilog () {
	}

	public void doProlog (String sourceLanguage,
		String targetLanguage)
	{
		commonFolder = null; // Reset
	}

	public String getInputRoot () {
		return null;
	}

	public String getOutputRoot () {
		return null;
	}

	public IParameters getParameters () {
		return params;
	}

	public boolean hasParameters () {
		return true;
	}

	public boolean isFilterDriven () {
		return false;
	}

	public boolean needsRoots () {
		return false;
	}

	public void addInputData (String path,
		String encoding,
		String filterSettings)
	{
		inputPath = path;
		inputEncoding = encoding;
	}

	public void addOutputData (String path,
		String encoding)
	{
		// Compute the longest common folder
		commonFolder = Util.longestCommonDir(commonFolder,
			Util.getDirectoryName(path), !Util.isOSCaseSensitive());
		outputPath = path;
		// Encoding stays the same as the input
	}

	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
	}

	public void setRoots (String inputRoot,
		String outputRoot)
	{
		// Not used in this utility.
	}

	public String getFolderAfterProcess () {
		return commonFolder;
	}

	public int getInputCount() {
		return 1;
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

}

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
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.okapi.applications.rainbow.utilities.BaseFilterDrivenUtility;
import net.sf.okapi.applications.rainbow.utilities.ISimpleUtility;
import net.sf.okapi.common.BOMAwareInputStream;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;

public class Utility extends BaseFilterDrivenUtility implements ISimpleUtility { 
	
	private Parameters params;
	
	public Utility () {
		params = new Parameters();
	}
	
	public String getName () {
		return "oku_searchandreplace";
	}
	
	public void processInput () {
		BufferedReader reader = null;
		OutputStreamWriter oWriter = null;
		BufferedWriter writer = null;

		try {
	        FileInputStream fis = new FileInputStream(getInputPath(0));
			BOMAwareInputStream bis = new BOMAwareInputStream(
				new FileInputStream(getInputPath(0)), getInputEncoding(0));
			String encoding = bis.detectEncoding();	        
	        FileChannel fc = fis.getChannel();
	    
	        // Create a read-only CharBuffer on the file
	        ByteBuffer bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0, (int)fc.size());
	        CharBuffer cbuf = Charset.forName(encoding).newDecoder().decode(bbuf);
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
	        
			Util.createDirectories(getOutputPath(0));
			oWriter = new OutputStreamWriter(new BufferedOutputStream(
				new FileOutputStream(getOutputPath(0))), encoding);
			writer = new BufferedWriter(oWriter);
			Util.writeBOMIfNeeded(writer, true, encoding);
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

	public void preprocess () {
		// Nothing to do
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
		return !params.plainText;
	}

	public int requestInputCount() {
		return 1;
	}

	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case TEXT_UNIT:
			processTextUnit((TextUnit)event.getResource());
			break;
		}
		return event;
	}	
	
	private void processTextUnit (TextUnit tu) {
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
			logger.log(Level.WARNING, "Error when updating content: '"+tmp+"'", e);
		}
	}

}

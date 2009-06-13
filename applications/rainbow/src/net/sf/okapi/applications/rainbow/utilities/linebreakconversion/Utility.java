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
============================================================================*/

package net.sf.okapi.applications.rainbow.utilities.linebreakconversion;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import net.sf.okapi.applications.rainbow.utilities.BaseUtility;
import net.sf.okapi.applications.rainbow.utilities.ISimpleUtility;
import net.sf.okapi.common.BOMAwareInputStream;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;

public class Utility extends BaseUtility implements ISimpleUtility {

	private static final int BUFFER_SIZE = 1024;
	
	private Parameters params;

	public Utility () {
		params = new Parameters();
	}
	
	public String getName () {
		return "oku_linebreakconversion";
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

	public boolean isFilterDriven () {
		return false;
	}

	public boolean needsRoots () {
		return false;
	}

	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
	}

	public int requestInputCount() {
		return 1;
	}

	public void processInput () {
		BufferedReader reader = null;
		OutputStreamWriter writer = null;
		try {
			// Open the input
			InputStream input = new FileInputStream(getInputPath(0));
			BOMAwareInputStream bis = new BOMAwareInputStream(input, getInputEncoding(0));
			String encoding = bis.detectEncoding(); // Update the encoding: it'll be use for the output
			reader = new BufferedReader(new InputStreamReader(bis, encoding));
			
			// Open the output
			Util.createDirectories(getOutputPath(0)); // Make sure the folder exists
			OutputStream output = new FileOutputStream(getOutputPath(0));
			writer = new OutputStreamWriter(new BufferedOutputStream(output), encoding);
			// Write BOM if there was one
			Util.writeBOMIfNeeded(writer, (bis.getBOMSize()>0), encoding);
			
			// Set the variables
			char[] buf = new char[BUFFER_SIZE];
			int length = 0;
			int i;
			int done = 0;
			
			// Process the file
			while ( (length = reader.read(buf, 0, BUFFER_SIZE-1)) > 0 ) {
				// Check if you need to read the next char to avoid splitting cases
				if ( buf[length-1] == '\r'  ) {
					int count = reader.read(buf, length, 1);
					if ( count > -1 ) length++;
				}
				// Reset 'done' flag on second pass after it was set
				if ( done == 1 ) done++; else done = 0;
				// Replace line-breaks
				int start = 0;
				for ( i=0; i<length; i++ ) {
					if ( buf[i] == '\n') {
						if (( i != 0 ) || ( done == 0 )) {
							writer.write(buf, start, i-start);
							writer.write(params.lineBreak);
						}
						start = i+1;
					}
					else if ( buf[i] == '\r') {
						writer.write(buf, start, i-start);
						writer.write(params.lineBreak);
						// Check if it's a \r\n
						if ( i+1 < length ) {
							if ( buf[i+1] == '\n' ) {
								i++; // Skip it
							}
						}
						start = i+1;
						// We could be splitting a \r\n, so let's remember
						done = 1;
					}
				}
				// Write out the remainder of the buffer
				if ( length-start > 0 ) {
					writer.write(buf, start, length-start);
				}
			}

		}
		catch ( UnsupportedEncodingException e ) {
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
			catch ( IOException e ) {
				throw new RuntimeException(e);
			}
		}
	}

}

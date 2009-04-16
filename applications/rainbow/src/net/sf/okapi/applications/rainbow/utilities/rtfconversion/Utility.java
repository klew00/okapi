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

package net.sf.okapi.applications.rainbow.utilities.rtfconversion;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import net.sf.okapi.applications.rainbow.utilities.BaseUtility;
import net.sf.okapi.applications.rainbow.utilities.ISimpleUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.rtf.RTFFilter;

public class Utility extends BaseUtility implements ISimpleUtility {
	
	private RTFFilter filter;
	private Parameters params;
	private String lineBreak;

	public Utility () {
		params = new Parameters();
	}
	
	public void processInput () {
		OutputStreamWriter writer = null;
		try {
			// Open the RTF input
			File f = new File(getInputPath(0));
			filter.open(new RawDocument(f.toURI(), getInputEncoding(0), srcLang, trgLang));
			
			// Open the output document
			Util.createDirectories(getOutputPath(0));
			writer = new OutputStreamWriter(new BufferedOutputStream(
				new FileOutputStream(getOutputPath(0))), getOutputEncoding(0));
			Util.writeBOMIfNeeded(writer, true, getOutputEncoding(0));
			
			// Process
			StringBuilder buf = new StringBuilder();
			while ( filter.getTextUntil(buf, -1, 0) == 0 ) {
				writer.write(buf.toString());
				writer.write(lineBreak);
			}
		}
		catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
		catch ( FileNotFoundException e ) {
			throw new RuntimeException(e);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		finally {
			if ( writer != null ) {
				try {
					writer.close();
				}
				catch (IOException e) {
					// Do nothing
				}
			}
			if ( filter != null ) {
				filter.close();
			}
		}
	}

	public String getName () {
		return "oku_rtfconversion";
	}

	public IParameters getParameters () {
		return params;
	}

	public boolean hasParameters () {
		return false;
	}

	public boolean isFilterDriven () {
		return false;
	}

	public boolean needsRoots () {
		return false;
	}

	public void postprocess () {
	}

	public void preprocess () {
		filter = new RTFFilter();
		switch ( params.lineBreakType ) {
		case 0:
			lineBreak = Util.LINEBREAK_DOS;
			break;
		case 1:
			lineBreak = Util.LINEBREAK_MAC;
			break;
		default:
			lineBreak = Util.LINEBREAK_UNIX;
			break;
		}
	}

	public int requestInputCount () {
		return 1;
	}

	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
	}

}

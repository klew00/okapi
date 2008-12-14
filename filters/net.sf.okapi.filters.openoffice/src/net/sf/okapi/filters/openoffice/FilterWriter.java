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

package net.sf.okapi.filters.openoffice;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilterWriter;

public class FilterWriter implements IFilterWriter {

	private Parameters params;
	private OutputStreamWriter writer;

	public void close() {
		try {
			if ( writer != null ) {
				writer.close();
				writer = null;
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public String getName () {
		return "OpenOfficeFilterWriter";
	}

	public IParameters getParameters () {
		return params;
	}

	public FilterEvent handleEvent (FilterEvent event) {
		// TODO Auto-generated method stub
		return event;
	}

	public void setOptions (String language,
		String defaultEncoding)
	{
		// TODO Auto-generated method stub
		
	}

	public void setOutput (String path) {
		// TODO Auto-generated method stub
		
	}

	public void setOutput (OutputStream output) {
		// TODO Auto-generated method stub
		
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

}

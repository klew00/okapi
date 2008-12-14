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

package net.sf.okapi.common.encoder;

import java.util.Hashtable;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.IEncoder;

public class EncoderManager implements IEncoder {

	private String mimeType = "";
	private IEncoder encoder;
	private Hashtable<String, String> mimeMap;

	public EncoderManager () {
		mimeMap = new Hashtable<String, String>();
		// Default mapping
		mimeMap.put("text/xml", "net.sf.okapi.common.encoder.XMLEncoder");
		mimeMap.put("text/x-properties", "net.sf.okapi.common.encoder.PropertiesEncoder");
	}

	public void clearMap () {
		mimeMap.clear();
	}
	
	public void addMapping (String mimeType,
		String className)
	{
		mimeMap.put(mimeType, className);
	}
	
	public void removeMapping (String mimeType) {
		mimeMap.remove(mimeType);
	}
	
	public void updateEncoder (String newMimeType) {
		try {
			if ( newMimeType == null ) return;
			// Check if the current encoder is for the same mime-type
			if ( mimeType.compareTo(newMimeType) == 0 ) return;
		
			// If not: lookup what encoder to use
			mimeType = newMimeType;
			String name = mimeMap.get(mimeType);
			if ( name == null ) { // Not in the map, nullify the encoder
				encoder = null;
				return;
			}
			// Else: Instantiate the encoder based on the class name
			encoder = (IEncoder)Class.forName(name).newInstance();
		}
		catch ( InstantiationException e ) {
			throw new RuntimeException(e);
		}
		catch ( IllegalAccessException e ) {
			throw new RuntimeException(e);
		}
		catch ( ClassNotFoundException e ) {
			throw new RuntimeException(e);
		}
	}

	public String encode (String text, int context) {
		if ( encoder != null ) return encoder.encode(text, context);
		else return text;
	}

	public String encode (char value, int context) {
		if ( encoder != null ) return encoder.encode(value, context);
		else return String.valueOf(value);
	}

	public IEncoder getEncoder () {
		return encoder;
	}

	public void setOptions (IParameters params,
		String encoding)
	{
		if ( encoder != null ) {
			encoder.setOptions(params, encoding);
		}
	}

}

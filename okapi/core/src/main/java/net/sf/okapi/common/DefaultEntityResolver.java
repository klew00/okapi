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
===========================================================================*/

package net.sf.okapi.common;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Helper class that implement a default EntityResolver.
 */
public class DefaultEntityResolver implements EntityResolver {

	/**
	 * Resolves a given entity to the input source for an empty XML document.
	 * @param publicID The public ID of the entity.
	 * @param systemID The system ID of the entity.
	 * @return The input source for the resolved entity. This default implementation always returns
	 * the input source for an empty XML document.
	 */
	public InputSource resolveEntity (String publicID, String systemID)
		throws SAXException, IOException
	{
		InputSource source = new InputSource(
			new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		source.setPublicId(publicID);
		source.setSystemId(systemID);
		return source;
	}

}

/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.encoder.tests;

import net.sf.okapi.common.encoder.IEncoder;
import net.sf.okapi.common.encoder.XMLEncoder;
import junit.framework.*;

public class XMLEncoderTest extends TestCase {

	public void testXMLEncoder () {
		XMLEncoder enc = new XMLEncoder();
		
		assertEquals("en", enc.toNative(IEncoder.PROP_LANGUAGE, "en"));
		assertEquals("windows-1252", enc.toNative(IEncoder.PROP_ENCODING, "windows-1252"));
		
		enc.setOptions(null, "us-ascii", "\n");
		assertEquals("&#x20000;", enc.encode("\uD840\uDC00", 0));
		assertEquals("&#x20000;", enc.encode(0x20000, 0));
		assertEquals("abc", enc.encode("abc", 0));
		assertEquals("a", enc.encode((int)'a', 0));

		enc.setOptions(null, "UTF-8", "\n");
		assertEquals(0x20000, enc.encode("\uD840\uDC00", 0).codePointAt(0));
		assertEquals(0x20000, enc.encode(0x20000, 0).codePointAt(0));
		assertEquals("abc", enc.encode("abc", 0));
		assertEquals("a", enc.encode((int)'a', 0));
	}

}

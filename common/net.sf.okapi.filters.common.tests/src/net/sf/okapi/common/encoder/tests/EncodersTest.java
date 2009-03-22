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

import net.sf.okapi.common.encoder.HtmlEncoder;
import net.sf.okapi.common.encoder.PropertiesEncoder;
import net.sf.okapi.common.encoder.XMLEncoder;
import net.sf.okapi.common.resource.Property;
import junit.framework.*;

public class EncodersTest extends TestCase {

	public void testXMLEncoder () {
		XMLEncoder enc = new XMLEncoder();
		
		assertEquals("en", enc.toNative(Property.LANGUAGE, "en"));
		assertEquals("windows-1252", enc.toNative(Property.ENCODING, "windows-1252"));
		
		enc.setOptions(null, "us-ascii", "\n");
		assertEquals("&#x20000;", enc.encode("\uD840\uDC00", 0));
		assertEquals("&#x20000;", enc.encode(0x20000, 0));
		assertEquals("abc", enc.encode("abc", 0));
		assertEquals("a", enc.encode('a', 0));
		assertEquals("a", enc.encode((int)'a', 0));

		enc.setOptions(null, "UTF-8", "\n");
		assertEquals(0x20000, enc.encode("\uD840\uDC00", 0).codePointAt(0));
		assertEquals(0x20000, enc.encode(0x20000, 0).codePointAt(0));
		assertEquals("abc", enc.encode("abc", 0));
		assertEquals("a", enc.encode((int)'a', 0));
		assertEquals("\u00a0", enc.encode("\u00a0", 0));
		assertEquals("\u00a0", enc.encode('\u00a0', 0));
		assertEquals("\u00a0", enc.encode(0x00a0, 0));
		assertEquals(">", enc.encode(">", 0));
		assertEquals(">", enc.encode((int)'>', 0));
		assertEquals(">", enc.encode('>', 0));
		
		DummyParameters params = new DummyParameters();
		params.setBoolean("escapeGt", true);
		params.setBoolean("escapeNbsp", true);
		enc.setOptions(params, "UTF-8", "\n");
		assertEquals("&#x00a0;", enc.encode("\u00a0", 0));
		assertEquals("&#x00a0;", enc.encode('\u00a0', 0));
		assertEquals("&#x00a0;", enc.encode(0x00a0, 0));
		assertEquals("&gt;", enc.encode(">", 0));
		assertEquals("&gt;", enc.encode((int)'>', 0));
		assertEquals("&gt;", enc.encode('>', 0));
	}

	public void testHTMLEncoder () {
		HtmlEncoder enc = new HtmlEncoder();
		
		assertEquals("en", enc.toNative(Property.LANGUAGE, "en"));
		assertEquals("windows-1252", enc.toNative(Property.ENCODING, "windows-1252"));
		
		enc.setOptions(null, "us-ascii", "\n");
		assertEquals("&#x20000;", enc.encode("\uD840\uDC00", 0));
		assertEquals("&#x20000;", enc.encode(0x20000, 0));
		assertEquals("abc", enc.encode("abc", 0));
		assertEquals("a", enc.encode('a', 0));
		assertEquals("a", enc.encode((int)'a', 0));

		enc.setOptions(null, "UTF-8", "\n");
		assertEquals(0x20000, enc.encode("\uD840\uDC00", 0).codePointAt(0));
		assertEquals(0x20000, enc.encode(0x20000, 0).codePointAt(0));
		assertEquals("abc", enc.encode("abc", 0));
		assertEquals("a", enc.encode('a', 0));
		assertEquals("a", enc.encode((int)'a', 0));
	}

	public void testpropertiesEncoder () {
		PropertiesEncoder enc = new PropertiesEncoder();
		
		assertEquals("en", enc.toNative(Property.LANGUAGE, "en"));
		assertEquals("windows-1252", enc.toNative(Property.ENCODING, "windows-1252"));
		
		enc.setOptions(null, "us-ascii", "\n");
		assertEquals("\\ud840\\udc00", enc.encode("\uD840\uDC00", 0));
		assertEquals("\\ud840\\udc00", enc.encode(0x20000, 0));
		assertEquals("abc", enc.encode("abc", 0));
		assertEquals("a", enc.encode('a', 0));
		assertEquals("a", enc.encode((int)'a', 0));

		enc.setOptions(null, "UTF-8", "\n");
		assertEquals(0x20000, enc.encode("\uD840\uDC00", 0).codePointAt(0));
		assertEquals(0x20000, enc.encode(0x20000, 0).codePointAt(0));
		assertEquals("abc", enc.encode("abc", 0));
		assertEquals("a", enc.encode('a', 0));
		assertEquals("a", enc.encode((int)'a', 0));
	}

}

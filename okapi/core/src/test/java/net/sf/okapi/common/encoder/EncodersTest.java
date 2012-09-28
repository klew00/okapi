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
===========================================================================*/

package net.sf.okapi.common.encoder;

import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.encoder.DTDEncoder;
import net.sf.okapi.common.encoder.DefaultEncoder;
import net.sf.okapi.common.encoder.HtmlEncoder;
import net.sf.okapi.common.encoder.PropertiesEncoder;
import net.sf.okapi.common.encoder.XMLEncoder;
import net.sf.okapi.common.filters.DummyFilter;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.resource.Property;

import org.junit.Test;
import static org.junit.Assert.*;

public class EncodersTest {

	@Test
	public void testXMLEncoder () {
		XMLEncoder enc = new XMLEncoder();
		
		assertEquals("en", enc.toNative(Property.LANGUAGE, "en"));
		assertEquals("windows-1252", enc.toNative(Property.ENCODING, "windows-1252"));
		
		enc.setOptions(null, "us-ascii", "\n");
		assertEquals("&#x20000;", enc.encode("\uD840\uDC00", EncoderContext.TEXT));
		assertEquals("&#x20000;", enc.encode(0x20000, EncoderContext.TEXT));
		assertEquals("abc", enc.encode("abc", EncoderContext.TEXT));
		assertEquals("a", enc.encode('a', EncoderContext.TEXT));
		assertEquals("a", enc.encode((int)'a', EncoderContext.TEXT));

		enc.setOptions(null, "UTF-8", "\n");
		assertEquals(0x20000, enc.encode("\uD840\uDC00", EncoderContext.TEXT).codePointAt(0));
		assertEquals(0x20000, enc.encode(0x20000, EncoderContext.TEXT).codePointAt(0));
		assertEquals("abc", enc.encode("abc", EncoderContext.TEXT));
		assertEquals("a", enc.encode((int)'a', EncoderContext.TEXT));
		assertEquals("\u00a0", enc.encode("\u00a0", EncoderContext.TEXT));
		assertEquals("\u00a0", enc.encode('\u00a0', EncoderContext.TEXT));
		assertEquals("\u00a0", enc.encode(0x00a0, EncoderContext.TEXT));
		assertEquals(">", enc.encode(">", EncoderContext.TEXT));
		assertEquals(">", enc.encode((int)'>', EncoderContext.TEXT));
		assertEquals(">", enc.encode('>', EncoderContext.TEXT));
		
		DummyParameters params = new DummyParameters();
		params.setBoolean(XMLEncoder.ESCAPEGT, true);
		params.setBoolean(XMLEncoder.ESCAPENBSP, true);
		enc.setOptions(params, "UTF-8", "\n");
		assertEquals("&#x00a0;", enc.encode("\u00a0", EncoderContext.TEXT));
		assertEquals("&#x00a0;", enc.encode('\u00a0', EncoderContext.TEXT));
		assertEquals("&#x00a0;", enc.encode(0x00a0, EncoderContext.TEXT));
		assertEquals("&gt;", enc.encode(">", EncoderContext.TEXT));
		assertEquals("&gt;", enc.encode((int)'>', EncoderContext.TEXT));
		assertEquals("&gt;", enc.encode('>', EncoderContext.TEXT));
	}

	@Test
	public void testHTMLEncoder () {
		HtmlEncoder enc = new HtmlEncoder();
		
		assertEquals("en", enc.toNative(Property.LANGUAGE, "en"));
		assertEquals("windows-1252", enc.toNative(Property.ENCODING, "windows-1252"));
		
		enc.setOptions(null, "us-ascii", "\n");
		assertEquals("&#x20000;", enc.encode("\uD840\uDC00", EncoderContext.TEXT));
		assertEquals("&#x20000;", enc.encode(0x20000, EncoderContext.TEXT));
		assertEquals("abc", enc.encode("abc", EncoderContext.TEXT));
		assertEquals("a", enc.encode('a', EncoderContext.TEXT));
		assertEquals("a", enc.encode((int)'a', EncoderContext.TEXT));

		enc.setOptions(null, "UTF-8", "\r");
		assertEquals(0x20000, enc.encode("\uD840\uDC00", EncoderContext.TEXT).codePointAt(0));
		assertEquals(0x20000, enc.encode(0x20000, EncoderContext.TEXT).codePointAt(0));
		assertEquals("abc\r", enc.encode("abc\n", EncoderContext.TEXT));
		assertEquals("a", enc.encode('a', EncoderContext.TEXT));
		assertEquals("a", enc.encode((int)'a', EncoderContext.TEXT));
	}

	@Test
	public void testPropertiesEncoder () {
		PropertiesEncoder enc = new PropertiesEncoder();
		
		assertEquals("en", enc.toNative(Property.LANGUAGE, "en"));
		assertEquals("windows-1252", enc.toNative(Property.ENCODING, "windows-1252"));
		
		enc.setOptions(null, "us-ascii", "\n");
		assertEquals("\\ud840\\udc00", enc.encode("\uD840\uDC00", EncoderContext.TEXT));
		assertEquals("\\ud840\\udc00", enc.encode(0x20000, EncoderContext.TEXT));
		assertEquals("abc", enc.encode("abc", EncoderContext.TEXT));
		assertEquals("a", enc.encode('a', EncoderContext.TEXT));
		assertEquals("a", enc.encode((int)'a', EncoderContext.TEXT));

		enc.setOptions(null, "UTF-8", "\n");
		assertEquals(0x20000, enc.encode("\uD840\uDC00", EncoderContext.TEXT).codePointAt(0));
		assertEquals(0x20000, enc.encode(0x20000, EncoderContext.TEXT).codePointAt(0));
		assertEquals("abc\\n", enc.encode("abc\n", EncoderContext.TEXT));
		assertEquals("a", enc.encode('a', EncoderContext.TEXT));
		assertEquals("a", enc.encode((int)'a', EncoderContext.TEXT));
	}

	@Test
	public void testDefaultEncoder () {
		DefaultEncoder enc = new DefaultEncoder();
		
		assertEquals("en", enc.toNative(Property.LANGUAGE, "en"));
		assertEquals("windows-1252", enc.toNative(Property.ENCODING, "windows-1252"));
		
		enc.setOptions(null, "us-ascii", "\r\n");
		assertEquals("\uD840\uDC00", enc.encode("\uD840\uDC00", EncoderContext.TEXT));
		assertEquals("\uD840\uDC00", enc.encode(0x20000, EncoderContext.TEXT));
		assertEquals("abc\r\n", enc.encode("abc\n", EncoderContext.TEXT));
		assertEquals("a", enc.encode('a', EncoderContext.TEXT));
		assertEquals("a", enc.encode((int)'a', EncoderContext.TEXT));

		enc.setOptions(null, "UTF-8", "\r");
		assertEquals(0x20000, enc.encode("\uD840\uDC00", EncoderContext.TEXT).codePointAt(0));
		assertEquals(0x20000, enc.encode(0x20000, EncoderContext.TEXT).codePointAt(0));
		assertEquals("abc\r", enc.encode("abc\n", EncoderContext.TEXT));
		assertEquals("a", enc.encode('a', EncoderContext.TEXT));
		assertEquals("a", enc.encode((int)'a', EncoderContext.TEXT));
	}

	@Test
	public void testDTDEncoder () {
		DTDEncoder enc = new DTDEncoder();
		enc.setOptions(null, "us-ascii", "\n");
		assertEquals("&lt;&amp;&#37;", enc.encode("<&%", EncoderContext.TEXT));
		assertEquals("&#x20000;", enc.encode("\uD840\uDC00", EncoderContext.TEXT));
		assertEquals("&#x20000;", enc.encode(0x20000, EncoderContext.TEXT));
		assertEquals("&#37;", enc.encode('%', EncoderContext.TEXT));
		assertEquals("&#37;", enc.encode((int)'%', EncoderContext.TEXT));
	}

	@Test
	public void changeEncoderTest () {
		IFilter filter = new DummyFilter();
		EncoderManager em1 = filter.getEncoderManager();
		assertNotNull(em1);
		em1.setDefaultOptions(null, "UTF-8", "\n");
		em1.updateEncoder(MimeTypeMapper.XML_MIME_TYPE);
		assertEquals("net.sf.okapi.common.encoder.XMLEncoder", em1.getEncoder().getClass().getName());
		
		GenericFilterWriter gfw = new GenericFilterWriter(filter.createSkeletonWriter(),
			filter.getEncoderManager());
		EncoderManager em2 = gfw.getEncoderManager();
		assertSame(em1, em2);

		// Bogus MIME type result in default encoder 
		em2.updateEncoder("bogus");
		assertEquals("net.sf.okapi.common.encoder.DefaultEncoder", em2.getEncoder().getClass().getName());
		
		// Now change the mapping
		em2.setMapping(MimeTypeMapper.XML_MIME_TYPE, "net.sf.okapi.common.encoder.PropertiesEncoder");
		em2.updateEncoder(MimeTypeMapper.XML_MIME_TYPE);
		assertEquals("net.sf.okapi.common.encoder.PropertiesEncoder", em2.getEncoder().getClass().getName());
		assertEquals("net.sf.okapi.common.encoder.PropertiesEncoder", em1.getEncoder().getClass().getName());
	}
	
}

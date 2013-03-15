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

package net.sf.okapi.common;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import static org.junit.Assert.*;

public class Base64Test {

	@Test
	public void testSimpleConversion () {
		String text = "This is a test.";
		String res = Base64.encodeString(text);
		assertEquals(text, Base64.decodeString(res));
	}
	
	@Test
	public void testExtendedCharacters () {
		String text = "\u00a0\u00c6\u9151\uff3b\uffed\u2605";
		String res = Base64.encodeString(text);
		assertEquals(text, Base64.decodeString(res));
	}

	@Test
	public void testMixedCharacters () {
		String text = "\u00a0\u00c6<>&%$#@!Abxz\u9151[]\uff3b\uffed\u2605";
		String res = Base64.encodeString(text);
		assertEquals(text, Base64.decodeString(res));
	}

	@Test
	public void testLineBreaks () {
		String text = "\u00a0\n\u00c6\rABC\r\nEnd";
		String res = Base64.encodeString(text);
		assertEquals(text, Base64.decodeString(res));
	}

	@Test
	public void testUTF8Bytes () throws UnsupportedEncodingException {
		String text = "\u00a0\u00c6";
		String res = Base64.encodeString(text);
		byte[] expected = text.getBytes("UTF8");
		byte[] output  = Base64.decode(res);
		assertEquals(expected.length, output.length);
		for ( int i=0; i<expected.length; i++ ) {
			assertEquals(expected[i], output[i]);
		}
	}

	@Test
	public void testLongBlock () {
		String text = "[This is a long text that may need to be wrapped onto several lines.]";
		//String res = Base64.encodeString(text);
		String wrapped = "W1RoaXMgaXMg\r\nYSBsb25nIHRleHQgdGhhdC\rBtYXkgb\n\n\nmVlZCB0byBiZSB3cmFwcGVkIG9udG8gc2V\n"
			+ "2ZXJhbCBsaW5lcy5d";
		assertEquals(text, Base64.decodeString(wrapped));
	}

    @Test(expected = RuntimeException.class)
	public void testBadInput () {
    	// Input not in a length multiple of 4
		Base64.decode("123");
	}

}

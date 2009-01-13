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

package net.sf.okapi.applications.rainbow.packages.rtf;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.writer.ILayerProvider;

public class LayerProvider implements ILayerProvider {

	private CharsetEncoder outputEncoder;

	public String endCode () {
		return "}";
	}

	public String endInline () {
		return "}";
	}

	public String startCode () {
		return "{\\cs5\\f1\\cf15\\lang1024 ";
	}

	public String startInline () {
		return "{\\cs6\\f1\\cf6\\lang1024 ";
	}
	
	public String startSegment () {
		return "{\\cs15\\v\\cf12\\sub\\f2 \\{0>}{\\v\\f1 ";
	}
	
	public String endSegment () {
		return "{\\cs15\\v\\cf12\\sub\\f2 <0\\}}";
	}
	
	public String midSegment (int leverage) {
		return String.format("%s%d%s", "}{\\cs15\\v\\cf12\\sub\\f2 <\\}", leverage, "\\{>}");
	}
	
	// context: 0=in text, 1=in skeleton, 2=in inline
	public String encode (String text, int context) {
		//TODO: change to better faster support
		return Util.escapeToRTF(text, true, context, outputEncoder);
	}

	public String encode (char value, int context) {
		//TODO: change to better faster support
		return Util.escapeToRTF(String.valueOf(value), true, context, outputEncoder);
	}

	public void setOptions (IParameters params,
		String encoding)
	{
		outputEncoder = Charset.forName(encoding).newEncoder();
	}

	// Not used for a layer provider
	public String toNative(String propertyName, String value) {
		return value; // No modification
	}

}

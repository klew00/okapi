/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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
import net.sf.okapi.common.encoder.EncoderContext;
import net.sf.okapi.common.filterwriter.ILayerProvider;

public class LayerProvider implements ILayerProvider {

	private CharsetEncoder outputEncoder;
	private String lineBreak;

	@Override
	public String endCode () {
		return "}";
	}

	@Override
	public String endInline () {
		return "}";
	}

	@Override
	public String startCode () {
		return "{\\cs5\\f1\\cf15\\lang1024 ";
	}

	@Override
	public String startInline () {
		return "{\\cs6\\f1\\cf6\\lang1024 ";
	}
	
	@Override
	public String startSegment () {
		return "{\\cs15\\v\\cf12\\sub\\f2 \\{0>}{\\v\\f1 ";
	}
	
	@Override
	public String endSegment () {
		return "{\\cs15\\v\\cf12\\sub\\f2 <0\\}}";
	}
	
	@Override
	public String midSegment (int leverage) {
		return String.format("%s%d%s", "}{\\cs15\\v\\cf12\\sub\\f2 <\\}", leverage, "\\{>}");
	}
	
	// Context: 0=in text, 1=in skeleton, 2=in inline
	@Override
	public String encode (String text,
			EncoderContext context)
	{
		// Context here can be used for lineBreak type
		return Util.escapeToRTF(text, true, context.ordinal(), outputEncoder);
	}

	@Override
	public String encode (char value,
			EncoderContext context)
	{
		// Context here can be used for lineBreak type
		return Util.escapeToRTF(String.valueOf(value), true, context.ordinal(), outputEncoder);
	}

	@Override
	public String encode (int value,
			EncoderContext context)
	{
		// Context here can be used for lineBreak type
		if ( Character.isSupplementaryCodePoint(value) ) {
			return Util.escapeToRTF(new String(Character.toChars(value)),
				true, context.ordinal(), outputEncoder);
		}
		return Util.escapeToRTF(String.valueOf((char)value),
			true, context.ordinal(), outputEncoder);
	}

	@Override
	public void setOptions (IParameters params,
		String encoding,
		String lineBreak)
	{
		outputEncoder = Charset.forName(encoding).newEncoder();
		this.lineBreak = lineBreak;
	}

	@Override
	public String toNative(String propertyName,
		String value)
	{
		// No modification: The layer provider does not change the value
		return value;
	}

	@Override
	public String getLineBreak () {
		return lineBreak;
	}

	@Override
	public CharsetEncoder getCharsetEncoder () {
		return outputEncoder;
	}

}

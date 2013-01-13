/*===========================================================================
  Copyright (C) 2013 by the Okapi Framework contributors
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

package net.sf.okapi.filters.xml;

import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.encoder.EncoderContext;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements ISkeletonWriter for the ITS filters 
 */
public class XMLSkeletonWriter extends GenericSkeletonWriter {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Gets the original content of a TextFragment.
	 * @param tf the TextFragment to process.
	 * @param locToUse locale to output. Use null for the source, or the locale
	 * for the target locales. This is used for referenced content in inline codes.
	 * @param context Context flag: 0=text, 1=skeleton, 2=inline.
	 * @return The string representation of the text unit content.
	 */
	@Override
	public String getContent (TextFragment tf,
		LocaleId locToUse,
		EncoderContext context)
	{
		// Output simple text
		if ( !tf.hasCode() ) {
			if ( encoderManager == null ) {
				if ( layer == null ) {
					return tf.toText();
				}
				else {
					return layer.encode(tf.toText(), context);
				}
			}
			else {
				if ( layer == null ) {
					return encoderManager.encode(tf.toText(), context);
				}
				else {
					return layer.encode(
						encoderManager.encode(tf.toText(), context), context);
				}
			}
		}

		// Output text with in-line codes
		List<Code> codes = tf.getCodes();
		StringBuilder tmp = new StringBuilder();
		String text = tf.getCodedText();
		Code code;
		char ch;
		for ( int i=0; i<text.length(); i++ ) {
			ch = text.charAt(i);
			switch ( ch ) {
			case TextFragment.MARKER_OPENING:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				tmp.append(expandCodeContent(code, locToUse, context));
				break;
			case TextFragment.MARKER_CLOSING:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				tmp.append(expandCodeContent(code, locToUse, context));
				break;
			case TextFragment.MARKER_ISOLATED:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				tmp.append(expandCodeContent(code, locToUse, context));
				break;
			default:
				if ( Character.isHighSurrogate(ch) ) {
					int cp = text.codePointAt(i);
					i++; // Skip low-surrogate
					if ( encoderManager == null ) {
						if ( layer == null ) {
							tmp.append(new String(Character.toChars(cp)));
						}
						else {
							tmp.append(layer.encode(cp, context));
						}
					}
					else {
						if ( layer == null ) {
							tmp.append(encoderManager.encode(cp, context));
						}
						else {
							tmp.append(layer.encode(
								encoderManager.encode(cp, context),
								context));
						}
					}
				}
				else { // Non-supplemental case
					if ( encoderManager == null ) {
						if ( layer == null ) {
							tmp.append(ch);
						}
						else {
							tmp.append(layer.encode(ch, context));
						}
					}
					else {
						if ( layer == null ) {
							tmp.append(encoderManager.encode(ch, context));
						}
						else {
							tmp.append(layer.encode(
								encoderManager.encode(ch, context),
								context));
						}
					}
				}
				break;
			}
		}
		return tmp.toString();
	}

}

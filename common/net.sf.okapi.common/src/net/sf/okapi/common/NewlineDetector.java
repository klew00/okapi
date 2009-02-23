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
import java.io.Reader;

public final class NewlineDetector {
	public static final String CR = "\r";
	public static final String LF = "\n";
	public static final String CRLF = "\r\n";

	/**
	 * Returns the <a target="_blank"
	 * href="http://en.wikipedia.org/wiki/Newline">newline</a> character
	 * sequence used in the source document.
	 * <p>
	 * If the document does not contain any newline characters, this method
	 * returns <code>null</code>.
	 * <p>
	 * The three possible return values (aside from <code>null</code>) are
	 * <code>"\n"</code>, <code>"\r\n"</code> and <code>"\r"</code>.
	 * 
	 * @return the <a target="_blank"
	 *         href="http://en.wikipedia.org/wiki/Newline">newline</a> character
	 *         sequence used in the source document, or <code>null</code> if
	 *         none is present.
	 */
	public static String getNewLine(String text) {
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == '\n')
				return LF;
			if (ch == '\r')
				return (++i < text.length() && text.charAt(i) == '\n') ? CRLF : CR;
		}
		return null;
	}

	public static String getNewLine(Reader reader) {
		char c;
		try {
			while ((c = (char) reader.read()) != -1) {
				if (c == '\n')
					return LF;
				if (c == '\r') {
					char c2 = (char) reader.read();
					if (c2 == -1)
						return CR;
					else
						return (reader.read() == '\n') ? CRLF : CR;
				}
			}
		} catch (IOException e) {
			return null;
		}

		return null;
	}

	public static String getBestGuessNewLine(String text) {
		final String newLine = getNewLine(text);
		if (newLine != null)
			return newLine;
		return System.getProperty("line.separator");
	}
}

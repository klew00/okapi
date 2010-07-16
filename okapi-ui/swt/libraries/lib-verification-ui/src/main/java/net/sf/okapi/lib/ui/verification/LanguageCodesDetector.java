/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.lib.ui.verification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.BOMAwareInputStream;

/**
 * General purpose utility to try to detect the source and possibly target languages 
 * defined in a few file formats such as TMX, XLIFF, TTX, etc.
 * The results must be seen as a <b>guess</p>.
 */
public class LanguageCodesDetector {

	/**
	 * Tries to guess the language(s) declared in the given input file.
	 * @param path the full path of the file to process.
	 * @return a list of strings that can be empty (never null). The first string is the possible source
	 * language, the next strings are the potential target languages. 
	 */
	public List<String> guessLanguages (String path) {
		ArrayList<String> list = new ArrayList<String>();
		BufferedReader reader = null;
		
		try {
			// Deal with the potential BOM
			String encoding = Charset.defaultCharset().name();
			BOMAwareInputStream bis = new BOMAwareInputStream(new FileInputStream(path), encoding);
			encoding = bis.detectEncoding();
			
			// Open the input document with BOM-aware reader
			reader = new BufferedReader(new InputStreamReader(bis, encoding));
			
			// Read the top of the file
			char[] buf = new char[2048];
			int n = reader.read(buf);
			if ( n == -1 ) return list; // Empty file: no languages
			String trgValue = null;
			
			// Else: Try the detect the language codes
			// For XLIFF: source-language, xml:lang, lang, target-language
			// For TMX: srcLang, xml:lang, lang
			// For TTX: SourceLanguage, TargetLanguage, Lang
			// For TS: sourcelanguage, language
			// Note: the order matter: target cases should be last
			Pattern pattern = Pattern.compile(
				"\\s(srclang|source-?language|xml:lang|lang|(target-?)?language)\\s*?=\\s*?['\"](.*?)['\"]",
				Pattern.CASE_INSENSITIVE);
			Matcher m = pattern.matcher(new String(buf));
			int pos = 0;
			while ( m.find(pos) ) {
				String lang = m.group(3).toLowerCase();
				if ( lang.isEmpty() ) continue;
				String name = m.group(1).toLowerCase();
				
				// If we have a header-type target declaration
				if ( name.equalsIgnoreCase("language") || name.startsWith("target") ) {
					if ( list.isEmpty() ) {
						// Note that we don't do anything to handle a second match, but that should be OK
						trgValue = lang;
						pos = m.end();
						continue; // Move to the next
					}
					// Else: we can add to the normal list as the source is defined already
				}
				
				// Else: add the language
				if ( !list.contains(lang) ) {
					list.add(lang);
				}
				// Then check if we have a target to add. This will be done only once.
				if ( trgValue != null ) {
					// Add the target
					list.add(trgValue);
					trgValue = null;
				}
				pos = m.end();
			}
		}
		catch ( Throwable e ) {
			new RuntimeException("Error while trying to guess language information.\n"+e.getLocalizedMessage());
		}
		finally {
			if ( reader != null ) {
				try {
					reader.close();
				}
				catch ( IOException e ) {
					// Swallow this error
				}
			}
		}
		return list;
	}
	
}

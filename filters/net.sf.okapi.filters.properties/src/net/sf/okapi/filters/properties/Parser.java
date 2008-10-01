/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.properties;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.BOMAwareInputStream;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IParser;
import net.sf.okapi.common.resource.IContainable;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;

public class Parser implements IParser {

	private static final int RESULT_ERROR        = 0;
	private static final int RESULT_END          = 1;
	private static final int RESULT_ITEM         = 2;
	private static final int RESULT_DATA         = 3;
	
	private static final int NEXTACTION_TRANSUNIT     = 0;
	private static final int NEXTACTION_ENDINPUT      = 1;
	
	protected Resource       resource;
	
	private SkeletonUnit     lbSkel;
	private int              nextAction;
	private IContainable     currentRes;
	private BufferedReader   reader;
	private TextUnit         item;
	private String           textLine;
	private int              lineNumber;
	private int              lineSince;
	private long             position;
	private int              itemID;
	private int              sklID;
	private Pattern          keyConditionPattern;
	private final Logger     logger = LoggerFactory.getLogger("net.sf.okapi.logging");
	private boolean          stop;
	private String           lineBreak;

	public Parser () {
		resource = new Resource();
	}
	
	public void close () {
		try {
			if ( reader != null ) {
				reader.close();
				reader = null;
			}
		}
		catch ( IOException e) {
			throw new RuntimeException(e);
		}
	}

	public IContainable getResource () {
		return currentRes;
	}

	public void open (InputStream input)
	{
		try {
			stop = false;
			// Open the input reader from the provided stream
			BOMAwareInputStream bis = new BOMAwareInputStream(input,
				resource.getSourceEncoding());
			reader = new BufferedReader(
				new InputStreamReader(bis, bis.detectEncoding()));
			
			// Initializes the variables
			lineBreak = "\n"; //TODO: Auto-detection of line-break type or at least by platform
			lbSkel = new SkeletonUnit("after", lineBreak); 
			itemID = 0;
			sklID = 0;
			lineNumber = 0;
			lineSince = 0;
			position = 0;
			nextAction = -1;
			// Compile conditions
			if ( resource.params.useKeyCondition ) {
				keyConditionPattern = Pattern.compile(resource.params.keyCondition); 
			}
			else {
				keyConditionPattern = null;
			}
			// Compile code finder rules
			if ( resource.params.useCodeFinder ) {
				resource.params.codeFinder.compile();
			}
		}
		catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void open (CharSequence input) {
		//TODO: Check for better solution, going from char to byte to read char is just not good
		open(new ByteArrayInputStream(input.toString().getBytes())); 
	}

	public void open (URL input) {
		try { //TODO: Make sure this is actually working (encoding?, etc.)
			open(input.openStream());
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public ParserTokenType parseNext () {
		if ( stop ) {
			nextAction = -1;
			return ParserTokenType.ENDINPUT;
		}
		// Deal with next action
		switch ( nextAction ) {
		case NEXTACTION_TRANSUNIT:
			currentRes = item;
			nextAction = -1;
			return ParserTokenType.TRANSUNIT;
		case NEXTACTION_ENDINPUT:
			currentRes = null;
			nextAction = -1;
			return ParserTokenType.ENDINPUT;
		}
		
		// Continue the parsing
		int n;
		boolean resetBuffer = true;
		do {
			switch ( n = readItem(resetBuffer) ) {
			case RESULT_DATA:
				// Don't send the skeleton chunk now, wait for the complete one
				resetBuffer = false;
				break;
			case RESULT_ITEM:
				nextAction = NEXTACTION_TRANSUNIT;
				currentRes = resource.sklRes;
				return ParserTokenType.SKELETON;
			default:
				resetBuffer = true;
				break;
			}
		} while ( n > RESULT_END );
		
		nextAction = NEXTACTION_ENDINPUT;
		currentRes = resource.sklRes;
		return ParserTokenType.SKELETON;
	}

	private int readItem (boolean resetBuffer) {
		int result = RESULT_ERROR;
		try {
			if ( resetBuffer ) {
				resource.sklRes = new SkeletonUnit();
				resource.sklRes.setID(String.format("s%d", ++sklID));
			}
			
			StringBuilder keyBuffer = new StringBuilder();
			StringBuilder textBuffer = new StringBuilder();
			String value = "";
			String key = "";
			boolean isMultiline = false;
			int startText = 0;
			long lS = -1;

			while ( true ) {
				if ( !getNextLine() ) {
					return RESULT_END;
				}
				// Else: process the line

				// Remove any leading white-spaces
				String tmp = Util.trimStart(textLine, "\t\r\n \f");

				if ( isMultiline ) {
					value += tmp;
				}
				else {
					// Empty lines
					if ( tmp.length() == 0 ) {
						resource.sklRes.appendData(textLine);
						resource.sklRes.appendData(lineBreak);
						continue;
					}

					// Comments
					boolean isComment = (( tmp.charAt(0) == '#' ) || ( tmp.charAt(0) == '!' ));
					if ( !isComment &&  resource.params.extraComments ) {
						isComment = (tmp.charAt(0) == ';'); // .NET style
						if ( tmp.startsWith("//") ) isComment = true; // C++/Java-style
					}

					if ( isComment ) {
						resource.params.locDir.process(tmp);
						resource.sklRes.appendData(textLine);
						resource.sklRes.appendData(lineBreak);
						continue;
					}

					// Get the key
					boolean bEscape = false;
					int n = 0;
					for ( int i=0; i<tmp.length(); i++ ) {
						if ( bEscape ) bEscape = false;
						else {
							if ( tmp.charAt(i) == '\\' ) {
								bEscape = true;
								continue;
							}
							if (( tmp.charAt(i) == ':' ) || ( tmp.charAt(i) == '=' )
								|| ( Character.isWhitespace(tmp.charAt(i)) )) {
								// That the first white-space after the key
								n = i;
								break;
							}
						}
					}

					// Get the key
					if ( n == 0 ) {
						// Line empty after the key
						n = tmp.length();
					}
					key = tmp.substring(0, n);

					// Gets the value
					boolean bEmpty = true;
					boolean bCheckEqual = true;
					for ( int i=n; i<tmp.length(); i++ ) {
						if ( bCheckEqual && (( tmp.charAt(i) == ':' )
							|| ( tmp.charAt(i) == '=' ))) {
							bCheckEqual = false;
							continue;
						}
						if ( !Character.isWhitespace(tmp.charAt(i)) ) {
							// That the first white-space after the key
							n = i;
							bEmpty = false;
							break;
						}
					}

					if ( bEmpty ) n = tmp.length();
					value = tmp.substring(n);
					// Real text start point (adjusted for trimmed characters)
					startText = n + (textLine.length() - tmp.length());
					// Use m_nLineSince-1 to not count the current one
					lS = (position-(textLine.length()+(lineSince-1))) + startText;
					lineSince = 0; // Reset the line counter for next time
				}

				// Is it a multi-lines entry?
				if ( value.endsWith("\\") ) {
					// Make sure we have an odd number of ending '\'
					int n = 0;
					for ( int i=value.length()-1;
						(( i > -1 ) && ( value.charAt(i) == '\\' ));
						i-- ) n++;

					if ( (n % 2) != 0 ) { // Continue onto the next line
						value = value.substring(0, value.length()-1);
						isMultiline = true;
						// Preserve parsed text in case we do not extract
						if ( keyBuffer.length() == 0 ) {
							keyBuffer.append(textLine.substring(0, startText));
							startText = 0; // Next time we get the whole line
						}
						textBuffer.append(textLine.substring(startText));
						continue; // Read next line
					}
				}

				// Check for key condition
				// Directives overwrite the key condition
				boolean extract = true;
				if ( resource.params.locDir.isWithinScope() ) {
					extract = resource.params.locDir.isLocalizable(true);
				}
				else { // Check for key condition
					if (  keyConditionPattern != null ) {
						if ( resource.params.extractOnlyMatchingKey ) {
							if ( !keyConditionPattern.matcher(key).matches() )
								extract = false;
						}
						else { // Extract all but items with matching keys
							if ( keyConditionPattern.matcher(key).matches() )
								extract = false;
						}
					}
				}

				if ( extract ) {
					item = new TextUnit(String.valueOf(++itemID),
						unescape(value));
					item.setName(key);
					item.setPreserveWhitespaces(true);
				}

				if ( extract ) {
					// Parts before the text
					if ( keyBuffer.length() == 0 ) {
						// Single-line case
						keyBuffer.append(textLine.substring(0, startText));
					}
					resource.sklRes.appendData(keyBuffer);
					// Line-break
					item.setSkeletonAfter(lbSkel);
				}
				else {
					resource.sklRes.appendData(keyBuffer);
					resource.sklRes.appendData(textBuffer);
					resource.sklRes.appendData(textLine);
					resource.sklRes.appendData(lineBreak);
					return RESULT_DATA;
				}

				result = RESULT_ITEM;
				item.setProperty("start", String.valueOf(lS));

//TODO: handle {0,choice...} cases
//http://java.sun.com/j2se/1.4.2/docs/api/java/text/MessageFormat.html
				if ( resource.params.useCodeFinder )
					resource.params.codeFinder.process(item.getSourceContent());

				return result;
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the next line of the string or file input.
	 * @return True if there was a line to read,
	 * false if this is the end of the input.
	 */
	private boolean getNextLine ()
		throws IOException
	{
		while ( true ) {
			textLine = reader.readLine();
			if ( textLine != null ) {
				lineNumber++;
				lineSince++;
				// We count char instead of byte, while the BaseStream.Length is in byte
				// Not perfect, but better than nothing.
				position += textLine.length() + 1; // +1 For the line-break //TODO: lb size could be 2
			}
			return (textLine != null);
		}
	}

	/**
	 * Un-escapes slash-u+HHHH characters in a string.
	 * @param text The string to convert.
	 * @return The converted string.
	 */
	//TODO: Deal with escape ctrl, etc. \n should be converted
	private String unescape (String text) {
		if ( text.indexOf('\\') == -1 ) return text;
		StringBuilder tmpText = new StringBuilder();
		for ( int i=0; i<text.length(); i++ ) {
			if ( text.charAt(i) == '\\' ) {
				switch ( text.charAt(i+1) ) {
				case 'u':
					if ( i+5 < text.length() ) {
						try {
							int nTmp = Integer.parseInt(text.substring(i+2, i+6), 16);
							tmpText.append((char)nTmp);
						}
						catch ( Exception e ) {
							logMessage(Level.WARNING,
								String.format(Res.getString("INVALID_UESCAPE"),
								text.substring(i+2, i+6)));
						}
						i += 5;
						continue;
					}
					else {
						logMessage(Level.WARNING,
							String.format(Res.getString("INVALID_UESCAPE"),
							text.substring(i+2)));
					}
					break;
				case '\\':
					tmpText.append("\\\\");
					i++;
					continue;
				case 'n':
					tmpText.append("\n");
					i++;
					continue;
				case 't':
					tmpText.append("\t");
					i++;
					continue;
				}
			}
			else tmpText.append(text.charAt(i));
		}
		return tmpText.toString();
	}

	private void logMessage (Level level,
		String text)
	{
		if ( level == Level.WARNING ) {
			logger.warn(String.format(Res.getString("LINE_LOCATION"), lineNumber) + text);
		}
		else if ( level == Level.SEVERE ) {
			logger.error(String.format(Res.getString("LINE_LOCATION"), lineNumber) + text);
		}
		else {
			logger.info(String.format(Res.getString("LINE_LOCATION"), lineNumber) + text);
		}
	}

	public void cancel () {
		stop = true;
	}
}

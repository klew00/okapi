/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.properties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.BOMAwareInputStream;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IInputFilter;
import net.sf.okapi.common.pipeline.IResourceBuilder;
import net.sf.okapi.common.resource.Container;
import net.sf.okapi.common.resource.ExtractionItem;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.SkeletonResource;

public class InputFilter implements IInputFilter {

	public static final int RESULT_ERROR         = 0;
	public static final int RESULT_CANCELLATION  = 1;
	public static final int RESULT_END           = 2;
	public static final int RESULT_ITEM          = 3;
	public static final int RESULT_DATA          = 4;
	
	private InputStream      input;
	private BufferedReader   reader;
	private IResourceBuilder output;
	private Resource         res;
	private IExtractionItem  item;
	private String           textLine;
	private int              lineNumber;
	private int              lineSince;
	private long             position;
	private int              itemID;
	private int              sklID;
	private final Logger     logger = LoggerFactory.getLogger("net.sf.okapi.logging");


	public InputFilter () {
		res = new Resource();
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

	public IParameters getParameters () {
		return res.params;
	}
	
	public void initialize (InputStream input,
		String name,
		String filterSettings,
		String encoding,
		String sourceLanguage,
		String targetLanguage)
	{
		close();
		this.input = input;
		res.setName(name);
		res.setFilterSettings(filterSettings);
		res.setSourceEncoding(encoding);
		res.setSourceLanguage(sourceLanguage);
		res.setTargetLanguage(targetLanguage);
		//TODO: Get the real target/output encoding from parameters
		res.setTargetEncoding(encoding);
	}

	private int readItem (boolean resetBuffer) {
		int result = RESULT_ERROR;
		try {
			if ( resetBuffer ) {
				res.sklRes = new SkeletonResource();
				res.sklRes.setID(String.format("s%d", ++sklID));
			}
			
			StringBuilder keyBuffer = new StringBuilder();
			StringBuilder textBuffer = new StringBuilder();
			String sValue = "";
			String sKey = "";
			boolean isMultiline = false;
			int nStartText = 0;
			long lS = -1;

			while ( true ) {
				if ( !getNextLine() ) {
					if ( !res.sklRes.isEmpty() ) {
						// Some data still to pass along
						res.endingLB = false; // No ending line-break;
					}
					return RESULT_END;
				}
				// Else: process the line

				// Remove any leading white-spaces
				String sTmp = Util.trimStart(textLine, "\t\r\n \f");

				if ( isMultiline ) {
					sValue += sTmp;
				}
				else {
					// Empty lines
					if ( sTmp.length() == 0 ) {
						res.sklRes.appendData(textLine);
						res.sklRes.appendData(res.lineBreak);
						continue;
					}

					// Comments
					boolean isComment = (( sTmp.charAt(0) == '#' ) || ( sTmp.charAt(0) == '!' ));
					if ( !isComment &&  res.params.extraComments ) {
						isComment = (sTmp.charAt(0) == ';'); // .NET style
						if ( sTmp.startsWith("//") ) isComment = true; // C++/Java-style
					}

					if ( isComment ) {
//TODO						m_Opt.m_LD.process(sTmp);
						res.sklRes.appendData(textLine);
						res.sklRes.appendData(res.lineBreak);
						continue;
					}

					// Get the key
					boolean bEscape = false;
					int n = 0;
					for ( int i=0; i<sTmp.length(); i++ ) {
						if ( bEscape ) bEscape = false;
						else {
							if ( sTmp.charAt(i) == '\\' ) {
								bEscape = true;
								continue;
							}
							if (( sTmp.charAt(i) == ':' ) || ( sTmp.charAt(i) == '=' )
								|| ( Character.isWhitespace(sTmp.charAt(i)) )) {
								// That the first white-space after the key
								n = i;
								break;
							}
						}
					}

					// Get the key
					if ( n == 0 ) {
						// Line empty after the key
						n = sTmp.length();
					}
					sKey = sTmp.substring(0, n);

					// Gets the value
					boolean bEmpty = true;
					boolean bCheckEqual = true;
					for ( int i=n; i<sTmp.length(); i++ ) {
						if ( bCheckEqual && (( sTmp.charAt(i) == ':' )
							|| ( sTmp.charAt(i) == '=' ))) {
							bCheckEqual = false;
							continue;
						}
						if ( !Character.isWhitespace(sTmp.charAt(i)) ) {
							// That the first white-space after the key
							n = i;
							bEmpty = false;
							break;
						}
					}

					if ( bEmpty ) n = sTmp.length();
					sValue = sTmp.substring(n);
					// Real text start point (adjusted for trimmed characters)
					nStartText = n + (textLine.length() - sTmp.length());
					// Use m_nLineSince-1 to not count the current one
					lS = (position-(textLine.length()+(lineSince-1))) + nStartText;
					lineSince = 0; // Reset the line counter for next time
				}

				// Is it a multi-lines entry?
				if ( sValue.endsWith("\\") ) {
					// Make sure we have an odd number of ending '\'
					int n = 0;
					for ( int i=sValue.length()-1;
						(( i > -1 ) && ( sValue.charAt(i) == '\\' ));
						i-- ) n++;

					if ( (n % 2) != 0 ) { // Continue onto the next line
						sValue = sValue.substring(0, sValue.length()-1);
						isMultiline = true;
						// Preserve parsed text in case we do not extract
						if ( keyBuffer.length() == 0 ) {
							keyBuffer.append(textLine.substring(0, nStartText));
							nStartText = 0; // Next time we get the whole line
						}
						textBuffer.append(textLine.substring(nStartText));
						continue; // Read next line
					}
				}

				// Check for key condition
				// Then for directives (they can overwrite the condition)
				//TODO: Revisit if key should override directives or reverse
				boolean bExtract = true;
				
/*				if ( m_Opt.m_bUseKeyCondition ) {
					if ( m_Opt.m_bExtractOnlyMatchingKey ) {
						if ( m_RE.matcher(sKey).matches() )
							bExtract = m_Opt.m_LD.isLocalizable(true);
						else
							bExtract = false;
					}
					else { // Extract all but items with matching keys
						if ( !m_RE.matcher(sKey).matches() )
							bExtract = m_Opt.m_LD.isLocalizable(true);
						else
							bExtract = false;
					}
				}
				
				if ( bExtract ) bExtract = m_Opt.m_LD.isLocalizable(true);
				else {
					// Make sure we pop/push the directives even if the 
					// outcome is already decided, otherwise it gets out-of-sync
					m_Opt.m_LD.isLocalizable(true);
				}*/

				if ( bExtract ) {
					item = new ExtractionItem();
					item.setSource(new Container(unescape(sValue)));
					item.setName(sKey);

					// Check the DNL list here to have resname, etc.
//					bExtract = !m_Opt.m_LD.isInDNLList(srcItem);
				}

				if ( bExtract ) {
					// Parts before the text
					if ( keyBuffer.length() == 0 ) {
						// Single-line case
						keyBuffer.append(textLine.substring(0, nStartText));
					}
					res.sklRes.appendData(keyBuffer);
				}
				else {
					res.sklRes.appendData(keyBuffer);
					res.sklRes.appendData(textBuffer);
					res.sklRes.appendData(textLine);
					return RESULT_DATA;
				}

				item.setPreserveSpace(true);
				item.setID(String.valueOf(++itemID));
//For test
				item.setExtension("TestExtension",
					new TestExtension("Data of the extension for item " + item.getID()));
//end for test				
				result = RESULT_ITEM;
				item.setProperty("start", String.valueOf(lS));

//TODO: handle {0,choice...} cases
//http://java.sun.com/j2se/1.4.2/docs/api/java/text/MessageFormat.html

//				if ( m_Opt.m_bUseCodeFinder )
//					m_Opt.m_CodeFinder.processFilterItem(srcItem);

				return result;
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public boolean supports (int feature) {
		switch ( feature ) {
		case FEATURE_TEXTBASED:
			return true;
		default:
			return false;
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

	public void process () {
		try {
			// Open the input reader from the provided stream
			BOMAwareInputStream bis = new BOMAwareInputStream(input, res.getSourceEncoding());
			reader = new BufferedReader(
				new InputStreamReader(bis, bis.detectEncoding()));
			
			// Initializes the variables
			res.endingLB = true;
			res.lineBreak = "\n"; //TODO: Auto-detection of line-break type
			itemID = 0;
			sklID = 0;
			lineNumber = 0;
			lineSince = 0;
			position = 0;
			
			// Get started
//For test			
			res.setExtension("docLevelExtension",
				new TestExtension("Document-level extension."));
//End for test			
			output.startResource(res);
			
			// Process
			int n;
			boolean resetBuffer = true;
			do {
				switch ( n = readItem(resetBuffer) ) {
				case RESULT_DATA:
					// Don't send the skeleton chunk now, wait for the complete one
					resetBuffer = false;
					break;
				case RESULT_ITEM:
					output.skeletonContainer(res.sklRes);
					output.startExtractionItem(item);
					output.endExtractionItem(item);
					resetBuffer = true;
					break;
				default:
					resetBuffer = true;
					break;
				}
			} while ( n > RESULT_END ); 
			
			output.skeletonContainer(res.sklRes);
			output.endResource(res);
		}
		catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		finally {
			close();
		}
	}

	public void setOutput (IResourceBuilder builder) {
		this.output = builder;
	}

}

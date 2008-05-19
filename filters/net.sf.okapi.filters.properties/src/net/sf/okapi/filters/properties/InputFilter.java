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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sf.okapi.common.ILog;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IInputFilter;
import net.sf.okapi.common.pipeline.IResourceBuilder;
import net.sf.okapi.common.resource.Container;
import net.sf.okapi.common.resource.ExtractionItem;
import net.sf.okapi.common.resource.IExtractionItem;

public class InputFilter implements IInputFilter {

	public static final int RESULT_ERROR         = 0;
	public static final int RESULT_CANCELLATION  = 1;
	public static final int RESULT_END           = 2;
	public static final int RESULT_ITEM          = 3;
	public static final int RESULT_DATA          = 4;
	
	private InputStream      input;
	private String           encoding;
	private IResourceBuilder output;
	private Resource         res;
	private BufferedReader   rdr;
	private IExtractionItem  item;
	
	private String           textLine;
	private String           lineBreak = "\n"; //TODO: Detect LB and use that
	private int              lineNumber;
	private int              lineSince;
	private long             position;
	private int              id;

	public InputFilter () {
		res = new Resource();
	}
	
	public void close ()
		throws Exception 
	{
		if ( rdr != null ) {
			rdr.close();
			rdr = null;
		}
	}

	public IParameters getParameters () {
		return res.params;
	}

	public void initialize (InputStream input,
		String encoding,
		String sourceLanguage,
		String targetLanguage)
		throws Exception
	{
		close();
		this.input = input;
		this.encoding = encoding;
		// neither sourceLanguage nor targetLanguage are used in this filter
	}

	private int readItem (boolean resetBuffer) {
		int result = RESULT_ERROR;

		try
		{
			if ( resetBuffer ) {
				res.buffer = new StringBuilder();
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
					if ( res.buffer.length() > 0 ) {
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
						res.buffer.append(textLine);
						res.buffer.append(lineBreak);
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
						res.buffer.append(textLine);
						res.buffer.append(lineBreak);
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
					item.setContent(new Container(unescape(sValue)));
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
					res.buffer.append(keyBuffer);
				}
				else {
					res.buffer.append(keyBuffer);
					res.buffer.append(textBuffer);
					res.buffer.append(textLine);
					return RESULT_DATA;
				}

				item.setPreserveFormatting(true);
				item.setID(++id);
				result = RESULT_ITEM;
				item.setProperty("start", lS);

//TODO: handle {0,choice...} cases
//http://java.sun.com/j2se/1.4.2/docs/api/java/text/MessageFormat.html

//				if ( m_Opt.m_bUseCodeFinder )
//					m_Opt.m_CodeFinder.processFilterItem(srcItem);

				return result;
			}
		}
		catch ( Exception E ) {
			logMessage(ILog.TYPE_ERROR, E.getMessage());
			result = RESULT_ERROR;
		}

		return result;
	}

	public void setParameters (IParameters params) {
		res.params = (Parameters)params;
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
			textLine = rdr.readLine();
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
	private String unescape (String text) {
		if ( text.indexOf('\\') == -1 ) return text;
		StringBuilder tmpText = new StringBuilder();
		for ( int i=0; i<text.length(); i++ ) {
			if ( text.charAt(i) == '\\' ) {
				if ( i+1 < text.length() ) {
					switch ( text.charAt(i+1) ) {
					case 'u':
						if ( i+5 < text.length() ) {
							try {
								int nTmp = Integer.parseInt(text.substring(i+2, i+6), 16);
								tmpText.append((char)nTmp);
							}
							catch ( Exception E ) {
								logMessage(ILog.TYPE_WARNING,
									String.format(Res.getString("INVALID_UESCAPE"),
									text.substring(i+2, i+6)));
							}
							i += 5;
							continue;
						}
						else {
							logMessage(ILog.TYPE_WARNING,
								String.format(Res.getString("INVALID_UESCAPE"),
								text.substring(i+2)));
						}
						break;
					case '\\':
						tmpText.append("\\\\");
						i++; // Next '\' will be set after
						continue;
					}
				}
			}
			else tmpText.append(text.charAt(i));
		}
		return tmpText.toString();
	}

	private void logMessage (int type,
		String text)
	{
		System.err.println(text);
		//m_Log.setLog(p_nType, 0, String.format(
		//	Res.getString("LINE_LOCATION"), m_nLine) + p_sText);
	}

	public void convert () {
		try {
			// Create new resource

			// Open the input reader from the provided stream
			rdr = new BufferedReader(
				new InputStreamReader( //TODO: use a real BOM-aware input reader!
					new BufferedInputStream(input), encoding));
			
			// Initializes the variables
			res.endingLB = true;
			id = 0;
			lineNumber = 0;
			lineSince = 0;
			position = 0;
			
			// Get started
			output.startResource(res);
			
			// Process
			int n;
			boolean resetBuffer = true;
			do {
				switch ( n = readItem(resetBuffer) ) {
				case RESULT_DATA:
					resetBuffer = false;
					break;
				case RESULT_ITEM:
					output.startExtractionItem(item);
					output.endExtractionItem(item);
					resetBuffer = true;
					break;
				default:
					resetBuffer = true;
					break;
				}
			} while ( n > RESULT_END ); 
			
			output.endResource(res);
		}
		catch ( Exception e ) {
			logMessage(0, e.getLocalizedMessage());
		}
		finally {
			try {
				close();
			}
			catch ( Exception e ) {
				logMessage(0, e.getLocalizedMessage());
			}
		}
	}

	public void setOutput (IResourceBuilder builder) {
		this.output = builder;
	}

}

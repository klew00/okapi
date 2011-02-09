/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.mif;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.okapi.common.BOMAwareInputStream;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiIllegalFilterOperationException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.exceptions.OkapiUnsupportedEncodingException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

@UsingParameters() // No parameters used
public class MIFFilter implements IFilter {
	
	private final Logger logger = Logger.getLogger(getClass().getName());

	// Must be windows-1252 to allow proper auto-detection
	public static final String DEFENCODING = "UTF-8"; // String in MIF 9 are UTF-8 according documentation
	
	private static final Hashtable<String, String> charTable = initCharTable();
	
	private static final String TOPSTATEMENTSTOSKIP = "ColorCatalog;ConditionCatalog;BoolCondCatalog;"
		+ "CombinedFontCatalog;PgfCatalog;ElementDefCatalog;FmtChangeListCatalog;DefAttrValuesCatalog;"
		+ "AttrCondExprCatalog;FontCatalog;RulingCatalog;TblCatalog;KumihanCatalog;Views;VariableFormats;"
		+ "MarkerTypeCatalog;XRefFormats;Document;BookComponent;InitialAutoNums;Dictionary;AFrames;Tbls;"
		+ "Page";

	private String lineBreak;
	private String docName;
	private BufferedReader reader;
	private StringBuilder tagBuffer;
	private StringBuilder strBuffer;
	private int tuId;
	private int otherId;
	private boolean canceled;
	private LinkedList<Event> queue;
	private LocaleId srcLang;
	private GenericSkeleton skel;
	private boolean hasNext;
	private EncoderManager encoderManager;
	private int inTextFlow;
//	private String version;
	private String sdId;
	private int paraLevel;
	private StringBuilder paraBuf;
	private boolean paraBufNeeded;
	
	private static Hashtable<String, String> initCharTable () {
		Hashtable<String, String> table = new Hashtable<String, String>();
		table.put("Tab", "\t");
		table.put("HardSpace", "\u00a0"); // = Unicode non-breaking space
		table.put("SoftHyphen", ""); // "\u2010" = Unicode Hyphen (not Soft-Hyphen), but we remove those
		table.put("HardHyphen", "\u2011"); // = Unicode Non-Breaking Hyphen
		table.put("DiscHyphen", "\u00ad"); // = Unicode Soft-Hyphen
		table.put("NoHyphen", "\u200d"); // = Unicode Zero-Width Joiner
		table.put("Cent", "\u00a2");
		table.put("Pound", "\u00a3");
		table.put("Yen", "\u00a5");
		table.put("EnDash", "\u2013");
		table.put("EmDash", "\u2014");
		table.put("Dagger", "\u2020");
		table.put("DoubleDagger", "\u2021");
		table.put("Bullet", "\u2022");
		table.put("HardReturn", "\n");
		table.put("NumberSpace", "\u2007");
		table.put("ThinSpace", "\u2009");
		table.put("EnSpace", "\u2002");
		table.put("EmSpace", "\u2003");
		return table;
	}

	@Override
	public void cancel () {
		canceled = true;
	}

	@Override
	public void close () {
		try {
			if ( reader != null ) {
				reader.close();
				reader = null;
			}
			hasNext = false;
		}
		catch ( IOException e ) {
			throw new OkapiIOException(e);
		}
	}

	@Override
	public String getName () {
		return "okf_mif";
	}
	
	@Override
	public String getDisplayName () {
		return "MIF Filter (ALPHA don't even try)";
	}

	@Override
	public String getMimeType () {
		return MimeTypeMapper.MIF_MIME_TYPE;
	}

	@Override
	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			MimeTypeMapper.MIF_MIME_TYPE,
			getClass().getName(),
			"MIF (ALPHA don't even try)",
			"Adobe FrameMaker MIF documents"));
		return list;
	}
	
	@Override
	public EncoderManager getEncoderManager () {
		if ( encoderManager == null ) {
			encoderManager = new EncoderManager();
			encoderManager.setMapping(MimeTypeMapper.MIF_MIME_TYPE, "net.sf.okapi.filters.mif.MIFEncoder");
		}
		return encoderManager;
	}

	@Override
	public IParameters getParameters () {
		return null;
	}

	@Override
	public boolean hasNext () {
		return hasNext;
	}

	@Override
	public void open (RawDocument input) {
		open(input, true);
	}
	
	@Override
	public void open (RawDocument input,
		boolean generateSkeleton)
	{
		close();
		setOptions(input.getSourceLocale(), input.getTargetLocale(),
			DEFENCODING, generateSkeleton);
		
		if ( input.getInputURI() != null ) {
			docName = input.getInputURI().getPath();
		}
		
		input.setEncoding(DEFENCODING);
		open(input.getStream());
	}
	
	private void setOptions (LocaleId sourceLocale,
		LocaleId targetLocale,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		srcLang = sourceLocale;
	}

	private void open (InputStream input) {
		try {
			// Detect encoding
			Object[] res = guessEncoding(input);
			reader = new BufferedReader(new InputStreamReader((InputStream)res[0], (String)res[1]));
			
			tagBuffer = new StringBuilder();
			strBuffer = new StringBuilder();
			paraBuf = new StringBuilder();
			tuId = 0;
			otherId = 0;
			canceled = false;
			hasNext = true;
			inTextFlow = 0;
//			version = "";
			lineBreak = "\n"; //TODO: Get from input file
			
			queue = new LinkedList<Event>();
			sdId = "sd1";
			StartDocument startDoc = new StartDocument(sdId);
			startDoc.setName(docName);
			startDoc.setLineBreak(lineBreak);
			startDoc.setEncoding((String)res[1], false);
			// We assume no BOM in all case for MIF
			startDoc.setLocale(srcLang);
			startDoc.setFilterParameters(getParameters());
			startDoc.setFilterWriter(createFilterWriter());
			startDoc.setType(getMimeType());
			startDoc.setMimeType(getMimeType());
			queue.add(new Event(EventType.START_DOCUMENT, startDoc));
		}
		catch ( UnsupportedEncodingException e ) {
			throw new OkapiUnsupportedEncodingException("Error reading MIF input.", e);
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error reading MIF input.", e);
		}
	}
	
	@Override
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		// Not used
	}

	@Override
	public void setParameters (IParameters params) {
		// No parameters for now
	}

	@Override
	public Event next () {
		// Treat cancel
		if ( canceled ) {
			queue.clear();
			queue.add(new Event(EventType.CANCELED));
			hasNext = false;
		}
		// Fill the queue if it's empty
		if ( queue.isEmpty() ) {
			read();
		}
		// Update hasNext flag on the FINISHED event
		if ( queue.peek().getEventType() == EventType.END_DOCUMENT ) {
			hasNext = false;
		}
		// Return the head of the queue
		return queue.poll();
	}

	@Override
	public ISkeletonWriter createSkeletonWriter () {
		return new GenericSkeletonWriter();
	}

	@Override
	public IFilterWriter createFilterWriter () {
		return new MIFFilterWriter(createSkeletonWriter(), getEncoderManager());
	}

	/**
	 * Top-level read
	 */
	private void read () {
		try {
			skel = new GenericSkeleton();
			int c;
			
			// Check if we are still processing a TextFlow 
			if ( inTextFlow > 0 ) {
				processTextFlow();
				return;
			}
			
			while ( (c = reader.read()) != -1 ) {
				switch ( c ) {
				case '#':
					skel.append((char)c);
					readComment(true, null);
					break;
					
				case '<': // Start of statement
					skel.append((char)c);
					String tag = readTag(true, true, null);
					//TODO: dispatch according tags
					if ( TOPSTATEMENTSTOSKIP.indexOf(tag) > -1 ) {
						skipOverContent(true, null);
					}
					else if ( "TextFlow".equals(tag) ) {
						processTextFlow();
						return;
					}
					else if ( "MIFFile".equals(tag) ) {
						MIFToken token = getNextTokenInStatement(true);
						if ( token.getType() == MIFToken.TYPE_STRING ) {
							//version = token.getString();
						}
						else {
							throw new OkapiIOException("MIF version not found.");
						}
					}
					else {
						// Default: skip over
						skipOverContent(true, null);
					}
					// Flush the skeleton from time to time to allow very large files
					queue.add(new Event(EventType.DOCUMENT_PART,
						new DocumentPart(String.valueOf(++otherId), false),
						skel));
					return;
					
				case '>': // End of statement
					skel.append((char)c);
					// Return skeleton
					DocumentPart dp = new DocumentPart(String.valueOf(++otherId), false, skel); 
					queue.add(new Event(EventType.DOCUMENT_PART, dp));
					return;
				default:
					skel.append((char)c);
					break;
				}
			}
			
			Ending ending = new Ending(String.valueOf(++otherId)); 
			queue.add(new Event(EventType.END_DOCUMENT, ending, skel));
		}
		catch ( IOException e ) {
			throw new OkapiIOException(e);
		}

		// Else: we are done
		queue.add(new Event(EventType.END_DOCUMENT,
			new Ending(String.valueOf(++otherId))));
	}

	/**
	 * Skips over the content of the current statement.
	 * Normally "<token" has been processed and level for after '<'
	 * @param store true to store in the skeleton
	 * @param buffer the StringBuilder object where to copy the content, or null to not copy.
	 * @throws IOException if an error occurs.
	 */
	private void skipOverContent (boolean store,
		StringBuilder buffer)
		throws IOException
	{
		int baseLevel = 1;
		int c;
		boolean inEscape = false;
		boolean inString = false;
		while ( (c = reader.read()) != -1 ) {
			if ( store ) {
				if ( buffer != null ) buffer.append((char)c);
				else skel.append((char)c);
			}
			// Parse a string content
			if ( inString ) {
				if ( c == '\'' ) inString = false;
				continue;
			}
			// Else: we are outside a string
			if ( inEscape ) {
				inEscape = false;
			}
			else {
				switch ( c ) {
				case '`':
					inString = true;
					break;
				case '\\':
					inEscape = true;
					break;
				case '<':
					baseLevel++;
					break;
				case '>':
					baseLevel--;
					if ( baseLevel == 0 ) {
						return;
					}
					break;
				}
			}
		}
		// Unexpected end
		throw new OkapiIllegalFilterOperationException("Unexpected end of input.");
	}
	
	private void readComment (boolean store,
		StringBuilder sb)
		throws IOException
	{
		int c;
		while ( (c = reader.read()) != -1 ) {
			if ( store ) {
				if ( sb != null ) sb.append((char)c);
				else skel.append((char)c);
			}
			switch ( c ) {
			case '\r':
			case '\n':
				return;
			}
		}
		// A comment can end the file
	}

	/**
	 * Process the first or next entry of a TextFlow statement.
	 * @throws IOException if a low-level error occurs.
	 */
	private void processTextFlow ()
		throws IOException
	{
		// Process one Para statement at a time
		if ( readUntil("Para", true) != null ) {
			inTextFlow++; // We are not done yet with this TextFlow statement
			processPara();
		}
		else { // Done
			inTextFlow = 0; // We are done
			// Close 
		}
		
		// If needed, create a document part and return
		if ( !skel.isEmpty() ) {
			queue.add(new Event(EventType.DOCUMENT_PART,
				new DocumentPart(String.valueOf(++otherId), false),
				skel));
		}
	}

	private void processPara ()
		throws IOException
	{
		TextFragment tf = new TextFragment();
		boolean first = true;
		paraLevel = 1;
		paraBuf.setLength(0);
		paraBufNeeded = false;

		// Go to the first ParaLine
		int res = readUntilText(paraBuf, false);
		while ( res > 0 ) {
			
			// Get the text to append
			paraLevel--; // We close String or Char outside of readUntilText() 
			String text = null;
			switch ( res ) {
			case 1: // String
				text = processString(false);
				paraBuf.append("`");
				break;
			case 2: // Char
				//TODO: we may have to remove the Char tag from the sb
				text = processChar(false).toString();
				break;
			}
			
			if ( Util.isEmpty(text) ) {
				// Nothing to do, keep on reading
			}
			else { // We have text
				if ( first ) { // First text in the fragment: put the codes in the skeleton
					first = false;
					skel.append(paraBuf.toString());
				}
				else { // Put the codes in an inline code 
					if ( paraBufNeeded && ( paraBuf.length() > 0 )) {
						tf.append(TagType.PLACEHOLDER, "x", paraBuf.toString());
					}
				}
				// Reset the codes buffer for next sequence
				paraBuf.setLength(0);
				paraBufNeeded = false;
				// Set the text
				tf.append(text);
			}
			
			// Place the closing of the String
			if ( res == 1 ) {
				paraBuf.append("'>");
			}

			// Move to the next text
			res = readUntilText(paraBuf, true);
		}

		TextUnit tu = null;
		if ( tf.hasText() ) {
			// Add the text unit to the queue
			tu = new TextUnit(String.valueOf(++tuId));
			tu.setPreserveWhitespaces(true);
			tu.setSourceContent(tf);
			queue.add(new Event(EventType.TEXT_UNIT, tu, skel));
			skel.addContentPlaceholder(tu);
		}
		else { // Put back the content/codes in skeleton
			skel.append(tf.toText());
		}

		if ( paraBuf.length() > 0 ) {
			skel.append(paraBuf.toString());
		}
		if ( tu != null ) {
			// New skeleton object for the next parts of the parent statement
			skel = new GenericSkeleton();
		}
	}
	
//	private void processParaLine (TextFragment tf,
//		StringBuilder sb)
//		throws IOException
//	{
//		String tag;
//		while ( true ) {
//			tag = readInlineUntil("String;Char;Variable;Font;Marker;XRef", true, sb);
//			if ( tag == null ) break; // Done
//			
//			if ( "String".equals(tag) ) {
//				sb.append("<String `");
//				tf.append(TagType.PLACEHOLDER, "x", sb.toString());
//				sb.setLength(0);
//				String text = processString(false);
//				tf.append(text);
//				sb.append("'>");
//				continue; // Skip common sb.SetLength(0);
//			}
//			else if ( "Char".equals(tag) ) {
//				//TODO: deal with parts strtaing with <Char
//				MIFToken token = processChar(false);
//				tf.append(token.toString());
//			}
//			else if ( "Variable".equals(tag) ) {
//				sb.append("<Variable ");
//				skipOverContent(true, sb);
//				tf.append(TagType.PLACEHOLDER, "var", sb.toString());
//			}
//			else if ( "Font".equals(tag) ) {
//				sb.append("<Font ");
//				skipOverContent(true, sb);
//				tf.append(TagType.PLACEHOLDER, "font", sb.toString());
//			}
//			else if ( "Marker".equals(tag) ) {
//				sb.append("<Marker ");
//				skipOverContent(true, sb);
//				tf.append(TagType.PLACEHOLDER, "marker", sb.toString());
//			}
//			else if ( "XRef".equals(tag) ) {
//				sb.append("<XRef ");
//				skipOverContent(true, sb);
//				tf.append(TagType.PLACEHOLDER, "xref", sb.toString());
//			}
//			
//			sb.setLength(0);
//		}
//	}

	private MIFToken getNextTokenInStatement (boolean store)
		throws IOException
	{
		int n;
		boolean leadingWSDone = false;
		do {
			switch ( n = reader.read() ) {
			case ' ':
			case '\t':
			case '\r':
			case '\n':
				if ( store ) skel.add((char)n);
				break;
			case -1:
				throw new OkapiIllegalFilterOperationException("Unexpected end of input.");
			default:
				if ( store ) skel.add((char)n);
				leadingWSDone = true;
				break;
			}
		}
		while ( !leadingWSDone );
		
		StringBuilder tmp = new StringBuilder();
		tmp.append((char)n);
		do {
			n = reader.read();
			if ( store ) skel.add((char)n);
			switch ( n ) {
			case ' ':
			case '\t':
			case '\r':
			case '\n':
			case '>': // End of statement
				MIFToken token = new MIFToken(tmp.toString());
				token.setLast(n == '>');
				return token;
			case -1:
				throw new OkapiIllegalFilterOperationException("Unexpected end of input.");
			default:
				tmp.append((char)n);
				break;
			}
		}
		while ( true );
	}
	
	private MIFToken processChar (boolean store)
		throws IOException
	{
		// Get the next token: the name of the character
		MIFToken token = getNextTokenInStatement(store);
		if ( !token.isLast() ) {
			skipOverContent(store, null);
		}
		
		// Default return is also a token
		MIFToken chToken = new MIFToken();
		// Map the character to its string, if possible
		if ( token.getType() == MIFToken.TYPE_STRING ) {
			String str = charTable.get(token.getString());
			if ( str == null ) {
				logger.warning(String.format("Unknow character name '%s'. This character will be ignored.", token));
			}
			else {
				chToken.setString(str); 
			}
		}
		else {
			// Invalid statement
			logger.warning("Unexpected token is Char statement. This character will be ignored.");
		}
		return chToken;
	}
	
//	/**
//	 * Reads until the end of the current statement or the first occurrence of the given statement.
//	 * @param tagNames the list of tag names to stop at.
//	 * @param store true if we store the parsed characters into the skeleton.
//	 * @param tf the text fragment where to store the inline codes
//	 * @return the name of the tag found, or null if none was found.
//	 * @throws IOException if a low-level error occurs.
//	 */
//	private String readInlineUntil (String tagNames,
//		boolean store,
//		StringBuilder sb)
//		throws IOException
//	{
//		int c;
//		int baseLevel = 1;
//		while ( (c = reader.read()) != -1 ) {
//			switch ( c ) {
//			case '#':
//				//sb.append((char)c);
//				readComment(false, null);
//				break;
//				
//			case '<': // Start of statement
//				baseLevel++;
//				StringBuilder sbTag = new StringBuilder();
//				String tag = readTag(store, true, sbTag);
//				if ( tagNames.indexOf(tag) > -1 ) {
//					return tag;
//				}
//				else { // Default: skip over
//					sb.append((char)c);
//					sb.append(sbTag);
//					skipOverContent(store, sb);
//					baseLevel--;
//				}
//				break;
//				
//			case '>': // End of statement
//				baseLevel--;
//				sb.append((char)c);
//				if ( baseLevel == 0 ) {
//					return null;
//				}
//				break;
//
//			default:
//				sb.append((char)c);
//				break;
//			}
//		}
//		return null;
//	}

	
	private int readUntilText (StringBuilder sb,
		boolean checkIfParaBufNeeded)
		throws IOException
	{
		int c;
		while ( (c = reader.read()) != -1 ) {
			switch ( c ) {
			case '#':
				sb.append((char)c);
				readComment(true, sb);
				break;
				
			case '<': // Start of statement
				paraLevel++;
				sb.append((char)c);
				String tag = readTag(true, false, sb);
				if ( "ParaLine".equals(tag) ) {
					return readUntilText(sb, checkIfParaBufNeeded);
				}
				// Cases for inside ParaLine
				else if ( "String".equals(tag) ) {
					return 1;
				}
				else if ( "Char".equals(tag) ) {
					return 2;
				}
				// Default: skip over
				else {
					if ( checkIfParaBufNeeded ) paraBufNeeded = true;
					skipOverContent(true, sb);
					paraLevel--;
				}
				break;
				
			case '>': // End of statement
				paraLevel--;
				sb.append((char)c);
				if ( paraLevel == 0 ) {
					return 0;
				}
				break;

			default:
				sb.append((char)c);
				break;
			}
		}
		return 0;
	}

	/**
	 * Reads until the end of the current statement or the first occurrence of the given statement.
	 * @param tagNames the list of tag names to stop at.
	 * @param store true if we store the parsed characters into the skeleton.
	 * @return the name of the tag found, or null if none was found.
	 * @throws IOException if a low-level error occurs.
	 */
	private String readUntil (String tagNames,
		boolean store)
		throws IOException
	{
		int c;
		int baseLevel = 1;
		while ( (c = reader.read()) != -1 ) {
			if ( store ) skel.append((char)c);
			switch ( c ) {
			case '#':
				readComment(true, null);
				break;
				
			case '<': // Start of statement
				baseLevel++;
				String tag = readTag(store, true, null);
				if ( tagNames.indexOf(tag) > -1 ) {
					return tag;
				}
				else { // Default: skip over
					skipOverContent(store, null);
					baseLevel--;
				}
				break;
				
			case '>': // End of statement
				baseLevel--;
				if ( baseLevel == 0 ) {
					return null;
				}
				break;
			}
		}
		return null;
	}
	
	/**
	 * Reads a tag name.
	 * @param store true to store the tag codes
	 * @param sb Not null to store the codes there, null to store in skeleton
	 * @return The name of the tag.
	 * @throws IOException
	 */
	private String readTag (boolean store,
		boolean storeCharStatement,
		StringBuilder sb)
		throws IOException
	{
		tagBuffer.setLength(0);
		int c;
		int wsStart = ((sb != null ) ? sb.length()-1 : -1);
		boolean leadingWSDone = false;
		// Skip and whitespace between '<' and the name
		do {
			switch ( c = reader.read() ) {
			case ' ':
			case '\t':
			case '\r':
			case '\n':
				if ( store ) {
					if ( sb != null ) sb.append((char)c);
					else skel.add((char)c);
				}
				break;
			case -1:
			default:
				leadingWSDone = true;
				break;
			}
		}
		while ( !leadingWSDone );
		
		// Now read the name
		while ( true ) {
			switch ( c ) {
			case ' ':
			case '\t':
			case '\r':
			case '\n':
				if ( store ) {
					if ( !storeCharStatement && tagBuffer.toString().equals("Char") ) {
						// Special case for <Char...>: we don't store it
						if ( wsStart > 0 ) {
							sb.delete(wsStart, sb.length());
						}
					}
					else {
						if ( sb != null ) {
							sb.append(tagBuffer.toString());
							sb.append((char)c);
						}
						else {
							skel.append(tagBuffer.toString());
							skel.append((char)c);
						}
					}
				}
				return tagBuffer.toString();
				
			case -1:
				throw new OkapiIllegalFilterOperationException("Unexpected end of input.");
				
			default:
				tagBuffer.append((char)c);
				break;
			}
			c = reader.read();
		}
	}
	
	private String processString (boolean store)
		throws IOException
	{
		strBuffer.setLength(0);
		int c;
		boolean inString = false;
		boolean inEscape = false;
		while ( (c = reader.read()) != -1 ) {
			if ( store ) skel.append((char)c);
			if ( inString ) {
				if ( inEscape ) {
					switch ( c ) {
					case '\\':
					case '>':
						strBuffer.append((char)c);
						break;
					case 't':
						strBuffer.append('\t');
						break;
					case 'Q':
						strBuffer.append('`');
						break;
					case 'q':
						strBuffer.append('\'');
						break;
					case 'u':
					case 'x':
						//TODO: parse escaped U and X styled chars
						break;
					}
					inEscape = false;
				}
				else {
					switch ( c ) {
					case '\'': // End of string
						inString = false;
					case '\\':
						inEscape = true;
						break;
					default:
						strBuffer.append((char)c);
						break;
					}
				}
			}
			else { // Not yet in string
				switch ( c ) {
				case '`':
					inString = true;
					break;
				case '>':
					return strBuffer.toString();
				}
			}
		}
		// Else: Missing end of string error
		throw new OkapiIllegalFilterOperationException("End of string is missing.");
	}

	private Object[] guessEncoding (InputStream input)
		throws IOException
	{
		Object[] res = new Object[2];
		// Detect any BOM-type encoded (and set the stream to skip over it)
		BOMAwareInputStream bais = new BOMAwareInputStream(input, DEFENCODING);
		res[0] = bais;
		res[1] = bais.detectEncoding();
		if ( bais.autoDtected() ) {
			return res;
		}
			
		// Else: try detect based on MIF weird mechanism
		//TODO

		// Try to match the file signature with the pre-defined signatures
		String signature = "";
			
		if ( signature.equals("\u201C\u00FA\u2013\u007B\u0152\u00EA") ) {
			// "Japanese" in Shift-JIS seen in Windows-1252 (coded in Unicode)
			res[1] = "Shift_JIS";
			return res;
		}
		else if ( signature.equals("\u00C6\u00FC\u00CB\u00DC\u00B8\u00EC") ) {
			res[1] = "EUC-JP";
			// "Japanese" in EUC seen in Windows-1252 (coded in Unicode)
			return res;
		}

		// Default
		res[1] = DEFENCODING;
		return res;
	}

}

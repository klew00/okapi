package net.sf.okapi.filters.mif;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Stack;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.FilterEventType;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.SkeletonUnit;

public class MIFFilter implements IFilter {
	
	static final Hashtable<String, Character> charTable = initCharTable();

	private BufferedReader reader;
	private StringBuilder buffer;
	private StringBuilder tagBuffer;
	private StringBuilder strBuffer;
	private Stack<String> tagStack;
	private Document docRes;
	private int parseState = 0;
	private boolean inPara;
	private IResource currentRes;
	private int tuId;
	private int skId;
	
	private static Hashtable<String, Character> initCharTable () {
		Hashtable<String, Character> table = new Hashtable<String, Character>();
		table.put("HardSpace",    '\u00a0');
		table.put("DiscHyphen",   '\u00ad');
		table.put("NoHyphen",     '\u200d');
		table.put("Tab",          '\t');
		table.put("Cent",         '\u00a2');
		table.put("Pound",        '\u00a3');
		table.put("Yen",          '\u00a5');
		table.put("EnDash",       '\u2013');
		table.put("Dagger",       '\u2020');
		table.put("EmDash",       '\u2014');
		table.put("DoubleDagger", '\u2021');
		table.put("Bullet",       '\u2022');
		table.put("NumberSpace",  '\u2007');
		table.put("ThinSpace",    '\u2009');
		table.put("EnSpace",      '\u2002');
		table.put("EmSpace",      '\u2003');
		table.put("HardReturn",   '\r');
		return table;
	}

	public void cancel () {
		// TODO
	}

	public void close () {
		try {
			if ( reader != null ) {
				reader.close();
				reader = null;
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public String getName () {
		return "MIFFilter";
	}

	public IParameters getParameters () {
		return null;
	}

	public IResource getResource () {
		return currentRes;
	}

	public boolean hasNext () {
		return (( parseState == 1 ) || ( parseState == 2 ));
	}
	
	public void open (InputStream input) {
		try {
			close(); //TODO: encoding for non-EN
			reader = new BufferedReader(
				new InputStreamReader(input, "UTF-8"));
			tagBuffer = new StringBuilder();
			buffer = new StringBuilder();
			strBuffer = new StringBuilder();
			tagStack = new Stack<String>();
			parseState = 1; // Need to send start-document
			inPara = false;
			tuId = -1;
			skId = -1;
		}
		catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public void open (URL inputPath) {
		try { //TODO: Make sure this is actually working (encoding?, etc.)
			open(inputPath.openStream());
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void open (CharSequence inputText) {
		// Not supported with MIF filter
		throw new UnsupportedOperationException();
	}

	public void setOptions (String language,
		String defaultEncoding)
	{
	}

	public void setParameters (IParameters params) {
	}

	public FilterEvent next () {
		try {
			// Handle first call
			if ( parseState == 1 ) {
				docRes = new Document();
				parseState = 2; // Inside the document
				currentRes = docRes;
				return new FilterEvent(FilterEventType.START_DOCUMENT, docRes);
			}
			
			// Process other calls
			buffer.setLength(0);
			int c;
			while ( (c = reader.read()) != -1 ) {
				switch ( c ) {
				case '#':
					buffer.append((char)c);
					readComment();
					break;
				case '<': // Start of statement
					buffer.append((char)c);
					readTag();
					if ( "Para".equals(tagBuffer) ) {
						
						inPara = true;
						// Return skeleton before
						currentRes = new SkeletonUnit(getId(skId), buffer.toString());
						return new FilterEvent(FilterEventType.SKELETON_UNIT, currentRes);
					}
					break;
				case '>': // End of statement
					buffer.append((char)c);
					// Return skeleton
					currentRes = new SkeletonUnit(getId(skId), buffer.toString());
					return new FilterEvent(FilterEventType.SKELETON_UNIT, currentRes);
				case '`': // Start of string
					//processString();
					break;
				}
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		
		parseState = 0; // No more
		currentRes = docRes;
		return new FilterEvent(FilterEventType.END_DOCUMENT, null);
	}

	private void readComment () throws IOException {
		int c;
		while ( (c = reader.read()) != -1 ) {
			buffer.append((char)c);
			switch ( c ) {
			case '\r':
			case '\n':
			case -1:
				return;
			}
		}
	}
	
	private String readTag () throws IOException {
		tagBuffer.setLength(0);
		int c;
		boolean leadingWSDone = false;
		// Skip and whitespace between '<' and the name
		do {
			switch ( c = reader.read() ) {
			case ' ':
			case '\t':
			case '\r':
			case '\n':
				buffer.append((char)c);
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
			case -1:
				buffer.append(tagBuffer);
				return tagBuffer.toString(); 
			default:
				tagBuffer.append((char)c);
				break;
			}
			c = reader.read();
		}
	}
	
	String processString () throws IOException {
		strBuffer.setLength(0);
		int c;
		boolean inEscape = false;
		while ( (c = reader.read()) != -1 ) {
			if ( inEscape ) {
				switch ( c ) {
				case '\'':
					return strBuffer.toString();
				case '\\':
					inEscape = true;
					break;
				default:
					strBuffer.append((char)c);
					break;
				}
			}
			else {
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
					//TODO
					break;
				}
				inEscape = false;
			}
		}
		// Else: Missing end of string error
		throw new RuntimeException("End of string is missing.");
	}

	/**
	 * Increments and format a given Id value.
	 * @param idToIncrement The Id variable to process.
	 * @return The formatted string value of the Id.
	 */
	private String getId (int idToIncrement) {
		//TODO: mmm does this increment the real Id or a copy??? To test.
		return String.valueOf(++idToIncrement);
	}

}

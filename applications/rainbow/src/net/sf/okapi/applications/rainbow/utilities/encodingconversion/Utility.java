/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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

package net.sf.okapi.applications.rainbow.utilities.encodingconversion;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.EventListenerList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.utilities.CancelListener;
import net.sf.okapi.applications.rainbow.utilities.ISimpleUtility;
import net.sf.okapi.common.BOMAwareInputStream;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;

public class Utility implements ISimpleUtility {

	private final Logger     logger = LoggerFactory.getLogger("net.sf.okapi.logging");

	private IParameters                     params;
	private String                          commonFolder;
	private String                          inputPath;
	private String                          inputEncoding;
	private String                          outputPath;
	private String                          outputEncoding;
	private String                          outFormat;
	private CharsetEncoder                  outputEncoder;
	private boolean                         escapeAll;
	private boolean                         useBytes;
	private boolean                         useCER;
	private Hashtable<String, Character>    CharEntities;
	private CharBuffer                      buffer;
	private boolean                         reportUnsupported;
	private Pattern                         pattern;
	private String                          prevBuf;
	private EventListenerList               listenerList = new EventListenerList();


	public Utility () {
		params = new Parameters();
	}
	
	public void resetLists () {
		// Not used in this utility
	}
	
	public String getID () {
		return "oku_encodingconversion";
	}
	
	public void processInput () {
		BufferedReader reader = null;
		OutputStreamWriter writer = null;
		try {
			// Open the input document
			BOMAwareInputStream bis = new BOMAwareInputStream(
				new FileInputStream(inputPath), inputEncoding);
			inputEncoding = bis.detectEncoding();
			reader = new BufferedReader(new InputStreamReader(bis, inputEncoding));
			logger.info("Input encoding: " + inputEncoding);
			
			// Open the output document
			Util.createDirectories(outputPath);
			writer = new OutputStreamWriter(new BufferedOutputStream(
				new FileOutputStream(outputPath)), outputEncoding);
			outputEncoder = Charset.forName(outputEncoding).newEncoder();
			logger.info("Output encoding: " + outputEncoding);
			Util.writeBOMIfNeeded(writer, params.getBoolean("BOMonUTF8"), outputEncoding);
			
			int n;
			CharBuffer tmpBuf = CharBuffer.allocate(1);
			ByteBuffer encBuf;
			boolean canEncode;

			while ( true ) {
				buffer.clear();
				// Start with previous buffer remains if needed
				if ( prevBuf != null ) {
					buffer.append(prevBuf);
				}
				// Read the next block
				n = reader.read(buffer);
				// Check if we need to stop here
				boolean needSplitCheck = true;
				if ( n == -1 ) {
					// Make sure we do not start an endless loop by
					// re-checking the last previous buffer
					if ( prevBuf != null ) {
						needSplitCheck = false;
						prevBuf = null;
						buffer.limit(buffer.position());
					}
					else break; // No previous, no read: Done
				}

				// Un-escape if requested
				if ( pattern != null ) {
					if ( needSplitCheck ) checkSplitSequence();
					unescape();
				}
				
				// Output
				n = buffer.position();
				buffer.position(0);
				for ( int i=0; i<n; i++ ) {
					if ( !(canEncode = outputEncoder.canEncode(buffer.get(i))) ) {
						if ( reportUnsupported ) {
							logger.warn(String.format("Un-supported character: U+%04X", (int)buffer.get(i)));
						}
					}
					
					if (( escapeAll && ( buffer.get(i) > 127 )) || !canEncode ) {
						boolean fallBack = false;
						// Write escape form
						if ( useCER ) {
							String tmp = findCER(buffer.get(i));
							if ( tmp == null ) fallBack = true;
							else writer.write("&"+tmp+";");
						}
						else {
							if ( useBytes ) { // Escape bytes
								if ( canEncode ) {
									tmpBuf.put(0, buffer.get(i));
									tmpBuf.position(0);
									encBuf = outputEncoder.encode(tmpBuf);
									for ( int j=0; j<encBuf.limit(); j++ ) {
										writer.write(String.format(outFormat,
											(encBuf.get(j)<0 ? (0xFF^~encBuf.get(j)) : encBuf.get(j)) ));
									}
								}
								else fallBack = true;
							}
							else { // Escape character
								writer.write(String.format(outFormat, (int)buffer.get(i)));
							}
						}
						if ( fallBack ) { // Default escaping when nothing else works
							writer.write(String.format("&#x%X;", (int)buffer.get(i)));
						}
					}
					else { // Normal raw forms
						writer.write(buffer.get(i));
					}
				}
			}
		}
		catch ( FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		catch ( IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if ( writer != null ) {
					writer.close();
					writer = null;
				}
				if ( reader != null ) {
					reader.close();
					reader = null;
				}
			}
			catch ( IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void doEpilog () {
	}

	public void doProlog (String sourceLanguage,
		String targetLanguage)
	{
		buffer = CharBuffer.allocate(13); //TODO: change to real length
		commonFolder = null; // Reset

		String tmp = "";
		if ( params.getBoolean("unescapeNCR") ) {
			tmp += "&#([0-9]*?);|&#[xX]([0-9a-fA-F]*?);";
		}
		if ( params.getBoolean("unescapeCER") ) {
			if ( tmp.length() > 0 ) tmp += "|";
			tmp += "(&\\w*?;)";
		}
		if ( params.getBoolean("unescapeJava") ) {
			if ( tmp.length() > 0 ) tmp += "|";
			tmp += "(\\\\[Uu]([0-9a-fA-F]{1,4}))";
		}
		if ( tmp.length() > 0 ) {
			pattern = Pattern.compile(tmp, Pattern.CASE_INSENSITIVE);
			if ( CharEntities == null ) createCharEntitiesTable();
		}
		else pattern = null;
        		
		reportUnsupported = params.getBoolean("reportUnsupported");
		useBytes = params.getBoolean("useBytes");
		escapeAll = params.getBoolean("escapeAll");

		useCER = false;
		switch ( params.getInt("escapeNotation") ) {
		case Parameters.ESCAPE_CER:
			useCER = true;
			outFormat = "&#x%X;"; // Here outFormat is used only if no CER can be used
			break;
		case Parameters.ESCAPE_JAVAL:
			outFormat = "\\u%04x";
			break;
		case Parameters.ESCAPE_JAVAU:
			outFormat = "\\u%04X";
			break;
		case Parameters.ESCAPE_NCRDECI:
			outFormat = "&#%d;";
			break;
		case Parameters.ESCAPE_NCRHEXAL:
			outFormat = "&#x%x;";
			break;
		case Parameters.ESCAPE_USERFORMAT:
			outFormat = params.getParameter("userFormat");
			break;
		case Parameters.ESCAPE_NCRHEXAU:
		default:
			outFormat = "&#x%X;";
			break;
		}
	}

	public String getInputRoot () {
		return null;
	}

	public String getOutputRoot () {
		return null;
	}

	public IParameters getParameters () {
		return params;
	}

	public boolean hasParameters () {
		return true;
	}

	public boolean isFilterDriven () {
		return false;
	}

	public boolean needsRoots () {
		return false;
	}

	public void addInputData (String path,
		String encoding,
		String filterSettings)
	{
		inputPath = path;
		inputEncoding = encoding;
	}

	public void addOutputData (String path,
		String encoding)
	{
		// Compute the longest common folder
		commonFolder = Util.longestCommonDir(commonFolder,
			Util.getDirectoryName(path), !Util.isOSCaseSensitive());
		outputPath = path;
		outputEncoding = encoding;
	}

	public void setParameters (IParameters paramsObject) {
		params = paramsObject;
	}

	public void setRoots (String inputRoot,
		String outputRoot)
	{
		// Not used in this utility.
	}

	public String getFolderAfterProcess () {
		return commonFolder;
	}

	public int getInputCount() {
		return 1;
	}

	private void checkSplitSequence () {
		int len = buffer.position();
		buffer.position(0);

		// Search for the first & or \ in the last 10 (or less) characters
		prevBuf = null;
		int j = 0;
		for ( int i=len-1; ((i>=0) && (j<10)); i-- ) {
			if (( buffer.charAt(i) == '&' ) || ( buffer.charAt(i) == '\\' )) {
				prevBuf = buffer.subSequence(i, len).toString();
				len = i;
				break;
			}
			j++;
		}
		
		buffer.position(len);
		buffer.limit(len);
	}
	
	private void unescape () {
		int len = buffer.position();
		buffer.position(0);
		Matcher m = pattern.matcher(buffer);
		int pos = 0;
		StringBuilder tmp = new StringBuilder(len);
		String seq = null;
		while ( m.find(pos) ) {
			// Copy any previous text
			if ( m.start() > pos ) {
				// Get text before
				tmp.append(buffer.subSequence(pos, m.start()));
			}
			pos = m.end();

			// Treat the escape sequence
			seq = m.group();
			int value = -1;
			int uIndex = seq.indexOf('u');
			if ( seq.indexOf('x') == 2 ) {
				// Hexadecimal NCR "&#xHHH;"
				value = Integer.parseInt(seq.substring(3, seq.length()-1), 16);
			}
			else if (( uIndex == 1 ) && ( seq.charAt(uIndex-1) == '\\' )) {
				// Java style "\ and uHHH"
				value = Integer.parseInt(seq.substring(2), 16);
			}
			else if ( seq.indexOf('#') == 1 ) {
				// Decimal NCR "&#DDD;"
				value = Integer.parseInt(seq.substring(2, seq.length()-1));
			}
			else {
				// Character entity reference: &NAME;
				seq = seq.substring(1, seq.length()-1);
				if ( CharEntities.containsKey(seq) ) {
					value = (int)CharEntities.get(seq);
				}
				else { // Unidentified: leave it like that
					value = -1;
				}
			}

			// Append the parsed escape
			if ( value < 128 ) {
				// Unknown pattern or ASCII values: Keep it as it
				// (so <, &, ", etc.. stay escaped)
				tmp.append(m.group());
			}
			else {
				tmp.append((char)value);
			}
		}
		
		// Copy last part and re-build the buffer
		if ( seq != null ) { // We had at least one match
			if ( pos < len ) {
				// Get text before
				tmp.append(buffer.subSequence(pos, len));
			}
			// Reset the buffer
			buffer.clear();
			buffer.append(tmp.toString(), 0, tmp.length());
		}
		else { // Else: nothing to un-escape
			buffer.position(len);
		}
	}
	
	private String findCER (char value) {
		if ( CharEntities == null ) createCharEntitiesTable();
		for ( String key : CharEntities.keySet() ) {
			if ( value == CharEntities.get(key) ) {
				return key;
			}
		}
		return null;
	}
	
	private void createCharEntitiesTable ()
	{
		CharEntities = new Hashtable<String, Character>();

		CharEntities.put("nbsp",	'\u00a0');
		CharEntities.put("iexcl",	'\u00a1');
		CharEntities.put("cent",	'\u00a2');
		CharEntities.put("pound",	'\u00a3');
		CharEntities.put("curren",	'\u00a4');
		CharEntities.put("yen",	'\u00a5');
		CharEntities.put("brvbar",	'\u00a6');
		CharEntities.put("sect",	'\u00a7');
		CharEntities.put("uml",	'\u00a8');
		CharEntities.put("copy",	'\u00a9');
		CharEntities.put("ordf",	'\u00aa');
		CharEntities.put("laquo",	'\u00ab');
		CharEntities.put("not",	'\u00ac');
		CharEntities.put("shy",	'\u00ad');
		CharEntities.put("reg",	'\u00ae');
		CharEntities.put("macr",	'\u00af');
		CharEntities.put("deg",	'\u00b0');
		CharEntities.put("plusmn",	'\u00b1');
		CharEntities.put("sup2",	'\u00b2');
		CharEntities.put("sup3",	'\u00b3');
		CharEntities.put("acute",	'\u00b4');
		CharEntities.put("micro",	'\u00b5');
		CharEntities.put("para",	'\u00b6');
		CharEntities.put("middot",	'\u00b7');
		CharEntities.put("cedil",	'\u00b8');
		CharEntities.put("sup1",	'\u00b9');
		CharEntities.put("ordm",	'\u00ba');
		CharEntities.put("raquo",	'\u00bb');
		CharEntities.put("frac14",	'\u00bc');
		CharEntities.put("frac12",	'\u00bd');
		CharEntities.put("frac34",	'\u00be');
		CharEntities.put("iquest",	'\u00bf');
		CharEntities.put("Agrave",	'\u00c0');
		CharEntities.put("Aacute",	'\u00c1');
		CharEntities.put("Acirc",	'\u00c2');
		CharEntities.put("Atilde",	'\u00c3');
		CharEntities.put("Auml",	'\u00c4');
		CharEntities.put("Aring",	'\u00c5');
		CharEntities.put("AElig",	'\u00c6');
		CharEntities.put("Ccedil",	'\u00c7');
		CharEntities.put("Egrave",	'\u00c8');
		CharEntities.put("Eacute",	'\u00c9');
		CharEntities.put("Ecirc",	'\u00ca');
		CharEntities.put("Euml",	'\u00cb');
		CharEntities.put("Igrave",	'\u00cc');
		CharEntities.put("Iacute",	'\u00cd');
		CharEntities.put("Icirc",	'\u00ce');
		CharEntities.put("Iuml",	'\u00cf');
		CharEntities.put("ETH",	'\u00d0');
		CharEntities.put("Ntilde",	'\u00d1');
		CharEntities.put("Ograve",	'\u00d2');
		CharEntities.put("Oacute",	'\u00d3');
		CharEntities.put("Ocirc",	'\u00d4');
		CharEntities.put("Otilde",	'\u00d5');
		CharEntities.put("Ouml",	'\u00d6');
		CharEntities.put("times",	'\u00d7');
		CharEntities.put("Oslash",	'\u00d8');
		CharEntities.put("Ugrave",	'\u00d9');
		CharEntities.put("Uacute",	'\u00da');
		CharEntities.put("Ucirc",	'\u00db');
		CharEntities.put("Uuml",	'\u00dc');
		CharEntities.put("Yacute",	'\u00dd');
		CharEntities.put("THORN",	'\u00de');
		CharEntities.put("szlig",	'\u00df');
		CharEntities.put("agrave",	'\u00e0');
		CharEntities.put("aacute",	'\u00e1');
		CharEntities.put("acirc",	'\u00e2');
		CharEntities.put("atilde",	'\u00e3');
		CharEntities.put("auml",	'\u00e4');
		CharEntities.put("aring",	'\u00e5');
		CharEntities.put("aelig",	'\u00e6');
		CharEntities.put("ccedil",	'\u00e7');
		CharEntities.put("egrave",	'\u00e8');
		CharEntities.put("eacute",	'\u00e9');
		CharEntities.put("ecirc",	'\u00ea');
		CharEntities.put("euml",	'\u00eb');
		CharEntities.put("igrave",	'\u00ec');
		CharEntities.put("iacute",	'\u00ed');
		CharEntities.put("icirc",	'\u00ee');
		CharEntities.put("iuml",	'\u00ef');
		CharEntities.put("eth",	'\u00f0');
		CharEntities.put("ntilde",	'\u00f1');
		CharEntities.put("ograve",	'\u00f2');
		CharEntities.put("oacute",	'\u00f3');
		CharEntities.put("ocirc",	'\u00f4');
		CharEntities.put("otilde",	'\u00f5');
		CharEntities.put("ouml",	'\u00f6');
		CharEntities.put("divide",	'\u00f7');
		CharEntities.put("oslash",	'\u00f8');
		CharEntities.put("ugrave",	'\u00f9');
		CharEntities.put("uacute",	'\u00fa');
		CharEntities.put("ucirc",	'\u00fb');
		CharEntities.put("uuml",	'\u00fc');
		CharEntities.put("yacute",	'\u00fd');
		CharEntities.put("thorn",	'\u00fe');
		CharEntities.put("yuml",	'\u00ff');
		CharEntities.put("OElig",	'\u0152');
		CharEntities.put("oelig",	'\u0153');
		CharEntities.put("Scaron",	'\u0160');
		CharEntities.put("scaron",	'\u0161');
		CharEntities.put("Yuml",	'\u0178');
		CharEntities.put("circ",	'\u02c6');
		CharEntities.put("tilde",	'\u02dc');
		CharEntities.put("ensp",	'\u2002');
		CharEntities.put("emsp",	'\u2003');
		CharEntities.put("thinsp",	'\u2009');
		CharEntities.put("zwnj",	'\u200c');
		CharEntities.put("zwj",	'\u200d');
		CharEntities.put("lrm",	'\u200e');
		CharEntities.put("rlm",	'\u200f');
		CharEntities.put("ndash",	'\u2013');
		CharEntities.put("mdash",	'\u2014');
		CharEntities.put("lsquo",	'\u2018');
		CharEntities.put("rsquo",	'\u2019');
		CharEntities.put("sbquo",	'\u201a');
		CharEntities.put("ldquo",	'\u201c');
		CharEntities.put("rdquo",	'\u201d');
		CharEntities.put("bdquo",	'\u201e');
		CharEntities.put("dagger",	'\u2020');
		CharEntities.put("Dagger",	'\u2021');
		CharEntities.put("permil",	'\u2030');
		CharEntities.put("lsaquo",	'\u2039');
		CharEntities.put("rsaquo",	'\u203a');
		CharEntities.put("euro",	'\u20ac');
		CharEntities.put("fnof",	'\u0192');
		CharEntities.put("Alpha",	'\u0391');
		CharEntities.put("Beta",	'\u0392');
		CharEntities.put("Gamma",	'\u0393');
		CharEntities.put("Delta",	'\u0394');
		CharEntities.put("Epsilon",	'\u0395');
		CharEntities.put("Zeta",	'\u0396');
		CharEntities.put("Eta",	'\u0397');
		CharEntities.put("Theta",	'\u0398');
		CharEntities.put("Iota",	'\u0399');
		CharEntities.put("Kappa",	'\u039a');
		CharEntities.put("Lambda",	'\u039b');
		CharEntities.put("Mu",	'\u039c');
		CharEntities.put("Nu",	'\u039d');
		CharEntities.put("Xi",	'\u039e');
		CharEntities.put("Omicron",	'\u039f');
		CharEntities.put("Pi",	'\u03a0');
		CharEntities.put("Rho",	'\u03a1');
		CharEntities.put("Sigma",	'\u03a3');
		CharEntities.put("Tau",	'\u03a4');
		CharEntities.put("Upsilon",	'\u03a5');
		CharEntities.put("Phi",	'\u03a6');
		CharEntities.put("Chi",	'\u03a7');
		CharEntities.put("Psi",	'\u03a8');
		CharEntities.put("Omega",	'\u03a9');
		CharEntities.put("alpha",	'\u03b1');
		CharEntities.put("beta",	'\u03b2');
		CharEntities.put("gamma",	'\u03b3');
		CharEntities.put("delta",	'\u03b4');
		CharEntities.put("epsilon",	'\u03b5');
		CharEntities.put("zeta",	'\u03b6');
		CharEntities.put("eta",	'\u03b7');
		CharEntities.put("theta",	'\u03b8');
		CharEntities.put("iota",	'\u03b9');
		CharEntities.put("kappa",	'\u03ba');
		CharEntities.put("lambda",	'\u03bb');
		CharEntities.put("mu",	'\u03bc');
		CharEntities.put("nu",	'\u03bd');
		CharEntities.put("xi",	'\u03be');
		CharEntities.put("omicron",	'\u03bf');
		CharEntities.put("pi",	'\u03c0');
		CharEntities.put("rho",	'\u03c1');
		CharEntities.put("sigmaf",	'\u03c2');
		CharEntities.put("sigma",	'\u03c3');
		CharEntities.put("tau",	'\u03c4');
		CharEntities.put("upsilon",	'\u03c5');
		CharEntities.put("phi",	'\u03c6');
		CharEntities.put("chi",	'\u03c7');
		CharEntities.put("psi",	'\u03c8');
		CharEntities.put("omega",	'\u03c9');
		CharEntities.put("thetasym",	'\u03d1');
		CharEntities.put("upsih",	'\u03d2');
		CharEntities.put("piv",	'\u03d6');
		CharEntities.put("bull",	'\u2022');
		CharEntities.put("hellip",	'\u2026');
		CharEntities.put("prime",	'\u2032');
		CharEntities.put("Prime",	'\u2033');
		CharEntities.put("oline",	'\u203e');
		CharEntities.put("frasl",	'\u2044');
		CharEntities.put("weierp",	'\u2118');
		CharEntities.put("image",	'\u2111');
		CharEntities.put("real",	'\u211c');
		CharEntities.put("trade",	'\u2122');
		CharEntities.put("alefsym",	'\u2135');
		CharEntities.put("larr",	'\u2190');
		CharEntities.put("uarr",	'\u2191');
		CharEntities.put("rarr",	'\u2192');
		CharEntities.put("darr",	'\u2193');
		CharEntities.put("harr",	'\u2194');
		CharEntities.put("crarr",	'\u21b5');
		CharEntities.put("lArr",	'\u21d0');
		CharEntities.put("uArr",	'\u21d1');
		CharEntities.put("rArr",	'\u21d2');
		CharEntities.put("dArr",	'\u21d3');
		CharEntities.put("hArr",	'\u21d4');
		CharEntities.put("forall",	'\u2200');
		CharEntities.put("part",	'\u2202');
		CharEntities.put("exist",	'\u2203');
		CharEntities.put("empty",	'\u2205');
		CharEntities.put("nabla",	'\u2207');
		CharEntities.put("isin",	'\u2208');
		CharEntities.put("notin",	'\u2209');
		CharEntities.put("ni",	'\u220b');
		CharEntities.put("prod",	'\u220f');
		CharEntities.put("sum",	'\u2211');
		CharEntities.put("minus",	'\u2212');
		CharEntities.put("lowast",	'\u2217');
		CharEntities.put("radic",	'\u221a');
		CharEntities.put("prop",	'\u221d');
		CharEntities.put("infin",	'\u221e');
		CharEntities.put("ang",	'\u2220');
		CharEntities.put("and",	'\u2227');
		CharEntities.put("or",	'\u2228');
		CharEntities.put("cap",	'\u2229');
		CharEntities.put("cup",	'\u222a');
		CharEntities.put("int",	'\u222b');
		CharEntities.put("there4",	'\u2234');
		CharEntities.put("sim",	'\u223c');
		CharEntities.put("cong",	'\u2245');
		CharEntities.put("asymp",	'\u2248');
		CharEntities.put("ne",	'\u2260');
		CharEntities.put("equiv",	'\u2261');
		CharEntities.put("le",	'\u2264');
		CharEntities.put("ge",	'\u2265');
		CharEntities.put("sub",	'\u2282');
		CharEntities.put("sup",	'\u2283');
		CharEntities.put("nsub",	'\u2284');
		CharEntities.put("sube",	'\u2286');
		CharEntities.put("supe",	'\u2287');
		CharEntities.put("oplus",	'\u2295');
		CharEntities.put("otimes",	'\u2297');
		CharEntities.put("perp",	'\u22a5');
		CharEntities.put("sdot",	'\u22c5');
		CharEntities.put("lceil",	'\u2308');
		CharEntities.put("rceil",	'\u2309');
		CharEntities.put("lfloor",	'\u230a');
		CharEntities.put("rfloor",	'\u230b');
		CharEntities.put("lang",	'\u2329');
		CharEntities.put("rang",	'\u232a');
		CharEntities.put("loz",	'\u25ca');
		CharEntities.put("spades",	'\u2660');
		CharEntities.put("clubs",	'\u2663');
		CharEntities.put("hearts",	'\u2665');
		CharEntities.put("diams",	'\u2666');
	}

	public void setFilterAccess (FilterAccess filterAccess,
		String paramsFolder)
	{
		// Not used
	}
	
	public void setContextUI (Object contextUI) {
		// Not used
	}

	public void addCancelListener (CancelListener listener) {
		listenerList.add(CancelListener.class, listener);
	}

	public void removeCancelListener (CancelListener listener) {
		listenerList.remove(CancelListener.class, listener);
	}

	/*private void fireCancelEvent (CancelEvent event) {
		Object[] listeners = listenerList.getListenerList();
		for ( int i=0; i<listeners.length; i+=2 ) {
			if ( listeners[i] == CancelListener.class ) {
				((CancelListener)listeners[i+1]).cancelOccurred(event);
			}
		}
	}*/

}

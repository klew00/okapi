/*===========================================================================
  Copyright (C) 2008-2013 by the Okapi Framework contributors
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

package net.sf.okapi.common.filterwriter;

import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.resource.TextPart;

/**
 * Handles the conversion between a coded text object and XLIFF.
 * In some case this class may output ITS attributes.
 * It assumes the namespace for ITS is declared and the corresponding prefix is 'its'.
 */
public class XLIFFContent {

	public static final String ITS_PREFIX = "its:";
	public static final String REF_PREFIX = "REF:";
	
	private String codedText;
	private List<Code> codes;
	private XLIFFContent innerContent;
	private CharsetEncoder chsEnc;
	private List<GenericAnnotations> standoff;
	
	/**
	 * Creates a new XLIFFContent object without any content.
	 */
	public XLIFFContent () {
		codedText = "";
	}
	
	/**
	 * Creates a new XLIFFContent object and set its content to the given fragment.
	 * @param content The TextFragment object to format.
	 */
	public XLIFFContent (TextFragment content) {
		setContent(content);
	}

	/**
	 * Sets the character set encoder to use. 
	 * @param chsEnc the character set encoder to use.
	 */
	public void setCharsetEncoder (CharsetEncoder chsEnc) {
		this.chsEnc = chsEnc;
	}

	/**
	 * Sets the fragment to format.
	 * @param content The TextFragment object to format.
	 * @return Itself
	 */
	public XLIFFContent setContent (TextFragment content) {
		codedText = content.getCodedText();
		codes = content.getCodes();
		return this;
	}
	
	/**
	 * Generates an XLIFF string from the content.
	 * This is the same as calling this.toString(1, true, false, false).
	 * @return The string formatted in XLIFF.
	 */
	@Override
	public String toString () {
		return toString(1, true, false, false);
	}

	/**
	 * Generates an XLIFF string from the content.
	 * This is the same as calling this.toString(1, true, false) and setting gMode.
	 * @param gMode True to use g/x markup, false to use bpt/ept/ph
	 * @return The string formatted in XLIFF.
	 */
	public String toString (boolean gMode) {
		return toString(1, true, false, gMode);
	}

	/**
	 * Generates an XLIFF string from the content.
	 * <p>In some cases, a reference to an ITS standoff element may be generated.
	 * Use {@link #getStandoff()} to get the standoff information that needs to be output
	 * along with the inline reference. Each call to {@link #toString(int, boolean, boolean, boolean)} resets 
	 * the standoff information returned by {@link #getStandoff()}.
	 * @param quoteMode 0=no quote escaped, 1=apos and quot, 2=#39 and quot,
	 * and 3=quot only.
	 * @param escapeGT True to always escape '>' to gt
	 * @param codeOnlyMode True when the in-line codes are to be set as raw-values.
	 * @param gMode True to use g/x markup, false to use bpt/ept/ph
	 * This option is to be used when the in-line code is an XLIFF-in-line code itself.
	 * @return The string formatted in XLIFF.
	 */
	public String toString (int quoteMode,
		boolean escapeGT,
		boolean codeOnlyMode,
		boolean gMode)
	{
		StringBuilder tmp = new StringBuilder();
		int index;
		Code code;
		standoff = null;
		for ( int i=0; i<codedText.length(); i++ ) {
			switch ( codedText.codePointAt(i) ) {
			case TextFragment.MARKER_OPENING:
				index = TextFragment.toIndex(codedText.charAt(++i));
				code = codes.get(index);
				if ( codeOnlyMode ) {
					tmp.append(code.toString());
				}
				else {
					// Output the code (if it's not a marker-only one)
					if ( !code.getType().equals(GenericAnnotationType.ANNOTATION_ONLY_MARKER) ) {
						if ( gMode ) {
							tmp.append(String.format("<g id=\"%d\">", code.getId()));
						}
						else {
							tmp.append(String.format("<bpt id=\"%d\">", code.getId()));//TODO: escape unsupported chars
							tmp.append(Util.escapeToXML(code.toString(), quoteMode, escapeGT, chsEnc));
							tmp.append("</bpt>");
						}
					}
					// Then, if needed, output the marker element
					// (Markers linked to original codes have the marker inside the spanned content) 
					if ( code.hasAnnotation("protected") ) {
						tmp.append("<mrk mtype=\"protected\">");
					}
					else if ( code.hasAnnotation(GenericAnnotationType.GENERIC) ) {
						tmp.append("<mrk mtype=\"x-its\"");
						outputITSAttributes((GenericAnnotations)code.getAnnotation(GenericAnnotationType.GENERIC), quoteMode, escapeGT, tmp);
						tmp.append(">");
					}
				}
				break;
			case TextFragment.MARKER_CLOSING:
				index = TextFragment.toIndex(codedText.charAt(++i));
				code = codes.get(index);
				if ( codeOnlyMode ) {
					tmp.append(code.toString());
				}
				else {
					// Close the marker, if needed
					if ( code.hasAnnotation(GenericAnnotationType.GENERIC) ) {
						tmp.append("</mrk>");
					}
					else if ( code.hasAnnotation("protected") ) {
						tmp.append("</mrk>");
					}
					// Then close the code
					if ( !code.getType().equals(GenericAnnotationType.ANNOTATION_ONLY_MARKER) ) {
						if ( gMode ) {
							tmp.append("</g>");
						}
						else {
							tmp.append(String.format("<ept id=\"%d\">", code.getId())); //TODO: escape unsupported chars
							tmp.append(Util.escapeToXML(code.toString(), quoteMode, escapeGT, chsEnc));
							tmp.append("</ept>");
						}
					}
				}
				break;
			case TextFragment.MARKER_ISOLATED:
				index = TextFragment.toIndex(codedText.charAt(++i));
				code = codes.get(index);
				if ( codeOnlyMode ) {
					tmp.append(code.toString());
				}
				else {
					if ( gMode ) {
						if ( code.getTagType() == TagType.OPENING ) {
							tmp.append(String.format("<bx id=\"%d\"/>", code.getId()));
						}
						else if ( code.getTagType() == TagType.CLOSING ) {
							tmp.append(String.format("<ex id=\"%d\"/>", code.getId()));
						}
						else {
							tmp.append(String.format("<x id=\"%d\"/>", code.getId()));
						}
					}
					else {
						if ( code.getTagType() == TagType.OPENING ) {
							tmp.append(String.format("<it id=\"%d\" pos=\"open\">", code.getId())); //TODO: escape unsupported chars
							tmp.append(Util.escapeToXML(code.toString(), quoteMode, escapeGT, chsEnc));
							tmp.append("</it>");
						}
						else if ( code.getTagType() == TagType.CLOSING ) {
							tmp.append(String.format("<it id=\"%d\" pos=\"close\">", code.getId())); //TODO: escape unsupported chars
							tmp.append(Util.escapeToXML(code.toString(), quoteMode, escapeGT, chsEnc));
							tmp.append("</it>");
						}
						else {
							tmp.append(String.format("<ph id=\"%d\">", code.getId())); //TODO: escape unsupported chars
							tmp.append(Util.escapeToXML(code.toString(), quoteMode, escapeGT, chsEnc));
							tmp.append("</ph>");
						}
					}
				}
				break;
			case '>':
				if ( escapeGT ) tmp.append("&gt;");
				else {
					if (( i > 0 ) && ( codedText.charAt(i-1) == ']' )) 
						tmp.append("&gt;");
					else
						tmp.append('>');
				}
				break;
			case '\r': // Not a line-break in the XML context, but a literal
				tmp.append("&#13;");
				break;
			case '<':
				tmp.append("&lt;");
				break;
			case '&':
				tmp.append("&amp;");
				break;
			case '"':
				if ( quoteMode > 0 ) tmp.append("&quot;");
				else tmp.append('"');
				break;
			case '\'':
				switch ( quoteMode ) {
				case 1:
					tmp.append("&apos;");
					break;
				case 2:
					tmp.append("&#39;");
					break;
				default:
					tmp.append(codedText.charAt(i));
					break;
				}
				break;
			default:
				if ( codedText.charAt(i) > 127 ) { // Extended chars
					if ( Character.isHighSurrogate(codedText.charAt(i)) ) {
						int cp = codedText.codePointAt(i++);
						String buf = new String(Character.toChars(cp));
						if (( chsEnc != null ) && !chsEnc.canEncode(buf) ) {
							tmp.append(String.format("&#x%x;", cp));
						} else {
							tmp.append(buf);
						}
					}
					else {
						if (( chsEnc != null ) && !chsEnc.canEncode(codedText.charAt(i)) ) {
							tmp.append(String.format("&#x%04x;", codedText.codePointAt(i)));
						}
						else { // No encoder or char is supported
							tmp.append(codedText.charAt(i));
						}
					}
				}
				else { // ASCII chars
					tmp.append(codedText.charAt(i));
				}
				break;
			}
		}
		return tmp.toString();
	}
	
	/**
	 * Generates an XLIFF string from a given text container.
	 * @param container The container to write out.
	 * @param quoteMode 0=no quote escaped, 1=apos and quot, 2=#39 and quot,
	 * and 3=quot only.
	 * @param escapeGT True to always escape '>' to &amp;gt;
	 * @param withMarkers True to output mrk elements, false to output only 
	 * the content of mrk element.
	 * @param gMode True to use g/x markup, false to use bpt/ept/ph
	 * @return The coded string.
	 */
	public String toSegmentedString (TextContainer container,
		int quoteMode,
		boolean escapeGT,
		boolean withMarkers,
		boolean gMode)
	{
		StringBuilder tmp = new StringBuilder();
		if ( innerContent == null ) {
			innerContent = new XLIFFContent();
			innerContent.setCharsetEncoder(chsEnc);
		}

		for ( TextPart part : container ) {
			// Segment marker if needed
			if ( withMarkers && part.isSegment() && container.hasBeenSegmented() ) {
				tmp.append(String.format("<mrk mid=\"%s\" mtype=\"seg\">", ((Segment)part).id));
			}
			// Fragment
			innerContent.setContent(part.text);
			tmp.append(innerContent.toString(quoteMode, escapeGT, false, gMode));
			standoff = innerContent.getStandoff(); // Trickle up the standoff information too.
			if ( withMarkers && part.isSegment() && container.hasBeenSegmented() ) {
				if ( withMarkers ) tmp.append("</mrk>");
			}
		}
		return tmp.toString();
	}

	/**
	 * Gets the standoff information for a possible list of annotations.
	 * @return null if there are no standoff markup to generate, or a list of {@link GenericAnnotations} objects.
	 * The data of each annotation set is the id that is used
	 * in the local markup to point to this standoff markup.
	 */
	public List<GenericAnnotations> getStandoff () {
		return standoff;
	}
	
	private void outputITSAttributes (GenericAnnotations anns,
		int quoteMode,
		boolean escapeGT,
		StringBuilder output)
	{
		for ( GenericAnnotation ann : anns ) {
			// AnnotatorsRef
			//TODO
			// Disambiguation
			if ( ann.getType().equals(GenericAnnotationType.DISAMB) ) {
				printITSStringAttribute(ann.getString(GenericAnnotationType.DISAMB_CLASS), "disambigClass", quoteMode, escapeGT, output);
				printITSDoubleAttribute(ann.getDouble(GenericAnnotationType.DISAMB_CONFIDENCE), "disambigConfidence", output);
				//TODO: needs annotatorsRef if confidence is there
				String value = ann.getString(GenericAnnotationType.DISAMB_GRANULARITY);
				if ( !value.equals(GenericAnnotationType.DISAMB_GRANULARITY_ENTITY) ) { // Output only the non-default value
					printITSStringAttribute(value, "disambigGranularity", quoteMode, escapeGT, output);
				}
				printITSStringAttribute(ann.getString(GenericAnnotationType.DISAMB_IDENT), "disambigIdent", quoteMode, escapeGT, output);
				printITSStringAttribute(ann.getString(GenericAnnotationType.DISAMB_SOURCE), "disambigSource", quoteMode, escapeGT, output);
			}
			
			// Terminology
			else if ( ann.getType().equals(GenericAnnotationType.TERM) ) {
				printITSDoubleAttribute(ann.getDouble(GenericAnnotationType.TERM_CONFIDENCE), "termConfidence", output);
				printITSStringAttribute(ann.getString(GenericAnnotationType.TERM_INFO), "termInfo", quoteMode, escapeGT, output);
			}
			
			// Allowed Characters
			else if ( ann.getType().equals(GenericAnnotationType.ALLOWEDCHARS) ) {
				printITSStringAttribute(ann.getString(GenericAnnotationType.ALLOWEDCHARS_PATTERN), "allowedCharacters", quoteMode, escapeGT, output);
			}
			
			// Storage Size
			else if ( ann.getType().equals(GenericAnnotationType.STORAGESIZE) ) {
				printITSIntegerAttribute(ann.getInteger(GenericAnnotationType.STORAGESIZE_SIZE), "storageSize", output);
				String tmp = ann.getString(GenericAnnotationType.STORAGESIZE_ENCODING);
				if ( !tmp.equals("UTF-8") ) printITSStringAttribute(tmp, "storageEncoding", quoteMode, escapeGT, output);
				tmp = ann.getString(GenericAnnotationType.STORAGESIZE_LINEBREAK);
				if ( !tmp.equals("lf") ) printITSStringAttribute(tmp, "storageLinebreak", quoteMode, escapeGT, output);
			}
			
			// Localization Quality issue
			else if ( ann.getType().equals(GenericAnnotationType.LQI) ) {
				continue; // LQI are dealt with separately
			}
		}
		
		// Deal with LQI information
		List<GenericAnnotation> list = anns.getAnnotations(GenericAnnotationType.LQI);
		if ( list.size() == 1 ) {
			// If there is only one QI entry: we output it locally
			GenericAnnotation ann = list.get(0);
			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_COMMENT), "locQualityIssueComment", quoteMode, escapeGT, output);
			Boolean booVal = ann.getBoolean(GenericAnnotationType.LQI_ENABLED);
			if (( booVal != null ) && !booVal ) { // Output only non-default value (if one is set)
				printITSBooleanAttribute(booVal, "locQualityIssueEnabled", output);
			}
			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_PROFILEREF), "locQualityIssueProfile", quoteMode, escapeGT, output);
			printITSDoubleAttribute(ann.getDouble(GenericAnnotationType.LQI_SEVERITY), "locQualityIssueSeverity", output);
			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_TYPE), "locQualityIssueType", quoteMode, escapeGT, output);
		}
		else if ( list.size() > 1 ) {
			// If there are 2 or more: the items need to be output as standoff markup.
			// This generates the reference and creates initializes the standoff data.
			// Call getStandoff() to get the items to output in the standoff element.
			String refId = anns.getData(); // ID to use is already generated in the annotation
			output.append(" "+ITS_PREFIX+"locQualityIssuesRef=\"#"+refId+"\"");
			if ( standoff == null ) standoff = new ArrayList<GenericAnnotations>();
			GenericAnnotations newSet = new GenericAnnotations();
			standoff.add(newSet);
			newSet.setData(refId);
			newSet.addAll(list);
		}
	}
	
	private void printITSStringAttribute (String value,
		String attrName,
		int quoteMode,
		boolean escapeGT,
		StringBuilder output)
	{
		if ( value != null ) {
			String ref = "";
			if ( value.startsWith(REF_PREFIX) ) {
				ref = "Ref";
				value = value.substring(REF_PREFIX.length());
			}
			output.append(" "+ITS_PREFIX+attrName+ref+"=\""+Util.escapeToXML(value, quoteMode, escapeGT, chsEnc)+"\"");
		}
	}

	private void printITSDoubleAttribute (Double value,
		String attrName,
		StringBuilder output)
	{
		if ( value != null ) {
			output.append(" "+ITS_PREFIX+attrName+"=\""+Util.formatDouble(value)+"\"");
		}
	}

	private void printITSIntegerAttribute (Integer value,
		String attrName,
		StringBuilder output)
	{
		if ( value != null ) {
			output.append(" "+ITS_PREFIX+attrName+"=\""+value+"\"");
		}
	}

	private void printITSBooleanAttribute (Boolean value,
		String attrName,
		StringBuilder output)
	{
		if ( value != null ) {
			output.append(" "+ITS_PREFIX+attrName+"=\""+(value ? "yes" : "no")+"\"");
		}
	}

}

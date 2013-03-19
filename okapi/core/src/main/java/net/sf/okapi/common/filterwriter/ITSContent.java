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

package net.sf.okapi.common.filterwriter;

import java.nio.charset.CharsetEncoder;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.annotation.IssueAnnotation;

/**
 * Utilities for writing out ITS markup.
 */
public class ITSContent {

	/**
	 * URI for the ITS namespace
	 */
	public static final String ITS_NS_URI = "http://www.w3.org/2005/11/its";
	
	/**
	 * Marker used in a skeleton part to indicate where standoff markup can be inserted when merging.
	 */
	public static final String STANDOFFMARKER = "$#@StandOff@#$";

	public static final String REF_PREFIX = "REF:";
	
	private CharsetEncoder encoder;
	private boolean isHTML5;
	private boolean isXLIFF;
	private List<GenericAnnotations> standoff;
	private String prefix;
	
	/**
	 * Creates an ITSContent object with a given character set encoder.
	 * @param encoder the character set encoder to use (can be null for UTF-8)
	 * @param isHTML5 true to generate markup for HTML5, false for XML.
	 * @param isXLIFF true if the XML output is XLIFF, false for generic ITS.
	 * This parameter is ignored if <code>isHTML5</code is true.
	 */
	public ITSContent (CharsetEncoder encoder,
		boolean isHTML5,
		boolean isXLIFF)
	{
		if ( isHTML5 && isXLIFF ) {
			throw new InvalidParameterException("You can have both isHTML5 and isXLIFF true at the same time");
		}
		this.encoder = encoder;
		this.isHTML5 = isHTML5;
		this.isXLIFF = isXLIFF;
		this.prefix = (isHTML5 ? "its-" : "its:");
	}

	/**
	 * Output the standoff markup for this object and clear it afterward.
	 * This is the same as calling <code>this.writeStandoffLQI(this.getStandoff());</code> then <code>this.clearStandoff()</code>
	 * @return the generated output.
	 */
	public String writeStandoffLQI () {
		String res = writeStandoffLQI(getStandoff());
		clearStandoff();
		return res;
	}
	
	/**
	 * Output all the Localization Quality issue annotation groups in a given list.
	 * The given standoff items are not cleared automatically. 
	 * @param annotations the list of annotation set to process.
	 * @return the generated output.
	 */
	public String writeStandoffLQI (List<GenericAnnotations> annotations) {
		if ( annotations == null ) return "";
		StringBuilder sb = new StringBuilder();
		for ( GenericAnnotations anns : annotations ) {
			// Check if we have something to output
			List<GenericAnnotation> list = anns.getAnnotations(GenericAnnotationType.LQI);
			if ( list.isEmpty() ) continue;
			// Output
			if ( isHTML5 ) {
				sb.append("<script id=\""+anns.getData()+"\" type=\"application/its+xml\">\n");
				sb.append("<its:locQualityIssues xmlns:its=\""+ITS_NS_URI+"\" version=\"2.0\" ");
			}
			else {
				sb.append("<its:locQualityIssues ");
			}
			
			sb.append("xml:id=\""+anns.getData()+"\">\n");
			for ( GenericAnnotation ann : list ) {
				sb.append("<its:locQualityIssue");
				String strVal = ann.getString(GenericAnnotationType.LQI_COMMENT);
				if ( strVal != null ) {
					sb.append(" locQualityIssueComment=\"" + Util.escapeToXML(strVal, 3, false, encoder) + "\"");
				}
				Boolean booVal = ann.getBoolean(GenericAnnotationType.LQI_ENABLED);
				if (( booVal != null ) && !booVal ) {
					sb.append(" locQualityIssueEnabled=\"no\"");
				}
				strVal = ann.getString(GenericAnnotationType.LQI_PROFILEREF);
				if ( strVal != null ) {
					sb.append(" locQualityIssueProfileRef=\"" + Util.escapeToXML(strVal, 3, false, encoder) + "\"");
				}
				Double dblVal = ann.getDouble(GenericAnnotationType.LQI_SEVERITY);
				if ( dblVal != null ) {
					sb.append(" locQualityIssueSeverity=\"" + Util.formatDouble(dblVal) + "\"");
				}
				strVal = ann.getString(GenericAnnotationType.LQI_TYPE);
				if ( strVal != null ) {
					sb.append(" locQualityIssueType=\"" + strVal + "\"");
				}
				// Extended data
				if ( ann instanceof IssueAnnotation ) {
					IssueAnnotation iann = (IssueAnnotation)ann;
					sb.append(" okp:lqiType=\"" + iann.getIssueType().toString() + "\"");
					sb.append(String.format(" okp:lqiPos=\"%d %d %d %d\"",
						iann.getSourceStart(), iann.getSourceEnd(), iann.getTargetStart(), iann.getTargetEnd()));
					strVal = iann.getCodes();
					if ( strVal != null ) {
						sb.append(" okp:lqiCodes=\"" + Util.escapeToXML(strVal, 3, false, encoder) + "\"");
					}
					strVal = iann.getSegId();
					if ( strVal != null ) {
						sb.append(" okp:lqiSegId=\"" + Util.escapeToXML(strVal, 3, false, encoder) + "\"");
					}
				}
				// End
				sb.append("/>\n");
			}
			sb.append("</its:locQualityIssues>\n");
			if ( isHTML5 ) {
				sb.append("</script>\n");
			}
		}
		return sb.toString();
	}
		
	public String writeStandoffProvenance (List<GenericAnnotations> annotations) {
		StringBuilder sb = new StringBuilder();
		for ( GenericAnnotations anns : annotations ) {
			// Check if we have something to output
			List<GenericAnnotation> list = anns.getAnnotations(GenericAnnotationType.PROV);
			if ( list.isEmpty() ) continue;
			// Output
			if ( isHTML5 ) {
				sb.append("<script id=\""+anns.getData()+"\" type=\"application/its+xml\">");
			}
			sb.append("<its:provenanceRecords xmlns:its=\""+ITS_NS_URI+"\" version=\"2.0\" ");
			sb.append("xml:id=\""+anns.getData()+"\">");
			for ( GenericAnnotation ann : list ) {
				sb.append("<its:provenanceRecord");
				String strVal = ann.getString(GenericAnnotationType.PROV_PERSON);
				if ( strVal != null ) sb.append(outputRefOrValue(" person", strVal, false));
				strVal = ann.getString(GenericAnnotationType.PROV_ORG);
				if ( strVal != null ) sb.append(outputRefOrValue(" org", strVal, false));
				strVal = ann.getString(GenericAnnotationType.PROV_TOOL);
				if ( strVal != null ) sb.append(outputRefOrValue(" tool", strVal, false));
				strVal = ann.getString(GenericAnnotationType.PROV_REVPERSON);
				if ( strVal != null ) sb.append(outputRefOrValue(" revPerson", strVal, false));
				strVal = ann.getString(GenericAnnotationType.PROV_REVORG);
				if ( strVal != null ) sb.append(outputRefOrValue(" revOrg", strVal, false));
				strVal = ann.getString(GenericAnnotationType.PROV_REVTOOL);
				if ( strVal != null ) sb.append(outputRefOrValue(" revTool", strVal, false));
				strVal = ann.getString(GenericAnnotationType.PROV_PROVREF);
				if ( strVal != null ) sb.append(" provRef=\"" + Util.escapeToXML(strVal, 3, false, encoder) + "\"");
				sb.append("/>");
			}
			sb.append("</its:provenanceRecords>");
			if ( isHTML5 ) {
				sb.append("</script>");
			}
		}
		return sb.toString();
	}

	private String outputRefOrValue (String partialName,
		String value,
		boolean useHTML5Notation)
	{
		if ( value.startsWith(GenericAnnotationType.REF_PREFIX) ) {
			value = value.substring(GenericAnnotationType.REF_PREFIX.length());
			partialName = partialName + (useHTML5Notation ? "-ref" : "Ref");
		}
		return partialName+"=\""+Util.escapeToXML(value, 3, false, encoder)+"\"";
	}

//	/**
//	 * Generates the markup for the ITS attributes for a given annotation set on an inline code.
//	 * @param code the code where the annotation set is localed.
//	 * @param output the buffer where to append the output.
//	 */
//	public void outputAnnotations (Code code,
//		StringBuilder output)
//	{
//		outputAnnotations(code.getGenericAnnotations(), output, true);
//	}

	/**
	 * Generates the markup for the ITS attributes for a given annotation set.
	 * @param anns the annotations set (can be null).
	 * @param output the buffer where to append the output.
	 * @param inline true if the element is an inline element (e.g. XLIFF mrk).
	 */
	public void outputAnnotations (GenericAnnotations anns,
		StringBuilder output,
		boolean inline)
	{
		if ( anns == null ) return;
		
		boolean hasTerm = false;
		for ( GenericAnnotation ann : anns ) {
			// AnnotatorsRef
			//TODO

			// Text Analysis
			if ( ann.getType().equals(GenericAnnotationType.TA) ) {
				
				printITSStringAttribute(ann.getString(GenericAnnotationType.TA_CLASS),
					(isHTML5 ? "ta-class" : "taClass"), output);
				printITSDoubleAttribute(ann.getDouble(GenericAnnotationType.TA_CONFIDENCE),
					(isHTML5 ? "ta-confidence" : "taConfidence"), output);
				printITSStringAttribute(ann.getString(GenericAnnotationType.TA_IDENT),
					(isHTML5 ? "ta-ident" : "taIdent"), output);
				printITSStringAttribute(ann.getString(GenericAnnotationType.TA_SOURCE),
					(isHTML5 ? "ta-source" : "taSource"), output);
			}
			
			// Terminology
			else if ( ann.getType().equals(GenericAnnotationType.TERM) ) {
				hasTerm = true;
				if ( !(isXLIFF && inline) ) {
					printITSBooleanAttribute(true, "term", output);
				}
				printITSDoubleAttribute(ann.getDouble(GenericAnnotationType.TERM_CONFIDENCE),
					(isHTML5 ? "term-confidence" : "termConfidence"), output);
				// If it's not a Ref info, we must used a user-define attribute because there is no local ITS termInfo attribute
				String value = ann.getString(GenericAnnotationType.TERM_INFO);
				if ( value != null ) {
					if ( value.startsWith(REF_PREFIX) ) {
						String ref = (isHTML5 ? "-ref" : "Ref");
						value = value.substring(REF_PREFIX.length());
						output.append(" "+prefix+(isHTML5 ? "term-info" : "termInfo")+ref+"=\""+Util.escapeToXML(value, 3, false, encoder)+"\"");
					}
					else {
						output.append((isHTML5 ? " data-its-term-info" : " comment")+"=\""+Util.escapeToXML(value, 3, false, encoder)+"\"");
					}
				}
			}
			
			// Allowed Characters
			else if ( ann.getType().equals(GenericAnnotationType.ALLOWEDCHARS) ) {
				printITSStringAttribute(ann.getString(GenericAnnotationType.ALLOWEDCHARS_VALUE),
					(isHTML5 ? "allowed-characters" : "allowedCharacters"), output);
			}
			
			// Storage Size
			else if ( ann.getType().equals(GenericAnnotationType.STORAGESIZE) ) {
				printITSIntegerAttribute(ann.getInteger(GenericAnnotationType.STORAGESIZE_SIZE),
					(isHTML5 ? "storage-size" : "storageSize"), output);
				String tmp = ann.getString(GenericAnnotationType.STORAGESIZE_ENCODING);
				if ( !tmp.equals("UTF-8") ) printITSStringAttribute(tmp,
					(isHTML5 ? "storage-encoding" : "storageEncoding"), output);
				tmp = ann.getString(GenericAnnotationType.STORAGESIZE_LINEBREAK);
				if ( !tmp.equals("lf") ) printITSStringAttribute(tmp,
					(isHTML5 ? "storage-linebreak" : "storageLinebreak"), output);
			}
			
			// Localization Note
			else if ( ann.getType().equals(GenericAnnotationType.LOCNOTE) ) {
				if ( inline && !isHTML5 ) {
					// in mrk element
					output.append(" comment=\""+Util.escapeToXML(ann.getString(GenericAnnotationType.LOCNOTE_VALUE), 3, false, encoder)+"\"");
					printITSExtStringAttribute(ann.getString(GenericAnnotationType.LOCNOTE_TYPE), "locNoteType", output);
				}
				else {
					printITSStringAttribute(ann.getString(GenericAnnotationType.LOCNOTE_VALUE),
						(isHTML5 ? "loc-note" : "locNote"), output);
					printITSIntegerAttribute(ann.getInteger(GenericAnnotationType.LOCNOTE),
						(isHTML5 ? "loc-note" : "locNote"), output);
				}
			}

			// Domain
			else if ( ann.getType().equals(GenericAnnotationType.DOMAIN) ) {
				printITSExtStringAttribute(ann.getString(GenericAnnotationType.DOMAIN_VALUE), "domain", output);
			}
			
			// External Resource
			else if ( ann.getType().equals(GenericAnnotationType.EXTERNALRES) ) {
				printITSExtStringAttribute(ann.getString(GenericAnnotationType.EXTERNALRES_VALUE), "externalResourceRef", output);
			}
			
			// Localization Quality issue
			else if ( ann.getType().equals(GenericAnnotationType.LQI) ) {
				continue; // LQI are dealt with separately
			}
			
			// Provenance
			else if ( ann.getType().equals(GenericAnnotationType.PROV) ) {
				continue; // Provenance are dealt with separately
			}
		}
			
		// Deal with LQI information
		List<GenericAnnotation> list = anns.getAnnotations(GenericAnnotationType.LQI);
		if ( list.size() == 1 ) {
			// If there is only one QI entry: we output it locally
			GenericAnnotation ann = list.get(0);
			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_COMMENT),
				(isHTML5 ? "loc-quality-issue-comment" : "locQualityIssueComment"), output);
			Boolean booVal = ann.getBoolean(GenericAnnotationType.LQI_ENABLED);
			if (( booVal != null ) && !booVal ) { // Output only non-default value (if one is set)
				printITSBooleanAttribute(booVal,
					(isHTML5 ? "loc-quality-issue-enabled" : "locQualityIssueEnabled"), output);
			}
			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_PROFILEREF),
				(isHTML5 ? "loc-quality-issue-profile" : "locQualityIssueProfile"), output);
			printITSDoubleAttribute(ann.getDouble(GenericAnnotationType.LQI_SEVERITY),
				(isHTML5 ? "loc-quality-issue-severity" : "locQualityIssueSeverity"), output);
			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_TYPE),
				(isHTML5 ? "loc-quality-issue-type" : "locQualityIssueType"), output);

			// Extended data
			if ( ann instanceof IssueAnnotation ) {
				IssueAnnotation iann = (IssueAnnotation)ann;
				output.append(" okp:lqiType=\"" + iann.getIssueType().toString() + "\"");
				output.append(String.format(" okp:lqiPos=\"%d %d %d %d\"",
					iann.getSourceStart(), iann.getSourceEnd(), iann.getTargetStart(), iann.getTargetEnd()));
				String strVal = iann.getCodes();
				if ( strVal != null ) {
					output.append(" okp:lqiCodes=\"" + Util.escapeToXML(strVal, 3, false, encoder) + "\"");
				}
				strVal = iann.getSegId();
				if ( strVal != null ) {
					output.append(" okp:lqiSegId=\"" + Util.escapeToXML(strVal, 3, false, encoder) + "\"");
				}
			}
		}
		else if ( list.size() > 1 ) {
			// If there are 2 or more: the items need to be output as standoff markup.
			// Inside a ,script> at the end of the document.
			String refId = anns.getData(); // ID to use should already be set
			if ( refId == null ) { // But check and possibly fix anyway
				anns.setData(Util.makeId(UUID.randomUUID().toString()));
				refId = anns.getData();
			}
			if ( isHTML5 ) output.append(" its-loc-quality-issues-ref=\"#"+refId+"\"");
			else output.append(" its:locQualityIssuesRef=\"#"+refId+"\"");
			// Create a standoff list and copy the items
			GenericAnnotations newSet = new GenericAnnotations();
			newSet.setData(refId);
			newSet.addAll(list);
			if ( standoff == null ) standoff = new ArrayList<GenericAnnotations>();
			standoff.add(newSet);
		}

		// Deal with Provenance information
		list = anns.getAnnotations(GenericAnnotationType.PROV);
		if ( list.size() == 1 ) {
			// If there is only one QI entry: we output it locally
			GenericAnnotation ann = list.get(0);
			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_COMMENT),
				(isHTML5 ? "loc-quality-issue-comment" : "locQualityIssueComment"), output);
			Boolean booVal = ann.getBoolean(GenericAnnotationType.LQI_ENABLED);
			if (( booVal != null ) && !booVal ) { // Output only non-default value (if one is set)
				printITSBooleanAttribute(booVal, "locQualityIssueEnabled", output);
			}
			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_PROFILEREF),
				(isHTML5 ? "loc-quality-issue-profile" : "locQualityIssueProfile"), output);
			printITSDoubleAttribute(ann.getDouble(GenericAnnotationType.LQI_SEVERITY),
				(isHTML5 ? "loc-quality-issue-severity" : "locQualityIssueSeverity"), output);
			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_TYPE),
				(isHTML5 ? "loc-quality-issue-type" : "locQualityIssueType"), output);
			
		}
		else if ( list.size() > 0 ) { // For now all as standoff
			// If there are 2 or more: the items need to be output as standoff markup.
			// Inside a ,script> at the end of the document.
			String refId = anns.getData(); // ID to use should already be set
			if ( refId == null ) { // But check and possibly fix anyway
				anns.setData(Util.makeId(UUID.randomUUID().toString()));
				refId = anns.getData();
			}
			if ( isHTML5 ) output.append(" its-provenance-records-ref=\"#"+refId+"\"");
			else output.append(" its:provenanceRecordsRef=\"#"+refId+"\"");
			GenericAnnotations newSet = new GenericAnnotations();
			newSet.setData(refId);
			newSet.addAll(list);
			if ( standoff == null ) standoff = new ArrayList<GenericAnnotations>();
			standoff.add(newSet);
		}
		
		// Output mtype if needed
		if ( isXLIFF && inline ) {
			output.append(" mtype=\"" + (hasTerm ? "term" : "x-its") + "\"");
		}
	}
	
	/**
	 * Gets the current standoff markup.
	 * @return the current standoff markup (can be null)
	 */
	public List<GenericAnnotations> getStandoff () {
		return standoff;
	}
	
	/**
	 * Indicates if this object has at least standoff item.
	 * @return true if this object has at least standoff item, false otherwise.
	 */
	public boolean hasStandoff () {
		return !Util.isEmpty(standoff);
	}
	
	/**
	 * Clears the standoff markup.
	 */
	public void clearStandoff () {
		standoff = null;
	}

	private void printITSStringAttribute (String value,
		String attrName,
		StringBuilder output)
	{
		if ( value != null ) {
			String ref = "";
			if ( value.startsWith(REF_PREFIX) ) {
				ref = (isHTML5 ? "-ref" : "Ref");
				value = value.substring(REF_PREFIX.length());
			}
			output.append(" "+prefix+attrName+ref+"=\""+Util.escapeToXML(value, 3, false, encoder)+"\"");
		}
	}

	private void printITSExtStringAttribute (String value,
		String attrName,
		StringBuilder output)
	{
		if ( value != null ) {
			output.append((isHTML5 ? " data-" : " itsx:")+attrName+"=\""+Util.escapeToXML(value, 3, false, encoder)+"\"");
		}
	}

	private void printITSDoubleAttribute (Double value,
		String attrName,
		StringBuilder output)
	{
		if ( value != null ) {
			output.append(" "+prefix+attrName+"=\""+Util.formatDouble(value)+"\"");
		}
	}

	private void printITSIntegerAttribute (Integer value,
		String attrName,
		StringBuilder output)
	{
		if ( value != null ) {
			output.append(" "+prefix+attrName+"=\""+value+"\"");
		}
	}

	private void printITSBooleanAttribute (Boolean value,
		String attrName,
		StringBuilder output)
	{
		if ( value != null ) {
			output.append(" "+prefix+attrName+"=\""+(value ? "yes" : "no")+"\"");
		}
	}

}

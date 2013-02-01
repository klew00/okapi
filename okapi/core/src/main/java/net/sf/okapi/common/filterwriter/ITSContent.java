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
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.annotation.IssueAnnotation;
import net.sf.okapi.common.resource.Code;

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

	public static final String ITS_PREFIX = "its:";
	public static final String REF_PREFIX = "REF:";
	
	private CharsetEncoder encoder;
	private boolean isHTML5;
	private List<GenericAnnotations> standoff;
	
	/**
	 * Creates an ITSContent object with a given character set encoder.
	 * @param encoder the character set encoder to use (can be null for UTF-8)
	 * @param isHTML5 true to general markup for HTML5, false for XML.
	 */
	public ITSContent (CharsetEncoder encoder,
		boolean isHTML5)
	{
		this.encoder = encoder;
		this.isHTML5 = isHTML5;
	}

	/**
	 * Output all the Localization Quality issue annotation groups in a given list.
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
				sb.append("<script id=\""+anns.getData()+"\" type=\"application/its+xml\">");
				sb.append("<its:locQualityIssues xmlns:its=\""+ITS_NS_URI+"\" version=\"2.0\" ");
			}
			else {
				sb.append("<its:locQualityIssues ");
			}
			
			sb.append("xml:id=\""+anns.getData()+"\">");
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
				sb.append("/>");
			}
			sb.append("</its:locQualityIssues>");
			if ( isHTML5 ) {
				sb.append("</script>");
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

	/**
	 * Generates the markup for the ITS attributes for a given annotation set on an inline code.
	 * @param code the code where the annotation set is localed.
	 * @param output the buffer where to append the output.
	 */
	public void outputAnnotations (Code code,
		StringBuilder output)
	{
		outputAnnotations((GenericAnnotations)code.getAnnotation(GenericAnnotationType.GENERIC), output);
	}

	/**
	 * Generates the markup for the ITS attributes for a given annotation set.
	 * @param anns the annotations set (can be null).
	 * @param output the buffer where to append the output.
	 */
	public void outputAnnotations (GenericAnnotations anns,
		StringBuilder output)
	{
		standoff = null;
		if ( anns == null ) return;
		
		for ( GenericAnnotation ann : anns ) {
			// AnnotatorsRef
			//TODO
//TODO: XML
			// Disambiguation
			if ( ann.getType().equals(GenericAnnotationType.DISAMB) ) {
				
				printITSStringAttribute(ann.getString(GenericAnnotationType.DISAMB_CLASS), "disambig-class", output);
				printITSDoubleAttribute(ann.getDouble(GenericAnnotationType.DISAMB_CONFIDENCE), "disambigConfidence", output);
				//TODO: needs annotatorsRef if confidence is there
				String value = ann.getString(GenericAnnotationType.DISAMB_GRANULARITY);
				if ( !value.equals(GenericAnnotationType.DISAMB_GRANULARITY_ENTITY) ) { // Output only the non-default value
					printITSStringAttribute(value, "disambig-granularity", output);
				}
				printITSStringAttribute(ann.getString(GenericAnnotationType.DISAMB_IDENT), "disambig-ident", output);
				printITSStringAttribute(ann.getString(GenericAnnotationType.DISAMB_SOURCE), "disambig-source", output);
			}
			
			// Terminology
			else if ( ann.getType().equals(GenericAnnotationType.TERM) ) {
				printITSBooleanAttribute(true, "term", output);
				printITSDoubleAttribute(ann.getDouble(GenericAnnotationType.TERM_CONFIDENCE),
					(isHTML5 ? "term-confidence" : "termConfidence"), output);
				printITSStringAttribute(ann.getString(GenericAnnotationType.TERM_INFO),
					(isHTML5 ? "term-info" : "termInfo"), output);
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

			// Domain
			else if ( ann.getType().equals(GenericAnnotationType.DOMAIN) ) {
				printITSExtStringAttribute(ann.getString(GenericAnnotationType.DOMAIN_VALUE), "itsDomain", output);
			}
			
			// External Resource
			else if ( ann.getType().equals(GenericAnnotationType.EXTERNALRES) ) {
				printITSExtStringAttribute(ann.getString(GenericAnnotationType.EXTERNALRES_VALUE), "itsExternalResourceRef", output);
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
			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_COMMENT), "loc-quality-issue-comment", output);
			Boolean booVal = ann.getBoolean(GenericAnnotationType.LQI_ENABLED);
			if (( booVal != null ) && !booVal ) { // Output only non-default value (if one is set)
				printITSBooleanAttribute(booVal, "loc-quality-issue-enabled", output);
			}
			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_PROFILEREF), "loc-quality-issue-profile", output);
			printITSDoubleAttribute(ann.getDouble(GenericAnnotationType.LQI_SEVERITY), "loc-quality-issue-severity", output);
			printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_TYPE), "loc-quality-issue-type", output);
		}
		else if ( list.size() > 1 ) {
			// If there are 2 or more: the items need to be output as standoff markup.
			// Inside a ,script> at the end of the document.
			String refId = anns.getData(); // ID to use is already generated in the annotation
			if ( isHTML5 ) output.append(" its-loc-quality-issues-ref=\"#"+refId+"\"");
			else output.append(" its:locQualityIssuesRef=\"#"+refId+"\"");
			if ( standoff == null ) standoff = new ArrayList<GenericAnnotations>();
			GenericAnnotations newSet = new GenericAnnotations();
			standoff.add(newSet);
			newSet.setData(refId);
			newSet.addAll(list);
		}

		// Deal with Provenance information
		list = anns.getAnnotations(GenericAnnotationType.PROV);
		if ( list.size() == 1 ) {
//TODO			
			// If there is only one QI entry: we output it locally
//				GenericAnnotation ann = list.get(0);
//				printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_COMMENT), "loc-quality-issue-comment", output);
//				Boolean booVal = ann.getBoolean(GenericAnnotationType.LQI_ENABLED);
//				if (( booVal != null ) && !booVal ) { // Output only non-default value (if one is set)
//					printITSBooleanAttribute(booVal, "loc-quality-issue-enabled", output);
//				}
//				printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_PROFILEREF), "loc-quality-issue-profile", output);
//				printITSDoubleAttribute(ann.getDouble(GenericAnnotationType.LQI_SEVERITY), "loc-quality-issue-severity", output);
//				printITSStringAttribute(ann.getString(GenericAnnotationType.LQI_TYPE), "loc-quality-issue-type", output);
		}
		else if ( list.size() > 0 ) { // For now all as standoff
			// If there are 2 or more: the items need to be output as standoff markup.
			// Inside a ,script> at the end of the document.
			String refId = anns.getData(); // ID to use is already generated in the annotation
			if ( isHTML5 ) output.append(" its-provenance-records-ref=\"#"+refId+"\"");
			else output.append(" its:provenanceRecordsRef=\"#"+refId+"\"");
			if ( standoff == null ) standoff = new ArrayList<GenericAnnotations>();
			GenericAnnotations newSet = new GenericAnnotations();
			standoff.add(newSet);
			newSet.setData(refId);
			newSet.addAll(list);
		}
	}
	
	public List<GenericAnnotations> getStandoff () {
		return standoff;
	}

	private void printITSStringAttribute (String value,
		String attrName,
		StringBuilder output)
	{
		if ( value != null ) {
			String ref = "";
			if ( value.startsWith(REF_PREFIX) ) {
				ref = "Ref";
				value = value.substring(REF_PREFIX.length());
			}
			output.append(" "+ITS_PREFIX+attrName+ref+"=\""+Util.escapeToXML(value, 3, false, encoder)+"\"");
		}
	}

	private void printITSExtStringAttribute (String value,
		String attrName,
		StringBuilder output)
	{
		if ( value != null ) {
			output.append(" okp:"+attrName+"=\""+Util.escapeToXML(value, 3, false, encoder)+"\"");
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

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
import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;

/**
 * Utilities for writing out ITS markup.
 */
public class ITSContent {

	public static final String ITS_NS_URI = "http://www.w3.org/2005/11/its";
	
	private CharsetEncoder encoder;
	private boolean isHTML5;
	
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
		StringBuilder sb = new StringBuilder();
		for ( GenericAnnotations anns : annotations ) {
			// Check if we have something to output
			List<GenericAnnotation> list = anns.getAnnotations(GenericAnnotationType.LQI);
			if ( list.isEmpty() ) continue;
			// Output
			if ( isHTML5 ) {
				sb.append("<script id=\""+anns.getData()+"\" type=\"application/its+xml\">");
			}
			sb.append("<its:locQualityIssues xmlns:its=\""+ITS_NS_URI+"\" version=\"2.0\" ");
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
}

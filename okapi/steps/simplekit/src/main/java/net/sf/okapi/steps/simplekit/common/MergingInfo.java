/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.steps.simplekit.common;

import org.w3c.dom.Element;

import net.sf.okapi.common.Base64;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;

public class MergingInfo {

	private static final String INVALIDVALUE = "Invalid value for attribute '%s'.";

	private int docId;
	private String extractionType;
	private String relativeInputPath;
	private String filterId;
	private String filterParameters;
	private String inputEncoding;
	private String relativeTargetPath;
	private String targetEncoding;

	/**
	 * Creates a new merging information file object with no settings.
	 */
	public MergingInfo () {
	}
	
	/**
	 * Creates a new merging information file object.
	 * @param docId the document id in the manifest/batch.
	 * @param relativeInputPath the relative input path of the extracted document
	 * @param filterId the id of the filter used to extract.
	 * @param filterParameters the parameters used to extract (can be null).
	 * @param inputEncoding the encoding used to extract.
	 * @param relativeTargetPath the relative output path for the merged file relative to the root.
	 * @param targetEncoding the default encoding for the merged file.
	 */
	public MergingInfo (int docId,
		String extractionType,
		String relativeInputPath,
		String filterId,
		String filterParameters,
		String inputEncoding,
		String relativeTargetPath,
		String targetEncoding)
	{
		this.docId = docId;
		this.extractionType = extractionType;
		this.relativeInputPath = relativeInputPath;
		this.filterId = filterId;
		this.filterParameters = filterParameters;
		this.inputEncoding = inputEncoding;
		this.relativeTargetPath = relativeTargetPath;
		this.targetEncoding = targetEncoding;
	}
	
	public int getDocId () {
		return docId;
	}
	
	public String getExtractionType () {
		return extractionType;
	}
	
	public String getRelativeInputPath () {
		return relativeInputPath;
	}
	
	public String getFilterId () {
		return filterId;
	}

	public String getFilterParameters () {
		return filterParameters;
	}

	public String getInputEncoding () {
		return inputEncoding;
	}

	public String getRelativeTargetPath () {
		return relativeTargetPath;
	}

	public String getTargetEncoding () {
		return targetEncoding;
	}
	
	/**
	 * Creates a string output of the XML representation of the information.
	 * The output file must be UTF-8.
	 * @param elementQName the name of the element that encloses the data.
	 * @param base64 true if the content needs to be encoded in Base64.
	 * @return the XML representation of the information.
	 */
	public String writeToXML (String elementQName,
		boolean base64)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("<%s xml:space=\"preserve\" docId=\"%d\" extractionType=\"%s\" relativeInputPath=\"%s\" "
			+ "filterId=\"%s\" inputEncoding=\"%s\" relativeTargetPath=\"%s\" targetEncoding=\"%s\"",
			elementQName, docId, extractionType, Util.escapeToXML(relativeInputPath, 3, false, null),
			filterId, inputEncoding, Util.escapeToXML(relativeTargetPath, 3, false, null), targetEncoding
		));
		if ( filterParameters == null ) {
			// Empty element
			sb.append(" />");
		}
		else { // Write the content
			String tmp = Util.escapeToXML(filterParameters, 0, false, null);
			if ( base64 ) {
				tmp = Base64.encodeString(tmp);
			}
			sb.append(">" + tmp + String.format("</%s>", elementQName));
		}
		return sb.toString();
	}

	/**
	 * Reads the merging information from an XML element created with {@link #writeToXML(String, boolean)}.
	 * @param element the element containing the information.
	 * @return a new MergingInfo object with its values set to values stored in the element.
	 */
	static public MergingInfo readFromXML (Element element) {
		MergingInfo info = new MergingInfo();
		
		String tmp = element.getAttribute("docId");
		if ( tmp.isEmpty() ) throw new OkapiIOException(String.format(INVALIDVALUE, "docId"));
		try {
			info.docId = Integer.parseInt(tmp);
		}
		catch ( NumberFormatException e ) {
			throw new OkapiIOException(String.format(INVALIDVALUE, "docId"));
		}
		
		tmp = element.getAttribute("extractionType");
		if ( tmp.isEmpty() ) throw new OkapiIOException(String.format(INVALIDVALUE, "extractionType"));
		info.extractionType = tmp;
		
		tmp = element.getAttribute("relativeInputPath");
		if ( tmp.isEmpty() ) throw new OkapiIOException(String.format(INVALIDVALUE, "relativeInputPath"));
		info.relativeInputPath = tmp;
		
		tmp = element.getAttribute("filterId");
		if ( tmp.isEmpty() ) throw new OkapiIOException(String.format(INVALIDVALUE, "filterId"));
		info.filterId = tmp;
		
		tmp = element.getAttribute("inputEncoding");
		if ( tmp.isEmpty() ) throw new OkapiIOException(String.format(INVALIDVALUE, "inputEncoding"));
		info.inputEncoding = tmp;
		
		tmp = element.getAttribute("relativeTargetPath");
		if ( tmp.isEmpty() ) throw new OkapiIOException(String.format(INVALIDVALUE, "relativeTargetPath"));
		info.relativeTargetPath = tmp;
		
		tmp = element.getAttribute("targetEncoding");
		if ( tmp.isEmpty() ) info.targetEncoding = info.inputEncoding;
		else info.targetEncoding = tmp;
		
		// Read the content (filter parameters data)
		tmp = Util.getTextContent(element);
		if ( !tmp.isEmpty() ) {
			// If it does not start with version info, it's in Base64
			if ( !tmp.startsWith("#v") ) {
				tmp = Base64.decodeString(tmp);
			}
			info.filterParameters = tmp;
		}
		return info;
	}

}
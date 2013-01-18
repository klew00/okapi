/*===========================================================================
  Copyright (C) 2010-2013 by the Okapi Framework contributors
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

package net.sf.okapi.lib.verification;

import java.net.URI;
import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.IssueType;
import net.sf.okapi.common.resource.Code;

public class Issue extends GenericAnnotation {

	public static final int SEVERITY_LOW = 0;
	public static final int SEVERITY_MEDIUM = 1;
	public static final int SEVERITY_HIGH = 2;
	
	private URI docURI;
	private IssueType issueType;
	private String tuId;
	private String segId;
	private String tuName;
	private int trgStart;
	private int trgEnd;
	private List<Code> codes;
	private String source;
	private String target;
	
	public Issue (URI docId,
		IssueType issueType,
		String tuId,
		String segId,
		String message, 
		int srcStart, 
		int srcEnd, 
		int trgStart, 
		int trgEnd,
		int severity,
		String tuName)
	{
		super(GenericAnnotationType.LQI);
		this.docURI = docId;
		this.issueType = issueType;
		this.tuId = tuId;
		this.segId = segId;
		setString(GenericAnnotationType.LQI_COMMENT, message);
		setInteger(GenericAnnotationType.LQI_XSTART, srcStart);
		setInteger(GenericAnnotationType.LQI_XEND, srcEnd);
		this.trgStart = trgStart;
		this.trgEnd = trgEnd;
		setInteger(GenericAnnotationType.LQI_SEVERITY, severity);
		this.tuName = tuName;
	}
		
	public URI getDocumentURI () {
		return docURI;
	}
		
	public IssueType getIssueType () {
		return issueType;
	}
		
	public String getITSType () {
		return getString(GenericAnnotationType.LQI_TYPE);
	}
		
	public String getTuId () {
		return tuId;
	}
	
	public String getTuName () {
		return tuName;
	}
	
	public String getSegId () {
		return segId;
	}
	
	public int getSourceStart () {
		return getInteger(GenericAnnotationType.LQI_XSTART);
	}
		
	public int getSourceEnd () {
		return getInteger(GenericAnnotationType.LQI_XEND);
	}
		
	public int getTargetStart () {
		return trgStart;
	}
		
	public int getTargetEnd () {
		return trgEnd;
	}
	
	public boolean getEnabled () {
		return getBoolean(GenericAnnotationType.LQI_ENABLED);
	}
	
	public void setEnabled (boolean enabled) {
		setBoolean(GenericAnnotationType.LQI_ENABLED, enabled);
	}
	
	public int getSeverity () {
		return getInteger(GenericAnnotationType.LQI_SEVERITY);
	}
	
	public String getMessage () {
		return getString(GenericAnnotationType.LQI_COMMENT);
	}
	
	public List<Code> getCodes () {
		return codes;
	
	}
	public void setCodes (List<Code> codes) {
		this.codes = codes;
	}
	
	public String getSource () {
		return source;
	}
		
	public void setSource (String source) {
		this.source = source;
	}
		
	public String getTarget () {
		return target;
	}
		
	public void setTarget (String target) {
		this.target = target;
	}
		
	String getSignature () {
		return String.format("%s-%s-%s-%d-%s", docURI, tuId, (segId==null) ? "" : segId, getInteger(GenericAnnotationType.LQI_XSTART), issueType);
	}

	/**
	 * Gets the string representation of the issue.
	 * <p><b>TEST ONLY</b>: The representation in raw XML (ITS 2.0 QA error element).
	 */
	@Override
	public String toString () {
		StringBuilder tmp = new StringBuilder();
		tmp.append("<its:qaItem");
		tmp.append(" docUri=\""+Util.escapeToXML(docURI.getPath(), 3, false, null)+"\"");
		tmp.append(" tuId=\""+Util.escapeToXML(tuId, 3, false, null)+"\"");
		tmp.append(" segId=\""+Util.escapeToXML(segId, 3, false, null)+"\"");
		tmp.append(" tuName=\""+(tuName!=null ? Util.escapeToXML(tuName, 3, false, null) : "")+"\"");
		tmp.append(String.format(" srcStart=\"%d\" srcEnd=\"%d\"", getInteger(GenericAnnotationType.LQI_XSTART), getInteger(GenericAnnotationType.LQI_XEND)));
		tmp.append(String.format(" trgStart=\"%d\" trgEnd=\"%d\"", trgStart, trgEnd));
		tmp.append(String.format(" severity=\"%d\"", getString(GenericAnnotationType.LQI_SEVERITY)));
		tmp.append("><its:qaNote>"+Util.escapeToXML(getString(GenericAnnotationType.LQI_COMMENT), 0, false, null)+"<its:qaNote>");
		tmp.append("</its:qaItem>");
		return tmp.toString();
	}

}

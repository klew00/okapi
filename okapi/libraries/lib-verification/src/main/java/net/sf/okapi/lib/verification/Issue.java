/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

import net.sf.okapi.common.Util;

public class Issue {

	public static final int SEVERITY_LOW = 0;
	public static final int SEVERITY_MEDIUM = 1;
	public static final int SEVERITY_HIGH = 2;
	
	public URI docURI;
	public IssueType issueType;
	public String tuId;
	public String segId;
	public String message;
	public int srcStart;
	public int srcEnd;
	public int trgStart;
	public int trgEnd;
	public boolean enabled;
	public int severity;
	public Object extra;
	// Temporary waiting for DB
	public String tuName;
	public String oriSource;
	public String oriTarget;
	// DB
	public long tuKey;
	public long docKey;
	
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
		this.docURI = docId;
		this.issueType = issueType;
		this.tuId = tuId;
		this.segId = segId;
		this.message = message;
		this.srcStart = srcStart;
		this.srcEnd = srcEnd;
		this.trgStart = trgStart;
		this.trgEnd = trgEnd;
		this.severity = severity;
		this.tuName = tuName;
	}

	String getSignature () {
		return String.format("%s-%s-%s-%d-%s", docURI, tuId, (segId==null) ? "" : segId, srcStart, issueType);
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
		tmp.append(String.format(" srcStart=\"%d\" srcEnd=\"%d\"", srcStart, srcEnd));
		tmp.append(String.format(" trgStart=\"%d\" trgEnd=\"%d\"", trgStart, trgEnd));
		tmp.append(String.format(" severity=\"%d\"", severity));
		tmp.append("><its:qaNote>"+Util.escapeToXML(message, 0, false, null)+"<its:qaNote>");
		tmp.append("</its:qaItem>");
		return tmp.toString();
	}
}

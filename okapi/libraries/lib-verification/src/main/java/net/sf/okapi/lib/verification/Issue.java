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

public class Issue {

	public static final int SEVERITY_LOW = 0;
	public static final int SEVERITY_MEDIUM = 1;
	public static final int SEVERITY_HIGH = 2;
	
	public URI docId;
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
	// Temporary waiting for DB
	public String oriSource;
	public String oriTarget;
	
	public Issue (URI docId,
		IssueType issueType,
		String tuId,
		String segId,
		String message, 
		int srcStart, 
		int srcEnd, 
		int trgStart, 
		int trgEnd,
		int severity)
	{
		this.docId = docId;
		this.issueType = issueType;
		this.tuId = tuId;
		this.segId = segId;
		this.message = message;
		this.srcStart = srcStart;
		this.srcEnd = srcEnd;
		this.trgStart = trgStart;
		this.trgEnd = trgEnd;
		this.severity = severity;
	}

	String getSignature () {
		return String.format("%s-%s-%s-%d-%s", docId, tuId, (segId==null) ? "" : segId, srcStart, issueType);
	}

}

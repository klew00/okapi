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

package net.sf.okapi.common.annotation;

import java.security.InvalidParameterException;
import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.resource.Code;

public class IssueAnnotation extends GenericAnnotation {

	public static final int SEVERITY_LOW    = 0;
	public static final int SEVERITY_MEDIUM = 1;
	public static final int SEVERITY_HIGH   = 2;

	private static final String CODES_SEP = "\u2628";
	
	private IssueType issueType;
	
	public IssueAnnotation (IssueType issueType,
		String comment,
		int severity,
		String segId,
		int srcStart, 
		int srcEnd, 
		int trgStart, 
		int trgEnd,
		List<Code> codes)
	{
		super(GenericAnnotationType.LQI);
		setBoolean(GenericAnnotationType.LQI_ENABLED, true);
		setIssueType(issueType);
		setString(GenericAnnotationType.LQI_COMMENT, comment);
		setSeverity(severity);
		setSourcePosition(srcStart, srcEnd);
		setTargetPosition(trgStart, trgEnd);
		setString(GenericAnnotationType.LQI_XSEGID, segId);
		setCodes(codes);
	}
		
	public IssueType getIssueType () {
		return issueType;
	}
	
	/**
	 * Sets the issue type and its corresponding ITS type mapping.
	 * To override the default mapping, use {@link #setITSType(String)}.
	 * @param issueType the issue type to set.
	 */
	public void setIssueType (IssueType issueType) {
		this.issueType = issueType;
		setString(GenericAnnotationType.LQI_XTYPE, issueType.toString());
		setString(GenericAnnotationType.LQI_TYPE, IssueType.getITSType(issueType));
	}
		
	public String getITSType () {
		return getString(GenericAnnotationType.LQI_TYPE);
	}
	
	public void setITSType (String itsType) {
		setString(GenericAnnotationType.LQI_TYPE, itsType);
	}
	
	public String getSegId () {
		return getString(GenericAnnotationType.LQI_XSEGID);
	}
	
	public int getSourceStart () {
		return getInteger(GenericAnnotationType.LQI_XSTART);
	}
		
	public int getSourceEnd () {
		return getInteger(GenericAnnotationType.LQI_XEND);
	}
		
	public void setSourcePosition (int start,
		int end)
	{
		setInteger(GenericAnnotationType.LQI_XSTART, start);
		setInteger(GenericAnnotationType.LQI_XEND, end);
	}
		
	public int getTargetStart () {
		return getInteger(GenericAnnotationType.LQI_XTRGSTART);
	}
		
	public int getTargetEnd () {
		return getInteger(GenericAnnotationType.LQI_XTRGEND);
	}
	
	public void setTargetPosition (int start,
		int end)
	{
		setInteger(GenericAnnotationType.LQI_XTRGSTART, start);
		setInteger(GenericAnnotationType.LQI_XTRGEND, end);
	}
	
	public boolean getEnabled () {
		return getBoolean(GenericAnnotationType.LQI_ENABLED);
	}
	
	public void setEnabled (boolean enabled) {
		setBoolean(GenericAnnotationType.LQI_ENABLED, enabled);
	}
	
	public double getSeverity () {
		return getDouble(GenericAnnotationType.LQI_SEVERITY);
	}
	
	public void setSeverity (double severity) {
		//TODO: handle severity value mapping
		if (( severity < SEVERITY_LOW ) || ( severity > SEVERITY_HIGH )) {
			throw new InvalidParameterException("Invalid severity value.");
		}
		setDouble(GenericAnnotationType.LQI_SEVERITY, severity);
	}
	
	public String getComment () {
		return getString(GenericAnnotationType.LQI_COMMENT);
	}
	
	public void setComment (String comment) {
		setString(GenericAnnotationType.LQI_COMMENT, comment);
	}
	
	/**
	 * Gets the string list of the codes for this issue.
	 * @return the string list of the codes for this issue.
	 */
	public String getCodes () {
		return getString(GenericAnnotationType.LQI_XCODES);
	}
	
	public String[] getCodesAsArray () {
		String tmp = getString(GenericAnnotationType.LQI_XCODES);
		if ( tmp == null ) return null;
		return tmp.split(CODES_SEP, 0);
	}
	
	public void setCodes (List<Code> codes) {
		if ( Util.isEmpty(codes) ) {
			setString(GenericAnnotationType.LQI_XCODES, null);
		}
		else {
			StringBuilder values = new StringBuilder();
			for ( Code code : codes ) {
				values.append(CODES_SEP);
				values.append(code.getData());
			}
			// Final string is like this: "data1<sep>data2<sep>data3"
			setString(GenericAnnotationType.LQI_XCODES, values.toString().substring(1));
		}
	}

	@Override
	public String toString () {
		setString("lqiXIssueType", issueType.toString());
		return super.toString();
	}
	
	@Override
	public void fromString (String storage) {
		super.fromString(storage);
		issueType = IssueType.valueOf(getString("lqiXIssueType"));
		setString("lqiXIssueType", null);
	}

}

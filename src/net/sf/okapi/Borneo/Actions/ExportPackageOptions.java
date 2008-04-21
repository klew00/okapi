/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
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

package net.sf.okapi.Borneo.Actions;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import net.sf.okapi.Library.Base.BaseParameters;
import net.sf.okapi.Library.Base.FieldsString;
import net.sf.okapi.Library.Base.Utils;

public class ExportPackageOptions extends BaseParameters {

	private String           m_sPackageType;
	private String           m_sName;
	private boolean          m_bCreateZip;
	private SimpleDateFormat m_DF;
	
	public ExportPackageOptions () {
		m_DF = new SimpleDateFormat("yyyyMMddHHmm");
		m_DF.setTimeZone(TimeZone.getTimeZone("GMT"));
		reset();
	}
	
	public void reset () {
		m_sPackageType = "";
		m_sName = "";
		m_bCreateZip = false;
	}
	
	public void fromString (String p_sData) {
		FieldsString FS = new FieldsString(p_sData);
		m_sPackageType = FS.get("packagetype", m_sPackageType);
		m_sName = FS.get("name", m_sName);
		m_bCreateZip = FS.get("zip", m_bCreateZip);
	}
	
	public String toString () {
		FieldsString FS = new FieldsString();
		FS.add("packagetype", m_sPackageType);
		FS.add("name", m_sName);
		FS.add("zip", m_bCreateZip);
		return FS.toString();
	}
	
	/**
	 * Create a package ID and a directory name based on the options.
	 * @param p_sProjectID Identifier of the project.
	 * @param p_sTarget Code of the target language.
	 * @return An array of strings: 0=package ID, 1=package directory name
	 */
	public String[] makePackageName (String p_sProjectID,
		String p_sTarget)
	{
		String[] aRes = new String[2];
		
		// Create the PackageID
		String sTmp = m_DF.format(new java.util.Date());
		aRes[0] = Utils.makeID(sTmp + p_sProjectID);
		
		// Create the directory
		aRes[1] = String.format("%s%s_%s_%s", sTmp,
			((( m_sName != null ) && ( m_sName.length() != 0 )) ? "_"+m_sName : ""),
			p_sTarget, aRes[0]);
			
		return aRes;
	}


	String getPackageType () {
		return m_sPackageType;
	}
	
	void setPackageType (String p_sPackageType) {
		m_sPackageType = p_sPackageType;
	}
	
	String getName () {
		return m_sName;
	}
	
	void setName (String p_sName) {
		m_sName = p_sName;
	}
	
	boolean getCreateZip () {
		return m_bCreateZip;
	}
	
	void setCreateZip (boolean p_bValue) {
		m_bCreateZip = p_bValue;
	}
}

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

import net.sf.okapi.Library.Base.BaseParameters;
import net.sf.okapi.Library.Base.FieldsString;

public class ImportTranslationOptions extends BaseParameters {

	public static final int  NBCASE = 4;
	
	private int         m_nImportType;
	private String      m_sEncoding;
	private String      m_sPath;
	private String      m_sFSettings;
	private boolean     m_bIncludeWithout;
	private boolean     m_bIncludeWith;
	private boolean[]   m_aWithout;
	private boolean[]   m_aWith;
	
	public ImportTranslationOptions () {
		m_aWith = new boolean[NBCASE];
		reset();
	}
	
	public void reset () {
		m_nImportType = 0;
		m_sEncoding = "";
		m_sPath = "";
		m_sFSettings = "";
		m_bIncludeWithout = true;
		m_bIncludeWith = false;
		m_aWithout = new boolean[NBCASE];
		for ( int i=0; i<NBCASE; i++ ) {
			m_aWithout[i] = true;
		}
		m_aWith = new boolean[NBCASE];
		for ( int i=0; i<NBCASE; i++ ) {
			m_aWith[i] = ((i==0) ? true : false);
		}
	}
	
	public void fromString (String p_sData) {
		FieldsString FS = new FieldsString(p_sData);
		m_nImportType = FS.get("importtype", m_nImportType);
		m_sEncoding = FS.get("sourceEncoding", m_sEncoding);
		m_sPath = FS.get("path", m_sPath);
		m_sFSettings = FS.get("fsettings", m_sFSettings);
		m_bIncludeWithout = FS.get("incwithout", m_bIncludeWithout);
		m_bIncludeWith = FS.get("incwith", m_bIncludeWith);
		for ( int i=0; i<NBCASE; i++ ) {
			m_aWithout[i] = FS.get(String.format("without%d", i), m_aWithout[i]);
		}
		for ( int i=0; i<NBCASE; i++ ) {
			m_aWith[i] = FS.get(String.format("with%d", i), m_aWith[i]);
		}
	}
	
	public String toString () {
		FieldsString FS = new FieldsString();
		FS.add("importtype", m_nImportType);
		FS.add("sourceEncoding", m_sEncoding);
		FS.add("path", m_sPath);
		FS.add("fsettings", m_sFSettings);
		FS.add("incwithout", m_bIncludeWithout);
		FS.add("incwith", m_bIncludeWith);
		for ( int i=0; i<NBCASE; i++ ) {
			FS.add(String.format("without%d", i), m_aWithout[i]);
		}
		for ( int i=0; i<NBCASE; i++ ) {
			FS.add(String.format("with%d", i), m_aWith[i]);
		}
		return FS.toString();
	}
	
	public int getImportType () {
		return m_nImportType;
	}
	
	public void setImportType (int p_nValue) {
		m_nImportType = p_nValue;
	}
	
	public String getPath () {
		return m_sPath;
	}
	
	public void setPath (String p_sValue) {
		m_sPath = p_sValue;
	}
	
	public String getFSettings () {
		return m_sFSettings;
	}
	
	public void setFSettings (String p_sValue) {
		m_sFSettings = p_sValue;
	}
	
	public String getEncoding () {
		return m_sEncoding;
	}
	
	public void setEncoding (String p_sValue) {
		m_sEncoding = p_sValue;
	}

	public boolean includeItemsWithoutTranslation () {
		return m_bIncludeWithout;
	}

	public void setIncludeItemsWithoutTranslation (boolean p_bValue) {
		m_bIncludeWithout = p_bValue;
	}

	public boolean includeItemsWithTranslation () {
		return m_bIncludeWith;
	}

	public void setIncludeItemsWithTranslation (boolean p_bValue) {
		m_bIncludeWith = p_bValue;
	}
	
	public boolean[] getScopeWithoutTranslation () {
		return m_aWithout;
	}
	
	public boolean[] getScopeWithTranslation () {
		return m_aWith;
	}
}

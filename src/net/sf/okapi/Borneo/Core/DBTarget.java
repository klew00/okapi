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

package net.sf.okapi.Borneo.Core;

import net.sf.okapi.Library.Base.PathBuilder;

public class DBTarget {
	
	private String         m_sRoot;
	private String         m_sEncoding;
	private PathBuilder    m_DefPB;

	public DBTarget (String p_sLangCode) {
		try {
			m_sRoot = null;
			m_DefPB = new PathBuilder();

			/*TODO: get the right default sourceEncoding
			 *  System.Globalization.CultureInfo CI
				= System.Globalization.CultureInfo.GetCultureInfo(p_sLangCode);
			m_sEncoding = System.Text.Encoding.GetEncoding(CI.TextInfo.ANSICodePage).WebName;
			*/
			m_sEncoding = "windows-1252"; //TODO: change to correct default
		}
		catch ( Exception E ) { // Or fall back to UTF-8
			m_sEncoding = "UTF-8";
		}
	}


	public String getRoot () {
		return m_sRoot;
	}
	
	public void setRoot (String p_sValue) {
		if (( p_sValue == null ) || ( p_sValue.length() == 0 )) 
			m_sRoot = null;
		else
			m_sRoot = p_sValue;
	}

	public String getEncoding () {
		return m_sEncoding;
	}
	
	public void setEncoding (String p_sValue) {
		m_sEncoding = p_sValue;
	}

	public PathBuilder getDefaultPB () {
		return m_DefPB;
	}
}

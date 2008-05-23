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

package net.sf.okapi.applications.rainbow.packages;

public class ManifestItem {

	private String      m_sFile;
	private boolean     m_bSelected;
	private boolean     m_bExist;

	public ManifestItem (String p_sFile,
		boolean p_bSelected)
	{
		m_sFile = p_sFile;
		m_bSelected = p_bSelected;
		m_bExist = true;
	}

	public String getRelativePath () {
		return m_sFile;
	}
	
	public String getRelativeTargetPath () {
		return m_sFile;
	}
	
	public boolean isSelected () {
		return m_bSelected;
	}
	
	public void setSelected (boolean p_bValue) {
		m_bSelected = p_bValue;
	}
	
	public boolean exists () {
		return m_bExist;
	}
	
	public void setExist (boolean p_bValue) {
		m_bExist = p_bValue;
	}
}

/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
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

package net.sf.okapi.applications.rainbow.lib;

import net.sf.okapi.common.ui.UIUtil;

public class LanguageItem {

	public String       name;
	public String       code;
	public String       encodingW;
	public String       encodingM;
	public String       encodingU;
	public int          lcid;
	
	public String toString () {
		return name;
	}
	
	public String getEncoding (int platformType) {
		String sTmp;
		switch ( platformType ) {
		case UIUtil.PFTYPE_MAC:
			sTmp = encodingM;
			break;
		case UIUtil.PFTYPE_UNIX:
			sTmp = encodingU;
			break;
		default:
			sTmp = encodingW;
			break;
		}
		if ( sTmp == null ) return encodingW;
		else return sTmp;
	}
	
	public void setEncoding (String value,
		int platformType)
	{
		switch ( platformType ) {
		case UIUtil.PFTYPE_MAC:
			encodingM = value;
			break;
		case UIUtil.PFTYPE_UNIX:
			encodingU = value;
			break;
		default:
			encodingW = value;
			break;
		}
	}
	
}

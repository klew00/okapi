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

package net.sf.okapi.Library.Segmentation;

class Rule {

	public String     m_sBefore;
	public String     m_sAfter;
	public boolean    m_bBreak;

	public Rule () {
		m_sAfter = "";
		m_sBefore = "";
		m_bBreak = true;
	}
	
	public Rule (String p_sBefore,
		String p_sAfter,
		boolean p_bBreak)
	{
		if ( p_sBefore == null ) m_sBefore = "";
		else m_sBefore = p_sBefore;

		if ( p_sAfter == null ) m_sAfter = "";
		else m_sAfter = p_sAfter;
			
		m_bBreak = p_bBreak;
	}
}

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

package net.sf.okapi.Translation;

import net.sf.okapi.Filter.FilterItem;
import net.sf.okapi.Filter.FilterItemText;
import net.sf.okapi.Filter.IFilterItem;

public class Match implements IMatch {
	
	private int         m_nKind = 0;
	private int         m_nScore = 0;
	private FilterItem  m_SrcFI;
	private FilterItem  m_TrgFI;

	public Match () {
		m_SrcFI = new FilterItem();
		m_TrgFI = new FilterItem();
	}
	
	public int getKind () {
		return m_nKind;
	}

	public int getScore () {
		return m_nScore;
	}

	public IFilterItem getSource () {
		return m_SrcFI;
	}

	public String getSourceText () {
		return m_SrcFI.getText(FilterItemText.ORIGINAL);
	}

	public IFilterItem getTarget () {
		return m_TrgFI;
	}

	public String getTargetText () {
		return m_TrgFI.getText(FilterItemText.ORIGINAL);
	}

	public void setKind (int p_nKind) {
		m_nKind = p_nKind;
	}

	public void setScore (int p_nScore) {
		m_nScore = p_nScore;
	}

	public void setSource (IFilterItem p_Item) {
		m_SrcFI.copyFrom(p_Item);
	}

	public void setSourceText (String p_sText) {
		m_SrcFI.setText(p_sText);
	}

	public void setTarget (IFilterItem p_Item) {
		m_TrgFI.copyFrom(p_Item);
	}

	public void setTargetText (String p_sText) {
		m_TrgFI.setText(p_sText);
	}

}

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

package net.sf.okapi.Format.TMX;

import net.sf.okapi.Filter.FilterItem;
import net.sf.okapi.Filter.IFilterItem;

/**
 * Represents a target item read by the Reader.
 */
public class Item {

	private IFilterItem      m_FI;
	private String           m_sLang;
	
	public Item (String p_sLang) {
		m_sLang = p_sLang;
		m_FI = new FilterItem();
	}

	/**
	 * Gets the filter item for this item.
	 * @return The filter item.
	 */
	public IFilterItem getFI () {
		return m_FI;
	}
	
	/**
	 * Gets the language for this item.
	 * @return The code of the language.
	 */
	public String getLang () {
		return m_sLang;
	}

	/**
	 * sets the language for this item.
	 * @param p_sLang The code of the language.
	 */
	public void setLang (String p_sLang) {
		m_sLang = p_sLang;
	}
}

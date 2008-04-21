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

import net.sf.okapi.Filter.IFilterItem;

public interface ISegmenter {

	/**
	 * Loads a given segmentation rules file.
	 * @param p_sPath Full path of the rules file to load.
	 */
	public void loadRules (String p_sPath);
	
	/**
	 * Saves the current rules to a given file.
	 * @param p_sPath Full path of the file where to save the rules.
	 * @param p_bSaveAsSRX True if the file should be saved as SRX,
	 * false to save the file in a proprietary format (which can be SRX too).
	 */
	public void saveRules (String p_sPath,
		boolean p_bSaveAsSRX);
	
	/**
	 * Sets the language to use for the rules.
	 * @param p_sCode Code of the language to use.
	 */
	public void setLanguage (String p_sCode);
	
	/**
	 * Gets the language used for the rules.
	 * @return Code of the current language.
	 */
	public String getLanguage ();
	
	/**
	 * Segments a given plain text string.
	 * @param p_sText Plain text string to segment.
	 * @return Number of segments found.
	 */
	public int Segment (String p_sText);
	
	/**
	 * Segments a given filter item.
	 * @param p_FI Filter item to segment.
	 * @return Number of segments found.
	 */
	public int Segment (IFilterItem p_FI);
	
	/**
	 * Gets the number of segments found last time Segment() was called.
	 * @return Number of segments found.
	 */
	public int getCount ();
	
	/**
	 * Gets the next segment in the list of segments found last time Segment()
	 * was called. 
	 * @return A filter item that contains the next segment, or null if there is
	 * no more segment.
	 */
	public IFilterItem getNext ();
}

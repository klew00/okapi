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

import net.sf.okapi.Filter.IFilterItem;

public interface ITMQuery {

	/**
	 * Logs on to the server.
	 * @param p_sConnection The connection data.
	 * @param p_sUsername The user-name.
	 * @param p_sPassword The password.
	 */
	public void login (String p_sConnection,
		String p_sUsername,
		String p_sPassword);
	
	public void logout ();
	
	/**
	 * Opens a give resource.
	 * @param p_sName
	 */
	public void open (String p_sName);
	
	public void close ();
	
	public boolean isTMOpened ();
	
	public int query (String p_sText);
	
	public int query (IFilterItem p_Text);
	
	public int getCount ();
	
	public void resetList ();
	
	public int getMaximum ();
	
	public void setMaximum (int p_nValue);
	
	/**
	 * Gets the next match in the match list.
	 * @return The next match, or null if there is no more matches.
	 */
	public IMatch getNextMatch ();
	
}

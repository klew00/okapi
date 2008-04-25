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
	 * @param connection The connection data. This value is different for
	 * each TM system. See the documentation of the system for details.
	 * @param username The username.
	 * @param password The password.
	 */
	public void login (String connection,
		String username,
		String password);
	
	public void logout ();
	
	/**
	 * Opens a give resource.
	 * @param name Name of the TM to open.
	 */
	public void open (String name);
	
	public void close ();
	
	public boolean isTMOpened ();
	
	public int query (String text);
	
	public int query (IFilterItem filterItem);
	
	public int getCount ();
	
	public void resetList ();
	
	public int getMaximum ();
	
	public void setMaximum (int value);
	
	/**
	 * Gets the next match in the match list.
	 * @return The next match, or null if there is no more matches.
	 */
	public IMatch getNextMatch ();
	
}

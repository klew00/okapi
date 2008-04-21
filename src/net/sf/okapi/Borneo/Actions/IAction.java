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

import net.sf.okapi.Library.Base.IParameters;

public interface IAction
{
	String getID ();

	String getName ();

	boolean isOptional ();

	boolean isManual ();

	boolean needsTarget ();

	boolean isFileSetLevel ();

	boolean hasOptions ();

	boolean hasResultToOpen ();

	IParameters getOptions ();
	
	void setOptions (IParameters p_Value);

	void openResult ();

	boolean start ();

	void stop ();

	boolean execute (int[] p_aDKeys,
		String[] p_aTargets);

}

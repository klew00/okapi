/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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

package net.sf.okapi.applications.rainbow.utilities.bomconversion;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {

	private boolean     removeBOM;
	private boolean     alsoNonUTF8;

	public Parameters () {
		reset();
	}
	
	@Override
	public void fromString (String data) {
		reset();
		super.fromString(data);
		removeBOM = getBoolean("removeBOM", removeBOM);
		alsoNonUTF8 = getBoolean("alsoNonUTF8", alsoNonUTF8);
	}

	@Override
	public void reset() {
		removeBOM = false;
		alsoNonUTF8 = false;
	}

	@Override
	public String toString() {
		setBoolean("removeBOM", removeBOM);
		setBoolean("alsoNonUTF8", alsoNonUTF8);
		return super.toString();
	}

}

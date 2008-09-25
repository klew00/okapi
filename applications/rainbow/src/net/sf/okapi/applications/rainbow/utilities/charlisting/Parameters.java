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

package net.sf.okapi.applications.rainbow.utilities.charlisting;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {

	public String       outputPath;
	public boolean      autoOpen;

	public Parameters () {
		reset();
	}
	
	public void reset() {
		outputPath = "";
		autoOpen = true;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		outputPath = buffer.getString("outputPath", outputPath);
		autoOpen = buffer.getBoolean("autoOpen", autoOpen);
	}

	public String toString() {
		buffer.reset();
		buffer.setParameter("outputPath", outputPath);
		buffer.setParameter("autoOpen", autoOpen);
		return buffer.toString();
	}
	
}

/*===========================================================================*/
/* Copyright (C) 2008 By the Okapi Framework contributors                    */
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

package net.sf.okapi.applications.rainbow.utilities.xsltransform;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {

	/**
	 * Path of the XSLT template to apply.
	 */
	public String xsltPath;
	/**
	 * List of parameters to pass to the template.
	 */
	public String paramList;

	public Parameters () {
		reset();
	}
	
	public void reset () {
		xsltPath = ""; //$NON-NLS-1$
		paramList = ""; //$NON-NLS-1$
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		xsltPath = buffer.getString("xsltPath", xsltPath); //$NON-NLS-1$
		paramList = buffer.getString("paramList", paramList); //$NON-NLS-1$
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString("xsltPath", xsltPath); //$NON-NLS-1$
		buffer.setString("paramList", paramList); //$NON-NLS-1$
		return buffer.toString();
	}

}

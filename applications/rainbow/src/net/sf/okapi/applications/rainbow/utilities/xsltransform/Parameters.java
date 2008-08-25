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

package net.sf.okapi.applications.rainbow.utilities.xsltransform;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {

	/**
	 * Path of the XSLT template to apply.
	 */
	private String      xsltPath;
	/**
	 * List of parameters to pass to the template.
	 */
	private String      paramList;
	

	public Parameters () {
		reset();
	}
	
	public void fromString (String data) {
		// Read the file content as a set of fields
		FieldsString tmp = new FieldsString(data);
		// Parse the fields
		xsltPath = tmp.get("xsltPath", xsltPath); //$NON-NLS-1$
		paramList = tmp.get("paramList", paramList); //$NON-NLS-1$
	}

	@Override
	public void reset () {
		xsltPath = ""; //$NON-NLS-1$
		paramList = ""; //$NON-NLS-1$
	}

	public String toString () {
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("xsltPath", xsltPath); //$NON-NLS-1$
		tmp.add("paramList", paramList); //$NON-NLS-1$
		return tmp.toString();
	}

}

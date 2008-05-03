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

package net.sf.okapi.Package.XLIFF;

import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Package.IReader;

/**
 * Implements IReader for generic XLIFF translation packages.
 */
public class Reader implements IReader {
	
	ILog                               m_Log;
	net.sf.okapi.Format.XLIFF.Reader   m_R;
	
	public Reader (ILog p_Log) {
		m_Log = p_Log;
		m_R = new net.sf.okapi.Format.XLIFF.Reader();
	}

	public void closeDocument () {
		try {
			m_R.close();
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
		}
	}

	public IFilterItem getSourceItem () {
		return m_R.getSourceItem();
	}

	public IFilterItem getTargetItem () {
		return m_R.getTargetItem();
	}

	public void openDocument (String p_sPath) {
		try {
			m_R.open(p_sPath);
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
		}
	}

	public boolean readItem () {
		try {
			return m_R.readItem();
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
			return false;
		}
	}

}

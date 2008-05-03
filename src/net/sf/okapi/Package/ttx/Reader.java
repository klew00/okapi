/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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

package net.sf.okapi.Package.ttx;

import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Package.IReader;

/**
 * Implements IReader for TTX-based translation packages.
 */
public class Reader implements IReader {

	ILog                log;
	TTXReader           reader;
	
	public Reader (ILog p_Log) {
		log = p_Log;
		reader = new TTXReader();
	}
	
	public void closeDocument () {
		try {
			reader.close();
		}
		catch ( Exception E ) {
			log.error(E.getLocalizedMessage());
		}
	}

	public IFilterItem getSourceItem () {
		return reader.getSourceItem();
	}

	public IFilterItem getTargetItem () {
		return reader.getSourceItem();
	}

	public void openDocument (String path) {
		try {
			reader.open(path);
		}
		catch ( Exception E ) {
			log.error(E.getLocalizedMessage());
		}
	}

	public boolean readItem () {
		try {
			return reader.readItem();
		}
		catch ( Exception E ) {
			log.error(E.getLocalizedMessage());
			return false;
		}
	}
	
}

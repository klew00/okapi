/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.lib.tmdb.memory;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.lib.tmdb.IRepository;
import net.sf.okapi.lib.tmdb.ITm;

public class Repository implements IRepository {

	private List<ITm> tms;
	private long lastKey = 0;
	
	public Repository () {
		tms = new ArrayList<ITm>();
	}

	@Override
	public List<ITm> getTmList () {
		return tms;
	}

	@Override
	public ITm addTm (String name,
		String description)
	{
		Tm tm = new Tm(++lastKey, name, description);
		tms.add(tm);
		return tm;
	}

}

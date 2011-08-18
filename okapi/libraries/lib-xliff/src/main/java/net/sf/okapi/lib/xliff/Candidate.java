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

package net.sf.okapi.lib.xliff;

import org.oasisopen.xliff.v2.ICandidate;
import org.oasisopen.xliff.v2.IFragment;

public class Candidate implements ICandidate {

	private static final long serialVersionUID = 0100L;

	private Fragment source;
	private Fragment target;
	private DataStore store;
	
	public Candidate () {
		store = new DataStore();
		source = new Fragment(store, false);
		target = new Fragment(store, true);
	}
	
	public Candidate (String sourceContent,
		String targetContent)
	{
		store = new DataStore();
		source = new Fragment(store, false, sourceContent);
		target = new Fragment(store, true, targetContent);
	}
	
	@Override
	public Fragment createSource () {
		source = new Fragment(store, false);
		return source;
	}
	
	@Override
	public Fragment createTarget () {
		target = new Fragment(store, true);
		return target;
	}

	@Override
	public Fragment getSource () {
		return source;
	}
	
	@Override
	public boolean hasTarget () {
		return (target != null);
	}

	@Override
	public Fragment getTarget () {
		return target;
	}
	
	@Override
	public DataStore getDataStore () {
		return store;
	}
	
	@Override
	public void setSource (IFragment fragment) {
		if ( store != fragment.getDataStore() ) {
			throw new RuntimeException("The fragment passed in setSource must use the same codes store.");
		}
		source = (Fragment)fragment;
	}

	@Override
	public void setTarget (IFragment fragment) {
		if ( store != fragment.getDataStore() ) {
			throw new RuntimeException("The fragment passed in setTarget must use the same codes store.");
		}
		target = (Fragment)fragment;
	}

}

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

import java.io.Serializable;

public class Alternate implements Serializable {

	private static final long serialVersionUID = 0100L;

	private Fragment source;
	private Fragment target;
	private CodesStore store;
	
	
	public Alternate () {
		store = new CodesStore();
		source = new Fragment(store, false);
		target = new Fragment(store, true);
	}
	
	public Alternate (String sourceContent,
		String targetContent)
	{
		store = new CodesStore();
		source = new Fragment(store, false, sourceContent);
		target = new Fragment(store, true, targetContent);
	}
	
	public Fragment createSource () {
		source = new Fragment(store, false);
		return source;
	}
	
	public Fragment createTarget () {
		target = new Fragment(store, true);
		return target;
	}

	public Fragment getSource () {
		return source;
	}
	
	public boolean hasTarget () {
		return (target != null);
	}

	public Fragment getTarget () {
		return target;
	}
	
	public CodesStore getCodesStore () {
		return store;
	}
	
	public void setSource (Fragment fragment) {
		if ( store != fragment.getCodesStore() ) {
			throw new RuntimeException("The fragment passed in setSource must use the same codes store.");
		}
		source = fragment;
	}

	public void setTarget (Fragment fragment) {
		if ( store != fragment.getCodesStore() ) {
			throw new RuntimeException("The fragment passed in setTarget must use the same codes store.");
		}
		target = fragment;
	}

}

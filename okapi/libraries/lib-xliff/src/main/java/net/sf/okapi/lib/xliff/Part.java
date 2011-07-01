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

public class Part implements Serializable {

	private static final long serialVersionUID = 0100L;

	protected String id;
	protected CodesStore store;
	protected Fragment source;
	protected Fragment target;
	protected int targetOrder;
	
	public Part (CodesStore store) {
		this.store = store;
		source = new Fragment(store);
	}
	
	public Part (CodesStore store,
		String sourceContent)
	{
		this.store = store;
		source = new Fragment(store, false, sourceContent);
	}
	
	public String getId () {
		return id;
	}
	
	public void setId (String id) {
		this.id = id;
	}
	
	public Fragment getSource () {
		return source;
	}
	
	public void setSource (Fragment fragment) {
		if ( store != fragment.getCodesStore() ) {
			throw new RuntimeException("The fragment passed in setSource must use the same codes store.");
		}
		source = fragment;
	}
	
	public void setSource (String plainText) {
		source = new Fragment(store, false, plainText);
	}
	
	public boolean hasTarget () {
		return (target != null);
	}

	public Fragment getTarget (boolean createIfNeeded) {
		if (( target == null ) && createIfNeeded ) {
			target = new Fragment(store, true);
		}
		return target;
	}
	
	public void setTarget (Fragment fragment) {
		if ( store != fragment.getCodesStore() ) {
			throw new RuntimeException("The fragment passed in setTarget must use the same codes store.");
		}
		target = fragment;
	}
	
	public void settarget (String plainText) {
		target = new Fragment(store, true, plainText);
	}
	
	public void setTargetOrder (int targetOrder) {
		this.targetOrder = targetOrder;
	}
	
	public CodesStore getCodesStore () {
		return store;
	}

}

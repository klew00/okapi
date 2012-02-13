/*===========================================================================
  Copyright (C) 2011-2012 by the Okapi Framework contributors
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

import org.oasisopen.xliff.v2.IExtendedAttributes;
import org.oasisopen.xliff.v2.IFragment;
import org.oasisopen.xliff.v2.IPart;

public class Part implements IPart {

	private static final long serialVersionUID = 0100L;

	private DataStore store;
	private IFragment source;
	private IFragment target;
	private int targetOrder;
	private IExtendedAttributes xattrs;
	
	public Part (DataStore store) {
		this.store = store;
		source = new Fragment(store);
	}
	
	public Part (DataStore store,
		String sourceContent)
	{
		this.store = store;
		source = new Fragment(store, false, sourceContent);
	}

	@Override
	public IFragment getSource () {
		return source;
	}
	
	@Override
	public void setSource (IFragment fragment) {
		if ( store != fragment.getDataStore() ) {
			throw new RuntimeException("The fragment passed in setSource must use the same codes store.");
		}
		source = fragment;
	}
	
	@Override
	public void setSource (String plainText) {
		source = new Fragment(store, false, plainText);
	}
	
	@Override
	public boolean hasTarget () {
		return (target != null);
	}

	@Override
	public IFragment getTarget (boolean createIfNeeded) {
		if (( target == null ) && createIfNeeded ) {
			target = new Fragment(store, true);
		}
		return target;
	}
	
	@Override
	public void setTarget (IFragment fragment) {
		if ( store != fragment.getDataStore() ) {
			throw new RuntimeException("The fragment passed in setTarget must use the same codes store.");
		}
		target = fragment;
	}
	
	@Override
	public void setTarget (String plainText) {
		target = new Fragment(store, true, plainText);
	}
	
	@Override
	public void setTargetOrder (int targetOrder) {
		this.targetOrder = targetOrder;
	}
	
	@Override
	public int getTargetOrder () {
		return targetOrder;
	}
	
	@Override
	public DataStore getDataStore () {
		return store;
	}

	@Override
	public void setExtendedAttributes (IExtendedAttributes attributes) {
		this.xattrs = attributes;
	}

	@Override
	public IExtendedAttributes getExtendedAttributes () {
		return xattrs;
	}

	@Override
	public boolean hasExtendedAttribute () {
		if ( xattrs == null ) return false;
		return (xattrs.size() > 0);
	}

}

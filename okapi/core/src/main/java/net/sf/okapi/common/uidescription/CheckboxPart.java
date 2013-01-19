/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.uidescription;

import net.sf.okapi.common.ParameterDescriptor;

/**
 * UI part descriptor for a check box. This UI part supports the following types:
 * Boolean, Integer (0: false, non-0: true), and String ("0": false, not-"0": true).  
 */
public class CheckboxPart extends AbstractPart {

	/**
	 * Creates a new CheckboxPart object with a given parameter descriptor.
	 * @param paramDescriptor the parameter descriptor for this UI part.
	 */
	public CheckboxPart (ParameterDescriptor paramDescriptor) {
		super(paramDescriptor);
	}

	@Override
	protected void checkType () {
		// Check type support
		if ( getType().equals(boolean.class) ) return;
		if ( getType().equals(int.class) ) return;
		if ( getType().equals(String.class) ) return;
		// Otherwise: call the base method.
		super.checkType();
	}

}

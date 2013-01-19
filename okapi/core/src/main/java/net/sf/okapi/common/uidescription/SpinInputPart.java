/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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
 * UI part descriptor for a spin-like input field. This UI part supports the following
 * types: Integer.
 */
public class SpinInputPart extends AbstractPart {

	private int minimumValue = Integer.MIN_VALUE;
	private int maximumValue = Integer.MAX_VALUE;
	
	/**
	 * Creates a new TextInputPart object with a given parameter descriptor.
	 * @param paramDescriptor the parameter descriptor for this UI part.
	 */
	public SpinInputPart (ParameterDescriptor paramDescriptor) {
		super(paramDescriptor);
	}
	
	@Override
	protected void checkType () {
		// Check type support
		if ( getType().equals(int.class) ) return;
		// Otherwise: call the base method.
		super.checkType();
	}

	/**
	 * Gets the minimum value allowed (for integer input).
	 * @return the minimum value allowed.
	 */
	public int getMinimumValue () {
		return minimumValue;
	}
	
	/**
	 * Gets the maximum value allowed (for integer input).
	 * @return the maximum value allowed.
	 */
	public int getMaximumValue () {
		return maximumValue;
	}
	
	/**
	 * Sets the minimum and maximum values allowed.
	 * If the values are lesser or greater than the minimum and maximum
	 * values allowed by an Integer, they are reset to those values.
	 * If the maximum is less than the minimum it is reset to the minimum.
	 * @param minimumValue the minimum value allowed.
	 * @param maximumValue the maximum value allowed.
	 */
	public void setRange (int minimumValue,
		int maximumValue)
	{
		if ( minimumValue < Integer.MIN_VALUE ) {
			minimumValue = Integer.MIN_VALUE;
		}
		this.minimumValue = minimumValue;
		
		if ( maximumValue < minimumValue ) {
			maximumValue = minimumValue;
		}
		if ( maximumValue > Integer.MAX_VALUE ) {
			maximumValue = Integer.MAX_VALUE;
		}
		this.maximumValue = maximumValue;
	}

}

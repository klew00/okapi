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
 * UI part descriptor for a text input field. This UI part supports the following
 * types: Integer and String.  
 */
public class TextInputPart extends AbstractPart {

	private boolean allowEmpty;
	private boolean password;
	
	public TextInputPart (ParameterDescriptor paramDescriptor,
		boolean allowEmpty,
		boolean password)
	{
		super(paramDescriptor);
		checkType();
		this.allowEmpty = allowEmpty;
		this.password = password;
	}

	@Override
	protected void checkType () {
		// Check type support
		if ( getType().equals(String.class) ) return;
		if ( getType().equals(Integer.class) ) return;
		// otherwise: call the base method.
		super.checkType();
	}

	/**
	 * Indicates if the input text can be empty.
	 * @return true if the input text can be empty, false otherwise.
	 */
	public boolean isAllowEmpty () {
		return allowEmpty;
	}

	/**
	 * Sets the flag indicating if the input text can be empty. 
	 * @param allowEmpty true if the input text can be empty, false otherwise.
	 */
	public void setAllowEmpty (boolean allowEmpty) {
		this.allowEmpty = allowEmpty;
	}

	/**
	 * Indicates if the input text should be treated like a password.
	 * @return true if the input text can be should be treated like a password.
	 */
	public boolean isPassword () {
		return password;
	}

	/**
	 * Sets the flag indicating if the input text should be treated like a password. 
	 * @param password true if the input text should be treated like a password, false otherwise.
	 */
	public void setPassword (boolean password) {
		this.password = password;
	}

}

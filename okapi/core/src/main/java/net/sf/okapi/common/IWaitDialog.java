/*===========================================================================
  Copyright (C) 2011-2013 by the Okapi Framework contributors
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

package net.sf.okapi.common;

/**
 * Provides a generic way to open a dialog box and wait for a simple
 * input from the user.
 */
public interface IWaitDialog {

	/**
	 * Opens a dialog or prompt and waits for the user input.
	 * @param message the message to display.
	 * @param okLabel the label to display for the OK button.
	 * @return 0 if the user cancels, a positive value otherwise.
	 */
	public int waitForUserInput (String message,
		String okLabel);

}

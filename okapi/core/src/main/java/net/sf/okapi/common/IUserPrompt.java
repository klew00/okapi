/*===========================================================================
  Copyright (C) 2013 by the Okapi Framework contributors
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

import net.sf.okapi.common.exceptions.OkapiUserCanceledException;

/**
 * An interface for prompting the user for confirmation before continuing.
 */
public interface IUserPrompt {

	/**
	 * Initialize the prompt.
	 * @param uiParent The UI parent (used in GUI mode only; can be null otherwise)
	 * @param title The title of the dialog (used in GUI mode only; can be null)
	 */
	public void initialize(Object uiParent, String title);

	/**
	 * Prompt the user to decide between "Yes", "No", and "Cancel".
	 * @param uiParent The GUI parent object. Used in GUI mode only (can be null otherwise).
	 * @param message The text message to display
	 * @return true if yes, false if no
	 * @throws OkapiUserCanceledException If user cancels
	 */
	public boolean promptYesNoCancel (String message)
			throws OkapiUserCanceledException;

	/**
	 * Prompt the user to decide between "OK" and "Cancel".
	 * @param uiParent The GUI parent object. Used in GUI mode only (can be null otherwise).
	 * @param message The text message to display
	 * @return true if OK
	 * @throws OkapiUserCanceledException If user cancels
	 */
	public boolean promptOKCancel (String message)
			throws OkapiUserCanceledException;
}

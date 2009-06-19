/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.ui.filters.plaintext.common;

import net.sf.okapi.common.IParameters;

/**
 * Base interface for a parameters editor page 
 * 
 * @version 0.1, 13.06.2009
 * @author Sergei Vasilyev
 */

public interface IParametersEditorPage {

	/**
	 * Called by the parent editor when the page is attached. Implementation is expected to configure GUI controls after the parameters values.
	 * @param parameters provided by the editor.
	 * @return true if GUI controls were configured successfully 
	 */
	public boolean load(IParameters parameters);
	
	/**
	 * Provides a means for synchronization of the page's GUI controls. All integrity checks are performed in this method.
	 * Event handlers of the controls affecting other controls should call this method. 
	 */
	public void interop();
	
	/**
	 * Called by the parent editor when it was closed with OK. Implementation is expected to set the given parameters according to
	 * the state of corresponding GUI controls.   
	 * @param parameters
	 * @return
	 */
	public boolean save(IParameters parameters);

	/**
	 * Checks if the page can be closed. Called by the editor when OK or Cancel were pressed.
	 * @param isOK the editor is being closed with OK 
	 * @return true if the page can be closed.
	 */
	public boolean canClose(boolean isOK);
	
}

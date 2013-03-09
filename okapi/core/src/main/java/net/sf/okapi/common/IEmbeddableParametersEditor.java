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

package net.sf.okapi.common;

/**
 * Common way to embed in a dialog box an editor to modify the 
 * parameters of a component. The parameters are implemented
 * through the {@link net.sf.okapi.common.IParameters} interface. 
 */
public interface IEmbeddableParametersEditor {

	/**
	 * Validates and save the current parameters for this editor.
	 * @return the string storage of the saved parameter, the same 
	 * string as the one returned by IParameters.toString() 
	 */
	public String validateAndSaveParameters ();

}

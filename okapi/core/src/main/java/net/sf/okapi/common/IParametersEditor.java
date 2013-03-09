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

package net.sf.okapi.common;

/**
 * Common way to call in an editor to modify the parameters of 
 * a component. The parameters are implemented
 * through the {@link net.sf.okapi.common.IParameters} interface. 
 */
public interface IParametersEditor {

	/**
	 * Edits the values for the given parameters. If the edit succeeds
	 * (returns true), the parameters have been updated in p_Parameters to
	 * reflect the changes. 
	 * @param paramsObject the parameters to edit.
	 * @param readOnly indicates if the editor is used just to view the parameters.
	 * If true, the editor must return false.
	 * @param context an implementation of the {@link IContext} interface that
	 * holds caller-specific information.
	 * @return true if the edit was successful, false if the user canceled or if 
	 * an error occurred, or if the read-only mode is set. 
	 */
	public boolean edit (IParameters paramsObject,
		boolean readOnly,
		IContext context);
	
	/**
	 * Creates an instance of the parameters object the editor can edit (with
	 * the default values). This allows the user to create new parameters object 
	 * from the interface, without knowing what exact type of object is created. 
	 * @return an instance of the parameters object for the editor.
	 */
	public IParameters createParameters ();
}

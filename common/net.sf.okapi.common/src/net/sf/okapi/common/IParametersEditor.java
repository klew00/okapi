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
 * This interface provides a way to call in a generic way an editor
 * to modify the parameters of a component. The parameters are implemented
 * through the IParameters interface. 
 */
public interface IParametersEditor {

	/**
	 * Edits the values for the given parameters. If the edit succeeds
	 * (returns true), the parameters have been updated in p_Parameters to
	 * reflect the changes. 
	 * @param paramsObject The parameters to edit.
	 * @param uiContext An implementation-specific object. For example, the
	 * parent window from where the editor is called. The type and value of
	 * uiContext depend on each implementation. See the documentation of the 
	 * implementation for details. Because not all callers may be able to
	 * provide the proper context passing a null value is allowed.
	 * @param helpParam The IHelp object responsible for displaying the help.
	 * Because not all callers may be able to provide the proper object 
	 * passing a null value is allowed.
	 * @param projectDir Project directory (without separator). This is used 
	 * for the ${ProjDir} variable. Because not all callers may be able to
	 * provide the proper project directory passing a null value is allowed.
	 * @return True if the edit was successful, false if the user canceled or if 
	 * an error occurred. 
	 */
	public boolean edit (IParameters paramsObject,
		Object uiContext,
		IHelp helpParam,
		String projectDir);
	
	/**
	 * Creates an instance of the parameters object the editor can edit (with
	 * the default values). This allows the user to create new parameters object 
	 * from the interface, without knowing what exact type of object is created. 
	 * @return An instance of the parameters object for the editor.
	 */
	public IParameters createParameters ();
}

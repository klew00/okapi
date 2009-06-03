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
 * Common set of methods to manage parameters editors.
 */
public interface IParametersEditorMapper {

	/**
	 * Adds a new parameters editor mapping to this mapper.
	 * @param editorClass the class name of the editor to add.
	 * @param parametersClass the class name of the parameters this editor can edit.
	 * If this class name is already listed, the exiting entry will be replaced by
	 * this one.
	 */
	public void addEditor (String editorClass,
		String parametersClass);
	
	/**
	 * Removes a given editor from this mapper.
	 * @param editorClass the class name of the editor to remove.
	 */
	public void removeEditor (String editorClass);
	
	/**
	 * Removes all editor mappings for this mapper.
	 */
	public void clearEditors ();

	/**
	 * Creates an instance of the parameters editor for a given parameters class name. 
	 * @param parametersClass the parameters class name to use for look-up.
	 * @return a new IParametersEditor object for the given
	 * class name, or null if no editor is available or if
	 * the object could not be created.
	 * @throws OkapiEditorCreationException if the editor could not be created.
	 */
	public IParametersEditor createParametersEditor (String parametersClass);
	
}

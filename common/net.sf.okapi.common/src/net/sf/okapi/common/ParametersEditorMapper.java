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

import java.util.LinkedHashMap;

import net.sf.okapi.common.exceptions.OkapiEditorCreationException;

/**
 * Common set of methods to manage parameters editors.
 */
public class ParametersEditorMapper implements IParametersEditorMapper {

	/**
	 * Map of the editors for this mapper.
	 */
	protected LinkedHashMap<String, String> editorMap;
	
	/**
	 * Map of the editor descriptions for this mapper.
	 */
	protected LinkedHashMap<String, String> descMap;

	/**
	 * Creates an empty ParametersEditorMapper object.
	 */
	public ParametersEditorMapper () {
		editorMap = new LinkedHashMap<String, String>();
		descMap = new LinkedHashMap<String, String>();
	}
	
	public void addEditor (String editorClass,
		String parametersClass)
	{
		editorMap.put(parametersClass, editorClass);
	}

	public void addDescriptionProvider (String descriptionProviderClass,
		String parametersClass)
	{
		descMap.put(parametersClass, descriptionProviderClass);
	}

	public void clearEditors () {
		editorMap.clear();
	}

	public void clearDescriptionProviders () {
		descMap.clear();
	}

	public void removeEditor (String className) {
		String found = null;
		for ( String key : editorMap.keySet() ) {
			if ( editorMap.get(key).equals(className) ) {
				found = key;
				break;
			}
		}
		if ( found != null ) {
			editorMap.remove(found);
		}
	}


	public void removeDescriptionProvider (String className) {
		String found = null;
		for ( String key : descMap.keySet() ) {
			if ( descMap.get(key).equals(className) ) {
				found = key;
				break;
			}
		}
		if ( found != null ) {
			descMap.remove(found);
		}
	}

	public IParametersEditor createParametersEditor (String parametersClass) {
		String editorClass = editorMap.get(parametersClass);
		if ( editorClass == null ) return null;
		// Else: instantiate the editor
		IParametersEditor editor = null;
		try {
			editor = (IParametersEditor)Class.forName(editorClass).newInstance();
		}
		catch ( InstantiationException e ) {
			throw new OkapiEditorCreationException(
				String.format("Cannot instantiate the editor '%s'", editorClass), e);
		}
		catch ( IllegalAccessException e ) {
			throw new OkapiEditorCreationException(
				String.format("Cannot instantiate the editor '%s'", editorClass), e);
		}
		catch ( ClassNotFoundException e ) {
			throw new OkapiEditorCreationException(
				String.format("Cannot instantiate the editor '%s'", editorClass), e);
		}
		return editor;
	}

}

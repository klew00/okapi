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
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

/**
 * Common set of methods to manage parameters editors.
 */
public class ParametersEditorMapper implements IParametersEditorMapper {

	/**
	 * Map of the editors for this mapper.
	 */
	protected LinkedHashMap<String, ClassInfo> editorMap;
	
	/**
	 * Map of the editor descriptions for this mapper.
	 */
	protected LinkedHashMap<String, ClassInfo> descMap;

	/**
	 * Creates an empty ParametersEditorMapper object.
	 */
	public ParametersEditorMapper () {
		editorMap = new LinkedHashMap<String, ClassInfo>();
		descMap = new LinkedHashMap<String, ClassInfo>();
	}
	
	@Override
	public void addEditor (ClassInfo editorClass,
		String parametersClassName)
	{
		editorMap.put(parametersClassName, editorClass);
	}

	@Override
	public void addEditor (String editorClassName,
		String parametersClassName)
	{
		editorMap.put(parametersClassName, new ClassInfo(editorClassName));
	}

	@Override
	public void addDescriptionProvider (ClassInfo descriptionProviderClass,
		String parametersClassName)
	{
		descMap.put(parametersClassName, descriptionProviderClass);
	}

	@Override
	public void addDescriptionProvider (String descriptionProviderClassName,
		String parametersClassName)
	{
		descMap.put(parametersClassName, new ClassInfo(descriptionProviderClassName));
	}

	@Override
	public void clearEditors () {
		editorMap.clear();
	}

	@Override
	public void clearDescriptionProviders () {
		descMap.clear();
	}

	@Override
	public void removeEditor (String className) {
		String found = null;
		for ( String key : editorMap.keySet() ) {
			if ( editorMap.get(key).name.equals(className) ) {
				found = key;
				break;
			}
		}
		if ( found != null ) {
			editorMap.remove(found);
		}
	}


	@Override
	public void removeDescriptionProvider (String className) {
		String found = null;
		for ( String key : descMap.keySet() ) {
			if ( descMap.get(key).name.equals(className) ) {
				found = key;
				break;
			}
		}
		if ( found != null ) {
			descMap.remove(found);
		}
	}

	@Override
	public IParametersEditor createParametersEditor (String parametersClassName) {
		ClassInfo ci = editorMap.get(parametersClassName);
		if ( ci == null ) return null;
		// Else: instantiate the editor
		IParametersEditor editor = null;
		try {
			if ( ci.loader == null ) {
				editor = (IParametersEditor)Class.forName(ci.name).newInstance();
			}
			else {
				editor = (IParametersEditor)Class.forName(ci.name,
					true, ci.loader).newInstance();
			}
		}
		catch ( InstantiationException e ) {
			throw new OkapiEditorCreationException(
				String.format("Cannot instantiate the editor '%s'", ci.name), e);
		}
		catch ( IllegalAccessException e ) {
			throw new OkapiEditorCreationException(
				String.format("Cannot instantiate the editor '%s'", ci.name), e);
		}
		catch ( ClassNotFoundException e ) {
			throw new OkapiEditorCreationException(
				String.format("Cannot instantiate the editor '%s'", ci.name), e);
		}
		return editor;
	}

	@Override
	public IEditorDescriptionProvider getDescriptionProvider (String parametersClassName) {
		ClassInfo ci = descMap.get(parametersClassName);
		if ( ci == null ) return null;
		// Else: instantiate the description provider
		IEditorDescriptionProvider descProv = null;
		try {
			if ( ci.loader == null ) {
				descProv = (IEditorDescriptionProvider)Class.forName(ci.name).newInstance();
			}
			else {
				descProv = (IEditorDescriptionProvider)Class.forName(ci.name,
					true, ci.loader).newInstance();
			}
		}
		catch ( InstantiationException e ) {
			throw new OkapiEditorCreationException(
				String.format("Cannot instantiate the description provider '%s'", ci.name), e);
		}
		catch ( IllegalAccessException e ) {
			throw new OkapiEditorCreationException(
				String.format("Cannot instantiate the description provider '%s'", ci.name), e);
		}
		catch ( ClassNotFoundException e ) {
			throw new OkapiEditorCreationException(
				String.format("Cannot instantiate the description provider '%s'", ci.name), e);
		}
		return descProv;
	}

}

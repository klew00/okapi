/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.common.plugins;

import net.sf.okapi.common.ClassInfo;

/**
 * Encapsulates the generic information for a plug-in item.
 * <p>The information is composed of the type of the plug-in, the name of the class
 * that implements it, and optional class information for the optional parameters editor
 * associated with the plug-in.
 */
public class PluginItem {

	/**
	 * Identifies a plug-in implementing {@link net.sf.okapi.common.filters.IFilter}.
	 */
	public static final int TYPE_IFILTER = 0x0001;
	
	/**
	 * Identifies a plug-in implementing {@link net.sf.okapi.common.pipeline.IPipelineStep}.
	 */
	public static final int TYPE_IPIPELINESTEP = 0x0002;
	
	/**
	 * Convenience mask for all main types of plug-ins. 
	 */
	public static final int TYPE_MAIN = 0x0003;
	
	/**
	 * Identifies a plug-in implementing {@link net.sf.okapi.common.IParametersEditor}.
	 */
	public static final int TYPE_IPARAMETERSEDITOR = 0x0004;
	
	/**
	 * Identifies a plug-in implementing {@link net.sf.okapi.common.IEmbeddableParametersEditor}.
	 */
	public static final int TYPE_IEMBEDDABLEPARAMETERSEDITOR = 0x0008;
	
	/**
	 * Identifies a plug-in implementing {@link net.sf.okapi.common.uidescription.IEditorDescriptionProvider}.
	 */
	public static final int TYPE_IEDITORDESCRIPTIONPROVIDER = 0x0010;
	
	/**
	 * Identifies a plug-in implementing {@link net.sf.okapi.common.query.IQuery}.
	 */
	public static final int TYPE_IQUERY = 0x0020;

	int type;
	String className;
	ClassInfo paramsEditor;
	ClassInfo embeddableParamsEditor;
	ClassInfo editorDescriptionProvider;

	/**
	 * Creates a new plug-in item of a given type and class name.
	 * @param type the type of the new plug-in.
	 * @param className the class name of the new plug-in.
	 */
	public PluginItem (int type,
		String className)
	{
		this.type = type;
		this.className = className;
	}
	
	/**
	 * Gets the type of this plug-in.
	 * @return the type of this plug-in.
	 */
	public int getType () {
		return type;
	}
	
	/**
	 * Gets the name of the class implementing this plug-in.
	 * @return the name of the class implementing this plug-in.
	 */
	public String getClassName () {
		return className;
	}
	
	/**
	 * Gets the class information for the IParameterEditor class associated with this plug-in,
	 * or null if there is none.
	 * @return the class information for the IParameterEditor class associated with this plug-in.
	 */
	public ClassInfo getParamsEditor () {
		return paramsEditor;
	}
	
	/**
	 * Gets the class information for the IEmbeddableParamsEditor class associated with this plug-in,
	 * or null if there is none.
	 * @return the class information for the IEmbeddableParamsEditor class associated with this plug-in.
	 */
	public ClassInfo getEmbeddableParamsEditor () {
		return embeddableParamsEditor;
	}
	
	/**
	 * Gets the class information for the IEditorDescriptionProvider class associated with this plug-in,
	 * or null if there is none.
	 * @return the class information for the IEditorDescriptionProvider class associated with this plug-in.
	 */
	public ClassInfo getEditorDescriptionProvider () {
		return editorDescriptionProvider;
	}

}

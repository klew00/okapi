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

public class PluginItem {

	public static final int TYPE_IFILTER = 0x0001;
	public static final int TYPE_IPIPELINESTEP = 0x0002;
	
	public static final int TYPE_MAIN = 0x0003;
	
	public static final int TYPE_IPARAMETERSEDITOR = 0x0004;
	public static final int TYPE_IEMBEDDABLEPARAMETERSEDITOR = 0x0008;
	public static final int TYPE_IEDITORDESCRIPTIONPROVIDER = 0x0010;

	protected int type;
	protected String className;
	protected ClassInfo paramsEditor;
	protected ClassInfo embeddableParamsEditor;
	protected ClassInfo editorDescriptionProvider;

	public PluginItem (int type,
		String className)
	{
		this.type = type;
		this.className = className;
	}
	
	public int getType () {
		return type;
	}
	
	public String getClassName () {
		return className;
	}
	
	public ClassInfo getParamsEditor () {
		return paramsEditor;
	}
	
	public ClassInfo getEmbeddableParamsEditor () {
		return embeddableParamsEditor;
	}
	
	public ClassInfo getEditorDescriptionProvider () {
		return editorDescriptionProvider;
	}

}

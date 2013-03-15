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

package net.sf.okapi.common.ui.genericeditor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.ui.ISWTEmbeddableParametersEditor;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

public class GenericEmbeddableEditor extends GenericEditor implements ISWTEmbeddableParametersEditor {

	private IEditorDescriptionProvider descProvider;
	
	public GenericEmbeddableEditor (IEditorDescriptionProvider descProvider) {
		this.descProvider = descProvider;
	}

	@Override
	public void initializeEmbeddableEditor (Composite parent,
		IParameters paramsObject,
		IContext context)
	{
		params = paramsObject; 
		shell = (Shell)context.getObject("shell");
		createComposite(parent, descProvider);
		setData();
	}

	@Override
	public Composite getComposite () {
		return mainComposite;
	}
	
	@Override
	public String validateAndSaveParameters () {
		if ( !saveData() ) return null;
		return params.toString();
	}

}

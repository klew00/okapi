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

package net.sf.okapi.common.ui;

import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IEmbeddableParametersEditor;
import net.sf.okapi.common.IParameters;

import org.eclipse.swt.widgets.Composite;

/**
 * Common way for SWT-based UI to implement an embedded panel defined 
 * through {@link IEmbeddableParametersEditor} interface. 
 */
public interface ISWTEmbeddableParametersEditor extends IEmbeddableParametersEditor {

	/**
	 * Initializes the object to be used as embedded editor.
	 * @param parent the composite parent where the editor is embedded.
	 * @param paramsObject the parameters to edit.
	 * @param context the context.
	 */
	public void initializeEmbeddableEditor (Composite parent,
		IParameters paramsObject,
		IContext context);
	
	/**
	 * Gets the Composite object (panel) of the editor. You must have called
	 * {@link #initializeEmbeddableEditor(Composite, IParameters, IContext)} before
	 * calling this method.
	 * @return the Composite object of the editor.
	 */
	public Composite getComposite ();
	
}

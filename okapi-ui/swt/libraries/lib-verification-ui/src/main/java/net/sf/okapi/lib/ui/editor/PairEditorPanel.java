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
===========================================================================*/

package net.sf.okapi.lib.ui.editor;

import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Allows to display or work on editing the target text for a bilingual text.
 * This class can edit either a segment or a text container.
 */
public class PairEditorPanel extends SashForm {

	private TextContainerEditorPanel edSource;
	private TextContainerEditorPanel edTarget;

	/**
	 * Creates a new PairEditorPanel object.
	 * @param parent the parent of this panel.
	 * @param flag the style flag: SWT.VERTICAL or SWT.HORIZONTAL 
	 */
	public PairEditorPanel (Composite parent,
		int flag)
	{
		super(parent, flag);

		// Default layout
		setLayout(new GridLayout());
		setLayoutData(new GridData(GridData.FILL_BOTH));
		
		edSource = new TextContainerEditorPanel(this, -1, false);
		edTarget = new TextContainerEditorPanel(this, -1, true);
		
		edSource.setEditable(false);
		edTarget.setTargetRelations(edSource, this);
		
		edTarget.setFocus();
	}

	public void setTextFragments (TextFragment source,
		TextFragment target)
	{
		edSource.setText(source);
		edTarget.setText(target);
	}
		
	public void setTextContainers (TextContainer source,
		TextContainer target)
	{
		edSource.setText(source);
		edTarget.setText(target);
	}
		
	public void clear () {
		edSource.clear();
		edTarget.clear();
	}

	public boolean isModified () {
		return (edTarget.isModified() || edSource.isModified());
	}
	
	public boolean applyChanges () {
		return edTarget.applyChanges();
	}

	@Override
	public void setEnabled (boolean enabled) {
		super.setEnabled(enabled);
		edSource.setEnabled(enabled);
		edTarget.setEnabled(enabled);
	}
	
}

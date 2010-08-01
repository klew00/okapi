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

import net.sf.okapi.common.resource.TextFragment;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class PairEditorPanel extends Composite {

	private FragmentEditorPanel edSource;
	private FragmentEditorPanel edTarget;
	
	public PairEditorPanel (Composite parent,
		int flag)
	{
		super(parent, flag);
		setLayout(new GridLayout());
		
		edSource = new FragmentEditorPanel(parent, -1, false);
		edTarget = new FragmentEditorPanel(parent, -1, true);
		
		edSource.setEditable(false);
		edTarget.setSource(edSource);
	}

	public void setText (TextFragment source,
		TextFragment target)
	{
		edSource.setText(source);
		edTarget.setText(target);
	}

	@Override
	public void setEnabled (boolean enabled) {
		super.setEnabled(enabled);
		edSource.setEnabled(enabled);
		edTarget.setEnabled(enabled);
	}
}

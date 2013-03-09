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

package net.sf.okapi.common.ui.abstracteditor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public abstract class AbstractListAddRemoveTab extends AbstractListTab {

	public AbstractListAddRemoveTab(Composite parent, int style) {
		
		super(parent, style);
		
		SWTUtil.setVisible(modify, false);
		SWTUtil.setVisible(up, false);
		SWTUtil.setVisible(down, false);
	}

	@Override
	protected boolean getDisplayModify() {
		
		return false;
	}

	@Override
	public void interop(Widget speaker) {
		
		super.interop(speaker);
		
		// If the list is empty, display a selection dialog
		if (speaker instanceof Shell && list.getItemCount() == 0) {// speaker is Shell only when called from showDialog()
		
			actionAdd(0);
			selectListItem(list.getItemCount() - 1);
			interop(null);  // To update the Remove button status 
		}			
	}
}

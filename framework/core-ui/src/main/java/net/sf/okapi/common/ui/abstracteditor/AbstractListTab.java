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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

public abstract class AbstractListTab extends ListTabLayout implements IDialogPage {

	protected abstract void actionAdd(int afterIndex);
	protected abstract void actionModify(int itemIndex);	
	protected abstract void actionUp(int itemIndex);
	protected abstract void actionDown(int itemIndex);
	
	protected abstract String getListDescription();
	protected abstract String getItemDescription(int index);
	
	public AbstractListTab(Composite parent, int style) {
		
		super(parent, style);
		
		// Configure interop
		SWTUtil.addSpeaker(this, listDescr);
		SWTUtil.addSpeaker(this, list, SWT.MouseDoubleClick);
		SWTUtil.addSpeaker(this, list, SWT.Selection);
		SWTUtil.addSpeaker(this, itemDescr);
		SWTUtil.addSpeaker(this, add);
		SWTUtil.addSpeaker(this, modify);
		SWTUtil.addSpeaker(this, remove);
		SWTUtil.addSpeaker(this, up);
		SWTUtil.addSpeaker(this, down);
		
		// Configure layout in descendants
		SWTUtil.setText(listDescr, getListDescription());
	}

	protected void selectListItem(int index) {
		
		if (SWTUtil.checkListIndex(list, index)) {
			
			list.setSelection(index);
			SWTUtil.setText(itemDescr, getItemDescription(index));
		}
		else
			SWTUtil.setText(itemDescr, "");
		
		list.setFocus();
	}
	
	public void interop(Widget speaker) {
		
		SWTUtil.setEnabled(add, true);
		
		int index = SWTUtil.getSelection(list);
		
		SWTUtil.setEnabled(modify, index != -1);
		SWTUtil.setEnabled(remove, index != -1);
		SWTUtil.setEnabled(up, index > 0);
		SWTUtil.setEnabled(down, index > -1 && index < SWTUtil.getNumItems(list) - 1);
		
		if (speaker == add) {
			
			actionAdd(index);			
			selectListItem(list.getItemCount() - 1);
			interop(null);
			//selectListItem(0);
		}
					
		else if (speaker == list && SWTUtil.getEventType() == SWT.MouseDoubleClick) {
			
			if (SWTUtil.getVisible(modify))
				actionModify(index);
			else
				actionAdd(index);
		}
		
		else if (speaker == list && SWTUtil.getEventType() == SWT.Selection) {
			
			SWTUtil.setText(itemDescr, getItemDescription(index));
		}
		
		else if (speaker == modify)
			actionModify(index);
		
		else if (speaker == remove) {
			
			list.remove(index);
			
			if (index > list.getItemCount() - 1) index = list.getItemCount() - 1;
			selectListItem(index);	
			interop(null);
		}
		
		else if (speaker == up)
			actionUp(index);
		
		else if (speaker == down)
			actionDown(index);
				
	}

}

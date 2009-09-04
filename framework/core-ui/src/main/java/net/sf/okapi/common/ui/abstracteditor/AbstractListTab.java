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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

public abstract class AbstractListTab extends ListTabLayout implements IDialogPage {

	private Control listDescr;
	private Control list;
	private Control itemDescr;
	private Control add;
	private Control modify;
	private Control remove;
	private Control up;
	private Control down;
	
	protected abstract String getListDescription();
	protected abstract String getAddButtonCaption();
	protected abstract String getModifyButtonCaption();
	protected abstract String getRemoveButtonCaption();
	protected abstract String getMoveUpButtonCaption();
	protected abstract String getMoveDownButtonCaption();
	protected abstract boolean getUpDownVisible();
	
	protected abstract void actionAdd(int afterIndex);
	protected abstract void actionModify(int itemIndex);
	protected abstract boolean actionRemove(int itemIndex);
	protected abstract void actionUp(int itemIndex);
	protected abstract void actionDown(int itemIndex);
	
	public AbstractListTab(Composite parent, int style) {
		
		super(parent, style);
		
		// Init
		listDescr = SWTUtils.findControl(this, "listDescr");
		list = SWTUtils.findControl(this, "list");
		itemDescr = SWTUtils.findControl(this, "itemDescr");
		add = SWTUtils.findControl(this, "add");
		modify = SWTUtils.findControl(this, "modify");
		remove = SWTUtils.findControl(this, "remove");
		up = SWTUtils.findControl(this, "up");
		down = SWTUtils.findControl(this, "down");
		
		// Configure interop
		SWTUtils.addSpeaker(this, listDescr);
		SWTUtils.addSpeaker(this, list, SWT.MouseDoubleClick);
		SWTUtils.addSpeaker(this, list, SWT.Selection);
		SWTUtils.addSpeaker(this, itemDescr);
		SWTUtils.addSpeaker(this, add);
		SWTUtils.addSpeaker(this, modify);
		SWTUtils.addSpeaker(this, remove);
		SWTUtils.addSpeaker(this, up);
		SWTUtils.addSpeaker(this, down);
		
		// Configure layout in descendants
		SWTUtils.setText(listDescr, getListDescription());
		SWTUtils.setText(add, getAddButtonCaption());
		SWTUtils.setText(modify, getModifyButtonCaption());
		SWTUtils.setText(remove, getRemoveButtonCaption());
		SWTUtils.setText(up, getMoveUpButtonCaption());
		SWTUtils.setText(down, getMoveDownButtonCaption());
		SWTUtils.setVisible(up, getUpDownVisible());
		SWTUtils.setVisible(down, getUpDownVisible());
	}

	public void interop(Widget speaker) {
		
		SWTUtils.setEnabled(add, true);
		
		int index = SWTUtils.getSelection(list);
		SWTUtils.setEnabled(modify, index != -1);
		SWTUtils.setEnabled(remove, index != -1);
		SWTUtils.setEnabled(up, index > 0);
		SWTUtils.setEnabled(down, index > -1 && index < SWTUtils.getNumItems(list) - 1);
		
		if (speaker == add)
			actionAdd(SWTUtils.getSelection(list));
		
		else if (speaker == list && SWTUtils.getEventType() == SWT.MouseDoubleClick)
			actionModify(SWTUtils.getSelection(list));
		
		else if (speaker == modify)
			actionModify(SWTUtils.getSelection(list));
		
		else if (speaker == remove) {
			
			if (actionRemove(SWTUtils.getSelection(list))) {
				
				
			}
		}
		
		else if (speaker == up)
			actionUp(SWTUtils.getSelection(list));
		
		else if (speaker == down)
			actionDown(SWTUtils.getSelection(list));
				
	}

}

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
		listDescr = SWTUtil.findControl(this, "listDescr");
		list = SWTUtil.findControl(this, "list");
		itemDescr = SWTUtil.findControl(this, "itemDescr");
		add = SWTUtil.findControl(this, "add");
		modify = SWTUtil.findControl(this, "modify");
		remove = SWTUtil.findControl(this, "remove");
		up = SWTUtil.findControl(this, "up");
		down = SWTUtil.findControl(this, "down");
		
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
		SWTUtil.setText(add, getAddButtonCaption());
		SWTUtil.setText(modify, getModifyButtonCaption());
		SWTUtil.setText(remove, getRemoveButtonCaption());
		SWTUtil.setText(up, getMoveUpButtonCaption());
		SWTUtil.setText(down, getMoveDownButtonCaption());
		SWTUtil.setVisible(up, getUpDownVisible());
		SWTUtil.setVisible(down, getUpDownVisible());
	}

	public void interop(Widget speaker) {
		
		SWTUtil.setEnabled(add, true);
		
		int index = SWTUtil.getSelection(list);
		SWTUtil.setEnabled(modify, index != -1);
		SWTUtil.setEnabled(remove, index != -1);
		SWTUtil.setEnabled(up, index > 0);
		SWTUtil.setEnabled(down, index > -1 && index < SWTUtil.getNumItems(list) - 1);
		
		if (speaker == add)
			actionAdd(SWTUtil.getSelection(list));
		
		else if (speaker == list && SWTUtil.getEventType() == SWT.MouseDoubleClick)
			actionModify(SWTUtil.getSelection(list));
		
		else if (speaker == modify)
			actionModify(SWTUtil.getSelection(list));
		
		else if (speaker == remove) {
			
			if (actionRemove(SWTUtil.getSelection(list))) {
				
				
			}
		}
		
		else if (speaker == up)
			actionUp(SWTUtil.getSelection(list));
		
		else if (speaker == down)
			actionDown(SWTUtil.getSelection(list));
				
	}

}

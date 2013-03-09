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

package net.sf.okapi.lib.ui.verification;

import java.util.Arrays;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.InputDialog;
import net.sf.okapi.common.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

public class StringListPanel extends Composite {

	private Button btAdd;
	private Button btRemove;
	private Button btRemoveAll;
	private List lbList;
	
	public StringListPanel (Composite parent,
		int flags,
		String label)
	{
		super(parent, flags);
		createContent(label);
	}
	
	private void createContent (String label) {
		GridLayout layTmp = new GridLayout(3, false);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);

		GridData gdTmp;
		if ( label != null ) {
			Label stTmp = new Label(this, SWT.NONE);
			stTmp.setText(label);
			gdTmp = new GridData();
			gdTmp.horizontalSpan = 3;
			stTmp.setLayoutData(gdTmp);
		}

		lbList = new List(this, SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 3;
		lbList.setLayoutData(gdTmp);
		
		btAdd = UIUtil.createGridButton(this, SWT.PUSH, "Add...", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addString();
			};
		});
		
		btRemove = UIUtil.createGridButton(this, SWT.PUSH, "Remove", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeString();
			};
		});
		
		btRemoveAll = UIUtil.createGridButton(this, SWT.PUSH, "Remove All", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btRemoveAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				lbList.removeAll();
				updateButtons();
			};
		});
	}
	
	private void addString () {
		try {
			InputDialog dlg = new InputDialog(getShell(), "Add String", "New string to add:", "", null, 0, -1, -1);
			String tmp = dlg.showDialog();
			if ( tmp == null ) return;
			if ( Arrays.asList(lbList.getItems()).contains(tmp) ) {
				Dialogs.showError(getShell(),
					String.format("The string \"%s\" exists already in the list.", tmp), null);
				return;
			}
			lbList.add(tmp);
			lbList.setSelection(lbList.getItemCount()-1);
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), e.getLocalizedMessage(), null);
		}
		finally {
			updateButtons();
		}
	}
	
	private void removeString () {
		try {
			int n = lbList.getSelectionIndex();
			if ( n == -1 ) return;
			lbList.remove(n);
			if ( n >= lbList.getItemCount() ) {
				n = lbList.getItemCount()-1;
			}
			if ( n > -1 ) {
				lbList.setSelection(n);
			}
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), e.getLocalizedMessage(), null);
		}
		finally {
			updateButtons();
		}
	}
	
	public void fillList (java.util.List<String> list) {
		lbList.removeAll();
		for ( String string : list ) {
			lbList.add(string);
		}
		if ( lbList.getItemCount() > 0 ) {
			lbList.select(0);
		}
		updateButtons();
	}
	
	public java.util.List<String> getList () {
		return Arrays.asList(lbList.getItems());
	}

	@Override
	public void setEnabled (boolean enabled) {
		super.setEnabled(enabled);
	}
	
	private void updateButtons () {
		btRemove.setEnabled(lbList.getItemCount()>0);
		btRemoveAll.setEnabled(lbList.getItemCount()>0);
	}

}

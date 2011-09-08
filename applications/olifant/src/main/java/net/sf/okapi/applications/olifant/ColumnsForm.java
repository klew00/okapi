/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.applications.olifant;

import java.util.ArrayList;
import java.util.Arrays;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.lib.tmdb.DbUtil;
import net.sf.okapi.lib.tmdb.ITm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

class ColumnsForm {
	
	private Shell shell;
	private final List lbAvailableFields;
	private final List lbDisplayFields;
	private final Button btShow;
	private final Button btHide;
	private final Button btShowAll;
	private final Button btShowAllTexts;
	private final Button btMoveUp;
	private final Button btMoveDown;
	private ArrayList<String> results = null;

	ColumnsForm (Shell parent,
		ITm tm,
		ArrayList<String> visibleFields)
	{
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("Fields Selection");
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout(3, false));

		Group group = new Group(shell, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setText("Available fields:");
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		lbAvailableFields = new List(group, SWT.BORDER | SWT.V_SCROLL);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		int minListWidth = 150;
		int minListHeight = 250;
		gdTmp.widthHint = minListWidth;
		gdTmp.heightHint = minListHeight;
		lbAvailableFields.setLayoutData(gdTmp);
		
		Composite cmp = new Composite(shell, SWT.NONE);
		cmp.setLayout(new GridLayout());
		
		int minButtonWidth = 100;
		btShow = UIUtil.createGridButton(cmp, SWT.PUSH, "Show >>", minButtonWidth, 1);
		btShow.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		btShow.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showField();
			}
		});
		
		btHide = UIUtil.createGridButton(cmp, SWT.PUSH, "<< Hide", minButtonWidth, 1);
		btHide.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		btHide.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				hideField();
			}
		});
		
		btShowAll = UIUtil.createGridButton(cmp, SWT.PUSH, "Show All", minButtonWidth, 1);
		btShowAll.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		btShowAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showAllFields();
			}
		});
		
		btShowAllTexts = UIUtil.createGridButton(cmp, SWT.PUSH, "Show All Texts", minButtonWidth, 1);
		btShowAllTexts.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		btShowAllTexts.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showAllTextFields();
			}
		});
		
		btMoveUp = UIUtil.createGridButton(cmp, SWT.PUSH, "Move Up", minButtonWidth, 1);
		btMoveUp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		btMoveUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveUp();
			}
		});
		
		btMoveDown = UIUtil.createGridButton(cmp, SWT.PUSH, "Move Down", minButtonWidth, 1);
		btMoveDown.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		btMoveDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveDown();
			}
		});
		
		UIUtil.setSameWidth(minButtonWidth, cmp.getChildren());
		
		group = new Group(shell, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setText("Fields to display:");
		group.setLayoutData(new GridData(GridData.FILL_BOTH));

		lbDisplayFields = new List(group, SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.widthHint = minListWidth;
		gdTmp.heightHint = minListHeight;
		lbDisplayFields.setLayoutData(gdTmp);

		lbDisplayFields.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateMoveCommands();
			};
		});

		ArrayList<String> list = new ArrayList<String>(tm.getAvailableFields());
		list.removeAll(visibleFields);
		for ( String fn :  list ) {
			lbAvailableFields.add(fn);
		}
		if ( lbAvailableFields.getItemCount() > 0 ) {
			lbAvailableFields.setSelection(0);
		}

		lbDisplayFields.add("Flag/SegKey (always)");
		for ( String fn : visibleFields ) {
			lbDisplayFields.add(fn);
		}
		lbDisplayFields.setSelection((lbDisplayFields.getItemCount() > 1 ? 1 : 0));

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( e.widget.getData().equals("o") ) { //$NON-NLS-1$
					results = new ArrayList<String>(Arrays.asList(lbDisplayFields.getItems()));
					results.remove(0);
				}
				shell.close();
			};
		};
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, false);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 3;
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		Rectangle Rect = shell.getBounds();
		shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(shell, parent);
		
		updateCommands();
	}

	private void updateCommands () {
		btShow.setEnabled(lbAvailableFields.getSelectionCount()>0);
		btShowAll.setEnabled(lbAvailableFields.getItemCount()>0);
		btShowAllTexts.setEnabled(lbAvailableFields.getItemCount()>0);
		updateMoveCommands();
	}
	
	private void updateMoveCommands () {
		int n = lbDisplayFields.getSelectionIndex();
		btHide.setEnabled(lbDisplayFields.getSelectionCount()>0 && lbDisplayFields.getItemCount()>1 && n>0);
		btMoveUp.setEnabled(n>1);
		btMoveDown.setEnabled(n<lbDisplayFields.getItemCount()-1 && n>0);
	}
	
	private void ensureSelection (int last,
		List list)
	{
		if ( list.getItemCount() > 0) {
			if ( last > list.getItemCount()-1 ) {
				list.setSelection(list.getItemCount()-1);
			}
			else {
				list.setSelection(last < 0 ? list.getItemCount()-1 : last);
			}
		}
	}
	
	private void showAllTextFields () {
		try {
			int n = lbAvailableFields.getSelectionIndex();
			for ( String fn : lbAvailableFields.getItems() ) {
				if ( fn.startsWith(DbUtil.TEXT_PREFIX) ) {
					lbAvailableFields.remove(fn);
					lbDisplayFields.add(fn);
				}
			}
			ensureSelection(-1, lbDisplayFields);
			ensureSelection(n, lbAvailableFields);
			updateCommands();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void showAllFields () {
		try {
			for ( String fn : lbAvailableFields.getItems() ) {
				lbAvailableFields.remove(fn);
				lbDisplayFields.add(fn);
			}
			ensureSelection(-1, lbDisplayFields);
			updateCommands();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void showField () {
		try {
			int n = lbAvailableFields.getSelectionIndex();
			if ( n < 0 ) return;
			String names[] = lbAvailableFields.getSelection();
			for ( String fn : names ) {
				lbDisplayFields.add(fn);
				lbAvailableFields.remove(fn);
			}
			ensureSelection(-1, lbDisplayFields);
			ensureSelection(n, lbAvailableFields);
			updateCommands();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void hideField () {
		try {
			int n = lbDisplayFields.getSelectionIndex();
			if ( n < 0 ) return;
			String names[] = lbDisplayFields.getSelection();
			for ( String fn : names ) {
				lbAvailableFields.add(fn);
				lbDisplayFields.remove(fn);
			}
			ensureSelection(-1, lbAvailableFields);
			ensureSelection(n, lbDisplayFields);
			updateCommands();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void moveUp () {
		try {
			int n = lbDisplayFields.getSelectionIndex();
			if ( n < 2 ) return;
//todo			
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void moveDown () {
		try {
			int n = lbDisplayFields.getSelectionIndex();
			if ( n < 0 ) return;
//todo			
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	ArrayList<String> showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return results;
	}

}

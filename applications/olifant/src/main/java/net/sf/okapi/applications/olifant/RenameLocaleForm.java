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

import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.lib.tmdb.DbUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class RenameLocaleForm {
	
	private Shell shell;
	private String currentCode;
	private List<String> existingCodes;
	private Text edCode;
	private String result = null;

	RenameLocaleForm (Shell parent,
		String currentCode,
		List<String> existingCodes)
	{
		this.existingCodes = existingCodes;
		this.currentCode = currentCode;
		
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("Rename TM");
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout(1, false));

		Group group = new Group(shell, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label stTmp = new Label(group, SWT.NONE);
		stTmp.setText("&Current locale code:");
		
		Text edCurrent = new Text(group, SWT.BORDER);
		edCurrent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edCurrent.setEditable(false);
		edCurrent.setText(currentCode);

		Label stCode = new Label(group, SWT.NONE);
		stCode.setText("&New code:");
		
		edCode = new Text(group, SWT.BORDER);
		edCode.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edCode.setFocus();
		
		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( e.widget.getData().equals("o") ) { //$NON-NLS-1$
					if ( !saveData() ) return;
				}
				shell.close();
			};
		};
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, false);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 3;
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		Rectangle Rect = shell.getBounds();
		shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(shell, parent);
	}

	String showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

	private boolean saveData () {
		// Check the name
		String code = edCode.getText();
		LocaleId locId;
		try {
			locId = LocaleId.fromString(code);
			code = DbUtil.toOlifantLocaleCode(locId);
			edCode.setText(code);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "You must specify a valid code.", null);
			edCode.setFocus();
			return false;
		}
		if ( code.equals(currentCode) ) {
			result = null; // No changes
			return true;
		}

		if ( existingCodes.contains(code) ) {
			Dialogs.showError(shell, "There is already a Locale with this code.", null);
			edCode.setFocus();
			return false;
		}
		result = code;
		return true;
	}
}

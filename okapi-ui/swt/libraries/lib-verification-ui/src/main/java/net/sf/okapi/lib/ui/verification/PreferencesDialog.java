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

import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.TextOptions;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.lib.ui.editor.TextOptionsPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

class PreferencesDialog {
	
	private Shell dialog;
	private Object[] result = null;
	private OKCancelPanel pnlActions;
	private IHelp help;
	private TextOptionsPanel pnlSourceOptions;
	private TextOptionsPanel pnlTargetOptions;
	
	public PreferencesDialog (Shell parent, IHelp paramHelp) {

		help = paramHelp;
		dialog = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		dialog.setText("User Preferences");
		dialog.setLayout(new GridLayout());
		dialog.setLayoutData(new GridData(GridData.FILL_BOTH));
		UIUtil.inheritIcon(dialog, parent);

		Group grpTmp = new Group(dialog, SWT.NONE);
		grpTmp.setText("Edit Fields");
		grpTmp.setLayout(new GridLayout(1, false));
		grpTmp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		pnlSourceOptions = new TextOptionsPanel(grpTmp, SWT.NONE, "Source edit field:", null);
		pnlSourceOptions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		pnlTargetOptions = new TextOptionsPanel(grpTmp, SWT.NONE, "Target edit field:", null);
		pnlTargetOptions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = null;
				if ( e.widget.getData().equals("h") ) {
					if ( help != null ) help.showWiki("CheckMate - User Preferences");
					return;
				}
				if ( e.widget.getData().equals("o") ) {
					result = new Object[2];
					result[0] = pnlSourceOptions.getOptions();
					result[1] = pnlTargetOptions.getOptions();
				}
				dialog.close();
			};
		};
		pnlActions = new OKCancelPanel(dialog, SWT.NONE, OKCancelActions, true);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		pnlActions.setLayoutData(gdTmp);

		dialog.pack();
		Point size = dialog.getSize();
		dialog.setMinimumSize(size);
		Dialogs.centerWindow(dialog, parent);
	}

	public void setData (TextOptions srcOptions,
		TextOptions trgOptions)
	{
		pnlSourceOptions.setOptions(srcOptions);
		pnlTargetOptions.setOptions(trgOptions);
	}
	
	public Object[] showDialog () {
		dialog.open();
		while ( !dialog.isDisposed() ) {
			if ( !dialog.getDisplay().readAndDispatch() )
				dialog.getDisplay().sleep();
		}
		return result;
	}

	public TextOptions getSourceTextOptions () {
		return pnlSourceOptions.getOptions();
	}

	public TextOptions getTargetTextOptions () {
		return pnlTargetOptions.getOptions();
	}

}

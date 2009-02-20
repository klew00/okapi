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

package net.sf.okapi.applications.rainbow.utilities.merging;

import net.sf.okapi.applications.rainbow.packages.Manifest;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class ManifestDialog {
	
	private Shell shell;
	private IHelp help;
	private ManifestTableModel tableMod;
	private SelectionAdapter CloseActions;
	private boolean result;

	public ManifestDialog (Shell parent,
		IHelp helpParam)
	{
		result = false;
		help = helpParam;
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("Manifest");
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout(1, false);
		cmpTmp.setLayout(layTmp);
		
		Label stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("List of the documents in the manifest:");
		stTmp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Table tableDocs = new Table(cmpTmp, SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.minimumHeight = 300;
		gdTmp.minimumWidth = 550;
		tableDocs.setLayoutData(gdTmp);
		tableDocs.setHeaderVisible(true);
		tableDocs.setLinesVisible(true);
		tableMod = new ManifestTableModel();
		tableMod.linkTable(tableDocs);

		//--- Dialog-level buttons

		CloseActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( e.widget.getData().equals("h") ) { //$NON-NLS-1$
					if ( help != null ) help.showTopic(this, "manifest"); //$NON-NLS-1$
					return;
				}
				if ( e.widget.getData().equals("o") ) {
					tableMod.saveData();
					result = true;
				}
				shell.close();
			};
		};
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, CloseActions, (help!=null));
		pnlActions.btOK.setText("Merge");
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 450 ) startSize.x = 450;
		shell.setSize(startSize);

    	Rectangle rect = tableDocs.getClientArea();
		int nPart = (int)(rect.width / 100);
		tableDocs.getColumn(0).setWidth(45*nPart);
		tableDocs.getColumn(1).setWidth(15*nPart);
		tableDocs.getColumn(2).setWidth(rect.width-(60*nPart));
		
		Dialogs.centerWindow(shell, parent);
	}
	
	public boolean showDialog (Manifest manifest) {
		tableMod.setManifest(manifest);
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

}

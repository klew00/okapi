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

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class InputDocumentDialog {
	
	private Shell shell;
	private Object[] result;
	private String help;
	private InputDocumentPanel pnlMain;
	private OKCancelPanel pnlActions;

	public InputDocumentDialog (Shell parent,
		String captionText,
		IFilterConfigurationMapper fcMapper)
	{
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		if ( captionText != null ) shell.setText(captionText);
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout());
		
		//--- Panel
		
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayout(new GridLayout());
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));

		pnlMain = new InputDocumentPanel(cmpTmp, SWT.NONE, "Input document:", null, fcMapper);
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = null;
				if ( e.widget.getData().equals("h") ) {
					UIUtil.start(help);
					return;
				}
				if ( e.widget.getData().equals("o") ) {
					if ( !saveData() ) return;
				}
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, false);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		Point size = shell.getSize();
		shell.setMinimumSize(size);
		if ( size.x < 600 ) size.x = 600;
		shell.setSize(size);
		Dialogs.centerWindow(shell, parent);
	}

	public Object[] showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

	public void setData (String path,
		String configId,
		String encoding,
		LocaleId srcLoc,
		LocaleId trgLoc)
	{
		pnlMain.setDocumentPath(path);
		pnlMain.setFilterConfigurationId(configId);
		pnlMain.setEncoding(encoding);
		pnlMain.setSourceLocale(srcLoc);
		pnlMain.setTargetLocale(trgLoc);
		// If no configuration is given, but we have a path: try to guess the configuration
		if ( pnlMain.getFilterConfigurationId().isEmpty() || !pnlMain.getDocumentPath().isEmpty() ) {
			pnlMain.guessConfiguration();
		}
	}
	
	public void setLocalesEditable (boolean editable) {
		pnlMain.setLocalesEditable(editable);
	}
	
	private boolean saveData () {
		result = null;
		if ( !pnlMain.validate(true) ) return false;
		result = new Object[5];
		result[0] = pnlMain.getDocumentPath();
		result[1] = pnlMain.getFilterConfigurationId();
		result[2] = pnlMain.getEncoding();
		result[3] = pnlMain.getSourceLocale();
		result[4] = pnlMain.getTargetLocale();
		return true;
	}

}

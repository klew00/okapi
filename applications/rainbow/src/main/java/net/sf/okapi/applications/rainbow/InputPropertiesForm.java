/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.applications.rainbow;

import net.sf.okapi.applications.rainbow.lib.FilterConfigSelectionPanel;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;

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

class InputPropertiesForm {
	
	private Shell shell;
	private Text edSrcEncoding;
	private Text edTrgEncoding;
	private String[] results;
	private OKCancelPanel pnlActions;
	private FilterConfigSelectionPanel pnlFilterConfigSelection;
	private IHelp help;
	private String oldData;

	InputPropertiesForm (Shell parent,
		IHelp helpParam,
		FilterConfigurationMapper fcMapper,
		Project project,
		String projectDir)
	{
		help = helpParam;
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(Res.getString("INPROP_CAPTION")); //$NON-NLS-1$
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout());
		
		Group grpTmp = new Group(shell, SWT.NONE);
		grpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		grpTmp.setLayout(new GridLayout(1, false));
		grpTmp.setText(Res.getString("INPROP_GRPPARAMS")); //$NON-NLS-1$

		pnlFilterConfigSelection = new FilterConfigSelectionPanel(grpTmp, help,
			SWT.NONE, fcMapper, projectDir);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		pnlFilterConfigSelection.setLayoutData(gdTmp);
		
		grpTmp = new Group(shell, SWT.NONE);
		grpTmp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		grpTmp.setLayout(new GridLayout(2, false));
		grpTmp.setText(Res.getString("INPROP_GRPENCODINGS")); //$NON-NLS-1$
		
		new Label(grpTmp, SWT.NONE); // Place-holder
		
		Label label = new Label(grpTmp, SWT.NONE);
		label.setText(Res.getString("INPROP_ENCNOTE")); //$NON-NLS-1$

		label = new Label(grpTmp, SWT.NONE);
		label.setText(Res.getString("INPROP_SRCENCODING")); //$NON-NLS-1$
		
		edSrcEncoding = new Text(grpTmp, SWT.BORDER | SWT.SINGLE);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edSrcEncoding.setLayoutData(gdTmp);
		
		label = new Label(grpTmp, SWT.NONE);
		label.setText(Res.getString("INPROP_TRGENCODING")); //$NON-NLS-1$
		edTrgEncoding = new Text(grpTmp, SWT.BORDER | SWT.SINGLE);
		edTrgEncoding.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Dialog-level buttons
		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				results = null;
				if ( e.widget.getData().equals("h") ) { //$NON-NLS-1$
					if ( help != null ) help.showWiki("Rainbow - Input Document Properties");
					return;
				}
				if ( e.widget.getData().equals("o") ) { //$NON-NLS-1$
					if ( !saveData() ) return;
				}
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		Rectangle Rect = shell.getBounds();
		shell.setMinimumSize(Rect.width, Rect.height);
		if ( Rect.width < 500 ) Rect.width = 500;
		if ( Rect.height < 450 ) Rect.height = 450;
		shell.setSize(Rect.width, Rect.height);
		Dialogs.centerWindow(shell, parent);
	}
	
	String[] showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return results;
	}

	void setData (String filterSettings,
		String sourceEncoding,
		String targetEncoding,
		FilterConfigurationMapper fcMapper)
	{
		oldData = filterSettings + sourceEncoding + targetEncoding;
		pnlFilterConfigSelection.setConfigurationId(filterSettings);
		edSrcEncoding.setText(sourceEncoding);
		edTrgEncoding.setText(targetEncoding);
	}

	private boolean saveData () {
		try {
			results = new String[4];
			results[0] = pnlFilterConfigSelection.getConfigurationId();
			//TODO: Check if the parameters are still OK.
			results[1] = edSrcEncoding.getText();
			results[2] = edTrgEncoding.getText();
			if ( !oldData.equals(results[0]+results[1]+results[2]) ) {
				results[3] = "!"; //$NON-NLS-1$
			} // Else: null means no changes
		}
		catch ( Exception E ) {
			results = null;
			return false;
		}
		return true;
	}
}

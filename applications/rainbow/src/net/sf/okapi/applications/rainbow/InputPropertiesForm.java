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

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.lib.FilterSettingsPanel;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParametersProvider;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class InputPropertiesForm {
	
	private Shell shell;
	private Text edSrcEncoding;
	private Text edTrgEncoding;
	private String[] results;
	private OKCancelPanel pnlActions;
	private FilterSettingsPanel pnlFilterSettings;
	private IHelp help;
	private String oldData;

	InputPropertiesForm (Shell p_Parent,
		IHelp helpParam,
		IParametersProvider paramsProv)
	{
		help = helpParam;
		shell = new Shell(p_Parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(Res.getString("INPROP_CAPTION")); //$NON-NLS-1$
		UIUtil.inheritIcon(shell, p_Parent);
		shell.setLayout(new GridLayout());
		
		Composite comp = new Composite(shell, SWT.BORDER);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp.setLayout(new GridLayout(2, false));

		pnlFilterSettings = new FilterSettingsPanel(comp, help, SWT.NONE, paramsProv);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		pnlFilterSettings.setLayoutData(gdTmp);
		
		int verticalIndent = 8;
		
		Label label = new Label(comp, SWT.NONE); // Place-holder
		gdTmp = new GridData();
		gdTmp.verticalIndent = verticalIndent;
		label.setLayoutData(gdTmp);
		
		label = new Label(comp, SWT.NONE);
		label.setText(Res.getString("INPROP_ENCNOTE")); //$NON-NLS-1$
		gdTmp = new GridData();
		gdTmp.verticalIndent = verticalIndent;
		label.setLayoutData(gdTmp);

		label = new Label(comp, SWT.NONE);
		label.setText(Res.getString("INPROP_SRCENCODING")); //$NON-NLS-1$
		
		edSrcEncoding = new Text(comp, SWT.BORDER | SWT.SINGLE);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edSrcEncoding.setLayoutData(gdTmp);
		
		label = new Label(comp, SWT.NONE);
		label.setText(Res.getString("INPROP_TRGENCODING")); //$NON-NLS-1$
		edTrgEncoding = new Text(comp, SWT.BORDER | SWT.SINGLE);
		edTrgEncoding.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Dialog-level buttons
		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				results = null;
				if ( e.widget.getData().equals("h") ) { //$NON-NLS-1$
					if ( help != null ) help.showTopic(this, "index", "inputDocProp.html"); //$NON-NLS-1$ //$NON-NLS-2$
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
		Dialogs.centerWindow(shell, p_Parent);
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
		FilterAccess p_FA)
	{
		oldData = filterSettings + sourceEncoding + targetEncoding;
		pnlFilterSettings.setData(filterSettings, p_FA);
		edSrcEncoding.setText(sourceEncoding);
		edTrgEncoding.setText(targetEncoding);
	}

	private boolean saveData () {
		try {
			results = new String[4];
			results[0] = pnlFilterSettings.getData();
			//TODO: Check if the parameters are still OK.
			results[1] = edSrcEncoding.getText();
			results[2] = edTrgEncoding.getText();
			if ( !oldData.equals(results[0]+results[1]+results[2]) ) {
				results[3] = "!";
			} // Else: null means no changes
		}
		catch ( Exception E ) {
			return false;
		}
		return true;
	}
}

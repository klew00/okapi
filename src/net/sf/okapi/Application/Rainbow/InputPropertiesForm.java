/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.Application.Rainbow;

import net.sf.okapi.Filter.FilterAccess;
import net.sf.okapi.Library.Base.IParametersProvider;
import net.sf.okapi.Library.UI.Dialogs;
import net.sf.okapi.Library.UI.FilterSettingsPanel;
import net.sf.okapi.Library.UI.OKCancelPanel;

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
	
	private Shell                 shell;
	private Text                  edSrcEncoding;
	private Text                  edTrgEncoding;
	private String[]              m_aResults = null;
	private OKCancelPanel         pnlActions;
	private FilterSettingsPanel   pnlFilterSettings;

	InputPropertiesForm (Shell p_Parent,
		IParametersProvider paramsProv)
	{
		shell = new Shell(p_Parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("Properties");
		shell.setImage(p_Parent.getImage());
		shell.setLayout(new GridLayout());
		
		Composite comp = new Composite(shell, SWT.BORDER);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp.setLayout(new GridLayout(2, false));

		Label label = new Label(comp, SWT.NONE);
		label.setText("Filter settings:");
		label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		pnlFilterSettings = new FilterSettingsPanel(comp, SWT.NONE, paramsProv);
		pnlFilterSettings.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		label = new Label(comp, SWT.NONE);
		label.setText("Source encoding:");
		edSrcEncoding = new Text(comp, SWT.BORDER | SWT.SINGLE);
		edSrcEncoding.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		label = new Label(comp, SWT.NONE);
		label.setText("Target encoding:");
		edTrgEncoding = new Text(comp, SWT.BORDER | SWT.SINGLE);
		edTrgEncoding.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Dialog-level buttons
		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_aResults = null;
				if ( e.widget.getData().equals("h") ) {
					//TODO: Call help
					return;
				}
				if ( e.widget.getData().equals("o") ) {
					if ( !saveData() ) return;
				}
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.m_btOK);

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
		return m_aResults;
	}

	void setData (String filterSettings,
		String sourceEncoding,
		String targetEncoding,
		FilterAccess p_FA)
	{
		pnlFilterSettings.setData(filterSettings, p_FA);
		edSrcEncoding.setText(sourceEncoding);
		edTrgEncoding.setText(targetEncoding);
	}

	private boolean saveData () {
		try {
			m_aResults = new String[3];
			m_aResults[0] = pnlFilterSettings.getData();
			m_aResults[1] = edSrcEncoding.getText();
			m_aResults[2] = edTrgEncoding.getText();
		}
		catch ( Exception E ) {
			return false;
		}
		return true;
	}
}

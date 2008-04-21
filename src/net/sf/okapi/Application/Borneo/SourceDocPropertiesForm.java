/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
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

package net.sf.okapi.Application.Borneo;

import net.sf.okapi.Filter.FilterAccess;
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

class SourceDocPropertiesForm {
	
	private Shell                 m_Shell;
	private Text                  m_edEncoding;
	private Text                  m_edFileSet;
	private String[]              m_aResults = null;
	private OKCancelPanel         m_pnlActions;
	private FilterSettingsPanel   m_pnlFSettings;

	SourceDocPropertiesForm (Shell p_Parent)
	{
		m_Shell = new Shell(p_Parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		m_Shell.setText(Res.getString("SRCDOCPROP_TITLE"));
		m_Shell.setImage(p_Parent.getImage());
		m_Shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(m_Shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		cmpTmp.setLayout(new GridLayout(2, false));

		Label stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Filter settings:");
		stTmp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		m_pnlFSettings = new FilterSettingsPanel(cmpTmp, SWT.NONE);
		m_pnlFSettings.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Encoding:");
		m_edEncoding = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE);
		m_edEncoding.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("File set:");
		m_edFileSet = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE);
		m_edFileSet.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		//--- Dialog-level buttons

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
				m_Shell.close();
			};
		};
		m_pnlActions = new OKCancelPanel(m_Shell, SWT.NONE, OKCancelActions, true);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		m_pnlActions.setLayoutData(gdTmp);
		m_Shell.setDefaultButton(m_pnlActions.m_btOK);

		m_Shell.pack();
		Rectangle Rect = m_Shell.getBounds();
		m_Shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(m_Shell, p_Parent);
	}
	
	String[] showDialog () {
		m_Shell.open();
		while ( !m_Shell.isDisposed() ) {
			if ( !m_Shell.getDisplay().readAndDispatch() )
				m_Shell.getDisplay().sleep();
		}
		return m_aResults;
	}

	void setData (String p_sFSettings,
		String p_sEncoding,
		String p_sFileSet,
		FilterAccess p_FA) {
		m_pnlFSettings.setData(p_sFSettings, p_FA);
		m_edEncoding.setText(p_sEncoding);
		m_edFileSet.setText(p_sFileSet);
	}

	private boolean saveData () {
		try {
			m_aResults = new String[3];
			m_aResults[0] = m_pnlFSettings.getData();
			m_aResults[1] = m_edEncoding.getText();
			m_aResults[2] = m_edFileSet.getText();
		}
		catch ( Exception E ) {
			return false;
		}
		return true;
	}
}

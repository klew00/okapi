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

import net.sf.okapi.Borneo.Core.DBOptions;
import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Library.UI.Dialogs;
import net.sf.okapi.Library.UI.OKCancelPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class SelectServerForm {
	private Shell            m_Shell;
	private List             m_lbDBType;
	private Text             m_edServer;
	private Text             m_edPort;
	private Text             m_edDatabase;
	private Text             m_edUsername;
	private Text             m_edPassword;
	private OKCancelPanel    m_pnlActions;
	private DBOptions        m_Data;
	private boolean          m_bResult = false;

	SelectServerForm (Shell p_Parent)
	{
		m_Shell = new Shell(p_Parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		m_Shell.setText(Res.getString("DBSERVSEL_TITLE"));
		m_Shell.setImage(p_Parent.getImage());
		m_Shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(m_Shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout(2, false);
		cmpTmp.setLayout(layTmp);
		
		
		Label stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Database type:");
		stTmp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		m_lbDBType = new List(cmpTmp, SWT.SINGLE | SWT.BORDER);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.heightHint = 50;
		m_lbDBType.setLayoutData(gdTmp);
		m_lbDBType.add("Internal Server - H2");
		m_lbDBType.add("Hosted Server - MySQL v5.x");
		m_lbDBType.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e1) {
				updateDisplay();
			}
			public void widgetDefaultSelected(SelectionEvent e2) {}
		});
		
		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Server:");
		m_edServer = new Text(cmpTmp, SWT.BORDER);
		m_edServer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Port:");
		m_edPort = new Text(cmpTmp, SWT.BORDER);
		m_edPort.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Database:");
		m_edDatabase = new Text(cmpTmp, SWT.BORDER);
		m_edDatabase.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Username:");
		m_edUsername = new Text(cmpTmp, SWT.BORDER);
		m_edUsername.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Password:");
		m_edPassword = new Text(cmpTmp, SWT.BORDER | SWT.PASSWORD);
		m_edPassword.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_bResult = false;
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
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 3;
		m_pnlActions.setLayoutData(gdTmp);
		m_Shell.setDefaultButton(m_pnlActions.m_btOK);

		m_Shell.pack();
		Rectangle Rect = m_Shell.getBounds();
		m_Shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(m_Shell, p_Parent);
	}
	
	boolean showDialog () {
		m_Shell.open();
		while ( !m_Shell.isDisposed() ) {
			if ( !m_Shell.getDisplay().readAndDispatch() )
				m_Shell.getDisplay().sleep();
		}
		return m_bResult;
	}

	public void setData (DBOptions p_Data) {
		m_Data = p_Data;
		m_lbDBType.select(p_Data.getDBType());
		m_edServer.setText(p_Data.getServer());
		m_edPort.setText(String.format("%d",p_Data.getPort()));
		m_edDatabase.setText(p_Data.getDatabase());
		m_edUsername.setText(p_Data.getUsername());
		m_edPassword.setText(p_Data.getPassword());
		updateDisplay();
	}
	
	private boolean saveData () {
		int nDBType = m_lbDBType.getSelectionIndex();
		int nPort;
		try {
			nPort = Integer.parseInt(m_edPort.getText());	
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
			m_edPort.setFocus(); m_edPort.selectAll();
			return false;
		}
		String sPassword = m_edPassword.getText();
		if ( nDBType == 1 ) {
			if ( sPassword.length() == 0 ) {
				Utils.showError("Password is missing", null);
				m_edPassword.setFocus(); m_edPassword.selectAll();
				return false;
			}
		}
		m_Data.setPort(nPort);
		m_Data.setPassword(sPassword);
		m_Data.setDBType(nDBType);
		m_Data.setServer(m_edServer.getText());
		m_Data.setDatabase(m_edDatabase.getText());
		m_Data.setUsername(m_edUsername.getText());
		m_bResult = true;
		return true;
	}
	
	private void updateDisplay () {
		boolean bOn = (m_lbDBType.getSelectionIndex()==1);
		m_edServer.setEnabled(bOn);
		m_edDatabase.setEnabled(bOn);
		m_edPort.setEnabled(bOn);
		m_edUsername.setEnabled(bOn);
		m_edPassword.setEnabled(bOn);
	}
}

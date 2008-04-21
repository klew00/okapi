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

import java.io.File;

import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Library.UI.Dialogs;
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

class NewProjectForm {
	
	private Shell            m_Shell;
	private Text             m_edName;
	private Text             m_edPath;
	private String[]         m_aResults = null;
	private OKCancelPanel    m_pnlActions;

	NewProjectForm (Shell p_Parent,
		int p_nDBType)
	{
		m_Shell = new Shell(p_Parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		m_Shell.setText(Res.getString("NEWPRJ_TITLE"));
		m_Shell.setImage(p_Parent.getImage());
		m_Shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(m_Shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);

		final Label stName = new Label(cmpTmp, SWT.NONE);
		stName.setText("Enter the name of the new project:");
		m_edName = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE);
		m_edName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Label stPath = new Label(cmpTmp, SWT.NONE);
		if ( p_nDBType == 0 ) stPath.setText(Res.getString("NEWPRJ_DBPATH"));
		else stPath.setText(Res.getString("NEWPRJ_NETSTORAGE"));
		m_edPath = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE);
		m_edPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
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
		m_pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_Shell.setDefaultButton(m_pnlActions.m_btOK);

		setData();
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

	private void setData () {
		m_edName.setText(Res.getString("NEWPRJ_DEFAULTNAME"));
		m_edPath.setText(System.getProperty("user.home") + File.separator + Res.getString("BNOSUBDIR_NAME"));
	}

	private boolean saveData () {
		m_aResults = new String[2];
		m_aResults[0] = m_edName.getText();
		char chBad;
		if (( chBad = Utils.checksCharList(m_aResults[0], " \t[]'\"")) != (char)0 ) {
			Utils.showError(String.format("The character '%c' is not allowed.", chBad), null);
			return false;
		}
		m_aResults[1] = m_edPath.getText();
		return true;
	}
}

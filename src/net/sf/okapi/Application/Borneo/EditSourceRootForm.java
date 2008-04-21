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

class EditSourceRootForm {
	
	private Shell            m_Shell;
	private Text             m_edDir;
	private String           m_sResult = null;
	private OKCancelPanel    m_pnlActions;

	EditSourceRootForm (Shell p_Parent,
		String p_sRoot)
	{
		m_Shell = new Shell(p_Parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		m_Shell.setText(Res.getString("SRCROOT_TITLE"));
		m_Shell.setImage(p_Parent.getImage());
		m_Shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(m_Shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);

		final Label stDir = new Label(cmpTmp, SWT.NONE);
		stDir.setText("Enter the root directory of the source documents:");
		
		m_edDir = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE);
		m_edDir.setText(p_sRoot);
		m_edDir.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_sResult = null;
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

		m_Shell.pack();
		Rectangle Rect = m_Shell.getBounds();
		m_Shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(m_Shell, p_Parent);
	}
	
	String showDialog () {
		m_Shell.open();
		while ( !m_Shell.isDisposed() ) {
			if ( !m_Shell.getDisplay().readAndDispatch() )
				m_Shell.getDisplay().sleep();
		}
		return m_sResult;
	}

	private boolean saveData () {
		try {
			m_sResult = m_edDir.getText();
			if ( m_sResult.length() == 0 ) {
				m_edDir.selectAll();
				m_edDir.setFocus();
				return false;
			}
		}
		catch ( Exception E ) {
			return false;
		}
		return true;
	}
}

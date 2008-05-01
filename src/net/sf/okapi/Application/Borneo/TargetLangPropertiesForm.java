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

import net.sf.okapi.Borneo.Core.DBTarget;
import net.sf.okapi.Library.UI.Dialogs;
import net.sf.okapi.Library.UI.OKCancelPanel;
import net.sf.okapi.Library.UI.PathBuilderPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class TargetLangPropertiesForm {
	
	private Shell            m_Shell;
	private boolean          m_bResult = false;
	private DBTarget         m_Data;
	private OKCancelPanel    m_pnlActions;
	private PathBuilderPanel m_pnlPB;
	private Text             m_edEncoding;
	private Text             m_edRoot;

	TargetLangPropertiesForm (Shell p_Parent)
	{
		m_Shell = new Shell(p_Parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		m_Shell.setImage(p_Parent.getImage());
		m_Shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(m_Shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout(2, false);
		cmpTmp.setLayout(layTmp);
	
		Label stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Default encoding:");
		m_edEncoding = new Text(cmpTmp, SWT.BORDER);
		m_edEncoding.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Output root:");
		m_edRoot = new Text(cmpTmp, SWT.BORDER);
		m_edRoot.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group grpTmp = new Group(cmpTmp, SWT.NONE);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		grpTmp.setText("Default path mapping");
		gdTmp.horizontalSpan = 2;
		grpTmp.setLayoutData(gdTmp);
		grpTmp.setLayout(new GridLayout());
		
		m_pnlPB = new PathBuilderPanel(grpTmp, SWT.NONE);
		m_pnlPB.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Add the listener for the target root 
		m_edRoot.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				m_pnlPB.setTargetRoot(m_edRoot.getText());
				m_pnlPB.updateSample();
			}
		});

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
		gdTmp.horizontalSpan = 2;
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

	void setData (String p_sSourceRoot,
		String p_sSampleSourcePath,
		DBTarget p_Data,
		String p_sLang)
	{
		m_Shell.setText(Res.getString("TRGLANGPROP_TITLE")+p_sLang);
		m_Data = p_Data;
		
		m_edEncoding.setText(m_Data.getEncoding());
		m_edRoot.setText((m_Data.getRoot()==null) ? "" : m_Data.getRoot());
		
		if ( p_sSampleSourcePath == null ) {
			p_sSampleSourcePath = p_sSourceRoot + File.separator + "myProject"
				+ File.separator + "myFile.ext";
		}
		m_pnlPB.setData(p_Data.getDefaultPB(), p_sSourceRoot, p_sSampleSourcePath,
			p_Data.getRoot(), p_sLang);
	}
	
	private boolean saveData () {
		if ( m_edEncoding.getText().length() == 0 ) {
			m_edEncoding.setFocus();
			return false;
		}
		m_Data.setEncoding(m_edEncoding.getText());
		m_Data.setRoot(m_edRoot.getText());
		m_pnlPB.saveData();
		m_bResult = true;
		return true;
	}
	
}

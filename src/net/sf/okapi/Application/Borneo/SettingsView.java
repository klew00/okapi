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

import net.sf.okapi.Borneo.Core.Controller;
import net.sf.okapi.Borneo.Core.DBBase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

class SettingsView extends Composite {

	private MainForm    m_MF;
	private Controller  m_C;
	private Text        m_edStorage; 
	private Text        m_edSrcLang; 
	private Text        m_edSrcEnc;
	private Button      m_btModify;
	private Button      m_btDiscard;
	private Group       m_grpTargets;
	private List        m_lbTargets;
	private Button      m_btAddTargets;
	private Button      m_btRemoveTarget;
	private Button      m_btEditTarget;
	private Text        m_edDBType;
	private Text        m_edServer;
	private Text        m_edDatabase;
	
	SettingsView (Composite p_Parent,
		int p_nFlags,
		MainForm p_MF,
		Controller p_C)
	{
		super(p_Parent, p_nFlags);
		m_MF = p_MF;
		m_C = p_C;
		createContent();
	}
	
	private void createContent () {
		GridLayout layTmp = new GridLayout();
		layTmp.numColumns = 4;
		setLayout(layTmp);
		
		Label stStorage = new Label(this, SWT.NONE);
		stStorage.setText("Storage location:");
		
		m_edStorage = new Text(this, SWT.SINGLE | SWT.BORDER);
		m_edStorage.setEditable(false);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gdTmp.horizontalSpan = 3;
		m_edStorage.setLayoutData(gdTmp);

		int nVIndent = 40;
		Label stSrcLang = new Label(this, SWT.NONE);
		stSrcLang.setText("Source language:");
		gdTmp = new GridData();
		gdTmp.verticalIndent = nVIndent;
		stSrcLang.setLayoutData(gdTmp);
		
		m_edSrcLang = new Text(this, SWT.SINGLE | SWT.BORDER);
		m_edSrcLang.setEditable(false);
		gdTmp = new GridData();
		gdTmp.widthHint = 200;
		gdTmp.verticalIndent = nVIndent;
		m_edSrcLang.setLayoutData(gdTmp);
		m_edSrcLang.setTextLimit(DBBase.LANGCODE_MAX);
		
		m_grpTargets = new Group(this, SWT.NONE);
		m_grpTargets.setText("Target languages");
		gdTmp = new GridData(GridData.FILL_VERTICAL);
		gdTmp.verticalSpan = 7;
		gdTmp.horizontalSpan = 2;
		m_grpTargets.setLayoutData(gdTmp);
		GridLayout layTmp2 = new GridLayout();
		layTmp2.numColumns = 2;
		m_grpTargets.setLayout(layTmp2);

		m_lbTargets = new List(m_grpTargets, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.verticalSpan = 3;
		gdTmp.widthHint = 230;
		gdTmp.minimumWidth = 230;
		m_lbTargets.setLayoutData(gdTmp);
		m_lbTargets.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateTargetButtons();
			}
		});
		
		m_btAddTargets = new Button(m_grpTargets, SWT.PUSH);
		m_btAddTargets.setText(Res.getString("PVIEW_btAddTargets"));
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		gdTmp.widthHint = Res.getInteger("PVIEW_btAddTargets_w");
		m_btAddTargets.setLayoutData(gdTmp);
		m_btAddTargets.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_C.addTargetLanguages();
			}
		});

		m_btRemoveTarget = new Button(m_grpTargets, SWT.PUSH);
		m_btRemoveTarget.setText(Res.getString("PVIEW_btRemoveTargets"));
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		m_btRemoveTarget.setLayoutData(gdTmp);
		m_btRemoveTarget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_MF.removeTargetLanguages();
			}
		});
		
		m_btEditTarget = new Button(m_grpTargets, SWT.PUSH);
		m_btEditTarget.setText(Res.getString("PVIEW_btEditTargets"));
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		m_btEditTarget.setLayoutData(gdTmp);
		m_btEditTarget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_C.editTargetProperties(getTargetListSelection());
			}
		});
		
		Label stSrcEnc = new Label(this, SWT.NONE);
		stSrcEnc.setText("Default sourceEncoding:");

		m_edSrcEnc = new Text(this, SWT.SINGLE | SWT.BORDER);
		m_edSrcEnc.setEditable(false);
		gdTmp = new GridData();
		gdTmp.widthHint = 200;
		m_edSrcEnc.setLayoutData(gdTmp);
		
		int nWidth = Res.getInteger("PVIEW_btModify_w");
		int nHIndent = 20;
		m_btModify = new Button(this, SWT.PUSH);
		m_btModify.setText(Res.getString("PVIEW_btModify"));
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);	
		gdTmp.verticalIndent = 10;
		gdTmp.horizontalIndent = nHIndent;
		gdTmp.horizontalSpan = 2;
		gdTmp.widthHint = nWidth;
		m_btModify.setLayoutData(gdTmp);
		m_btModify.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( inEditMode() ) applyChanges();
				else startEditMode();
			}
		});

		m_btDiscard = new Button(this, SWT.PUSH);
		m_btDiscard.setText(Res.getString("PVIEW_btDiscard"));
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);	
		gdTmp.horizontalIndent = nHIndent;
		gdTmp.horizontalSpan = 2;
		gdTmp.widthHint = nWidth;
		m_btDiscard.setLayoutData(gdTmp);
		m_btDiscard.setVisible(false);
		m_btDiscard.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				discardChanges();
			}
		});

		nVIndent = 30;
		nWidth = 200;
		Label stDBType = new Label(this, SWT.NONE);
		stDBType.setText("Database type:");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.verticalIndent = nVIndent;
		stDBType.setLayoutData(gdTmp);
		
		m_edDBType = new Text(this, SWT.SINGLE | SWT.BORDER);
		m_edDBType.setEditable(false);
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.verticalIndent = nVIndent;
		gdTmp.widthHint = nWidth;
		m_edDBType.setLayoutData(gdTmp);

		Label stServer = new Label(this, SWT.NONE);
		stServer.setText("Server:");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		stServer.setLayoutData(gdTmp);
		
		m_edServer = new Text(this, SWT.SINGLE | SWT.BORDER);
		m_edServer.setEditable(false);
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = nWidth;
		m_edServer.setLayoutData(gdTmp);

		Label stDatabase = new Label(this, SWT.NONE);
		stDatabase.setText("Database instance:");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		stDatabase.setLayoutData(gdTmp);
		
		m_edDatabase = new Text(this, SWT.SINGLE | SWT.BORDER);
		m_edDatabase.setEditable(false);
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = nWidth;
		m_edDatabase.setLayoutData(gdTmp);
	}

	List getTargetList () {
		return m_lbTargets;
	}
	
	void updateView () {
		boolean bOn = m_C.isProjectOpened();
		m_btModify.setEnabled(bOn);
		m_btAddTargets.setEnabled(bOn);
		m_MF.updateTargetLists();

		if ( bOn ) {
			m_edStorage.setText(m_C.getDB().getStorage());
			m_edSrcLang.setText(m_C.getDB().getSourceLanguage());
			m_edSrcEnc.setText(m_C.getDB().getSourceEncoding());
		}
		else {
			m_edStorage.setText("");
			m_edSrcLang.setText("");
			m_edSrcEnc.setText("");
		}
		
		if ( m_C.getDB() == null ) {
			m_edDBType.setText(Res.getString("PVIEW_NOSERVERSELECTED"));
			m_edServer.setText("");
			m_edDatabase.setText("");
		}
		else {
			String[] aInfo = m_C.getDBInfo();
			m_edDBType.setText(aInfo[0]);
			m_edServer.setText(aInfo[1]);
			m_edDatabase.setText(aInfo[2]);
		}
	}
	
	void updateTargetButtons () {
		boolean bOn = (m_lbTargets.getSelectionIndex()>-1);
		m_btRemoveTarget.setEnabled(bOn);
		m_btEditTarget.setEnabled(bOn);
	}

	boolean inEditMode () {
		return m_btDiscard.isVisible();
	}

	private void enableSettingsControls (boolean p_bEnabled,
		boolean p_bMoveFocus)
	{
		if ( p_bEnabled ) {
			m_btModify.setText(Res.getString("PVIEW_ACCEPTCHANGES"));
			if ( p_bMoveFocus ) {
				m_edSrcLang.selectAll();
				m_edSrcLang.setFocus();
			}
			getShell().setDefaultButton(m_btModify);
		}
		else {
			getShell().setDefaultButton(null);
			m_btModify.setText(Res.getString("PVIEW_btModify"));
			if ( p_bMoveFocus ) {
				m_btModify.setFocus();
			}
		}
		m_btDiscard.setVisible(p_bEnabled);
		m_grpTargets.setEnabled(!p_bEnabled);
		m_edSrcLang.setEditable(p_bEnabled);
		m_edSrcEnc.setEditable(p_bEnabled);
	}

	void startEditMode () {
		enableSettingsControls(true, true);
	}
	
	private void applyChanges () {
		enableSettingsControls(false, true);
		saveData();
	}

	private void discardChanges () {
		enableSettingsControls(false, true);
		fillSettingsControls();
	}

	private void saveData () {
		m_C.getDB().setSourceEncoding(m_edSrcEnc.getText());
		m_C.getDB().setSourceLanguage(m_edSrcLang.getText());
		m_MF.saveSettings();
	}

	private void fillSettingsControls () {
		m_edSrcLang.setText(m_C.getDB().getSourceLanguage());
		m_edSrcEnc.setText(m_C.getDB().getSourceEncoding());
	}

	String getTargetListSelection () {
		String[] aLangs = m_lbTargets.getSelection();
		if (( aLangs == null ) || ( aLangs.length == 0 )) return null;
		// Keep only the code
		int n = aLangs[0].indexOf(' ');
		if ( n > -1 ) aLangs[0] = aLangs[0].substring(0, n);
		return aLangs[0];
	}
	
	String[] getTargetListItems () {
		String[] aLangs = m_lbTargets.getItems();
		if (( aLangs == null ) || ( aLangs.length == 0 )) return null;
		// Keep only the code
		int n;
		for ( int i=0; i<aLangs.length; i++ ) {
			n = aLangs[i].indexOf(' ');
			if ( n > -1 ) aLangs[i] = aLangs[i].substring(0, n);
		}
		return aLangs;
	}
	
	int getTargetListSelectionIndex () {
		return m_lbTargets.getSelectionIndex();
	}
	
	void setTargetListSelection (int p_nIndex) {
		if ( p_nIndex > m_lbTargets.getItemCount()-1 )
			p_nIndex = m_lbTargets.getItemCount()-1;
		if ( p_nIndex > -1 ) {
			m_lbTargets.select(p_nIndex);
		}
	}
}


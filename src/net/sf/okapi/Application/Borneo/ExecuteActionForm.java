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

import net.sf.okapi.Borneo.Actions.BaseAction;
import net.sf.okapi.Borneo.Actions.ExportPackageForm;
import net.sf.okapi.Borneo.Actions.IAction;
import net.sf.okapi.Borneo.Actions.ImportTranslationForm;
import net.sf.okapi.Borneo.Core.DBDoc;
import net.sf.okapi.Library.Base.IParametersEditor;
import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Library.UI.Dialogs;
import net.sf.okapi.Library.UI.LanguageManager;
import net.sf.okapi.Library.UI.OKCancelPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class ExecuteActionForm {
	
	private Shell            m_Shell;
	private IAction          m_Action;
	private DBDoc            m_Doc;
	private DocumentsView    m_DView;
	private SettingsView     m_PView;
	private String           m_sCurrentTarget;
	private OKCancelPanel    m_pnlActions;
	private Object[]         m_aResults;
	private Text             m_edAction;
	private Button           m_btOptions;
	private Button           m_chkOpenOutput;
	private Text             m_edDocs;
	private Button           m_rdDocAll;
	private Button           m_rdDocSelected;
	private Button           m_rdDocCurrent;
	private Label            m_stLangs;
	private Text             m_edLangs;
	private Button           m_rdLangAll;
	private Button           m_rdLangCurrent;
	private Group            m_grpLanguages;
	private LanguageManager  m_LM;

	ExecuteActionForm (Shell p_Parent,
		LanguageManager p_LM,
		DocumentsView p_DView,
		SettingsView p_PView)
	{
		m_Shell = new Shell(p_Parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		m_Shell.setText(Res.getString("EXEC_TITLE"));
		m_Shell.setImage(p_Parent.getImage());
		m_Shell.setLayout(new GridLayout());
		m_LM = p_LM;
		m_DView = p_DView;
		m_PView = p_PView;
		
		Composite cmpTmp = new Composite(m_Shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout();
		layTmp.numColumns = 4;
		cmpTmp.setLayout(layTmp);

		Label stAction = new Label(cmpTmp, SWT.NONE);
		stAction.setText("Action to execute:");
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		stAction.setLayoutData(gdTmp);

		m_edAction = new Text(cmpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		m_edAction.setLayoutData(gdTmp);
		m_edAction.setEditable(false);
		
		m_btOptions = new Button(cmpTmp, SWT.PUSH);
		m_btOptions.setText("Action &Options...");
		gdTmp = new GridData(GridData.CENTER);
		gdTmp.horizontalSpan = 2;
		gdTmp.widthHint = 130;
		m_btOptions.setLayoutData(gdTmp);
		m_btOptions.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editOptions();
			}
		});

		m_chkOpenOutput = new Button(cmpTmp, SWT.CHECK);
		m_chkOpenOutput.setText("Open results");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		m_chkOpenOutput.setLayoutData(gdTmp);

		Group grpTmp = new Group(cmpTmp, SWT.NONE);
		grpTmp.setText("Documents");
		gdTmp = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.horizontalSpan = 2;
		grpTmp.setLayoutData(gdTmp);
		grpTmp.setLayout(new GridLayout());
		
	    SelectionAdapter DocAdapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateDocuments();;
			}
		};

	    m_rdDocAll = new Button(grpTmp, SWT.RADIO);
		m_rdDocAll.setText("All the documents");
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		m_rdDocAll.setLayoutData(gdTmp);
		m_rdDocAll.addSelectionListener(DocAdapter);
		
		m_rdDocSelected = new Button(grpTmp, SWT.RADIO);
		m_rdDocSelected.setText("Only selected document(s)");
		m_rdDocSelected.setLayoutData(gdTmp);
		m_rdDocSelected.addSelectionListener(DocAdapter);

		m_rdDocCurrent = new Button(grpTmp, SWT.RADIO);
		m_rdDocCurrent.setText("Only the current document");
		m_rdDocCurrent.setLayoutData(gdTmp);
		m_rdDocCurrent.addSelectionListener(DocAdapter);
		
		m_grpLanguages = new Group(cmpTmp, SWT.NONE);
		m_grpLanguages.setText("Languages");
		gdTmp = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.horizontalSpan = 2;
		m_grpLanguages.setLayoutData(gdTmp);
		m_grpLanguages.setLayout(new GridLayout());
		
	    SelectionAdapter LangAdapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateLanguages();;
			}
		};

		m_rdLangAll = new Button(m_grpLanguages, SWT.RADIO);
		m_rdLangAll.setText("All the languages");
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		m_rdDocAll.setLayoutData(gdTmp);
		m_rdDocAll.addSelectionListener(LangAdapter);

		m_rdLangCurrent = new Button(m_grpLanguages, SWT.RADIO);
		m_rdLangCurrent.setText("Only the current language");
		m_rdLangCurrent.setLayoutData(gdTmp);
		m_rdLangCurrent.addSelectionListener(LangAdapter);

		m_stLangs = new Label(cmpTmp, SWT.NONE);
		m_stLangs.setText("Language(s):");
		
		m_edLangs = new Text(cmpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 3;
		m_edLangs.setLayoutData(gdTmp);
		m_edLangs.setEditable(false);
		
		Label stDocs = new Label(cmpTmp, SWT.NONE);
		stDocs.setText("Document(s):");

		m_edDocs = new Text(cmpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 3;
		m_edDocs.setLayoutData(gdTmp);
		m_edDocs.setEditable(false);

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
		m_pnlActions.setOKText("Execute");
		m_Shell.setDefaultButton(m_pnlActions.m_btOK);

		m_Shell.pack();
		Rectangle Rect = m_Shell.getBounds();
		m_Shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(m_Shell, p_Parent);
	}
	
	Object[] showDialog () {
		m_Shell.open();
		while ( !m_Shell.isDisposed() ) {
			if ( !m_Shell.getDisplay().readAndDispatch() )
				m_Shell.getDisplay().sleep();
		}
		return m_aResults;
	}

	void setData (IAction p_Action,
		int p_nDocScope,
		DBDoc p_Doc,
		String p_sTargetScope,
		String p_sCurrentTarget)
	{
		m_Action = p_Action;
		m_Doc = p_Doc;
		m_sCurrentTarget = p_sCurrentTarget;
		m_edAction.setText(m_Action.getName());
		m_btOptions.setEnabled(m_Action.hasOptions());
		m_chkOpenOutput.setVisible(m_Action.hasResultToOpen());
		
		switch ( p_nDocScope ) {
			case -1: // All
				m_rdDocAll.setSelection(true);
				break;
			case -3: // Selected documents
				m_rdDocSelected.setSelection(true);
				break;
			default:
				m_rdDocCurrent.setSelection(true);
				break;
		}

		if ( m_Action.needsTarget() ) {
			if ( p_sTargetScope == null ) m_rdLangAll.setSelection(true);
			else m_rdLangCurrent.setSelection(true);
		}
		else {
			m_grpLanguages.setEnabled(false);
			m_rdLangAll.setEnabled(false);
			m_rdLangCurrent.setEnabled(false);
			m_edLangs.setEnabled(false);
			m_edLangs.setText("<N/A>");
			m_stLangs.setEnabled(false);
		}

		updateLanguages();
		updateDocuments();
	}
	
	private boolean saveData () {
		try {
			m_aResults = new Object[3];

			// Result 0 = keys
			int[] aDKeys;
			if ( m_rdDocAll.getSelection() ) aDKeys = m_DView.getAllDKeys();
			else if ( m_rdDocSelected.getSelection() ) aDKeys = m_DView.getSelectedDKeys();
			else {
				aDKeys = new int[1];
				aDKeys[0] = m_Doc.getKey();
			}
			m_aResults[0] = aDKeys;

			// Result 1 = targets
			String[] aTargets = null;
			if ( m_Action.needsTarget() ) {
				if ( m_rdLangAll.getSelection() ) {
					aTargets = m_PView.getTargetListItems();
				}
				else {
					aTargets = new String[1];
					aTargets[0] = m_sCurrentTarget;
					m_aResults[1] = aTargets;
				}
			}
			m_aResults[1] = aTargets;

			// Result 2 = (!= null) is true
			if ( m_Action.hasResultToOpen() )
				m_aResults[2] = m_chkOpenOutput.getSelection();
			else
				m_aResults[2] = false;
		}
		catch ( Exception E ) {
			return false;
		}
		return true;
	}
	
	private void updateLanguages () {
		if ( m_Action.needsTarget() ) {
			if ( m_rdLangAll.getSelection() )
				m_edLangs.setText(String.format("<%s>", m_rdLangAll.getText()));
			else {
				m_edLangs.setText(String.format("%s - %s", m_sCurrentTarget,
					m_LM.GetNameFromCode(m_sCurrentTarget)));
			}
		}
	}
	
	private void updateDocuments () {
		if ( m_rdDocAll.getSelection() )
			m_edDocs.setText(String.format("<%s>", m_rdDocAll.getText()));
		else if ( m_rdDocSelected.getSelection() )
			m_edDocs.setText(String.format("<%s>", m_rdDocSelected.getText()));
		else
			m_edDocs.setText(m_Doc.getRelativePath());
	}
	
	private void editOptions () {
		try {
			IParametersEditor OptEd = null;
			if ( m_Action.getID().equals(BaseAction.ID_EXPORTPACKAGE) ) {
				OptEd = new ExportPackageForm();
			}
			else if ( m_Action.getID().equals(BaseAction.ID_IMPORTTRANSLATION) ) {
				OptEd = new ImportTranslationForm();
			}
			// Edit the options
			if ( OptEd != null ) {
				OptEd.edit(m_Action.getOptions(), m_Shell);
			}
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}
}

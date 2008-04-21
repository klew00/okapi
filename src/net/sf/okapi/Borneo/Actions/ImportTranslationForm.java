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

package net.sf.okapi.Borneo.Actions;

import net.sf.okapi.Library.Base.IParameters;
import net.sf.okapi.Library.Base.IParametersEditor;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class ImportTranslationForm implements IParametersEditor {
	
	private Shell                      m_Shell;
	private boolean                    m_bResult = false;
	private Button                     m_rdFromSource;
	private Button                     m_rdFromTarget;
	private Button                     m_rdFromOther;
	private Text                       m_edPath;
	private Text                       m_edFSettings;
	private Text                       m_edEncoding;
	private Button                     m_chkIncludeWithout;
	private Label                      m_stLabelWithout;
	private Table                      m_tblWithout;
	private Button                     m_chkIncludeWith;
	private Label                      m_stLabelWith;
	private Table                      m_tblWith;
	private OKCancelPanel              m_pnlActions;
	private boolean                    m_bInInit = true;
	private ImportTranslationOptions   m_Opt;
	
	/**
	 * Invokes the editor for the options of the ExportPackage action.
	 * @param p_Options The option object of the action.
	 * @param p_Object The SWT Shell object of the parent shell in the UI.
	 */
	public boolean edit (IParameters p_Options,
		Object p_Object)
	{
		boolean bRes = false;
		m_Shell = null;
		m_Opt = (ImportTranslationOptions)p_Options;
		try {
			m_Shell = new Shell((Shell)p_Object, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
			create((Shell)p_Object);
			return showDialog();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
			bRes = false;
		}
		finally {
			// Dispose of the shell, but not of the display
			if ( m_Shell != null ) m_Shell.dispose();
		}
		return bRes;
	}
	
	public IParameters createParameters () {
		return new ImportTranslationOptions();
	}
	
	private void create (Shell p_Parent)
	{
		m_Shell.setText("Import Translation");
		m_Shell.setImage(p_Parent.getImage());
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		m_Shell.setLayout(layTmp);

		//--- Options tab

		TabFolder tfTmp = new TabFolder(m_Shell, SWT.NONE);
		tfTmp.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout(2, false));

		Label stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Which type of import to perform:");
		GridData gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		stTmp.setLayoutData(gdTmp);
		
		Composite cmpTmp2 = new Composite(cmpTmp, SWT.NONE);
		cmpTmp2.setLayout(new GridLayout());
		gdTmp = new GridData();
		gdTmp.horizontalIndent = 8;
		gdTmp.horizontalSpan = 2;
		cmpTmp2.setLayoutData(gdTmp);
		
		SelectionListener SLUpdateType = new SelectionListener() {
			public void widgetSelected(SelectionEvent e1) {
				updateType();
			}
			public void widgetDefaultSelected(SelectionEvent e2) {}
		};

		m_rdFromSource = new Button(cmpTmp2, SWT.RADIO);
		m_rdFromSource.setText("From the target text in the bilingual source document");
		m_rdFromSource.addSelectionListener(SLUpdateType);
		
		m_rdFromTarget = new Button(cmpTmp2, SWT.RADIO);
		m_rdFromTarget.setText("From the existing target document");
		m_rdFromTarget.addSelectionListener(SLUpdateType);

		m_rdFromOther = new Button(cmpTmp2, SWT.RADIO);
		m_rdFromOther.setText("From the source text of another specific document");
		m_rdFromOther.addSelectionListener(SLUpdateType);
		
		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Full path of the document to import:");
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		stTmp.setLayoutData(gdTmp);

		m_edPath = new Text(cmpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		m_edPath.setLayoutData(gdTmp);
		
		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Filter settings:");
		m_edFSettings = new Text(cmpTmp, SWT.BORDER);
		m_edFSettings.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Encoding:");
		m_edEncoding = new Text(cmpTmp, SWT.BORDER);
		m_edEncoding.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Input");
		tiTmp.setControl(cmpTmp);

		//--- Scope tab

		cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout(2, true));
		
		m_chkIncludeWithout = new Button(cmpTmp, SWT.CHECK);
		m_chkIncludeWithout.setText("Apply to items with&out translation");
		m_chkIncludeWithout.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e1) {
				updateScopeWithout();
			}
			public void widgetDefaultSelected(SelectionEvent e2) {}
		});

		m_chkIncludeWith = new Button(cmpTmp, SWT.CHECK);
		m_chkIncludeWith.setText("Apply to items &with translation");
		m_chkIncludeWith.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e1) {
				updateScopeWith();
			}
			public void widgetDefaultSelected(SelectionEvent e2) {}
		});
		
		m_stLabelWithout = new Label(cmpTmp, SWT.NONE);
		m_stLabelWithout.setText("And with the following status:");

		m_stLabelWith = new Label(cmpTmp, SWT.NONE);
		m_stLabelWith.setText("And with the following status:");
		
		m_tblWithout = new Table(cmpTmp, SWT.BORDER | SWT.CHECK);
		m_tblWithout.setLayoutData(new GridData(GridData.FILL_BOTH));
		TableItem tbliTmp = new TableItem(m_tblWithout, SWT.NONE);
		tbliTmp.setText("To Translate");
		tbliTmp = new TableItem(m_tblWithout, SWT.NONE);
		tbliTmp.setText("To Edit");
		tbliTmp = new TableItem(m_tblWithout, SWT.NONE);
		tbliTmp.setText("To Review");
		tbliTmp = new TableItem(m_tblWithout, SWT.NONE);
		tbliTmp.setText("Ready");

		m_tblWith = new Table(cmpTmp, SWT.BORDER | SWT.CHECK);
		m_tblWith.setLayoutData(new GridData(GridData.FILL_BOTH));
		tbliTmp = new TableItem(m_tblWith, SWT.NONE);
		tbliTmp.setText("To Translate");
		tbliTmp = new TableItem(m_tblWith, SWT.NONE);
		tbliTmp.setText("To Edit");
		tbliTmp = new TableItem(m_tblWith, SWT.NONE);
		tbliTmp.setText("To Review");
		tbliTmp = new TableItem(m_tblWith, SWT.NONE);
		tbliTmp.setText("Ready");

		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Scope");
		tiTmp.setControl(cmpTmp);
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_bResult = false;
				if ( e.widget.getData().equals("h") ) {
					//TODO: Call help
					return;
				}
				if ( e.widget.getData().equals("o") )
					if ( !saveData() ) return;
				m_Shell.close();
			};
		};
		m_pnlActions = new OKCancelPanel(m_Shell, SWT.NONE, OKCancelActions, true);
		m_pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_Shell.setDefaultButton(m_pnlActions.m_btOK);

		setData();
		m_bInInit = false;
		updateType();
		updateScopeWithout();
		updateScopeWith();
		m_Shell.pack();
		Rectangle Rect = m_Shell.getBounds();
		m_Shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(m_Shell, p_Parent);
	}
	
	private boolean showDialog () {
		m_Shell.open();
		while ( !m_Shell.isDisposed() ) {
			if ( !m_Shell.getDisplay().readAndDispatch() )
				m_Shell.getDisplay().sleep();
		}
		return m_bResult;
	}

	private void setData () {
		m_rdFromSource.setSelection(m_Opt.getImportType()==0);
		m_rdFromTarget.setSelection(m_Opt.getImportType()==1);
		m_rdFromOther.setSelection(m_Opt.getImportType()==2);
		m_edPath.setText(m_Opt.getPath());
		m_edFSettings.setText(m_Opt.getFSettings());
		m_edEncoding.setText(m_Opt.getEncoding());
		
		m_chkIncludeWithout.setSelection(m_Opt.includeItemsWithoutTranslation());
		for ( int i=0; i<ImportTranslationOptions.NBCASE; i++ ) {
			m_tblWithout.getItem(i).setChecked(m_Opt.getScopeWithoutTranslation()[i]);
		}
		m_tblWithout.setSelection(0);
		
		m_chkIncludeWith.setSelection(m_Opt.includeItemsWithTranslation());
		for ( int i=0; i<ImportTranslationOptions.NBCASE; i++ ) {
			m_tblWith.getItem(i).setChecked(m_Opt.getScopeWithTranslation()[i]);
		}
		m_tblWith.setSelection(0);
	}

	private boolean saveData () {
		if ( m_bInInit ) return true;
		if ( m_rdFromSource.getSelection() )
			m_Opt.setImportType(0);
		else if ( m_rdFromTarget.getSelection() )
			m_Opt.setImportType(1);
		else m_Opt.setImportType(2);
		m_Opt.setPath(m_edPath.getText());
		m_Opt.setFSettings(m_edFSettings.getText());
		m_Opt.setEncoding(m_edEncoding.getText());
		
		m_Opt.setIncludeItemsWithoutTranslation(m_chkIncludeWithout.getSelection());
		for ( int i=0; i<ImportTranslationOptions.NBCASE; i++ ) {
			m_Opt.getScopeWithoutTranslation()[i] = m_tblWithout.getItem(i).getChecked();
		}
		
		m_Opt.setIncludeItemsWithTranslation(m_chkIncludeWith.getSelection());
		for ( int i=0; i<ImportTranslationOptions.NBCASE; i++ ) {
			m_Opt.getScopeWithTranslation()[i] = m_tblWith.getItem(i).getChecked();
		}
		
		return true;
	}
	
	private void updateType () {
		if ( m_bInInit ) return;
		boolean bOn = m_rdFromOther.getSelection();
		m_edPath.setEnabled(bOn);
		m_edFSettings.setEnabled(bOn);
		m_edEncoding.setEnabled(bOn);
	}
	
	private void updateScopeWithout () {
		if ( m_bInInit ) return;
		boolean bOn = m_chkIncludeWithout.getSelection();
		m_stLabelWithout.setEnabled(bOn);
		m_tblWithout.setEnabled(bOn);
	}

	private void updateScopeWith () {
		if ( m_bInInit ) return;
		boolean bOn = m_chkIncludeWith.getSelection();
		m_stLabelWith.setEnabled(bOn);
		m_tblWith.setEnabled(bOn);
	}
}

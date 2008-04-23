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

package net.sf.okapi.Format.Properties;

import net.sf.okapi.Library.Base.IParameters;
import net.sf.okapi.Library.Base.IParametersEditor;
import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Library.UI.CodeFinderPanel;
import net.sf.okapi.Library.UI.Dialogs;
import net.sf.okapi.Library.UI.LDPanel;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class ParametersForm implements IParametersEditor {
	
	private Shell            m_Shell;
	private boolean          m_bResult = false;
	private Button           m_chkUseKeyFilter;
	private Button           m_rdExtractOnlyMatchingKey;
	private Button           m_rdExcludeMatchingKey;
	private Text             m_edKeyCondition;
	private Button           m_chkExtraComments;
	private LDPanel          m_pnlLD;
	private OKCancelPanel    m_pnlActions;
	private Parameters       m_Data;
	private Button           m_chkEscapeExtendedChars;
	private CodeFinderPanel  m_CFPanel;

	/**
	 * Invokes the editor for the Properties filter parameters.
	 * @param p_Options The option object of the action.
	 * @param p_Object The SWT Shell object of the parent shell in the UI.
	 */
	public boolean edit (IParameters p_Options,
		Object p_Object)
	{
		boolean bRes = false;
		m_Shell = null;
		m_Data = (Parameters)p_Options;
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
		return new Parameters();
	}
	
	private void create (Shell p_Parent)
	{
		m_Shell.setText("Properties Filter Parameters");
		m_Shell.setImage(p_Parent.getImage());
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		m_Shell.setLayout(layTmp);

		TabFolder tfTmp = new TabFolder(m_Shell, SWT.NONE);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		tfTmp.setLayoutData(gdTmp);

		//--- Options tab
		
		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);
		
		Group grpTmp = new Group(cmpTmp, SWT.NONE);
		layTmp = new GridLayout();
		grpTmp.setLayout(layTmp);
		grpTmp.setText("Localization directives");
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		grpTmp.setLayoutData(gdTmp);
		m_pnlLD = new LDPanel(grpTmp, SWT.NONE);
		
		grpTmp = new Group(cmpTmp, SWT.NONE);
		layTmp = new GridLayout();
		grpTmp.setLayout(layTmp);
		grpTmp.setText("Key filtering");
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		grpTmp.setLayoutData(gdTmp);
		
		m_chkUseKeyFilter = new Button(grpTmp, SWT.CHECK);
		m_chkUseKeyFilter.setText("Use the following key condition:");
		m_chkUseKeyFilter.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateKeyFilter();
			};
		});

		m_rdExtractOnlyMatchingKey = new Button(grpTmp, SWT.RADIO);
		m_rdExtractOnlyMatchingKey.setText("Extract only the items with a key matching the given expression");
		gdTmp = new GridData();
		gdTmp.horizontalIndent = 16;
		m_rdExtractOnlyMatchingKey.setLayoutData(gdTmp);

		m_rdExcludeMatchingKey = new Button(grpTmp, SWT.RADIO);
		m_rdExcludeMatchingKey.setText("Do not extract the items with a key matching the given expression");
		m_rdExcludeMatchingKey.setLayoutData(gdTmp);
		
		m_edKeyCondition = new Text(grpTmp, SWT.NONE);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalIndent = 16;
		m_edKeyCondition.setLayoutData(gdTmp);
		
		m_chkExtraComments = new Button(cmpTmp, SWT.CHECK);
		m_chkExtraComments.setText("Recognize additional comment markers");

		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Options");
		tiTmp.setControl(cmpTmp);
		
		//--- Inline tab
		
		//cmpTmp = new Composite(tfTmp, SWT.NONE);
		//layTmp = new GridLayout();
		//cmpTmp.setLayout(layTmp);
		
		m_CFPanel = new CodeFinderPanel(tfTmp, SWT.NONE);
		
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Inline Codes");
		tiTmp.setControl(m_CFPanel);

		//--- Output tab
		
		cmpTmp = new Composite(tfTmp, SWT.NONE);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);
		
		grpTmp = new Group(cmpTmp, SWT.NONE);
		layTmp = new GridLayout();
		grpTmp.setLayout(layTmp);
		grpTmp.setText("Extended characters");
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		grpTmp.setLayoutData(gdTmp);

		m_chkEscapeExtendedChars = new Button(grpTmp, SWT.CHECK);
		m_chkEscapeExtendedChars.setText("Always escape all extended characters");
		
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Output");
		tiTmp.setControl(cmpTmp);
		
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( e.widget.getData().equals("h") ) {
					//TODO: Call help
					return;
				}
				if ( e.widget.getData().equals("o") ) saveData();
				m_Shell.close();
			};
		};
		m_pnlActions = new OKCancelPanel(m_Shell, SWT.NONE, OKCancelActions, true);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		m_pnlActions.setLayoutData(gdTmp);
		m_Shell.setDefaultButton(m_pnlActions.m_btOK);

		setData();
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
		m_pnlLD.m_chkUseLD.setSelection(m_Data.m_LD.useDirectives());
		m_pnlLD.m_chkLocalizeOutside.setSelection(m_Data.m_LD.localizeOutside());
		m_edKeyCondition.setText(m_Data.m_sKeyCondition);
		m_rdExtractOnlyMatchingKey.setSelection(m_Data.m_bExtractOnlyMatchingKey);
		m_rdExcludeMatchingKey.setSelection(!m_Data.m_bExtractOnlyMatchingKey);
		m_chkUseKeyFilter.setSelection(m_Data.m_bUseKeyCondition);
		m_chkExtraComments.setSelection(m_Data.m_bExtraComments);
		m_chkEscapeExtendedChars.setSelection(m_Data.m_bEscapeExtendedChars);
		m_CFPanel.setData(m_Data.m_bUseCodeFinder, m_Data.m_CodeFinder);
		m_pnlLD.updateDisplay();
		updateKeyFilter();
	}
	
	private void saveData () {
		m_Data.m_LD.setOptions(m_pnlLD.m_chkUseLD.getSelection(),
			m_pnlLD.m_chkLocalizeOutside.getSelection(), false);
		m_Data.m_bUseKeyCondition = m_chkUseKeyFilter.getSelection();
		m_Data.m_sKeyCondition = m_edKeyCondition.getText();
		m_Data.m_bExtractOnlyMatchingKey = m_rdExtractOnlyMatchingKey.getSelection();
		m_Data.m_bExtraComments = m_chkExtraComments.getSelection();
		m_Data.m_bEscapeExtendedChars = m_chkEscapeExtendedChars.getSelection();
		
		m_bResult = true;
	}
	
	private void updateKeyFilter () {
		m_edKeyCondition.setEnabled(m_chkUseKeyFilter.getSelection());
		m_rdExtractOnlyMatchingKey.setEnabled(m_chkUseKeyFilter.getSelection());
		m_rdExcludeMatchingKey.setEnabled(m_chkUseKeyFilter.getSelection());
	}
}

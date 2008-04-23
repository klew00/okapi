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

package net.sf.okapi.Format.JSON;

import net.sf.okapi.Library.Base.IParameters;
import net.sf.okapi.Library.Base.IParametersEditor;
import net.sf.okapi.Library.Base.Utils;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class ParametersForm  implements IParametersEditor {
	
	private Shell            m_Shell;
	private boolean          m_bResult = false;
	private Button           m_chkExtractStandalone;
	private Button           m_rdExtractAllPairs;
	private Button           m_rdExtractNoPairs;
	private Text             m_edExceptions;
	private Button           m_chkEscapeExtendedChars;
	private LDPanel          m_pnlLD;
	private OKCancelPanel    m_pnlActions;
	private Parameters       m_Data;

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
		m_Shell.setText("JSON Filter Parameters");
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
		grpTmp.setText("Scope");
		gdTmp = new GridData(GridData.FILL_BOTH);
		grpTmp.setLayoutData(gdTmp);
		
		m_chkExtractStandalone = new Button(grpTmp, SWT.CHECK);
		m_chkExtractStandalone.setText("Extract all standalone strings");

		m_rdExtractAllPairs = new Button(grpTmp, SWT.RADIO);
		m_rdExtractAllPairs.setText("Extract all strings in key/value pairs");

		m_rdExtractNoPairs = new Button(grpTmp, SWT.RADIO);
		m_rdExtractNoPairs.setText("Extract none of the strings in key/value pairs");
		
		Label stExceptions = new Label(grpTmp, SWT.NONE);
		stExceptions.setText("Except for the strings with the following key values (one per line):");
		
		m_edExceptions = new Text(grpTmp, SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.heightHint = 80;
		m_edExceptions.setLayoutData(gdTmp);
		
		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Options");
		tiTmp.setControl(cmpTmp);
		
		//--- Inline tab
		
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Inline Codes");

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
		m_chkExtractStandalone.setSelection(m_Data.m_bExtractStandalone);
		m_rdExtractAllPairs.setSelection(m_Data.m_bExtractAllPairs);
		m_rdExtractNoPairs.setSelection(!m_Data.m_bExtractAllPairs);
		String sTmp = m_Data.m_sExceptions.replace('\t', '\n');
		m_edExceptions.setText(sTmp);
		m_chkEscapeExtendedChars.setSelection(m_Data.m_bEscapeExtendedChars);

		m_pnlLD.updateDisplay();
	}
	
	private void saveData () {
		m_Data.m_LD.setOptions(m_pnlLD.m_chkUseLD.getSelection(),
			m_pnlLD.m_chkLocalizeOutside.getSelection(), false);
		m_Data.m_bExtractStandalone = m_chkExtractStandalone.getSelection();
		m_Data.m_bExtractAllPairs = m_rdExtractAllPairs.getSelection();
		String sTmp = m_edExceptions.getText().trim();
		m_Data.m_sExceptions = sTmp.replace('\n', '\t') + '\t';
		m_Data.m_bEscapeExtendedChars = m_chkEscapeExtendedChars.getSelection();
		m_bResult = true;
	}
}

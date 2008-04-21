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

import java.util.Vector;

import net.sf.okapi.Borneo.Core.DBBase;
import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Library.UI.Dialogs;
import net.sf.okapi.Library.UI.LanguageItem;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class AddLanguagesForm {
	private Shell            m_Shell;
	private LanguageManager  m_LM;
	private OKCancelPanel    m_pnlActions;
	private String[]         m_aResults = null;
	private Table            m_Available;
	private List             m_lbToAdd;
	private Button           m_btAdd;
	private Button           m_btRemove;

	AddLanguagesForm (Shell p_Parent,
		LanguageManager p_LM,
		Vector<String> p_CurrentTargets)
	{
		m_LM = p_LM;
		m_Shell = new Shell(p_Parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		m_Shell.setText("Add Target Languages");
		m_Shell.setImage(p_Parent.getImage());
		m_Shell.setLayout(new GridLayout());

		Composite cmpTmp = new Composite(m_Shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout(4, false);
		cmpTmp.setLayout(layTmp);
		
		// Available languages
		Label stAvailable = new Label(cmpTmp, SWT.NONE);
		stAvailable.setText("Available languages:");
		GridData gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		stAvailable.setLayoutData(gdTmp);
		
		// Buttons
		Composite cmpTmp2 = new Composite(cmpTmp, SWT.NONE);
		layTmp = new GridLayout();
		cmpTmp2.setLayout(layTmp);
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		gdTmp.verticalSpan = 2;
		gdTmp.widthHint = 90;
		cmpTmp2.setLayoutData(gdTmp);
		
		m_btAdd = new Button(cmpTmp2, SWT.PUSH);
		m_btAdd.setText("Add >>");
		m_btAdd.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_btAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				add();
			}
		});

		m_btRemove = new Button(cmpTmp2, SWT.PUSH);
		m_btRemove.setText("<< Remove");
		m_btRemove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_btRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				remove();
			}
		});
		
		// Selected languages
		Label stSelected = new Label(cmpTmp, SWT.NONE);
		stSelected.setText("Languages to add:");
		
		m_Available = new Table(cmpTmp, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.heightHint = 160;
		gdTmp.widthHint = 300;
		gdTmp.horizontalSpan = 2;
		m_Available.setLayoutData(gdTmp);
		m_Available.setHeaderVisible(true);
		TableColumn colTmp = new TableColumn(m_Available, SWT.NONE);
		colTmp.setText("Language");
		colTmp = new TableColumn(m_Available, SWT.NONE);
		colTmp.setText("ISO Code");
		colTmp = new TableColumn(m_Available, SWT.NONE);
		colTmp.setText("Windows LCID");
		for ( int i=0; i<m_Available.getColumnCount(); i++ ) {
			m_Available.getColumn(i).pack();
		}
		
		m_lbToAdd = new List(cmpTmp, SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_VERTICAL);
		gdTmp.widthHint = 150;
		m_lbToAdd.setLayoutData(gdTmp);

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
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		m_pnlActions.setLayoutData(gdTmp);
		m_Shell.setDefaultButton(m_pnlActions.m_btOK);
		
		fillList(p_CurrentTargets);

		m_Shell.pack();
		Rectangle Rect = m_Shell.getBounds();
		m_Shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(m_Shell, p_Parent);
	}
	
	private void fillList (Vector<String> p_CurrentTargets) {
		LanguageItem LI;
		TableItem TI;
		m_Available.clearAll();
		for ( int i=0; i<m_LM.getCount(); i++ ) {
			LI = m_LM.getItem(i);
			// Add the languages not already in the database
			if ( p_CurrentTargets.contains(LI.getCode().toUpperCase()) ) continue;
			if ( LI.getLCID() == -1 ) continue; //TODO: option for -1 lcid
			TI = new TableItem(m_Available, SWT.NONE);
			TI.setText(0, LI.getName());
			TI.setText(1, LI.getCode().toUpperCase());
			if ( LI.getLCID() == -1 )
				TI.setText(2, "<None>");
			else
				TI.setText(2, String.format("0x%04X (%d)", LI.getLCID(), LI.getLCID()));
		}
		if ( m_Available.getItemCount() > 0 )
			m_Available.setSelection(0);
		updateButtons();
	}
	
	private boolean saveData () {
		if ( m_lbToAdd.getItemCount() == 0 ) return true; // Nothing to save
		m_aResults = m_lbToAdd.getItems();
		// Check the length of the code
		for ( int i=0; i<m_aResults.length; i++ ) {
			if ( m_aResults[i].length() > DBBase.LANGCODE_MAX ) {
				Utils.showError(String.format("The code '%s' is too long (Maximum is %d characters)",
					m_aResults[i], DBBase.LANGCODE_MAX), null);
				return false;
			}
		}
		return true;
	}
	
	private void add () {
		TableItem[] aSel = m_Available.getSelection();
		if ( aSel.length == 0 ) return;
		
		int n = m_Available.getSelectionIndex();
		for ( int i=0; i<aSel.length; i++ ) {
			m_lbToAdd.add(aSel[i].getText(1));
		}
		m_Available.remove(m_Available.getSelectionIndices());

		// Reset selection in the available languages
		if ( n >= m_Available.getItemCount()-1 )
			n = m_Available.getItemCount()-1;
		if ( n > -1 )
			m_Available.setSelection(n);
		
		// Set selection in the to-add list
		m_lbToAdd.setSelection(m_lbToAdd.getItemCount()-1);
		updateButtons();
	}
	
	private void remove () {
		int n = m_lbToAdd.getSelectionIndex();
		TableItem TI;
		LanguageItem LI;
		String[] aSel = m_lbToAdd.getSelection();
		for ( int i=0; i<aSel.length; i++ ) {
			// Add the languages to the available languages
			LI = m_LM.GetItem(aSel[i]);
			TI = new TableItem(m_Available, SWT.NONE);
			TI.setText(0, LI.getName());
			TI.setText(1, LI.getCode().toUpperCase());
			if ( LI.getLCID() == -1 )
				TI.setText(2, "<None>");
			else
				TI.setText(2, String.format("0x%04X (%d)", LI.getLCID(), LI.getLCID()));
		}
		m_lbToAdd.remove(m_lbToAdd.getSelectionIndices());

		// Reset selection in the to-add list
		if ( n >= m_lbToAdd.getItemCount()-1 ) n = m_lbToAdd.getItemCount()-1;
		if ( n > -1 ) m_lbToAdd.setSelection(n);
		
		// Reset selection in the available list
		m_Available.setSelection(m_Available.getItemCount()-1);
		updateButtons();
	}
	
	private void updateButtons () {
		m_btAdd.setEnabled(m_Available.getItemCount() > 0);
		m_btRemove.setEnabled(m_lbToAdd.getItemCount() > 0);
	}
	
	String[] showDialog () {
		m_Shell.open();
		while ( !m_Shell.isDisposed() ) {
			if ( !m_Shell.getDisplay().readAndDispatch() )
				m_Shell.getDisplay().sleep();
		}
		return m_aResults;
	}
}

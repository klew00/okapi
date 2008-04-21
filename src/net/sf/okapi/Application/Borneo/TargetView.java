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
import net.sf.okapi.Borneo.Core.DBDoc;
import net.sf.okapi.Library.Base.Utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

class TargetView extends Composite {

	private MainForm    m_MF;
	private Controller  m_C;
	private DBDoc       m_Doc = null;
	private Text        m_edDCurrent;
	private Text        m_edGCurrent;
	private Button      m_btDFirst;
	private Button      m_btDPrev;
	private Button      m_btDNext;
	private Button      m_btDLast;
	private Button      m_btDAll;
	private Button      m_btGFirst;
	private Button      m_btGPrev;
	private Button      m_btGNext;
	private Button      m_btGLast;
	private Button      m_btGAll;
	private SashForm    m_splMain;

	private Table                 m_Items;
	private TargetModel           m_ItemsM;
	private TargetDetailsPanel    m_Details;

	TargetView (Composite p_Parent,
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
		layTmp.numColumns = 7;
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 4;
		layTmp.horizontalSpacing = 0;
		layTmp.verticalSpacing = 0;
		setLayout(layTmp);

		//=== Document navigation
		
		CLabel stDocuments = new CLabel(this, SWT.NONE);
		stDocuments.setText("Doc:");
		
		int nWidth = 74;
		m_btDFirst = new Button(this, SWT.PUSH);
		m_btDFirst.setText("First");
		m_btDFirst.setImage(m_MF.m_RM.getImage("First"));
		GridData gdTmp = new GridData();
		gdTmp.widthHint = nWidth;
		m_btDFirst.setLayoutData(gdTmp);
		m_btDFirst.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				firstDocument();
			}
		});
		
		m_btDPrev = new Button(this, SWT.PUSH);
		m_btDPrev.setText("Previous");
		m_btDPrev.setImage(m_MF.m_RM.getImage("Prev"));
		gdTmp = new GridData();
		gdTmp.widthHint = nWidth;
		m_btDPrev.setLayoutData(gdTmp);
		m_btDPrev.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				previousDocument();
			}
		});

		m_btDNext = new Button(this, SWT.PUSH);
		m_btDNext.setText("Next");
		m_btDNext.setImage(m_MF.m_RM.getImage("Next"));
		gdTmp = new GridData();
		gdTmp.widthHint = nWidth;
		m_btDNext.setLayoutData(gdTmp);
		m_btDNext.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				nextDocument();
			}
		});

		m_btDLast = new Button(this, SWT.PUSH);
		m_btDLast.setText("Last");
		m_btDLast.setImage(m_MF.m_RM.getImage("Last"));
		gdTmp = new GridData();
		gdTmp.widthHint = nWidth;
		m_btDLast.setLayoutData(gdTmp);
		m_btDLast.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				lastDocument();
			}
		});

		m_btDAll = new Button(this, SWT.PUSH);
		m_btDAll.setText("All");
		m_btDAll.setImage(m_MF.m_RM.getImage("All"));
		gdTmp = new GridData();
		gdTmp.widthHint = nWidth;
		m_btDAll.setLayoutData(gdTmp);
		m_btDAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				allDocuments();
			}
		});

		m_edDCurrent = new Text(this, SWT.BORDER);
		m_edDCurrent.setEditable(false);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalIndent = 2;
		m_edDCurrent.setLayoutData(gdTmp);
		
		CLabel stGroups = new CLabel(this, SWT.NONE);
		stGroups.setText("Grp:");
		
		m_btGFirst = new Button(this, SWT.PUSH);
		m_btGFirst.setText("First");
		m_btGFirst.setImage(m_MF.m_RM.getImage("First"));
		gdTmp = new GridData();
		gdTmp.widthHint = nWidth;
		m_btGFirst.setLayoutData(gdTmp);
		
		m_btGPrev = new Button(this, SWT.PUSH);
		m_btGPrev.setText("Previous");
		m_btGPrev.setImage(m_MF.m_RM.getImage("Prev"));
		gdTmp = new GridData();
		gdTmp.widthHint = nWidth;
		m_btGPrev.setLayoutData(gdTmp);

		m_btGNext = new Button(this, SWT.PUSH);
		m_btGNext.setText("Next");
		m_btGNext.setImage(m_MF.m_RM.getImage("Next"));
		gdTmp = new GridData();
		gdTmp.widthHint = nWidth;
		m_btGNext.setLayoutData(gdTmp);

		m_btGLast = new Button(this, SWT.PUSH);
		m_btGLast.setText("Last");
		m_btGLast.setImage(m_MF.m_RM.getImage("Last"));
		gdTmp = new GridData();
		gdTmp.widthHint = nWidth;
		m_btGLast.setLayoutData(gdTmp);

		m_btGAll = new Button(this, SWT.PUSH);
		m_btGAll.setText("All");
		m_btGAll.setImage(m_MF.m_RM.getImage("All"));
		gdTmp = new GridData();
		gdTmp.widthHint = nWidth;
		m_btGAll.setLayoutData(gdTmp);

		m_edGCurrent = new Text(this, SWT.BORDER);
		m_edGCurrent.setEditable(false);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalIndent = 2;
		m_edGCurrent.setLayoutData(gdTmp);

		//=== Main panel

		m_splMain = new SashForm(this, SWT.VERTICAL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 7;
		gdTmp.verticalIndent = 2;
		m_splMain.setLayoutData(gdTmp);
		
		m_Items = new Table(m_splMain, SWT.BORDER | SWT.FULL_SELECTION);
		m_Items.setHeaderVisible(true);
		m_Items.setLinesVisible(true);
		m_ItemsM = new TargetModel();
		m_ItemsM.linkTable(m_Items, m_MF.m_RM);
		
		m_Details = new TargetDetailsPanel(m_splMain, SWT.NONE);
		
		m_splMain.SASH_WIDTH = 4;
		m_splMain.setWeights(new int[]{72,28});
	}

	void updateView () {
		setDocument(m_MF.m_nTrgDocIndex);
	}
	
	void toggleDetails (boolean p_bShow) {
		if ( p_bShow ) m_splMain.setMaximizedControl(null);
		else m_splMain.setMaximizedControl(m_Items);
	}

	boolean isDetailsVisible () {
		return (m_splMain.getMaximizedControl()==null);
	}

	void setDocument (int p_nIndex)
	{
		try {
			saveEdits();
			if ( p_nIndex == -1 ) { // No file
				m_Doc = null;
				m_edDCurrent.setText("");
				//m_edStatus.setText("");
				m_Items.removeAll();
				m_MF.m_nSrcDocIndex = p_nIndex;
				updateDocumentsButtons();
//TODO				UpdateCounter();
//TODO				m_CntxPanel.ClearContextFile();

				return;
			}

///TODO			Cursor = Cursors.WaitCursor;
			if ( p_nIndex == -2 ) { // All files
				m_Doc = null;
				m_edDCurrent.setText("<All documents>");
				//m_edStatus.setText("");
				m_ItemsM.setDB(m_C.getDB()); //TODO: move this to do it only when needed
				m_ItemsM.updateTable(-2, m_MF.m_sCurrentTarget);
				m_MF.m_nTrgDocIndex = p_nIndex;
				updateDocumentsButtons();
//TODO				UpdateCounter();
//TODO				m_CntxPanel.ClearContextFile();
				return;
			}

			// Otherwise: a specific file
			int nDKey = m_MF.getDKeyFromListIndex(p_nIndex);
			m_Doc = m_MF.m_DView.getSourceDocumentData(nDKey, null);

			// Re-check the m_Doc object to avoid issue with m_Doc being nullified when
			// calling Cancel when extracting on re-entering calls
			if ( m_Doc == null ) {
				// nDKey should be still OK
				m_Doc = m_MF.m_DView.getSourceDocumentData(nDKey, null);
			}

			m_edDCurrent.setText(String.format("(%d/%d) ", p_nIndex+1, m_MF.getDocumentCount())
				+ m_Doc.getRelativePath());
			//TODO m_edStatus.setText(m_Doc.getStatus());
			m_ItemsM.setDB(m_C.getDB()); //TODO: move this to do it only when needed
			m_ItemsM.updateTable(nDKey, m_MF.m_sCurrentTarget);
			m_MF.m_nTrgDocIndex = p_nIndex;
			updateDocumentsButtons();
//TODO			UpdateCounter();
//TODO			m_CntxPanel.ReadContextFile(GetContextPath(m_Doc.Key), m_Grid.RowCount);*/
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
		finally {
			//Cursor = Cursors.Default;
		}
	}
	
	private void firstDocument () {
		if (( m_MF.getDocumentCount() > 0 )
			&& (( m_MF.m_nTrgDocIndex > 0 )
				|| (m_MF.m_nTrgDocIndex == -2))) {
			setDocument(0);
		}
	}

	private void previousDocument () {
		if ( m_MF.m_nTrgDocIndex > 0 ) {
			setDocument(m_MF.m_nTrgDocIndex-1);
		}
	}

	private void nextDocument () {
		if (( m_MF.m_nTrgDocIndex > -1 )
			&& ( m_MF.m_nTrgDocIndex < m_MF.getDocumentCount()-1 )) {
			setDocument(m_MF.m_nTrgDocIndex+1);
		}
	}

	private void lastDocument () {
		if (( m_MF.getDocumentCount() > 0 )
			&& ( m_MF.m_nTrgDocIndex < m_MF.getDocumentCount()-1 )) {
			setDocument(m_MF.getDocumentCount()-1);
		}
	}

	private void allDocuments () {
		if (( m_MF.m_nTrgDocIndex > -2 ) && ( m_MF.getDocumentCount() > 0 )) {
			setDocument(-2);
		}
	}

	private void updateDocumentsButtons () {
		m_btDFirst.setEnabled(
			(( m_MF.getDocumentCount() > 0 ) && (( m_MF.m_nTrgDocIndex > 0 )
				|| ( m_MF.m_nTrgDocIndex == -2 ))));
		m_btDPrev.setEnabled(
			( m_MF.m_nTrgDocIndex > 0 ));
		m_btDNext.setEnabled(
			(( m_MF.m_nTrgDocIndex > -1 )
				&& ( m_MF.m_nTrgDocIndex < m_MF.getDocumentCount()-1 )));
		m_btDLast.setEnabled(
			(( m_MF.getDocumentCount() > 0 )
				&& ( m_MF.m_nTrgDocIndex < m_MF.getDocumentCount()-1 )));
		m_btDAll.setEnabled(
			(( m_MF.m_nTrgDocIndex > -2 ) && ( m_MF.getDocumentCount() > 0 )));
		updateGroupButtons();
	}
	
	private void updateGroupButtons () {
		//TODO: handle groups
		m_btGFirst.setEnabled(false);
		m_btGPrev.setEnabled(false);
		m_btGNext.setEnabled(false);
		m_btGLast.setEnabled(false);
		m_btGAll.setEnabled(false);
	}
	
	void gotoItem (int p_nSKey) {
		try {
			saveEdits();
			for ( int i=0; i<m_Items.getItemCount(); i++ ) {
				//TODO: maybe use set/get data for the SKey value in int???
				if ( p_nSKey == Integer.valueOf(m_Items.getItem(i).getText(SourceModel.COL_KEY)) ) {
					m_Items.select(i);
					return;
				}
			}
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}

	void saveEdits () {
/*TODO	if ( IsEditPanelVisible ) SaveEditedData();
		if ( m_Grid.IsCurrentCellInEditMode ) m_Grid.EndEdit();*/
	}
			
}

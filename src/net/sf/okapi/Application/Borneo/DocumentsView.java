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
import net.sf.okapi.Borneo.Core.DBTarget;
import net.sf.okapi.Borneo.Core.DBTargetDoc;
import net.sf.okapi.Library.Base.Utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

class DocumentsView extends Composite {

	private Table                 m_Docs;
	private DocumentsModel        m_DocsM;
	private Table                 m_Targets;
	private DocumentTargetsModel  m_TargetsM;
	private Text                  m_edRoot;
	private Button                m_btGetRoot;
	private SashForm              m_splMain;
	private MainForm              m_MF;
	private Controller            m_C;
	private DropTarget            m_dropDocs;
	private Menu                  m_mnuDocs;
	private MenuItem              m_miGoToSrcTable;
	private MenuItem              m_miGoToTrgTable;
	private MenuItem              m_miAddDocuments;
	private MenuItem              m_miRemoveDocuments;
	private MenuItem              m_miEditSrcDocProp;
	private MenuItem              m_miEditTrgLangProp;
	private Menu                  m_mnuTargets;
	private MenuItem              m_miEditTrgLangProp2;
	private MenuItem              m_miGoToTrgTable2;
	
	DocumentsView (Composite p_Parent,
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
		setLayout(layTmp);
		layTmp.numColumns = 3;
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 4;
		layTmp.horizontalSpacing = 4;
		layTmp.verticalSpacing = 0;
		
		Label stRoot = new Label(this, SWT.NONE);
		stRoot.setText("Source root:");
		
		m_edRoot = new Text(this, SWT.SINGLE | SWT.BORDER);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		m_edRoot.setLayoutData(gdTmp);
		m_edRoot.setEditable(false);
		
		m_btGetRoot = new Button(this, SWT.PUSH);
		m_btGetRoot.setText(" ... ");
		m_btGetRoot.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_C.changeSourceRoot();
			}
		});

		m_splMain = new SashForm(this, SWT.VERTICAL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 3;
		m_splMain.setLayoutData(gdTmp);
		
		m_Docs = new Table(m_splMain, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		m_Docs.setHeaderVisible(true);
		m_Docs.setLinesVisible(true);
		m_DocsM = new DocumentsModel();
		m_DocsM.linkTable(m_Docs);

		// Drop target for the table
		m_dropDocs = new DropTarget(m_Docs, DND.DROP_DEFAULT | DND.DROP_COPY | DND.DROP_MOVE);
		m_dropDocs.setTransfer(new FileTransfer[]{FileTransfer.getInstance()}); 
		m_dropDocs.addDropListener(new DropTargetAdapter() {
			public void drop (DropTargetEvent e) {
				FileTransfer FT = FileTransfer.getInstance();
				if ( FT.isSupportedType(e.currentDataType) ) {
					String[] aPaths = (String[])e.data;
					if ( aPaths != null ) {
						if ( aPaths.length == 1 ) {
							if ( Utils.getFilename(aPaths[0], true).toLowerCase().equals("manifest.xml") ) {
								m_C.importPackage(aPaths[0]);
								return;
							}
						}
						m_MF.addDocumentsFromList(aPaths);
					}
				}
			}
		});

		m_Targets = new Table(m_splMain, SWT.BORDER | SWT.FULL_SELECTION);
		m_Targets.setLinesVisible(true);
		m_TargetsM = new DocumentTargetsModel();
		m_TargetsM.linkTable(m_Targets);

		m_Docs.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent e) {
				int n = m_Docs.getSelectionIndex();
				if ( n == -1 ) return;
				m_TargetsM.updateTable(DBTargetDoc.convertFromString(
					m_Docs.getItem(n).getText(DocumentsModel.COL_TARGETS)));
			}
		});
		
		m_splMain.SASH_WIDTH = 4;
		m_splMain.setWeights(new int[]{80,20});
		
		// Context menu for the Documents table
		m_mnuDocs = new Menu(getShell(), SWT.POP_UP);
		m_miGoToSrcTable = new MenuItem (m_mnuDocs, SWT.PUSH);
		m_miGoToSrcTable.setText("&Go To Source Table");
		m_miGoToSrcTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_MF.gotoSourceTable(m_MF.m_DView.getSelectedDIndex(), -1);
			}
		});
		m_miGoToTrgTable = new MenuItem (m_mnuDocs, SWT.PUSH);
		m_miGoToTrgTable.setText("&Go To Target Table");
		m_miGoToTrgTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_MF.gotoTargetTable(m_MF.m_sCurrentTarget, m_MF.m_DView.getSelectedDIndex(), -1);
			}
		});

		new MenuItem(m_mnuDocs, SWT.SEPARATOR);

		m_miAddDocuments = new MenuItem (m_mnuDocs, SWT.PUSH);
		m_miAddDocuments.setText("&Add Documents...");
		m_miAddDocuments.setImage(m_MF.m_RM.getImage("AddDocuments"));
		m_miAddDocuments.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_MF.addDocumentsFromList(null);
			}
		});
		m_miRemoveDocuments = new MenuItem (m_mnuDocs, SWT.PUSH);
		m_miRemoveDocuments.setText("&Remove Documents...");
		m_miRemoveDocuments.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_MF.removeDocuments();
			}
		});

		new MenuItem(m_mnuDocs, SWT.SEPARATOR);

		m_miEditSrcDocProp = new MenuItem (m_mnuDocs, SWT.PUSH);
		m_miEditSrcDocProp.setText("Edit &Source Document Properties...");
		m_miEditSrcDocProp.setImage(m_MF.m_RM.getImage("SourceDocProperties"));
		m_miEditSrcDocProp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_MF.editSourceDocumentProperties(-3);
			}
		});
		m_miEditTrgLangProp = new MenuItem (m_mnuDocs, SWT.PUSH);
		m_miEditTrgLangProp.setText("&Edit Target Language Properties...");
		m_miEditTrgLangProp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_C.editTargetProperties(m_MF.m_sCurrentTarget);
			}
		});

		m_Docs.setMenu(m_mnuDocs);

		// Context menu for the Targets table
		m_mnuTargets = new Menu(getShell(), SWT.POP_UP);
		m_miEditTrgLangProp2 = new MenuItem (m_mnuTargets, SWT.PUSH);
		m_miEditTrgLangProp2.setText("&Edit Target Language Properties...");
		m_miEditTrgLangProp2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_C.editTargetProperties(getTargetSelection());
			}
		});
		
		new MenuItem(m_mnuTargets, SWT.SEPARATOR);

		m_miGoToTrgTable2 = new MenuItem (m_mnuTargets, SWT.PUSH);
		m_miGoToTrgTable2.setText("&Go To Target Table");
		m_miGoToTrgTable2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String sLang = getTargetSelection(); 
				if ( sLang == null ) return;
				m_MF.gotoTargetTable(sLang, m_MF.m_DView.getSelectedDIndex(), -1);
			}
		});

		m_Targets.setMenu(m_mnuTargets);
	}

	void updateView () {
		try {
			boolean bOn = m_C.isProjectOpened(); 
			if ( bOn ) {
				m_edRoot.setText(m_C.getDB().getSourceRoot());
				m_TargetsM.updateTable(null);
				m_DocsM.setDB(m_C.getDB());
				m_DocsM.updateTable();
			}
			else {
				m_edRoot.setText("");
				m_Docs.removeAll();
				m_Targets.removeAll();
			}
			m_edRoot.setEnabled(bOn);
			m_btGetRoot.setEnabled(bOn);
			m_Docs.setEnabled(bOn);
			m_Targets.setEnabled(bOn);
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}
	
	void toggleDetails (boolean p_bShow) {
		if ( p_bShow ) m_splMain.setMaximizedControl(null);
		else m_splMain.setMaximizedControl(m_Docs);
	}

	boolean isDetailsVisible () {
		return (m_splMain.getMaximizedControl()==null);
	}
	
	int[] getSelectedDKeys () {
		int[] aSel = m_Docs.getSelectionIndices();
		if (( aSel == null ) || ( aSel.length == 0 )) {
			if ( m_Docs.getItemCount() > 0 ) {
				m_Docs.select(0);
				aSel = m_Docs.getSelectionIndices();
			}
			else return null;
		}
		int[] aDKeys = new int[aSel.length];
		for ( int i=0; i<aSel.length; i++ ) {
			TableItem TI = m_Docs.getItem(aSel[i]);
			aDKeys[i] = Integer.parseInt(TI.getText(DocumentsModel.COL_KEY));
		}
		return aDKeys;
	}

	int getSelectedDKey () {
		int nIndex = m_Docs.getSelectionIndex();
		if ( nIndex == -1 ) {
			if ( m_Docs.getItemCount() > 0 ) {
				m_Docs.select(0);
				nIndex = m_Docs.getSelectionIndex();
			}
			else return -1;
		}
		TableItem TI = m_Docs.getItem(nIndex);
		return Integer.parseInt(TI.getText(DocumentsModel.COL_KEY));
	}

	int getSelectedDIndex () {
		int nIndex = m_Docs.getSelectionIndex();
		if ( nIndex == -1 ) {
			if ( m_Docs.getItemCount() > 0 ) {
				m_Docs.select(0);
				nIndex = m_Docs.getSelectionIndex();
			}
			else return -1;
		}
		return nIndex;
	}

	int[] getAllDKeys () {
		int[] aDKeys = new int[m_Docs.getItemCount()];
		for ( int i=0; i<aDKeys.length; i++ ) {
			TableItem TI = m_Docs.getItem(i);
			aDKeys[i] = Integer.parseInt(TI.getText(DocumentsModel.COL_KEY));
		}
		return aDKeys;
	}

	Table getDocumentsTable () {
		return m_Docs;
	}
	
	void saveDocuments () {
		try {
			m_DocsM.updateDB();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}
	
	TableItem getSourceDocumentItem (int p_nDKey)
	{
		// All selected items: return the current item
		if ( p_nDKey < 0 ) {
			int n = m_Docs.getSelectionIndex();
			if ( n == -1 ) return null;
			return m_Docs.getItem(n);
		}
		
		// Else: search for the given document
		String sTmp = Integer.toString(p_nDKey);
		for ( int i=0; i<m_Docs.getItemCount(); i++ ) {
			if ( m_Docs.getItem(i).getText(0).equals(sTmp) )
				return m_Docs.getItem(i);
		}
		return null; // Not found
	}

	/**
	 * Gets the source data of a given document.
	 * @param p_nDKey The key of the document to retrieve.
	 * @param p_sTrgLang The target language. Use null to not load the target information part.
	 * @param p_Doc The document data. If p_Doc is not null use it (not reset to null if not found).
	 * If p_Doc is null: a new one is created if one if found.
	 * @return The target data (in p_Doc), or null if not found and p_Doc was null.
	 */
	DBDoc getSourceDocumentData (int p_nDKey,
		String p_sTrgLang)
	{
		TableItem TI = getSourceDocumentItem(p_nDKey);
		if ( TI == null ) return null;

		DBTarget Trg = null;
		if ( p_sTrgLang != null ) Trg = m_MF.getTargetData(p_sTrgLang);
		
		DBDoc Doc = new DBDoc(TI, m_C.getDB().getSourceRoot(), m_C.getDB().getSourceRoot(), p_sTrgLang, Trg);
		return Doc;
	}
	
	/**
	 * Gets the language code of the target language currently selected
	 * in the Targets detail view.
	 * @return The language selected, or null.
	 */
	String getTargetSelection () {
		int n = m_Targets.getSelectionIndex();
		if ( n == -1 ) return null;
		return m_Targets.getItem(n).getText(0);
	}
}

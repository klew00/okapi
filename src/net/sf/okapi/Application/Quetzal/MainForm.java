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

package net.sf.okapi.Application.Quetzal;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sf.okapi.ferret.Core.Engine;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.LogForm;
import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Library.UI.Dialogs;
import net.sf.okapi.Translation.IMatch;

public class MainForm
{
	private Shell       m_Shell;
	private ILog        m_Log;
	private Engine      m_C;
	private Text        m_edQuery;
	private Button      m_btQuery;
	private List        m_lbResults;
	private MenuItem    m_miCloseTM;
	private MenuItem    m_miImportTMX;
	private MenuItem    m_miExportTMX;
	private Text        m_edCount;
	
	public MainForm (Shell p_Shell) {
		m_Shell = p_Shell;
		m_Log = new LogForm(m_Shell);
		m_Log.setTitle("Quetzal Log");
		m_C = new Engine(m_Log);
		createContent();
	}

	public void createContent () {
		GridLayout layTmp = new GridLayout();
		layTmp.numColumns = 4;
		m_Shell.setLayout(layTmp);
		
		m_Shell.setText("Quetzal [ALPHA]");
		
	    Menu mnuMain = new Menu(m_Shell, SWT.BAR);

	    MenuItem mihFile = new MenuItem(mnuMain, SWT.CASCADE);
		mihFile.setText("&File");
		Menu mnuTmp = new Menu(m_Shell, SWT.DROP_DOWN);
		mihFile.setMenu(mnuTmp);
		
		MenuItem miNewTM = new MenuItem(mnuTmp, SWT.PUSH);
		miNewTM.setText("&New TM...\tCtrl+N");
		miNewTM.setAccelerator(SWT.CTRL+'N');
		miNewTM.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				createTM();
			}
		});

		MenuItem miOpenTM = new MenuItem(mnuTmp, SWT.PUSH);
		miOpenTM.setText("&Open TM...\tCtrl+O");
		miOpenTM.setAccelerator(SWT.CTRL+'O');
		miOpenTM.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openTM();
			}
		});

		new MenuItem(mnuTmp, SWT.SEPARATOR);
		
		m_miCloseTM = new MenuItem(mnuTmp, SWT.PUSH);
		m_miCloseTM.setText("&Close TM...");
		m_miCloseTM.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_C.close();
				updateCommands();
			}
		});

		new MenuItem(mnuTmp, SWT.SEPARATOR);

		m_miImportTMX = new MenuItem(mnuTmp, SWT.PUSH);
		m_miImportTMX.setText("&Import TMX...");
		m_miImportTMX.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				importTMX();
			}
		});

		m_miExportTMX = new MenuItem(mnuTmp, SWT.PUSH);
		m_miExportTMX.setText("&Export TMX...");
		m_miExportTMX.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				exportTMX();
			}
		});

		m_Shell.setMenuBar(mnuMain);
		
		CLabel stQuery = new CLabel(m_Shell, SWT.None);
		stQuery.setText("Query:");
		
		m_edQuery = new Text(m_Shell, SWT.BORDER);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		m_edQuery.setLayoutData(gdTmp);
		
		m_btQuery = new Button(m_Shell, SWT.PUSH);
		m_btQuery.setText("Search");
		gdTmp = new GridData();
		gdTmp.widthHint = 80;
		m_btQuery.setLayoutData(gdTmp);
		m_btQuery.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				search();
			}
		});
		m_Shell.setDefaultButton(m_btQuery);
		
		m_edCount = new Text(m_Shell, SWT.BORDER);
		gdTmp = new GridData();
		gdTmp.widthHint = 80;
		m_edCount.setLayoutData(gdTmp);
		m_edCount.setEditable(false);
		
		m_lbResults = new List(m_Shell, SWT.V_SCROLL | SWT.H_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		m_lbResults.setLayoutData(gdTmp);
		updateCommands();
	}

	private void updateCommands () {
		boolean bOn = m_C.isTMOpened();
		m_btQuery.setEnabled(bOn);
		m_edCount.setEnabled(bOn);
		m_edQuery.setEnabled(bOn);
		m_miCloseTM.setEnabled(bOn);
		m_miImportTMX.setEnabled(bOn);
		m_miExportTMX.setEnabled(bOn);
		if ( !bOn ) {
			m_edQuery.setText("");
			m_lbResults.removeAll();
		}
	}
	
	public void run ()
	{
		try
		{
			Display Disp = m_Shell.getDisplay();
			while ( !m_Shell.isDisposed() ) {
				if (!Disp.readAndDispatch())
					Disp.sleep();
			}
		}
		finally {
			// Dispose of any global resources 
			if ( m_C != null ) m_C.logout();
		}
	}

	public ILog getLog () {
		return m_Log;
	}
	
	private void createTM () {
		try
		{
			String[] aPaths = Dialogs.browseFilenames(m_Shell, "New TM", false, null, null, null);
			if ( aPaths == null ) return;
			m_C.login("", "", "");
			m_C.createTM(aPaths[0]);
		}
		catch ( Exception E ) {
			Utils.showError(E.getMessage(), null);
		}
		finally {
			updateCommands();
		}
	}
	
	private void openTM () {
		try
		{
			String[] aPaths = Dialogs.browseFilenames(m_Shell, "Open TM", false, null, null, null);
			if ( aPaths == null ) return;
			String sBasePath = aPaths[0].substring(0, aPaths[0].lastIndexOf(File.separatorChar));
			m_C.login("", "", "");
			m_C.open(sBasePath);
		}
		catch ( Exception E ) {
			Utils.showError(E.getMessage(), null);
		}
		finally {
			updateCommands();
		}
	}
	
	private void importTMX () {
		try
		{
			String[] aPaths = Dialogs.browseFilenames(m_Shell, "TMX File to Import", false, null, null, null);
			if ( aPaths == null ) return;
			m_C.importTMX(aPaths[0]);
		}
		catch ( Exception E ) {
			Utils.showError(E.getMessage(), null);
		}
	}
	
	private void exportTMX () {
		try
		{
			String[] aPaths = Dialogs.browseFilenames(m_Shell, "TMX File to Export", false, null, null, null);
			if ( aPaths == null ) return;
			m_C.exportTMX(aPaths[0]);
		}
		catch ( Exception E ) {
			Utils.showError(E.getMessage(), null);
		}
	}
	
	private void search () {
		try
		{
			m_lbResults.removeAll();

			int nCount = 0;
			if ( m_edQuery.getText().equals("*") )
				nCount = m_C.queryAllEntries();
			else
				nCount = m_C.query(m_edQuery.getText());

			m_edCount.setText(String.format("=> %d", nCount));
			IMatch M;
			if ( nCount == 0 ) {
				m_lbResults.add("<No match found for '"+m_edQuery.getText()+"'>");
			}
			else for ( int i=0; i<nCount; i++ ) {
				M = m_C.getNextMatch();
				m_lbResults.add(String.format("[%d] S=%s, T=%s", M.getScore(),
					M.getSourceText(), M.getTargetText()));
			}
		}
		catch ( Exception E ) {
			Utils.showError(E.getMessage(), null);
		}
	}
}

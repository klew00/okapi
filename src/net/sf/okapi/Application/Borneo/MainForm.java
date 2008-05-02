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
import java.util.Vector;

import net.sf.okapi.Borneo.Core.Controller;
import net.sf.okapi.Borneo.Core.DBBase;
import net.sf.okapi.Borneo.Core.DBDoc;
import net.sf.okapi.Borneo.Core.DBOptions;
import net.sf.okapi.Borneo.Core.DBTarget;
import net.sf.okapi.Borneo.Core.IControllerUI;
import net.sf.okapi.Borneo.Actions.BaseAction;
import net.sf.okapi.Borneo.Actions.IAction;
import net.sf.okapi.Borneo.Actions.ImportPackageForm;
import net.sf.okapi.Filter.FilterAccess;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Library.UI.AboutForm;
import net.sf.okapi.Library.UI.Dialogs;
import net.sf.okapi.Library.UI.FormatManager;
import net.sf.okapi.Library.UI.LanguageManager;
import net.sf.okapi.Library.UI.ResourceManager;
import net.sf.okapi.Package.Manifest;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

class MainForm implements IControllerUI {

	static final int    VIEW_DOCUMENTS      = 0;
	static final int    VIEW_SOURCE         = 1;
	static final int    VIEW_TARGET         = 2;
	static final int    VIEW_SETTINGS       = 3;
	static final int    TSTATUSBASE         = 100;
	
	ResourceManager     m_RM;
	LanguageManager     m_LM;
	FormatManager       m_FM;
	FilterAccess        m_FA;

	String                   rootFolder = null;
	String                   sharedFolder = null;
	int                      srcDocIndex = -1;
	int                      trgDocIndex = -1;
	String                   currentTarget = null;
	boolean                  targetIsBeingChanged = false;
	String                   m_sCachedTrg;
	DBTarget                 m_CachedTrg;
	
	private Controller       backEnd;
	private Shell            shell;
	private ILog             m_Log;
	private StatusBar        stbMain;
	private int              m_nWait = 0;
	
	private ToolBar          m_tbMain;
	private Combo            cbTarget;
	
	private Menu             m_mnuMain;
	private MenuItem         m_mihFile;
	private MenuItem         m_miNewProject;
	private MenuItem         m_miOpenProject;
	private MenuItem         m_miCloseProject;
	private MenuItem         m_miSelectServer;
	private MenuItem         m_miExit;
	private MenuItem         m_mihView;
	private MenuItem         m_miViewDocuments;
	private MenuItem         m_miViewSource;
	private MenuItem         m_miViewTarget;
	private MenuItem         m_miViewSettings;
	private MenuItem         m_miRefreshAll;
	private MenuItem         m_miToggleLog;
	private MenuItem         m_miToggleDetails;
	private MenuItem         m_mihDocuments;
	private MenuItem         m_miAddDocuments;
	private MenuItem         m_miRemoveDocuments;
	private MenuItem         m_miEditSrcDocProp;
	private MenuItem         m_mihSource;
	private MenuItem         m_miExtractSource;
	private MenuItem         m_miUpdateSource;
	private MenuItem         m_mihTarget;
	private MenuItem         m_miUpdateTarget;
	private MenuItem         m_miImportTranslation;
	private MenuItem         m_miExportPackage;
	private MenuItem         m_miImportPackage;
	private MenuItem         m_miGenerateTarget;
	private MenuItem         m_mihHelp;
	
	private ToolItem         m_tiEditSrcDocProp;
	private ToolItem         m_tiAddDocuments;
	
	private CLabel           m_stViewTitle;
	private CLabel           m_stDView;
	private CLabel           m_stSView;
	private CLabel           m_stTView;
	private CLabel           m_stPView;
	private int              m_nView = -1;
	private MenuItem         m_miView = null;
	
	private SashForm         m_splMain;
	private Composite        m_pnlMain;
	private StackLayout      m_layMainPanel;
	DocumentsView            m_DView;
	private SourceView       m_SView;
	private TargetView       m_TView;
	private SettingsView     m_PView;
	
	public static String[]   s_aSrcStatus;
	public static String[]   s_aTrgStatus;
	
	MainForm (Shell p_Shell) {
		try {
			shell = p_Shell;
			setDirectories();
			loadResources();
			createContent();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);			
		}
	}

    private void setDirectories () {
    	// Get the location of the main class source
    	File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
    	rootFolder = file.getAbsolutePath();
    	// Remove the JAR file if running an installed version
    	if ( rootFolder.endsWith(".jar") ) rootFolder = Utils.getDirectoryName(rootFolder);
    	// Remove the application folder in all cases
    	rootFolder = Utils.getDirectoryName(rootFolder);
System.err.println("bno- final sRoot="+rootFolder);
		sharedFolder = Utils.getOkapiSharedFolder(rootFolder);
	}
	
	private void loadResources () {
		try {
			// Create the core engine
			backEnd = new Controller(this);

			// Create manager for shared UI resources
			m_RM = new ResourceManager(MainForm.class, shell.getDisplay());
			
			// Load images
			m_RM.addImage("Borneo");
			m_RM.addImage("First");
			m_RM.addImage("Prev");
			m_RM.addImage("Next");
			m_RM.addImage("Last");
			m_RM.addImage("All");
			m_RM.addImage("NewProject");
			m_RM.addImage("OpenProject");
			m_RM.addImage("AddDocuments");
			m_RM.addImage("SourceDocProperties");
			m_RM.addImage("TargetDocProperties");
			m_RM.addImage("SourceDocProperties");
			m_RM.addImage("Log");
			m_RM.addImage("HelpTopics");
			
			// Load colors
			m_RM.addColor("Documents", 178, 34, 34);
			m_RM.addColor("Source", 0 , 0, 205);
			m_RM.addColor("Target", 0, 128, 0);
			m_RM.addColor("Settings", 218, 165, 32);
			m_RM.addColor("White", 255, 255, 255);
			m_RM.addColor("SrcLightBG", 240, 248, 255);
			m_RM.addColor("TrgLightBG", 240, 255, 240);
			m_RM.addColor("SrcDarkBG", 192, 248, 255);
			m_RM.addColor("TrgDarkBG", 192, 255, 192);
			m_RM.addColor("Thistle", 216, 191, 216);
			
			// Colors for the source status
			m_RM.addColor(DBBase.SSTATUS_SAME_SAME, 50, 205, 50); // LimeGreen
			m_RM.addColor(DBBase.SSTATUS_SAME_MOD, 255, 215,0); // Gold
			m_RM.addColor(DBBase.SSTATUS_GUESSED_SAME, 144, 238, 144); // LightGreen			
			m_RM.addColor(DBBase.SSTATUS_GUESSED_MOD, 255, 222, 173); // NavajoWhite			
			m_RM.addColor(DBBase.SSTATUS_NEW, 255, 255, 0); // Yellow			
			m_RM.addColor(DBBase.SSTATUS_DELETED, 255, 99, 71); // Tomato			
			m_RM.addColor(DBBase.SSTATUS_PENDING, 216, 191, 216); // Thistle

			// Colors for the target status
			// Always add the base index to the status value
			m_RM.addColor(DBBase.TSTATUS_NOTRANS+TSTATUSBASE, 192, 192, 192); // Silver
			m_RM.addColor(DBBase.TSTATUS_TOTRANS+TSTATUSBASE, 255, 255, 0); // Yellow
			m_RM.addColor(DBBase.TSTATUS_TOEDIT+TSTATUSBASE, 255, 215, 144); // Gold
			m_RM.addColor(DBBase.TSTATUS_TOREVIEW+TSTATUSBASE, 144, 238, 144); // LightGreen
			m_RM.addColor(DBBase.TSTATUS_OK+TSTATUSBASE, 50, 205, 50); // LimeGreen
			m_RM.addColor(DBBase.TSTATUS_UNUSED+TSTATUSBASE, 255, 99, 71); // Tomato

			// Status labels for source
			s_aSrcStatus = new String[7];
			s_aSrcStatus[DBBase.SSTATUS_PENDING] = Res.getString("SSTATUS_PENDING");
			s_aSrcStatus[DBBase.SSTATUS_NEW] = Res.getString("SSTATUS_NEW");
			s_aSrcStatus[DBBase.SSTATUS_GUESSED_MOD] = Res.getString("SSTATUS_GUESSED_MOD");
			s_aSrcStatus[DBBase.SSTATUS_SAME_MOD] = Res.getString("SSTATUS_SAME_MOD");
			s_aSrcStatus[DBBase.SSTATUS_GUESSED_SAME] = Res.getString("SSTATUS_GUESSED_SAME");
			s_aSrcStatus[DBBase.SSTATUS_SAME_SAME] = Res.getString("SSTATUS_SAME_SAME");
			s_aSrcStatus[DBBase.SSTATUS_DELETED] = Res.getString("SSTATUS_DELETED");

			// Status labels for target
			s_aTrgStatus = new String[6];
			s_aTrgStatus[DBBase.TSTATUS_NOTRANS] = Res.getString("TSTATUS_NOTRANS");
			s_aTrgStatus[DBBase.TSTATUS_UNUSED] = Res.getString("TSTATUS_UNUSED");
			s_aTrgStatus[DBBase.TSTATUS_TOTRANS] = Res.getString("TSTATUS_TOTRANS");
			s_aTrgStatus[DBBase.TSTATUS_TOEDIT] = Res.getString("TSTATUS_TOEDIT");
			s_aTrgStatus[DBBase.TSTATUS_TOREVIEW] = Res.getString("TSTATUS_TOREVIEW");
			s_aTrgStatus[DBBase.TSTATUS_OK] = Res.getString("TSTATUS_OK");

			m_LM = new LanguageManager();
			m_LM.loadList(sharedFolder + File.separator + "languages.xml");

			m_FM = new FormatManager();
			//TODO m_FM.load(p_sPath);
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}

	void run () {
		try {
			Display Disp = shell.getDisplay();
			while ( !shell.isDisposed() ) {
				if (!Disp.readAndDispatch())
					Disp.sleep();
			}
		}
		finally {
			// Dispose of any global resources
			if ( m_RM != null ) m_RM.dispose();
			if ( backEnd != null ) backEnd.close();
		}
	}

	ILog getLog () {
		return m_Log;
	}
	
	public void startWaiting (String p_sText,
		boolean p_bStartLog)
	{
		if ( ++m_nWait > 1 ) {
			shell.getDisplay().update();
			return;
		}

		if ( p_sText != null ) stbMain.setInfo(p_sText);
		if ( p_bStartLog ) m_Log.beginProcess(null); 
		shell.getDisplay().update();
	}

	public void stopWaiting () {
		m_nWait--;
		if ( m_nWait < 1 ) stbMain.clearInfo();
		shell.getDisplay().update();
		if ( m_Log.inProgress() ) m_Log.endProcess(null); 
		if ( m_Log.getErrorAndWarningCount() > 0 ) m_Log.show();
	}
	
	void toggleLog (boolean p_bShow) {
		if ( p_bShow ) m_splMain.setMaximizedControl(null);
		else m_splMain.setMaximizedControl(m_pnlMain);
		m_miToggleLog.setSelection(p_bShow);
	}
	
	public void reset () {
		srcDocIndex = -1;
		trgDocIndex = -1;
		currentTarget = null;
	}
	
	public void updateEverything () {
		if ( needsClosure(true) ) return;

		// Set the application title
		shell.setText((backEnd.isProjectOpened() ? backEnd.getDB().getName() + " - " : "")
			+ "Borneo [ALPHA]");

		// Reset the cached data
		m_CachedTrg = null;
		
		// Update all and everything
		updateCommands();
		updateAllViews();
	}
	
	void updateCommands ()
	{
		boolean bOn = backEnd.isProjectOpened();
		
		m_miCloseProject.setEnabled(bOn);

		m_miAddDocuments.setEnabled(bOn);
		m_tiAddDocuments.setEnabled(bOn);
		
		bOn = (bOn && (m_DView.getDocumentsTable().getItemCount()>0));
		m_miRemoveDocuments.setEnabled(bOn);

		m_miEditSrcDocProp.setEnabled(bOn);
		m_tiEditSrcDocProp.setEnabled(bOn);
		
		m_miExtractSource.setEnabled(bOn);
		m_miUpdateSource.setEnabled(bOn);
		
		m_miUpdateTarget.setEnabled(bOn && (currentTarget != null));
		m_miImportTranslation.setEnabled(bOn && (currentTarget != null));
		m_miExportPackage.setEnabled(bOn && (currentTarget != null));
		m_miImportPackage.setEnabled(bOn && (currentTarget != null));
		m_miGenerateTarget.setEnabled(bOn && (currentTarget != null));

		m_miToggleLog.setSelection(m_splMain.getMaximizedControl()==null);
		//TODO do the update when the menu drop down
		m_miToggleDetails.setSelection(m_SView.isDetailsVisible());
	}
	
	public void updateAllViews () {
		m_DView.updateView();
		m_SView.updateView();
		m_TView.updateView();
		m_PView.updateView();
	}
	
	/**
	 * Updates a given view.
	 * @param p_nView The view index to update, or the current one if -1.
	 */
	void updateView (int p_nView) {
		if ( p_nView == -1 ) p_nView = m_nView;
		switch ( p_nView ) {
			case VIEW_DOCUMENTS:
				m_DView.updateView();
				break;
			case VIEW_SOURCE:
				m_SView.updateView();
				break;
			case VIEW_TARGET:
				m_TView.updateView();
				break;
			case VIEW_SETTINGS:
				m_PView.updateView();
				break;
		}
	}
	
	void setView (int p_nView) {
		if ( m_nView == p_nView ) return; // Already set
		if ( needsClosure(true) ) return;
		String sTitle = " ";
		if ( m_miView != null ) m_miView.setSelection(false);
		switch ( p_nView ) {
		case VIEW_DOCUMENTS:
			sTitle += Res.getString("MAIN_DViewTitle");
			m_stViewTitle.setBackground(m_RM.getColor("Documents"));
			m_layMainPanel.topControl = m_DView;
			m_miView = m_miViewDocuments;
			break;
		case VIEW_SOURCE:
			sTitle += Res.getString("MAIN_SViewTitle");
			m_stViewTitle.setBackground(m_RM.getColor("Source"));
			m_layMainPanel.topControl = m_SView;
			m_miView = m_miViewSource;
			break;
		case VIEW_TARGET:
			sTitle += Res.getString("MAIN_TViewTitle");
			m_stViewTitle.setBackground(m_RM.getColor("Target"));
			m_layMainPanel.topControl = m_TView;
			m_miView = m_miViewTarget;
			break;
		case VIEW_SETTINGS:
			sTitle += Res.getString("MAIN_PViewTitle");
			m_stViewTitle.setBackground(m_RM.getColor("Settings"));
			m_layMainPanel.topControl = m_PView;
			m_miView = m_miViewSettings;
			break;
		}
		m_stViewTitle.setText(sTitle);
		m_nView = p_nView;
		m_miView.setSelection(true);
		m_pnlMain.layout();
	}
	
	private void createContent ()
		throws Exception
	{
		shell.setImage(m_RM.getImage("Borneo"));
		GridLayout layTmp1 = new GridLayout();
		layTmp1.marginHeight = 0;
		layTmp1.marginWidth = 0;
		//layTmp1.horizontalSpacing = 0;
		layTmp1.verticalSpacing = 0;
		shell.setLayout(layTmp1);
		
		//=== Menus
		
	    m_mnuMain = new Menu(shell, SWT.BAR);
		
		m_mihFile = new MenuItem(m_mnuMain, SWT.CASCADE);
		m_mihFile.setText("&File");
		Menu mnuTmp = new Menu(shell, SWT.DROP_DOWN);
		m_mihFile.setMenu(mnuTmp);
		m_miNewProject = new MenuItem(mnuTmp, SWT.PUSH);
		m_miNewProject.setText("&New Project...\tCtrl+N");
		m_miNewProject.setImage(m_RM.getImage("NewProject"));
		m_miNewProject.setAccelerator(SWT.CTRL+'N');
		m_miNewProject.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( needsClosure(true) ) return;
				backEnd.createProject();
			}
		});
		m_miOpenProject = new MenuItem(mnuTmp, SWT.PUSH);
		m_miOpenProject.setText("&Open Project...\tCtrl+O");
		m_miOpenProject.setImage(m_RM.getImage("OpenProject"));
		m_miOpenProject.setAccelerator(SWT.CTRL+'O');
		m_miOpenProject.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( needsClosure(true) ) return;
				backEnd.openProject(null);
			}
		});
		m_miCloseProject = new MenuItem(mnuTmp, SWT.PUSH);
		m_miCloseProject.setText("&Close Project...\tCtrl+F4");
		m_miCloseProject.setAccelerator(SWT.CTRL | SWT.F4);
		m_miCloseProject.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( needsClosure(true) ) return;
				backEnd.closeProject();
			}
		});
		
		new MenuItem(mnuTmp, SWT.SEPARATOR);
		
		m_miSelectServer = new MenuItem(mnuTmp, SWT.PUSH);
		m_miSelectServer.setText("&Select Server...");
		m_miSelectServer.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( needsClosure(true) ) return;
				backEnd.selectProjectServer();
			}
		});

		new MenuItem(mnuTmp, SWT.SEPARATOR);
		
		m_miExit = new MenuItem(mnuTmp, SWT.PUSH);
		m_miExit.setText("E&xit");
		m_miExit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if ( needsClosure(true) ) return;
            	shell.close();
            }
		});
		
		m_mihView = new MenuItem(m_mnuMain, SWT.CASCADE);
		m_mihView.setText("&View");
		mnuTmp = new Menu(shell, SWT.DROP_DOWN);
		m_mihView.setMenu(mnuTmp);

		m_miViewDocuments = new MenuItem(mnuTmp, SWT.RADIO);
		m_miViewDocuments.setText(Res.getString("MAIN_DViewTitle"));
		m_miViewDocuments.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setView(VIEW_DOCUMENTS);
			}
		});
		m_miViewSource = new MenuItem(mnuTmp, SWT.RADIO);
		m_miViewSource.setText(Res.getString("MAIN_SViewTitle"));
		m_miViewSource.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setView(VIEW_SOURCE);
			}
		});
		m_miViewTarget = new MenuItem(mnuTmp, SWT.RADIO);
		m_miViewTarget.setText(Res.getString("MAIN_TViewTitle"));
		m_miViewTarget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setView(VIEW_TARGET);
			}
		});
		m_miViewSettings = new MenuItem(mnuTmp, SWT.RADIO);
		m_miViewSettings.setText(Res.getString("MAIN_PViewTitle"));
		m_miViewSettings.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setView(VIEW_SETTINGS);
			}
		});

		new MenuItem(mnuTmp, SWT.SEPARATOR);
		
		m_miRefreshAll = new MenuItem(mnuTmp, SWT.PUSH);
		m_miRefreshAll.setText("&Refresh All\tF5");
		m_miRefreshAll.setAccelerator(SWT.F5);
		m_miRefreshAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateEverything();
			}
		});

		new MenuItem(mnuTmp, SWT.SEPARATOR);
		
		m_miToggleLog = new MenuItem(mnuTmp, SWT.CHECK);
		m_miToggleLog.setText("&Log\tF9");
//Gives ugly display		m_miToggleLog.setImage(m_RM.getImage("Log"));
		m_miToggleLog.setAccelerator(SWT.F9);
		m_miToggleLog.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				toggleLog(m_splMain.getMaximizedControl()!=null);
			}
		});
		m_miToggleDetails = new MenuItem(mnuTmp, SWT.CHECK);
		m_miToggleDetails.setText("&Details Panel\tF4");
		m_miToggleDetails.setAccelerator(SWT.F4);
		m_miToggleDetails.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				switch ( m_nView ) {
					case VIEW_DOCUMENTS:
						m_DView.toggleDetails(!m_DView.isDetailsVisible());
						break;
					case VIEW_SOURCE:
						m_SView.toggleDetails(!m_SView.isDetailsVisible());
						break;
					case VIEW_TARGET:
						m_TView.toggleDetails(!m_TView.isDetailsVisible());
						break;
				}
			}
		});

		m_mihDocuments = new MenuItem(m_mnuMain, SWT.CASCADE);
		m_mihDocuments.setText("&Documents");
		mnuTmp = new Menu(shell, SWT.DROP_DOWN);
		m_mihDocuments.setMenu(mnuTmp);
		
		m_miAddDocuments = new MenuItem(mnuTmp, SWT.PUSH);
		m_miAddDocuments.setText("&Add Documents...");
		m_miAddDocuments.setImage(m_RM.getImage("AddDocuments"));
		m_miAddDocuments.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addDocumentsFromList(null);
			}
		});
		m_miRemoveDocuments = new MenuItem(mnuTmp, SWT.PUSH);
		m_miRemoveDocuments.setText("&Remove Documents...");
		m_miRemoveDocuments.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeDocuments();
			}
		});

		new MenuItem(mnuTmp, SWT.SEPARATOR);
		
		m_miEditSrcDocProp = new MenuItem(mnuTmp, SWT.PUSH);
		m_miEditSrcDocProp.setText("Edit &Source Document Properties...");
		m_miEditSrcDocProp.setImage(m_RM.getImage("SourceDocProperties"));
		m_miEditSrcDocProp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editSourceDocumentProperties(-3);
			}
		});

		m_mihSource = new MenuItem(m_mnuMain, SWT.CASCADE);
		m_mihSource.setText("&Source");
		mnuTmp = new Menu(shell, SWT.DROP_DOWN);
		m_mihSource.setMenu(mnuTmp);
		
		m_miExtractSource = new MenuItem(mnuTmp, SWT.PUSH);
		m_miExtractSource.setText("&Extract Source Document...");
		m_miExtractSource.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				launchActionFromAnywhere(BaseAction.ID_EXTRACTSOURCE);
			}
		});
		m_miUpdateSource = new MenuItem(mnuTmp, SWT.PUSH);
		m_miUpdateSource.setText("&Update Source Table...");
		m_miUpdateSource.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				launchActionFromAnywhere(BaseAction.ID_UPDATESOURCE);
			}
		});

		m_mihTarget = new MenuItem(m_mnuMain, SWT.CASCADE);
		m_mihTarget.setText("&Target");
		mnuTmp = new Menu(shell, SWT.DROP_DOWN);
		m_mihTarget.setMenu(mnuTmp);
		
		m_miUpdateTarget = new MenuItem(mnuTmp, SWT.PUSH);
		m_miUpdateTarget.setText("&Update Target Table...");
		m_miUpdateTarget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				launchActionFromAnywhere(BaseAction.ID_UPDATETARGET);
			}
		});
		m_miImportTranslation = new MenuItem(mnuTmp, SWT.PUSH);
		m_miImportTranslation.setText("Import &Translation...");
		m_miImportTranslation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				launchActionFromAnywhere(BaseAction.ID_IMPORTTRANSLATION);
			}
		});

		new MenuItem(mnuTmp, SWT.SEPARATOR);
		
		m_miExportPackage = new MenuItem(mnuTmp, SWT.PUSH);
		m_miExportPackage.setText("&Export Translation Package...");
		m_miExportPackage.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				launchActionFromAnywhere(BaseAction.ID_EXPORTPACKAGE);
			}
		});
		m_miImportPackage = new MenuItem(mnuTmp, SWT.PUSH);
		m_miImportPackage.setText("&Import Translation Package...");
		m_miImportPackage.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( needsClosure(true) ) return;
				backEnd.importPackage(null);
			}
		});

		new MenuItem(mnuTmp, SWT.SEPARATOR);
		
		m_miGenerateTarget = new MenuItem(mnuTmp, SWT.PUSH);
		m_miGenerateTarget.setText("&Generate Target Documents...");
		m_miGenerateTarget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				launchActionFromAnywhere(BaseAction.ID_GENERATETARGET);
			}
		});

		m_mihHelp = new MenuItem(m_mnuMain, SWT.CASCADE);
		m_mihHelp.setText("&Help");
		mnuTmp = new Menu(shell, SWT.DROP_DOWN);
		m_mihHelp.setMenu(mnuTmp);

		MenuItem miHelpTopic = new MenuItem(mnuTmp, SWT.PUSH);
		miHelpTopic.setText("&Help Topics");
		miHelpTopic.setImage(m_RM.getImage("HelpTopics"));
		miHelpTopic.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Utils.startPage("TODO");
			}
		});
		MenuItem miHowTo = new MenuItem(mnuTmp, SWT.PUSH);
		miHowTo.setText("How To &Use Borneo");
		miHowTo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Utils.startPage("TODO");
			}
		});

		new MenuItem(mnuTmp, SWT.SEPARATOR);

		MenuItem miSendFeedback = new MenuItem(mnuTmp, SWT.PUSH);
		miSendFeedback.setText("&Send Feedback");
		miSendFeedback.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Utils.startPage("mailto:okapitools@opentag.com&subject=Feedback (Borneo)");
			}
		});
		MenuItem miSubmitBug = new MenuItem(mnuTmp, SWT.PUSH);
		miSubmitBug.setText("&Submit &Bug Report");
		miSubmitBug.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO: fix param to make it work
				Utils.startPage("http://sourceforge.net/tracker/?group_id=42949&atid=434659");
			}
		});
		MenuItem miSubmitReq = new MenuItem(mnuTmp, SWT.PUSH);
		miSubmitReq.setText("&Submit Feature &Request");
		miSubmitReq.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO: fix param to make it work
				Utils.startPage("http://sourceforge.net/tracker/?group_id=42949&atid=434662");
			}
		});
		MenuItem miUserGroup = new MenuItem(mnuTmp, SWT.PUSH);
		miUserGroup.setText("User &Group");
		miUserGroup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Utils.startPage("http://tech.groups.yahoo.com/group/okapitools");
			}
		});

		new MenuItem(mnuTmp, SWT.SEPARATOR);

		MenuItem miAbout = new MenuItem(mnuTmp, SWT.PUSH);
		miAbout.setText("&About Borneo...");
		miAbout.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showAbout();
			}
		});

		shell.setMenuBar(m_mnuMain);
		
		//=== Toolbar
		
		m_tbMain = new ToolBar(shell, SWT.RIGHT);
		
		ToolItem tiTmp = new ToolItem(m_tbMain, SWT.PUSH);
		tiTmp.setImage(m_RM.getImage("NewProject"));
		tiTmp.setToolTipText("New Project");
		tiTmp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( needsClosure(true) ) return;
				backEnd.createProject();
			}
		});

		tiTmp = new ToolItem(m_tbMain, SWT.PUSH);
		tiTmp.setImage(m_RM.getImage("OpenProject"));
		tiTmp.setToolTipText("Open Project");
		tiTmp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( needsClosure(true) ) return;
				backEnd.openProject(null);
			}
		});
		
		new ToolItem(m_tbMain, SWT.SEPARATOR);

		tiTmp = new ToolItem(m_tbMain, SWT.PUSH);
		tiTmp.setImage(m_RM.getImage("Log"));
		tiTmp.setToolTipText("Show / Hide Log");
		tiTmp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				toggleLog(m_splMain.getMaximizedControl()!=null);
			}
		});
		
		new ToolItem(m_tbMain, SWT.SEPARATOR);

		m_tiAddDocuments = new ToolItem(m_tbMain, SWT.PUSH);
		m_tiAddDocuments.setImage(m_RM.getImage("AddDocuments"));
		m_tiAddDocuments.setToolTipText("Add Documents");
		m_tiAddDocuments.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addDocumentsFromList(null);
			}
		});

		m_tiEditSrcDocProp = new ToolItem(m_tbMain, SWT.PUSH);
		m_tiEditSrcDocProp.setImage(m_RM.getImage("SourceDocProperties"));
		m_tiEditSrcDocProp.setToolTipText("Edit Source Document Properties");
		m_tiEditSrcDocProp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editSourceDocumentProperties(-3);
			}
		});
		
	    new ToolItem(m_tbMain, SWT.SEPARATOR);

	    tiTmp = new ToolItem(m_tbMain, SWT.SEPARATOR);
		CLabel stLang = new CLabel(m_tbMain, SWT.NONE);
		stLang.setText("Target:");
		stLang.pack();
		tiTmp.setWidth(stLang.getSize().x+4);
	    tiTmp.setControl(stLang);

	    tiTmp = new ToolItem(m_tbMain, SWT.SEPARATOR);
		cbTarget = new Combo(m_tbMain, SWT.READ_ONLY);
		cbTarget.setVisibleItemCount(25);
		cbTarget.setBackground(m_RM.getColor("TrgDarkBG"));
	    cbTarget.pack();
	    cbTarget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				changeTarget(cbTarget.getItem(cbTarget.getSelectionIndex()), false);
			}
		});
		tiTmp.setWidth(100);
		tiTmp.setControl(cbTarget);
		
		m_tbMain.pack();
		
		//=== View bar
		
		Composite Comp = new Composite(shell, SWT.NONE);
		GridLayout layTmp2 = new GridLayout();
		layTmp2.numColumns = 5;
		layTmp2.marginHeight = 0;
		layTmp2.marginWidth = 0;
		layTmp2.horizontalSpacing = 0;
		Comp.setLayout(layTmp2);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		Comp.setLayoutData(gdTmp);

		m_stViewTitle = new CLabel(Comp, SWT.NONE);
		m_stViewTitle.setForeground(m_RM.getColor("White"));
		m_stViewTitle.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		int nWidth = Integer.valueOf(Res.getString("MAIN_stView_w"));
		int nHeight = Integer.valueOf(Res.getString("MAIN_stView_h"));
		m_stDView = new CLabel(Comp, SWT.CENTER);
		m_stDView.setText(Res.getString("MAIN_stDView"));
		gdTmp = new GridData();
		gdTmp.heightHint = nHeight;
		gdTmp.widthHint = nWidth;
		gdTmp.horizontalIndent = 4;
		m_stDView.setLayoutData(gdTmp);
		m_stDView.setBackground(m_RM.getColor("Documents"));
		m_stDView.setForeground(m_RM.getColor("White"));
		m_stDView.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {}
			public void mouseDown(MouseEvent e) {
				setView(VIEW_DOCUMENTS);
			}
			public void mouseUp(MouseEvent e) {}
		});

		m_stSView = new CLabel(Comp, SWT.CENTER);
		m_stSView.setText(Res.getString("MAIN_stSView"));
		gdTmp = new GridData();
		gdTmp.heightHint = nHeight;
		gdTmp.widthHint = nWidth;
		m_stSView.setLayoutData(gdTmp);
		m_stSView.setBackground(m_RM.getColor("Source"));
		m_stSView.setForeground(m_RM.getColor("White"));
		m_stSView.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {}
			public void mouseDown(MouseEvent e) {
				setView(VIEW_SOURCE);
			}
			public void mouseUp(MouseEvent e) {}
		});
		
		m_stTView = new CLabel(Comp, SWT.CENTER);
		m_stTView.setText(Res.getString("MAIN_stTView"));
		m_stTView.setLayoutData(gdTmp);
		m_stTView.setBackground(m_RM.getColor("Target"));
		m_stTView.setForeground(m_RM.getColor("White"));
		m_stTView.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {}
			public void mouseDown(MouseEvent e) {
				setView(VIEW_TARGET);
			}
			public void mouseUp(MouseEvent e) {}
		});
		
		m_stPView = new CLabel(Comp, SWT.CENTER);
		m_stPView.setText(Res.getString("MAIN_stPView"));
		m_stPView.setLayoutData(gdTmp);
		m_stPView.setBackground(m_RM.getColor("Settings"));
		m_stPView.setForeground(m_RM.getColor("White"));
		m_stPView.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {}
			public void mouseDown(MouseEvent e) {
				setView(VIEW_SETTINGS);
			}
			public void mouseUp(MouseEvent e) {}
		});

		//=== Main Panel
		
		m_splMain = new SashForm(shell, SWT.VERTICAL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		m_splMain.setLayoutData(gdTmp);

		m_pnlMain = new Composite(m_splMain, SWT.NONE);
		m_layMainPanel = new StackLayout();
		m_pnlMain.setLayout(m_layMainPanel);
		
		m_Log = new LogPanel(m_splMain, SWT.NONE, this);
		backEnd.initialize(m_Log, rootFolder);
		m_FA = new FilterAccess(m_Log);
		m_FA.loadList(sharedFolder+File.separator+"filters.xml");

		m_splMain.SASH_WIDTH = 4;
		m_splMain.setWeights(new int[]{80,20});

		m_DView = new DocumentsView(m_pnlMain, SWT.NONE, this, backEnd);
		m_SView = new SourceView(m_pnlMain, SWT.NONE, this, backEnd);
		m_TView = new TargetView(m_pnlMain, SWT.NONE, this, backEnd);
		m_PView = new SettingsView(m_pnlMain, SWT.NONE, this, backEnd);
		
		// Status bar
		stbMain = new StatusBar(shell, SWT.NONE);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		stbMain.setLayoutData(gdTmp);

		// Set the default view
		toggleLog(false);
		updateEverything();
		setView(VIEW_SETTINGS);
		
		shell.setSize(740, 510);
		shell.setMinimumSize(696, 480);
	}
	
	int getDocumentCount () {
		return m_DView.getDocumentsTable().getItemCount();
	}

	int getDKeyFromListIndex (int p_nIndex)
	{
		try {
			if ( getDocumentCount() <= 0 ) return -1;
			return Integer.valueOf(m_DView.getDocumentsTable().getItem(
				p_nIndex).getText(DocumentsModel.COL_KEY));
		}
		catch ( Exception E ) {
			return -1;
		}
	}

	DBTarget getTargetData (String p_sLangCode) {
		try {
			if (( m_CachedTrg == null ) || !p_sLangCode.equals(m_sCachedTrg) ) {
				// Load the target data
				m_CachedTrg = backEnd.getDB().getTargetData(p_sLangCode);
				m_sCachedTrg = p_sLangCode;
			}
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
		return m_CachedTrg;
	}
	
	boolean needsClosure (boolean p_bShowMessage) {
		// Save any updated data from source and target panels
		m_SView.saveEdits();
		m_TView.saveEdits();

		// Check if settings are being edited
		if ( m_PView.inEditMode() ) {
			if ( p_bShowMessage ) {
				MessageBox Dlg = new MessageBox(shell, SWT.ICON_WARNING);
				Dlg.setMessage(Res.getString("STILLEDITING"));
				Dlg.open();
			}
			return true;
		}
		return false;
	}
	
	//TODO: Move this to the controller
	void removeDocuments () {
		boolean bRes = false;
		try {
			if ( needsClosure(true) ) return;
			// Get the selected files from the Document view
			int[] aDKeys = m_DView.getSelectedDKeys();
			if ( aDKeys == null ) return;

			// Ask confirmation
			MessageBox Dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			Dlg.setMessage(Res.getString("ASK_REMOVEDOC"));
			Dlg.setText("Borneo");
			if ( Dlg.open() != SWT.YES ) return;
			
			startWaiting("REMOVING_DOCUMENTS", false);
			backEnd.getDB().startBatchMode();
			for ( int i=0; i<aDKeys.length; i++ ) {
				backEnd.getDB().removeDocument(aDKeys[i]);
			}
			bRes = true;
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
			bRes = false;
		}
		finally {
			try {
				backEnd.getDB().stopBatchMode(bRes);
			}
			catch ( Exception E ) {
				Utils.showError(E.getLocalizedMessage(), null);
			}
			updateCommands();
			m_DView.updateView();
			stopWaiting();
		}
	}
	
	/**
	 * Adds documents to the project from a list.
	 * @param p_aPaths List of the full path of the documents to add, use
	 * null to get select the files from a dialog.
	 */
	void addDocumentsFromList (String[] p_aPaths)
	{
		try {
			if ( needsClosure(true) ) return;
			// Get a list of paths if needed
			if ( p_aPaths == null ) {
				p_aPaths = Dialogs.browseFilenames(shell, "Add Documents",
					true, backEnd.getDB().getSourceRoot(), null, null);
			}
			if ( p_aPaths == null ) return;
			startWaiting("ADDING_DOCUMENTS", false);
			doAddDocuments(p_aPaths, null);
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
		finally {
			m_DView.updateView();
			updateCommands();
			stopWaiting();
		}
	}

	/**
	 * Adds a list of files/directories to the documents UI list. The method recurses
	 * into sub-directories if needed. 
	 * @param p_aPaths List of the files to add. If p_sDir is null, they must be
	 * full path names, if p_sDir is not null, they should be relative path to p_sDir.
	 * @param p_sDir Full path of the parent directory. Use null to use full paths in
	 * the p_aPaths list.
	 * @return The number of documents added.
	 * @throws Exception
	 */
	private int doAddDocuments (String[] p_aPaths,
		String p_sDir)
		throws Exception
	{
		int n = 0;
		for ( String sPath : p_aPaths ) {
			if ( p_sDir != null ) {
				sPath = p_sDir + File.separator + sPath;
			}
			File F = new File(sPath);
			if ( F.isDirectory() ) {
				n += doAddDocuments(F.list(), sPath);
			}
			else {
				String[] aRes = m_FM.guessFormat(sPath);
				if ( backEnd.getDB().addDocument(sPath, 0, aRes[0], aRes[1]) != -1 ) n++;
			}
		}
		return n;
	}
	
	void updateTargetLists () {
		try {
			cbTarget.removeAll();
			int nPrev = m_PView.getTargetListSelectionIndex();
			m_PView.getTargetList().removeAll();

			if ( backEnd.isProjectOpened() ) {
				Vector<String> aLangs = backEnd.getDB().fetchLanguageList();
				for ( int i=0; i<aLangs.size(); i++ ) {
					cbTarget.add(aLangs.get(i));
					m_PView.getTargetList().add(String.format("%s - %s",
						aLangs.get(i), m_LM.GetNameFromCode(aLangs.get(i))));
				}
				if ( nPrev > m_PView.getTargetList().getItemCount()-1 )
					nPrev = m_PView.getTargetList().getItemCount()-1;
				if ( nPrev > -1 )
					m_PView.getTargetList().select(nPrev);
				changeTarget(currentTarget, true);
			}
			else {
//TODO				m_btTarget.Text = Properties.Resources.TARGETBTN_ADD;
//				m_btTarget.ToolTipText = Properties.Resources.TARGETBTN_ADDTIP;
				m_PView.updateTargetButtons();
				return;
			}
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}
	
	void changeTarget (String p_sNewTarget,
		boolean p_bForce)
	{
		try {
			if ( needsClosure(true) ) return;
			if (( currentTarget == p_sNewTarget ) && !p_bForce ) return;
			currentTarget = p_sNewTarget;
			if ( currentTarget == null ) currentTarget = ""; 
			int n = cbTarget.indexOf(currentTarget);
			if ( n > -1 ) cbTarget.select(n);
			
			if ( cbTarget.getSelectionIndex() == -1 ) {
				// Try to fall back to the first available target
				if ( cbTarget.getItemCount() > 0 ) {
					cbTarget.select(0);
					currentTarget = cbTarget.getItem(cbTarget.getSelectionIndex());
				}
				else currentTarget = null;
			}

			if (( cbTarget.getItemCount() == 0 ) || ( currentTarget == null )) {
//TODO				m_btTarget.Text = Properties.Resources.TARGETBTN_ADD;
//TODO				m_btTarget.ToolTipText = Properties.Resources.TARGETBTN_ADDTIP;
			}
			else {
//TODO				m_btTarget.Text = Properties.Resources.TARGETBTN_EDIT;
//TODO				m_btTarget.ToolTipText = Properties.Resources.TARGETBTN_EDITTIP;
			}

			//TODO m_TView.SetFile(TargetDocumentIndex);
			m_PView.updateTargetButtons();
			updateCommands();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}
	
	public boolean editTargetProperties (DBTarget p_Data,
		String p_sLang) {
		try {
			TargetLangPropertiesForm Dlg = new TargetLangPropertiesForm(shell);
			
			int n = m_DView.getSelectedDKey();
			DBDoc Doc = null;
			if ( n > -1 ) Doc = backEnd.getDB().getSourceDocumentData(n, null);

			Dlg.setData(backEnd.getDB().getSourceRoot(),
				((Doc!=null) ? Doc.getFullPath() : null), p_Data, p_sLang);
			return Dlg.showDialog(); 
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
			return false;
		}
	}

	public String selectProject (int p_nDBType) {
		try {
			if ( p_nDBType == 0 ) {
				String[] aPaths = Dialogs.browseFilenames(shell, "Open Project", false,
					null, "Borneo Projects (*.index.db)", "*.index.db");
				if ( aPaths == null ) return null;
				else return aPaths[0];
			}
			else {
				SelectProjectForm Dlg = new SelectProjectForm(shell);
				return Dlg.showDialog();
			}
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
			return null;
		}
	}

	public String[] selectNewTargetLanguages () {
		try {
			AddLanguagesForm Dlg = new AddLanguagesForm(shell, m_LM,
				backEnd.getDB().fetchLanguageList());
			return Dlg.showDialog();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
			return null;
		}
	}
	
	void removeTargetLanguages () {
		try {
			// Get the language to remove
			String sLang = m_PView.getTargetListSelection();
			if ( sLang == null ) return;
			// Ask confirmation
			MessageBox Dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			Dlg.setMessage(String.format(Res.getString("ASK_REMOVETARGET"), sLang));
			Dlg.setText("Borneo");
			if ( Dlg.open() != SWT.YES ) return;
			// Remove the language
			backEnd.removeTargetLanguage(sLang);
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}
	
	public boolean selectServerType (DBOptions p_Data) {
		try {
			SelectServerForm Dlg = new SelectServerForm(shell);
			Dlg.setData(p_Data);
			return Dlg.showDialog();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
			return false;
		}
	}
	
	public String[] selectNewProjectInfo (int p_nDBType) {
		try {
			NewProjectForm Dlg = new NewProjectForm(shell, p_nDBType);
			return Dlg.showDialog();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
			return null;
		}
	}
	
	void saveSettings () {
		try {
			startWaiting("UPDATING_PROJECT", false);
			backEnd.getDB().saveSettings();
			m_PView.updateView();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
		finally {
			stopWaiting();
		}
	}
	
	private void showAbout () {
		try {
			AboutForm Dlg = new AboutForm(shell,
				"Borneo - Okapi Localization Manager",
				"Version 0.1.0");
			Dlg.showDialog();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}
	
	public String selectNewSourceRoot () {
		try {
			EditSourceRootForm Dlg = new EditSourceRootForm(shell,
				backEnd.getDB().getSourceRoot());
			return Dlg.showDialog();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
			return null;
		}
	}

	void editSourceDocumentProperties (int p_nDKey) {
		try {
			if ( needsClosure(true) ) return;
			if ( p_nDKey < 0 ) {
				p_nDKey = m_DView.getSelectedDKey();
			}
			// Get the document data
			TableItem TI = m_DView.getSourceDocumentItem(p_nDKey); 

			// Set defaults
			String sFSettings = TI.getText(DocumentsModel.COL_FSETTINGS);
			String sEncoding = TI.getText(DocumentsModel.COL_ENCODING);
			String sFileSet = TI.getText(DocumentsModel.COL_FILESET);

			// Call the dialog
			SourceDocPropertiesForm Dlg = new SourceDocPropertiesForm(shell, backEnd);
			Dlg.setData(sFSettings, sEncoding, sFileSet, m_FA);
			String[] aData = Dlg.showDialog();
			if ( aData == null ) return;

			// Update the file(s) data
			startWaiting("UPDATING_PROJECT", false);
			if ( p_nDKey < 0 ) {
				TableItem[] aItems = m_DView.getDocumentsTable().getSelection();
				for ( int i=0; i<aItems.length; i++ ) {
					aItems[i].setText(DocumentsModel.COL_FSETTINGS, aData[0]);
					aItems[i].setText(DocumentsModel.COL_ENCODING, aData[1]);
					aItems[i].setText(DocumentsModel.COL_FILESET, aData[2]);
				}
			}
			else {
				TI.setText(DocumentsModel.COL_FSETTINGS, aData[0]);
				TI.setText(DocumentsModel.COL_ENCODING, aData[1]);
				TI.setText(DocumentsModel.COL_FILESET, aData[2]);
			}
			m_DView.saveDocuments();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
		finally {
			stopWaiting();
		}
	}

	void launchActionFromAnywhere (String p_sActionID) {
		try {
			if ( needsClosure(true) ) return;
			switch ( m_nView ) {
				case VIEW_DOCUMENTS:
				case VIEW_SETTINGS:
					backEnd.launchAction(p_sActionID, -3, currentTarget, currentTarget);
					break;
				case VIEW_SOURCE:
					backEnd.launchAction(p_sActionID, getDKeyFromListIndex(srcDocIndex),
						currentTarget, currentTarget);
					break;
				case VIEW_TARGET:
					backEnd.launchAction(p_sActionID, getDKeyFromListIndex(trgDocIndex),
						currentTarget, currentTarget);
					break;
			}
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}

	public Object[] getLaunchOptions (IAction p_Action,
		int p_nDocScope,
		String p_sTargetScope,
		String p_sCurrentTarget)
	{
		try {
			ExecuteActionForm Dlg = new ExecuteActionForm(shell, m_LM, m_DView, m_PView);
			int nCurDKey = p_nDocScope;
			if ( p_nDocScope < 0 ) nCurDKey = m_DView.getSelectedDKey();
			DBDoc Doc = m_DView.getSourceDocumentData(nCurDKey, null);
			Dlg.setData(p_Action, p_nDocScope, Doc, p_sTargetScope, p_sCurrentTarget);
			return Dlg.showDialog();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
			return null;
		}
	}

	public void setDocumentsView() {
		setView(VIEW_DOCUMENTS);
	}

	public void setSettingsView() {
		setView(VIEW_SETTINGS);
	}

	public void updateDocumentsView() {
		updateView(VIEW_DOCUMENTS);
	}

	public void updateSettingsView() {
		updateView(VIEW_SETTINGS);
	}
	
	public void updateTargetView() {
		updateView(VIEW_TARGET);
	}
	
	public void updateSourceView() {
		updateView(VIEW_SOURCE);
	}
	
	void gotoTargetTable (String p_sTarget,
		int p_nDIndex,
		int p_nSKey)
	{
		try {
			if ( needsClosure(true) ) return;
			setView(MainForm.VIEW_TARGET);
			if ( p_sTarget != null )
				changeTarget(p_sTarget, false);
			if (( p_nDIndex > -1 ) && ( p_nDIndex != trgDocIndex ))
				m_TView.setDocument(p_nDIndex);
			if ( p_nSKey > -1 )
				m_TView.gotoItem(p_nSKey);
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}

	void gotoSourceTable (int p_nDIndex,
		int p_nSKey)
	{
		try {
			if ( needsClosure(true) ) return;
			setView(MainForm.VIEW_SOURCE);
			if (( p_nDIndex > -1 ) && ( p_nDIndex != srcDocIndex ))
				m_SView.setDocument(p_nDIndex);
			if ( p_nSKey > -1 )
				m_SView.gotoItem(p_nSKey);
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}
	
	public String selectImportPackage () {
		try {
			String[] aPaths = Dialogs.browseFilenames(shell, "Import Translation Package", false,
				null, "Borneo Manifests (Manifest.xml)", "Manifest.xml");
			if ( aPaths == null ) return null;
			else return aPaths[0];
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
			return null;
		}
	}

	public boolean editImportPackageOptions (Manifest p_Manifest) {
		try {
			ImportPackageForm Dlg = new ImportPackageForm();
			return Dlg.edit(p_Manifest, (Object)shell);
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
			return false;
		}
	}



	/*TEST with thread
	public void openProject (String p_sName) {
		try {
			// Do we need to select a server?
			if ( backEnd.getDB() == null ) {
				
			}
			// Get the project name
			String sName = p_sName;
			// If there is no project parameter, select one
			if ( sName == null ) {
				if ( backEnd.getDBOptions().getDBType() == 0 ) {
					String[] aPaths = Dialogs.browseFilenames(shell, "Open Project", false, null);
					if ( aPaths == null ) return;
					else sName = aPaths[0];
				}
				else {
					SelectProjectForm Dlg = new SelectProjectForm(shell);
					sName = Dlg.showDialog();
				}
				if ( sName == null ) return;
			}
			startWaiting("OPENING_PROJECT", null);
			Worker W = new Worker(backEnd, 0);
			W.setParam1(sName);
			W.start();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
		finally {
			updateEverything();
			setView(MainForm.VIEW_DOCUMENTS);
			stopWaiting();
		}
	}*/
	
}

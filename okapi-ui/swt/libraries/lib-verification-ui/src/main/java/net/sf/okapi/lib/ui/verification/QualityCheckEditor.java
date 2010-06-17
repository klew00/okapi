/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.lib.ui.verification;

import java.io.File;

import net.sf.okapi.common.BaseContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.ResourceManager;
import net.sf.okapi.common.ui.UserConfiguration;
import net.sf.okapi.lib.verification.QualityCheckSession;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class QualityCheckEditor {

	private static final String APPNAME = "CheckMate"; //$NON-NLS-1$

	private UserConfiguration config;
	private IHelp help;
	private Shell shell;
	private ResourceManager rm;
	private Table tblIssues;
	private Text edSource;
	private Text edTarget;
	private IssuesTableModel issuesModel;
	private QualityCheckSession session;
	private Button btRefreshAll;

	@Override
	protected void finalize () {
		dispose();
	}
	
	/**
	 * Creates a new QualityCheckEditor dialog.
	 * @param parent the parent shell.
	 * @param asDialog true if used from another program.
	 * @param helpParam the help engine to use.
	 */
	public QualityCheckEditor (Shell parent,
		boolean asDialog,
		IHelp helpParam,
		IFilterConfigurationMapper fcMapper)
	{
		help = helpParam;
		config = new UserConfiguration();
		config.load(APPNAME);

		session = new QualityCheckSession();
		session.setFilterConfigurationMapper(fcMapper);

		if ( asDialog ) {
			shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.MAX | SWT.MIN | SWT.APPLICATION_MODAL);
		}
		else {
			shell = parent;
		}
		shell.setLayout(new GridLayout());

		rm = new ResourceManager(QualityCheckEditor.class, shell.getDisplay());
		rm.loadCommands("net.sf.okapi.lib.ui.verification.Commands"); //$NON-NLS-1$
		
		createMenus();
		createContent();
	}
	
	public void dispose () {
		if ( rm != null ) {
			rm.dispose();
		}
	}

	/**
	 * Opens the dialog box, loads an QC session if one is specified.
	 * @param path Optional QC session to load. Use null to load nothing.
	 */
	public void showDialog (String path) {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
	}
	
	private void createMenus () {
		// Menus
	    Menu menuBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuBar);

		MenuItem topItem = new MenuItem(menuBar, SWT.CASCADE);
		topItem.setText("&File"); //$NON-NLS-1$
		Menu dropMenu = new Menu(shell, SWT.DROP_DOWN);
		topItem.setMenu(dropMenu);
	}
	
	private void createContent () {
		// Create the two main parts of the UI
		SashForm sashMain = new SashForm(shell, SWT.VERTICAL);
		sashMain.setLayoutData(new GridData(GridData.FILL_BOTH));
		sashMain.setSashWidth(3);
		sashMain.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		
		//--- Edit panel
		
		Composite cmpTmp = new Composite(sashMain, SWT.NONE);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout();
		layTmp.marginWidth = 0;
		layTmp.marginHeight = 0;
		cmpTmp.setLayout(layTmp);
		
		SashForm sashEdit = new SashForm(cmpTmp, SWT.VERTICAL);
		sashEdit.setLayoutData(new GridData(GridData.FILL_BOTH));
		sashEdit.setSashWidth(2);
		
		edSource = new Text(sashEdit, SWT.BORDER);
		edSource.setLayoutData(new GridData(GridData.FILL_BOTH));

		edTarget = new Text(sashEdit, SWT.BORDER);
		edTarget.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		//--- Issues panel
		
		cmpTmp = new Composite(sashMain, SWT.BORDER);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite cmpButtons = new Composite(cmpTmp, SWT.NONE);
		layTmp = new GridLayout(7, true);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		cmpButtons.setLayout(layTmp);
		cmpButtons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		btRefreshAll = new Button(cmpButtons, SWT.PUSH);
		btRefreshAll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btRefreshAll.setText("Refresh All");
		btRefreshAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});
		
		Button btOptions = new Button(cmpButtons, SWT.PUSH);
		btOptions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btOptions.setText("Options...");
		btOptions.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editOptions();
			}
		});
		
		//UIUtil.ensureWidth(btRecheck, UIUtil.BUTTON_DEFAULT_WIDTH);
		
		tblIssues = new Table(cmpTmp, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.CHECK);
		tblIssues.setHeaderVisible(true);
		tblIssues.setLinesVisible(true);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		//gdTmp.minimumHeight = 250;
		tblIssues.setLayoutData(gdTmp);
		
		tblIssues.addControlListener(new ControlAdapter() {
		    public void controlResized(ControlEvent e) {
		    	Rectangle rect = tblIssues.getClientArea();
		    	int checkColWidth = 32;
				int part = (int)((rect.width-checkColWidth) / 100);
				int remainder = (int)((rect.width-checkColWidth) % 100);
				tblIssues.getColumn(0).setWidth(checkColWidth);
				tblIssues.getColumn(1).setWidth(part*10);
				tblIssues.getColumn(2).setWidth(part*10);
				tblIssues.getColumn(3).setWidth(remainder+(part*80));
		    }
		});
		
		issuesModel = new IssuesTableModel();
		issuesModel.linkTable(tblIssues);
		
		// Set minimum and start sizes
		Point defaultSize = shell.getSize();
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = defaultSize;
		if ( startSize.x < 700 ) startSize.x = 700; 
		if ( startSize.y < 600 ) startSize.y = 600; 
		shell.setSize(startSize);
//		if ( asDialog ) {
//			Dialogs.centerWindow(shell, parent);
//		}
		
	}
	
	public void addDocument (String path) {
		String filterConfigId = null;
		String ext = Util.getExtension(path);
		if ( ext.equalsIgnoreCase(".xlf")
			|| ext.equalsIgnoreCase(".xliff")
			|| ext.equalsIgnoreCase(".sdlxlf") ) {
			filterConfigId = "okf_xliff";
		}
		else if ( ext.equalsIgnoreCase(".tmx") ) {
			filterConfigId = "okf_tmx";
		}
		else if ( ext.equalsIgnoreCase(".po") ) {
			filterConfigId = "okf_po";
		}
		else if ( ext.equalsIgnoreCase(".ts") ) {
			filterConfigId = "okf_ts";
		}
		
		LocaleId srcLoc = LocaleId.ENGLISH;
		LocaleId trgLoc = LocaleId.FRENCH;
		String encoding = "UTF-8";
		RawDocument rd = new RawDocument((new File(path)).toURI(), encoding, srcLoc, trgLoc);
		rd.setFilterConfigId(filterConfigId);
		session.addRawDocument(rd);
	}
	
	private void refresh () {
		try {
			session.refreshAll();
			issuesModel.setIssues(session.getIssues());
			issuesModel.updateTable(0);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error while refreshing.\n"+e.getMessage(), null);
		}
	}

	private void editOptions () {
		try {
			ParametersEditor editor = new ParametersEditor();
			BaseContext context = new BaseContext();
			context.setObject("help", help);
			context.setObject("shell", shell);
			editor.edit(session.getParameters(), false, context);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error editing options.\n"+e.getMessage(), null);
		}
	}
}

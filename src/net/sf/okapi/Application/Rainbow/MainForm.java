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

package net.sf.okapi.Application.Rainbow;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.LogForm;
import net.sf.okapi.Library.Base.Utils;

public class MainForm
{
	private Shell       shell;
	private ILog        log;
	private String      rootFolder;
	private String      sharedFolder;
	
	private Table       inputTable;
	private StatusBar   statusBar;
	private Text        edRoot;
	private Button      btGetRoot;
	private TabItem     tiInput;
	private TabItem     tiOptions;
	
	public MainForm (Shell p_Shell) {
		try {
			shell = p_Shell;
			log = new LogForm(shell);
			log.setTitle("Rainbow Log");
			setDirectories();
			loadResources();
			createContent();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);			
		}
	}

	private void createContent () {
		GridLayout layTmp = new GridLayout(3, false);
		shell.setLayout(layTmp);
		shell.setText("Rainbow [v6 ALPHA]");

		// Menus
	    Menu menuBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuBar);

		// File menu
		MenuItem topItem = new MenuItem(menuBar, SWT.CASCADE);
		topItem.setText("&File");
		Menu dropMenu = new Menu(shell, SWT.DROP_DOWN);
		topItem.setMenu(dropMenu);
		
		MenuItem menuItem = new MenuItem(dropMenu, SWT.PUSH);
		menuItem.setText("E&xit");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
            	shell.close();
            }
		});

		// View menu
		topItem = new MenuItem(menuBar, SWT.CASCADE);
		topItem.setText("&View");
		dropMenu = new Menu(shell, SWT.DROP_DOWN);
		topItem.setMenu(dropMenu);

		// Input menu
		topItem = new MenuItem(menuBar, SWT.CASCADE);
		topItem.setText("&Input");
		dropMenu = new Menu(shell, SWT.DROP_DOWN);
		topItem.setMenu(dropMenu);

		// Utilities menu
		topItem = new MenuItem(menuBar, SWT.CASCADE);
		topItem.setText("&Utilities");
		dropMenu = new Menu(shell, SWT.DROP_DOWN);
		topItem.setMenu(dropMenu);

		// Root panel
		Label label = new Label(shell, SWT.NONE);
		label.setText("Root:");
		
		edRoot = new Text(shell, SWT.SINGLE | SWT.BORDER);
		edRoot.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edRoot.setEditable(false);
		
		btGetRoot = new Button(shell, SWT.PUSH);
		btGetRoot.setText(" ... ");
		btGetRoot.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				changeRoot();
			}
		});
		
		// Tab control
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 3;
		tabFolder.setLayoutData(gdTmp);

		// Input List tab
		Composite comp = new Composite(tabFolder, SWT.NONE);
		comp.setLayout(new GridLayout());
		tiInput = new TabItem(tabFolder, SWT.NONE);
		tiInput.setText("Input Documents");
		tiInput.setControl(comp);
	
		inputTable = new Table(comp, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		inputTable.setHeaderVisible(true);
		inputTable.setLinesVisible(true);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 3;
		inputTable.setLayoutData(gdTmp);
		
		// Options tab
		comp = new Composite(tabFolder, SWT.NONE);
		comp.setLayout(new GridLayout());
		tiOptions = new TabItem(tabFolder, SWT.NONE);
		tiOptions.setText("Options");
		tiOptions.setControl(comp);

		// Output tab
		comp = new Composite(tabFolder, SWT.NONE);
		comp.setLayout(new GridLayout());
		tiOptions = new TabItem(tabFolder, SWT.NONE);
		tiOptions.setText("Output");
		tiOptions.setControl(comp);

		// Status bar
		statusBar = new StatusBar(shell, SWT.NONE);
	}

	public void run () {
		try {
			Display Disp = shell.getDisplay();
			while ( !shell.isDisposed() ) {
				if (!Disp.readAndDispatch())
					Disp.sleep();
			}
		}
		finally {
			// Dispose of any global resources 
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
		sharedFolder = Utils.getOkapiSharedFolder(rootFolder);
	}
	
	private void loadResources () {
		
	}
	
	private void changeRoot () {
		
	}
}

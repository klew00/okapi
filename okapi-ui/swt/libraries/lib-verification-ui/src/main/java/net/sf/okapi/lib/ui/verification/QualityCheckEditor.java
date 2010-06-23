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
import java.net.URI;

import net.sf.okapi.common.BaseContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.ui.AboutDialog;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.ResourceManager;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.common.ui.UserConfiguration;
import net.sf.okapi.lib.verification.IQualityCheckEditor;
import net.sf.okapi.lib.verification.Issue;
import net.sf.okapi.lib.verification.QualityCheckSession;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class QualityCheckEditor implements IQualityCheckEditor {

	private static final String APPNAME = "CheckMate"; //$NON-NLS-1$

	private String qcsPath;
	private UserConfiguration config;
	private IHelp help;
	private Shell shell;
	private ResourceManager rm;
	private Table tblIssues;
	private Text edMessage;
	private Text edSource;
	private Text edTarget;
	private Font displayFont;
	private IssuesTableModel issuesModel;
	private StatusBar statusBar;
	private QualityCheckSession session;
	private Button btCheckAll;
	private Combo cbDisplay;
	private Button btRefreshDisplay;
	private Combo cbTypes;
	
	private int displayType = 1;
	private int issueType = 0;

	/**
	 * Creates a default editor, to allow dynamic instantiation.
	 * Either {@link #runEditSession(Object, boolean, IHelp, IFilterConfigurationMapper)} or
	 * {@link #initialize(Object, boolean, IHelp, IFilterConfigurationMapper)} must be called
	 * afterward. 
	 */
	public QualityCheckEditor () {
	}
	
	@Override
	public void edit () {
		showDialog(null);
	}

	/**
	 * Initializes this IQualityCheckEditor object.
	 * @param parent the object representing the parent window/shell for this editor.
	 * In this implementation  this parameter must be the Shell of the caller.
	 * @param asDialog true if used from another program.
	 * @param helpParam the help engine to use.
	 * @param fcMapper the IFilterConfigurationMapper object to use with the editor.
	 * @param session an optional session to use (null to use one created internally)
	 */
	@Override
	public void initialize (Object parent,
		boolean asDialog,
		IHelp helpParam,
		IFilterConfigurationMapper fcMapper,
		QualityCheckSession paramSession)
	{
		help = helpParam;
		config = new UserConfiguration();
		config.load(APPNAME);
		long id = Thread.currentThread().getId();
		// If no parent is defined, create a new display and shell
		if ( parent == null ) {
			// Start the application
			Display dispMain = new Display();
			long t2 = dispMain.getThread().getId();
			parent = new Shell(dispMain);
		}

		// Set or create the session
		if ( paramSession == null ) {
			session = new QualityCheckSession();
		}
		else {
			session = paramSession;
		}
		session.setFilterConfigurationMapper(fcMapper);

		if ( asDialog ) {
			shell = new Shell((Shell)parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.MAX | SWT.MIN | SWT.APPLICATION_MODAL);
		}
		else {
			shell = (Shell)parent;
		}
		shell.setLayout(new GridLayout());

		rm = new ResourceManager(QualityCheckEditor.class, shell.getDisplay());
		rm.loadCommands("net.sf.okapi.lib.ui.verification.Commands"); //$NON-NLS-1$

		rm.addImages("checkmate", "checkmate16", "checkmate32"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		shell.setImages(rm.getImages("checkmate")); //$NON-NLS-1$
		
		createMenus();
		createContent();
	}
	
	public void addRawDocument (RawDocument rawDoc) {
		session.addRawDocument(rawDoc);
	}
	
	@Override
	protected void finalize () {
		dispose();
	}

	private void dispose () {
		if ( displayFont != null ) {
			displayFont.dispose();
			displayFont = null;
		}
		if ( rm != null ) {
			rm.dispose();
			rm = null;
		}
	}

	/**
	 * Opens the dialog box, loads an QC session if one is specified.
	 * @param path Optional QC session to load. Use null to load nothing.
	 */
	public void showDialog (String path) {
		shell.open();
		if ( path != null ) {
			String ext = Util.getExtension(path);
			if ( ext.equalsIgnoreCase(QualityCheckSession.FILE_EXTENSION) ) {
				loadSession(path);
			}
			else {
				addDocumentFromUI(path);
			}
		}
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
	}
	
	private void createMenus () {
		// Menus
	    Menu menuBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuBar);

		//=== File menu
		
		MenuItem topItem = new MenuItem(menuBar, SWT.CASCADE);
		topItem.setText(rm.getCommandLabel("file")); //$NON-NLS-1$
		Menu dropMenu = new Menu(shell, SWT.DROP_DOWN);
		topItem.setMenu(dropMenu);
		
		MenuItem menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "file.open"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				loadSession(null);
            }
		});

		new MenuItem(dropMenu, SWT.SEPARATOR);

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "file.save"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveSessionAs(qcsPath);
            }
		});

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "file.saveas"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveSessionAs(null);
            }
		});

		new MenuItem(dropMenu, SWT.SEPARATOR);

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "file.adddocument"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				addDocumentFromUI(null);
            }
		});

		new MenuItem(dropMenu, SWT.SEPARATOR);

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "file.exit"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
            }
		});

		//=== Issues menu

		topItem = new MenuItem(menuBar, SWT.CASCADE);
		topItem.setText(rm.getCommandLabel("issues")); //$NON-NLS-1$
		dropMenu = new Menu(shell, SWT.DROP_DOWN);
		topItem.setMenu(dropMenu);
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "issues.options"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				editOptions();
            }
		});
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "issues.checkall"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				checkAll();
            }
		});
		
		//=== Help menu

		topItem = new MenuItem(menuBar, SWT.CASCADE);
		topItem.setText(rm.getCommandLabel("help")); //$NON-NLS-1$
		dropMenu = new Menu(shell, SWT.DROP_DOWN);
		topItem.setMenu(dropMenu);
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "help.topics"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if ( help != null ) help.showTopic(this, "index"); //$NON-NLS-1$
            }
		});
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "help.howtouse"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if ( help != null ) help.showTopic(this, "index", "howTo.html"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});

		new MenuItem(dropMenu, SWT.SEPARATOR);

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "help.update"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				UIUtil.start("http://okapi.opentag.com/updates?" //$NON-NLS-1$
					+ getClass().getPackage().getImplementationTitle()
					+ "=" //$NON-NLS-1$
					+ getClass().getPackage().getImplementationVersion());
			}
		});

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "help.feedback"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				UIUtil.start("mailto:okapitools@opentag.com&subject=Feedback (CheckMate: Quality Checker)"); //$NON-NLS-1$
            }
		});
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "help.bugreport"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				UIUtil.start("http://code.google.com/p/okapi/issues/list"); //$NON-NLS-1$
			}
		});
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "help.featurerequest"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				UIUtil.start("http://code.google.com/p/okapi/issues/list"); //$NON-NLS-1$
			}
		});
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "help.users"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				UIUtil.start("http://groups.yahoo.com/group/okapitools/"); //$NON-NLS-1$
            }
		});

		new MenuItem(dropMenu, SWT.SEPARATOR);

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "help.about"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				AboutDialog dlg = new AboutDialog(shell,
					"About CheckMate",
					"CheckMate - Okapi Quality Checker",
					getClass().getPackage().getImplementationVersion());
				dlg.showDialog();
            }
		});

	}
	
	private void createContent () {
		// Create the two main parts of the UI
		SashForm sashMain = new SashForm(shell, SWT.VERTICAL);
		sashMain.setLayoutData(new GridData(GridData.FILL_BOTH));
		sashMain.setSashWidth(4);
		//Not needed: sashMain.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		
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
		
		Font font = edSource.getFont();
		FontData[] fontData = font.getFontData();
		fontData[0].setHeight(fontData[0].getHeight()+2);
		displayFont = new Font(font.getDevice(), fontData[0]);
		edSource.setFont(displayFont);
		edTarget.setFont(displayFont);
		
		//--- Issues panel
		
		cmpTmp = new Composite(sashMain, SWT.BORDER);
		layTmp = new GridLayout(4, false);
		cmpTmp.setLayout(layTmp);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		edMessage = new Text(cmpTmp, SWT.BORDER);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		edMessage.setLayoutData(gdTmp);

		Composite cmpButtons = new Composite(cmpTmp, SWT.NONE);
		layTmp = new GridLayout(4, true);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		cmpButtons.setLayout(layTmp);
		cmpButtons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		btCheckAll = new Button(cmpButtons, SWT.PUSH);
		btCheckAll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btCheckAll.setText("Check All");
		btCheckAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				checkAll();
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
		
		cbTypes = new Combo(cmpTmp, SWT.DROP_DOWN | SWT.READ_ONLY);
		cbTypes.add("<All types of issues>"); // All types
		cbTypes.add("Missing target"); // MISSING_TARGETTU
		cbTypes.add("Missing target segment"); // MISSING_TARGETSEG
		cbTypes.add("Empty target segment"); // EMPTY_TARGETSEG
		cbTypes.add("Target same as source"); // TARGET_SAME_AS_SOURCE
		cbTypes.add("Missing white spaces"); // All missing whitespace-related issues
		cbTypes.add("Extra white spaces"); // All extra whitespace-related issues
		cbTypes.add("Inline codes differents"); // CODE_DIFFERENCE
		cbTypes.add("Unexpected patterns"); // MISSING_PATTERN
		cbTypes.add("Warnings from LanguageTool checker"); // LANGUAGETOOL_ERROR
		cbTypes.setVisibleItemCount(10);
		cbTypes.setLayoutData(new GridData());
		cbTypes.select(issueType);
		cbTypes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateDisplayType();
			};
		});
		
		cbDisplay = new Combo(cmpTmp, SWT.DROP_DOWN | SWT.READ_ONLY);
		cbDisplay.add("Show enabled and disabled issues");
		cbDisplay.add("Show only enabled issues");
		cbDisplay.add("Show only disabled issues");
		cbDisplay.setLayoutData(new GridData()); //GridData.FILL_HORIZONTAL));
		cbDisplay.select(displayType);
		cbDisplay.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateDisplayType();
			};
		});
		
		btRefreshDisplay = new Button(cmpTmp, SWT.PUSH);
		btRefreshDisplay.setText("Refresh");
		UIUtil.ensureWidth(btRefreshDisplay, UIUtil.BUTTON_DEFAULT_WIDTH);
		btRefreshDisplay.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateDisplayType();
			}
		});

		tblIssues = new Table(cmpTmp, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.CHECK);
		tblIssues.setHeaderVisible(true);
		tblIssues.setLinesVisible(true);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
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
				tblIssues.getColumn(2).setWidth(part*5);
				tblIssues.getColumn(3).setWidth(remainder+(part*85));
		    }
		});
		
		tblIssues.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if ( event.detail == SWT.CHECK ) {
					tblIssues.setSelection((TableItem)event.item); // Force selection to move if needed
					Issue issue = (Issue)event.item.getData();
					issue.enabled = !issue.enabled;
				}
				updateCurrentIssue();
            }
		});

		issuesModel = new IssuesTableModel();
		issuesModel.linkTable(tblIssues);

		sashMain.setWeights(new int[]{30, 70});
		
		statusBar = new StatusBar(shell, SWT.NONE);
		
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
		
		updateCaption();
		updateDisplayType();
	}

	private void updateDisplayType () {
		displayType = cbDisplay.getSelectionIndex();
		issueType = cbTypes.getSelectionIndex();
		issuesModel.updateTable(tblIssues.getSelectionIndex(), displayType, issueType);
		updateCurrentIssue();
	}
	
	private void addDocumentFromUI (String path) {
		try {
			InputDocumentDialog dlg = new InputDocumentDialog(shell, "Add document",
				session.getFilterConfigurationMapper());
			// Set default data
			dlg.setData(path, null, "UTF-8", session.getSourceLocale(), session.getTargetLocale());
			// Lock the locales if we have already documents in the session
			dlg.setLocalesEditable(session.getDocumentCount()==0);

			// Edit
			Object[] data = dlg.showDialog();
			if ( data == null ) return;
			
			// Create the raw document to add to the session
			URI uri = (new File((String)data[0])).toURI();
			RawDocument rd = new RawDocument(uri, (String)data[2], (LocaleId)data[3], (LocaleId)data[4]);
			rd.setFilterConfigId((String)data[1]);
			session.addRawDocument(rd);
			
			// If it is the first document: its locales become the default
			if ( session.getDocumentCount() == 1 ) {
				session.setSourceLocale((LocaleId)data[3]);
				session.setTargetLocale((LocaleId)data[4]);
			}
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error adding document.\n"+e.getMessage(), null);
		}
	}
	
	private void updateCaption () {
		String filename;
		if ( qcsPath != null ) {
			filename = Util.getFilename(qcsPath, true);
		}
		else {
			filename = "Untitled";
		}
		String text = "CheckMate [ALPHA]";
		shell.setText(filename + " - " + text); //$NON-NLS-1$
	}

	private void updateCurrentIssue () {
		int n = tblIssues.getSelectionIndex();
		if ( n == -1 ) {
			edMessage.setText("");
			edSource.setText("");
			edTarget.setText("");
		}
		else {
			Issue issue = (Issue)tblIssues.getItem(n).getData();
			edMessage.setText(issue.message);
			String tmp = issue.oriSource;
			edSource.setText(tmp);
			tmp = issue.oriTarget;
			edTarget.setText(tmp);
		}
		statusBar.setCounter(n, tblIssues.getItemCount());
	}

	private void checkAll () {
		try {
			session.refreshAll();
			issuesModel.setIssues(session.getIssues());
			issuesModel.updateTable(0, displayType, issueType);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error while refreshing.\n"+e.getMessage(), null);
		}
		finally {
			updateCurrentIssue();
		}
	}

	private void editOptions () {
		try {
			ParametersEditor editor = new ParametersEditor();
			BaseContext context = new BaseContext();
			if ( help != null ) context.setObject("help", help);
			context.setObject("shell", shell);
			editor.edit(session.getParameters(), false, context);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error editing options.\n"+e.getMessage(), null);
		}
	}

	private void saveSessionAs (String path) {
		try {
			if ( path == null ) {
				path = Dialogs.browseFilenamesForSave(shell, "Save Session", null,
					String.format("Quality Check Sessions (*%s)\tAll Files (*.*)", QualityCheckSession.FILE_EXTENSION),
					String.format("*%s\t*.*", QualityCheckSession.FILE_EXTENSION));
				if ( path == null ) return;
				qcsPath = path;
			}
			session.saveSession(qcsPath);
			updateCaption();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error while saving.\n"+e.getMessage(), null);
		}
	}

	private void loadSession (String path) {
		try {
			if ( path == null ) {
				String[] paths = Dialogs.browseFilenames(shell, "Open Session", false, null,
					String.format("Quality Check Sessions (*%s)\tAll Files (*.*)", QualityCheckSession.FILE_EXTENSION),
					String.format("*%s\t*.*", QualityCheckSession.FILE_EXTENSION));
				if ( paths == null ) return;
				path = paths[0];
			}
			session.loadSession(path);
			issuesModel.updateTable(0, displayType, issueType);
			qcsPath = path;
			updateCaption();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error while saving.\n"+e.getMessage(), null);
		}
	}

}

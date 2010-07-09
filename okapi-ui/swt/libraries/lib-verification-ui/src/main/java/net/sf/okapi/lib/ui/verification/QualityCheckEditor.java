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
import java.util.Collections;
import java.util.Iterator;

import net.sf.okapi.common.BaseContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.ui.AboutDialog;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.MRUList;
import net.sf.okapi.common.ui.ResourceManager;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.common.ui.UserConfiguration;
import net.sf.okapi.lib.verification.IQualityCheckEditor;
import net.sf.okapi.lib.verification.Issue;
import net.sf.okapi.lib.verification.IssueComparator;
import net.sf.okapi.lib.verification.QualityCheckSession;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class QualityCheckEditor implements IQualityCheckEditor {

	static final int ISSUETYPE_ALL = 0;
	static final int ISSUETYPE_ENABLED = 1;
	static final int ISSUETYPE_DISABLED = 2;
	
	private static final String OPT_BOUNDS = "bounds"; //$NON-NLS-1$
	private static final String OPT_MAXIMIZED = "maximized"; //$NON-NLS-1$
	private static final String APPNAME = "CheckMate"; //$NON-NLS-1$
	private static final String CFG_SOURCELOCALE = "sourceLocale"; //$NON-NLS-1$
	private static final String CFG_TARGETLOCALE = "targetLocale"; //$NON-NLS-1$

	private String qcsPath;
	private UserConfiguration config;
	private MRUList mruList;
	private MenuItem miMRU;
	private IHelp help;
	private Shell shell;
	private ResourceManager rm;
	private Table tblIssues;
	private Text edMessage;
	private Text edDocument;
	private StyledText edSource;
	private StyledText edTarget;
	private Font displayFont;
	private IssuesTableModel issuesModel;
	private StatusBar statusBar;
	private QualityCheckSession session;
	private Combo cbDisplay;
	private Button btRefreshDisplay;
	private Combo cbTypes;
	private int waitCount;
	
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
	public void edit (boolean processOnStart) {
		showDialog(null, processOnStart);
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
		mruList = new MRUList(9);
		mruList.getFromProperties(config);
		
		// If no parent is defined, create a new display and shell
		if ( parent == null ) {
			// Start the application
			Display dispMain = new Display();
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
		try {
			LocaleId tmpLoc = LocaleId.fromBCP47(config.getProperty(CFG_SOURCELOCALE, "en"));
			session.setSourceLocale(tmpLoc);
			tmpLoc = LocaleId.fromBCP47(config.getProperty(CFG_TARGETLOCALE, "fr"));
			session.setTargetLocale(tmpLoc);
			session.setModified(false);
		}
		catch ( Throwable e ) {
			// Just use the defaults, no need to have an error
		}

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
		if ( asDialog ) {
			Dialogs.centerWindow(shell, (Shell)parent);
		}
		tblIssues.setFocus();
	}
	
	@Override
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
	 * @param processOnStart true to trigger the verification process when the editor is opened.
	 */
	public void showDialog (String path,
		boolean processOnStart)
	{
		shell.open();
		if ( path != null ) {
			String ext = Util.getExtension(path);
			if ( ext.equalsIgnoreCase(QualityCheckSession.FILE_EXTENSION) ) {
				loadSession(path);
				// Loading a session always trigger a re-processing
				// So don't call it here again
			}
			else {
				addDocumentFromUI(path);
				if ( processOnStart ) checkAll();
			}
		}
		else {
			if ( processOnStart ) checkAll();
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
		rm.setCommand(menuItem, "file.new"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				newSession();
            }
		});

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
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

		miMRU = new MenuItem(dropMenu, SWT.CASCADE);
		rm.setCommand(miMRU, "file.mru"); //$NON-NLS-1$
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "file.clearmru"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
            	clearMRU();
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
		rm.setCommand(menuItem, "file.sessionsettings"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				editSessionSettings();
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
		rm.setCommand(menuItem, "issues.resetdisabledissues"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				resetDisabledIssues();
            }
		});
		
		new MenuItem(dropMenu, SWT.SEPARATOR);

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "issues.checkall"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				checkAll();
            }
		});
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "issues.checkdocument"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				checkCurrentDocument();
            }
		});
		
		new MenuItem(dropMenu, SWT.SEPARATOR);

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "issues.generatereport"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				generateReport();
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
		// Handling of the closing event
		shell.addShellListener(new ShellListener() {
			public void shellActivated(ShellEvent event) {}
			public void shellClosed(ShellEvent event) {
				if ( !saveSessionIfNeeded() ) event.doit = false;
			}
			public void shellDeactivated(ShellEvent event) {}
			public void shellDeiconified(ShellEvent event) {}
			public void shellIconified(ShellEvent event) {}
		});

		// Create the two main parts of the UI
		SashForm sashMain = new SashForm(shell, SWT.VERTICAL);
		sashMain.setLayoutData(new GridData(GridData.FILL_BOTH));
		sashMain.setSashWidth(4);
		//Not needed: sashMain.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		
		// Drop target for the table
		DropTarget dropTarget = new DropTarget(sashMain, DND.DROP_DEFAULT | DND.DROP_COPY | DND.DROP_MOVE);
		dropTarget.setTransfer(new FileTransfer[]{FileTransfer.getInstance()}); 
		dropTarget.addDropListener(new DropTargetAdapter() {
			public void drop (DropTargetEvent e) {
				FileTransfer FT = FileTransfer.getInstance();
				if ( FT.isSupportedType(e.currentDataType) ) {
					String[] paths = (String[])e.data;
					if ( paths != null ) {
						for ( String path : paths ) {
							if ( !addDocumentFromUI(path) ) {
								break; // Stop now
							}
						}
					}
				}
			}
		});

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
		
		edSource = new StyledText(sashEdit, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		edSource.setLayoutData(new GridData(GridData.FILL_BOTH));
		edSource.setEditable(false);

		edTarget = new StyledText(sashEdit, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		edTarget.setLayoutData(new GridData(GridData.FILL_BOTH));
		edTarget.setEditable(false);
		
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
		edMessage.setEditable(false);

		edDocument = new Text(cmpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		edDocument.setLayoutData(gdTmp);
		edDocument.setEditable(false);

		Composite cmpButtons = new Composite(cmpTmp, SWT.NONE);
		layTmp = new GridLayout(4, true);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		cmpButtons.setLayout(layTmp);
		cmpButtons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button btCheckAll = new Button(cmpButtons, SWT.PUSH);
		btCheckAll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btCheckAll.setText("Check All");
		btCheckAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				checkAll();
			}
		});
		
		Button btCheckDoc = new Button(cmpButtons, SWT.PUSH);
		btCheckDoc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btCheckDoc.setText("Check Document");
		btCheckDoc.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				checkCurrentDocument();
			}
		});
		
		Button btSession = new Button(cmpButtons, SWT.PUSH);
		btSession.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btSession.setText("Session...");
		btSession.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editSessionSettings();
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
		cbTypes.add("Missing and extra segments"); // All missing and extra segments issues
		cbTypes.add("Empty segments"); // All empty segment issues
		cbTypes.add("Target same as source"); // TARGET_SAME_AS_SOURCE
		cbTypes.add("White spaces differences"); // All whitespace-related issues
		cbTypes.add("Inline codes differences"); // CODE_DIFFERENCE
		cbTypes.add("Unexpected patterns"); // UNEXPECTED_PATTERN
		cbTypes.add("Suspect patterns"); // SUSPECT_PATTERN
		cbTypes.add("Target length"); // TARGET_LENGTH
		cbTypes.add("Allowed characters"); // ALLOWED_CHARACTERS
		cbTypes.add("LanguageTool checker warnings"); // LANGUAGETOOL_ERROR
		cbTypes.setVisibleItemCount(cbTypes.getItemCount());
		cbTypes.setLayoutData(new GridData());
		cbTypes.select(issueType);
		cbTypes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				refreshTableDisplay();
			};
		});
		
		cbDisplay = new Combo(cmpTmp, SWT.DROP_DOWN | SWT.READ_ONLY);
		cbDisplay.add("Enabled and disabled issues");
		cbDisplay.add("Only enabled issues");
		cbDisplay.add("Only disabled issues");
		cbDisplay.setLayoutData(new GridData()); //GridData.FILL_HORIZONTAL));
		cbDisplay.select(displayType);
		cbDisplay.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				refreshTableDisplay();
			};
		});
		
		btRefreshDisplay = new Button(cmpTmp, SWT.PUSH);
		btRefreshDisplay.setText("Refresh");
		UIUtil.ensureWidth(btRefreshDisplay, UIUtil.BUTTON_DEFAULT_WIDTH);
		btRefreshDisplay.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				refreshTableDisplay();
			}
		});

		tblIssues = new Table(cmpTmp, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK | SWT.V_SCROLL);
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
		    	int severityColWidth = 28;
				int part = (int)((rect.width-(checkColWidth+severityColWidth)) / 100);
				int remainder = (int)((rect.width-(checkColWidth+severityColWidth)) % 100);
				tblIssues.getColumn(0).setWidth(checkColWidth);
				tblIssues.getColumn(1).setWidth(severityColWidth);
				tblIssues.getColumn(2).setWidth(part*10);
				tblIssues.getColumn(3).setWidth(part*5);
				tblIssues.getColumn(4).setWidth(remainder+(part*85));
		    }
		});
		
		tblIssues.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if ( event.detail == SWT.CHECK ) {
					// Do not force the selection: tblIssues.setSelection((TableItem)event.item);
					Issue issue = (Issue)event.item.getData();
					issue.enabled = !issue.enabled;
					session.setModified(true);
				}
				updateCurrentIssue();
            }
		});

		tblIssues.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if ( e.character == ' ' ) {
					TableItem si = tblIssues.getItem(tblIssues.getSelectionIndex());
					for ( TableItem ti : tblIssues.getSelection() ) {
						if ( ti == si ) continue; // Skip focused item because it will get set by SelectionAdapter()
						Issue issue = (Issue)ti.getData();
						issue.enabled = !issue.enabled;
						ti.setChecked(issue.enabled);
						session.setModified(true);
					}
				}
			}
		});

		// Sort listener for the table
		Listener sortListener = new Listener() {
			public void handleEvent(Event event) {
				// Determine new sort column and direction
				TableColumn sortCol = tblIssues.getSortColumn();
				TableColumn curCol = (TableColumn)event.widget;
				int dir = tblIssues.getSortDirection();
				if ( sortCol == curCol ) {
					// Same column as before? then reverse sort direction
					dir = (dir == SWT.UP ? SWT.DOWN : SWT.UP);
				}
				else { // Other column? the set the new column
					tblIssues.setSortColumn(curCol);
					dir = SWT.UP;
				}
				// Select the issue part to sort
				int type = IssueComparator.TYPE_ENABLED;
				if ( tblIssues.indexOf(curCol) == 1 ) type = IssueComparator.TYPE_SEVERITY;
				else if ( tblIssues.indexOf(curCol) == 2 ) type = IssueComparator.TYPE_TU;
				else if ( tblIssues.indexOf(curCol) == 3 ) type = IssueComparator.TYPE_SEG;
				else if ( tblIssues.indexOf(curCol) == 4 ) type = IssueComparator.TYPE_MESSAGE;
				// Perform the sort
				Collections.sort(session.getIssues(),
					new IssueComparator(type, dir==SWT.UP ? IssueComparator.DIR_ASC : IssueComparator.DIR_DESC));
				// Set direction
				tblIssues.setSortDirection(dir);
				refreshTableDisplay();
			}
		};
		
		issuesModel = new IssuesTableModel(shell.getDisplay());
		issuesModel.linkTable(tblIssues, sortListener);

		sashMain.setWeights(new int[]{30, 70});
		
		statusBar = new StatusBar(shell, SWT.NONE);
		updateMRU();
		
		// Set minimum and start sizes
		Point defaultSize = shell.getSize();
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = defaultSize;
		if ( startSize.x < 700 ) startSize.x = 700; 
		if ( startSize.y < 600 ) startSize.y = 600; 
		shell.setSize(startSize);
		
		// Maximize if requested
		if ( config.getBoolean(OPT_MAXIMIZED) ) { //$NON-NLS-1$
			shell.setMaximized(true);
		}
		else { // Or try to re-use the bounds of the previous session
			Rectangle ar = UIUtil.StringToRectangle(config.getProperty(OPT_BOUNDS));
			if ( ar != null ) {
				Rectangle dr = shell.getDisplay().getBounds();
				if ( dr.contains(ar.x+ar.width, ar.y+ar.height)
					&& dr.contains(ar.x, ar.y) ) {
					shell.setBounds(ar);
				}
			}
		}
		
		updateCaption();
		refreshTableDisplay();
	}

	private void resetTableDisplay () {
		try {
			issuesModel.setIssues(session.getIssues());
			tblIssues.setSortColumn(null); // reset the sort column
			displayType = cbDisplay.getSelectionIndex();
			issueType = cbTypes.getSelectionIndex();
			issuesModel.updateTable(0, displayType, issueType);
			updateCurrentIssue();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error resetting the table.\n"+e.getMessage(), null);
		}
		
	}

	private void refreshTableDisplay () {
		displayType = cbDisplay.getSelectionIndex();
		issueType = cbTypes.getSelectionIndex();
		issuesModel.updateTable(tblIssues.getSelectionIndex(), displayType, issueType);
		updateCurrentIssue();
	}
	
	private void editSessionSettings () {
		try {
			SessionSettingsDialog dlg = new SessionSettingsDialog(shell, help);
			dlg.setData(session);
			if ( !dlg.showDialog() ) return;
			
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error adding document.\n"+e.getMessage(), null);
		}
	}
	
	private boolean addDocumentFromUI (String path) {
		try {
			InputDocumentDialog dlg = new InputDocumentDialog(shell, "Add Document",
				session.getFilterConfigurationMapper());
			// Set default data
			dlg.setData(path, null, "UTF-8", session.getSourceLocale(), session.getTargetLocale());
			// Lock the locales if we have already documents in the session
			dlg.setLocalesEditable(session.getDocumentCount()==0);

			// Edit
			Object[] data = dlg.showDialog();
			if ( data == null ) return false;
			
			// Create the raw document to add to the session
			URI uri = (new File((String)data[0])).toURI();
			RawDocument rd = new RawDocument(uri, (String)data[2], (LocaleId)data[3], (LocaleId)data[4]);
			rd.setFilterConfigId((String)data[1]);
			session.addRawDocument(rd);
			return true;
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error adding document.\n"+e.getMessage(), null);
			return false;
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
		try {
			int n = tblIssues.getSelectionIndex();
			if ( n == -1 ) {
				edDocument.setText("");
				edMessage.setText("");
				edSource.setText("");
				edTarget.setText("");
			}
			else {
				Issue issue = (Issue)tblIssues.getItem(n).getData();
				edDocument.setText(issue.docId.getPath());
				edMessage.setText(issue.message);
				setTexts(issue);
			}
			statusBar.setCounter(n, tblIssues.getItemCount(), session.getIssues().size());
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error while updating table.\n"+e.getMessage(), null);
		}
	}
	
	private void setTexts (Issue issue) {
		edSource.setText(issue.oriSource);
		edTarget.setText(issue.oriTarget);
		if ( issue.srcEnd > 0 ) {
			StyleRange sr = new StyleRange();
			sr.background = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
			sr.start = issue.srcStart;
			sr.length = issue.srcEnd-issue.srcStart;
			edSource.setStyleRange(sr);
			edSource.setCaretOffset(issue.srcEnd);
			edSource.showSelection();
		}
		if ( issue.trgEnd > 0 ) {
			StyleRange sr = new StyleRange();
			sr.background = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
			sr.start = issue.trgStart;
			sr.length = issue.trgEnd-issue.trgStart;
			edTarget.setStyleRange(sr);
			edTarget.setCaretOffset(issue.trgEnd);
			edTarget.showSelection();
		}
	}

	private void startWaiting (String text) {
		if ( ++waitCount > 1 ) {
			shell.getDisplay().update();
			return;
		}
		if ( text != null ) statusBar.setInfo(text);
		//startLogWasRequested = p_bStartLog;
		//if ( startLogWasRequested ) log.beginProcess(null);
		shell.getDisplay().update();
	}

	private void stopWaiting () {
		waitCount--;
		if ( waitCount < 1 ) statusBar.clearInfo();
		shell.getDisplay().update();
		//if ( log.inProgress() ) log.endProcess(null); 
		//if ( startLogWasRequested && ( log.getErrorAndWarningCount() > 0 )) log.show();
		//startLogWasRequested = false;
	}

	private void generateReport () {
		try {
			startWaiting("Generating report...");
			String rootDir = (qcsPath==null ? null : Util.getDirectoryName(qcsPath));
			session.generateReport(rootDir);
			String finalPath = Util.fillRootDirectoryVariable(session.getParameters().getOutputPath(), rootDir);
			if ( session.getParameters().getAutoOpen() ) {
				Util.openURL((new File(finalPath)).getAbsolutePath());
			}
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error while generating report.\n"+e.getMessage(), null);
		}
		finally {
			stopWaiting();
		}
	}

	private void resetDisabledIssues () {
		MessageBox dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
		dlg.setText(shell.getText());
		dlg.setMessage("This command will re-enabled all issues that have been disabled.\nDo you want to proceed?");
		if  ( dlg.open() != SWT.YES ) return;
		// Proceed
		session.resetDisabledIssues();
		issuesModel.updateTable(0, displayType, issueType);
	}
	
	private void checkCurrentDocument () {
		try {
			int n = tblIssues.getSelectionIndex();
			if ( n < 0 ) return;
			Issue issue = (Issue)tblIssues.getItem(n).getData();
			startWaiting("Checking current document...");
			session.recheckDocument(issue.docId);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error while running the verification.\n"+e.getMessage(), null);
		}
		finally {
			resetTableDisplay();
			stopWaiting();
		}
	}
	
	private void checkAll () {
		try {
			startWaiting("Checking all documents...");
			// Recheck all using the signatures of the current issue lists
			session.recheckAll(null);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error while running the verification.\n"+e.getMessage(), null);
		}
		finally {
			resetTableDisplay();
			stopWaiting();
		}
	}

	private void editOptions () {
		try {
			ParametersEditor editor = new ParametersEditor();
			BaseContext context = new BaseContext();
			if ( help != null ) context.setObject("help", help);
			context.setObject("shell", shell);
			context.setBoolean("stepMode", false); // Not in a step
			String old = session.getParameters().toString();
			if ( editor.edit(session.getParameters(), false, context) ) {
				// Compare before and after to set or not the modified flag
				if ( !old.equals(session.getParameters().toString()) ) {
					session.setModified(true);
				}
			}
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
				mruList.add(path);
				updateMRU();
			}
			startWaiting("Saving session...");
			session.saveSession(qcsPath);
			updateCaption();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error while saving.\n"+e.getMessage(), null);
		}
		finally {
			stopWaiting();
		}
	}

	private void newSession () {
		try {
			if ( !saveSessionIfNeeded() ) return;
			session.reset();
			qcsPath = null;
			updateCaption();
			resetTableDisplay();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void loadSession (String path) {
		try {
			if ( !saveSessionIfNeeded() ) return;
			if ( path == null ) {
				String[] paths = Dialogs.browseFilenames(shell, "Open Session", false, null,
					String.format("Quality Check Sessions (*%s)\tAll Files (*.*)", QualityCheckSession.FILE_EXTENSION),
					String.format("*%s\t*.*", QualityCheckSession.FILE_EXTENSION));
				if ( paths == null ) return;
				path = paths[0];
			}
			
			// Check if the file exists
			if ( !(new File(path)).exists() ) {
				Dialogs.showError(shell, "The settings file cannot be found:\n"+path, null);
				mruList.remove(path);
				updateMRU();
				return;
			}
			
			startWaiting("Loading session and processing documents...");
			session.loadSession(path);
			qcsPath = path;
			updateCaption();
			mruList.add(path);
			updateMRU();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error while loading.\n"+e.getMessage(), null);
		}
		finally {
			resetTableDisplay();
			stopWaiting();
		}
	}

	/**
	 * Checks if the session needs to be saved, if so save them after prompting
	 * the user if needed.
	 * @return False if the user cancel, true if a decision is made. 
	 */
	private boolean saveSessionIfNeeded () {
		config.setProperty(CFG_SOURCELOCALE, session.getSourceLocale().toBCP47());
		config.setProperty(CFG_TARGETLOCALE, session.getTargetLocale().toBCP47());
		// Set the window placement
		config.setProperty(OPT_MAXIMIZED, shell.getMaximized());
		Rectangle r = shell.getBounds();
		config.setProperty(OPT_BOUNDS, String.format("%d,%d,%d,%d", r.x, r.y, r.width, r.height)); //$NON-NLS-1$
		// Set the MRU list
		mruList.copyToProperties(config);
		// Save to the user home directory as ".appname" file
		config.save(APPNAME, getClass().getPackage().getImplementationVersion());
		
		if ( session.isModified() ) {
			MessageBox dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
			dlg.setText(shell.getText());
			dlg.setMessage("The session has been modified. Do you want to save it?");
			switch ( dlg.open() ) {
			case SWT.CANCEL:
				return false;
			case SWT.YES:
				saveSessionAs(qcsPath);
			}
		}
		return true;
	}

	private void clearMRU () {
		try {
			mruList.clear();
			updateMRU();
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void updateMRU () {
		try {
			// miMRU is the MenuItem where to attached the sub-menu
			// Remove and dispose of the previous sub-menu
			Menu oldMenu = miMRU.getMenu();
			miMRU.setMenu(null);
			if ( oldMenu != null ) oldMenu.dispose();

			// Set the new one
			if ( mruList.getfirst() == null ) {
				// No items to set: it's disabled
				miMRU.setEnabled(false);
			}
			else { // One or more items
				// Create the menu
				Menu submenu = new Menu(shell, SWT.DROP_DOWN);
				int i = 0;
				String path;
				MenuItem menuItem;
				Iterator<String> iter = mruList.getIterator();
				while ( iter.hasNext() ) {
					menuItem = new MenuItem(submenu, SWT.PUSH);
					path = iter.next();
					menuItem.setText(String.format("&%d %s", ++i, path)); //$NON-NLS-1$
					menuItem.setData(path);
					menuItem.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent event) {
							loadSession((String)((MenuItem)event.getSource()).getData());
						}
					});
				}
				miMRU.setMenu(submenu);
				miMRU.setEnabled(true);
			}
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}

	@Override
	public QualityCheckSession getSession() {
		return session;
	}

}

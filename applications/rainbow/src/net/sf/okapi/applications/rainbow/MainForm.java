/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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

package net.sf.okapi.applications.rainbow;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Logger;

import net.sf.okapi.applications.rainbow.lib.EncodingItem;
import net.sf.okapi.applications.rainbow.lib.EncodingManager;
import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.lib.FormatManager;
import net.sf.okapi.applications.rainbow.lib.ILog;
import net.sf.okapi.applications.rainbow.lib.LanguageItem;
import net.sf.okapi.applications.rainbow.lib.LanguageManager;
import net.sf.okapi.applications.rainbow.lib.LogForm;
import net.sf.okapi.applications.rainbow.lib.PathBuilderPanel;
import net.sf.okapi.applications.rainbow.lib.ResourceManager;
import net.sf.okapi.applications.rainbow.lib.Utils;
import net.sf.okapi.applications.rainbow.plugins.PluginItem;
import net.sf.okapi.applications.rainbow.plugins.PluginsAccess;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersProvider;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.InputDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class MainForm implements IParametersProvider {
	
	private Shell            shell;
	private ILog             log;
	private LogHandler       logHandler;
	private String           rootFolder;
	private String           sharedFolder;
	private Project          prj;
	private Table            inputTable;
	private InputTableModel  inputTableMod;
	private StatusBar        statusBar;
	private Text             edInputRoot;
	private Button           btGetRoot;
	private Button           chkUseOutputRoot;
	private Text             edOutputRoot;
	private Text             edSourceLang;
	private List             lbSourceLang;
	private boolean          inSourceLangSelection;
	private Text             edSourceEnc;
	private List             lbSourceEnc;
	private boolean          inSourceEncSelection;
	private Text             edTargetLang;
	private List             lbTargetLang;
	private boolean          inTargetLangSelection;
	private Text             edTargetEnc;
	private List             lbTargetEnc;
	private boolean          inTargetEncSelection;
	private Text             edParamsFolder;
	private TabItem          tiInput;
	private TabItem          tiOptions;
	private PathBuilderPanel pnlPathBuilder;
	private int              waitCount;
	private boolean          startLogWasRequested;
	private LanguageManager  lm;
	private ResourceManager  rm;
	private FormatManager    fm;
	private FilterAccess     fa;
	private EncodingManager  em;
	private PluginsAccess    plugins;
	private UtilityDriver    ud;
	private MenuItem         miInput;
	private MenuItem         miUtilities;
	private MenuItem         miSave;
	private MenuItem         miEditInputProperties;
	private MenuItem         cmiEditInputProperties;
	private MenuItem         miOpenInputDocument;
	private MenuItem         cmiOpenInputDocument;
	private MenuItem         miRemoveInputDocuments;
	private MenuItem         cmiRemoveInputDocuments;
	
	public MainForm (Shell p_Shell) {
		try {
			shell = p_Shell;
			setDirectories();
			loadResources();
			createContent();
			createProject();
		}
		catch ( Exception E ) {
			Dialogs.showError(shell, E.getLocalizedMessage(), null);			
		}
	}

	private void createContent ()
		throws Exception
	{
		GridLayout layTmp = new GridLayout(3, false);
		shell.setLayout(layTmp);
		shell.setImage(rm.getImage("Rainbow"));

		log = new LogForm(shell);
		log.setTitle("Rainbow Log");
		logHandler = new LogHandler(log);
	    Logger.getLogger("net.sf.okapi.logging").addHandler(logHandler);

		fa = new FilterAccess();
		fa.loadList(sharedFolder + File.separator + "filters.xml");

		// Menus
	    Menu menuBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuBar);

		// File menu
		MenuItem topItem = new MenuItem(menuBar, SWT.CASCADE);
		topItem.setText("&File");
		Menu dropMenu = new Menu(shell, SWT.DROP_DOWN);
		topItem.setMenu(dropMenu);
		
		MenuItem menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "file.new");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				createProject();
            }
		});

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "file.open");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				openProject(null);
            }
		});

		new MenuItem(dropMenu, SWT.SEPARATOR);

		miSave = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(miSave, "file.save");
		miSave.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
            	saveProject(prj.path);
            }
		});
		
		miSave = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(miSave, "file.saveas");
		miSave.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
            	saveProject(null);
            }
		});
		
		new MenuItem(dropMenu, SWT.SEPARATOR);

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "file.exit");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//TODO: check at the close event level
				if ( canContinue() ) shell.close();
            }
		});

		// View menu
		topItem = new MenuItem(menuBar, SWT.CASCADE);
		topItem.setText("&View");
		dropMenu = new Menu(shell, SWT.DROP_DOWN);
		topItem.setMenu(dropMenu);

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "view.log");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if ( log.isVisible() ) log.hide();
				else log.show();
            }
		});
		
		// Input menu
		miInput = new MenuItem(menuBar, SWT.CASCADE);
		miInput.setText("&Input");
		dropMenu = new Menu(shell, SWT.DROP_DOWN);
		miInput.setMenu(dropMenu);

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "input.addDocuments");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				addDocumentsFromList(null);
            }
		});
		
		miRemoveInputDocuments = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(miRemoveInputDocuments, "input.removeDocuments");
		miRemoveInputDocuments.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				removeDocuments(-1);
            }
		});
		
		new MenuItem(dropMenu, SWT.SEPARATOR);
		
		miOpenInputDocument = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(miOpenInputDocument, "input.openDocument");
		miOpenInputDocument.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				openDocument(-1);
            }
		});
		
		miEditInputProperties = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(miEditInputProperties, "input.editProperties");
		miEditInputProperties.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editInputProperties(-1);
			}
		});

		// Utilities menu
		miUtilities = new MenuItem(menuBar, SWT.CASCADE);
		miUtilities.setText("&Utilities");
		buildUtilitiesMenu();

		// Root panel
		Label label = new Label(shell, SWT.NONE);
		label.setText("Input Root:");
		
		edInputRoot = new Text(shell, SWT.SINGLE | SWT.BORDER);
		edInputRoot.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edInputRoot.setEditable(false);
		
		btGetRoot = new Button(shell, SWT.PUSH);
		btGetRoot.setText(" ... ");
		btGetRoot.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				changeRoot();
			}
		});
		
		// Tab control
		final TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 3;
		tabFolder.setLayoutData(gdTmp);
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				miInput.setEnabled(tabFolder.getSelectionIndex()==0);
            }
		});

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
		inputTable.addControlListener(new ControlAdapter() {
		    public void controlResized(ControlEvent e) {
				Rectangle rect = inputTable.getClientArea();
				//TODO: Check behavior when manual resize a column width out of client area
				int nPart = (int)(rect.width / 100);
				inputTable.getColumn(0).setWidth(70*nPart);
				inputTable.getColumn(1).setWidth(rect.width-inputTable.getColumn(0).getWidth());
		    }
		});
		inputTable.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.character == ' ') {
					editInputProperties(-1);
				}
			}
		});

		inputTableMod = new InputTableModel();
		inputTableMod.linkTable(inputTable);
		
		// Drop target for the table
		DropTarget dropTarget = new DropTarget(inputTable, DND.DROP_DEFAULT | DND.DROP_COPY | DND.DROP_MOVE);
		dropTarget.setTransfer(new FileTransfer[]{FileTransfer.getInstance()}); 
		dropTarget.addDropListener(new DropTargetAdapter() {
			public void drop (DropTargetEvent e) {
				FileTransfer FT = FileTransfer.getInstance();
				if ( FT.isSupportedType(e.currentDataType) ) {
					String[] paths = (String[])e.data;
					if ( paths != null ) {
						if ( paths.length == 1 ) {
							//if ( Utils.getFilename(aPaths[0], true).toLowerCase().equals("manifest.xml") ) {
							//	m_C.importPackage(aPaths[0]);
							//	return;
							//}
						}
						addDocumentsFromList(paths);
					}
				}
			}
		});
		
		// Context menu for the input list
		Menu inputTableMenu = new Menu(shell, SWT.POP_UP);
		
		menuItem = new MenuItem(inputTableMenu, SWT.PUSH);
		rm.setCommand(menuItem, "input.addDocuments");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				addDocumentsFromList(null);
            }
		});
		
		cmiRemoveInputDocuments = new MenuItem(inputTableMenu, SWT.PUSH);
		rm.setCommand(cmiRemoveInputDocuments, "input.removeDocuments");
		cmiRemoveInputDocuments.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				removeDocuments(-1);
            }
		});
		
		new MenuItem(inputTableMenu, SWT.SEPARATOR);
		
		cmiOpenInputDocument = new MenuItem(inputTableMenu, SWT.PUSH);
		rm.setCommand(cmiOpenInputDocument, "input.openDocument");
		cmiOpenInputDocument.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				openDocument(-1);
            }
		});
		
		cmiEditInputProperties = new MenuItem(inputTableMenu, SWT.PUSH);
		rm.setCommand(cmiEditInputProperties, "input.editProperties");
		cmiEditInputProperties.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editInputProperties(-1);
			}
		});

		inputTable.setMenu(inputTableMenu);

		// Options tab
		comp = new Composite(tabFolder, SWT.NONE);
		comp.setLayout(new GridLayout());
		tiOptions = new TabItem(tabFolder, SWT.NONE);
		tiOptions.setText("Languages and Encodings");
		tiOptions.setControl(comp);
		
		Group group = new Group(comp, SWT.NONE);
		group.setText("Source");
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setLayout(new GridLayout(4, false));
		
		label = new Label(group, SWT.NONE);
		label.setText("Language:");
		
		edSourceLang = new Text(group, SWT.BORDER);
		edSourceLang.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edSourceLang.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateSourceLanguageSelection();
			}
		});

		label = new Label(group, SWT.NONE);
		label.setText("Encoding:");
		
		edSourceEnc = new Text(group, SWT.BORDER);
		edSourceEnc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edSourceEnc.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateSourceEncodingSelection();
			}
		});
		
		lbSourceLang = new List(group, SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		gdTmp.heightHint = 30;
		lbSourceLang.setLayoutData(gdTmp);
		lbSourceLang.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int n = lbSourceLang.getSelectionIndex();
				if ( n > -1 ) {
					inSourceLangSelection = true;
					edSourceLang.setText(lm.getItem(n).code);
					inSourceLangSelection = false;
				}
			};
		});

		lbSourceEnc = new List(group, SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		gdTmp.heightHint = 30;
		lbSourceEnc.setLayoutData(gdTmp);
		lbSourceEnc.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int n = lbSourceEnc.getSelectionIndex();
				if ( n > -1 ) {
					inSourceEncSelection = true;
					edSourceEnc.setText(em.getItem(n).ianaName);
					inSourceEncSelection = false;
				}
			};
		});

		group = new Group(comp, SWT.NONE);
		group.setText("Target");
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setLayout(new GridLayout(4, false));
		
		label = new Label(group, SWT.NONE);
		label.setText("Language:");
		
		edTargetLang = new Text(group, SWT.BORDER);
		edTargetLang.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edTargetLang.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateTargetLanguageSelection();
			}
		});

		label = new Label(group, SWT.NONE);
		label.setText("Encoding:");
		
		edTargetEnc = new Text(group, SWT.BORDER);
		edTargetEnc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edTargetEnc.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateTargetEncodingSelection();
			}
		});

		lbTargetLang = new List(group, SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		gdTmp.heightHint = 30;
		lbTargetLang.setLayoutData(gdTmp);
		lbTargetLang.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int n = lbTargetLang.getSelectionIndex();
				if ( n > -1 ) {
					inTargetLangSelection = true;
					edTargetLang.setText(lm.getItem(n).code);
					inTargetLangSelection = false;
				}
			};
		});

		lbTargetEnc = new List(group, SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		gdTmp.heightHint = 30;
		lbTargetEnc.setLayoutData(gdTmp);
		lbTargetEnc.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int n = lbTargetEnc.getSelectionIndex();
				if ( n > -1 ) {
					inTargetEncSelection = true;
					edTargetEnc.setText(em.getItem(n).ianaName);
					inTargetEncSelection = false;
				}
			};
		});
		
		LanguageItem li;
		for ( int i=0; i<lm.getCount(); i++ ) {
			li = lm.getItem(i);
			lbSourceLang.add(li.name + "  -  " + li.code);
			lbTargetLang.add(li.name + "  -  " + li.code);
		}
		
		EncodingItem ei;
		for ( int i=0; i<em.getCount(); i++ ) {
			ei = em.getItem(i);
			lbSourceEnc.add(ei.name + "  -  " + ei.ianaName);
			lbTargetEnc.add(ei.name + "  -  " + ei.ianaName);
		}
		
		// Other settings tab
		comp = new Composite(tabFolder, SWT.NONE);
		comp.setLayout(new GridLayout());
		tiOptions = new TabItem(tabFolder, SWT.NONE);
		tiOptions.setText("Other Settings");
		tiOptions.setControl(comp);
		
		group = new Group(comp, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setText("Output");
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		chkUseOutputRoot = new Button(group, SWT.CHECK);
		chkUseOutputRoot.setText("Use this root:");
		chkUseOutputRoot.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				edOutputRoot.setEnabled(chkUseOutputRoot.getSelection());
				updateOutputRoot();
			};
		});
		
		edOutputRoot = new Text(group, SWT.SINGLE | SWT.BORDER);
		edOutputRoot.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edOutputRoot.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateOutputRoot();
			}
		});
		
		pnlPathBuilder = new PathBuilderPanel(group, SWT.NONE);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		pnlPathBuilder.setLayoutData(gdTmp);

		group = new Group(comp, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setText("Filters Parameters");
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		label = new Label(group, SWT.NONE);
		label.setText("Folder:");
		
		edParamsFolder = new Text(group, SWT.BORDER);
		edParamsFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Status bar
		statusBar = new StatusBar(shell, SWT.NONE);
		
		// Set the minimal size to the packed size
		// And then reset the original start size
		Point origSize = shell.getSize();
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		shell.setSize(origSize);
	}
	
	private void buildUtilitiesMenu () {
		// Remove an existing menu
		Menu menu = miUtilities.getMenu();
		if ( menu != null ) menu.dispose();
		// Create new one
		Menu dropMenu = new Menu(shell, SWT.DROP_DOWN);
		miUtilities.setMenu(dropMenu);
		
		// Add the plug-in utilities
		Iterator<String> iter = plugins.getIterator();
		while ( iter.hasNext() ) {
			PluginItem item = plugins.getItem(iter.next());
			MenuItem menuItem = new MenuItem(dropMenu, SWT.PUSH);
			menuItem.setText(item.name+"...");
			menuItem.setData(item.id);
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					launchUtility(event);
				}
			});
		}
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
			if ( rm != null ) rm.dispose();
		}
	}

	private void launchUtility (SelectionEvent event) {
		try {
			// Save any pending data
			saveSurfaceData();
			// Create the utility driver if needed
			if ( ud == null ) {
				ud = new UtilityDriver(log, fa, plugins);
			}
			// Get the utility to run and instantiate it
			String id = (String)((MenuItem)event.getSource()).getData();
			if ( id == null ) return;
			
			ud.setData(prj, id);
			// Run it
			startWaiting("Processing files...", true);
			ud.execute(shell);
		}
		catch ( Exception E ) {
			Dialogs.showError(shell, E.getLocalizedMessage(), null);
		}
		finally {
			stopWaiting();
		}
	}

	private void updateSourceLanguageSelection () {
		if ( inSourceLangSelection ) return;
		int n = lm.getIndexFromCode(edSourceLang.getText());
		if ( n > -1 ) {
			lbSourceLang.setSelection(n);
			lbSourceLang.showSelection();
		}
	}

	private void updateTargetLanguageSelection () {
		if ( inTargetLangSelection ) return;
		int n = lm.getIndexFromCode(edTargetLang.getText());
		if ( n > -1 ) {
			lbTargetLang.setSelection(n);
			lbTargetLang.showSelection();
		}
	}

	private void updateSourceEncodingSelection () {
		if ( inSourceEncSelection ) return;
		int n = em.getIndexFromIANAName(edSourceEnc.getText());
		if ( n > -1 ) {
			lbSourceEnc.setSelection(n);
			lbSourceEnc.showSelection();
		}
	}

	private void updateTargetEncodingSelection () {
		if ( inTargetEncSelection ) return;
		int n = em.getIndexFromIANAName(edTargetEnc.getText());
		if ( n > -1 ) {
			lbTargetEnc.setSelection(n);
			lbTargetEnc.showSelection();
		}
	}

	private void updateOutputRoot () {
		if ( chkUseOutputRoot.getSelection() )
			pnlPathBuilder.setTargetRoot(edOutputRoot.getText());
		else
			pnlPathBuilder.setTargetRoot(null);
		pnlPathBuilder.updateSample();
	}
	
	private void setDirectories () {
    	// Get the location of the main class source
    	File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
    	rootFolder = file.getAbsolutePath();
    	// Remove the JAR file if running an installed version
    	if ( rootFolder.endsWith(".jar") ) rootFolder = Util.getDirectoryName(rootFolder);
    	// Remove the application folder in all cases
    	rootFolder = Util.getDirectoryName(rootFolder);
		sharedFolder = Utils.getOkapiSharedFolder(rootFolder);
	}
	
	private void startWaiting (String text,
		boolean p_bStartLog)
	{
		if ( ++waitCount > 1 ) {
			shell.getDisplay().update();
			return;
		}
		if ( text != null ) statusBar.setInfo(text);
		startLogWasRequested = p_bStartLog;
		if ( startLogWasRequested ) log.beginProcess(null);
		shell.getDisplay().update();
	}

	private void stopWaiting () {
		waitCount--;
		if ( waitCount < 1 ) statusBar.clearInfo();
		shell.getDisplay().update();
		if ( log.inProgress() ) log.endProcess(null); 
		if ( startLogWasRequested && ( log.getErrorAndWarningCount() > 0 )) log.show();
		startLogWasRequested = false;
	}

	private void updateCommands () {
		boolean enabled = (inputTable.getItemCount()>0);
		miEditInputProperties.setEnabled(enabled);
		cmiEditInputProperties.setEnabled(enabled);
		miOpenInputDocument.setEnabled(enabled);
		cmiOpenInputDocument.setEnabled(enabled);
		miRemoveInputDocuments.setEnabled(enabled);
		cmiRemoveInputDocuments.setEnabled(enabled);
		miUtilities.setEnabled(enabled);
	}
	
	private void loadResources ()
		throws Exception 
	{
		rm = new ResourceManager(MainForm.class, shell.getDisplay());
		rm.addImage("Rainbow");
		rm.loadCommands("commands.xml"); //TODO: deal with localization
		fm = new FormatManager();
		lm = new LanguageManager();
		lm.loadList(sharedFolder + File.separator + "languages.xml");
		em = new EncodingManager();
		em.loadList(sharedFolder + File.separator + "encodings.xml");
		plugins = new PluginsAccess();
		//TODO: Choose a better location 
		plugins.addAllPackages(sharedFolder);
	}
	
	private void changeRoot () {
		try {
			InputDialog dlg = new InputDialog(shell, "Source Root",
				"New root folder:", prj.getInputRoot(), null);
			String newRoot = dlg.showDialog();
			if ( newRoot == null ) return;
			if ( newRoot.length() < 2 ) newRoot = System.getProperty("user.home");
			//TODO: additional check, dir exists, no trailing separator, etc.
			prj.setInputRoot(newRoot);
			resetDisplay();
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
	}

	/**
	 * Gets the table index if the input currently focused. If none is
	 * focused, tries to selected the first item. 
	 * @return The index of the focused item, or -1.
	 */
	private int getFocusedInputIndex () {
		int index = inputTable.getSelectionIndex();
		if ( index == -1 ) {
			if ( inputTable.getItemCount() > 0 ) {
				inputTable.select(0);
				return inputTable.getSelectionIndex();
			}
			else return -1;
		}
		return inputTable.getSelectionIndex();
	}
	
	private boolean canContinue () {
		try {
			saveSurfaceData();
			if ( !prj.isModified ) return true;
			else {
				// Ask confirmation
				MessageBox dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
				dlg.setMessage("Do you want to save the project?");
				dlg.setText("Rainbow");
				switch  ( dlg.open() ) {
				case SWT.NO:
					return true;
				case SWT.CANCEL:
					return false;
				}
				// Else save the project
				saveProject(prj.path);
			}
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
			return false;
		}
		return true;
	}
	
	private void saveProject (String path) {
		try {
			if ( path == null ) {
				path = Dialogs.browseFilenamesForSave(shell, "Save Project", null, null, null);
				if ( path == null ) return;
			}
			saveSurfaceData();
			prj.save(path);
			updateTitle();
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
	}
	
	private void updateTitle () {
		shell.setText(((prj.path == null)
			? Res.getString("UNTITLED")
			: Util.getFilename(prj.path, true))
			+ " - Rainbow [v6.ALPHA]");
	}

	private void resetDisplay () {
		updateTitle();
		inputTableMod.setProject(prj);
		setSurfaceData();
		updateCommands();
		inputTable.setFocus();
	}
	
	private void createProject () {
		prj = new Project(lm);
		resetDisplay();
	}
	
	private void openProject (String path) {
		try {
			if ( !canContinue() ) return;
			if ( path == null ) {
				String[] paths = Dialogs.browseFilenames(shell, "Open Project", false, null, null, null);
				if ( paths == null ) return;
				path = paths[0];
			}
			prj = new Project(lm);
			prj.load(path);
			resetDisplay();
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
	}

	private void setSurfaceData () {
		inputTableMod.updateTable(null);
		
		String sampleInput = prj.getInputRoot() + File.separator
			+ "mySubFolder" + File.separator + "myFile.ext";
		
		pnlPathBuilder.setData(prj.pathBuilder, prj.getInputRoot(), sampleInput,
			prj.getOutputRoot(), "fr");

		edInputRoot.setText(prj.getInputRoot());
		chkUseOutputRoot.setSelection(prj.getUseOutputRoot());
		edOutputRoot.setText(prj.getOutputRoot());

		edSourceLang.setText(prj.getSourceLanguage());
		edSourceEnc.setText(prj.getSourceEncoding());
		edTargetLang.setText(prj.getTargetLanguage());
		edTargetEnc.setText(prj.getTargetEncoding());
		edParamsFolder.setText(prj.getParametersFolder());

		edOutputRoot.setEnabled(chkUseOutputRoot.getSelection());
		updateOutputRoot();
	}
	
	/**
	 * Saves the UI-accessible properties of the project into the project object.
	 */
	private void saveSurfaceData () {
		//TODO: Fix this, tmp is already equal because of example display
		String tmp = prj.pathBuilder.toString();
		pnlPathBuilder.saveData(prj.pathBuilder);
		if ( !tmp.equals(prj.pathBuilder.toString()))
			prj.isModified = true;
	
		prj.setUseOutputRoot(chkUseOutputRoot.getSelection());
		prj.setOutputRoot(edOutputRoot.getText());
		prj.setSourceLanguage(edSourceLang.getText());
		prj.setSourceEncoding(edSourceEnc.getText());
		prj.setTargetLanguage(edTargetLang.getText());
		prj.setTargetEncoding(edTargetEnc.getText());
		prj.setParametersFolder(edParamsFolder.getText());
	}

	private void addDocumentsFromList (String[] paths) {
		try {
			// Get a list of paths if needed
			if ( paths == null ) {
				paths = Dialogs.browseFilenames(shell, "Add Documents",
					true, prj.getInputRoot(), null, null);
			}
			if ( paths == null ) return;
			// Add all the selected files and folders
			startWaiting("Adding input documents...", false);
			doAddDocuments(paths, null);
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
		finally {
			inputTableMod.updateTable(null);
			updateCommands();
			stopWaiting();
		}
	}

	private int doAddDocuments (String[] paths,
		String dir)
		throws Exception
	{
		int n = 0;
		for ( String path : paths ) {
			if ( dir != null ) {
				path = dir + File.separator + path;
			}
			File F = new File(path);
			if ( F.isDirectory() ) {
				n += doAddDocuments(F.list(), path);
			}
			else {
				String[] aRes = fm.guessFormat(path);
				switch ( prj.addDocument(path, aRes[0], null, aRes[1]) ) {
				case 0: // OK
					n++;
					break;
				case 1: // Bad root
					Dialogs.showError(shell, "The file '"+path+"' does not have the same root as the project's input root.", null);
					return n;
				}
			}
		}
		return n;
	}
	
	private void editInputProperties (int index) {
		try {
			saveSurfaceData();
			int n = index;
			if ( n < 0 ) {
				if ( (n = getFocusedInputIndex()) < 0 ) return;
			}
			Input inp = prj.getItemFromRelativePath(
				inputTable.getItem(n).getText(0));

			// Call the dialog
			InputPropertiesForm dlg = new InputPropertiesForm(shell, this);
			dlg.setData(inp.filterSettings, inp.sourceEncoding,
				inp.targetEncoding, fa);
			String[] aRes = dlg.showDialog();
			if ( aRes == null ) return;

			// Update the file(s) data
			startWaiting("Updating project...", false);
			if ( index < 0 ) {
				int[] indices = inputTable.getSelectionIndices();
				for ( int i=0; i<indices.length; i++ ) {
					inp = prj.getItemFromRelativePath(
						inputTable.getItem(indices[i]).getText(0));
					inp.filterSettings = aRes[0];
					inp.sourceEncoding = aRes[1];
					inp.targetEncoding = aRes[2];
				}
			}
			else {
				inp = prj.getItemFromRelativePath(
					inputTable.getItem(index).getText(0));
				inp.filterSettings = aRes[0];
				inp.sourceEncoding = aRes[1];
				inp.targetEncoding = aRes[2];
			}
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
		finally {
			inputTableMod.updateTable(inputTable.getSelectionIndices());
			stopWaiting();
		}
	}

	public IParameters createParameters (String location)
		throws Exception
	{
		String[] aRes = FilterAccess.splitFilterSettingsType1(prj.getParametersFolder(),
			location);
		fa.loadFilter(aRes[1], null);
		return fa.inputFilter.getParameters();
	}

	public IParameters load (String location)
		throws Exception
	{
		String[] aRes = FilterAccess.splitFilterSettingsType1(prj.getParametersFolder(),
			location);
		fa.loadFilter(aRes[1], aRes[3]);
		return fa.inputFilter.getParameters();
	}

	public void save (String location,
		IParameters paramsObject)
		throws Exception
	{
		String[] aRes = FilterAccess.splitFilterSettingsType1(prj.getParametersFolder(),
			location);
		paramsObject.save(aRes[3], null);
	}

	public String[] splitLocation (String location) {
		return FilterAccess.splitFilterSettingsType1(prj.getParametersFolder(), location);
	}

	public String[] getParametersList () {
		return FilterAccess.getParametersList(prj.getParametersFolder());
	}

	private void openDocument (int index) {
		try {
			if ( index < 0 ) {
				if ( (index = getFocusedInputIndex()) < 0 ) return;
			}
			Input inp = prj.getItemFromRelativePath(
				inputTable.getItem(index).getText(0));
			Program.launch(prj.getInputRoot() + File.separator + inp.relativePath); 
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
	}
	
	private void removeDocuments (int index) {
		try {
			int n = index;
			if ( n < 0 ) {
				if ( (n = getFocusedInputIndex()) < 0 ) return;
			}
			
			// Ask confirmation
			MessageBox dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			dlg.setMessage("You are going to remove the selected document(s) from the list.\nDo you want to proceed?");
			dlg.setText("Rainbow");
			if ( dlg.open() != SWT.YES ) return;

			Input inp;
			startWaiting("Updating project...", false);
			if ( index < 0 ) {
				int[] indices = inputTable.getSelectionIndices();
				for ( int i=0; i<indices.length; i++ ) {
					inp = prj.getItemFromRelativePath(
						inputTable.getItem(indices[i]).getText(0));
					prj.inputList.remove(inp);
				}
			}
			else {
				inp = prj.getItemFromRelativePath(
					inputTable.getItem(index).getText(0));
				prj.inputList.remove(inp);
			}
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
		finally {
			inputTableMod.updateTable(null);
			updateCommands();
			stopWaiting();
		}
	}

}

/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.applications.olifant;

import java.io.File;
import java.net.URI;
import java.util.HashMap;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.ResourceManager;
import net.sf.okapi.lib.tmdb.DbUtil;
import net.sf.okapi.lib.tmdb.IRepository;
import net.sf.okapi.lib.tmdb.ITm;
import net.sf.okapi.lib.tmdb.memory.Repository;
import net.sf.okapi.lib.ui.editor.InputDocumentDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class MainForm {
	
	public static final String APPNAME = "Olifant"; //$NON-NLS-1$
	
	private Shell shell;
	private ResourceManager rm;
	private IFilterConfigurationMapper fcMapper;
	private IRepository repo;
	private SashForm topSash;
	private CTabFolder tabs;
	private List tmList;
	private TmPanel currentTmTab;

	public MainForm (Shell shell,
		String[] args)
	{
		try {
			this.shell = shell;
			shell.setLayout(new GridLayout());
			loadResources();
			createContent();
			repo = new Repository();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}

	private void createContent ()
		throws Exception
	{
		shell.setLayout(new GridLayout(1, false));
		
		createMenu();

		// Drag and drop handling for adding files
		DropTarget dropTarget = new DropTarget(shell, DND.DROP_DEFAULT | DND.DROP_COPY | DND.DROP_MOVE);
		dropTarget.setTransfer(new FileTransfer[]{FileTransfer.getInstance()}); 
		dropTarget.addDropListener(new DropTargetAdapter() {
			public void drop (DropTargetEvent e) {
				FileTransfer ft = FileTransfer.getInstance();
				if ( ft.isSupportedType(e.currentDataType) ) {
					String[] paths = (String[])e.data;
					if ( paths != null ) {
						boolean acceptAll = false;
						for ( String path : paths ) {
							Boolean res;
							if ((res = addDocumentFromUI(path, paths.length>1, acceptAll)) == null ) {
								return; // Stop now
							}
							// Else use the result to set the next value of the accept-all button
							acceptAll = res;
						}
					}
				}
			}
		});

		// Create the two main parts of the UI
		topSash = new SashForm(shell, SWT.HORIZONTAL);
		topSash.setLayout(new GridLayout(1, false));
		topSash.setLayoutData(new GridData(GridData.FILL_BOTH));
		topSash.setSashWidth(4);
		
		tabs = new CTabFolder(topSash, SWT.TOP | SWT.CLOSE);
		tabs.setBorderVisible(true);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		tabs.setLayoutData(gdTmp);
		
		tabs.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
				currentTmTab = (TmPanel)tabs.getSelection().getControl();
			}
		});
		
		tmList = new List(topSash, SWT.BORDER);

		topSash.setWeights(new int[]{4, 1});
		
		// Set the minimal size to the packed size
		// And then set the start size
		Point startSize = shell.getSize();
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		shell.setSize(startSize);
	}

	private void addTmTab (ITm tm) {
		TmPanel tp = new TmPanel(tabs, SWT.NONE, tm);
		CTabItem ti = new CTabItem(tabs, SWT.NONE);
		ti.setText(tm.getName());
		ti.setControl(tp);
		tp.fillTable();
		tmList.add(tm.getName());
		tmList.setData(tm.getUUID(), tm);
	}
	
	private void createMenu () {
		// Menus
	    Menu menuBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuBar);

		// File menu
		MenuItem topItem = new MenuItem(menuBar, SWT.CASCADE);
		topItem.setText(rm.getCommandLabel("file")); //$NON-NLS-1$
		Menu dropMenu = new Menu(shell, SWT.DROP_DOWN);
		topItem.setMenu(dropMenu);

		MenuItem menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "file.exit"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
            }
		});
		
		// View menu
		topItem = new MenuItem(menuBar, SWT.CASCADE);
		topItem.setText(rm.getCommandLabel("view")); //$NON-NLS-1$
		dropMenu = new Menu(shell, SWT.DROP_DOWN);
		topItem.setMenu(dropMenu);

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "view.showhidetmlist"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if ( topSash.getSashWidth() > 0 ) {
					topSash.setWeights(new int[]{1, 0});
					topSash.setSashWidth(0);
				}
				else {
					topSash.setWeights(new int[]{4, 1});
					topSash.setSashWidth(4);
				}
            }
		});
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "view.showhideextrafield"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if ( currentTmTab != null ) {
					currentTmTab.toggleExtra();
				}
            }
		});
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "view.showhidefieldlist"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if ( currentTmTab != null ) {
					currentTmTab.getEditorPanel().getExtraFieldPanel().toggleFieldList();
				}
			}
		});
	
	}

	private void loadResources ()
		throws Exception 
	{
		rm = new ResourceManager(MainForm.class, shell.getDisplay());
//		rm.addImage("Olifant"); //$NON-NLS-1$
	
		rm.loadCommands("net.sf.okapi.applications.olifant.Commands"); //$NON-NLS-1$

		// Create the filter configuration mapping
		fcMapper = new FilterConfigurationMapper();
		// Get pre-defined configurations
		fcMapper.addConfigurations("net.sf.okapi.filters.tmx.TmxFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.xliff.XLIFFFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.po.POFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.ttx.TTXFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.rtf.RTFFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.ts.TsFilter");
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

	/**
	 * Adds a document using the UI dialog.
	 * @param path the path of the document to add.
	 * @param batchMode true if the check box to accept all next documents should be displayed.
	 * @param acceptAll value of the check box to accept all.
	 * @return Null if the user cancel the operation, otherwise: true if the accept-all button was checked,
	 * or false if we are not in batch mode or if the accept-all button was not checked.
	 */
	private Boolean addDocumentFromUI (String path,
		boolean batchMode,
		boolean acceptAll)
	{
		try {
			InputDocumentDialog dlg = new InputDocumentDialog(shell, "Input Document",
				fcMapper, batchMode);
			// Lock the locales if we have already documents in the session
			boolean canChangeLocales = true; //session.getDocumentCount()==0;
			dlg.setLocalesEditable(canChangeLocales);
			// Set default data
			dlg.setData(path, null, "UTF-8", LocaleId.ENGLISH, LocaleId.FRENCH);
			
			if ( batchMode && ( path != null )) {
				dlg.setAcceptAll(acceptAll);
			}

			// Edit
			Object[] data = dlg.showDialog();
			if ( data == null ) return null;
			
			// Create the raw document to add to the session
			URI uri = (new File((String)data[0])).toURI();
			RawDocument rd = new RawDocument(uri, (String)data[2], (LocaleId)data[3], (LocaleId)data[4]);
			rd.setFilterConfigId((String)data[1]);
			addRawDocument(rd);
			
			if ( canChangeLocales ) { // In case the locales have changed
//				resetTextFieldOrientation();
			}
			
			// If dialog return OK, we return value of accept all
			return (Boolean)data[5];
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error adding document.\n"+e.getMessage(), null);
			return null;
		}
	}

	private void addRawDocument (RawDocument rd) {
		IFilter filter = fcMapper.createFilter(rd.getFilterConfigId());
		filter.open(rd);
		String filename = Util.getFilename(rd.getInputURI().getPath(), true);
		ITm tm = repo.addTm(filename, null);
		HashMap<String, String> map = new HashMap<String, String>();
		String[] trgFields;
		
		while ( filter.hasNext() ) {
			Event event = filter.next();
			if ( !event.isTextUnit() ) continue;
			
			ITextUnit tu = event.getTextUnit();
			ISegments srcSegs = tu.getSourceSegments();

			// For each source segment
			for ( net.sf.okapi.common.resource.Segment srcSeg : srcSegs ) {

				// Get the source fields
				String[] srcFields = DbUtil.fragmentToTmFields(srcSeg.getContent());
				map.clear();
				map.put("Text_"+DbUtil.toDbLang(rd.getSourceLocale()),srcFields[0]);

				// For each target
				for ( LocaleId locId : tu.getTargetLocales() ) {
					// Get the segment
					net.sf.okapi.common.resource.Segment trgSeg = tu.getTargetSegments(rd.getTargetLocale()).get(srcSeg.getId());
					if ( trgSeg != null ) {
						trgFields = DbUtil.fragmentToTmFields(trgSeg.getContent());
					}
					else {
						trgFields = new String[2];
					}
					map.put("Text_"+DbUtil.toDbLang(locId), trgFields[0]);
				}
				// Add the record to the database
				tm.addRecord(map);
			}
		}
		filter.close();
		addTmTab(tm);
	}
}

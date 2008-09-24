/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel and the Okapi Framework contributors     */
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

package sf.okapi.lib.ui.segmentation;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.sf.okapi.common.ui.ClosePanel;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.InputDialog;
import net.sf.okapi.lib.segmentation.LanguageMap;
import net.sf.okapi.lib.segmentation.Rule;
import net.sf.okapi.lib.segmentation.SRXDocument;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class GroupsAndOptionsDialog {

	private Shell            shell;
	private List             lbLangRules;
	private List             lbLangMaps;
	private SRXDocument      srxDoc;
	private Button           btAddRules;
	private Button           btRenameRules;
	private Button           btRemoveRules;
	private Button           btAddMap;
	private Button           btEditMap;
	private Button           btRemoveMap;
	private Button           btMoveUpMap;
	private Button           btMoveDownMap;
	private Button           chkSegmentSubFlows;
	private Button           chkCascade;
	private Button           chkIncludeOpeningCodes;
	private Button           chkIncludeClosingCodes;
	private Button           chkIncludeIsolatedCodes;
	private Button           chkOneSegmentIncludesAll;
	private ClosePanel       pnlActions;	


	public GroupsAndOptionsDialog (Shell parent,
		SRXDocument srxDoc)
	{
		this.srxDoc = srxDoc;
		
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("Groups And Options");
		shell.setImage(parent.getImage());
		shell.setLayout(new GridLayout(2, true));
		
		Group grpTmp = new Group(shell, SWT.NONE);
		grpTmp.setText("Options");
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		grpTmp.setLayoutData(gdTmp);
		GridLayout layTmp = new GridLayout(2, false);
		grpTmp.setLayout(layTmp);

		chkSegmentSubFlows = new Button(grpTmp, SWT.CHECK);
		chkSegmentSubFlows.setText("Segment sub-flow items");
		
		chkIncludeOpeningCodes = new Button(grpTmp, SWT.CHECK);
		chkIncludeOpeningCodes.setText("Include opening in-line codes");
		
		chkCascade = new Button(grpTmp, SWT.CHECK);
		chkCascade.setText("Cascade language maps matching");
		
		chkIncludeClosingCodes = new Button(grpTmp, SWT.CHECK);
		chkIncludeClosingCodes.setText("Include closing in-line codes");

		chkOneSegmentIncludesAll = new Button(grpTmp, SWT.CHECK);
		chkOneSegmentIncludesAll.setText("Include all text for single segment (extension)");
		
		chkIncludeIsolatedCodes = new Button(grpTmp, SWT.CHECK);
		chkIncludeIsolatedCodes.setText("Include isolated in-line codes");
		
		//=== Language Rules
		
		grpTmp = new Group(shell, SWT.NONE);
		grpTmp.setText("Language Rules");
		gdTmp = new GridData(GridData.FILL_BOTH);
		grpTmp.setLayoutData(gdTmp);
		layTmp = new GridLayout(2, false);
		grpTmp.setLayout(layTmp);
		
		int listWidthHint = 150;
		lbLangRules = new List(grpTmp, SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.verticalSpan = 3;
		gdTmp.widthHint = listWidthHint;
		lbLangRules.setLayoutData(gdTmp);
		lbLangRules.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateRulesButtons();
			};
		});
		
		int buttonWidth = 80;
		btAddRules = new Button(grpTmp, SWT.PUSH);
		btAddRules.setText("Add...");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = buttonWidth;
		btAddRules.setLayoutData(gdTmp);
		btAddRules.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editRules(true);
			}
		});
		
		btRenameRules = new Button(grpTmp, SWT.PUSH);
		btRenameRules.setText("Rename...");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = buttonWidth;
		btRenameRules.setLayoutData(gdTmp);
		btRenameRules.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editRules(false);
			}
		});
		
		btRemoveRules = new Button(grpTmp, SWT.PUSH);
		btRemoveRules.setText("Remove...");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = buttonWidth;
		btRemoveRules.setLayoutData(gdTmp);
		btRemoveRules.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeRules();
			}
		});
		
		//=== Language Maps
		
		grpTmp = new Group(shell, SWT.NONE);
		grpTmp.setText("Language Maps");
		gdTmp = new GridData(GridData.FILL_BOTH);
		grpTmp.setLayoutData(gdTmp);
		layTmp = new GridLayout(2, false);
		grpTmp.setLayout(layTmp);
		
		lbLangMaps = new List(grpTmp, SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.verticalSpan = 5;
		gdTmp.widthHint = listWidthHint;		
		lbLangMaps.setLayoutData(gdTmp);
		lbLangMaps.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateMapsButtons();
			};
		});

		btAddMap = new Button(grpTmp, SWT.PUSH);
		btAddMap.setText("Add...");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = buttonWidth;
		btAddMap.setLayoutData(gdTmp);
		btAddMap.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editMap(true);
			}
		});

		btEditMap = new Button(grpTmp, SWT.PUSH);
		btEditMap.setText("Edit...");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = buttonWidth;
		btEditMap.setLayoutData(gdTmp);
		btEditMap.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editMap(false);
			}
		});
		
		btRemoveMap = new Button(grpTmp, SWT.PUSH);
		btRemoveMap.setText("Remove");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = buttonWidth;
		btRemoveMap.setLayoutData(gdTmp);
		btRemoveMap.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeMap();
			}
		});
		
		btMoveUpMap = new Button(grpTmp, SWT.PUSH);
		btMoveUpMap.setText("Move Up");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = buttonWidth;
		btMoveUpMap.setLayoutData(gdTmp);
		btMoveUpMap.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveUpMap();
			}
		});
		
		btMoveDownMap = new Button(grpTmp, SWT.PUSH);
		btMoveDownMap.setText("Move Down");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = buttonWidth;
		btMoveDownMap.setLayoutData(gdTmp);
		btMoveDownMap.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveDownMap();
			}
		});

		// Handling of the closing event
		shell.addShellListener(new ShellListener() {
			public void shellActivated(ShellEvent event) {}
			public void shellClosed(ShellEvent event) {
				if ( !validate() ) event.doit = false;
				else getOptions();
			}
			public void shellDeactivated(ShellEvent event) {}
			public void shellDeiconified(ShellEvent event) {}
			public void shellIconified(ShellEvent event) {}
		});

		//--- Dialog-level buttons
		
		SelectionAdapter CloseActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( e.widget.getData().equals("h") ) {
					//TODO: UIUtil.start(help);
					return;
				}
				if ( e.widget.getData().equals("c") ) {
					shell.close();
				}
			};
		};
		pnlActions = new ClosePanel(shell, SWT.NONE, CloseActions, true);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btClose);
		
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.y < 400 ) startSize.y = 400;
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);

		setOptions();
		updateLanguageRules(null);
		updateLanguageMaps(0);
	}
	
	public void showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
	}

	private void setOptions () {
		chkSegmentSubFlows.setSelection(srxDoc.segmentSubFlows());
		chkCascade.setSelection(srxDoc.cascade());
		chkIncludeOpeningCodes.setSelection(srxDoc.includeStartCodes());
		chkIncludeClosingCodes.setSelection(srxDoc.includeEndCodes());
		chkIncludeIsolatedCodes.setSelection(srxDoc.includeIsolatedCodes());
		chkOneSegmentIncludesAll.setSelection(srxDoc.oneSegmentIncludesAll());
	}
	
	private void getOptions () {
		srxDoc.setSegmentSubFlows(chkSegmentSubFlows.getSelection());
		srxDoc.setCascade(chkCascade.getSelection());
		srxDoc.setIncludeStartCodes(chkIncludeOpeningCodes.getSelection());
		srxDoc.setIncludeEndCodes(chkIncludeClosingCodes.getSelection());
		srxDoc.setIncludeIsolatedCodes(chkIncludeIsolatedCodes.getSelection());
		srxDoc.setOneSegmentIncludesAll(chkOneSegmentIncludesAll.getSelection());
	}
	
	private void updateRulesButtons () {
		boolean enabled = (lbLangRules.getSelectionIndex()!=-1);
		btRenameRules.setEnabled(enabled);
		btRemoveRules.setEnabled(enabled);
	}
	
	private void updateLanguageRules (String selection) {
		lbLangRules.removeAll();
		LinkedHashMap<String, ArrayList<Rule>> list = srxDoc.getAllLanguageRules();
		
		if (( selection != null ) && !list.containsKey(selection) ) {
			selection = null;
		}
		for ( String ruleName : list.keySet() ) {
			lbLangRules.add(ruleName);
			if ( selection == null ) selection = ruleName;
		}
		if ( lbLangRules.getItemCount() > 0 ) {
			if ( selection != null ) {
				lbLangRules.select(lbLangRules.indexOf(selection));
			}
		}
		updateRulesButtons();
	}
	
	private void updateMapsButtons () {
		int n = lbLangMaps.getSelectionIndex();
		boolean enabled = (n!=-1);
		btEditMap.setEnabled(enabled);
		btRemoveMap.setEnabled(enabled);
		btMoveUpMap.setEnabled(n>0);
		btMoveDownMap.setEnabled(n<lbLangMaps.getItemCount()-1);
	}
	
	private void updateLanguageMaps (int selection) {
		lbLangMaps.removeAll();
		ArrayList<LanguageMap> list = srxDoc.getAllLanguagesMaps();
		for ( LanguageMap langMap : list ) {
			lbLangMaps.add(langMap.getPattern() + " --> " + langMap.getRuleName());
		}
		if (( selection < 0 ) || ( selection >= lbLangMaps.getItemCount() )) {
			selection = 0;
		}
		if ( lbLangMaps.getItemCount() > 0 ) {
			lbLangMaps.select(selection);
		}
		updateMapsButtons();
	}
	
	private void editMap (boolean createNewMap) {
		LanguageMap langMap;
		String caption;
		int n = -1;
		if ( createNewMap ) {
			caption = "New Language Map";
			langMap = new LanguageMap("", "");
		}
		else {
			n = lbLangMaps.getSelectionIndex();
			if ( n == -1 ) return;
			langMap = srxDoc.getAllLanguagesMaps().get(n);
			caption = "Edit Language Map";
		}
		
		LanguageMapDialog dlg = new LanguageMapDialog(shell, caption, langMap);
		if ( (langMap = dlg.showDialog()) == null ) return; // Cancel
		
		if ( createNewMap ) {
			srxDoc.addLanguageMap(langMap);
			n = srxDoc.getAllLanguagesMaps().size()+1;
		}
		else {
			srxDoc.getAllLanguagesMaps().set(n, langMap);
		}
		srxDoc.setIsModified(true);
		updateLanguageMaps(n);
	}
	
	private void removeMap () {
		int n = lbLangMaps.getSelectionIndex();
		if ( n == -1 ) return;
		srxDoc.getAllLanguagesMaps().remove(n);
		srxDoc.setIsModified(true);
		updateLanguageMaps(n);
	}
	
	private void moveUpMap () {
		int n = lbLangMaps.getSelectionIndex();
		if ( n < 1 ) return;
		LanguageMap tmp = srxDoc.getAllLanguagesMaps().get(n-1);
		srxDoc.getAllLanguagesMaps().set(n-1,
			srxDoc.getAllLanguagesMaps().get(n));
		srxDoc.getAllLanguagesMaps().set(n, tmp);
		srxDoc.setIsModified(true);
		updateLanguageMaps(--n);
	}
	
	private void moveDownMap () {
		int n = lbLangMaps.getSelectionIndex();
		if ( n > lbLangMaps.getItemCount()-2 ) return;
		LanguageMap tmp = srxDoc.getAllLanguagesMaps().get(n+1);
		srxDoc.getAllLanguagesMaps().set(n+1,
			srxDoc.getAllLanguagesMaps().get(n));
		srxDoc.getAllLanguagesMaps().set(n, tmp);
		srxDoc.setIsModified(true);
		updateLanguageMaps(++n);
	}
	
	private void editRules (boolean createNewRules) {
		String name;
		String oldName = null;
		String caption;
		if ( createNewRules ) {
			name = String.format("group%d",
				srxDoc.getAllLanguageRules().size()+1);
			caption = "New Rules";
		}
		else {
			int n = lbLangRules.getSelectionIndex();
			if ( n == -1 ) return;
			oldName = name = lbLangRules.getItem(n);
			caption = "Rename Rules";
		}
		
		while ( true ) {
			// Edit the name
			InputDialog dlg = new InputDialog(shell, caption, "Name of the rules:", name, null);
			if ( (name = dlg.showDialog()) == null ) return; // Cancel
		
			// Else:
			if ( createNewRules ) {
				if ( srxDoc.getAllLanguageRules().containsKey(name) ) {
					Dialogs.showError(shell,
						String.format("The name \"%s\" exists already.\nPlease choose another one.", name),
						null);
				}
				else {
					srxDoc.addLanguageRule(name, new ArrayList<Rule>());
					break;
				}
			}
			else {
				ArrayList<Rule> list = srxDoc.getLanguageRules(oldName);
				srxDoc.getAllLanguageRules().remove(oldName);
				srxDoc.addLanguageRule(name, list);
				break;
			}
		}
		updateLanguageRules(name);
	}
	
	private void removeRules () {
		int n = lbLangRules.getSelectionIndex();
		if ( n == -1 ) return;
		String ruleName = lbLangRules.getItem(n);
		// Ask confirmation
		MessageBox dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
		dlg.setText(shell.getText());
		dlg.setMessage(String.format("This command will delete all thes rules grouped under '%s'.\n"
			+ "Do you want to proceed with the deletion?", ruleName));
		switch ( dlg.open() ) {
		case SWT.CANCEL:
		case SWT.NO:
			return;
		}
		// Remove
		srxDoc.getAllLanguageRules().remove(ruleName);
		srxDoc.setIsModified(true);
		updateLanguageRules(null);
	}
	
	private boolean validate () {
		try {
			int nonexistingRules = 0;
			StringBuilder notMapped = new StringBuilder();
			LinkedHashMap<String, ArrayList<Rule>> list = srxDoc.getAllLanguageRules();
			for ( LanguageMap langRule : srxDoc.getAllLanguagesMaps() ) {
				if ( !list.containsKey(langRule.getRuleName()) ) {
					if ( nonexistingRules > 0 ) notMapped.append(", ");
					notMapped.append(langRule.getRuleName());
					nonexistingRules++;
				}
			}
			
			if ( nonexistingRules == 0 ) return true;
			// Else: Error.
			MessageBox dlg = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
			dlg.setText(shell.getText());
			dlg.setMessage(String.format("Number of language maps associated with non-existing rules: %d\n"
				+ "(%s)\n"
				+ "Do you want to proceed closing the dialog?", nonexistingRules, notMapped.toString()));
			switch ( dlg.open() ) {
			case SWT.CANCEL:
			case SWT.NO:
				return false;
			case SWT.YES:
				return true;
			}
		}
		catch ( Exception e) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
			return false;
		}
		return true;
	}
}

package sf.okapi.lib.ui.segmentation;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.InputDialog;
import net.sf.okapi.lib.segmentation.LanguageMap;
import net.sf.okapi.lib.segmentation.Rule;
import net.sf.okapi.lib.segmentation.Segmenter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class GroupsAndOptionsDialog {

	private Shell            shell;
	private List             lbLangRules;
	private List             lbLangMaps;
	private Segmenter        segmenter;
	private Button           btAddRules;
	private Button           btRenameRules;
	private Button           btRemoveRules;
	private Button           btAddMap;
	private Button           btEditMap;
	private Button           btRemoveMap;
	private Button           btMoveUpMap;
	private Button           btMoveDownMap;
	

	public GroupsAndOptionsDialog (Shell parent,
		Segmenter segmenter)
	{
		this.segmenter = segmenter;
		
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
		
		//=== Language Rules
		
		grpTmp = new Group(shell, SWT.NONE);
		grpTmp.setText("Language Rules");
		gdTmp = new GridData(GridData.FILL_BOTH);
		grpTmp.setLayoutData(gdTmp);
		layTmp = new GridLayout(2, false);
		grpTmp.setLayout(layTmp);
		
		lbLangRules = new List(grpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.verticalSpan = 3;
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
		
		lbLangMaps = new List(grpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.verticalSpan = 5;
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
		btRemoveMap.setText("Remove...");
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

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Dialogs.centerWindow(shell, parent);

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
	
	private void updateRulesButtons () {
		boolean enabled = (lbLangRules.getSelectionIndex()!=-1);
		btRenameRules.setEnabled(enabled);
		btRemoveRules.setEnabled(enabled);
	}
	
	private void updateLanguageRules (String selection) {
		lbLangRules.removeAll();
		LinkedHashMap<String, ArrayList<Rule>> list = segmenter.getAllLanguageRules();
		
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
		ArrayList<LanguageMap> list = segmenter.getAllLanguagesMaps();
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
		
	}
	
	private void removeMap () {
		int n = lbLangMaps.getSelectionIndex();
		if ( n == -1 ) return;
		segmenter.getAllLanguagesMaps().remove(n);
		updateLanguageMaps(n);
	}
	
	private void moveUpMap () {
		
	}
	
	private void moveDownMap () {
		
	}
	
	private void editRules (boolean createNewRules) {
		String name;
		String oldName = null;
		String caption;
		if ( createNewRules ) {
			name = String.format("group%d",
				segmenter.getAllLanguageRules().size()+1);
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
				if ( segmenter.getAllLanguageRules().containsKey(name) ) {
					Dialogs.showError(shell,
						String.format("The name \"%s\" exists already.\nPlease choose another one.", name),
						null);
				}
				else {
					segmenter.addLanguageRule(name, new ArrayList<Rule>());
					break;
				}
			}
			else {
				ArrayList<Rule> list = segmenter.getLanguageRules(oldName);
				segmenter.getAllLanguageRules().remove(oldName);
				segmenter.addLanguageRule(name, list);
				break;
			}
		}
		updateLanguageRules(name);
	}
	
	private void removeRules () {
		int n = lbLangRules.getSelectionIndex();
		if ( n == -1 ) return;
		String ruleName = lbLangRules.getItem(n);
		segmenter.getAllLanguageRules().remove(ruleName);
		updateLanguageRules(null);
	}
}

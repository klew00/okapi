package sf.okapi.lib.ui.segmentation;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.sf.okapi.common.ui.ClosePanel;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.lib.segmentation.Rule;
import net.sf.okapi.lib.segmentation.Segmenter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class SRXEditor {

	private Shell            shell;
	private Text             edSampleText;
	private Text             edResults;
	private Table            tblRules;
	private RulesTableModel  rulesTableMod;
	private Combo            cbGroup;
	private Segmenter        segmenter;
	private String           srxPath;
//	private IContainer       sampleText;
	private Button           btSRXDocs;
	private Menu             popupSRXDocs;
	private Button           btAddRule;
	private Button           btEditRule;
	private Button           btRemoveRule;
	private Button           btMoveUpRule;
	private Button           btMoveDownRule;
	private ClosePanel       pnlActions;
	private Button           rdApplySampleForMappedRules;
	private Button           rdApplySampleForCurrentSet;
	private Text             edSampleLanguage;
	

	public SRXEditor (Shell parent) {
		segmenter = new Segmenter();
		srxPath = null;
//		sampleText = new Container();
		
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("Segmentation Rules Editor");
		shell.setImage(parent.getImage());
		shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout(6, false);
		cmpTmp.setLayout(layTmp);
		
		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText("Language rules currently displayed:");
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		label.setLayoutData(gdTmp);
		
		cbGroup = new Combo(cmpTmp, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		cbGroup.setLayoutData(gdTmp);
		cbGroup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateRules(0, false);
			};
		});
		
		int topButtonsWidth = 140;
		Button btTmp = new Button(cmpTmp, SWT.PUSH);
		btTmp.setText("Groups and Options...");
		gdTmp = new GridData();
		gdTmp.widthHint = topButtonsWidth;
		btTmp.setLayoutData(gdTmp);
		btTmp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editGroupsAndOptions();
			}
		});
		
		btSRXDocs = new Button(cmpTmp, SWT.PUSH);
		btSRXDocs.setText("SRX Document...");
		gdTmp = new GridData();
		gdTmp.widthHint = topButtonsWidth;
		btSRXDocs.setLayoutData(gdTmp);
		setFileMenu(shell, btSRXDocs);
		btSRXDocs.addSelectionListener( new SelectionAdapter () {
			public void widgetSelected(SelectionEvent evt) {
				if (( popupSRXDocs != null ) && ( !popupSRXDocs.isVisible() )) {
					Rectangle bounds = btSRXDocs.getBounds(); 
					Point menuLoc = btSRXDocs.getParent().toDisplay(
						bounds.x, bounds.y + bounds.height);
					popupSRXDocs.setLocation(menuLoc.x, menuLoc.y);
					popupSRXDocs.setVisible(true);
			}}
		});
		
		tblRules = new Table(cmpTmp, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		tblRules.setHeaderVisible(true);
		tblRules.setLinesVisible(true);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 6;
		gdTmp.minimumHeight = 130;
		tblRules.setLayoutData(gdTmp);
		tblRules.addControlListener(new ControlAdapter() {
		    public void controlResized(ControlEvent e) {
		    	Rectangle rect = tblRules.getClientArea();
				//TODO: Check behavior when manual resize a column width out of client area
		    	int typeColWidth = 60;
				int nHalf = (int)((rect.width-typeColWidth) / 2);
				tblRules.getColumn(0).setWidth(typeColWidth);
				tblRules.getColumn(1).setWidth(nHalf);
				tblRules.getColumn(2).setWidth(nHalf);
		    }
		});
		tblRules.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateRulesButtons();
			};
		});
		rulesTableMod = new RulesTableModel();
		rulesTableMod.linkTable(tblRules);
		
		Composite cmpGroup = new Composite(cmpTmp, SWT.NONE);
		layTmp = new GridLayout(5, true);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		cmpGroup.setLayout(layTmp);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 5;
		cmpGroup.setLayoutData(gdTmp);

		int ruleButtonsWidth = 80;
		btAddRule = new Button(cmpGroup, SWT.PUSH);
		btAddRule.setText("&Add...");
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btAddRule.setLayoutData(gdTmp);
		btAddRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editRule(true);
			}
		});
		
		btEditRule = new Button(cmpGroup, SWT.PUSH);
		btEditRule.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btEditRule.setText("&Edit...");
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btEditRule.setLayoutData(gdTmp);
		btEditRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editRule(false);
			}
		});
		
		btRemoveRule = new Button(cmpGroup, SWT.PUSH);
		btRemoveRule.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btRemoveRule.setText("&Remove");
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btRemoveRule.setLayoutData(gdTmp);
		btRemoveRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeRule();
			}
		});

		btMoveUpRule = new Button(cmpGroup, SWT.PUSH);
		btMoveUpRule.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btMoveUpRule.setText("Move &Up");
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btMoveUpRule.setLayoutData(gdTmp);
		btMoveUpRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveUpRule();
			}
		});

		btMoveDownRule = new Button(cmpGroup, SWT.PUSH);
		btMoveDownRule.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btMoveDownRule.setText("Move &Down");
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btMoveDownRule.setLayoutData(gdTmp);
		btMoveDownRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveDownRule();
			}
		});

		//--- Sample block
		
		cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		cmpTmp.setLayout(new GridLayout(3, false));
		
		label = new Label(cmpTmp, SWT.None);
		label.setText("Sample text (use <x>...</x> and <x/> for inline codes):");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 3;
		label.setLayoutData(gdTmp);
		
		int sampleMinHeight = 40;
		edSampleText = new Text(cmpTmp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.minimumHeight = sampleMinHeight;
		gdTmp.horizontalSpan = 3;
		edSampleText.setLayoutData(gdTmp);
		
		rdApplySampleForCurrentSet = new Button(cmpTmp, SWT.RADIO);
		rdApplySampleForCurrentSet.setText("Test on the current set of rules only");
		rdApplySampleForCurrentSet.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				edSampleLanguage.setEnabled(rdApplySampleForMappedRules.getSelection());
				updateRules(tblRules.getSelectionIndex(), true);
			};
		});

		rdApplySampleForMappedRules = new Button(cmpTmp, SWT.RADIO);
		rdApplySampleForMappedRules.setText("Test on the rules for this language code:");
		rdApplySampleForMappedRules.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				edSampleLanguage.setEnabled(rdApplySampleForMappedRules.getSelection());
				updateRules(tblRules.getSelectionIndex(), true);
			};
		});
		
		edSampleLanguage = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE);
		gdTmp = new GridData();
		edSampleLanguage.setLayoutData(gdTmp);
		edSampleLanguage.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateResults(false);
			}
		});


		edResults = new Text(cmpTmp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		edResults.setLayoutData(gdTmp);
		gdTmp.minimumHeight = sampleMinHeight;
		gdTmp.horizontalSpan = 3;
		edResults.setEditable(false);

		edSampleText.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateResults(false);
			}
		});

		// Handling of the closing event
		shell.addShellListener(new ShellListener() {
			public void shellActivated(ShellEvent event) {}
			public void shellClosed(ShellEvent event) {
				if ( !checkIfRulesNeedSaving() ) event.doit = false;
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
		if ( startSize.x < 700 ) startSize.x = 700; 
		if ( startSize.y < 600 ) startSize.y = 600; 
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
		
		updateAll();
	}
	
	private void setFileMenu (Shell shell,
		Button fileButton)
	{
		popupSRXDocs = new Menu(shell, SWT.POP_UP);
		fileButton.setMenu(popupSRXDocs);

		MenuItem menuItem = new MenuItem(popupSRXDocs, SWT.PUSH);
		menuItem.setText("New Document");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				newSRXDocument();
            }
		});

		menuItem = new MenuItem(popupSRXDocs, SWT.PUSH);
		menuItem.setText("Open Document...");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				loadSRXDocument(null);
            }
		});

		new MenuItem(popupSRXDocs, SWT.SEPARATOR);

		menuItem = new MenuItem(popupSRXDocs, SWT.PUSH);
		menuItem.setText("Save Current Document");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveSRXDocument(srxPath);
            }
		});

		menuItem = new MenuItem(popupSRXDocs, SWT.PUSH);
		menuItem.setText("Save Current Document As...");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveSRXDocument(null);
            }
		});
}

	public void showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
	}
	
/*	private void processInlineCodes () {
		try {
			String text = edSampleText.getText().replaceAll("<x>", String.valueOf((char)IContainer.CODE_OPENING));
			text = text.replaceAll("</x>", String.valueOf((char)IContainer.CODE_CLOSING));
			text = text.replaceAll("<x/>", String.valueOf((char)IContainer.CODE_ISOLATED));
	
			sampleText.reset();
			Stack<Integer> stackID = new Stack<Integer>();
			int id = 0;
			for ( int i=0; i<text.length(); i++ ) {
				switch ( text.codePointAt(i) ) {
				case IContainer.CODE_OPENING:
					sampleText.append(new CodeFragment(IContainer.CODE_OPENING,
						stackID.push(++id), "<x>"));
					break;
				case IContainer.CODE_CLOSING:
					sampleText.append(new CodeFragment(IContainer.CODE_CLOSING,
						stackID.pop(), "</x>"));
					break;
				case IContainer.CODE_ISOLATED:
					sampleText.append(new CodeFragment(IContainer.CODE_OPENING,
						++id, "<x/>"));
					break;
				default:
					sampleText.append(text.charAt(i));
				}
			}
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
	}*/
	
	private void updateResults (boolean forceReset) {
		// Check if we need to re-build the list of applicable rules
		if ( cbGroup.getSelectionIndex() != -1 ) {
			// Both methods applies new rules only if the 
			// parameter passed is different from the current identifier
			// or if forceReset is true.
			if ( rdApplySampleForCurrentSet.getSelection() ) {
				segmenter.applySingleLanguageRule(cbGroup.getText(), forceReset);
			}
			else { // Applies all the matching rules
				// Make sure we have a language code
				if ( edSampleLanguage.getText().length() == 0 ) {
					edSampleLanguage.setText("en");
				}
				segmenter.applyLanguageRules(edSampleLanguage.getText(), forceReset);
			}
		}
		
		// Convert the sample field content into an parsed item
		//processInlineCodes();
		
		String oriText = edSampleText.getText();
		segmenter.segment(oriText);
		//segmenter.segment(sampleText);
		StringBuilder tmp = new StringBuilder();
		ArrayList<Integer> list = segmenter.getSplitPositions();
		tmp.append(String.format("%d: ", list.size()+1));
		int start = 0;
		for ( int pos : list ) {
			tmp.append("["+oriText.substring(start, pos)+"] ");
				start = pos;
		}
		// Last one
		tmp.append("["+oriText.substring(start)+"]");
		edResults.setText(tmp.toString());
	}
	
	private void updateLanguageRuleList () {
		cbGroup.removeAll();
		LinkedHashMap<String, ArrayList<Rule>> langRules = segmenter.getAllLanguageRules();
		for ( String ruleName : langRules.keySet() ) {
			cbGroup.add(ruleName);
		}
		if ( cbGroup.getItemCount() > 0 ) {
			cbGroup.select(0);
		}
		updateRules(0, true);
	}
	
	private void updateRules (int selection,
		boolean forceReset) {
		rulesTableMod.setLanguageRules(
			segmenter.getLanguageRules(cbGroup.getText()));
		rulesTableMod.updateTable(selection);
		updateResults(forceReset);
		updateRulesButtons();
	}
	
	private void updateRulesButtons () {
		int n = tblRules.getSelectionIndex();
		btAddRule.setEnabled(cbGroup.getSelectionIndex()>-1);
		btEditRule.setEnabled(n != -1);
		btRemoveRule.setEnabled(n != -1);
		btMoveUpRule.setEnabled(n > 0);
		btMoveDownRule.setEnabled(n < tblRules.getItemCount()-1);
	}
	
	private void editGroupsAndOptions () {
		try {
			getSurfaceData();
			GroupsAndOptionsDialog dlg = new GroupsAndOptionsDialog(shell, segmenter);
			dlg.showDialog();
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
		finally {
			updateAll();
		}
	}

	private void setSurfaceData () {
		edSampleText.setText(segmenter.getSampleText());
		edSampleLanguage.setText(segmenter.getSampleLanguage());
		rdApplySampleForMappedRules.setSelection(segmenter.sampleOnMappedRules());
		rdApplySampleForCurrentSet.setSelection(!segmenter.sampleOnMappedRules());
		edSampleLanguage.setEnabled(rdApplySampleForMappedRules.getSelection());
	}

	private void getSurfaceData () {
		segmenter.setSampleText(edSampleText.getText());
		segmenter.setSampleLanguage(edSampleLanguage.getText());
		segmenter.setSampleOnMappedRules(rdApplySampleForMappedRules.getSelection());
	}
	
	private void updateAll () {
		cbGroup.removeAll();
		setSurfaceData();
		updateLanguageRuleList();
	}
	
	private void newSRXDocument () {
		if ( !checkIfRulesNeedSaving() ) return;
		segmenter = new Segmenter();
		srxPath = null;
		updateAll();
	}
	
	private void loadSRXDocument (String path) {
		try {
			if ( path == null ) {
				String[] paths = Dialogs.browseFilenames(shell, "Open SRX Document",
					false, null, "SRX Documents (*.srx)", "*.srx");
				if ( paths == null ) return; // Cancel
				else path = paths[0];
			}
			segmenter.loadRules(path);
			srxPath = path;
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
		finally {
			updateAll();
		}
	}
	
	private boolean saveSRXDocument (String path) {
		try {
			if ( path == null ) {
				path = Dialogs.browseFilenamesForSave(shell, "Save SRX Document", null,
					"SRX Documents (*.srx)", "*.srx");
				if ( path == null ) return false;
			}
			getSurfaceData();
			segmenter.saveRules(path);
			srxPath = path;
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
		return true;
	}
	
	private void editRule (boolean createNewRule) {
		Rule rule;
		String caption;
		String ruleName = cbGroup.getItem(cbGroup.getSelectionIndex());
		int n = -1;
		if ( createNewRule ) {
			caption = "New Rule";
			rule = new Rule("", "", true);
		}
		else {
			n = tblRules.getSelectionIndex();
			if ( n == -1 ) return;
			rule = segmenter.getLanguageRules(ruleName).get(n);
			caption = "Edit Rule";
		}
		
		RuleDialog dlg = new RuleDialog(shell, caption, rule);
		if ( (rule = dlg.showDialog()) == null ) return; // Cancel
		
		if ( createNewRule ) {
			segmenter.getLanguageRules(ruleName).add(rule);
			n = segmenter.getLanguageRules(ruleName).size()-1;
		}
		else {
			segmenter.getLanguageRules(ruleName).set(n, rule);
		}
		segmenter.setIsModified(true);
		updateRules(n, true);
	}
	
	private void removeRule () {
		int n = tblRules.getSelectionIndex();
		if ( n == -1 ) return;
		String ruleName = cbGroup.getItem(cbGroup.getSelectionIndex());
		segmenter.getLanguageRules(ruleName).remove(n);
		segmenter.setIsModified(true);
		tblRules.remove(n);
		if ( n > tblRules.getItemCount()-1 )
			n = tblRules.getItemCount()-1;
		if ( tblRules.getItemCount() > 0 )
			tblRules.select(n);
		updateRulesButtons();
		updateResults(true);
	}
	
	private void moveUpRule () {
		int n = tblRules.getSelectionIndex();
		if ( n < 1 ) return;
		// Move in the segmenter
		String ruleName = cbGroup.getItem(cbGroup.getSelectionIndex());
		Rule tmp = segmenter.getLanguageRules(ruleName).get(n-1);
		segmenter.getLanguageRules(ruleName).set(n-1,
			segmenter.getLanguageRules(ruleName).get(n));
		segmenter.getLanguageRules(ruleName).set(n, tmp);
		segmenter.setIsModified(true);
		// Update
		updateRules(n-1, true);
	}
	
	private void moveDownRule () {
		int n = tblRules.getSelectionIndex();
		if ( n > tblRules.getItemCount()-2 ) return;
		// Move in the segmenter
		String ruleName = cbGroup.getItem(cbGroup.getSelectionIndex());
		Rule tmp = segmenter.getLanguageRules(ruleName).get(n+1);
		segmenter.getLanguageRules(ruleName).set(n+1,
			segmenter.getLanguageRules(ruleName).get(n));
		segmenter.getLanguageRules(ruleName).set(n, tmp);
		segmenter.setIsModified(true);
		// Update
		updateRules(n+1, true);
	}
	
	/**
	 * Checks if the rules need saving, and save them after prompting
	 * the user if needed.
	 * @return False if the user cancel, true if a decision is made. 
	 */
	private boolean checkIfRulesNeedSaving () {
		getSurfaceData();
		if ( segmenter.isModified() ) {
			MessageBox dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
			dlg.setText(shell.getText());
			dlg.setMessage("The segmentation rules have been modified but not saved.\n"
				+"Do you want to save them?");
			switch ( dlg.open() ) {
			case SWT.CANCEL:
				return false;
			case SWT.YES:
				return saveSRXDocument(srxPath);
			}
		}
		return true;
	}
}

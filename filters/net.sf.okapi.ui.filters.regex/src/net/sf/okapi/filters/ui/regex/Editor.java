/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.filters.ui.regex;

import java.util.ArrayList;
import java.util.regex.Pattern;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.InputDialog;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.filters.InlineCodeFinderDialog;
import net.sf.okapi.common.ui.filters.LDPanel;
import net.sf.okapi.filters.regex.Parameters;
import net.sf.okapi.filters.regex.Rule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class Editor implements IParametersEditor {
	
	private Shell shell;
	private boolean result = false;
	private Text edExpression;
	private Button chkExtractOuterStrings;
	private Text edStartString;
	private Text edEndString;
	private List lbRules;
	private Button btAdd;
	private Button btEdit;
	private Button btRename;
	private Button btRemove;
	private Button btMoveUp;
	private Button btMoveDown;
	private LDPanel pnlLD;
	private Parameters params;
	private ArrayList<Rule> rules;
	private int ruleIndex = -1;
	private Combo cbRuleType;
	private Button chkPreserveWS;
	private Button chkUnwrap;
	private Button chkUseCodeFinder;
	private Button btEditFinderRules;
	private Button chkIgnoreCase;
	private Button chkDotAll;
	private Button chkMultiline;
	
	/**
	 * Invokes the editor for the Properties filter parameters.
	 * @param p_Options The option object of the action.
	 * @param p_Object The SWT Shell object of the parent shell in the UI.
	 */
	public boolean edit (IParameters p_Options,
		Object p_Object)
	{
		boolean bRes = false;
		shell = null;
		
		params = (Parameters)p_Options;
		// Make a work copy (in case of escape)
		rules = new ArrayList<Rule>();
		for ( Rule rule : params.getRules() ) {
			rules.add(new Rule(rule));
		}
		
		try {
			shell = new Shell((Shell)p_Object, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
			create((Shell)p_Object);
			return showDialog();
		}
		catch ( Exception E ) {
			Dialogs.showError(shell, E.getLocalizedMessage(), null);
			bRes = false;
		}
		finally {
			// Dispose of the shell, but not of the display
			if ( shell != null ) shell.dispose();
		}
		return bRes;
	}
	
	public IParameters createParameters () {
		return new Parameters();
	}
	
	private void create (Shell p_Parent) {
		shell.setText("Regex Filter Parameters");
		if ( p_Parent != null ) shell.setImage(p_Parent.getImage());
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);

		TabFolder tfTmp = new TabFolder(shell, SWT.NONE);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		tfTmp.setLayoutData(gdTmp);

		//--- Rules tab
		
		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
		layTmp = new GridLayout(2, false);
		cmpTmp.setLayout(layTmp);
		
		lbRules = new List(cmpTmp, SWT.BORDER | SWT.H_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 1;
		gdTmp.grabExcessHorizontalSpace = true;
		gdTmp.verticalSpan = 3;
		lbRules.setLayoutData(gdTmp);
		lbRules.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateRule();
				updateMoveButtons();
			};
		});
		lbRules.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				editRule(false);
			}
			public void mouseDown(MouseEvent e) {}
			public void mouseUp(MouseEvent e) {}
		});
		
		//--- Rule properties
		
		Group propGroup = new Group(cmpTmp, SWT.NONE);
		layTmp = new GridLayout(2, false);
		propGroup.setLayout(layTmp);
		propGroup.setText("Rule properties");
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.verticalSpan = 3;
		propGroup.setLayoutData(gdTmp);
		
		edExpression = new Text(propGroup, SWT.BORDER | SWT.V_SCROLL);
		edExpression.setEditable(false);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		gdTmp.heightHint = 50;
		edExpression.setLayoutData(gdTmp);
		
		Label label = new Label(propGroup, SWT.NONE);
		label.setText("Action:");
		
		cbRuleType = new Combo(propGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		cbRuleType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cbRuleType.add("Extract the strings inside the content");
		cbRuleType.add("Extract the content itself");
		cbRuleType.add("Treat the content as a comment");
		cbRuleType.add("Do not extract the content");
		
		chkPreserveWS = new Button(propGroup, SWT.CHECK);
		chkPreserveWS.setText("Preserve white spaces");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		chkPreserveWS.setLayoutData(gdTmp);
		
		chkUnwrap = new Button(propGroup, SWT.CHECK);
		chkUnwrap.setText("Unwrap text");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		chkUnwrap.setLayoutData(gdTmp);
		
		chkUseCodeFinder = new Button(propGroup, SWT.CHECK);
		chkUseCodeFinder.setText("Has in-line codes");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		chkUseCodeFinder.setLayoutData(gdTmp);
		chkUseCodeFinder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateEditFinderRulesButton();
			};
		});
		
		btEditFinderRules = new Button(propGroup, SWT.PUSH);
		btEditFinderRules.setText("  Edit In-line Codes Patterns...  ");
		gdTmp = new GridData();
		gdTmp.horizontalIndent = 16;
		gdTmp.horizontalSpan = 2;
		btEditFinderRules.setLayoutData(gdTmp);
		btEditFinderRules.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editFinderRules();
			};
		});

		//--- Buttons
		
		Composite cmpButtons = new Composite(cmpTmp, SWT.NONE);
		layTmp = new GridLayout(2, false);
		layTmp.marginWidth = 0;
		cmpButtons.setLayout(layTmp);
		cmpButtons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		int buttonWidth = 90;
		
		btAdd = new Button(cmpButtons, SWT.PUSH);
		btAdd.setText("Add...");
		gdTmp = new GridData();
		gdTmp.widthHint = buttonWidth;
		btAdd.setLayoutData(gdTmp);
		btAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editRule(true);
			};
		});
		
		btEdit = new Button(cmpButtons, SWT.PUSH);
		btEdit.setText("Edit...");
		gdTmp = new GridData();
		gdTmp.widthHint = buttonWidth;
		btEdit.setLayoutData(gdTmp);
		btEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editRule(false);
			};
		});
		
		btRename = new Button(cmpButtons, SWT.PUSH);
		btRename.setText("Rename...");
		gdTmp = new GridData();
		gdTmp.widthHint = buttonWidth;
		btRename.setLayoutData(gdTmp);
		btRename.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				renameRule();
			};
		});
		
		btMoveUp = new Button(cmpButtons, SWT.PUSH);
		btMoveUp.setText("Move Up");
		gdTmp = new GridData();
		gdTmp.widthHint = buttonWidth;
		btMoveUp.setLayoutData(gdTmp);
		btMoveUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveUpRule();
			};
		});
		
		btRemove = new Button(cmpButtons, SWT.PUSH);
		btRemove.setText("Remove");
		gdTmp = new GridData();
		gdTmp.widthHint = buttonWidth;
		gdTmp.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		btRemove.setLayoutData(gdTmp);
		btRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeRule();
			};
		});
		
		btMoveDown = new Button(cmpButtons, SWT.PUSH);
		btMoveDown.setText("Move Down");
		gdTmp = new GridData();
		gdTmp.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
		gdTmp.widthHint = buttonWidth;
		btMoveDown.setLayoutData(gdTmp);
		btMoveDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveDownRule();
			};
		});

		//--- Options
		
		Group optionsGroup = new Group(cmpTmp, SWT.NONE);
		layTmp = new GridLayout(2, false);
		optionsGroup.setLayout(layTmp);
		optionsGroup.setText("Regular expression options");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		gdTmp.verticalSpan = 1;
		optionsGroup.setLayoutData(gdTmp);
		
		chkDotAll = new Button(optionsGroup, SWT.CHECK);
		chkDotAll.setText("Dot also matches line-feed");

		chkMultiline = new Button(optionsGroup, SWT.CHECK);
		chkMultiline.setText("Multi-line");
		
		chkIgnoreCase = new Button(optionsGroup, SWT.CHECK);
		chkIgnoreCase.setText("Ignore case differences");
		
		//--- end Options
		
		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Rules");
		tiTmp.setControl(cmpTmp);
		
		
		//--- Options tab
		
		cmpTmp = new Composite(tfTmp, SWT.NONE);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);
		
		Group grpTmp = new Group(cmpTmp, SWT.NONE);
		layTmp = new GridLayout();
		grpTmp.setLayout(layTmp);
		grpTmp.setText("Localization directives");
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		grpTmp.setLayoutData(gdTmp);
		pnlLD = new LDPanel(grpTmp, SWT.NONE);

		grpTmp = new Group(cmpTmp, SWT.NONE);
		layTmp = new GridLayout(2, false);
		grpTmp.setLayout(layTmp);
		grpTmp.setText("Strings");
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		grpTmp.setLayoutData(gdTmp);
		
		chkExtractOuterStrings = new Button(grpTmp, SWT.CHECK);
		chkExtractOuterStrings.setText("Extract strings outside the rules");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		chkExtractOuterStrings.setLayoutData(gdTmp);

		label = new Label(grpTmp, SWT.NONE);
		label.setText("Beginning of string:");
		edStartString = new Text(grpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edStartString.setLayoutData(gdTmp);

		label = new Label(grpTmp, SWT.NONE);
		label.setText("End of string:");
		edEndString = new Text(grpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edEndString.setLayoutData(gdTmp);

		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Options");
		tiTmp.setControl(cmpTmp);
		
		//--- Dialog-level buttons

		SelectionAdapter okCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( e.widget.getData().equals("h") ) {
					//TODO: Call help
					return;
				}
				if ( e.widget.getData().equals("o") ) saveData();
				shell.close();
			};
		};
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, okCancelActions, true);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		Rectangle Rect = shell.getBounds();
		shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(shell, p_Parent);
		setData();
	}
	
	private boolean showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}
	
	private void updateRule () {
		saveRuleData(ruleIndex);
		
		int newRuleIndex = lbRules.getSelectionIndex();
		boolean enabled = (newRuleIndex > -1 );
		cbRuleType.setEnabled(enabled);
		chkPreserveWS.setEnabled(enabled);
		chkUnwrap.setEnabled(enabled);
		chkUseCodeFinder.setEnabled(enabled);

		ruleIndex = newRuleIndex;
		if ( ruleIndex < 0 ) {
			edExpression.setText("");
			cbRuleType.select(0);
			chkPreserveWS.setSelection(false);
			chkUnwrap.setSelection(false);
			chkUseCodeFinder.setSelection(false);
			btEditFinderRules.setEnabled(false);
			return;
		}
		Rule rule = rules.get(ruleIndex);
		edExpression.setText("(("+rule.getStart()+")(.*?)("+rule.getEnd()+"))");
		cbRuleType.select(rule.getRuleType());
		chkPreserveWS.setSelection(rule.preserveWS());
		chkUnwrap.setSelection(rule.unwrap());
		chkUseCodeFinder.setSelection(rule.useCodeFinder());
		updateEditFinderRulesButton();
	}

	private void updateEditFinderRulesButton () {
		btEditFinderRules.setEnabled(chkUseCodeFinder.getSelection());
	}
	
	private void editFinderRules () {
		try {
			Rule rule = rules.get(ruleIndex);
			InlineCodeFinderDialog dlg = 
				new InlineCodeFinderDialog(shell, "In-Line Codes Patterns", null);
			dlg.setData(rule.getCodeFinderRules());
			String tmp = dlg.showDialog();
			if ( tmp == null ) return;
			rule.setCodeFinderRules(tmp);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void saveRuleData (int index) {
		if ( index < 0 ) return;
		Rule rule = rules.get(index);
		rule.setRuleType(cbRuleType.getSelectionIndex());
		rule.setPreserveWS(chkPreserveWS.getSelection());
		rule.setUnwrap(chkUnwrap.getSelection());
		rule.setUseCodeFinder(chkUseCodeFinder.getSelection());
	}

	private void updateMoveButtons () {
		int n = lbRules.getSelectionIndex();
		btMoveUp.setEnabled(n > 0);
		btMoveDown.setEnabled((n != -1) && ( n < lbRules.getItemCount()-1 ));
	}

	private void updateRuleButtons () {
		int n = lbRules.getSelectionIndex();
		btRemove.setEnabled(n != -1);
		btEdit.setEnabled(n != -1);
		btRename.setEnabled(n != -1);
		updateMoveButtons();
	}
	
	private void renameRule () {
		try {
			int n = lbRules.getSelectionIndex();
			if ( n == -1 ) return;
			Rule rule = rules.get(n);
			String name = rule.getRuleName();
			InputDialog dlg = new InputDialog(shell, "Rename Rule",
				"New name of the rule:", name, null, 0);
			if ( (name = dlg.showDialog()) == null ) return;
			rule.setRuleName(name);
			lbRules.setItem(n, name);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void editRule (boolean newRule) {
		try {
			Rule rule;
			String caption;
			if ( newRule ) {
				// Get the name
				String name = "newRule";
				InputDialog dlg = new InputDialog(shell, "New Rule",
					"Name of the new rule:", name, null, 0);
				if ( (name = dlg.showDialog()) == null ) return;
				rule = new Rule();
				rule.setRuleName(name);
				caption = "Add New Rule";
			}
			else {
				int n = lbRules.getSelectionIndex();
				if ( n == -1 ) return;
				rule = rules.get(n);
				caption = "Edit Rule";
			}
			
			RuleDialog dlg = new RuleDialog(shell, caption, rule, getRegexOptions());
			if ( !dlg.showDialog() ) return;
			rule = dlg.getRule();
			
			if ( newRule ) {
				rules.add(rule);
				lbRules.add(rule.getRuleName());
				lbRules.select(lbRules.getItemCount()-1);
			}
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
		finally {
			updateRule();
			updateRuleButtons();
		}
	}
	
	private void removeRule () {
		int n = lbRules.getSelectionIndex();
		if ( n == -1 ) return;
		ruleIndex = -1;
		rules.remove(n);
		lbRules.remove(n);
		if ( n > lbRules.getItemCount()-1  ) n = lbRules.getItemCount()-1;
		lbRules.select(n);
		updateRule();
		updateRuleButtons();
	}
	
	private void moveUpRule () {
		int n = lbRules.getSelectionIndex();
		if ( n < 1 ) return;
		saveRuleData(ruleIndex);
		ruleIndex = -1;
		// Move in the rules array
		Rule tmp = rules.get(n);
		rules.set(n, rules.get(n-1));
		rules.set(n-1, tmp);
		// Refresh the list box
		lbRules.setItem(n, rules.get(n).getRuleName());
		lbRules.setItem(n-1, rules.get(n-1).getRuleName());
		lbRules.select(n-1);
		updateRule();
		updateRuleButtons();
	}
	
	private void moveDownRule () {
		int n = lbRules.getSelectionIndex();
		if ( n < 0 ) return;
		saveRuleData(ruleIndex);
		ruleIndex = -1;
		// Move in the rules array
		Rule tmp = rules.get(n);
		rules.set(n, rules.get(n+1));
		rules.set(n+1, tmp);
		// Refresh the list box
		lbRules.setItem(n, rules.get(n).getRuleName());
		lbRules.setItem(n+1, rules.get(n+1).getRuleName());
		lbRules.select(n+1);
		updateRule();
		updateRuleButtons();
	}
	
	private void setData () {
		pnlLD.setOptions(params.locDir.useLD(), params.locDir.localizeOutside());
		chkExtractOuterStrings.setSelection(params.extractOuterStrings);
		edStartString.setText(params.startString);
		edEndString.setText(params.endString);
		for ( Rule rule : rules ) {
			lbRules.add(rule.getRuleName());
		}
		int tmp = params.regexOptions;
		chkDotAll.setSelection((tmp & Pattern.DOTALL)==Pattern.DOTALL);
		chkIgnoreCase.setSelection((tmp & Pattern.CASE_INSENSITIVE)==Pattern.CASE_INSENSITIVE);
		chkMultiline.setSelection((tmp & Pattern.MULTILINE)==Pattern.MULTILINE);
		pnlLD.updateDisplay();
		if ( lbRules.getItemCount() > 0 ) lbRules.select(0);
		updateRule();
		updateRuleButtons();
	}
	
	private void saveData () {
		saveRuleData(ruleIndex);
		//TODO: validation
		params.locDir.setOptions(pnlLD.getUseLD(), pnlLD.getLocalizeOutside());
		params.extractOuterStrings = chkExtractOuterStrings.getSelection();
		params.startString = edStartString.getText();
		params.endString = edEndString.getText();
		
		ArrayList<Rule> paramRules = params.getRules();
		paramRules.clear();
		for ( Rule rule : rules ) {
			paramRules.add(rule);
		}
		params.regexOptions = getRegexOptions();
		result = true;
	}

	private int getRegexOptions () {
		int tmp = 0;
		if ( chkDotAll.getSelection() ) tmp |= Pattern.DOTALL;
		if ( chkIgnoreCase.getSelection() ) tmp |= Pattern.CASE_INSENSITIVE;
		if ( chkMultiline.getSelection() ) tmp |= Pattern.MULTILINE;
		return tmp;
	}
}

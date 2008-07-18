package net.sf.okapi.filters.ui.regex;

import java.util.ArrayList;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.LDPanel;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.filters.regex.Parameters;
import net.sf.okapi.filters.regex.Rule;

import org.eclipse.swt.SWT;
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
	
	private Shell            shell;
	private boolean          result = false;
	private Button           chkExtractOuterStrings;
	private Text             edStartString;
	private Text             edEndString;
	private List             lbRules;
	private Button           btAdd;
	private Button           btRemove;
	private Button           btMoveUp;
	private Button           btMoveDown;
	private LDPanel          pnlLD;
	private OKCancelPanel    pnlActions;
	private Parameters       params;
	private ArrayList<Rule>  rules;
	private int              ruleIndex = -1;
	private Combo            cbRuleType;
	private Button           chkPreserveWS;

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
		// Make a copy (in case of escape)
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

		//--- Options tab
		
		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
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

		Label label = new Label(grpTmp, SWT.NONE);
		label.setText("Beginning of string:");
		edStartString = new Text(grpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edStartString.setLayoutData(gdTmp);

		label = new Label(grpTmp, SWT.NONE);
		label.setText("End of string:");
		edEndString = new Text(grpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edEndString.setLayoutData(gdTmp);

		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Options");
		tiTmp.setControl(cmpTmp);
		
		//--- Rules tab
		
		cmpTmp = new Composite(tfTmp, SWT.NONE);
		layTmp = new GridLayout(3, false);
		cmpTmp.setLayout(layTmp);
		
		lbRules = new List(cmpTmp, SWT.BORDER | SWT.H_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		lbRules.setLayoutData(gdTmp);
		lbRules.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateRule();
			};
		});
		
		//--- Rule properties
		
		Group propGroup = new Group(cmpTmp, SWT.NONE);
		layTmp = new GridLayout(2, false);
		propGroup.setLayout(layTmp);
		propGroup.setText("Rule properties");
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.verticalSpan = 3;
		propGroup.setLayoutData(gdTmp);
		
		Text edExpression = new Text(propGroup, SWT.BORDER | SWT.V_SCROLL);
		edExpression.setEditable(false);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		gdTmp.heightHint = 50;
		edExpression.setLayoutData(gdTmp);
		
		label = new Label(propGroup, SWT.NONE);
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

		//--- end rule properties
		
		int buttonWidth = 80;
		btAdd = new Button(cmpTmp, SWT.PUSH);
		btAdd.setText("Add...");
		gdTmp = new GridData();
		gdTmp.widthHint = buttonWidth;
		btAdd.setLayoutData(gdTmp);
		btAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editRule(true);
			};
		});
		
		btMoveUp = new Button(cmpTmp, SWT.PUSH);
		btMoveUp.setText("Move Up");
		gdTmp = new GridData();
		gdTmp.widthHint = buttonWidth;
		btMoveUp.setLayoutData(gdTmp);
		btMoveUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveUpRule();
			};
		});
		
		btRemove = new Button(cmpTmp, SWT.PUSH);
		btRemove.setText("Remove...");
		gdTmp = new GridData();
		gdTmp.widthHint = buttonWidth;
		btRemove.setLayoutData(gdTmp);
		btRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeRule();
			};
		});
		
		btMoveDown = new Button(cmpTmp, SWT.PUSH);
		btMoveDown.setText("Move Down");
		gdTmp = new GridData();
		gdTmp.widthHint = buttonWidth;
		btMoveDown.setLayoutData(gdTmp);
		btMoveDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveDownRule();
			};
		});
		
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Rules");
		tiTmp.setControl(cmpTmp);
		
		//--- Inline tab
		
		//cmpTmp = new Composite(tfTmp, SWT.NONE);
		//layTmp = new GridLayout();
		//cmpTmp.setLayout(layTmp);
		
/*TODO		m_CFPanel = new CodeFinderPanel(tfTmp, SWT.NONE);
		
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Inline Codes");
		tiTmp.setControl(m_CFPanel);
*/
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( e.widget.getData().equals("h") ) {
					//TODO: Call help
					return;
				}
				if ( e.widget.getData().equals("o") ) saveData();
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btOK);

		setData();
		shell.pack();
		Rectangle Rect = shell.getBounds();
		shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(shell, p_Parent);
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

		ruleIndex = newRuleIndex;
		if ( ruleIndex < 0 ) return;
		
		Rule rule = rules.get(ruleIndex);
		chkPreserveWS.setSelection(rule.preserveSpace());
	}
	
	private void saveRuleData (int index) {
		if ( index < 0 ) return;
		Rule rule = rules.get(index);
		rule.setPreserveSpace(chkPreserveWS.getSelection());
	}
	
	private void updateRuleButtons () {
		int n = lbRules.getSelectionIndex();
		btMoveUp.setEnabled(n > 0);
		btMoveDown.setEnabled((n != -1) && ( n < lbRules.getItemCount()-2 ));
		btRemove.setEnabled(n != -1);
	}
	
	private void editRule (boolean newRule) {
		//TODO
		Rule rule;
		if ( newRule ) {
			rule = new Rule();
			rule.setRuleName("newRule");
		}
		else rule = null; //TODO: get real rule
		
		//EditRuleForm dlg = new EdirRuleForm();
		
		if ( newRule ) {
			rules.add(rule);
			lbRules.add(rule.getRuleName());
			lbRules.select(lbRules.getItemCount()-1);
		}
	}
	
	private void removeRule () {
		
	}
	
	private void moveUpRule () {
		
	}
	
	private void moveDownRule () {
		
	}
	
	private void setData () {
		chkExtractOuterStrings.setSelection(
			"1".equals(params.getParameter("extractOuterStrings")));
		edStartString.setText(params.getParameter("startString"));
		edEndString.setText(params.getParameter("endString"));
		for ( Rule rule : rules ) {
			lbRules.add(rule.getRuleName());
		}
		updateRule();
		updateRuleButtons();
	}
	
	private void saveData () {
		//TODO: validation
		params.setParameter("extractOuterStrings",
			chkExtractOuterStrings.getSelection() ? "1" : "0");
		params.setParameter("startString", edStartString.getText());
		params.setParameter("endString", edEndString.getText());
		result = true;
	}
	
}

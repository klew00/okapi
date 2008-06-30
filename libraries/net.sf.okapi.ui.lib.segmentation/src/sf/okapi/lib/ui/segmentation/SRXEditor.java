package sf.okapi.lib.ui.segmentation;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.lib.segmentation.LanguageMap;
import net.sf.okapi.lib.segmentation.Rule;
import net.sf.okapi.lib.segmentation.Segmenter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class SRXEditor {

	private Shell            shell;
	private Text             edSample;
	private Text             edResults;
	private Table            tblRules;
	private RulesTableModel  rulesTableMod;
	private Combo            cbGroup;
	private Segmenter        segmenter;
	

	public SRXEditor (Shell parent) {
		segmenter = new Segmenter();
		//TODO: normal load
		//start test
		ArrayList<Rule> langRule = new ArrayList<Rule>();
		langRule.add(new Rule("Mr\\.", "\\s", false));
		segmenter.addLanguageRule("french", langRule);
		langRule = new ArrayList<Rule>();
		langRule.add(new Rule("\\b\\w{2,}[\\.\\?!]+[\"\'”\\)]?", "\\s", true));
		langRule.add(new Rule("\\.\\.\\.", "\\s", true));
		langRule.add(new Rule("[Ee][Tt][Cc]\\.", ".", false));
		segmenter.addLanguageRule("default", langRule);
		segmenter.addLanguageMap(new LanguageMap("[Ff][Rr].*", "french"));
		segmenter.addLanguageMap(new LanguageMap(".*", "default"));
		segmenter.setCascade(true);
		segmenter.selectLanguageRule("fr");
		//end test
		
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		//TODO: Externalize string
		shell.setText("Edit Segmentation Rules");
		shell.setImage(parent.getImage());
		shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout(5, false);
		cmpTmp.setLayout(layTmp);
		
		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText("Language rule currently displayed:");
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		label.setLayoutData(gdTmp);
		
		cbGroup = new Combo(cmpTmp, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		cbGroup.setLayoutData(gdTmp);
		cbGroup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateRules();
			};
		});
		
		int topButtonsWidth = 150;
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
		
		tblRules = new Table(cmpTmp, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		tblRules.setHeaderVisible(true);
		tblRules.setLinesVisible(true);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 5;
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
		btTmp = new Button(cmpGroup, SWT.PUSH);
		btTmp.setText("&Add...");
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btTmp.setLayoutData(gdTmp);
		
		btTmp = new Button(cmpGroup, SWT.PUSH);
		btTmp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btTmp.setText("&Edit...");
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btTmp.setLayoutData(gdTmp);

		btTmp = new Button(cmpGroup, SWT.PUSH);
		btTmp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btTmp.setText("&Remove");
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btTmp.setLayoutData(gdTmp);

		btTmp = new Button(cmpGroup, SWT.PUSH);
		btTmp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btTmp.setText("Move &Up");
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btTmp.setLayoutData(gdTmp);

		btTmp = new Button(cmpGroup, SWT.PUSH);
		btTmp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btTmp.setText("Move &Down");
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btTmp.setLayoutData(gdTmp);

		cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);
		
		label = new Label(cmpTmp, SWT.None);
		label.setText("Sample text (use <x>...</x> and <x/> for inline codes):");
		
		int sampleMinHeight = 40;
		edSample = new Text(cmpTmp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		gdTmp.minimumHeight = sampleMinHeight;
		edSample.setLayoutData(gdTmp);

		edResults = new Text(cmpTmp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		edResults.setLayoutData(gdTmp);
		gdTmp.minimumHeight = sampleMinHeight;
		edResults.setEditable(false);

		edSample.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateResults();
			}
		});
		edSample.setText("Hello <x>Mr. Gandalf.</x>");

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Dialogs.centerWindow(shell, parent);
	}
	
	public void showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
	}
	
	private void updateResults () {
		//TODO: update result box
		String text = edSample.getText();
		segmenter.segment(text);
		StringBuilder tmp = new StringBuilder();
		ArrayList<Integer> list = segmenter.getSplitPositions();
		tmp.append(String.format("%d: ", list.size()+1));
		int start = 0;
		for ( int pos : list ) {
			tmp.append("["+text.substring(start, pos)+"] ");
			start = pos;
		}
		// Last one
		tmp.append("["+text.substring(start)+"]");
		edResults.setText(tmp.toString());
	}
	
	private void fillLanguageRuleList () {
		cbGroup.removeAll();
		LinkedHashMap<String, ArrayList<Rule>> langRules = segmenter.getAllLanguageRules();
		for ( String ruleName : langRules.keySet() ) {
			cbGroup.add(ruleName);
		}
	}
	
	private void updateRules () {
		rulesTableMod.setLanguageRules(
			segmenter.getLanguageRules(cbGroup.getText()));
		rulesTableMod.updateTable();
	}
	
	private void editGroupsAndOptions () {
		fillLanguageRuleList();
		if ( cbGroup.getItemCount() > 0 ) {
			cbGroup.select(0);
		}
		updateRules();
	}
}

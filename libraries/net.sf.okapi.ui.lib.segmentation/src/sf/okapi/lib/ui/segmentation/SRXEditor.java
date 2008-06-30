package sf.okapi.lib.ui.segmentation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Stack;

import net.sf.okapi.common.resource.CodeFragment;
import net.sf.okapi.common.resource.Container;
import net.sf.okapi.common.resource.IContainer;
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
	private IContainer       sampleText;
	private Button           btSRXDocs;
	private Menu             popupSRCDocs;
	

	public SRXEditor (Shell parent) {
		segmenter = new Segmenter();
		sampleText = new Container();
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
				updateRules();
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
				if (( popupSRCDocs != null ) && ( !popupSRCDocs.isVisible() )) {
					Rectangle bounds = btSRXDocs.getBounds(); 
					Point menuLoc = btSRXDocs.getParent().toDisplay(
						bounds.x, bounds.y + bounds.height);
					popupSRCDocs.setLocation(menuLoc.x, menuLoc.y);
					popupSRCDocs.setVisible(true);
				}}
			});
		
		tblRules = new Table(cmpTmp, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
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
	
	private void setFileMenu (Shell shell,
		Button fileButton)
	{
		popupSRCDocs = new Menu(shell, SWT.POP_UP);
		fileButton.setMenu(popupSRCDocs);

		MenuItem menuItem = new MenuItem(popupSRCDocs, SWT.PUSH);
		menuItem.setText("Open SRX Document...");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				loadSRXDocument(null);
            }
		});

		menuItem = new MenuItem(popupSRCDocs, SWT.PUSH);
		menuItem.setText("Save Current Document");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//TODO: save doc
            }
		});

		menuItem = new MenuItem(popupSRCDocs, SWT.PUSH);
		menuItem.setText("Save Current Document As...");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//TODO: save doc as
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
	
	private void processInlineCodes () {
		try {
			String text = edSample.getText().replaceAll("<x>", String.valueOf((char)IContainer.CODE_OPENING));
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
	}
	
	private void updateResults () {
		// Convert the sample field content into an parsed item
		processInlineCodes();
		
		String oriText = edSample.getText();
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
		try {
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
	
	private void updateAll () {
		fillLanguageRuleList();
		if ( cbGroup.getItemCount() > 0 ) {
			cbGroup.select(0);
		}
		updateRules();
	}
	
	private void loadSRXDocument (String path) {
		try {
			if ( path == null ) {
				String[] paths = Dialogs.browseFilenames(shell, "Open SRX Document",
					false, null, "SRX Documents (*.srx)", "*.srx");
				if ( paths != null ) path = paths[0];
			}
			segmenter.loadRules(path);
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
		finally {
			fillLanguageRuleList();
			updateAll();
		}
	}
}

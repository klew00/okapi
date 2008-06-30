package sf.okapi.lib.ui.segmentation;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.lib.segmentation.Rule;
import net.sf.okapi.lib.segmentation.Segmenter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class GroupsAndOptionsDialog {

	private Shell            shell;
	private List             lbLangRules;
	private List             lbLangMaps;
	private Segmenter        segmenter;
	

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
		gdTmp.verticalSpan = 5;
		lbLangRules.setLayoutData(gdTmp);
		
		int buttonWidth = 80;
		Button btTmp = new Button(grpTmp, SWT.PUSH);
		btTmp.setText("Add...");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = buttonWidth;
		btTmp.setLayoutData(gdTmp);
		
		btTmp = new Button(grpTmp, SWT.PUSH);
		btTmp.setText("Edit...");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = buttonWidth;
		btTmp.setLayoutData(gdTmp);
		
		btTmp = new Button(grpTmp, SWT.PUSH);
		btTmp.setText("Remove...");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = buttonWidth;
		btTmp.setLayoutData(gdTmp);
		
		btTmp = new Button(grpTmp, SWT.PUSH);
		btTmp.setText("Move Up");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = buttonWidth;
		btTmp.setLayoutData(gdTmp);
		
		btTmp = new Button(grpTmp, SWT.PUSH);
		btTmp.setText("Move Down");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = buttonWidth;
		btTmp.setLayoutData(gdTmp);

		//=== Language Maps
		
		grpTmp = new Group(shell, SWT.NONE);
		grpTmp.setText("Language Maps");
		gdTmp = new GridData(GridData.FILL_BOTH);
		grpTmp.setLayoutData(gdTmp);
		layTmp = new GridLayout(2, false);
		grpTmp.setLayout(layTmp);
		
		lbLangMaps = new List(grpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.verticalSpan = 3;
		lbLangMaps.setLayoutData(gdTmp);

		btTmp = new Button(grpTmp, SWT.PUSH);
		btTmp.setText("Add...");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = buttonWidth;
		btTmp.setLayoutData(gdTmp);

		btTmp = new Button(grpTmp, SWT.PUSH);
		btTmp.setText("Rename...");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = buttonWidth;
		btTmp.setLayoutData(gdTmp);

		btTmp = new Button(grpTmp, SWT.PUSH);
		btTmp.setText("Remove...");
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gdTmp.widthHint = buttonWidth;
		btTmp.setLayoutData(gdTmp);
		
		fillLanguageRules();
		
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
	
	private void fillLanguageRules () {
		lbLangRules.removeAll();
		LinkedHashMap<String, ArrayList<Rule>> list = segmenter.getAllLanguageRules();
		for ( String ruleName : list.keySet() ) {
			lbLangRules.add(ruleName);
		}
	}
}

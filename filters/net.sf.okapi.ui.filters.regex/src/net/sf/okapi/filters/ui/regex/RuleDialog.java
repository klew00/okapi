package net.sf.okapi.filters.ui.regex;

import java.util.regex.Pattern;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.filters.regex.Rule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class RuleDialog {
	
	private Shell            shell;
	private Text             edStart;
	private Text             edEnd;
	private Text             edNameStart;
	private Text             edNameEnd;
	private Text             edNameFormat;
	private boolean          result = false;
	private Rule             rule = null;
	private OKCancelPanel    pnlActions;


	public RuleDialog (Shell parent,
		String caption,
		Rule rule)
	{
		this.rule = rule;
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		if ( caption != null ) shell.setText(caption);
		shell.setImage(parent.getImage());
		shell.setLayout(new GridLayout());
		
		Group grpTmp = new Group(shell, SWT.NONE);
		grpTmp.setText("Boundaries of the content");
		grpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout(2, false);
		grpTmp.setLayout(layTmp);

		Label label = new Label(grpTmp, SWT.NONE);
		label.setText("Start:");
		
		edStart = new Text(grpTmp, SWT.BORDER | SWT.SINGLE);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edStart.setLayoutData(gdTmp);
		edStart.setText(rule.getStart());
		
		label = new Label(grpTmp, SWT.NONE);
		label.setText("End:");
		
		edEnd = new Text(grpTmp, SWT.BORDER | SWT.SINGLE);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edEnd.setLayoutData(gdTmp);
		edEnd.setText(rule.getEnd());
		
		grpTmp = new Group(shell, SWT.NONE);
		grpTmp.setText("Resource name (inside the start expression)");
		grpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		layTmp = new GridLayout(2, false);
		grpTmp.setLayout(layTmp);

		label = new Label(grpTmp, SWT.NONE);
		label.setText("Before the name:");
		
		edNameStart = new Text(grpTmp, SWT.BORDER | SWT.SINGLE);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edNameStart.setLayoutData(gdTmp);
		edNameStart.setText(rule.getNameStart());
		
		label = new Label(grpTmp, SWT.NONE);
		label.setText("After the name:");
		
		edNameEnd = new Text(grpTmp, SWT.BORDER | SWT.SINGLE);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edNameEnd.setLayoutData(gdTmp);
		edNameEnd.setText(rule.getNameEnd());
		
		label = new Label(grpTmp, SWT.NONE);
		label.setText("Format (optional):");
		
		edNameFormat = new Text(grpTmp, SWT.BORDER | SWT.SINGLE);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edNameFormat.setLayoutData(gdTmp);
		edNameFormat.setText(rule.getNameFormat());
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) {
					//TODO: UIUtil.start(help);
					return;
				}
				if ( e.widget.getData().equals("o") ) {
					if ( !saveData() ) return;
				}
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Dialogs.centerWindow(shell, parent);
	}
	
	public boolean showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

	private boolean saveData () {
		try {
			if (( edStart.getText().length() == 0 )
				&& ( edEnd.getText().length() == 0 )) {
				edStart.selectAll();
				edStart.setFocus();
				return false;
			}
			Pattern.compile(edStart.getText());
			Pattern.compile(edEnd.getText());
			rule.setStart(edStart.getText());
			rule.setEnd(edEnd.getText());
			result = true;
			return result;
		}
		catch ( Exception e) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
			return false;
		}
	}
	
	public Rule getRule () {
		return rule;
	}
}

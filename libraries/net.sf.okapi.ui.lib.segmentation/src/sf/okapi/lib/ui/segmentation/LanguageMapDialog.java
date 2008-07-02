package sf.okapi.lib.ui.segmentation;

import java.util.regex.Pattern;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.lib.segmentation.LanguageMap;
import net.sf.okapi.lib.segmentation.Rule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LanguageMapDialog {
	
	private Shell            shell;
	private Text             edPattern;
	private Text             edRuleName;
	private LanguageMap      result = null;
	private OKCancelPanel    pnlActions;


	public LanguageMapDialog (Shell parent,
		String caption,
		LanguageMap langMap)
	{
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		if ( caption != null ) shell.setText(caption);
		shell.setImage(parent.getImage());
		shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);

		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText("Regular expression describing the language map:");
		
		edPattern = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edPattern.setLayoutData(gdTmp);
		edPattern.setText(langMap.getPattern());
		
		label = new Label(cmpTmp, SWT.NONE);
		label.setText("Name of the language rule associated with the regular expression:");
		
		edRuleName = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edRuleName.setLayoutData(gdTmp);
		edRuleName.setText(langMap.getRuleName());
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = null;
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
	
	public LanguageMap showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

	private boolean saveData () {
		try {
			if (( edPattern.getText().length() == 0 )
				&& ( edRuleName.getText().length() == 0 )) {
				edPattern.selectAll();
				edPattern.setFocus();
				return false;
			}
			Pattern.compile(edPattern.getText());
			result = new LanguageMap(edPattern.getText(), edRuleName.getText());
			return true;
		}
		catch ( Exception e) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
			return false;
		}
	}
}

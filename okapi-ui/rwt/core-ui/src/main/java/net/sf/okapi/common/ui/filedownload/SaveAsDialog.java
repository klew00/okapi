package net.sf.okapi.common.ui.filedownload;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SaveAsDialog {

	private Shell shell;
	private String result;
	private Text text;
	
	public SaveAsDialog (Shell parent, String prompt) {
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("File Download");
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout());
			
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layTmp = new GridLayout(1, false);
		cmpTmp.setLayout(layTmp);
		
//		Label lblSaveToFile = new Label(cmpTmp, SWT.NONE);
//		lblSaveToFile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
//		lblSaveToFile.setText(prompt);
		Label label = new Label(cmpTmp, SWT.NONE);
		GridData gdTmp = new GridData();
		gdTmp.horizontalAlignment = SWT.FILL;
		gdTmp.grabExcessHorizontalSpace = true;
		gdTmp.horizontalSpan = 1;
		label.setLayoutData(gdTmp);
		label.setText(prompt + ":");
		
		text = new Text(cmpTmp, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		//--- Dialog-level buttons

		@SuppressWarnings("serial")
		SelectionAdapter okCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = null;
				
				if ( e.widget.getData().equals("o") ) { //$NON-NLS-1$
					if ( !saveData() ) return;
				}
				shell.close();
			};
		};
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, okCancelActions, false);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 500 ) startSize.x = 500;
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
	}
	
	public String showDialog (String fileName) {
		shell.open();
		if (fileName == null) fileName = "";
		text.setText(Util.getFilename(fileName, true));
		text.setFocus();		
		
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}
	
	private boolean saveData () {
		try {
			result = null;
			if ( text.getText().length() == 0 ) {
				text.selectAll();
				text.setFocus();
				return false;
			}
			result = Util.getFilename(text.getText(), true);
			return true;
		}
		catch ( Exception e) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
			return false;
		}
	}
}

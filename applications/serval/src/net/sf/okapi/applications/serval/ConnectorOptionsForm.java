package net.sf.okapi.applications.serval;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.lib.translation.IQuery;
import net.sf.okapi.lib.translation.ResourceItem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ConnectorOptionsForm {

	private Shell shell;
	private Text edSrcLang;
	private Text edTrgLang;
	private Text edName;
	private Text edConnection;
	private OKCancelPanel pnlActions;
	private ResourceItem resItem;
	private boolean result;
	private Button btGetPath;
	private Text edUser;
	private Text edUserPassword;
	private Text edLogin;
	private Text edLoginPassword;
	private Text edServer;
	private Text edPort;
	
	public ConnectorOptionsForm (Shell parent) {
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("Translation Resource Options");
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout(2, false));
		
		Label stTmp = new Label(shell, SWT.NONE);
		stTmp.setText("Resource name:");
		
		edName = new Text(shell, SWT.BORDER);
		edName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stTmp = new Label(shell, SWT.NONE);
		stTmp.setText("Source language:");
		
		edSrcLang = new Text(shell, SWT.BORDER);
		edSrcLang.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stTmp = new Label(shell, SWT.NONE);
		stTmp.setText("Target language:");
		
		edTrgLang = new Text(shell, SWT.BORDER);
		edTrgLang.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group grpTmp = new Group(shell, SWT.NONE);
		grpTmp.setText("Connection");
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		grpTmp.setLayoutData(gdTmp);
		grpTmp.setLayout(new GridLayout(3, false));
		
		stTmp = new Label(grpTmp, SWT.NONE);
		stTmp.setText("Path:");
		
		edConnection = new Text(grpTmp, SWT.BORDER);
		edConnection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		btGetPath = new Button(grpTmp, SWT.PUSH);
		btGetPath.setText("...");
		
		stTmp = new Label(grpTmp, SWT.NONE);
		stTmp.setText("Server:");

		edServer = new Text(grpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		edServer.setLayoutData(gdTmp);
		
		stTmp = new Label(grpTmp, SWT.NONE);
		stTmp.setText("User name:");

		edUser = new Text(grpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		edUser.setLayoutData(gdTmp);
		
		stTmp = new Label(grpTmp, SWT.NONE);
		stTmp.setText("User password:");

		edUserPassword = new Text(grpTmp, SWT.BORDER);
		edUserPassword.setEchoChar('*');
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		edUserPassword.setLayoutData(gdTmp);
		
		stTmp = new Label(grpTmp, SWT.NONE);
		stTmp.setText("Login name:");

		edLogin = new Text(grpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		edLogin.setLayoutData(gdTmp);
		
		stTmp = new Label(grpTmp, SWT.NONE);
		stTmp.setText("User password:");

		edLoginPassword = new Text(grpTmp, SWT.BORDER);
		edLoginPassword.setEchoChar('*');
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		edLoginPassword.setLayoutData(gdTmp);
		
		stTmp = new Label(grpTmp, SWT.NONE);
		stTmp.setText("Port:");

		edPort = new Text(grpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		edPort.setLayoutData(gdTmp);
		
		// Dialog-level buttons
		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) {
					//TODO
				}
				if ( e.widget.getData().equals("o") ) {
					if ( !checkData() ) return;
				}
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 500 ) startSize.x = 500; 
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
	}
	
	private boolean checkData () {
		if ( edSrcLang.getText().length() == 0 ) {
			return false;
		}
		if ( edTrgLang.getText().length() == 0 ) {
			return false;
		}
		resItem.query.setLanguages(edSrcLang.getText(), edTrgLang.getText());
		resItem.name = edName.getText();
		
		if ( edConnection.isEnabled() ) {
			resItem.connectionString = edConnection.getText();
			if ( resItem.connectionString.length() == 0 ) {
				resItem.enabled = false;
			}
		}
		
		result = true;
		return result;
	}
	
	public boolean showDialog (ResourceItem resItem) {
		this.resItem = resItem;
		edName.setText(resItem.name);
		edSrcLang.setText(resItem.query.getSourceLanguage());
		edTrgLang.setText(resItem.query.getTargetLanguage());
		edConnection.setText((resItem.connectionString == null) ? "" : resItem.connectionString);

		edConnection.setEnabled(resItem.query.hasOption(IQuery.HAS_FILEPATH));
		btGetPath.setEnabled(resItem.query.hasOption(IQuery.HAS_FILEPATH));
		edServer.setEnabled(resItem.query.hasOption(IQuery.HAS_SERVER));
		edUser.setEnabled(resItem.query.hasOption(IQuery.HAS_USER));
		edUserPassword.setEnabled(resItem.query.hasOption(IQuery.HAS_USERPASSWORD));
		edLogin.setEnabled(resItem.query.hasOption(IQuery.HAS_LOGIN));
		edLoginPassword.setEnabled(resItem.query.hasOption(IQuery.HAS_LOGINPASSWORD));
		edPort.setEnabled(resItem.query.hasOption(IQuery.HAS_PORT));

		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

}

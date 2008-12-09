package net.sf.okapi.applications.serval;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.lib.translation.IQuery;
import net.sf.okapi.mt.google.GoogleMTConnector;
import net.sf.okapi.tm.opentran.OpenTranTMConnector;
import net.sf.okapi.tm.trados.TradosTMConnector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class SelectionForm {

	private Shell shell;
	private IQuery result;
	private List lbResources;
	private OKCancelPanel pnlActions;

	public SelectionForm (Shell parent) {
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("New Translation Resource");
		shell.setImage(parent.getImage());
		shell.setLayout(new GridLayout(1, false));
		
		Label stTmp = new Label(shell, SWT.NONE);
		stTmp.setText("Please, selection the type of translation resource to create:");
		
		lbResources = new List(shell, SWT.BORDER | SWT.V_SCROLL);
		lbResources.setLayoutData(new GridData(GridData.FILL_BOTH));

		//TODO: get list and attached data from plugin system
		lbResources.add("Google MT (Internet)");
		lbResources.add("SimpleTM local translation memory file");
		lbResources.add("Open-Tran translation search server (Internet)");
		lbResources.add("Trados TM local translation memory");
		lbResources.setSelection(0);
		
		// Dialog-level buttons
		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = null;
				if ( e.widget.getData().equals("h") ) {
					//TODO
				}
				if ( e.widget.getData().equals("o") ) {
					int n = lbResources.getSelectionIndex();
					switch ( n ) {
					case 0: // Google MT
						result = new GoogleMTConnector();
						break;
//					case 1: // SimpleTM
//						result = new SimpleTMConnector();
//						break;
					case 2: // Open-Tran
						result = new OpenTranTMConnector();
						break;
					case 3: // Trados TM
						result = new TradosTMConnector();
						break;
					}
				}
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.y < 200 ) startSize.y = 200; 
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
	}
	
	public IQuery showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}
}

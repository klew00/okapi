package net.sf.okapi.applications.rainbow.utilities.linebreakconversion;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class Editor implements IParametersEditor {
	
	private Shell                 shell;
	private boolean               result = false;
	private OKCancelPanel         pnlActions;
	private Parameters            params;
	private Button                rdDos;
	private Button                rdUnix;
	private Button                rdMac;


	public boolean edit (IParameters params,
		Object object)
	{
		boolean bRes = false;
		try {
			shell = null;
			this.params = (Parameters)params;
			shell = new Shell((Shell)object, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
			create((Shell)object);
			return showDialog();
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
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
	
	private void create (Shell parent)
	{
		shell.setText("Convert Line-Breaks");
		if ( parent != null ) shell.setImage(parent.getImage());
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);

		TabFolder tfTmp = new TabFolder(shell, SWT.NONE);
		tfTmp.setLayoutData(new GridData(GridData.FILL_BOTH));

		//--- Options tab

		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout());
		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Options");
		tiTmp.setControl(cmpTmp);

		Group group = new Group(cmpTmp, SWT.NONE);
		group.setLayout(new RowLayout(SWT.VERTICAL));
		group.setText("New type of line-break");
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		rdDos = new Button(group, SWT.RADIO);
		rdDos.setText("DOS/Windows (Carriage-Return + Line-Feed, \\r\\n, 0x0D+0x0A)");
		rdUnix = new Button(group, SWT.RADIO);
		rdUnix.setText("Unix/Linux (Line-Feed, \\n, 0x0A)");
		rdMac = new Button(group, SWT.RADIO);
		rdMac.setText("Macintosh (Carriage-Return, \\r, 0x0D)");

		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) {
					//TODO: Call help
					return;
				}
				if ( e.widget.getData().equals("o") ) saveData();
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btOK);

		setData();
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Dialogs.centerWindow(shell, parent);
	}
	
	private boolean showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

	private void setData () {
		if ( params.getParameter("lineBreak").equals(Util.LINEBREAK_MAC) )
			rdMac.setSelection(true);
		else if ( params.getParameter("lineBreak").equals(Util.LINEBREAK_UNIX) )
			rdUnix.setSelection(true);
		else
			rdDos.setSelection(true);
	}

	private boolean saveData () {
		if ( rdDos.getSelection() )
			params.setParameter("lineBreak", Util.LINEBREAK_DOS);
		else if ( rdUnix.getSelection() )
			params.setParameter("lineBreak", Util.LINEBREAK_UNIX);
		else
			params.setParameter("lineBreak", Util.LINEBREAK_MAC);
		result = true;
		return true;
	}
	
}

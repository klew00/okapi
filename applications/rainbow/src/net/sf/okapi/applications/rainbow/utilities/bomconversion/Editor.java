package net.sf.okapi.applications.rainbow.utilities.bomconversion;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class Editor implements IParametersEditor {
	
	private Shell                 shell;
	private boolean               result = false;
	private OKCancelPanel         pnlActions;
	private Parameters            params;
	private Button                rdAdd;
	private Label                 stAdd;
	private Button                rdRemove;
	private Label                 stRemove;


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
		shell.setText("Convert Byte-Order-Marks");
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
		group.setLayout(new GridLayout());
		group.setText("Action on the Byte-Order-Mark");
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		int indent = 16;
		rdRemove = new Button(group, SWT.RADIO);
		rdRemove.setText("Remove the Byte-Order-Mark if it is present");
		stRemove = new Label(group, SWT.NONE);
		stRemove.setText("Only UTF-8 files with BOM are modified.");
		GridData gdTmp = new GridData();
		gdTmp.horizontalIndent = indent;
		stRemove.setLayoutData(gdTmp);

		rdAdd = new Button(group, SWT.RADIO);
		rdAdd.setText("Add the Byte-Order-Mark if it is not already present");
		stAdd = new Label(group, SWT.NONE);
		stAdd.setText("IMPORTANT: The input files without BOM are assumed to be already in UTF-8.");
		gdTmp = new GridData();
		gdTmp.horizontalIndent = indent;
		stAdd.setLayoutData(gdTmp);

		rdRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateNotes();
			}
		});
		rdRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateNotes();
			}
		});

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
	
	private void updateNotes () {
		stRemove.setEnabled(rdRemove.getSelection());
		stAdd.setEnabled(rdAdd.getSelection());
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
		rdRemove.setSelection(params.getParameter("removeBOM").equals("1"));
		rdAdd.setSelection(!rdRemove.getSelection());
		updateNotes();
	}

	private boolean saveData () {
		params.setParameter("removeBOM", (rdRemove.getSelection() ? "1" : "0"));
		result = true;
		return result;
	}
	
}

/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
============================================================================*/

package net.sf.okapi.filters.json.ui;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.filters.InlineCodeFinderPanel;
import net.sf.okapi.filters.json.Parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

@EditorFor(Parameters.class)
public class Editor implements IParametersEditor {
	
	private Shell shell;
	private boolean result = false;
	private OKCancelPanel pnlActions;
	private Parameters params;
	private Button chkExtractStandalone;
	private Button rdExtractAllPairs;
	private Button rdDontExtractPairs;
	private Text edExceptions;
	private Button chkUseCodeFinder;
	private InlineCodeFinderPanel pnlCodeFinder;
	private IHelp help;

	public boolean edit (IParameters params,
		boolean readOnly,
		IContext context)
	{
		help = (IHelp)context.getObject("help");
		boolean bRes = false;
		shell = null;
		this.params = (Parameters)params;
		try {
			shell = new Shell((Shell)context.getObject("shell"), SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
			create((Shell)context.getObject("shell"), readOnly);
			return showDialog();
		}
		catch ( Exception E ) {
			Dialogs.showError(shell, E.getLocalizedMessage(), null);
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
	
	private void create (Shell parent,
		boolean readOnly)
	{
		shell.setText(Res.getString("EditorCaption"));
		if ( parent != null ) shell.setImage(parent.getImage());
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);

		TabFolder tfTmp = new TabFolder(shell, SWT.NONE);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		tfTmp.setLayoutData(gdTmp);

		//--- Options tab
		
		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);
		
		Group grpTmp = new Group(cmpTmp, SWT.NONE);
		layTmp = new GridLayout();
		grpTmp.setLayout(layTmp);
		grpTmp.setText(Res.getString("grpStandaloneStrings"));
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		grpTmp.setLayoutData(gdTmp);
		
		chkExtractStandalone = new Button(grpTmp, SWT.CHECK);
		chkExtractStandalone.setText(Res.getString("chkExtractStandalone"));
		
		grpTmp = new Group(cmpTmp, SWT.NONE);
		grpTmp.setLayout(new GridLayout());
		grpTmp.setText(Res.getString("grpKeyValuePairs"));
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		grpTmp.setLayoutData(gdTmp);
		
		rdExtractAllPairs = new Button(grpTmp, SWT.RADIO);
		rdExtractAllPairs.setText(Res.getString("rdExtractAllPairs"));
		rdDontExtractPairs = new Button(grpTmp, SWT.RADIO);
		rdDontExtractPairs.setText(Res.getString("rdDontExtractPairs"));
		
		Label label = new Label(grpTmp, SWT.NONE);
		label.setText(Res.getString("stExceptions"));
		
		edExceptions = new Text(grpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edExceptions.setLayoutData(gdTmp);
		
		
		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText(Res.getString("tabOptions"));
		tiTmp.setControl(cmpTmp);
		
		//--- Inline tab
		
		cmpTmp = new Composite(tfTmp, SWT.NONE);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);
		
		chkUseCodeFinder = new Button(cmpTmp, SWT.CHECK);
		chkUseCodeFinder.setText("Has inline codes as defined below:");
		chkUseCodeFinder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateInlineCodes();
			};
		});
		
		pnlCodeFinder = new InlineCodeFinderPanel(cmpTmp, SWT.NONE);
		pnlCodeFinder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Inline Codes");
		tiTmp.setControl(cmpTmp);
			

		//--- Output tab
		
		/*cmpTmp = new Composite(tfTmp, SWT.NONE);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);
		
		grpTmp = new Group(cmpTmp, SWT.NONE);
		layTmp = new GridLayout();
		grpTmp.setLayout(layTmp);
		grpTmp.setText(Res.getString("grpExtendedChars"));
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		grpTmp.setLayoutData(gdTmp);

		chkEscapeExtendedChars = new Button(grpTmp, SWT.CHECK);
		chkEscapeExtendedChars.setText(Res.getString("chkEscapeExtendedChars"));
		
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText(Res.getString("tabOutput"));
		tiTmp.setControl(cmpTmp);*/
		
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) {
					if ( help != null ) help.showWiki("JSON Filter");
					return;
				}
				if ( e.widget.getData().equals("o") ) {
					if ( !saveData() ) return;
					result = true;
				}
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		pnlActions.setLayoutData(gdTmp);
		pnlActions.btOK.setEnabled(!readOnly);
		if ( !readOnly ) {
			shell.setDefaultButton(pnlActions.btOK);
		}

		shell.pack();
		Rectangle Rect = shell.getBounds();
		shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(shell, parent);
		setData();
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
		chkExtractStandalone.setSelection(params.getExtractStandalone());
		rdExtractAllPairs.setSelection(params.getExtractAllPairs());
		rdDontExtractPairs.setSelection(!params.getExtractAllPairs());
		edExceptions.setText(params.getExceptions()==null ? "" : params.getExceptions());
		chkUseCodeFinder.setSelection(params.getUseCodeFinder());
		pnlCodeFinder.setRules(params.getCodeFinderData());
		
		updateInlineCodes();
		pnlCodeFinder.updateDisplay();
	}

	// Returns null if expression is OK, a message if it is not.
	private String checkExceptionsSyntax () {
		try {
			String tmp = edExceptions.getText();
			if ( Util.isEmpty(tmp) ) return null;
			Pattern.compile(tmp);
			params.setExceptions(tmp);
		}
		catch ( PatternSyntaxException e ) {
			return e.getMessage();
		}
		return null;
	}
	
	private boolean saveData () {
		if ( chkUseCodeFinder.getSelection() ) {
			if ( pnlCodeFinder.getRules() == null ) {
				return false;
			}
			else {
				params.setCodeFinderData(pnlCodeFinder.getRules());
			}
		}
		String tmp = checkExceptionsSyntax();
		if ( tmp != null ) {
			edExceptions.selectAll();
			edExceptions.setFocus();
			Dialogs.showError(shell, tmp, null);
			return false;
		}

		params.setUseCodeFinder(chkUseCodeFinder.getSelection());
		params.setExceptions(edExceptions.getText());
		params.setExtractStandalone(chkExtractStandalone.getSelection());
		params.setExtractAllPairs(rdExtractAllPairs.getSelection());
		return true;
	}
	
	private void updateInlineCodes () {
		pnlCodeFinder.setEnabled(chkUseCodeFinder.getSelection());
	}

}

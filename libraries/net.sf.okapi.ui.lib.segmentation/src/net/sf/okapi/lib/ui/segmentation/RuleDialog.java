/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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
===========================================================================*/

package net.sf.okapi.lib.ui.segmentation;

import java.util.regex.Pattern;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.lib.segmentation.Rule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class RuleDialog {
	
	private Shell shell;
	private Text edBefore;
	private Text edAfter;
	private Button rdBreak;
	private Button rdNoBreak;
	private Rule result = null;
	private String helpPath;

	public RuleDialog (Shell parent,
		Rule rule,
		String helpPath)
	{
		this.helpPath = helpPath;
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(Res.getString("ruleDlg.caption")); //$NON-NLS-1$
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);

		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText(Res.getString("ruleDlg.beforeLabel")); //$NON-NLS-1$
		
		edBefore = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edBefore.setLayoutData(gdTmp);
		edBefore.setText(rule.getBefore());
		
		label = new Label(cmpTmp, SWT.NONE);
		label.setText(Res.getString("ruleDlg.afterLabel")); //$NON-NLS-1$
		
		edAfter = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edAfter.setLayoutData(gdTmp);
		edAfter.setText(rule.getAfter());
		
		label = new Label(cmpTmp, SWT.NONE);
		label.setText(Res.getString("ruleDlg.actionLabel")); //$NON-NLS-1$
		
		rdBreak = new Button(cmpTmp, SWT.RADIO);
		rdBreak.setText(Res.getString("ruleDlg.isBreak")); //$NON-NLS-1$
		gdTmp = new GridData();
		int indent = 20;
		gdTmp.horizontalIndent = indent;
		rdBreak.setLayoutData(gdTmp);

		rdNoBreak = new Button(cmpTmp, SWT.RADIO);
		rdNoBreak.setText(Res.getString("ruleDlg.notBreak")); //$NON-NLS-1$
		gdTmp = new GridData();
		gdTmp.horizontalIndent = indent;
		rdNoBreak.setLayoutData(gdTmp);

		rdBreak.setSelection(rule.isBreak());
		rdNoBreak.setSelection(!rule.isBreak());
		
		//--- Dialog-level buttons

		SelectionAdapter okCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = null;
				if ( e.widget.getData().equals("h") ) { //$NON-NLS-1$
					callHelp();
					return;
				}
				if ( e.widget.getData().equals("o") ) { //$NON-NLS-1$
					if ( !saveData() ) return;
				}
				shell.close();
			};
		};
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, okCancelActions, true);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Dialogs.centerWindow(shell, parent);
	}
	
	public Rule showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

	private boolean saveData () {
		try {
			if (( edBefore.getText().length() == 0 )
				&& ( edAfter.getText().length() == 0 )) {
				edBefore.selectAll();
				edBefore.setFocus();
				return false;
			}
			Pattern.compile(edBefore.getText());
			Pattern.compile(edAfter.getText());
			result = new Rule(edBefore.getText(), edAfter.getText(), rdBreak.getSelection());
			return true;
		}
		catch ( Exception e) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
			return false;
		}
	}

	public void callHelp () {
		if ( helpPath != null ) UIUtil.start(helpPath);
	}

}

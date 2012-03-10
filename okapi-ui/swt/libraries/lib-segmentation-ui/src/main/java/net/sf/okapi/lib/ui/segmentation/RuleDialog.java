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

import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.lib.segmentation.ICURegex;
import net.sf.okapi.lib.segmentation.Rule;
import net.sf.okapi.lib.segmentation.SRXDocument;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
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
	private IHelp help;
	private Text edComments;
	private Label label_1;
	private Label label_2;
	private Composite composite;
	private GridData gdTmp_1;
	private ICURegex icuRegex;

	public RuleDialog (Shell parent,
		Rule rule,
		IHelp helpParam)
	{
		icuRegex = new ICURegex(); 
		help = helpParam;
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(Res.getString("ruleDlg.caption")); //$NON-NLS-1$
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout());
		
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout();
		layTmp.numColumns = 2;
		cmpTmp.setLayout(layTmp);

		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText(Res.getString("ruleDlg.beforeLabel")); //$NON-NLS-1$
		
		label = new Label(cmpTmp, SWT.NONE);
		label.setText(Res.getString("ruleDlg.afterLabel"));
		
		edBefore = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edBefore.setLayoutData(gdTmp);
		
		edAfter = new Text(cmpTmp, SWT.BORDER | SWT.SINGLE);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edAfter.setLayoutData(gdTmp);
		int indent = 20;
		
		composite = new Composite(cmpTmp, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		
		label_1 = new Label(composite, SWT.NONE);
		label_1.setText(Res.getString("ruleDlg.actionLabel"));
				
		rdBreak = new Button(composite, SWT.RADIO);
		GridData gd_rdBreak = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_rdBreak.horizontalIndent = indent;
		rdBreak.setLayoutData(gd_rdBreak);
		rdBreak.setText(Res.getString("ruleDlg.isBreak"));
						
		rdBreak.setSelection(rule.isBreak());
								
		rdNoBreak = new Button(composite, SWT.RADIO);
		GridData gd_rdNoBreak = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_rdNoBreak.horizontalIndent = indent;
		rdNoBreak.setLayoutData(gd_rdNoBreak);
		rdNoBreak.setText(Res.getString("ruleDlg.notBreak"));
		rdNoBreak.setSelection(!rule.isBreak());
		
		label_2 = new Label(cmpTmp, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		label_2.setText(Res.getString("RuleDialog.comments"));

		edComments = new Text(cmpTmp, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		gdTmp_1 = new GridData(GridData.FILL_BOTH);
		gdTmp_1.horizontalSpan = 2;
		gdTmp_1.heightHint = 60;
		edComments.setLayoutData(gdTmp_1);
		edComments.setText(rule.getComment()==null ? "" : rule.getComment()); //$NON-NLS-1$

		//--- Dialog-level buttons

		SelectionAdapter okCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = null;
				if ( e.widget.getData().equals("h") ) { //$NON-NLS-1$
					if ( help != null ) help.showWiki("Ratel - Edit Rule");
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
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 600 ) startSize.x = 600; 
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);

		// Set the text after the resize
		edAfter.setText(rule.getAfter());
		edBefore.setText(rule.getBefore());
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
			// We are just testing the syntax here, so no need to do more that replace
			// the ANYCODE by something valid
			Pattern.compile(icuRegex.processRule(edBefore.getText().replace(SRXDocument.ANYCODE, SRXDocument.INLINECODE_PATTERN)));
			Pattern.compile(icuRegex.processRule(edAfter.getText().replace(SRXDocument.ANYCODE, SRXDocument.INLINECODE_PATTERN)));
			// The patterns pass: create the new rule
			result = new Rule(edBefore.getText(), edAfter.getText(), rdBreak.getSelection());
			result.setComment(edComments.getText());
			return true;
		}
		catch ( Exception e) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
			return false;
		}
	}

}

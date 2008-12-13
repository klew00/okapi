/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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

package net.sf.okapi.filters.ui.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.filters.regex.Rule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class RuleDialog {
	
	private Shell shell;
	private Text edStart;
	private Text edEnd;
	private Text edSample;
	private Text edResult;
	private Text edNameStart;
	private Text edNameEnd;
	private Text edNameFormat;
	private boolean result = false;
	private Pattern fullPattern;
	private Pattern startPattern;
	private Pattern endPattern;
	private Rule rule = null;
	private int regexOptions;


	public RuleDialog (Shell parent,
		String caption,
		Rule rule,
		int regexOptions)
	{
		this.rule = rule;
		this.regexOptions = regexOptions;
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
		edStart.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateResults();
			}
		});
		
		label = new Label(grpTmp, SWT.NONE);
		label.setText("End:");
		
		edEnd = new Text(grpTmp, SWT.BORDER | SWT.SINGLE);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edEnd.setLayoutData(gdTmp);
		edEnd.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateResults();
			}
		});
		
		edSample = new Text(grpTmp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		gdTmp.heightHint = 64;
		edSample.setLayoutData(gdTmp);
		edSample.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateResults();
			}
		});

		edResult = new Text(grpTmp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		gdTmp.heightHint = 64;
		edResult.setLayoutData(gdTmp);
		edResult.setEditable(false);
		
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
		
		label = new Label(grpTmp, SWT.NONE);
		label.setText("After the name:");
		
		edNameEnd = new Text(grpTmp, SWT.BORDER | SWT.SINGLE);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edNameEnd.setLayoutData(gdTmp);
		
		label = new Label(grpTmp, SWT.NONE);
		label.setText("Format:");
		
		edNameFormat = new Text(grpTmp, SWT.BORDER | SWT.SINGLE);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edNameFormat.setLayoutData(gdTmp);
		
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
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		setData();
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

	private void updateResults () {
		try {
			fullPattern = Pattern.compile("("+edStart.getText()+")(.*?)("+edEnd.getText()+")",
				regexOptions);
			Matcher m1 = fullPattern.matcher(getSampleText());
			if ( m1.find() ) {
				startPattern = Pattern.compile(edStart.getText(), regexOptions);
				Matcher m2 = startPattern.matcher(m1.group());
				if ( m2.find() ) {
					endPattern = Pattern.compile(edEnd.getText(), regexOptions);
					Matcher m3 = endPattern.matcher(m1.group());
					if ( m3.find(m2.end()) ) {
						edResult.setText("start=[" + m2.group() + "]\ncontent=["
							+ m1.group().substring(m2.end(), m3.start())
							+ "]\nend=[" + m3.group() + "]");
					}
				}
			}
			else edResult.setText("<No match>");
		}
		catch ( Throwable e ) {
			edResult.setText("Error: "+e.getMessage());
		}
	}

	private String getSampleText() {
		// Change different line breaks type into \n cases
		String tmp = edSample.getText();
		tmp = tmp.replaceAll("\r\r\n", "\n");
		tmp = tmp.replace("\r\n", "\n");
		return tmp.replace("\r", "\n"); 
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
			Pattern.compile(edNameStart.getText());
			Pattern.compile(edNameEnd.getText());
			rule.setStart(edStart.getText());
			rule.setEnd(edEnd.getText());
			rule.setNameStart(edNameStart.getText());
			rule.setNameEnd(edNameEnd.getText());
			rule.setNameFormat(edNameFormat.getText());
			rule.setSample(getSampleText());
			result = true;
			return result;
		}
		catch ( Exception e) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
			return false;
		}
	}
	
	public void setData () {
		edStart.setText(rule.getStart());
		edEnd.setText(rule.getEnd());
		edNameFormat.setText(rule.getNameFormat());
		edNameEnd.setText(rule.getNameEnd());
		edNameStart.setText(rule.getNameStart());
		edSample.setText(rule.getSample());
	}
	
	public Rule getRule () {
		return rule;
	}
}

/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel and the Okapi Framework contributors     */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.ui.filters;

import net.sf.okapi.common.filters.InlineCodeFinder;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.ui.Dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

/**
 * Implements a panel for editing rules to capture in-line codes with
 * the {@link net.sf.okapi.common.filters.InlineCodeFinder} class.
 */
public class InlineCodeFinderPanel extends Composite {

	private InlineCodeFinder codeFinder;
	private List             lbRules;
	private Text             edExpression;
	private Text             edSample;
	private Text             edResults;
	private Button           btMoveUp;
	private Button           btModify;
	private Button           btDiscard;
	private Button           btInsertPattern;
	private Button           btAdd;
	private Button           btRemove;
	private Button           btMoveDown;
	private Button           chkTestAllRules;
	private boolean          editMode;
	private boolean          wasNew;
	private TextContainer    textCont;
	private GenericContent   genericCont;
	

	public InlineCodeFinderPanel (Composite parent,
		int flags)
	{
		super(parent, flags);
		codeFinder = new InlineCodeFinder();
		textCont = new TextContainer();
		genericCont = new GenericContent();
		createContent();
	}
	
	private void createContent () {
		GridLayout layTmp = new GridLayout(5, false);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);
		
		Composite cmpRules = new Composite(this, SWT.NONE);
		layTmp = new GridLayout(2, false);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		cmpRules.setLayout(layTmp);
		GridData gdTmp = new GridData(GridData.FILL_VERTICAL);
		gdTmp.verticalSpan = 4;
		cmpRules.setLayoutData(gdTmp);
		
		lbRules = new List(cmpRules, SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		//gdTmp.verticalSpan = 4;
		gdTmp.grabExcessVerticalSpace = true;
		lbRules.setLayoutData(gdTmp);
		lbRules.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateDisplay();
			};
		});
		
		int normalButtonWidth = 80;
		int largeButtonWidth = 80;

		// Buttons for the rules list

		btAdd = new Button(cmpRules, SWT.PUSH);
		btAdd.setText("Add");
		gdTmp = new GridData();
		gdTmp.widthHint = normalButtonWidth;
		btAdd.setLayoutData(gdTmp);
		btAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				startEditMode(true);
			};
		});
	
		btMoveUp = new Button(cmpRules, SWT.PUSH);
		btMoveUp.setText("Move Up");
		gdTmp = new GridData();
		gdTmp.widthHint = normalButtonWidth;
		btMoveUp.setLayoutData(gdTmp);
		btMoveUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveUp();
			};
		});
		
		btRemove = new Button(cmpRules, SWT.PUSH);
		btRemove.setText("Remove");
		gdTmp = new GridData();
		gdTmp.widthHint = normalButtonWidth;
		btRemove.setLayoutData(gdTmp);
		btRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeExpression();
			};
		});

		btMoveDown = new Button(cmpRules, SWT.PUSH);
		btMoveDown.setText("Move Down");
		gdTmp = new GridData();
		gdTmp.widthHint = normalButtonWidth;
		btMoveDown.setLayoutData(gdTmp);
		btMoveDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveDown();
			};
		});

		// Expression sides
		
		edExpression = new Text(this, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		gdTmp.heightHint = 60;
		edExpression.setLayoutData(gdTmp);
		edExpression.setEditable(false);
		edExpression.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateTest();
			}
		});

		btModify = new Button(this, SWT.PUSH);
		btModify.setText("Modify");
		gdTmp = new GridData();
		gdTmp.widthHint = largeButtonWidth;
		btModify.setLayoutData(gdTmp);
		btModify.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( editMode ) endEditMode(true);
				else startEditMode(false);
			};
		});
		
		btDiscard = new Button(this, SWT.PUSH);
		btDiscard.setText("Discard");
		gdTmp = new GridData();
		gdTmp.widthHint = largeButtonWidth;
		btDiscard.setLayoutData(gdTmp);
		btDiscard.setEnabled(false);
		btDiscard.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				endEditMode(false);
			};
		});
		
		btInsertPattern = new Button(this, SWT.PUSH);
		btInsertPattern.setText("Patterns...");
		gdTmp = new GridData();
		gdTmp.widthHint = largeButtonWidth;
		btInsertPattern.setLayoutData(gdTmp);
		btInsertPattern.setEnabled(false);
		btInsertPattern.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO
				Dialogs.showError(getShell(), "Not implemented yet", null);
			};
		});
		
		chkTestAllRules = new Button(this, SWT.CHECK);
		chkTestAllRules.setText("Test using all rules");
		gdTmp = new GridData();
		chkTestAllRules.setLayoutData(gdTmp);
		chkTestAllRules.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateTest();
			};
		});
		
		edSample = new Text(this, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		gdTmp.heightHint = 60;
		edSample.setLayoutData(gdTmp);
		edSample.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateTest();
			}
		});

		edResults = new Text(this, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		gdTmp.heightHint = 60;
		edResults.setLayoutData(gdTmp);
		edResults.setEditable(false);

		updateDisplay();
	}
	
	private void moveUp () {
		int n = lbRules.getSelectionIndex();
		if ( n < 1 ) return;
		// Move in the rules array
		String tmp = lbRules.getItem(n);
		lbRules.setItem(n, lbRules.getItem(n-1));
		lbRules.setItem(n-1, tmp);
		lbRules.select(n-1);
		updateDisplay();
	}
	
	private void moveDown () {
		int n = lbRules.getSelectionIndex();
		if ( n == -1 ) return;
		String tmp = lbRules.getItem(n);
		lbRules.setItem(n, lbRules.getItem(n+1));
		lbRules.setItem(n+1, tmp);
		lbRules.select(n+1);
		updateDisplay();
	}
	
	private void startEditMode (boolean createNew) {
		try {
			wasNew = createNew;
			if ( createNew ) {
				lbRules.add("");
				lbRules.setSelection(lbRules.getItemCount()-1);
				updateDisplay();
			}
			int n = lbRules.getSelectionIndex();
			if ( n == -1 ) return;
			toggleMode(true);
		}
		catch ( Throwable e) {
			Dialogs.showError(getShell(), e.getMessage(), null);
		}
	}
	
	private void endEditMode (boolean accept) {
		if ( accept ) {
			lbRules.setItem(lbRules.getSelectionIndex(), edExpression.getText());
		}
		else {
			if ( wasNew ) removeExpression();
		}
		toggleMode(false);
	}
	
	private void toggleMode (boolean editMode) {
		this.editMode = editMode;
		lbRules.setEnabled(!editMode);
		edExpression.setEditable(editMode);
		btDiscard.setEnabled(editMode);
		btInsertPattern.setEnabled(editMode);
		btAdd.setEnabled(!editMode);
		
		if ( editMode ) {
			btRemove.setEnabled(false);
			btMoveUp.setEnabled(false);
			btMoveDown.setEnabled(false);
			btModify.setEnabled(true);
			btModify.setText("Accept");
			edExpression.setFocus();
		}
		else {
			btModify.setText("Modify");
			updateDisplay();
		}
	}
	
	public void removeExpression () {
		int n = lbRules.getSelectionIndex();
		if ( n == -1 ) return;
		lbRules.remove(n);
		if ( n >= lbRules.getItemCount() ) n = lbRules.getItemCount()-1;
		if ( n > -1 ) lbRules.setSelection(n);
		updateDisplay();
	}
	
	public void updateDisplay () {
		int n = lbRules.getSelectionIndex();
		btRemove.setEnabled(n>-1);
		btMoveUp.setEnabled(n>0);
		btMoveDown.setEnabled(n<lbRules.getItemCount()-1);
		btModify.setEnabled(n>-1);
		if ( n == -1 ) edExpression.setText("");
		else edExpression.setText(lbRules.getItem(n));
		updateTest();
	}
	
	private void updateTest () {
		try {
			int n = lbRules.getSelectionIndex();
			if ( n == -1 ) return;
			codeFinder.getRules().clear();
			if ( chkTestAllRules.getSelection() ) {
				for ( String pattern : lbRules.getItems() ) {
					codeFinder.addRule(pattern);
				}
				codeFinder.getRules().set(n, edExpression.getText());
			}
			else {
				codeFinder.addRule(edExpression.getText());
			}
			codeFinder.compile();
			textCont.clear();
			textCont.setCodedText(getSampleText());
			codeFinder.process(textCont);
			genericCont.setContent(textCont);
			edResults.setText(genericCont.toString());
		}
		catch ( Throwable e ) {
			edResults.setText(e.getMessage());
		}
	}
	
	private String getSampleText () {
		// Change different line breaks type into \n cases
		String tmp = edSample.getText();
		tmp = tmp.replaceAll("\r\r\n", "\n");
		tmp = tmp.replace("\r\n", "\n");
		return tmp.replace("\r", "\n"); 
	}
	
	public void setData (String codeFinderRules) {
		codeFinder.fromString(codeFinderRules);
		lbRules.removeAll();
		for ( String pattern : codeFinder.getRules() ) {
			lbRules.add(pattern);
		}
		edSample.setText(codeFinder.getSample());
		chkTestAllRules.setSelection(codeFinder.useAllRulesWhenTesting());
		if ( lbRules.getItemCount() > 0 ) {
			lbRules.setSelection(0);
			updateDisplay();
		}
	}

	public String getData () {
		codeFinder.getRules().clear();
		for ( String pattern : lbRules.getItems() ) {
			codeFinder.addRule(pattern);
		}
		codeFinder.setSample(getSampleText());
		codeFinder.setUseAllRulesWhenTesting(chkTestAllRules.getSelection());
		return codeFinder.toString();
	}
	
	public void enable (boolean enabled) {
		this.setEnabled(enabled);
		btAdd.setEnabled(enabled);
		if ( enabled ) {
			updateDisplay();
		}
		else {
			btDiscard.setEnabled(false);
			btRemove.setEnabled(false);
			btMoveUp.setEnabled(false);
			btMoveDown.setEnabled(false);
			btModify.setEnabled(false);
		}
	}
}

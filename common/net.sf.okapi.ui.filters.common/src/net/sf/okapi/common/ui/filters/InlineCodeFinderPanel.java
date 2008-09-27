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
import net.sf.okapi.common.ui.Dialogs;

import org.eclipse.swt.SWT;
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
	private Button           btTest;
	private Button           btAdd;
	private Button           btRemove;
	private Button           btMoveDown;
	private boolean          editMode;
	private boolean          wasNew;
	

	public InlineCodeFinderPanel (Composite parent,
		int flags)
	{
		super(parent, flags);
		codeFinder = new InlineCodeFinder();
		createContent();
	}
	
	private void createContent () {
		GridLayout layTmp = new GridLayout(5, false);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);
		
		lbRules = new List(this, SWT.BORDER | SWT.V_SCROLL);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 1;
		gdTmp.verticalSpan = 3;
		gdTmp.grabExcessVerticalSpace = true;
		lbRules.setLayoutData(gdTmp);
		lbRules.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateDisplay();
			};
		});
		
		edExpression = new Text(this, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		gdTmp.heightHint = 40;
		edExpression.setLayoutData(gdTmp);
		edExpression.setEditable(false);

		int normalButtonWidth = 80;
		int largeButtonWidth = 80;
		
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
		
		btTest = new Button(this, SWT.PUSH);
		btTest.setText("Test");
		gdTmp = new GridData();
		gdTmp.widthHint = largeButtonWidth;
		btTest.setLayoutData(gdTmp);
		btTest.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO
				Dialogs.showError(getShell(), "Not implemented yet", null);
			};
		});
		
		edSample = new Text(this, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		gdTmp.heightHint = 40;
		edSample.setLayoutData(gdTmp);

		// Panel for rules list buttons
		Composite cmpRulesButtons = new Composite(this, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		cmpRulesButtons.setLayout(layout);
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_END | GridData.FILL_HORIZONTAL);
		cmpRulesButtons.setLayoutData(gdTmp);
		
		btAdd = new Button(cmpRulesButtons, SWT.PUSH);
		btAdd.setText("Add");
		gdTmp = new GridData();
		gdTmp.widthHint = normalButtonWidth;
		btAdd.setLayoutData(gdTmp);
		btAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				startEditMode(true);
			};
		});
	
		btMoveUp = new Button(cmpRulesButtons, SWT.PUSH);
		btMoveUp.setText("Move Up");
		gdTmp = new GridData();
		gdTmp.widthHint = normalButtonWidth;
		btMoveUp.setLayoutData(gdTmp);
		btMoveUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveUp();
			};
		});
		
		btRemove = new Button(cmpRulesButtons, SWT.PUSH);
		btRemove.setText("Remove");
		gdTmp = new GridData();
		gdTmp.widthHint = normalButtonWidth;
		btRemove.setLayoutData(gdTmp);
		btRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeExpression();
			};
		});

		btMoveDown = new Button(cmpRulesButtons, SWT.PUSH);
		btMoveDown.setText("Move Down");
		gdTmp = new GridData();
		gdTmp.widthHint = normalButtonWidth;
		btMoveDown.setLayoutData(gdTmp);
		btMoveDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveDown();
			};
		});

		edResults = new Text(this, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		//gdTmp.heightHint = 40;
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
	}
	
	public void setData (String codeFinderRules) {
		codeFinder.fromString(codeFinderRules);
		lbRules.removeAll();
		for ( String pattern : codeFinder.getRules() ) {
			lbRules.add(pattern);
		}
		if ( lbRules.getItemCount() > 0 ) {
			lbRules.setSelection(0);
			updateDisplay();
		}
		edSample.setText(codeFinder.getSample());
	}

	public String getData () {
		codeFinder.getRules().clear();
		for ( String pattern : lbRules.getItems() ) {
			codeFinder.addRule(pattern);
		}
		codeFinder.setSample(edSample.getText());
		return codeFinder.toString();
	}
	
	public void enable (boolean enabled) {
		this.setEnabled(enabled);
		btAdd.setEnabled(enabled);
		btTest.setEnabled(enabled);
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

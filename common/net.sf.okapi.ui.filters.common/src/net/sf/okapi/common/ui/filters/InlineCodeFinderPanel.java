/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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

//	private InlineCodeFinder codeFinder;
	private List             lbRules;
	private Text             edExpression;
	private Text             edSample;
	private Text             edResults;
	private Button           btMoveUp;
	private Button           btModify;
	private Button           btDiscard;
	private Button           btInsertPattern;
	private Button           btTest;
	private Button           btRemove;
	private Button           btMoveDown;
	

	public InlineCodeFinderPanel (Composite parent,
		int flags)
	{
		super(parent, flags);
		createContent();
	}
	
	private void createContent () {
		GridLayout layTmp = new GridLayout(6, false);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);
		
		lbRules = new List(this, SWT.BORDER | SWT.V_SCROLL);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		gdTmp.verticalSpan = 4;
		lbRules.setLayoutData(gdTmp);
		
		edExpression = new Text(this, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		gdTmp.heightHint = 40;
		edExpression.setLayoutData(gdTmp);

		int normalButtonWidth = 80;
		int largeButtonWidth = 80;
		
		btModify = new Button(this, SWT.PUSH);
		btModify.setText("Modify");
		gdTmp = new GridData();
		gdTmp.widthHint = largeButtonWidth;
		btModify.setLayoutData(gdTmp);
		btModify.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO:
			};
		});
		
		btDiscard = new Button(this, SWT.PUSH);
		btDiscard.setText("Discard");
		gdTmp = new GridData();
		gdTmp.widthHint = largeButtonWidth;
		btDiscard.setLayoutData(gdTmp);
		btDiscard.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO:
			};
		});
		
		btInsertPattern = new Button(this, SWT.PUSH);
		btInsertPattern.setText("Patterns...");
		gdTmp = new GridData();
		gdTmp.widthHint = largeButtonWidth;
		btInsertPattern.setLayoutData(gdTmp);
		btInsertPattern.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO:
			};
		});
		
		btTest = new Button(this, SWT.PUSH);
		btTest.setText("Test");
		gdTmp = new GridData();
		gdTmp.widthHint = largeButtonWidth;
		btTest.setLayoutData(gdTmp);
		btTest.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO:
			};
		});
		
		edSample = new Text(this, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		gdTmp.heightHint = 40;
		edSample.setLayoutData(gdTmp);

		edResults = new Text(this, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		gdTmp.heightHint = 40;
		edResults.setLayoutData(gdTmp);
		edResults.setEditable(false);
		
		Button btAdd = new Button(this, SWT.PUSH);
		btAdd.setText("Add");
		gdTmp = new GridData();
		gdTmp.widthHint = normalButtonWidth;
		btAdd.setLayoutData(gdTmp);
		btAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO: add();
			};
		});
	
		btMoveUp = new Button(this, SWT.PUSH);
		btMoveUp.setText("Move Up");
		gdTmp = new GridData();
		gdTmp.widthHint = normalButtonWidth;
		btMoveUp.setLayoutData(gdTmp);
		btMoveUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO: moveup
			};
		});
		
		btRemove = new Button(this, SWT.PUSH);
		btRemove.setText("Remove");
		gdTmp = new GridData();
		gdTmp.widthHint = normalButtonWidth;
		btRemove.setLayoutData(gdTmp);
		btRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO: btRemove();
			};
		});

		btMoveDown = new Button(this, SWT.PUSH);
		btMoveDown.setText("Move Down");
		gdTmp = new GridData();
		gdTmp.widthHint = normalButtonWidth;
		btMoveDown.setLayoutData(gdTmp);
		btMoveDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO: btMoveDown
			};
		});

		updateDisplay();
	}
	
	public void updateDisplay () {
		//TODO
	}
	
	public void setOptions (InlineCodeFinder codeFinder) {
//		this.codeFinder = codeFinder;
		lbRules.removeAll();
		for ( String pattern : codeFinder.getRules() ) {
			lbRules.add(pattern);
		}
		if ( lbRules.getItemCount() > 0 ) {
			lbRules.select(0);
			updateDisplay();
		}
		//TODO
		
	}
	
}

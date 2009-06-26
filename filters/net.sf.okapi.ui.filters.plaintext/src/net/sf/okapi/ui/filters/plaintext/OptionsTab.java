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

package net.sf.okapi.ui.filters.plaintext;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.filters.InlineCodeFinderPanel;
import net.sf.okapi.filters.plaintext.common.WrapMode;
import net.sf.okapi.ui.filters.plaintext.common.IParametersEditorPage;
import net.sf.okapi.ui.filters.plaintext.common.SWTUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormLayout;

/**
 * Options tab for plain text and table filters 
 * 
 * @version 0.1, 13.06.2009
 * @author Sergei Vasilyev
 */

public class OptionsTab extends Composite implements IParametersEditorPage {
	private Button btnTrimLeft;
	private Button btnTrimRight;
	private Group grpTextUnitProcessing;
	private Group grpInlineCodes;
	private Button btnProcessInlineCodes;
	private Group grpMultilineTextUnits;
	private Button btnSeparateLines;
	private Button btnUnwrapLines;
	private Button btnCreateInlineCodes;
	private Button btnUnescape;
	private InlineCodeFinderPanel inlineCodeFinderPanel;
	private Button btnAllowTrimming;
	private FormData formData;
	private FormData formData_2;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public OptionsTab(Composite parent, int style) {
		
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		FormData formData_4 = new FormData();
		formData_4.right = new FormAttachment(100, -169);
		
		grpTextUnitProcessing = new Group(this, SWT.NONE);
		grpTextUnitProcessing.setLayout(new FormLayout());
		grpTextUnitProcessing.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpTextUnitProcessing.setText("Text unit processing");
		
		btnAllowTrimming = new Button(grpTextUnitProcessing, SWT.CHECK);
		{
			FormData formData_1 = new FormData();
			formData_1.left = new FormAttachment(0, 10);
			formData_1.right = new FormAttachment(100, -10);
			formData_1.top = new FormAttachment(0, 10);
			btnAllowTrimming.setLayoutData(formData_1);
		}
		btnAllowTrimming.setText("Allow trimming");
		
		btnTrimLeft = new Button(grpTextUnitProcessing, SWT.CHECK);
		{
			FormData formData_1 = new FormData();
			formData_1.left = new FormAttachment(btnAllowTrimming, 10, SWT.LEFT);
			formData_1.top = new FormAttachment(btnAllowTrimming, 6);
			formData_1.right = new FormAttachment(100, -10);
			btnTrimLeft.setLayoutData(formData_1);
		}
		btnTrimLeft.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnTrimLeft.setText("Trim leading spaces and tabs");
		
		btnTrimRight = new Button(grpTextUnitProcessing, SWT.CHECK);
		{
			FormData formData_1 = new FormData();
			formData_1.left = new FormAttachment(btnTrimLeft, 0, SWT.LEFT);
			formData_1.top = new FormAttachment(btnTrimLeft, 6);
			formData_1.right = new FormAttachment(100, -10);
			btnTrimRight.setLayoutData(formData_1);
		}
		btnTrimRight.setText("Trim trailing spaces and tabs");
		
		btnUnescape = new Button(grpTextUnitProcessing, SWT.CHECK);
		{
			FormData formData_1 = new FormData();
			formData_1.left = new FormAttachment(btnAllowTrimming, 0, SWT.LEFT);
			formData_1.top = new FormAttachment(btnTrimRight, 6);
			formData_1.right = new FormAttachment(100, -10);
			btnUnescape.setLayoutData(formData_1);
		}
		btnUnescape.setText("Convert \\t, \\n, \\\\, \\uXXXX into characters            ");
		
		grpMultilineTextUnits = new Group(this, SWT.NONE);
		grpMultilineTextUnits.setLayout(new FormLayout());
		grpMultilineTextUnits.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpMultilineTextUnits.setText("Multi-line text units");
		
		btnSeparateLines = new Button(grpMultilineTextUnits, SWT.RADIO);
		{
			formData = new FormData();
			formData.left = new FormAttachment(0, 10);
			btnSeparateLines.setLayoutData(formData);
		}
		btnSeparateLines.setText("Separate lines with line-feeds (\\n)");
		
		btnUnwrapLines = new Button(grpMultilineTextUnits, SWT.RADIO);
		formData.right = new FormAttachment(btnUnwrapLines, 0, SWT.RIGHT);
		formData.bottom = new FormAttachment(btnUnwrapLines, -6);
		{
			formData_2 = new FormData();
			formData_2.left = new FormAttachment(0, 10);
			formData_2.right = new FormAttachment(100, -10);
			btnUnwrapLines.setLayoutData(formData_2);
		}
		btnUnwrapLines.setText("Unwrap lines (replace line ends with spaces)         ");
		
		btnCreateInlineCodes = new Button(grpMultilineTextUnits, SWT.RADIO);
		formData_2.bottom = new FormAttachment(100, -44);
		{
			FormData formData_1 = new FormData();
			formData_1.left = new FormAttachment(0, 10);
			formData_1.right = new FormAttachment(100, -10);
			formData_1.top = new FormAttachment(btnUnwrapLines, 7);
			formData_1.width = 371;
			btnCreateInlineCodes.setLayoutData(formData_1);
		}
		btnCreateInlineCodes.setText("Create inline codes for line ends");

		grpInlineCodes = new Group(this, SWT.NONE);
		grpInlineCodes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		grpInlineCodes.setLayout(new GridLayout(1, false));
		grpInlineCodes.setText("Inline codes");
		
		btnProcessInlineCodes = new Button(grpInlineCodes, SWT.CHECK);
		btnProcessInlineCodes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				interop();
			}
		});
		btnProcessInlineCodes.setText("Has inline codes as defined below:");
		
		inlineCodeFinderPanel = new InlineCodeFinderPanel(grpInlineCodes, SWT.NONE);
		inlineCodeFinderPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}

	public void interop() {

//		if (btnProcessInlineCodes.getSelection())
//			pnlCodeFinder.enable(true);
//		else
//			SWTUtils.setAllEnabled(pnlCodeFinder, false);
		
		if (btnProcessInlineCodes.getSelection())
			SWTUtils.setAllEnabled(grpInlineCodes, true);
		else {
			if (inlineCodeFinderPanel.inEditMode()) {

				Dialogs.showError(getShell(), "Cannot exit the mode while the rules for inline codes are being edited." +
						"\nPlease accept or discard changes first.", null);
				btnProcessInlineCodes.setSelection(true);
			}
			else
				SWTUtils.setAllEnabled(grpInlineCodes, false);
		}
						
//		SWTUtils.setAllEnabled(grpInlineCodes, btnProcessInlineCodes.getSelection());
		
//		if (btnPreserveWhiteSpaces.getSelection()) {
//			
//			btnTrimLeft.setSelection(false);
//			btnTrimLeft.setEnabled(false);
//			
//			btnTrimRight.setSelection(false);
//			btnTrimRight.setEnabled(false);
//		}
//		else {
//			
//			btnTrimLeft.setEnabled(true);
//			btnTrimRight.setEnabled(true);
//		}		
	}

	public boolean load(IParameters parameters) {
	
		if (parameters instanceof net.sf.okapi.filters.plaintext.base.Parameters) {
			
			net.sf.okapi.filters.plaintext.base.Parameters params = 
				(net.sf.okapi.filters.plaintext.base.Parameters) parameters;
			
			btnUnescape.setSelection(params.unescapeSource);
//			btnPreserveWhiteSpaces.setSelection(params.preserveWS);
			btnTrimLeft.setSelection(params.trimLeft);
			btnTrimRight.setSelection(params.trimRight);
						
			btnProcessInlineCodes.setSelection(params.useCodeFinder);
			inlineCodeFinderPanel.setData(params.codeFinderRules);
			
			switch (params.wrapMode) {
			
				case NONE:
					btnSeparateLines.setSelection(true);
					break;
					
				case SPACES:
					btnUnwrapLines.setSelection(true);
					break;
					
				case PLACEHOLDERS:
					btnCreateInlineCodes.setSelection(true);
					break;					
			}
		}

		return true;		
	}

	public boolean save(IParameters parameters) {
		
		if (parameters instanceof net.sf.okapi.filters.plaintext.base.Parameters) {
		
			net.sf.okapi.filters.plaintext.base.Parameters params = 
				(net.sf.okapi.filters.plaintext.base.Parameters) parameters; 
	
			params.unescapeSource = btnUnescape.getSelection();
//			params.preserveWS = btnPreserveWhiteSpaces.getSelection();
			params.trimLeft = btnTrimLeft.getSelection();
			params.trimRight = btnTrimRight.getSelection();
			
			params.useCodeFinder = btnProcessInlineCodes.getSelection();
			params.codeFinderRules = inlineCodeFinderPanel.getData();
			
			if (btnSeparateLines.getSelection())
				params.wrapMode = WrapMode.NONE;
			
			else if (btnUnwrapLines.getSelection())
				params.wrapMode = WrapMode.SPACES;
			
			else if (btnCreateInlineCodes.getSelection())
				params.wrapMode = WrapMode.PLACEHOLDERS;
		}
						
		return true;
	}

	public boolean canClose(boolean isOK) {
		
		if (inlineCodeFinderPanel.inEditMode()) {

			Dialogs.showError(getShell(), "Cannot close the window while the rules for inline codes are being edited." +
					"\nPlease accept or discard changes first.", null);
			return false;
		}
		return true;
	}
}


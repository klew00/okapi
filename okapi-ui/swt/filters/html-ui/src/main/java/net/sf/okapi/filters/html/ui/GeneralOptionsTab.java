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

package net.sf.okapi.filters.html.ui;

import net.sf.okapi.common.ui.abstracteditor.IDialogPage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

public class GeneralOptionsTab extends Composite implements IDialogPage {
	private Group grpSubFilterOptions;
	private Combo elementSubFilterCombo;
	private Combo cdataSubFilterCombo;
	private Label lblElementSubFilter;
	private Label lblCdataSubFilter;
	private Group grpGuidTagattribute;
	private Label lblTagName;
	private Text txtTagName;
	private Label lblAttributeName;
	private Text txtAttributeName;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GeneralOptionsTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, true));
		
		grpSubFilterOptions = new Group(this, SWT.NONE);
		{
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			gridData.heightHint = 18;
			grpSubFilterOptions.setLayoutData(gridData);
		}
		grpSubFilterOptions.setLayout(new GridLayout(2, false));
		grpSubFilterOptions.setText("Sub Filter Options");
		grpSubFilterOptions.setData("name", "grpSubFilterOptions");
		
		lblElementSubFilter = new Label(grpSubFilterOptions, SWT.NONE);
		{
			GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gridData.widthHint = 135;
			lblElementSubFilter.setLayoutData(gridData);
		}
		lblElementSubFilter.setData("name", "lblElementSubFilter");
		lblElementSubFilter.setText("Element Sub Filter:");
		
		elementSubFilterCombo = new Combo(grpSubFilterOptions, SWT.READ_ONLY);
		elementSubFilterCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		elementSubFilterCombo.setData("name", "elementSubFilterCombo");
		
		lblCdataSubFilter = new Label(grpSubFilterOptions, SWT.NONE);
		{
			GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gridData.widthHint = 132;
			lblCdataSubFilter.setLayoutData(gridData);
		}
		lblCdataSubFilter.setData("name", "lblCdataSubFilter");
		lblCdataSubFilter.setText("CDATA Sub Filter:");
		
		cdataSubFilterCombo = new Combo(grpSubFilterOptions, SWT.READ_ONLY);
		cdataSubFilterCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cdataSubFilterCombo.setData("name", "combo_1");
		
		grpGuidTagattribute = new Group(this, SWT.NONE);
		grpGuidTagattribute.setText("GUID Tag/Attribute");
		grpGuidTagattribute.setLayout(new GridLayout(2, false));
		{
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			gridData.heightHint = 32;
			grpGuidTagattribute.setLayoutData(gridData);
		}
		
		lblTagName = new Label(grpGuidTagattribute, SWT.NONE);
		{
			GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gridData.widthHint = 132;
			lblTagName.setLayoutData(gridData);
		}
		lblTagName.setText("Tag Name:");
		lblTagName.setBounds(0, 0, 135, 13);
		
		txtTagName = new Text(grpGuidTagattribute, SWT.BORDER);
		txtTagName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		txtTagName.setBounds(0, 0, 284, 21);
		
		lblAttributeName = new Label(grpGuidTagattribute, SWT.NONE);
		{
			GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gridData.widthHint = 133;
			lblAttributeName.setLayoutData(gridData);
		}
		lblAttributeName.setText("Attribute Name:");
		lblAttributeName.setBounds(0, 0, 132, 13);
		
		txtAttributeName = new Text(grpGuidTagattribute, SWT.BORDER);
		txtAttributeName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		txtAttributeName.setBounds(0, 0, 284, 21);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public boolean canClose(boolean isOK) {		
		return true;
	}

	public void interop(Widget speaker) {
	}

	public boolean load(Object data) {
		return true;
	}

	public boolean save(Object data) {
		return true;
	}
}

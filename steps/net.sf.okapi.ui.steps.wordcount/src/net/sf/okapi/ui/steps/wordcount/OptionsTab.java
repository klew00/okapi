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

package net.sf.okapi.ui.steps.wordcount;

import net.sf.okapi.steps.wordcount.common.Parameters;
import net.sf.okapi.ui.filters.plaintext.common.IDialogPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;

public class OptionsTab extends Composite implements IDialogPage {
	private Group grpCountInResources;
	private Button btnTextUnits;
	private Button btnBatches;
	private Button btnBatchItems;
	private Button btnDocuments;
	private Button btnSubdocuments;
	private Button btnGroups;
	private Link link;
	private Link link_1;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public OptionsTab(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		grpCountInResources = new Group(this, SWT.NONE);
		grpCountInResources.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpCountInResources.setText("Count in the following resources:");
		grpCountInResources.setLayout(new GridLayout(2, false));
		grpCountInResources.setData("name", "grpCountInResources");
		
		btnTextUnits = new Button(grpCountInResources, SWT.CHECK);
		btnTextUnits.setEnabled(false);
		btnTextUnits.setData("name", "btnTextUnits");
		btnTextUnits.setSelection(true);
		btnTextUnits.setText("Text Units");
		
		link = new Link(grpCountInResources, SWT.NONE);
		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				selectAll(true);
			}
		});
		link.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		link.setData("name", "link");
		link.setText("<a>Select all</a>");
		
		btnBatches = new Button(grpCountInResources, SWT.CHECK);
		btnBatches.setData("name", "btnBatches");
		btnBatches.setText("Batches");
		
		link_1 = new Link(grpCountInResources, SWT.NONE);
		link_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				selectAll(false);
			}
		});
		link_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		link_1.setData("name", "link_1");
		link_1.setText("<a>Unselect all</a>");
		
		btnBatchItems = new Button(grpCountInResources, SWT.CHECK);
		btnBatchItems.setData("name", "btnBatchItems");
		btnBatchItems.setText("Batch Items");
		new Label(grpCountInResources, SWT.NONE);
		
		btnDocuments = new Button(grpCountInResources, SWT.CHECK);
		btnDocuments.setData("name", "btnDocuments");
		btnDocuments.setText("Documents");
		new Label(grpCountInResources, SWT.NONE);
		
		btnSubdocuments = new Button(grpCountInResources, SWT.CHECK);
		btnSubdocuments.setData("name", "btnSubdocuments");
		btnSubdocuments.setText("Sub-documents");
		new Label(grpCountInResources, SWT.NONE);
		
		btnGroups = new Button(grpCountInResources, SWT.CHECK);
		btnGroups.setData("name", "btnGroups");
		btnGroups.setText("Groups");
		new Label(grpCountInResources, SWT.NONE);
		grpCountInResources.setTabList(new Control[]{btnTextUnits, btnBatches, btnBatchItems, btnDocuments, btnSubdocuments, btnGroups, link, link_1});

	}

	public boolean canClose(boolean isOK) {

		return true;
	}

	public void interop(Widget speaker) {

		
	}
	
	public void selectAll(boolean select) {
		
		btnBatches.setSelection(select);
		btnBatchItems.setSelection(select);
		btnDocuments.setSelection(select);
		btnSubdocuments.setSelection(select);
		btnGroups.setSelection(select);
	}

	public boolean load(Object data) {

		if (data instanceof Parameters) {
			
			Parameters params =	(Parameters) data;
			
			btnBatches.setSelection(params.countInBatch);
			btnBatchItems.setSelection(params.countInBatchItems);
			btnDocuments.setSelection(params.countInDocuments);
			btnSubdocuments.setSelection(params.countInSubDocuments);
			btnGroups.setSelection(params.countInGroups);
		}
			
		return true;
	}

	public boolean save(Object data) {

		if (data instanceof Parameters) {
			
			Parameters params = (Parameters) data;
			
			params.countInBatch = btnBatches.getSelection();
			params.countInBatchItems = btnBatchItems.getSelection();
			params.countInDocuments = btnDocuments.getSelection();
			params.countInSubDocuments = btnSubdocuments.getSelection();
			params.countInGroups = btnGroups.getSelection();
		}
		
		return true;
	}

}

/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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

package net.sf.okapi.applications.rainbow.lib;

import java.util.Iterator;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersProvider;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.InputDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Implements a common UI to select a filter settings string.
 */
public class FilterSettingsPanel extends Composite {

	private FilterAccess          fa;
	private Combo                 cbFilters;
	private Combo                 cbParameters;
	private Button                btEdit;
	private Button                btCreate;
	private IParametersProvider   paramsProv;
	private String[]              paramsList;
	
	public FilterSettingsPanel(Composite p_Parent,
		int p_nFlags,
		IParametersProvider paramProv)
	{
		super(p_Parent, SWT.NONE);
		this.paramsProv = paramProv;
		createContent();
	}
	
	private void createContent () {
		GridLayout layTmp = new GridLayout(3, false);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);

		Label label = new Label(this, SWT.NONE);
		label.setText("Filter:");
		
		cbFilters = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		cbFilters.setLayoutData(gdTmp);
		cbFilters.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e1) {
				fillParametersList(0);
			}
			public void widgetDefaultSelected(SelectionEvent e2) {}
		});

		label = new Label(this, SWT.NONE);
		label.setText("Parameters:");
		
		cbParameters = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		cbParameters.setLayoutData(gdTmp);
		cbParameters.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e1) {
				btEdit.setEnabled(!cbParameters.getText().startsWith("<"));
			}
			public void widgetDefaultSelected(SelectionEvent e2) {}
		});

		// place-holder
		new Label(this, SWT.NONE);
		
		int nWidth = 80;
		btEdit = new Button(this, SWT.PUSH);
		btEdit.setText("&Edit...");
		gdTmp = new GridData();
		gdTmp.widthHint = nWidth;
		btEdit.setLayoutData(gdTmp);
		btEdit.addSelectionListener(new SelectionListener () {
			public void widgetSelected(SelectionEvent e) {
				editParameters();
			}
			public void widgetDefaultSelected(SelectionEvent e) {};
		});

		btCreate = new Button(this, SWT.PUSH);
		btCreate.setText("&Create...");
		gdTmp = new GridData();
		gdTmp.widthHint = nWidth;
		btCreate.setLayoutData(gdTmp);
		btCreate.addSelectionListener(new SelectionListener () {
			public void widgetSelected(SelectionEvent e) {
				createParameters();
			}
			public void widgetDefaultSelected(SelectionEvent e) {};
		});

	}
	
	public void setData (String filterSettings,
		FilterAccess fa)
	{
		this.fa = fa;

		// Get the list of available filters
		cbFilters.add("<None>");
		Iterator<String> Iter = fa.getItems().keySet().iterator();
		while ( Iter.hasNext() ) {
			cbFilters.add(Iter.next());
		}

		// Get the list of available parameters
		paramsList = paramsProv.getParametersList();
	
		// Set the current filter
		String[] aRes = paramsProv.splitLocation(filterSettings);
		int n = -1;
		for ( int i=0; i<cbFilters.getItemCount(); i++ ) {
			if ( cbFilters.getItem(i).equals(aRes[1]) ) {
				n = i;
				break;
			}
		}
		cbFilters.select((n>-1) ? n : 0);
		fillParametersList(0);
		
		// Set the current parameters file
		n = -1;
		for ( int i=0; i<cbParameters.getItemCount(); i++ ) {
			if ( cbParameters.getItem(i).equals(filterSettings) ) {
				n = i;
				break;
			}
		}
		if ( n == -1 ) n = 0;
		cbParameters.select(n);
		btEdit.setEnabled(!cbParameters.getText().startsWith("<"));
		btCreate.setEnabled(!cbFilters.getText().startsWith("<"));
	}
	
	public String getData () {
		if ( !cbParameters.getText().startsWith("<") )
			return cbParameters.getText();
		if ( !cbFilters.getText().startsWith("<") )
			return cbFilters.getText();
		return "";
	}
	
	private void fillParametersList (int index) {
		cbParameters.removeAll();
		cbParameters.add("<None>");
		for ( String item : paramsList ) {
			if ( item.startsWith(cbFilters.getText()) ) {
				cbParameters.add(item);
			}
		}
		cbParameters.select(index);
		btEdit.setEnabled(!cbParameters.getText().startsWith("<"));
		btCreate.setEnabled(!cbFilters.getText().startsWith("<"));
	}
	
	private void editParameters () {
		try {
			String filterSettings = getData();
			// Get the components
			String[] aRes = paramsProv.splitLocation(filterSettings);
			if ( filterSettings.length() == 0 ) {
				//TODO: ask user if s/he wants to create new file
				return;
			}
			if ( aRes[2].length() == 0 ) {
				// Cannot edit non-specified parameters file
				Dialogs.showError(getShell(), "A parameters name must be defined.\nFor example: myParams in myFilter@myParams.", null);
				return;
			}

			// Invoke the parameters provider to load the parameters file.
			// We do this like this because the provider may be on the server side.
			IParameters params = paramsProv.load(filterSettings);
			if ( params == null ) {
				Dialogs.showError(getShell(), "Error when trying to load the parameters file.", null);
				return;
			}
			// Now call the editor (from the client side)
			if ( fa.editParameters(aRes[1], params, getParent().getShell()) ) {
				// Save the data if needed
				// We use the provider here to (to save on the server side)
				paramsProv.save(filterSettings, params);
			}
		}
		catch ( Exception e ) {
			Dialogs.showError(getShell(), e.getLocalizedMessage(), null);
		}
	}

	private void createParameters () {
		try {
			String filterSettings;
			while ( true ) {
				InputDialog dlg = new InputDialog(getShell(), "New Parameters",
					"Name:", "myParameters", null);
				String newName = dlg.showDialog();
				if ( newName == null ) return;
				filterSettings = cbFilters.getText() + FilterSettingsMarkers.PARAMETERSSEP + newName;
				boolean found = false;
				for ( String item : cbParameters.getItems() ) {
					if ( item.equalsIgnoreCase(filterSettings) ) {
						found = true;
						break;
					}
				}
				if ( !found ) break; // Name OK
			}
			
			// Get the components
			String[] aRes = paramsProv.splitLocation(filterSettings);

			// Create a default parameters object.
			// We do this like this because the provider may be on the server side.
			IParameters params = paramsProv.createParameters(filterSettings);
			if ( params == null ) {
				Dialogs.showError(getShell(), "Error when trying to create the parameters file.", null);
				return;
			}
			// Now call the editor (from the client side)
			if ( fa.editParameters(aRes[1], params, getParent().getShell()) ) {
				// Save the data if needed
				// We use the provider here to (to save on the server side)
				paramsProv.save(filterSettings, params);
				//TODO: Refresh the list of parameters and set the new one as the selected
			}
		}
		catch ( Exception e ) {
			Dialogs.showError(getShell(), e.getLocalizedMessage(), null);
		}
	}
}

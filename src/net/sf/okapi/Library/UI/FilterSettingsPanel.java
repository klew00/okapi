/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
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

package net.sf.okapi.Library.UI;

import net.sf.okapi.Filter.FilterAccess;
import net.sf.okapi.Library.Base.IParameters;
import net.sf.okapi.Library.Base.IParametersProvider;
import net.sf.okapi.Library.Base.Utils;

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

	private FilterAccess          m_FA;
	private Combo                 cbFilters;
	private Combo                 cbParameters;
	private Button                btEdit;
	private Button                btCreate;
	private IParametersProvider   paramProv;
	
	public FilterSettingsPanel(Composite p_Parent,
		int p_nFlags,
		IParametersProvider paramProv)
	{
		super(p_Parent, SWT.NONE);
		this.paramProv = paramProv;
		createContent();
	}
	
	private void createContent () {
		GridLayout layTmp = new GridLayout(3, false);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);

		Label label = new Label(this, SWT.NONE);
		label.setText("Filter:");
		
		cbFilters = new Combo(this, SWT.DROP_DOWN);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		cbFilters.setLayoutData(gdTmp);

		label = new Label(this, SWT.NONE);
		label.setText("Parameters:");
		
		cbParameters = new Combo(this, SWT.DROP_DOWN);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		cbParameters.setLayoutData(gdTmp);

		new Label(this, SWT.NONE);
		
		int nWidth = 80;
		btEdit = new Button(this, SWT.PUSH);
		btEdit.setText("Edit...");
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
		btCreate.setText("Create...");
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
	
	public void setData (String p_sFSettings,
		FilterAccess p_FA) {
		m_FA = p_FA;
		//TODO: Need to get the filter list from the plugin system
		cbFilters.add("okf_properties");
		cbFilters.add("okf_json");
		
		cbFilters.setText(p_sFSettings);
	}
	
	public String getData () {
		return cbFilters.getText();
	}
	
	private void selectParameters () {
	}
	
	private void editParameters () {
		try {
			String filterSettings = cbFilters.getText();
			// Get the components
			String[] aRes = paramProv.splitLocation(filterSettings);
			if ( filterSettings.length() == 0 ) {
				//TODO: ask user if s/he wants to create new file
				return;
			}
			if ( aRes[2].length() == 0 ) {
				// Cannot edit non-specified parameters file
				Utils.showError("A parameters name must be defined.\nFor example: myParams in myFilter@myParams.", null);
				return;
			}

			// Invoke the parameters provider to load the parameters file.
			// We do this like this because the provider may be on the server side.
			IParameters params = paramProv.load(filterSettings);
			if ( params == null ) {
				Utils.showError("Error when trying to load the parameters file.", null);
				return;
			}
			// Now call the editor (from the client side)
			if ( m_FA.editParameters(aRes[1], params, getParent().getShell()) ) {
				// Save the data if needed
				// We use the provider here to (to save on the server side)
				paramProv.save(filterSettings, params);
			}
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}

	private void createParameters () {
		try {
			String filterSettings = cbFilters.getText();
			// Get the components
			String[] aRes = paramProv.splitLocation(filterSettings);
			if ( aRes[2].length() == 0 ) {
				// Cannot edit non-specified parameters file
				Utils.showError("A parameters name must be defined.\nFor example: myParams in myFilter@myParams.", null);
				return;
			}

			// Create a default parameters object.
			// We do this like this because the provider may be on the server side.
			IParameters params = paramProv.createParameters(filterSettings);
			if ( params == null ) {
				Utils.showError("Error when trying to create the parameters file.", null);
				return;
			}
			// Now call the editor (from the client side)
			if ( m_FA.editParameters(aRes[1], params, getParent().getShell()) ) {
				// Save the data if needed
				// We use the provider here to (to save on the server side)
				paramProv.save(filterSettings, params);
			}
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}
}

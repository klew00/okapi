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

package net.sf.okapi.applications.rainbow.lib;

import java.util.ArrayList;

import net.sf.okapi.applications.rainbow.Project;
import net.sf.okapi.common.BaseContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

/**
 * Implements a common UI to select a filter configuration.
 */
public class FilterConfigSelectionPanel extends Composite {

	private FilterConfigMapper mapper;
	private Combo cbFilters;
	private Text edDescription;
	private List lbConfigs;
	private Button btEdit;
	private Button btCreate;
	private Button btDelete;
	private Button btMore;
	private BaseContext context;
	private ArrayList<FilterInfo> filters;
	private IFilter cachedFilter;
	private Project project;
	
	public FilterConfigSelectionPanel (Composite p_Parent,
		IHelp helpParam,
		int p_nFlags,
		FilterConfigMapper mapper,
		Project project,
		String projectDir)
	{
		super(p_Parent, SWT.NONE);
		context = new BaseContext();
		context.setObject("help", helpParam);
		context.setString("projDir", projectDir);
		context.setObject("shell", getShell());
		this.mapper = mapper;
		this.project = project;
		createContent();
	}
	
	private void createContent () {
		GridLayout layTmp = new GridLayout(4, false);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);

		cbFilters = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		gdTmp.widthHint = 340;
		cbFilters.setLayoutData(gdTmp);
		cbFilters.setVisibleItemCount(15);
		cbFilters.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e1) {
				fillConfigurations(0, null);
			}
			public void widgetDefaultSelected(SelectionEvent e2) {}
		});

		lbConfigs = new List(this, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		gdTmp.heightHint = 60;
		lbConfigs.setLayoutData(gdTmp);
		lbConfigs.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateConfigurationInfo();
            }
		});

		edDescription = new Text(this, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		gdTmp.heightHint = 60;
		edDescription.setLayoutData(gdTmp);
		edDescription.setEditable(false);
		
		int nWidth = 80;
		
		btEdit = new Button(this, SWT.PUSH);
		btEdit.setText("&Edit...");
		btEdit.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent e) {
				editOptions();
			}
		});

		btCreate = new Button(this, SWT.PUSH);
		btCreate.setText("&Create...");
		btCreate.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent e) {
				createParameters();
			}
		});

		btDelete = new Button(this, SWT.PUSH);
		btDelete.setText("&Delete...");
		btDelete.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent e) {
				deleteParameters();
			}
		});

		btMore = new Button(this, SWT.PUSH);
		btMore.setText("&More...");
		btMore.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent e) {
				editAllConfigurations();
			}
		});

		nWidth = UIUtil.getMinimumWidth(nWidth, btEdit, "&View...");
		UIUtil.setSameWidth(nWidth, btEdit, btCreate, btDelete, btMore);
	}
	
	public String getConfigurationId () {
		int n = lbConfigs.getSelectionIndex();
		if ( n < 1 ) return ""; // No configuration
		else return lbConfigs.getItem(n);
	}

	public void setConfigurationId (String configId) {
		// Fill the list of available filters
		// Rely on order (index+1) because we cannot attach object to the items
		cbFilters.removeAll();
		cbFilters.add("<None>");
		filters = mapper.getFilters();
		for ( FilterInfo item : filters ) {
			cbFilters.add(item.toString());
		}
		// Set the current filter
		FilterConfiguration config = mapper.getConfiguration(configId);
		if ( config == null ) {
			// Warn no configuration was found (if we were expecting one)
			if (( configId != null ) && ( configId.length()!=0 )) {
				Dialogs.showError(getShell(),
					String.format("The configuration for the identifier '%s' could not be found.", configId), null);
			}
		}
		setConfiguration(config);
	}

	private void setConfiguration (FilterConfiguration config) {
		int n = -1;
		if ( config != null ) {
			for ( int i=0; i<filters.size(); i++ ) {
				if ( filters.get(i).filterClass.equals(config.filterClass) ) {
					n = i; // Found it 
					break;
				}
			}
			if ( n == -1 ) {
				// Warn that the configuration or filter was not found
				Dialogs.showError(getShell(), String.format(
					"The configuration '%s' or its filter could not be found.",
					config.configId), null);
			}
		}
		
		cbFilters.select((n>-1) ? n+1 : 0); // n+1 to correct for <None> at 0
		fillConfigurations(0, (config==null) ? null : config.configId);
	}
	
	private void editAllConfigurations () {
		try {
			int n = lbConfigs.getSelectionIndex();
			String configId = null;
			if ( n > -1 ) {
				configId = lbConfigs.getItem(n);
			}
			String oldConfigId = configId;
			FilterConfigMapperDialog dlg = new FilterConfigMapperDialog(getShell(), true, project, mapper);
			configId = dlg.showDialog(configId);
			if ( configId == null ) { // Close without selection
				configId = oldConfigId;
			}
			// Update the list of configuration with the new or old selection
			setConfigurationId(configId);
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), e.getMessage(), null);
		}
	}
	
	private void updateConfigurationInfo () {
		int n = lbConfigs.getSelectionIndex();
		String configId = null;
		if ( n > -1 ) configId = lbConfigs.getItem(n);
		if (( configId == null ) || ( configId.length() == 0 )) {
			edDescription.setText("");
			btEdit.setEnabled(false);
			btCreate.setEnabled(false);
			btDelete.setEnabled(false);
		}
		else {
			FilterConfiguration config = mapper.getConfiguration(configId);
			edDescription.setText(config.name + "\n" + config.description);
			if ( config.custom ) btEdit.setText("&Edit...");
			else btEdit.setText("&View...");
			btEdit.setEnabled(true);
			btCreate.setEnabled(true);
			btDelete.setEnabled(config.custom);
		}
	}

	private void fillConfigurations (int index,
		String selectedConfigId)
	{
		// Set default index if we don't find the selected configuration
		if ( selectedConfigId != null ) index = 0;
		
		lbConfigs.removeAll();
		// We should always have at least one configuration,
		// otherwise there would be no filter
		int n = cbFilters.getSelectionIndex();
		if ( n < 1 ) {
			updateConfigurationInfo();
			return; // First is <None>
		}
		n--; // Real index in filters list
		java.util.List<FilterConfiguration> list = mapper.getFilterConfigurations(filters.get(n).filterClass);

		// Fill the list, and detect selected configuration if needed
		int i = 0;
		for ( FilterConfiguration item : list ) {
			lbConfigs.add(item.configId);
			if ( selectedConfigId != null ) {
				if ( selectedConfigId.equals(item.configId) ) index = i;
				i++;
			}
		}

		lbConfigs.setSelection(index);
		updateConfigurationInfo();
	}
	
	private void editOptions () {
		try {
			String configId = getConfigurationId();
			if ( configId == null ) return;
			FilterConfiguration config = mapper.getConfiguration(configId);
			if ( config == null ) return;

			cachedFilter = mapper.createFilter(config.configId, cachedFilter);
			IParametersEditor editor = mapper.createParametersEditor(config.configId, cachedFilter);
			IParameters params = mapper.getParameters(config, cachedFilter);
			// Call the editor
			if ( editor == null ) {
				//TODO
				Dialogs.showError(getShell(), "Editing of filter parameters without editor is not implemented yet.", null);
			}
			else {
				if ( !editor.edit(params, !config.custom, context) ) return;
			}
			// Don't try to save pre-defined parameters
			if ( !config.custom ) return;
			// Else save the modified parameters to custom storage
			mapper.saveCustomParameters(config, params);
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), e.getMessage(), null);
		}
	}

	private void createParameters () {
		try {
			Dialogs.showError(getShell(), "Not implemented yet.", null);
/*			String filterSettings;
			while ( true ) {
				InputDialog dlg = new InputDialog(getShell(), "New Parameters",
					"Name:", "myParameters", null, 0, -1);
				String newName = dlg.showDialog();
				if ( newName == null ) return;
				filterSettings = getFilterId(cbFilters.getText()) + FilterSettingsMarkers.PARAMETERSSEP + newName;
				boolean found = false;
				for ( String item : cbParameters.getItems() ) {
					if ( item.equalsIgnoreCase(filterSettings) ) {
						found = true;
						// Ask confirmation for overwriting
						MessageBox confDlg = new MessageBox(getParent().getShell(),
							SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
						confDlg.setMessage(String.format("The parameters file '%s' exists already.\n"
							+"Do you want to overwrite it?", filterSettings));
						confDlg.setText("Rainbow");
						found = (confDlg.open()!=SWT.YES);
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
			context.setObject("shell", getParent().getShell());
			if ( fa.editParameters(aRes[1], params, context, aRes[3]) ) {
				// Save the data if needed
				// We use the provider here to (to save on the server side)
				paramsProv.save(filterSettings, params);
				// Refresh the list of parameters and set the new one as the selected
				paramsList = paramsProv.getParametersList();
				fillParametersList(-1, filterSettings);
			}
*/		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), e.getMessage(), null);
		}
	}

	private void deleteParameters () {
		try {
			String configId = getConfigurationId();
			if ( configId == null ) return;
			FilterConfiguration config = mapper.getConfiguration(configId);
			if ( !config.custom ) return; // Cannot delete pre-defined configurations

			// Ask confirmation
			MessageBox dlg = new MessageBox(getParent().getShell(),
				SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
			dlg.setMessage(String.format("This command will delete permanently the configuration '%s'.\n"
				+"Do you want to proceed with the deletion?", configId));
			dlg.setText("Rainbow");
			switch  ( dlg.open() ) {
			case SWT.NO:
			case SWT.CANCEL:
				return;
			}
			// Else: delete the configuration
			mapper.deleteCustomParameters(config);
			mapper.removeConfiguration(configId);
			// Refresh the list of parameters
			fillConfigurations(0, null);
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), e.getMessage(), null);
		}
	}
}

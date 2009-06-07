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

import net.sf.okapi.common.BaseContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersProvider;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.InputDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

/**
 * Implements a common UI to select a filter settings string.
 */
public class FilterSettingsPanel extends Composite {

	private FilterAccess fa;
	private Combo cbFilters;
	private Text edDescription;
	private Combo cbParameters;
	private Button btEdit;
	private Button btCreate;
	private Button btDelete;
	private IParametersProvider paramsProv;
	private String[] paramsList;
	private BaseContext context;
	
	public FilterSettingsPanel (Composite p_Parent,
		IHelp helpParam,
		int p_nFlags,
		IParametersProvider paramProv,
		String projectDir)
	{
		super(p_Parent, SWT.NONE);
		context = new BaseContext();
		context.setObject("help", helpParam);
		context.setString("projDir", projectDir);
		this.paramsProv = paramProv;
		createContent();
	}
	
	private void createContent () {
		GridLayout layTmp = new GridLayout(4, false);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);

		Label label = new Label(this, SWT.NONE);
		label.setText("Filter:");
		
		cbFilters = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 3;
		gdTmp.widthHint = 340;
		cbFilters.setLayoutData(gdTmp);
		cbFilters.setVisibleItemCount(15);
		cbFilters.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e1) {
				fillParametersList(0, null);
			}
			public void widgetDefaultSelected(SelectionEvent e2) {}
		});

		new Label(this, SWT.NONE); // Place-holder
		
		edDescription = new Text(this, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 3;
		gdTmp.heightHint = 60;
		edDescription.setLayoutData(gdTmp);
		edDescription.setEditable(false);
		
		label = new Label(this, SWT.NONE);
		label.setText("Parameters:");
		
		cbParameters = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 3;
		cbParameters.setLayoutData(gdTmp);
		cbParameters.setVisibleItemCount(15);
		cbParameters.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				btEdit.setEnabled(!cbParameters.getText().startsWith("<"));
				btDelete.setEnabled(btEdit.getEnabled());
            }
		});

		new Label(this, SWT.NONE); // Place-holder
		
		int nWidth = 80;
		btEdit = new Button(this, SWT.PUSH);
		btEdit.setText("&Edit...");
		gdTmp = new GridData();
		gdTmp.widthHint = nWidth;
		btEdit.setLayoutData(gdTmp);
		btEdit.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent e) {
				editParameters();
			}
		});

		btCreate = new Button(this, SWT.PUSH);
		btCreate.setText("&Create...");
		gdTmp = new GridData();
		gdTmp.widthHint = nWidth;
		btCreate.setLayoutData(gdTmp);
		btCreate.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent e) {
				createParameters();
			}
		});

		btDelete = new Button(this, SWT.PUSH);
		btDelete.setText("&Delete...");
		gdTmp = new GridData();
		gdTmp.widthHint = nWidth;
		btDelete.setLayoutData(gdTmp);
		btDelete.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected(SelectionEvent e) {
				deleteParameters();
			}
		});

/*For later		Composite comp = new Composite(this, SWT.NONE);
		layTmp = new GridLayout(2, false);
		layTmp.marginWidth = 0;
		comp.setLayout(layTmp);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		comp.setLayoutData(gdTmp);
		
		edParamsDir = new Text(comp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		edParamsDir.setLayoutData(gdTmp);
		edParamsDir.setEditable(false);
*/		
	}
	
	public void setData (String filterSettings,
		FilterAccess fa)
	{
		this.fa = fa;
		// Get the list of available filters
		cbFilters.add("<None>");
		for ( FilterAccessItem item : fa.getItems().values() ) {
			cbFilters.add(item.toString());
		}

		// Get the list of available parameters
		paramsList = paramsProv.getParametersList();
	
		// Set the current filter
		String[] aRes = paramsProv.splitLocation(filterSettings);
		int n = -1;
		for ( int i=0; i<cbFilters.getItemCount(); i++ ) {
			String name = getFilterName(cbFilters.getItem(i));
			if ( name.equals(aRes[1]) ) {
				n = i;
				break;
			}
		}
		cbFilters.select((n>-1) ? n : 0);
		fillParametersList(0, null);
		
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
		btDelete.setEnabled(btEdit.getEnabled());
		btCreate.setEnabled(!cbFilters.getText().startsWith("<"));
	}
	
	public String getData () {
		if ( !cbParameters.getText().startsWith("<") )
			return cbParameters.getText();
		if ( !cbFilters.getText().startsWith("<") )
			return getFilterName(cbFilters.getText());
		return "";
	}

	private String getFilterName (String listEntry) {
		int pos = listEntry.indexOf('[');
		if ( pos == -1 ) return listEntry;
		return listEntry.substring(pos+1, listEntry.length()-1);
	}
	
	private void fillParametersList (int index,
		String selection)
	{
		if ( selection != null ) index = 0;
		
		cbParameters.removeAll();
		cbParameters.add("<Defaults>");
		String filterName = getFilterName(cbFilters.getText());
		int i = 1;
		for ( String item : paramsList ) {
			if ( item.startsWith(filterName) ) {
				cbParameters.add(item);
				if ( selection != null ) {
					if ( selection.equals(item) ) index = i;
				}
				i++;
			}
		}
		
		FilterAccessItem item = fa.getItems().get(filterName);
		if ( item == null ) edDescription.setText("");
		else edDescription.setText(item.description);
		
		cbParameters.select(index);
		btEdit.setEnabled(!cbParameters.getText().startsWith("<"));
		btDelete.setEnabled(btEdit.getEnabled());
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
			// Now call the editor (from the UI side)
			context.setObject("shell", getParent().getShell());
			if ( fa.editParameters(aRes[1], params, context, aRes[3]) ) {
				// Save the data if needed
				// We use the provider here to (to save on the server side)
				paramsProv.save(filterSettings, params);
			}
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), e.getLocalizedMessage(), null);
		}
	}

	private void createParameters () {
		try {
			String filterSettings;
			while ( true ) {
				InputDialog dlg = new InputDialog(getShell(), "New Parameters",
					"Name:", "myParameters", null, 0, -1);
				String newName = dlg.showDialog();
				if ( newName == null ) return;
				filterSettings = getFilterName(cbFilters.getText()) + FilterSettingsMarkers.PARAMETERSSEP + newName;
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
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), e.getLocalizedMessage(), null);
		}
	}

	private void deleteParameters () {
		try {
			String filterSettings = getData();
			// Ask confirmation
			MessageBox dlg = new MessageBox(getParent().getShell(),
				SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
			dlg.setMessage(String.format("This command will delete the filter parameters %s\n"
				+"Do you want to proceed with the deletion?", filterSettings));
			dlg.setText("Rainbow");
			switch  ( dlg.open() ) {
			case SWT.NO:
			case SWT.CANCEL:
				return;
			}
			// Else: delete the parameters
			if ( !paramsProv.deleteParameters(filterSettings) ) {
				Dialogs.showError(getShell(),
					String.format("Could not delete %s", filterSettings), null);
			}
			// Refresh the list of parameters
			paramsList = paramsProv.getParametersList();
			fillParametersList(0, null);
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), e.getLocalizedMessage(), null);
		}
	}
}

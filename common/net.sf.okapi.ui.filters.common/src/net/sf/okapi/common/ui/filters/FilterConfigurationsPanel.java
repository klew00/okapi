/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.ui.filters;

import net.sf.okapi.common.BaseContext;
import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * Default panel for the creation, edit and selection of filter configurations.
 */
public class FilterConfigurationsPanel extends Composite {

	private Table table;
	private FilterConfigurationsTableModel model;
	private Text edFilter;
	private Text edDescription;
	private Button btOptions;
	private Button btEdit;
	private Button btCreate;
	private Button btDelete;
	private IFilterConfigurationMapper mapper;
	private IFilter cachedFilter;
	private IContext context;

	/**
	 * Creates a FilterConfigurationsPanel object for a given parent with a given style.
	 * @param parent the parent of the panel to create.
	 * @param style the style of the panel.
	 */
	public FilterConfigurationsPanel (Composite parent,
		int style)
	{
		super(parent, style);
		createContent();
		context = new BaseContext();
		context.setObject("shell", getShell());
	}

	/**
	 * Sets the implementation of {@link IFilterConfigurationMapper} for this panel. 
	 * @param mapper the mapper to use with this panel.
	 * @param configId the optional configuration identifier to select,
	 * or null to select the first configuration in the list.
	 */
	public void setData (IFilterConfigurationMapper mapper,
		String configId)
	{
		this.mapper = mapper;
		model.setMapper(mapper);
		model.updateTable(0);

		// Try to select the configuration
		if ( configId != null ) { // try to get the index of the configuration
			for ( int i=0; i<table.getItemCount(); i++ ) {
				if ( configId.equals(
					table.getItem(i).getText(FilterConfigurationsTableModel.ID_COLINDEX)) ) {
					table.setSelection(i);
					break;
				}
			}
		}
		updateInfo();
	}
	
	/**
	 * Gets the identifier of the configuration currently selected.
	 * @return the configuration identifier of the current selection,
	 * or null if there no configuration is selected.  
	 */
	@Override
	public String getData () {
		int n = table.getSelectionIndex();
		if ( n == -1 ) return null;
		return table.getItem(n).getText(FilterConfigurationsTableModel.ID_COLINDEX);
	}
	
	private void createContent () {
		GridLayout layTmp = new GridLayout(4, false);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);

		table = new Table(this, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		table.setLayoutData(gdTmp);
		table.addControlListener(new ControlAdapter() {
		    public void controlResized(ControlEvent e) {
		    	Rectangle rect = table.getClientArea();
		    	int whidthCol3 = 80;
				int nPart = (int)((rect.width-whidthCol3) / 100);
				table.getColumn(0).setWidth(26*nPart);
				table.getColumn(1).setWidth(40*nPart);
				table.getColumn(2).setWidth(34*nPart);
				table.getColumn(3).setWidth(whidthCol3);
		    }
		});
		table.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				//editRule(false);
			}
			public void mouseDown(MouseEvent e) {}
			public void mouseUp(MouseEvent e) {}
		});
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateInfo();
			};
		});
		
		model = new FilterConfigurationsTableModel();
		model.linkTable(table);
		
		edFilter = new Text(this, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		edFilter.setLayoutData(gdTmp);
		edFilter.setEditable(false);
	
		edDescription = new Text(this, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		gdTmp.heightHint = 40;
		edDescription.setLayoutData(gdTmp);
		edDescription.setEditable(false);
		
		btOptions = new Button(this, SWT.PUSH);
		btOptions.setText("Options...");
		btOptions.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editParameters();
			};
		});
		
		btEdit = new Button(this, SWT.PUSH);
		btEdit.setText("Edit...");
		
		btCreate = new Button(this, SWT.PUSH);
		btCreate.setText("Create...");
		btCreate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				createConfiguration();
			};
		});
		
		btDelete = new Button(this, SWT.PUSH);
		btDelete.setText("Delete...");
		btDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				deleteConfiguration();
			};
		});
		
		UIUtil.setSameWidth(80, btOptions, btEdit, btCreate, btDelete);
	}

	private void updateInfo () {
		int n = table.getSelectionIndex();
		if ( n > -1 ) {
			FilterConfiguration config = mapper.getConfiguration(
				table.getItem(n).getText(FilterConfigurationsTableModel.ID_COLINDEX));
			if ( config != null ) {
				edFilter.setText(config.filterClass);
				edDescription.setText(config.description);
				btOptions.setEnabled(true); //config.parameters != null);
				btEdit.setEnabled(config.custom);
				btDelete.setEnabled(config.custom);
				btCreate.setEnabled(true);
				return;
			}
		}
		// Otherwise:
		edFilter.setText("");
		edDescription.setText("");
		btEdit.setEnabled(false);
		btOptions.setEnabled(false);
		btCreate.setEnabled(false);
		btDelete.setEnabled(false);
	}
	
	private void editParameters () {
		try {
			int n = table.getSelectionIndex();
			if ( n == -1 ) return;
			FilterConfiguration config = mapper.getConfiguration(
				table.getItem(n).getText(FilterConfigurationsTableModel.ID_COLINDEX));
			if ( config == null ) return;
			cachedFilter = mapper.createFilter(config.configId, cachedFilter);
			IParametersEditor editor = mapper.createParametersEditor(config.configId, cachedFilter);
			IParameters params = mapper.getParameters(config, cachedFilter);
			// Call the editor
			if ( editor == null ) {
				//TODO
			}
			else {
				if ( !editor.edit(params, context) ) return;
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
	
	private void deleteConfiguration () {
		try {
			int n = table.getSelectionIndex();
			if ( n == -1 ) return;
			String id = table.getItem(n).getText(FilterConfigurationsTableModel.ID_COLINDEX);
			FilterConfiguration config = mapper.getConfiguration(id);
			if ( !config.custom ) return; // Cannot delete pre-defined configurations
			
			// Ask confirmation
			MessageBox dlg = new MessageBox(getParent().getShell(),
				SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
			dlg.setMessage(String.format("This command will delete permanently the configuration '%s'.\n"
				+"Do you want to proceed with the deletion?", id));
			dlg.setText("Rainbow");
			switch  ( dlg.open() ) {
			case SWT.NO:
			case SWT.CANCEL:
				return;
			}

			// Else: Do delete the item
			mapper.deleteCustomParameters(config);
			mapper.removeConfiguration(id);
			model.updateTable(n);
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), e.getMessage(), null);
		}
	}
	
	private void createConfiguration () {
		try {
			int n = table.getSelectionIndex();
			if ( n == -1 ) return;
			//String id = table.getItem(n).getText(FilterConfigurationsTableModel.ID_COLINDEX);
			//TODO: Creation
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), e.getMessage(), null);
		}
	}
	
}

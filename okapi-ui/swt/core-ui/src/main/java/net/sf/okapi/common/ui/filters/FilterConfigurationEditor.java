/*===========================================================================
  Copyright (C) 2010-2012 by the Okapi Framework contributors
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import net.sf.okapi.common.BaseContext;
import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationEditor;
import net.sf.okapi.common.filters.IFilterConfigurationListEditor;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.ui.InputDialog;
import net.sf.okapi.common.ui.genericeditor.GenericEditor;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

/**
 * Implements {@link IFilterConfigurationEditor} for the SWT-based UI.
 */
public class FilterConfigurationEditor implements IFilterConfigurationEditor, IFilterConfigurationListEditor  {

	@Override
	public boolean editConfiguration (String configId,
		IFilterConfigurationMapper fcMapper)
	{
		return editConfiguration(configId, fcMapper, null, null, new BaseContext());
	}
	
	@Override
	public boolean editConfiguration (String configId,
		IFilterConfigurationMapper fcMapper,
		IFilter cachedFilter,
		Object parent,
		IContext context)
	{
		FilterConfiguration config = fcMapper.getConfiguration(configId);
		if ( config == null ) {
			throw new RuntimeException(String.format(
				"Cannot find the configuration for '%s'.", configId));
		}
		IParameters params = fcMapper.getParameters(config, cachedFilter);
		if ( params == null ) { // No parameter for this filter
			Shell shell = null;
			if (( parent != null ) && ( parent instanceof Shell )) {
				shell = (Shell)parent;
			}
			MessageBox dlg = new MessageBox(shell, SWT.ICON_INFORMATION);
			dlg.setMessage("This filter has no parameters to edit.");
			dlg.setText("Information");
			dlg.open();
			return false;
		}

		IParametersEditor editor = fcMapper.createConfigurationEditor(configId, cachedFilter);
		if ( editor != null ) {
			if ( !editor.edit(params, !config.custom, context) ) {
				return false; // Cancel
			}
		}
		else {
			// Try to see if we can edit with the generic editor
			IEditorDescriptionProvider descProv = fcMapper.getDescriptionProvider(params.getClass().getCanonicalName());
			if ( descProv != null ) {
				// Edit the data
				GenericEditor genEditor = new GenericEditor();
				if ( !genEditor.edit(params, descProv, !config.custom, context) ) {
					return false; // Cancel
				}
				// The params object gets updated if edit not canceled.
			}
			else { // Else: fall back to the plain text editor
				Shell shell = null;
				if (( parent != null ) && ( parent instanceof Shell )) {
					shell = (Shell)parent;
				}
				InputDialog dlg  = new InputDialog(shell,
					String.format("Filter Parameters (%s)", config.configId), "Parameters:",
					params.toString(), null, 0, 300, 800);
				dlg.setReadOnly(!config.custom); // Pre-defined configurations should be read-only
				dlg.changeFontSize(+2);
				String data = dlg.showDialog();
				if ( data == null ) return false; // Cancel
				if ( !config.custom ) return true; // Don't save pre-defined parameters
				data = data.replace("\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
				params.fromString(data.replace("\r", "\n")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		// If not canceled and if custom configuration: save the changes
		if ( config.custom ) {
			// Save the configuration filefcMapper
			fcMapper.saveCustomParameters(config, params);
		}
		return true;
	}

	@Override
	public void editConfigurations (IFilterConfigurationMapper fcMapper) {
		FilterConfigurationsDialog dlg = new FilterConfigurationsDialog(null, false,
			(FilterConfigurationMapper)fcMapper, null);
		dlg.showDialog(null);
	}

	public String editConfigurations (IFilterConfigurationMapper fcMapper,
		String configId)
	{
		FilterConfigurationsDialog dlg = new FilterConfigurationsDialog(null, true,
			(FilterConfigurationMapper)fcMapper, null);
		return dlg.showDialog(configId);
	}

}

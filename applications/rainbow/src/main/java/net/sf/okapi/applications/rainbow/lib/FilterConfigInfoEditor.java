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

package net.sf.okapi.applications.rainbow.lib;

import org.eclipse.swt.widgets.Shell;

import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.InputDialog;
import net.sf.okapi.common.ui.filters.IFilterConfigurationInfoEditor;

public class FilterConfigInfoEditor implements IFilterConfigurationInfoEditor {

	private Shell parent;

	public void create (Shell parent) {
		this.parent = parent;
	}

	public boolean showDialog (FilterConfiguration config,
		IFilterConfigurationMapper mapper)
	{
		int n = config.configId.indexOf(FilterSettingsMarkers.PARAMETERSSEP);
		String prefix = config.configId.substring(0, n);
		String part = config.configId.substring(n+1);

		while ( true ) {
			InputDialog dlg = new InputDialog(parent, Res.getString("FilterConfigInfoEditor.caption"), //$NON-NLS-1$
				String.format(Res.getString("FilterConfigInfoEditor.enterConfigId"), //$NON-NLS-1$
					prefix+FilterSettingsMarkers.PARAMETERSSEP),
				part, null, 0, -1, 500);
			String newPart = dlg.showDialog();
			if ( newPart == null ) return false;
		
			// Else: Update the configuration
			config.configId = config.configId.replace(part, newPart);
			config.name = config.name.replace(part, newPart);
			config.parametersLocation = config.parametersLocation.replace(part, newPart);
			
			// check if it exists already
			if ( mapper.getConfiguration(config.configId) != null ) {
				Dialogs.showError(parent, String.format(Res.getString("FilterConfigInfoEditor.configIdExitsAlready"), //$NON-NLS-1$
					config.configId), null);
			}
			else break; // Done
		}
		
		return true;
	}

}

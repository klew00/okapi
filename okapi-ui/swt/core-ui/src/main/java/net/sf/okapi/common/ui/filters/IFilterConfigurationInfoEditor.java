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

import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;

import org.eclipse.swt.widgets.Shell;

/**
 * Dialog box to edit the information for a given configuration.
 * This interface is used by the {@link FilterConfigurationsPanel} class to
 * have an application-specific way to define the information for a given
 * configuration, for example when creating a new one.
 */
public interface IFilterConfigurationInfoEditor {

	/**
	 * Creates the dialog box.
	 * @param parent the parent shell of this dialog.
	 */
	public void create (Shell parent);
	
	/**
	 * Calls the dialog box.
	 * @param config the configuration to edit.
	 * @param mapper the filter configuration mapper where this
	 * configuration will be set. Having access to this mapper allows
	 * for example to check for identifier duplication.
	 * @return true if the edit was successful, false if an error
	 * occurred or if the user canceled the operation.
	 */
	public boolean showDialog (FilterConfiguration config,
		IFilterConfigurationMapper mapper);

}

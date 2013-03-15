/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.common.filters;

/**
 * Interface to edit a list of filter configurations at once.
 */
public interface IFilterConfigurationListEditor {

	/**
	 * Displays a list of all available configurations in a given {@link FilterConfigurationMapper} and allow to edit them.
	 * @param fcMapper the {@link IFilterConfigurationMapper} to use.
	 */
	public void editConfigurations (IFilterConfigurationMapper fcMapper);

	/**
	 * Displays a list of all available configurations in a given {@link FilterConfigurationMapper}, allow to edit them
	 * and to select one.
	 * @param fcMapper the {@link IFilterConfigurationMapper} to use.
	 * @param configId the configuration id to start with (can be null or empty).
	 * @return the configuration ID selected or null if none was selected. If the dialog
	 * is terminated with a Cancel or Close rather than a Select action, the return is null.
	 */
	public String editConfigurations (IFilterConfigurationMapper fcMapper, String configId);

}

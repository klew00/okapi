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

import net.sf.okapi.common.IContext;

/**
 * Interface to edit the parameters of a filter configuration.
 * <p>There are different ways the parameters for a filter configuration can be edited
 * depending on what editors are available. This interface provides a single call access
 * to those different types of editors.
 */
public interface IFilterConfigurationEditor {

	/**
	 * Edits a given filter configuration.
	 * @param configId the filter configuration identifier.
	 * @param fcMapper the {@link IFilterConfigurationMapper} to use.
	 * @param cachedFilter an optional cached filter (can be null). If not null
	 * the call will try to re-use it to load the parameters and the appropriate editor.
	 * @param parent optional parent object used to place the dialog box (can be null).
	 * @param context optional context from the caller (help, etc.)
	 * The type of the object can be different depending on the implementations. 
	 * @return true if the configuration was done, false if it could not be done or was canceled.
	 * @throws RuntimeException if the configuration cannot be found, or if the parameters 
	 * cannot be loaded or another error occurs.
	 */
	public boolean editConfiguration (String configId,
		IFilterConfigurationMapper fcMapper,
		IFilter cachedFilter,
		Object parent,
		IContext context);

	/**
	 * Edits a given filter configuration.
	 * @param configId the filter configuration identifier.
	 * @param fcMapper the {@link IFilterConfigurationMapper} to use.
	 * @return true if the configuration was done, false if it could not be done or was canceled.
	 * @throws RuntimeException if the configuration cannot be found, or if the parameters 
	 * cannot be loaded or another error occurs.
	 */
	public boolean editConfiguration (String configId,
		IFilterConfigurationMapper fcMapper);

}

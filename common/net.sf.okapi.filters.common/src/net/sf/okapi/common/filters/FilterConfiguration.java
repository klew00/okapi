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

package net.sf.okapi.common.filters;

/**
 * Data set defining a filter configuration.
 */
public class FilterConfiguration {

	/**
	 * Unique identifier for this configuration.
	 */
	public String configId;
	/**
	 * The full name of the class that implement the filter for this configuration. 
	 */
	public String filterClass;
	/**
	 * The location of the parameters for this configuration. This should be the
	 * name of the file where the configuration is stored in the package resource
	 * for a pre-defined configurations; it can be null if the configuration is 
	 * the default one, and it can be anything for a custom configuration.
	 */
	public String parametersLocation;
	/**
	 * Short localizable name for this configuration.
	 */
	public String name;
	/**
	 * Longer localizable description of for this configuration.
	 */
	public String description;
	/**
	 * Flag indicating if this configuration is custom or pre-defined.
	 */
	public boolean custom;
	/**
	 * MIME type for this configuration.
	 */
	public String mimeType;

	/**
	 * Creates an empty FilterConfiguration object.
	 */
	public FilterConfiguration () {
	}
	
	/**
	 * Creates a FilterConfiguration object and initializes it.
	 * @param configId the configuration identifier.
	 * @param mimeType the MIME type associated with this configuration.
	 * @param filterClass the filter class name.
	 * @param name the localizable name of this configuration.
	 * @param description the localizable description of this configuration. 
	 * @param parametersLocation the location where the parameters for this configuration are stored.
	 */
	public FilterConfiguration (String configId,
		String mimeType,
		String filterClass,
		String name,
		String description,
		String parametersLocation)
	{
		create(configId, mimeType, filterClass, name, description, parametersLocation);
	}
	
	/**
	 * Creates a FilterConfiguration object and initializes it.
	 * @param configId the configuration identifier.
	 * @param mimeType the MIME type associated with this configuration.
	 * @param filterClass the filter class name.
	 * @param name the localizable name of this configuration.
	 * @param description the localizable description of this configuration. 
	 */
	public FilterConfiguration (String configId,
		String mimeType,
		String filterClass,
		String name,
		String description)
	{
		create(configId, mimeType, filterClass, name, description, null);
	}

	/**
	 * Creates a FilterConfiguration object and initializes it.
	 * @param configId the configuration identifier.
	 * @param mimeType the MIME type associated with this configuration.
	 * @param filterClass the filter class name.
	 * @param name the localizable name of this configuration.
	 * @param description the localizable description of this configuration. 
	 * @param parametersLocation the location where the parameters for this configuration are stored.
	 */
	private void create (String configId,
		String mimeType,
		String filterClass,
		String name,
		String description,
		String parametersLocation)
	{
		this.configId = configId;
		this.mimeType = mimeType;
		this.name = name;
		this.description = description;
		this.filterClass = filterClass;
		this.parametersLocation = parametersLocation;
	}

}

/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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

package net.sf.okapi.common.uidescription;

import java.net.URI;

import net.sf.okapi.common.ParameterDescriptor;

/**
 * UI part descriptor for a path. This UI part supports the following types:
 * String and URI.
 * <p>Use {@link #setForSaveAs(boolean)} to specify if the path is for saving
 * a file (by default it is assumed the path is for saving a file). That choice
 * affect which browsing dialog is invoked when using the browse button.
 */
public class PathInputPart extends AbstractPart {

	private boolean forSaveAs;
	private String browseTitle;
	private String filterNames;
	private String filterExtensions;
	private boolean allowEmpty = false;
	
	/**
	 * Creates a new PathInputPart object with a given  parameter descriptor.
	 * @param paramDescriptor the parameter descriptor for this UI part.
	 * @param browseTitle the title to use for the path browsing dialog.
	 * @param forSaveAs true if the path is to save a file (vs to open one).
	 */
	public PathInputPart (ParameterDescriptor paramDescriptor,
		String browseTitle,
		boolean forSaveAs)
	{
		super(paramDescriptor);
		this.forSaveAs = forSaveAs;
		this.browseTitle = browseTitle;
		filterNames = "All Files (*.*)";
		filterExtensions = "*.*";
	}

	@Override
	protected void checkType () {
		// Check type support
		if ( getType().equals(String.class) ) return;
		if ( getType().equals(URI.class) ) return;
		// Otherwise: call the base method.
		super.checkType();
	}

	/**
	 * Gets the names to used for the browse filter.
	 * @return the names to used for the browse filter.
	 */
	public String getFilterNames () {
		return filterNames;
	}

	/**
	 * Gets the extensions to use for the browse filter.
	 * @return the extensions to use for the browse filter.
	 */
	public String getFilterExtensions () {
		return filterExtensions;
	}

	/**
	 * Sets the names and extensions to use for the browse filter. Both strings
	 * must have the same number of names/extensions parts separated by a \t.
	 * Use a semi-colon (;) to separate multiple extensions.
	 * For example: <code>"Documents (*.txt;*.odt)\tAll Files (*.*)"</code> and
	 * <code>*.txt;*.odt\t*.*"</code>.
	 * @param filterNames the names to use for the browse filter.
	 * @param filterExtensions the extensions to use for the browse filter.
	 */
	public void setBrowseFilters (String filterNames,
		String filterExtensions)
	{
		this.filterNames = filterNames;
		this.filterExtensions = filterExtensions;
	}

	/**
	 * Gets the title of the path browsing dialog.
	 * @return the title of the path browsing dialog.
	 */
	public String getBrowseTitle () {
		return browseTitle;
	}

	/**
	 * Sets the title of the path browsing dialog.
	 * @param browseTitle the new title of the path browsing dialog.
	 */
	public void setBrowseTitle (String browseTitle) {
		this.browseTitle = browseTitle;
	}

	/**
	 * Indicates if the path is to save a file (vs to open one).
	 * @return true if the path is to save a file (vs to open one).
	 */
	public boolean isForSaveAs () {
		return forSaveAs;
	}

	/**
	 * Sets the flag indicating if the path is to save a file (vs to open one).
	 * @param forSaveAs true  if the path is to save a file (vs to open one).
	 */
	public void setForSaveAs (boolean forSaveAs) {
		this.forSaveAs = forSaveAs;
	}

	/**
	 * Indicates if the input text can be empty.
	 * @return true if the input text can be empty, false otherwise.
	 */
	public boolean isAllowEmpty () {
		return allowEmpty;
	}

	/**
	 * Sets the flag indicating if the input text can be empty. 
	 * @param allowEmpty true if the input text can be empty, false otherwise.
	 */
	public void setAllowEmpty (boolean allowEmpty) {
		this.allowEmpty = allowEmpty;
	}

}

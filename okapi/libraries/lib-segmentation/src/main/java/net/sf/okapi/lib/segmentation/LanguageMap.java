/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.lib.segmentation;

/**
 * Stores the data for an SRX &lt;languagemap> map element
 */
public class LanguageMap {

	/**
	 * The pattern of this language map.
	 */
	protected String pattern;
	
	/**
	 * The name of this language map.
	 */
	protected String ruleName;

	/**
	 * Creates an empty LanguageMap object. 
	 */
	public LanguageMap () {
	}
	
	/**
	 * Creates a LanguageMap object with a given pattern and a given name.
	 * @param pattern the pattern for the new language map.
	 * @param ruleName the name of the new language map.
	 */
	public LanguageMap (String pattern,
		String ruleName)
	{
		this.pattern = pattern;
		this.ruleName = ruleName;
	}
	
	/**
	 * Gets the pattern associated to this language map.
	 * @return the pattern associated to this language map.
	 */
	public String getPattern () {
		return pattern;
	}
	
	/**
	 * Gets the name of this language map.
	 * @return the name of this languag emap.
	 */
	public String getRuleName () {
		return ruleName;
	}

}

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

package net.sf.okapi.filters.abstractmarkup;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.filters.yaml.TaggedFilterConfiguration;

/**
 * {@link IParameters} based facade around the YAML configuration format.
 * 
 */
public class AbstractMarkupParameters extends BaseParameters {
	
	private TaggedFilterConfiguration taggedConfig;
	private String title = "Parameters Editor";

	public AbstractMarkupParameters () {
		reset();
	}

	@Override
	public void fromString (String data) {
		taggedConfig = new TaggedFilterConfiguration(data);
	}

	@Override
	public String toString () {
		return taggedConfig.toString();
	}

	@Override
	public void reset () {		
		taggedConfig = new TaggedFilterConfiguration("collapse_whitespace: false\nassumeWellformed: true");
	}

	/**
	 * Gets the title to use with the parameter editor.
	 * @return the title to use with the parameter editor.
	 */
	public String getEditorTitle () {
		return title;
	}
	
	/**
	 * Sets the title to use with the parameters editor.
	 * @param title the title to use with the parameters editor.
	 */
	public void setEditorTitle (String title) {
		this.title = title;
	}

	/**
	 * Gets the TaggedFilterConfiguration object for this parameters object.
	 * @return the TaggedFilterConfiguration object for this parameters object.
	 */
	public TaggedFilterConfiguration getTaggedConfig() {
		return taggedConfig;
	}

	/**
	 * Sets the TaggedFilterConfiguration object for this parameters object.
	 * @param taggedConfig the TaggedFilterConfiguration object for this parameters object.
	 */
	public void setTaggedConfig(TaggedFilterConfiguration taggedConfig) {
		this.taggedConfig = taggedConfig;
	}

}

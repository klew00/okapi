/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.xmlstream;

import java.io.File;
import java.net.URL;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.filters.yaml.TaggedFilterConfiguration;

/**
 * {@link IParameters} based facade around the YAML configuration format.
 * 
 */
public class Parameters extends BaseParameters {
	
	public static final String DEFAULT_PARAMETERS = "default.yml";
	public static final String DITA_PARAMETERS = "dita.yml";
	public static final String PROPERTY_XML_PARAMETERS = "javaPropertiesXml.yml";
	public static final String XML_ESCAPED_HTML = "xml_esc_html.yml";
	public static final String XML_CDATA_ESCAPED_HTML = "xml_cdata_esc_html.yml";
	public static final String IDD_ESCAPED_HTML = "idd_esc_html.yml";		
			
	private TaggedFilterConfiguration taggedConfig;

	/**
	 * XMLStreamFilter Parameters.
	 * Default constructor loads default.yml configuration
	 */
	public Parameters() {
		reset();
	}
	
	public Parameters(URL configPath) {
		setTaggedConfig(new TaggedFilterConfiguration(configPath));
	}

	public Parameters(File configFile) {
		setTaggedConfig(new TaggedFilterConfiguration(configFile));
	}

	public Parameters(String configAsString) {
		setTaggedConfig(new TaggedFilterConfiguration(configAsString));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.IParameters#fromString(java.lang.String)
	 */
	public void fromString(String data) {
		taggedConfig = new TaggedFilterConfiguration(data);
	}

	@Override
	public String toString() {
		return taggedConfig.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.IParameters#reset()
	 */
	public void reset() {		
		taggedConfig = new TaggedFilterConfiguration(XmlStreamFilter.class.getResource(DEFAULT_PARAMETERS));
	}

	public TaggedFilterConfiguration getTaggedConfig() {
		return taggedConfig;
	}

	/**
	 * @param IFilter Configuration
	 */
	public void setTaggedConfig(TaggedFilterConfiguration taggedConfig) {
		this.taggedConfig = taggedConfig;
	}
}

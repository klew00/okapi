/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.railsyaml;

import net.sf.okapi.common.filters.EventBuilder;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextFragment;

/**
 * @author PerkinsGW
 */
public class YamlEventBuilder extends EventBuilder {

//	private static final String YAML_WHITESPACE_REGEX = "[ ]+";
//	private static final Pattern YAML_WHITESPACE_PATTERN = Pattern.compile(YAML_WHITESPACE_REGEX);

	public YamlEventBuilder(String rootId, IFilter filter) {
		super(rootId, filter);
	}
	
	/**
	 * Normalize text after TextUnit is complete. Called after endTextUnit
	 */
	@Override
	protected ITextUnit postProcessTextUnit (ITextUnit textUnit) {
		TextFragment text = textUnit.getSource().getFirstContent();
		text.setCodedText(normalize(text.getCodedText()));
		return textUnit;
	}

	private String normalize (String text) {
		text = text.replace("&#34;", "\"");
		text = text.replace("&#42;", "*");
		text = text.replace("&#45;", "-");
		text = text.replace("&#47;", "/");
		text = text.replace("&#58;", ":");
		text = text.replace("&#63;", "?");
		text = text.replace("&#x2F;", "/");

		text = text.replace("\\_", "\u00A0"); // Non-breaking space
		//TODO: other escaped chars
		
		return text;
	}
	
}

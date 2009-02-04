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

package net.sf.okapi.filters.markupfilter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AttributeExtractionRule {
	private String attributeName;
	private List<ConditionalAttributeRule> extractionConditions;
	private Map<String, String> properties;
	
	public AttributeExtractionRule(String attributeName) {
		this.attributeName = attributeName;
		this.extractionConditions = new LinkedList<ConditionalAttributeRule>();
		this.properties = new HashMap<String, String>();
	}
		
	public void addConditionalAttributeRule(ConditionalAttributeRule conditionalAttribute) {
		this.extractionConditions.add(conditionalAttribute);
		this.properties = new HashMap<String, String>();
	}
	
	public void addProperty(String key, String value) {
		properties.put(key, value);
	}
}

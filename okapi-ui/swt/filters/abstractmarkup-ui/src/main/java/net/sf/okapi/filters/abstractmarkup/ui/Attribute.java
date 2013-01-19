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

package net.sf.okapi.filters.abstractmarkup.ui;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.filters.yaml.TaggedFilterConfiguration.RULE_TYPE;

class Attribute {
	
	public static final int SCOPE_ALL = 0;
	public static final int SCOPE_ONLY = 1;
	public static final int SCOPE_ALLEXCEPT = 2;

	String name;
	List<RULE_TYPE> rules;
	int scope;
	String scopeElements;
	List<Condition> conditions;
	List<Condition> wsPreserve;
	List<Condition> wsDefault;

	public Attribute () {
		rules = new ArrayList<RULE_TYPE>();
		scope = SCOPE_ALL;
		scopeElements = "";
	}
	
}

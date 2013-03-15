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

import net.sf.okapi.common.ListUtil;

class Condition {

	String part1;
	String operator;
	String part2;

	@Override
	public Condition clone () {
		Condition newCond = new Condition();
		newCond.part1 = part1;
		newCond.operator = operator;
		newCond.part2 = part2;
		return newCond;
	}
	
	@Override
	public String toString () {
		StringBuilder tmp = new StringBuilder();
		tmp.append(String.format("['%s', %s, ", part1, operator));
		if ( part2.indexOf(',') != -1 ) {
			java.util.List<String> list = ListUtil.stringAsList(part2);
			tmp.append("[");
			for ( int i=0; i<list.size(); i++ ) {
				if ( i > 0 ) tmp.append(", ");
				tmp.append(quotedValue(list.get(i)));
			}
			tmp.append("]");
		}
		else {
			tmp.append(quotedValue(part2));
		}
		tmp.append("]");
		return tmp.toString();
	}
	
	private String quotedValue (String value) {
		return "'"+value.replace("'", "''")+"'";
	}

}

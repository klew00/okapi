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

package net.sf.okapi.steps.searchandreplace;

import java.util.ArrayList;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {

	public boolean plainText;
	public boolean regEx;
	public boolean dotAll;
	public boolean ignoreCase;
	public  boolean multiLine;
	public ArrayList<String[]> rules;
	
	public Parameters () {
		reset();
	}

	public void reset () {
		plainText = true;
		regEx = false;
		dotAll = false;
		ignoreCase = false;
		multiLine = false;
		rules = new ArrayList<String[]>();
	}

	public void addRule (String pattern[]) {
		rules.add(pattern);
	}	
	
	public ArrayList<String[]> getRules () {
		return rules;
	}	

	public void fromString (String data) {

		reset();
		// Read the file content as a set of fields
		buffer.fromString(data);
		regEx = buffer.getBoolean("regEx", regEx);
		plainText = buffer.getBoolean("plainText", plainText);
		dotAll = buffer.getBoolean("dotAll", dotAll);
		ignoreCase = buffer.getBoolean("ignoreCase", ignoreCase);
		multiLine = buffer.getBoolean("multiLine", multiLine);
		
		int count = buffer.getInteger("count", 0);
		for ( int i=0; i<count; i++ ) {
			String []s = new String[3];
			s[0] = buffer.getString(String.format("use%d", i), "");
			s[1] = buffer.getString(String.format("search%d", i), "");
			s[2] = buffer.getString(String.format("replace%d", i), "");
			rules.add(s);
		}
	}

	public String toString() {

		buffer.reset();
		buffer.setBoolean("regEx", regEx);		
		buffer.setBoolean("plainText", plainText);
		buffer.setBoolean("dotAll", dotAll);
		buffer.setBoolean("ignoreCase", ignoreCase);
		buffer.setBoolean("multiLine", multiLine);
		buffer.setInteger("count", rules.size());
		int i = 0;

		for ( String[] temp : rules ) {
			buffer.setString(String.format("use%d", i), temp[0]);
			buffer.setString(String.format("search%d", i), temp[1]);
			buffer.setString(String.format("replace%d", i), temp[2]);
			i++;
		}
		return buffer.toString();
	}
	
}

/*===========================================================================*/
/* Copyright (C) 2008 Fredrik Liden                                          */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.applications.rainbow.utilities.searchandreplace;

import java.util.ArrayList;
import java.util.Iterator;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {

	protected boolean             ignoreCase;
	protected boolean             multiLine;
	protected ArrayList<String[]> rules;
	
	public Parameters () {
		reset();
	}
	
	public void fromString (String data) {
		reset();
		// Read the file content as a set of fields
		buffer.fromString(data);
		// Parse the fields
		ignoreCase = buffer.getBoolean("ignoreCase", ignoreCase);
		multiLine = buffer.getBoolean("multiLine", multiLine);
		
		int count = buffer.getInteger("count", 0);
		for ( int i=0; i<count; i++ ) {
			String []s=new String[3];
			s[0] = buffer.getString(String.format("use%d", i), "");
			s[1] = buffer.getString(String.format("search%d", i), "");
			s[2] = buffer.getString(String.format("replace%d", i), "");
			rules.add(s);
		}
	}

	public void reset () {
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
	
	@Override
	public String toString() {
		// Store the parameters in fields
		buffer.reset();
		buffer.setBoolean("ignoreCase", ignoreCase);
		buffer.setBoolean("multiLine", multiLine);
		
		buffer.setInteger("count", rules.size());
		int i = 0;
		Iterator<String[]> it = rules.iterator();
		while( it.hasNext() ) {
			String[] temp = (String[])it.next();
			buffer.setString(String.format("use%d", i), temp[0]);
			buffer.setString(String.format("search%d", i), temp[1]);
			buffer.setString(String.format("replace%d", i), temp[2]);
			i++;
		}
		
		return buffer.toString();
	}
}

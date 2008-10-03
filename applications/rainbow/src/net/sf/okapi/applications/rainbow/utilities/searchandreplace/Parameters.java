/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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
import net.sf.okapi.common.FieldsString;

public class Parameters extends BaseParameters {

	private String ignoreCase;
	private String multiLine;
	
	private ArrayList rules;
	
	public Parameters () {
		reset();
	}
	
	@Override
	public void fromString(String data) {
		// Read the file content as a set of fields
		FieldsString tmp = new FieldsString(data);
		// Parse the fields
		ignoreCase = tmp.get("ignoreCase", ignoreCase);
		multiLine = tmp.get("multiLine", multiLine);
		
		reset();
		int count = tmp.get("count", 0);
		for ( int i=0; i<count; i++ ) {
			
			String []s=new String[3];
			s[0] = tmp.get(String.format("use%d", i), "");
			s[1] = tmp.get(String.format("search%d", i), "");
			s[2] = tmp.get(String.format("replace%d", i), "");
			rules.add(s);
		}
	}

	@Override
	public void reset() {
//		ignoreCase = "";
//		multiLine = "";
		
		rules = new ArrayList();
	}

	public void addRule (String pattern[]) {
		rules.add(pattern);
	}	
	
	public ArrayList getRules () {
		return rules;
	}	
	
	
	@Override
	public String toString() {
		// Store the parameters in fields
		FieldsString tmp = new FieldsString();
		tmp.add("ignoreCase", ignoreCase);
		tmp.add("multiLine", multiLine);
		
		tmp.add("count", rules.size());
		int i = 0;
		Iterator it = rules.iterator();
		while(it.hasNext() ) {
			String[] temp = (String[])it.next();
			tmp.add(String.format("use%d", i), temp[0]);
			tmp.add(String.format("search%d", i), temp[1]);
			tmp.add(String.format("replace%d", i), temp[2]);
			i++;
		}
		
		return tmp.toString();
	}
}

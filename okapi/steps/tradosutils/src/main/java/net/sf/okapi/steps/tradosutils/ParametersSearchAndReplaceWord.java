/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.steps.tradosutils;

import java.util.ArrayList;

import net.sf.okapi.common.BaseParameters;

public class ParametersSearchAndReplaceWord extends BaseParameters {

	public boolean regEx;
	public boolean wholeWord;
	public boolean matchCase;
	public boolean replaceALL;
	public ArrayList<String[]> rules;
	
	public ParametersSearchAndReplaceWord () {
		reset();
	}

	public void reset () {
		regEx = false;
		wholeWord = false;
		matchCase = false;
		replaceALL = true;
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
		wholeWord = buffer.getBoolean("wholeWord", wholeWord);
		matchCase = buffer.getBoolean("matchCase", matchCase);
		replaceALL = buffer.getBoolean("replaceALL", replaceALL);
		
		int count = buffer.getInteger("count", 0);
		for ( int i=0; i<count; i++ ) {
			String []s = new String[5];
			s[0] = buffer.getString(String.format("use%d", i), "").replace("\r", "");
			s[1] = buffer.getString(String.format("search%d", i), "").replace("\r", "");
			s[2] = buffer.getString(String.format("replace%d", i), "").replace("\r", "");
			s[3] = buffer.getString(String.format("searchFormat%d", i), "").replace("\r", "");			
			s[4] = buffer.getString(String.format("replaceFormat%d", i), "").replace("\r", "");
			rules.add(s);
		}
	}

	public String toString() {

		buffer.reset();
		buffer.setBoolean("regEx", regEx);		
		buffer.setBoolean("wholeWord", wholeWord);
		buffer.setBoolean("matchCase", matchCase);
		buffer.setInteger("count", rules.size());
		buffer.setBoolean("replaceALL", replaceALL);
		
		int i = 0;

		for ( String[] temp : rules ) {
			buffer.setString(String.format("use%d", i), temp[0]);
			buffer.setString(String.format("search%d", i), temp[1]);
			buffer.setString(String.format("replace%d", i), temp[2]);
			buffer.setString(String.format("searchFormat%d", i), temp[3]);
			buffer.setString(String.format("replaceFormat%d", i), temp[4]);
			i++;
		}
		return buffer.toString();
	}

/* SAMPLE CONFIG
#v1
regEx.b=false
wholeWord.b=false
matchCase.b=false
count.i=0
replaceALL.b=true
use0=true
search0=Hello
replace0=Bonjour
searchFormat0=Normal
replaceFormat0=Heading 1
use1=true
search1=world
replace1=cosmos
searchFormat1=Heading 1
replaceFormat1=Heading 2
*/
}

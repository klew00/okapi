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

package net.sf.okapi.filters.plaintext.regex;

import java.util.regex.Pattern;

/**
 * Parameters of the Regex Plain Text Filter
 * 
 * @version 0.1, 09.06.2009
 * @author Sergei Vasilyev  
 */

public class Parameters extends net.sf.okapi.filters.plaintext.base.Parameters {
			
	public static final String	DEF_RULE = "(^(?=.+))(.*?)$";
	public static final int		DEF_GROUP = 2;
	public static final String	DEF_SAMPLE = "\nThis is the first sentence. And this is the second one.\n" +	
		"Second paragraph. Each one ends at the line-break.\n\nThird paragraph.\nAnd the last paragraph may have no line-break.";
	public static final int		DEF_OPTIONS = Pattern.MULTILINE;
	
	/**
	 * Java regex rule used to extract lines of text.<p>Default: "^(.*?)$". 
	 */
	public String rule;
	
	/**
	 * Java regex capturing group denoting text to be extracted.<p>Default: 1.
	 */
	public int sourceGroup;
	
	/**
	 * Java regex options.<p>Default: Pattern.MULTILINE.
	 */
	public int regexOptions;
	
	/**
	 * Sample text for the rule.
	 */
	public String sample;
							
//----------------------------------------------------------------------------------------------------------------------------	
	
	public Parameters() {
		super();		
		
		reset();
		toString(); // fill the list
	}

	public void reset() {
		
		super.reset();
		
		// All parameters are set to defaults here		
		rule = DEF_RULE;
		sourceGroup = DEF_GROUP;		
		regexOptions = DEF_OPTIONS;
		sample = DEF_SAMPLE;
	}

	public void fromString(String data) {
		reset();
		
		super.fromString(data);
		
		buffer.fromString(data);
		
		// All parameters are retrieved here
		rule =  buffer.getString("rule", DEF_RULE);
		sourceGroup = buffer.getInteger("sourceGroup", DEF_GROUP);		
		regexOptions = buffer.getInteger("regexOptions", DEF_OPTIONS);
		sample =  buffer.getString("sample", "");
	}
	
	@Override
	public String toString () {
		buffer.reset();
		
		super.toString(); // Will write to the same buffer
		
		// All parameters are set here
		buffer.setString("rule", rule);
		buffer.setInteger("sourceGroup", sourceGroup);		
		buffer.setInteger("regexOptions", regexOptions);
		buffer.setString("sample", sample);
		
		return buffer.toString();
	}
}

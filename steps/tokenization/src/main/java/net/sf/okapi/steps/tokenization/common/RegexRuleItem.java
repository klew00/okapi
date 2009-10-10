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

package net.sf.okapi.steps.tokenization.common;

import net.sf.okapi.common.ParametersString;
import net.sf.okapi.lib.extra.AbstractParameters;

public class RegexRuleItem extends AbstractParameters {

	/**
	 * 0-based index of the Java regex group in the parent rule. 
	 */
	private int groupIndex;
	
	/**
	 * Token(s) to be generated for the text extracted by the group.
	 */
	private String groupOutTokens;
	
	/**
	 * Java regex options. Composition of Pattern.CASE_INSENSITIVE, Pattern.MULTILINE, etc. flags.
	 */
	private int regexOptions;
	
	@Override
	protected void parameters_load(ParametersString buffer) {
		
		groupIndex = buffer.getInteger("groupIndex", groupIndex);
		groupOutTokens = buffer.getString("groupOutTokens", groupOutTokens);
		regexOptions = buffer.getInteger("regexOptions", regexOptions);
	}

	@Override
	protected void parameters_reset() {
		
		groupIndex = 0;
		groupOutTokens = ""; 
		regexOptions = 0;
	}

	@Override
	protected void parameters_save(ParametersString buffer) {
		
		buffer.setInteger("groupIndex", groupIndex);
		buffer.setString("groupOutTokens", groupOutTokens);
		buffer.setInteger("regexOptions", regexOptions);
	}
}

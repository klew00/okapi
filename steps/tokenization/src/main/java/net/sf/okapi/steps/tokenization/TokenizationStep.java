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

package net.sf.okapi.steps.tokenization;

import net.sf.okapi.steps.tokenization.common.CompoundStep;
import net.sf.okapi.steps.tokenization.common.CompoundStepParameters;

/**
 * 
 * 
 * @version 0.1 06.07.2009
 */

public class TokenizationStep extends CompoundStep {

	Parameters params;
	CompoundStepParameters structureParams;
	
	public TokenizationStep() {
		
		super();
				
		setName("Tokenization");
		setDescription("Extracts tokens from the text units content of a document.");
				
		structureParams = new net.sf.okapi.steps.tokenization.common.CompoundStepParameters(); 
		if (structureParams == null) return;
		
		super.setParameters(structureParams);
		structureParams.loadFromResource(this.getClass(), "okf_tokenizers.fprm");
				
		setParameters(new Parameters());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <A> A getParameters(Class<?> expectedClass) {
					
		if (expectedClass == CompoundStepParameters.class)
			return (A) structureParams;
		else
			return super.getParameters(expectedClass);
	}
	
	@Override
	protected void component_init() {
		
		params = getParameters(Parameters.class);	
	}
				
}

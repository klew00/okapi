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

import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ListUtils;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.common.utils.TextUnitUtils;
import net.sf.okapi.steps.common.framework.AbstractPipelineStep;
import net.sf.okapi.steps.tokenization.engine.Tokens;

/**
 * 
 * 
 * @version 0.1 06.07.2009
 */

public class TokenizationStep extends AbstractPipelineStep { // TODO derive from CompoundStep

	private Parameters params;
	private String[] tokenTypes;
	
	public TokenizationStep() {
		
		super();
		setName("Tokenization");
		setDescription("Extracts tokens from the text units content of a document.");
		setParameters(new Parameters());
	}
	
	public Tokens tokenize(Object text, String language, String... tokenTypes) {
		
		// TODO Goes through registered internal tokenizers, passes them its params, analyzes & collects results
		return null;		
	}
		
	@Override
	protected void component_init() {

		params = getParameters(Parameters.class);				
		tokenTypes = ListUtils.stringAsArray(params.tokensOfInterest);
	}

	@Override
	protected void handleTextUnit(Event event) {
		
		super.handleTextUnit(event);
		if (event == null) return;
		
		TextUnit tu = (TextUnit) event.getResource();
		if (tu == null) return;
		
		if (tu.isEmpty()) return;
		if (!tu.isTranslatable()) return;
		
		TextContainer tc = tu.getSource();
		if (tc == null) return;
		
		ArrayList<Integer> positions = new ArrayList<Integer> ();
		
		//Tokens tokens = Tokenizer.tokenize(TextUnitUtils.getText(tc.getContent(), positions), getLanguage(), tokenTypes);
		
		Tokens tokens = tokenize(TextUnitUtils.getText(tc.getContent(), positions), getLanguage(), tokenTypes);
		
		if (tokens == null) return;
		
		tokens.fixRanges(positions);
		
		// Attach to TU		
		IResource res = event.getResource();
		if (res == null) return;
		
		TokensAnnotation ta = res.getAnnotation(TokensAnnotation.class);
		
		if (ta == null) {
			
			ta = new TokensAnnotation();
			res.setAnnotation(ta);
		}
		
		if (ta == null) return;
		
		ta.setTokens(tokens);
	}	

}

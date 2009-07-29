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

package net.sf.okapi.steps.tokenization.common;

import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.ListUtils;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.plaintext.common.TextUnitUtils;
import net.sf.okapi.steps.tokenization.tokens.Tokens;
import net.sf.okapi.steps.tokenization.tokens.TokensAnnotation;

/**
 * 
 * 
 * @version 0.1 06.07.2009
 */
@SuppressWarnings("unused")
public abstract class AbstractTokenizationStep extends AbstractPipelineStep {

	private Parameters params;
	private String[] tokenTypes;	
	private String[] languages;
	
	public AbstractTokenizationStep() {
		
		super();
		
		setParameters(new Parameters());
		setName("Tokenization");
		setDescription("Extracts tokens from the text units content of a document.");
	}
	
	public abstract Tokens tokenize(String text, String language, String... tokenTypes);
	
	public String[] getTokenTypes() {
		
		return tokenTypes;
	}

	protected void setTokenTypes(String[] tokenTypes) {
		
		this.tokenTypes = tokenTypes;
	}

	@Override
	protected void component_init() {

		params = getParameters(Parameters.class);				
		tokenTypes = ListUtils.stringAsArray(params.tokenTypes);
		languages = ListUtils.stringAsArray(params.languages);
	}

	@Override
	protected void handleTextUnit(Event event) {
		
		super.handleTextUnit(event);
		if (event == null) return;
		
		TextUnit tu = (TextUnit) event.getResource();
		if (tu == null) return;
		
		if (tu.isEmpty()) return;
		if (!tu.isTranslatable()) return;
		
//		if (params.tokenizeSource)
//			tokenizeSource(tu);
//		
//		if (params.tokenizeTargets)			
//			tokenizeTargets(tu);
	}

	private void tokenizeSource(TextUnit tu) {
		
		if (tu == null) return;
		
		TextContainer tc = tu.getSource();
		if (tc == null) return;
		
		ArrayList<Integer> positions = new ArrayList<Integer> ();
		
		Tokens tokens = tokenize(TextUnitUtils.getText(tc.getContent(), positions), getLanguage(), tokenTypes);
		
		if (tokens == null) return;
		
		tokens.fixRanges(positions);
		
		// Attach to TU		
		TokensAnnotation ta = TextUnitUtils.getSourceAnnotation(tu, TokensAnnotation.class);
		
		if (ta == null)
			TextUnitUtils.setSourceAnnotation(tu, new TokensAnnotation(tokens));
		else
			ta.addTokens(tokens);
	}	

	private void tokenizeTargets(TextUnit tu) {
		
		if (tu == null) return;
		
		ArrayList<Integer> positions = new ArrayList<Integer> ();
		
		for (String language : tu.getTargetLanguages()) {
		
			TextContainer tc = tu.getTarget(language);
			if (tc == null) continue;
			
			positions.clear();
			
			Tokens tokens = tokenize(TextUnitUtils.getText(tc.getContent(), positions), getLanguage(), tokenTypes);			
			if (tokens == null) continue;
			
			tokens.fixRanges(positions);
			
			// Attach to TU		
			TokensAnnotation ta = TextUnitUtils.getTargetAnnotation(tu, language, TokensAnnotation.class);
			
			if (ta == null)
				TextUnitUtils.setTargetAnnotation(tu, language, new TokensAnnotation(tokens));
			else
				ta.addTokens(tokens);
		}
	}	

}

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
import java.util.List;
import java.util.logging.Level;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.lib.extra.Notification;
import net.sf.okapi.lib.extra.steps.AbstractPipelineStep;
import net.sf.okapi.steps.tokenization.common.ILexer;
import net.sf.okapi.steps.tokenization.common.LanguageAndTokenParameters;
import net.sf.okapi.steps.tokenization.common.Lexem;
import net.sf.okapi.steps.tokenization.common.Lexems;
import net.sf.okapi.steps.tokenization.common.LexerRule;
import net.sf.okapi.steps.tokenization.common.LexerRules;
import net.sf.okapi.steps.tokenization.common.StructureParameters;
import net.sf.okapi.steps.tokenization.common.StructureParametersItem;
import net.sf.okapi.steps.tokenization.common.Token;
import net.sf.okapi.steps.tokenization.common.TokensAnnotation;
import net.sf.okapi.steps.tokenization.locale.LanguageList;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

/**
 * 
 * 
 * @version 0.1 06.07.2009
 */

public class TokenizationStep extends AbstractPipelineStep {

	private Parameters params;
	private StructureParameters structureParams;
	
	private List<ILexer> lexers = new ArrayList<ILexer>();
	
//	/**
//	 * The lexers with no rules capable of processing the current set of languages and tokens specified by Parameters.
//	 */
//	private List<ILexer> idleLexers = new ArrayList<ILexer>(); 
	
	/**
	 * The rules not capable of processing the current set of languages and tokens specified by Parameters.<p>
	 * The list is compiled of the rules collected from all installed lexers.
	 **/
	private List<LexerRule> idleRules = new ArrayList<LexerRule>();
	
	private List<String> languageFilter = new ArrayList<String>();  
	private List<Integer> tokenFilter = new ArrayList<Integer>();
	private ArrayList<Integer> positions = new ArrayList<Integer> ();
	
	public TokenizationStep() {
		
		super();
				
		setName("Tokenization");
		setDescription("Extracts tokens from the text units content of a document.");
				
		structureParams = new StructureParameters(); 
		if (structureParams == null) return;
		
		if (!structureParams.loadFromResource(this.getClass(), "lexers.tprm"))
			logMessage(Level.FINE, "Lexers' config file not found.");
		
		instantiateLexers();		
		
		setParameters(new Parameters());				
	}

	private void instantiateLexers() {

		if (lexers == null) return;
		
		lexers.clear();
		
		for (StructureParametersItem item : structureParams.getItems()) {
			
			try {
				if (item == null) continue;
				if (!item.isEnabled()) continue;
				
				ILexer lexer = (ILexer) Class.forName(item.getLexerClass()).newInstance();
				if (lexer == null) continue;
				
				//lexer.setLexerId(lexers.size() + 1);
				
				LexerRules lexerRules = lexer.getRules(); // Null if the lexer doesn't need rules to operate
				
				// Load lexer's rules
				if (lexerRules != null)
					if (!lexerRules.loadFromResource(lexer.getClass(), item.getRulesLocation())) continue;
				
				lexer.init();
				lexers.add(lexer);
				
			} catch (ClassNotFoundException e) {
				
				logMessage(Level.FINE, "Lexer instantialion falied: " + e.getMessage());
				continue;
				
			} catch (InstantiationException e) {
				
				logMessage(Level.FINE, "Lexer instantialion falied: " + e.getMessage());
				continue;
				
			} catch (IllegalAccessException e) {
				
				logMessage(Level.FINE, "Lexer instantialion falied: " + e.getMessage());
				continue;
			}				
		}
	}
	
//	@Override
//	protected <A> A getParameters(Class<A> expectedClass) {
//					
//		if (expectedClass == CompoundStepParameters.class)
//			return expectedClass.cast(structureParams);
//		else
//			return super.getParameters(expectedClass);
//	}
	
	@Override
	protected void component_init() {
		
		validateParameters();
	}
				
	private void validateParameters() {

		params = getParameters(Parameters.class);
		if (params == null) return;
		
		if (params.getLanguageMode() == LanguageAndTokenParameters.LANGUAGES_ONLY_WHITE_LIST &&
				params.getLanguageWhiteList() != null && 
				params.getLanguageWhiteList().size() == 0)
			params.setLanguageMode(LanguageAndTokenParameters.LANGUAGES_ALL);
		
		if (params.getTokenMode() == LanguageAndTokenParameters.TOKENS_SELECTED &&
				params.getTokenNames() != null && 
				params.getTokenNames().size() == 0)
			params.setTokenMode(LanguageAndTokenParameters.TOKENS_ALL);
	}

	@Override
	public boolean exec(Object sender, String command, Object info) {
		
		if (super.exec(sender, command, info)) return true;
		
		if (command.equalsIgnoreCase(Notification.PARAMETERS_CHANGED)) {
			
			validateParameters();
			
			setFilters();
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sets filters for languages and tokens.<p>
	 * Fills up idleLexers & idleRules with the lexers and rules, that cannot handle the languages and tokens
	 * set by the current params.<p>
	 * If a rule handles at least one language and/or token, that comly with the params' settings, it's not idle.
	 * Called every time after the step parameters are changed.  
	 */
	private void setFilters() {
		
		if (params == null) return;
		//if (idleLexers == null) return;
		if (idleRules == null) return;
		
		//idleLexers.clear();
		idleRules.clear();
		
		languageFilter = LanguageList.getAllLanguages(); // No need to clone, getAllLanguages() returns a new list every time
		
		if (params.getLanguageMode() == LanguageAndTokenParameters.LANGUAGES_ONLY_WHITE_LIST)
			languageFilter.retainAll(params.getLanguageWhiteList());
		
		else if (params.getLanguageMode() == LanguageAndTokenParameters.LANGUAGES_ALL_EXCEPT_BLACK_LIST)
			languageFilter.removeAll(params.getLanguageBlackList());
		
		tokenFilter = Tokens.getTokenIDs();
		
		if (params.getTokenMode() == LanguageAndTokenParameters.TOKENS_SELECTED)
			tokenFilter.retainAll(Tokens.getTokenIDs(params.getTokenNames()));

//		// If nothing is allowed by params
//		if (languageFilter.size() == 0 || tokenFilter.size() == 0) {
//			
//			idleLexers.addAll(lexers);
//			return;
//		}
		
		for (ILexer lexer : lexers) {
			
			if (lexer == null) continue;
			if (lexer.getRules() == null) continue;
			
			//boolean isIdleLexer = true;
			
			// TODO Tests
			for (LexerRule rule : lexer.getRules()) {
				
				// Okapi-B 22, Check if the rule conforms to the current set of tokens and languages
				
				// Languages
				if (rule.getLanguageMode() == LanguageAndTokenParameters.LANGUAGES_ALL) {

					//isIdleLexer = false;
					
				} else if (rule.getLanguageMode() == LanguageAndTokenParameters.LANGUAGES_ALL_EXCEPT_BLACK_LIST) { 
					
					if (rule.getLanguageBlackList().containsAll(languageFilter))
						idleRules.add(rule);
					else
						//isIdleLexer = false;
						;
						
				} else if (rule.getLanguageMode() == LanguageAndTokenParameters.LANGUAGES_ONLY_WHITE_LIST) { 
					
					if (rule.getLanguageWhiteList().size() == 0)
						idleRules.add(rule);
					else
						//isIdleLexer = false;
						;
				}
								
				// Tokens
					if (rule.getInTokenIDs().size() == 0 && 
							rule.getOutTokenIDs().size() == 0 &&
							rule.getUserTokenIDs().size() == 0)
						idleRules.add(rule);
					else
						//isIdleLexer = false;
						;
			}
			
//			if (isIdleLexer)
//				idleLexers.add(lexer);
		}
	}

	@Override
	protected void handleTextUnit(Event event) {
		
		super.handleTextUnit(event);
		if (event == null) return;
		
		TextUnit tu = (TextUnit) event.getResource();
		if (tu == null) return;
		
		if (tu.isEmpty()) return;
		if (!tu.isTranslatable()) return;
		
		if (params.tokenizeSource)
			tokenizeSource(tu);
		
		if (params.tokenizeTargets)			
			tokenizeTargets(tu);
	}
	
	private void processLexem(Lexem lexem, ILexer lexer, Tokens tokens) {
		
		if (lexem == null) return;
		LexerRules rules = lexer.getRules();
		LexerRule rule = rules.getRule(lexem.getId());
		if (rule == null) return;
		if (idleRules.contains(rule)) return;
		
		lexem.setLexerId(lexers.indexOf(lexer) + 1);
		
		for (int tokenId : rule.getOutTokenIDs())
			if (tokenFilter.contains(tokenId)) {
				
				Token token = new Token(tokenId, lexem, 100);
				tokens.add(token);
			}
	}
	
	private Tokens tokenize(TextContainer tc, String language) {
		
		if (tc == null) return null;
		if (Util.isEmpty(language)) return null;
		if (!languageFilter.contains(language)) return null;
		
		if (positions == null) return null;		
		positions.clear();
		
		Tokens tokens = new Tokens(); 
		
		// Remove codes, store to positions
		String text = TextUnitUtil.getText(tc.getContent(), positions);
		
		for (ILexer lexer : lexers) {
		
			//if (idleLexers.contains(lexer)) continue;			
			if (lexer == null) continue;
			
			// Single-call way
			Lexems lexems = lexer.process(text, language, tokens);

			if (lexems != null)
				for (Lexem lexem : lexems)
					processLexem(lexem, lexer, tokens);
			
			// Iterator way
			lexer.open(text, language, tokens);
			try {
				
				while (lexer.hasNext())					
					processLexem(lexer.next(), lexer, tokens);
			}
			finally {
				
				lexer.close();
			}			
		}
									
		// Restore codes from positions
		if (tokens != null)
			tokens.fixRanges(positions);
		
		return tokens;
	}
	
	private void tokenizeSource(TextUnit tu) {
		
		if (tu == null) return;
		
		Tokens tokens = tokenize(tu.getSource(), getLanguage());		
		if (tokens == null) return;
		
		// Attach to TU		
		TokensAnnotation ta = TextUnitUtil.getSourceAnnotation(tu, TokensAnnotation.class);
		
		if (ta == null)
			TextUnitUtil.setSourceAnnotation(tu, new TokensAnnotation(tokens));
		else
			ta.addTokens(tokens);
	}	
	
	private void tokenizeTargets(TextUnit tu) {
		
		if (tu == null) return;
		
		for (String language : tu.getTargetLanguages()) {
		
			Tokens tokens = tokenize(tu.getTarget(language), language);
			if (tokens == null) continue;
			
			// Attach to TU		
			TokensAnnotation ta = TextUnitUtil.getTargetAnnotation(tu, language, TokensAnnotation.class);
			
			if (ta == null)
				TextUnitUtil.setTargetAnnotation(tu, language, new TokensAnnotation(tokens));
			else
				ta.addTokens(tokens);
		}
	}

}

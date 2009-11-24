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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.Range;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.lib.extra.Notification;
import net.sf.okapi.lib.extra.steps.AbstractPipelineStep;
import net.sf.okapi.steps.tokenization.common.Config;
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

public class TokenizationStep extends AbstractPipelineStep {

	public static final int RAWTEXT = -1;
	
	private Parameters params;
	private StructureParameters structureParams;
	private Config config = new Config();
	
	/**
	 * Lexers generating lexems
	 */
	private List<ILexer> lexers = new ArrayList<ILexer>();
	
	/**
	 * Lexers not generating lexems, but rather performing sorting, cleaning etc. service tasks. 
	 * Either have no lexer rules assigned, or the rules have outTokens empty.
	 */
	private List<ILexer> serviceLexers = new ArrayList<ILexer>();
	
	private boolean allowNewRawText = true;
	
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
	
	/**
	 * Lexems for repeated processing (text that was extracted from other lexems and requires tokenization)
	 */
	private LinkedList<Lexem> rawtextLexems = new LinkedList<Lexem>();
	
	public TokenizationStep() {
		
		super();
				
		setName("Tokenization");
		setDescription("Extracts tokens from the text units content of a document.");
				
		setConfiguration(this.getClass(), "config.tprm");
	}
	
	public void setConfiguration(Class<?> classRef, String configLocation) {
				
		if (config == null) 
			config = new Config();
		
		if (config == null) return;
		config.loadFromResource(classRef, configLocation);		
		
		structureParams = new StructureParameters(); 
		if (structureParams == null) return;
		
		String structureLocation = config.getEngineConfig();
		
		if (Util.isEmpty(structureLocation) || !structureParams.loadFromResource(classRef, structureLocation))
			logMessage(Level.FINE, "Lexers' config file not found.");
		
		instantiateLexers();		
		
		setParameters(new Parameters());
	}

	private void instantiateLexers() {

		if (lexers == null) return;
		
		lexers.clear();
		serviceLexers.clear();
		
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
				
				if (lexerRules == null)
					serviceLexers.add(lexer);
				
				else {
				
					if (lexerRules.hasOutTokens())
						lexers.add(lexer);
					else
						serviceLexers.add(lexer);
				}
				
			} catch (ClassNotFoundException e) {
				
				logMessage(Level.FINE, "Lexer instantiation falied: " + e.getMessage());
				continue;
				
			} catch (InstantiationException e) {
				
				logMessage(Level.FINE, "Lexer instantiation falied: " + e.getMessage());
				continue;
				
			} catch (IllegalAccessException e) {
				
				logMessage(Level.FINE, "Lexer instantiation falied: " + e.getMessage());
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
		setFilters();
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
				
				if (!rule.isEnabled())
					idleRules.add(rule);
				
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
	
	private void processLexem(Lexem lexem, ILexer lexer, Tokens tokens, int textShift) {
		
		if (lexem == null) return;
		
		if (lexem.getId() == RAWTEXT) {
			
			if (allowNewRawText)
				rawtextLexems.add(lexem);
			return;
		}
		
		LexerRules rules = lexer.getRules();
		LexerRule rule = rules.getRule(lexem.getId());
		if (rule == null) return;
		if (idleRules.contains(rule)) return;
		
		lexem.setLexerId(lexers.indexOf(lexer) + 1);
		
		for (int tokenId : rule.getOutTokenIDs())
			if (tokenFilter.contains(tokenId)) {
				
				if (textShift > 0) {
					
					Range r = lexem.getRange();
					r.start += textShift;
					r.end += textShift;
				}
					 
				Token token = new Token(tokenId, lexem, 100);
				tokens.add(token);
			}
	}
	
//	private void listTokens(Tokens tokens) {
//		
//		if (tokens == null) return;
//		if (tokens.size() < 21) return;
//		
//		for (Token token : tokens.subList(0, 20)) {	
//			
//			System.out.println(token.toString());
//		}
//	}
//	
	//public boolean logTokens = false;
	
	private void runLexers(List<ILexer> lexers, String text, LocaleId language, Tokens tokens, int textShift) {
	
		for (ILexer lexer : lexers) {
			
			//if (idleLexers.contains(lexer)) continue;			
			if (lexer == null) continue;
			
			// Single-call way
			Lexems lexems = lexer.process(text, language, tokens);

//			if (logTokens) {
//				System.out.println();
//				System.out.println();
//				System.out.println("--------------------");
//				System.out.println(lexer.getClass());
//				System.out.println("--------------------");
//			}

			
			if (lexems != null)
				for (Lexem lexem : lexems)
					processLexem(lexem, lexer, tokens, 0); // 0 - token ranges don't need shifting
			
			// Iterator way
			lexer.open(text, language, tokens);
			try {
				
				while (lexer.hasNext())					
					processLexem(lexer.next(), lexer, tokens, textShift);
			}
			finally {
				
				lexer.close();
			}
			
//			if (logTokens)
//				listTokens(tokens);
		}
	}
	
	private Tokens tokenize (TextContainer tc, LocaleId language) {
		if (tc == null) return null;
		if (Util.isNullOrEmpty(language)) return null;
		if (!languageFilter.contains(language)) return null;
		
		if (positions == null) return null;		
		positions.clear();
		
		Tokens tokens = new Tokens();
		Tokens tempTokens = new Tokens();
		int textShift = 0;
		rawtextLexems.clear();
		
		// Remove codes, store to positions
		String text = TextUnitUtil.getText(tc.getContent(), positions);
		
		allowNewRawText = true;
		runLexers(lexers, text, language, tokens, textShift);
		//logTokens = true;
		runLexers(serviceLexers, text, language, tokens, textShift);
		allowNewRawText = false;
		
		if (rawtextLexems.size() > 0) {
			
			int saveNumRawtextLexems = 0;
			
			while (rawtextLexems.size() > 0) {
				
				// Deadlock and chain-reaction protection
				if (saveNumRawtextLexems > 0 && rawtextLexems.size() >= saveNumRawtextLexems) {
					
					if (rawtextLexems.size() == saveNumRawtextLexems)
						logMessage(Level.FINE, "RAWTEXT lexems are not processed in tokenize()");
					else
						logMessage(Level.FINE, "RAWTEXT lexems are creating a chain reaction in tokenize()");
					
					break;
				}
	
				tempTokens.clear();
				saveNumRawtextLexems = rawtextLexems.size();
				
				Lexem lexem = rawtextLexems.poll();
				text = lexem.getValue();
				textShift = lexem.getRange().start;
				
				runLexers(lexers, text, language, tempTokens, textShift);
				tempTokens.setImmutable(true);
				tokens.addAll(tempTokens);
			}
						
			runLexers(serviceLexers, text, language, tokens, 0);
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
		
		for (LocaleId language : tu.getTargetLocales()) {
		
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

	public List<LexerRule> getIdleRules() {
		
		return idleRules;
	}

	public List<String> getLanguageFilter() {
		
		return languageFilter;
	}

	public List<Integer> getTokenFilter() {
		
		return tokenFilter;
	}

	public void setLexers(List<ILexer> lexers) {
		
		this.lexers = lexers;
	}

	public List<ILexer> getLexers() {
		
		return lexers;
	}

	public String getConfigInfo() {
		
		if (config == null) return "";
		
		return config.getEngineConfig();
	}

}

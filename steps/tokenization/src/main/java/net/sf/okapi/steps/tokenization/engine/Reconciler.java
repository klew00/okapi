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

package net.sf.okapi.steps.tokenization.engine;

import java.util.HashMap;

import net.sf.okapi.common.Range;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.steps.tokenization.common.AbstractLexer;
import net.sf.okapi.steps.tokenization.common.Lexem;
import net.sf.okapi.steps.tokenization.common.Lexems;
import net.sf.okapi.steps.tokenization.common.Token;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

public class Reconciler extends AbstractLexer {

	private HashMap<String, Tokens> sameRangeMap;
	private HashMap<Token, Tokens> sameScoreMap;
	private HashMap<Token, Tokens> reverseSameScoreMap;
	
	@Override
	protected boolean lexer_hasNext() {

		return false;
	}

	@Override
	protected void lexer_init() {

		sameRangeMap = new HashMap<String, Tokens>(); 
		sameScoreMap = new HashMap<Token, Tokens>();
		reverseSameScoreMap = new HashMap<Token, Tokens>();
	}

	@Override
	protected Lexem lexer_next() {

		return null;
	}

	@Override
	protected void lexer_open(String text, LocaleId language, Tokens tokens) {
	}

	/**
	 * Returns true if range2 is within range.
	 * @param range
	 * @param range2
	 * @return
	 */
	private boolean contains(Range range, Range range2) {

		// Exact matches are dropped
		return (range.start < range2.start && range.end >= range2.end) ||
			(range.start <= range2.start && range.end > range2.end);
	}
	
	private String formRangeId(Range range) {
		
		if (range == null) return null;
		
		return String.format("%d %d", range.start, range.end);
	}

	private void setSameRangeMap(Tokens tokens) {
		
		// Create a map of lists of tokens with equal ranges
		if (tokens == null) return;
		if (sameRangeMap == null) return;
		
		sameRangeMap.clear();
		
		for (Token token : tokens) {
			
			if (token.isDeleted()) continue;
			String rangeId = formRangeId(token.getRange());
			
			Tokens rangeTokens = sameRangeMap.get(rangeId);
			if (rangeTokens == null) {
				
				rangeTokens = new Tokens();
				sameRangeMap.put(rangeId, rangeTokens);
			}
			
			rangeTokens.add(token);
		}
	}
	
	private	boolean getSameTokenIds(Token token1, Token token2) {
		
		return token1.getTokenId() == token2.getTokenId();
	}
	
	private boolean getSameRules(Token token1, Token token2) {
		
		return (token1.getLexerId() == token2.getLexerId()) &&		
			(token1.getLexemId() == token2.getLexemId());
	}
			
	private void setSameScoreMap(Tokens tokens) {
		
		// Create a map of lists of tokens which score change is synchronized (change in the score of one 
		// token should be applied to other tokens in the group too)
		if (tokens == null) return;
		if (Util.isEmpty(sameRangeMap)) return;
		if (sameScoreMap == null) return;
		
		sameScoreMap.clear();
		reverseSameScoreMap.clear();
		
		// Create the direct and reverse same-score maps
		for (Tokens rangeTokens : sameRangeMap.values())			
			for (int i = 0; i < rangeTokens.size(); i++) {
				
				Token token1 = rangeTokens.get(i);
				if (token1.isDeleted()) continue;
				
				for (int j = 0; j < rangeTokens.size(); j++) {
					
					if (i >= j) continue;
					
					Token token2 = rangeTokens.get(j);				
					if (token2.isDeleted()) continue;
					if (token2 == token1) continue;
					
					boolean sameTokenIds = getSameTokenIds(token1, token2);
					boolean sameRules = getSameRules(token1, token2); 
					
					if ((sameTokenIds && sameRules) || // Erroneous duplication of the same token in the rule's outTokens 
						(!sameTokenIds && sameRules) || // Both tokens are listed on the rule's outTokens
						(sameTokenIds && !sameRules)) { // 2 different rules recognize this range as the same token
			
						// Update token1 group
						Tokens groupTokens = sameScoreMap.get(token1);
						if (groupTokens == null) {
							
							groupTokens = new Tokens();
							sameScoreMap.put(token1, groupTokens);
						}
						
						if (!groupTokens.contains(token2))
							groupTokens.add(token2);
						
						// Update token2 group
						Tokens groupTokens2 = reverseSameScoreMap.get(token2);
						if (groupTokens2 == null) {
							
							groupTokens2 = new Tokens();
							reverseSameScoreMap.put(token2, groupTokens2);
						}
						
						if (!groupTokens2.contains(token1))
							groupTokens2.add(token1);
					}					
				}
			}
		
		// Merge 2 maps together (Okapi-B 45**)
		for (Token key : sameScoreMap.keySet()) {
		
			Tokens groupTokens = sameScoreMap.get(key);
			if (groupTokens == null) continue;
			
			for (int i = groupTokens.size() - 1; i >= 0; i--) {
				
				Token token = groupTokens.get(i);
				
				Tokens reverseTokens = reverseSameScoreMap.get(token);
				if (reverseTokens == null) continue;
				
				for (Token token2 : reverseTokens) {
					
					if (token2 == key) continue;
					groupTokens.add(token2);
				}
			}
		}
		
	}
	
//	private void setTokenScore(Token token, int score) {
//		
//		if (token == null) return;
//		if (Util.isEmpty(sameScoreMap)) return;
//		
//		token.setScore(score);
//		
//		// Set the same score to all tokens in its same-score group
//		Tokens groupTokens = sameScoreMap.get(token);
//		if (groupTokens == null) return;
//		
//		for (Token groupToken : groupTokens)
//			groupToken.setScore(score);
//	}
	
	private boolean firstIsNewer(Token token1, Token token2) {
		
		if (token1 == null) return false;
		if (token2 == null) return true;
		
		return (token1.getLexerId() > token2.getLexerId()) ||
		((token1.getLexerId() == token2.getLexerId()) && (token1.getLexemId() > token2.getLexemId()));
	}
	
	public Lexems process(String text, LocaleId language, Tokens tokens) {
		
//		tokens.remove(8);
//		tokens.remove(0);
//		tokens.remove(0);
//		tokens.remove(0);
//		tokens.remove(0);
		
		setSameRangeMap(tokens);
		setSameScoreMap(tokens);
			

		// Set scores for equal range tokens (Okapi-B 45*)
		
		// 1. Temporarily *delete* tokens of all same-score groups to exclude them at step 2
		// The key token of the group is not touched. It never appears in other tokens' groups and thus keeps its score.
//		setSameRangeMap(tokens);
//		setSameScoreMap(tokens);
		
		for (Token key : sameScoreMap.keySet()) {
			
			if (key == null) continue;
			if (key.isDeleted()) continue;
			
			Tokens list = sameScoreMap.get(key);
			for (Token token : list)
				if (token != null)
					token.delete();
		}
		
		// 2. Count valid tokens in same-range lists 
		for (Tokens list : sameRangeMap.values()) { 
			
			int size = 0;
			for (Token token : list) {
				
				if (token == null) continue;
				if (token.isDeleted()) continue;
				size++;
			}
			
			for (Token token : list) {
				
				if (token == null) continue;
				if (token.isDeleted()) continue;
				
				token.setScore(100 / size);
			}
			
//			int size = list.size();
//			
//			if (size > 0)
//				for (Token token : list) {
//					
//					//setTokenScore(token, 100 / size);
//					
//				}
			
//			// All the tokens being compared have the same range
//			for (int i = 0; i < list.size(); i++) {
//				
//				Token token1 = list.get(i);
//				if (token1.isDeleted()) continue;
//				
//				for (int j = 0; j < list.size(); j++) {
//					
//					if (i >= j) continue;
//					
//					Token token2 = list.get(j);				
//					if (token2.isDeleted()) continue;
//					if (token2 == token1) continue;
//					
//					boolean sameTokenIds = getSameTokenIds(token1, token2);
//					boolean sameRules = getSameRules(token1, token2);
//					
//					if (!sameTokenIds && !sameRules) { // Only this case changes scores
//						
//						
//					}
//				}
//			}
		}
		
		// 3. Fix same-score groups
		for (Token token : sameScoreMap.keySet()) {
			
			if (token == null) continue;
			//if (token.isDeleted()) continue;
			
			int score = token.getScore();
			
			// Set the same score to all tokens in its same-score group
			Tokens groupTokens = sameScoreMap.get(token);
			if (groupTokens == null) continue;
			
			for (Token groupToken : groupTokens)
				//if (!groupToken.isDeleted())
					groupToken.setScore(score);
		}

		
		// Remove tokens included in other tokens etc. (Okapi-B 43)
		
		for (int i = 0; i < tokens.size(); i++) {
			
			Token token1 = tokens.get(i);
			if (token1.isDeleted()) continue;
			
			for (int j = 0; j < tokens.size(); j++) {
				
				if (i >= j) continue;
				
				Token token2 = tokens.get(j);				
				if (token2.isDeleted()) continue;
				if (token2 == token1) continue;				
			
				Range r1 = token1.getRange();
				Range r2 = token2.getRange();
												
				if (r1.start == r2.start && r1.end == r2.end) { // Equal ranges
					
					// Tokens are identical, remove duplication
					if (token1.getTokenId() == token2.getTokenId()) { 
						
						if (firstIsNewer(token1, token2))
							token2.delete();
						else
							token1.delete();
						
						continue;
					}
					
					
				}
				else { // One of the ranges contains the other, or no overlapping
				
					//Range bigger, smaller;
					
					if (contains(r1, r2)) {
						
//						bigger = r1;
//						smaller = r2;
						
						token2.delete();
					}
					else if (contains(r2, r1)) {
						
//						bigger = r2;
//						smaller = r1;
						
						token1.delete();
					}
					else
						continue; // The two ranges don't overlap
				}
				
				// If the token's range includes other tokens' ranges, destroy those.
//				if (//checkEqual(token, token2) || // Remove duplicate tokens 
//				contains(token.getLexem().getRange(), token2.getLexem().getRange())) // Remove overlapped tokens					
//				//wasteBin.add(token2);
//				token2.delete();

			}
		}
		
		return null;
	}


}


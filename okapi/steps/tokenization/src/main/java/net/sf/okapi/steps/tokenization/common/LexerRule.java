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

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.ListUtil;
import net.sf.okapi.common.ParametersString;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

public class LexerRule extends LanguageParameters {

	/**
	 * Unique in the lexer's scope name of the rule to be displayed on rule lists in GUI, also to identify the rule inside lexers.
	 * Should not contain spaces, and/or follow other parent lexer's naming standards.
	 */
	private String name;
	
	/**
	 * Rule description.
	 */
	private String description;
	
	/**
	 * True if the rule is active.
	 */
	private boolean enabled;
	
	/**
	 * Integer value, generated by the underlying lexer, and converted to the tokens specified 
	 * by LanguageAndTokenParameters.tokenNames.
	 */
	private int lexemId;
	
	/**
	 * Optional string, configuring the underlying lexer to produce the lexem.
	 */
	private String pattern;
	
	/**
	 * Exemplary text containing fragments to be captured by the pattern.
	 */
	private String sample;
	
	/**
	 * True if the input tokens (ones on the inTokens list) should be kept. Removed otherwise.
	 */
	private boolean keepInput;
	
	private List<Integer> inTokenIDs;
	private List<String> inTokens;
	
	private List<Integer> outTokenIDs;
	private List<String> outTokens;
	
	private List<Integer> userTokenIDs;
	private List<String> userTokens;
	
	@Override
	protected void parameters_reset() {

		super.parameters_reset();
		
		name = "";
		description = "";
		enabled = true;
		inTokenIDs.clear();
		outTokenIDs.clear();
		userTokenIDs.clear();
		lexemId = 0;
		pattern = "";
		sample = "";
		keepInput = false;
	}

	@Override
	protected void parameters_init() {

		super.parameters_init();
		
		inTokenIDs = new ArrayList<Integer>();
		inTokens = new ArrayList<String>();
		
		outTokenIDs = new ArrayList<Integer>();
		outTokens = new ArrayList<String>();
		
		userTokenIDs = new ArrayList<Integer>();
		userTokens = new ArrayList<String>();
	}
	
	@Override
	protected void parameters_load(ParametersString buffer) {

		super.parameters_load(buffer);
		
		name = buffer.getString("name", "");
		description = buffer.getString("description", "");
		enabled = buffer.getBoolean("enabled", true);
		
		ListUtil.stringAsList(inTokens, buffer.getString("inTokens"));

		// Convert token names to a list of IDs
		inTokenIDs.clear();
		
		for (String tokenName : inTokens)			
			inTokenIDs.add(Tokens.getTokenId(tokenName));
		
		ListUtil.stringAsList(outTokens, buffer.getString("outTokens"));

		// Convert token names to a list of IDs
		outTokenIDs.clear();
		
		for (String tokenName : outTokens)			
			outTokenIDs.add(Tokens.getTokenId(tokenName));
		
		ListUtil.stringAsList(userTokens, buffer.getString("userTokens"));

		// Convert token names to a list of IDs
		userTokenIDs.clear();
		
		for (String tokenName : userTokens)			
			userTokenIDs.add(Tokens.getTokenId(tokenName));
		
		lexemId = buffer.getInteger("lexemId", 0);
		pattern = buffer.getString("pattern", "");
		sample = buffer.getString("sample", "");
		keepInput = buffer.getBoolean("keepInput");
	}
	
	@Override
	protected void parameters_save(ParametersString buffer) {
		
		buffer.setString("name", name);
		buffer.setString("description", description);
		buffer.setBoolean("enabled", enabled);
		
		// Convert IDs to token names
		inTokens.clear();
		
		for (Integer tokenId : inTokenIDs)
			inTokens.add(Tokens.getTokenName(tokenId));

		buffer.setString("inTokens", ListUtil.listAsString(inTokens));
		
		// Convert IDs to token names
		outTokens.clear();
		
		for (Integer tokenId : outTokenIDs)
			outTokens.add(Tokens.getTokenName(tokenId));

		buffer.setString("outTokens", ListUtil.listAsString(outTokens));
		
		// Convert IDs to token names
		userTokens.clear();
		
		for (Integer tokenId : userTokenIDs)
			userTokens.add(Tokens.getTokenName(tokenId));

		buffer.setString("userTokens", ListUtil.listAsString(userTokens));		
		buffer.setInteger("lexemId", lexemId);		
		buffer.setString("pattern", pattern);
		buffer.setString("sample", sample);
		buffer.setBoolean("keepInput", keepInput);
		
		super.parameters_save(buffer);  // Languages go last
	}

	public String getName() {
		
		return name;
	}

	public void setName(String name) {
		
		this.name = name;
	}

	public String getDescription() {
		
		return description;
	}

	public void setDescription(String description) {
		
		this.description = description;
	}

	public int getLexemId() {
		
		return lexemId;
	}

	public void setLexemId(int lexemId) {
		
		this.lexemId = lexemId;
	}

	public String getPattern() {
		
		return pattern;
	}

	public void setPattern(String pattern) {
		
		this.pattern = pattern;
	}
	
	public String getSample() {
		
		return sample;
	}

	public void setSample(String sample) {
		
		this.sample = sample;
	}
		
	public List<Integer> getInTokenIDs() {
		
		return inTokenIDs;
	}
	
	public List<Integer> getOutTokenIDs() {
		
		return outTokenIDs;
	}
	
	public List<Integer> getUserTokenIDs() {
		
		return userTokenIDs;
	}

	public boolean isEnabled() {
		
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		
		this.enabled = enabled;
	}
	
	public boolean getKeepInput() {
		
		return keepInput;
	}

	public void setKeepInput(boolean keepInput) {
		
		this.keepInput = keepInput;
	}
}
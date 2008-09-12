/**
 * 
 */
package net.sf.okapi.filters.html;

import java.util.HashMap;

/**
 * @author HargraveJE
 *
 */
public interface IHtmlFilterConfiguration {
	
	public boolean isPreserveWhitespace();

	public void setPreserveWhitespace(boolean preserveWhitespace);

	public ExtractionRule getRule(String ruleName);
	
	public void addRule(String ruleName, ExtractionRule rule);
	
	public void clearRules();
		
	public void initializeDefaultRules();

}

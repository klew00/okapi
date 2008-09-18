/**
 * 
 */
package net.sf.okapi.filters.html;

import java.util.Map;

/**
 * @author HargraveJE
 *
 */
public interface IHtmlFilterConfiguration {
	
	public boolean isPreserveWhitespace();

	public void setPreserveWhitespace(boolean preserveWhitespace);

	public Map getRule(String ruleName);
	
	public void addRule(String ruleName, Map rule);
	
	public void clearRules();
		
	public void initializeDefaultRules();

}

package net.sf.okapi.lib.segmentation;

public class LanguageMap {

	protected String    pattern;
	protected String    ruleName;
	
	
	public LanguageMap (String pattern,
		String ruleName)
	{
		this.pattern = pattern;
		this.ruleName = ruleName;
	}
	
	public String getPattern () {
		return pattern;
	}
	
	public String getRuleName () {
		return ruleName;
	}
}

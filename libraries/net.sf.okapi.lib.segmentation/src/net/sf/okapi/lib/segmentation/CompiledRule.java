package net.sf.okapi.lib.segmentation;

import java.util.regex.Pattern;

class CompiledRule {
	
	protected Pattern   pattern;
	protected boolean   isBreak;
	
	CompiledRule (String pattern,
		boolean isBreak)
	{
		this.pattern = Pattern.compile(pattern);
		this.isBreak = isBreak;
	}
}

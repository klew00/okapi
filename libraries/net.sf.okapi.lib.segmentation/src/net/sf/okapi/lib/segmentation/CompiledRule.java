package net.sf.okapi.lib.segmentation;

class CompiledRule {
	
	protected String    pattern;
	protected boolean   isBreak;
	
	CompiledRule (String pattern,
		boolean isBreak)
	{
		this.pattern = pattern;
		this.isBreak = isBreak;
	}
}

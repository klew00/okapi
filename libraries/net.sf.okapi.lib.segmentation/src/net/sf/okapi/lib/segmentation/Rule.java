package net.sf.okapi.lib.segmentation;

public class Rule {

	protected String    before;
	protected String    after;
	protected boolean   isBreak;
	
	
	public Rule () {
		before = "";
		after = "";
		isBreak = false;
	}
	
	public Rule (String before,
		String after,
		boolean isBreak)
	{
		if ( before == null ) this.before = "";
		else this.before = before;
		
		if ( after == null ) this.after = "";
		else this.after = after;
		
		this.isBreak = isBreak;
	}
	
	public String getBefore () {
		return before;
	}
	
	public String getAfter () {
		return after;
	}
	
	public boolean isBreak () {
		return isBreak;
	}
}

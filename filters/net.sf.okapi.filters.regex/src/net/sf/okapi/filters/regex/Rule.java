package net.sf.okapi.filters.regex;

public class Rule {

	public static final int       RULETYPE_STRING     = 0;
	public static final int       RULETYPE_CONTENT    = 1;
	public static final int       RULETYPE_COMMENT    = 2;
	public static final int       RULETYPE_NOTRANS    = 3;
	
	protected String              name;
	protected String              start;
	protected String              end;
	protected String              startName;
	protected String              endName;
	protected String              splitters;
	protected int                 ruleType;
	protected boolean             preserveWS;

	public Rule () {
	}
	
	public Rule (Rule obj) {
		name = obj.name;
		start = obj.start;
		end = obj.end;
		splitters = obj.splitters;
		ruleType = obj.ruleType;
		preserveWS = obj.preserveWS;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String value) {
		name = value;
	}
	
	public String getStart () {
		return start;
	}
	
	public void setStart (String value) {
		start = value;
	}

	public String getEnd () {
		return end;
	}
	
	public void setEnd (String value) {
		end = value;
	}

	public String getStartName () {
		return startName;
	}
	
	public void setStartName (String value) {
		startName = value;
	}

	public String getEndName () {
		return endName;
	}
	
	public void setEndName (String value) {
		endName = value;
	}

	public String getSplitters () {
		return splitters;
	}
	
	public void setSplitters (String value) {
		splitters = value;
	}
	
	public int getRuleType () {
		return ruleType;
	}
	
	public void setRuleType (int value) {
		ruleType = value;
	}
	
	public boolean preserveSpace () {
		return preserveWS;
	}
	
	public void setPreserveSpace (boolean value) {
		preserveWS = value;
	}
}

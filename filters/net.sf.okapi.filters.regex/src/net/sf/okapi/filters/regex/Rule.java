package net.sf.okapi.filters.regex;

public class Rule {

	public static final int       RULETYPE_STRING     = 0;
	public static final int       RULETYPE_CONTENT    = 1;
	public static final int       RULETYPE_COMMENT    = 2;
	public static final int       RULETYPE_NOTRANS    = 3;
	
	protected String              start;
	protected String              nameInStart;
	protected String              typeInStart;
	protected String              end;
	protected String              splitters;
	protected int                 ruleType;
	
}

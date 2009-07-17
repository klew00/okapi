package org.w3c.its;

class ITSRule {
	
	int ruleType;
	boolean isInternal;
	String selector;
	boolean flag;
	int value;
	String info;
	int infoType;
	String idPointer;

	public ITSRule (int type) {
		ruleType = type;
	}
}

package net.sf.okapi.filters.tmx;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters{

	protected String startGroupTags;
	protected String documentPartTags;
	
	protected boolean escapeGT;

	public Parameters () {
		reset();
	}	

	public void reset() {
		startGroupTags = "<tmx><header><body><ude>";
		documentPartTags = "<note><prop><map>";
		escapeGT = false;
	}
	
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		startGroupTags = buffer.getString("startGroupTags", startGroupTags);
		documentPartTags = buffer.getString("documentPartTags", documentPartTags);
		escapeGT = buffer.getBoolean("escapeGT", escapeGT);
	}

	public String toString () {
		buffer.reset();
		buffer.setString("stateValues", startGroupTags);
		buffer.setString("documentPartTags", documentPartTags);		
		buffer.setBoolean("escapeGT", escapeGT);		
		return buffer.toString();
	}
}

package net.sf.okapi.filters.ts;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters{

	public boolean escapeGT;

	public Parameters () {
		reset();
	}	

	public void reset() {
		escapeGT = false;
	}
	
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		escapeGT = buffer.getBoolean("escapeGT", escapeGT);
	}

	public String toString () {
		buffer.reset();
		buffer.setBoolean("escapeGT", escapeGT);		
		return buffer.toString();
	}
}

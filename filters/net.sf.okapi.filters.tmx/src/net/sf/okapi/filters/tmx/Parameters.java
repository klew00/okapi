package net.sf.okapi.filters.tmx;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters{

	protected boolean processAllTargets;
	protected boolean consolidateDpSkeleton;
	protected boolean escapeGT;

	public Parameters () {
		reset();
	}	

	public void reset() {
		escapeGT = false;
		processAllTargets=true;
		consolidateDpSkeleton=true;
	}
	
	public void fromString(String data) {
		reset();
		buffer.fromString(data);
		escapeGT = buffer.getBoolean("escapeGT", escapeGT);
		processAllTargets = buffer.getBoolean("processAllTargets", processAllTargets);
		consolidateDpSkeleton = buffer.getBoolean("consolidateDpSkeleton", consolidateDpSkeleton);
	}

	public String toString () {
		buffer.reset();
		buffer.setBoolean("escapeGT", escapeGT);		
		buffer.setBoolean("processAllTargets", processAllTargets);
		buffer.setBoolean("consolidateDpSkeleton", consolidateDpSkeleton);
		return buffer.toString();
	}
}

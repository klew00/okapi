package net.sf.okapi.filters.tmx;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ParametersDescription;

public class Parameters extends BaseParameters{

	//--TODO change back to protected when the testing setup is completed-- 
	public boolean processAllTargets;
	public boolean consolidateDpSkeleton;
	public boolean escapeGT;

	public Parameters () {
		reset();
	}	

	public void reset() {
		escapeGT = false;
		processAllTargets=true;
		consolidateDpSkeleton=true;
	}
	
	public boolean getProcessAllTargets () {
		return processAllTargets;
	}

	public void setProcessAllTargets (boolean processAllTargets) {
		this.processAllTargets = processAllTargets;
	}

	public boolean getConsolidateDpSkeleton() {
		return consolidateDpSkeleton;
	}

	public void setConsolidateDpSkeleton (boolean consolidateDpSkeleton) {
		this.consolidateDpSkeleton = consolidateDpSkeleton;
	}

	public boolean getEscapeGT () {
		return escapeGT;
	}

	public void setEscapeGT (boolean escapeGT) {
		this.escapeGT = escapeGT;
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

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add("escapeGT", "Escape the greater-than characters", null);
		desc.add("processAllTargets", "Read all traget entries", null);
		desc.add("consolidateDpSkeleton", "Group all document parts skeleton into one", null);
		return desc;
	}

}

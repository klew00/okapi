package net.sf.okapi.common.filters;

import net.sf.okapi.common.ISkeleton;

/**
 * 
 * Placeholder class until we figure what state info we need to pass to the
 * subfilters
 * 
 * @author Jim Hargrave
 * 
 */
public class FilterState {
	public enum FILTER_STATE {
		INSIDE_TAG, INSIDE_TEXTUNIT, INSIDE_ATTRIBUTE, STANDALONE_TEXTUNIT
	}

	private FILTER_STATE state;
	private String parentId;
	private String parentTextUnitName;
	private ISkeleton startSkeleton;
	private ISkeleton endSkeleton;

	public FilterState(FILTER_STATE state, String parentId,
			ISkeleton startSkeleton, ISkeleton endSkeleton) {
		this.state = state;
		this.parentId = parentId;
		this.startSkeleton = startSkeleton;
		this.endSkeleton = endSkeleton;
	}

	public FilterState() {
		this.state = FILTER_STATE.INSIDE_TEXTUNIT;
	}

	public FILTER_STATE getState() {
		return state;
	}

	public void setState(FILTER_STATE state) {
		this.state = state;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentTextUnitName() {
		return parentTextUnitName;
	}

	public void setParentTextUnitName(String parentTextUnitName) {
		this.parentTextUnitName = parentTextUnitName;
	}

	public ISkeleton getStartSkeleton() {
		return startSkeleton;
	}

	public void setStartSkeleton(ISkeleton startSkeleton) {
		this.startSkeleton = startSkeleton;
	}

	public ISkeleton getEndSkeleton() {
		return endSkeleton;
	}

	public void setEndSkeleton(ISkeleton endSkeleton) {
		this.endSkeleton = endSkeleton;
	}
}
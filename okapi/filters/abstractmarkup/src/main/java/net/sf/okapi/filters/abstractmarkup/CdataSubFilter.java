package net.sf.okapi.filters.abstractmarkup;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.filters.BaseSubFilterAdapter;
import net.sf.okapi.common.filters.FilterState;
import net.sf.okapi.common.filters.IFilter;

public class CdataSubFilter extends BaseSubFilterAdapter {
	private int tuChildCount;

	public CdataSubFilter(IFilter filter, FilterState state) {
		super(filter, state);
		tuChildCount = 0;
	}
	
	public CdataSubFilter(IFilter filter) {
		super(filter);
		tuChildCount = 0;
	}

	@Override
	public void close() {
		super.close();
		tuChildCount = 0;
	}

	@Override
	public Event next() {
		Event e = super.next();
		// subfiltered textunits inherit any name from a parent TU
		if (e.isTextUnit()) {
			if (e.getTextUnit().getName() == null) {
				String parentName = getState().getParentTextUnitName();
				// we need to add a child id so each tu name is unique for this subfiltered content
				if (parentName != null) {
					parentName = parentName + "-" + Integer.toString(++tuChildCount); 
				}
				e.getTextUnit().setName(parentName);
			}
		}
		
		return e;
	}
}

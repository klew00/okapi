package net.sf.okapi.filters.abstractmarkup;

import net.sf.okapi.common.filters.BaseSubFilterAdapter;
import net.sf.okapi.common.filters.FilterState;
import net.sf.okapi.common.filters.IFilter;

public class PcdataSubFilter extends BaseSubFilterAdapter {

	public PcdataSubFilter(IFilter filter, FilterState state) {
		super(filter, state);
	}

	public PcdataSubFilter(IFilter filter) {
		super(filter);
	}
}

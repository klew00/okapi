package net.sf.okapi.steps.common.tufiltering;

import org.junit.Ignore;

import net.sf.okapi.common.resource.ITextUnit;

@Ignore
public class TestTuFilter implements ITextUnitFilter {

	@Override
	public boolean accept(ITextUnit tu) {
		return "tu2".equals(tu.getId());
	}

}

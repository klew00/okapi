package net.sf.okapi.filters.abstractmarkup.ui;

import java.io.File;
import java.io.IOException;

import net.sf.okapi.common.BaseContext;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.TestUtil;
import net.sf.okapi.common.filters.DefaultFilters;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.filters.abstractmarkup.AbstractMarkupParameters;

public class MainTry {
	
	public static void main (String[] args) throws IOException {

		IParameters params = new AbstractMarkupParameters();

		String root = TestUtil.getParentDir(MainTry.class, "/testConfig.yml");
		File file = new File(root+"testConfig.yml");
		params.load(file.toURI(), false);
		
		FilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		DefaultFilters.setMappings(fcMapper, true, true);

		BaseContext context = new BaseContext();
		context.setObject("fcMapper", fcMapper);
		
		Editor editor = new Editor();
		editor.edit(params, false, context);

	}
}

package net.sf.okapi.common.filters.tests;

import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FilterConfigurationTest {

	private FilterConfiguration fc1;
	
	@Before
	public void setUp () throws Exception {
		fc1 = new FilterConfiguration("config1",
			MimeTypeMapper.PROPERTIES_MIME_TYPE,
			"net.sf.okapi.filters.xml.XMLFilter",
			"Config1",
			"Description for Config1.");
	}
	
	@Test
	public void simpleConfigTest () {
		IFilterConfigurationMapper fcm = new FilterConfigurationMapper();
		fcm.addConfiguration(fc1);
		FilterConfiguration cfg = fcm.getConfiguration(fc1.configId);
		assertNotNull(cfg);
		assertEquals(cfg, fc1);
	}

	@Test
	public void getDefaultFromMimeTest () {
		IFilterConfigurationMapper fcm = new FilterConfigurationMapper();
		fcm.addConfigurations("net.sf.okapi.filters.regex.RegexFilter");
		FilterConfiguration cfg = fcm.getDefaultConfiguration("text/x-regex");
		assertNotNull(cfg);
		assertEquals("okf_regex-srt", cfg.configId);
	}

	@Test
	public void createFilterTest () {
		IFilterConfigurationMapper fcm = new FilterConfigurationMapper();
		fcm.addConfigurations("net.sf.okapi.filters.regex.RegexFilter");
		IFilter filter = fcm.createFilter("okf_regex-srt");
		assertNotNull(filter);
	}

	@Test
	public void clearConfigTest () {
		IFilterConfigurationMapper fcm = new FilterConfigurationMapper();
		fcm.addConfiguration(fc1);
		FilterConfiguration res1 = fcm.getConfiguration(fc1.configId);
		assertNotNull(res1);
		fcm.clearConfigurations();
		res1 = fcm.getConfiguration(fc1.configId);
		assertNull(res1);
	}

}

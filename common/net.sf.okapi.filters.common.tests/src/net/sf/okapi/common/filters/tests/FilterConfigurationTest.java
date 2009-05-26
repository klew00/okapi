package net.sf.okapi.common.filters.tests;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
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
	public void clearConfigTest () {
		IFilterConfigurationMapper fcm = new FilterConfigurationMapper();
		fcm.addConfiguration(fc1);
		FilterConfiguration res1 = fcm.getConfiguration(fc1.configId);
		assertNotNull(res1);
		fcm.clearConfigurations();
		res1 = fcm.getConfiguration(fc1.configId);
		assertNull(res1);
	}

	@Test
	public void createFilterTest () {
		String configId = "okf_regex-srt";
		IFilterConfigurationMapper fcm = new FilterConfigurationMapper();
		fcm.addConfigurations("net.sf.okapi.filters.regex.RegexFilter");
		FilterConfiguration cfg = fcm.getConfiguration(configId);
		IFilter filter = fcm.createFilter(configId);
		assertNotNull(filter);
		assertEquals(filter.getClass().getName(), cfg.filterClass);
	}

	@Test
	public void createEditorTest () {
		String configId = "okf_regex-srt";
		String editorClass = "net.sf.okapi.filters.ui.regex.Editor";
		IFilterConfigurationMapper fcm = new FilterConfigurationMapper();
		fcm.addConfigurations("net.sf.okapi.filters.regex.RegexFilter");
		// Get the parameters class name for this filter
		IFilter filter = fcm.createFilter(configId);
		assertNotNull(filter);
		IParameters params = filter.getParameters();
		// Add it to the mapper
		fcm.addEditor(editorClass, params.getClass().getName());
		// Try to instantiate the editor object
		IParametersEditor editor = fcm.createParametersEditor(configId);
		assertNotNull(editor);
		assertNotNull(editorClass, editor.getClass().getName());
	}

}

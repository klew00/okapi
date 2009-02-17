package net.sf.okapi.filters.openxml.tests;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.filters.markupfilter.Parameters;
import net.sf.okapi.filters.openxml.OpenXMLFilter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OpenXMLFullFileTest {
	private OpenXMLFilter openXMLFilter;
	private String[] testFileList;
	private static final int MSWORD=1;
	private static final int MSEXCEL=2;
	private static final int MSPOWERPOINT=3;
	private static final String deary="/net/sf/okapi/filters/openxml/tests/";

	@Before
	public void setUp() throws Exception {
		openXMLFilter = new OpenXMLFilter();
		openXMLFilter.setOptions("en", "UTF-8", true);

		// read all files in the test html directory
		URL url = OpenXMLFullFileTest.class.getResource(deary+"sample.docx");
		File dir = new File(url.toURI()).getParentFile();

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".docx") || name.endsWith(".pptx") || name.endsWith(".xlsx");
			}
		};
		testFileList = dir.list(filter);
	}

	@After
	public void tearDown() {
		openXMLFilter.close();
	}

	@Test
	public void testAll() throws URISyntaxException {
		for (String f : testFileList) {
			String ff = "src"+deary+f;
			try {
				if (f.endsWith(".docx"))
					openXMLFilter.doOneOpenXMLFile(ff,MSWORD,3);
				else if (f.endsWith(".xlsx"))
					openXMLFilter.doOneOpenXMLFile(ff,MSEXCEL,3);
				else if (f.endsWith(".pptx"))
					openXMLFilter.doOneOpenXMLFile(ff,MSPOWERPOINT,3);
				while (openXMLFilter.hasNext()) {
					FilterEvent event = openXMLFilter.next();
				}
			} catch (Exception e) {
				//System.err.println("Error for file: " + f + ": " + e.toString());
				throw new RuntimeException("Error for file: " + f + ": " + e.toString());
			}
		}
	}

	@Test
	public void testNonwellformed() {
		openXMLFilter.doOneOpenXMLFile("/nonwellformed.specialtest",MSWORD,3);
		while (openXMLFilter.hasNext()) {
			FilterEvent event = openXMLFilter.next();
		}
	}
}

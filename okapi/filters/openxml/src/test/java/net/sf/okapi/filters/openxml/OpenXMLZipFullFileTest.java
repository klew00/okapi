/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/
package net.sf.okapi.filters.openxml;

import java.io.File;
import java.io.FilenameFilter;
//import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.RawDocument;
//import net.sf.okapi.filters.markupfilter.Parameters;
//import net.sf.okapi.filters.openxml.OpenXMLContentFilter;
import net.sf.okapi.filters.openxml.OpenXMLFilter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This is a test that filters all files in the data directory.
 */

public class OpenXMLZipFullFileTest {

	private static Logger LOGGER;
	private OpenXMLFilter openXMLFilter;
	private String[] testFileList;
	private LocaleId locEN = LocaleId.fromString("en");
	private LocaleId locENUS = LocaleId.fromString("en-us");

	@Before
	public void setUp() throws Exception {
		LOGGER = LoggerFactory.getLogger(OpenXMLZipFullFileTest.class.getName());
		openXMLFilter = new OpenXMLFilter();
// TZU			LOGGER.addHandler(new LogHandlerSystemOut());
		openXMLFilter.setLogger(LOGGER);
		openXMLFilter.setOptions(locEN, "UTF-8", true);

		// read all files in the test html directory
		URL url = OpenXMLZipFullFileTest.class.getResource("/BoldWorld.docx");
		File dir = new File(Util.getDirectoryName(url.toURI().getPath()));

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return ((name.endsWith(".docx") || name.endsWith(".pptx") || name.endsWith(".xlsx")) && !name.startsWith("Output"));
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
			try {
				URL url = OpenXMLZipFullFileTest.class.getResource("/"+f);
				//URI uriFf = new URI(fff);
				openXMLFilter.open(new RawDocument(url.toURI(), "UTF-8", locENUS),true,true); // TZU ,Level.FINEST); // DWH 4-22-09
				while (openXMLFilter.hasNext()) {
					openXMLFilter.next();
				}
			}
			catch (Exception e) {
				//System.err.println("Error for file: " + f + ": " + e.toString());
				throw new RuntimeException("Error for file: " + f + ": " + e.toString());
			}
		}
	}

	@Test
	public void testNonwellformed() {
		String filename = "/nonwellformed.specialtest";
		try
		{
			URI uriFf = new URI(filename);
			openXMLFilter.open(uriFf,true); // TZU ,Level.FINEST); // DWH 4-22-09
			while (openXMLFilter.hasNext()) {
				Event event = openXMLFilter.next();
				assertNotNull(event);
			}
			throw new RuntimeException("Should have recognized" + filename + " is not an MSOffice 2007 file");
		}
		catch(Exception e)
		{
			//System.err.println("Error for file: " + f + ": " + e.toString());
			filename = "All is swell";
		}
	}
}

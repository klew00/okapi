/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.applications.longhorn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.Util;
import net.sf.okapi.lib.longhornapi.LonghornFile;
import net.sf.okapi.lib.longhornapi.LonghornProject;
import net.sf.okapi.lib.longhornapi.LonghornService;
import net.sf.okapi.lib.longhornapi.impl.rest.RESTService;

import org.junit.Before;
import org.junit.Test;

public class ServiceTest {
	private static final String SERVICE_BASE_URL = "http://localhost:9095/okapi-longhorn";
	private File batchConfig;
	private ArrayList<File> inputFiles;
	
	@Before
	public void init() throws Exception {

		batchConfig = new File(this.getClass().getResource("/html_segment_and_text_mod.bconf").toURI());
		URL input1 = this.getClass().getResource("/rawdocumenttofiltereventsstep.html");
		URL input2 = this.getClass().getResource("/searchandreplacestep.html");
		URL input3 = this.getClass().getResource("/segmentationstep.html");
		inputFiles = new ArrayList<File>();
		inputFiles.add(new File(input1.toURI()));
		inputFiles.add(new File(input2.toURI()));
		inputFiles.add(new File(input3.toURI()));
	}
	
	@Test
	public void dummy() {
		
	}

	//@Test
	public void runRainbowProject() throws Exception {
		
		LonghornService ws = new RESTService(new URI(SERVICE_BASE_URL));
		
		int projCountBefore = ws.getProjects().size();

		// Create project
		LonghornProject proj = ws.createProject();
		
		// Now there should be one project more than before...
		assertEquals(projCountBefore + 1, ws.getProjects().size());
		
		// ... that has still 0 input and output files
		assertEquals(0, proj.getInputFiles().size());
		assertEquals(0, proj.getOutputFiles().size());

		// Post batch configuration
		proj.addBatchConfiguration(batchConfig);

		// Send input files
		for (File inFile : inputFiles) {
			proj.addInputFile(inFile, inFile.getName());
		}

		// Test if upload worked
		assertEquals(inputFiles.size(), proj.getInputFiles().size());

		// Execute pipeline
		proj.executePipeline();

		// Get list of all output files
		ArrayList<LonghornFile> outputFiles = proj.getOutputFiles();
		// Should be the same number of files with this config
		assertEquals(inputFiles.size(), outputFiles.size());

		// Does the fetching of files work?
		for (LonghornFile of : outputFiles) {
			File outputFile = downloadFileToTemp(of.openStream());
			
			assertNotNull(outputFile);
			assertTrue(outputFile.exists());
			assertTrue(outputFile.length() > 0);
			outputFile.delete();
		}

		// Delete project
		proj.delete();
		
		// Has the project been deleted?
		assertEquals(projCountBefore, ws.getProjects().size());
	}

	private File downloadFileToTemp(InputStream remoteFile) throws IOException {
		
		File tempFile = File.createTempFile("okapi-longhorn", "outfile");
		Util.copy(remoteFile, tempFile);
		remoteFile.close();
		return tempFile;
	}
}

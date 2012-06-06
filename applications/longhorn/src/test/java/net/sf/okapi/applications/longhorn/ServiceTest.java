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

import org.junit.Test;

public class ServiceTest {
	private static final String SERVICE_BASE_URL = "http://localhost:9095/okapi-longhorn";
	
	@Test
	public void runRainbowProject() throws Exception {
		
		LonghornService ws = new RESTService(new URI(SERVICE_BASE_URL));
		
		int projCountBefore = ws.getProjects().size();

		//
		// Create project
		//
		LonghornProject proj = ws.createProject();
		
		// Now there should be one project more than before...
		assertEquals(projCountBefore + 1, ws.getProjects().size());
		
		// ... that has still 0 input and output files
		assertEquals(0, proj.getInputFiles().size());
		assertEquals(0, proj.getOutputFiles().size());

		//
		// Post batch configuration
		//
		URL bconf = this.getClass().getResource("/html_segment_and_text_mod.bconf");
		File bconfFile = new File(bconf.toURI());
		proj.addBatchConfiguration(bconfFile);

		//
		// Send input files
		//
		
		// First by single upload
		URL input1 = this.getClass().getResource("/rawdocumenttofiltereventsstep.html");
		File file1 = new File(input1.toURI());
		// in the root directory
		proj.addInputFile(file1, file1.getName());
		// and in a sub-directory
		proj.addInputFile(file1, "samefile/" + file1.getName());
		
		ArrayList<LonghornFile> inputFiles = proj.getInputFiles();
		assertEquals(2, inputFiles.size());
		assertEquals(0, proj.getOutputFiles().size());
		ArrayList<String> relFilePaths = new ArrayList<String>();
		for (LonghornFile f : inputFiles) {
			relFilePaths.add(f.getRelativePath());
		}
		assertTrue(relFilePaths.contains("rawdocumenttofiltereventsstep.html"));
		assertTrue(relFilePaths.contains("samefile/rawdocumenttofiltereventsstep.html"));
		
		// Then by package upload
		URL input2 = this.getClass().getResource("/more_files.zip");
		File file2 = new File(input2.toURI());
		proj.addInputFilesFromZip(file2);
		
		inputFiles = proj.getInputFiles();
		assertEquals(4, inputFiles.size());
		assertEquals(0, proj.getOutputFiles().size());

		// Are the input file names as expected (with 1 file in a sub-directory)?
		relFilePaths = new ArrayList<String>();
		for (LonghornFile f : inputFiles) {
			relFilePaths.add(f.getRelativePath());
		}
		assertTrue(relFilePaths.contains("rawdocumenttofiltereventsstep.html"));
		assertTrue(relFilePaths.contains("samefile/rawdocumenttofiltereventsstep.html"));
		assertTrue(relFilePaths.contains("searchandreplacestep.html"));
		assertTrue(relFilePaths.contains("subdir1/segmentationstep.html"));

		//
		// Execute pipeline
		//
		proj.executePipeline();

		//
		// Get output files
		//
		ArrayList<LonghornFile> outputFiles = proj.getOutputFiles();
		
		// Should be the same number of files with this config
		assertEquals(inputFiles.size(), outputFiles.size());

		// Are the names as expected?
		relFilePaths = new ArrayList<String>();
		for (LonghornFile f : outputFiles) {
			relFilePaths.add(f.getRelativePath());
		}
		assertTrue(relFilePaths.contains("rawdocumenttofiltereventsstep.html"));
		assertTrue(relFilePaths.contains("samefile/rawdocumenttofiltereventsstep.html"));
		assertTrue(relFilePaths.contains("searchandreplacestep.html"));
		assertTrue(relFilePaths.contains("subdir1/segmentationstep.html"));

		// Does the fetching of files work?
		for (LonghornFile of : outputFiles) {
			File outputFile = downloadFileToTemp(of.openStream());
			
			assertNotNull(outputFile);
			assertTrue(outputFile.exists());
			assertTrue(outputFile.length() > 0);
			outputFile.delete();
		}
		
		// also check if output files can be fetched as a zip file
		InputStream zippedOutputFiles = proj.getOutputFilesAsZip();
		assertNotNull(zippedOutputFiles);
		
		// check if fetching single files as a zip also works
		LonghornFile firstOutputFile = outputFiles.get(0);
		InputStream zippedFile = firstOutputFile.openStreamToZip();
		assertNotNull(zippedFile);

		//
		// Delete project
		//
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

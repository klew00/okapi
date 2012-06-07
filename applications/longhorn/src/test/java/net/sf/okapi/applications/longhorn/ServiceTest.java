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

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import net.sf.okapi.common.Util;
import net.sf.okapi.lib.longhornapi.LonghornFile;
import net.sf.okapi.lib.longhornapi.LonghornProject;
import net.sf.okapi.lib.longhornapi.LonghornService;
import net.sf.okapi.lib.longhornapi.impl.rest.RESTService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServiceTest {
	private static final String SERVICE_BASE_URL = "http://localhost:9095/okapi-longhorn";
	private LonghornService ws;
	private LonghornProject proj;
	private File inputFile;
	private File inputZip;
	private File bconf;
	
	@Before
	public void prep() throws Exception {
		ws = new RESTService(new URI(SERVICE_BASE_URL));
		proj = ws.createProject();
		
		URL input1 = this.getClass().getResource("/rawdocumenttofiltereventsstep.html");
		inputFile = new File(input1.toURI());
		
		URL input2 = this.getClass().getResource("/more_files.zip");
		inputZip = new File(input2.toURI());
		
		URL bconfUrl = this.getClass().getResource("/html_segment_and_text_mod.bconf");
		bconf = new File(bconfUrl.toURI());
	}
	
	@After
	public void cleanup() {
		if (proj != null)
			proj.delete();
	}
	
	@Test
	public void wrongUrlThrowsException() throws Exception {
		try {
			ws = new RESTService(new URI(SERVICE_BASE_URL + "wrong_url"));
			fail("Invalid URL should have caused Exception");
		}
		catch (IllegalArgumentException e) {
		}
	}
	
	@Test
	public void createAndDeleteProjects() {
		int projCountBefore = ws.getProjects().size();
		
		LonghornProject proj = ws.createProject();
		assertEquals(projCountBefore + 1, ws.getProjects().size());
		
		proj.delete();
		assertEquals(projCountBefore, ws.getProjects().size());
	}
	
	@Test
	public void newProjectIsEmpty() {
		assertEquals(0, proj.getInputFiles().size());
		assertEquals(0, proj.getOutputFiles().size());
	}
	
	@Test
	public void addingInputFile() throws FileNotFoundException {
		proj.addInputFile(inputFile, inputFile.getName());
		assertEquals(1, proj.getInputFiles().size());
		assertEquals(0, proj.getOutputFiles().size());
		ArrayList<LonghornFile> inputFiles = proj.getInputFiles();
		assertEquals("rawdocumenttofiltereventsstep.html", inputFiles.get(0).getRelativePath());
	}
	
	@Test
	public void addingInputFileInSubdir() throws FileNotFoundException {
		proj.addInputFile(inputFile, "samefile/" + inputFile.getName());
		assertEquals(1, proj.getInputFiles().size());
		assertEquals(0, proj.getOutputFiles().size());
		ArrayList<LonghornFile> inputFiles = proj.getInputFiles();
		assertEquals("samefile/rawdocumenttofiltereventsstep.html", inputFiles.get(0).getRelativePath());
	}
	
	@Test
	public void addingInputFilesFromZip() throws FileNotFoundException {
		proj.addInputFilesFromZip(inputZip);
		
		ArrayList<LonghornFile> inputFiles = proj.getInputFiles();
		assertEquals(2, inputFiles.size());
		assertEquals(0, proj.getOutputFiles().size());

		// Are the input file names as expected (with 1 file in a sub-directory)?
		ArrayList<String> relFilePaths = new ArrayList<String>();
		for (LonghornFile f : inputFiles) {
			relFilePaths.add(f.getRelativePath());
		}
		assertTrue(relFilePaths.contains("searchandreplacestep.html"));
		assertTrue(relFilePaths.contains("subdir1/segmentationstep.html"));
	}
	
	@Test
	public void addBconf() throws FileNotFoundException {
		proj.addBatchConfiguration(bconf);
	}
	
	@Test
	public void executePipelineCreatesOutputFiles() throws FileNotFoundException {
		prepProject();
		
		proj.executePipeline();

		ArrayList<LonghornFile> inputFiles = proj.getInputFiles();
		ArrayList<LonghornFile> outputFiles = proj.getOutputFiles();
		
		// Should be the same number of files with this config
		assertEquals(inputFiles.size(), outputFiles.size());

		// Are the names as expected?
		ArrayList<String> relFilePaths = new ArrayList<String>();
		for (LonghornFile f : outputFiles) {
			relFilePaths.add(f.getRelativePath());
		}
		assertTrue(relFilePaths.contains("rawdocumenttofiltereventsstep.html"));
		assertTrue(relFilePaths.contains("samefile/rawdocumenttofiltereventsstep.html"));
	}
	
	@Test
	public void executePipelineWithLangParametersCreatesOutputFiles() throws FileNotFoundException {
		prepProject();
		
		proj.executePipeline("en", "de");

		ArrayList<LonghornFile> inputFiles = proj.getInputFiles();
		ArrayList<LonghornFile> outputFiles = proj.getOutputFiles();
		
		// Should be the same number of files with this config
		assertEquals(inputFiles.size(), outputFiles.size());

		// Are the names as expected?
		ArrayList<String> relFilePaths = new ArrayList<String>();
		for (LonghornFile f : outputFiles) {
			relFilePaths.add(f.getRelativePath());
		}
		assertTrue(relFilePaths.contains("rawdocumenttofiltereventsstep.html"));
		assertTrue(relFilePaths.contains("samefile/rawdocumenttofiltereventsstep.html"));
	}
	
	@Test
	public void executePipelineWithMultipleTargetLangsCreatesOutputFiles() throws FileNotFoundException {
		prepProject();
		
		proj.executePipeline("en", Arrays.asList("de", "it", "fr"));

		ArrayList<LonghornFile> inputFiles = proj.getInputFiles();
		ArrayList<LonghornFile> outputFiles = proj.getOutputFiles();
		
		// Should be the same number of files with this config
		assertEquals(inputFiles.size(), outputFiles.size());

		// Are the names as expected?
		ArrayList<String> relFilePaths = new ArrayList<String>();
		for (LonghornFile f : outputFiles) {
			relFilePaths.add(f.getRelativePath());
		}
		assertTrue(relFilePaths.contains("rawdocumenttofiltereventsstep.html"));
		assertTrue(relFilePaths.contains("samefile/rawdocumenttofiltereventsstep.html"));
	}
	
	@Test
	public void fetchSingleOutputFile() throws IOException {
		prepProject();
		proj.executePipeline();

		ArrayList<LonghornFile> outputFiles = proj.getOutputFiles();
		assertEquals(2, outputFiles.size());
		
		for (LonghornFile of : outputFiles) {
			File outputFile = downloadFileToTemp(of.openStream());
			
			assertNotNull(outputFile);
			assertTrue(outputFile.exists());
			assertTrue(outputFile.length() > 0);
			outputFile.delete();
		}
	}
	
	@Test
	public void fetchOutputFilesAsZip() throws FileNotFoundException {
		prepProject();
		proj.executePipeline();

		InputStream zippedOutputFiles = proj.getOutputFilesAsZip();
		assertNotNull(zippedOutputFiles);
	}
	
	@Test
	public void fetchOutputFilesAsZipThrowsExceptionForNoFiles() throws FileNotFoundException {
		try {
			proj.getOutputFilesAsZip();
			fail("No output files should cause an exception.");
		}
		catch (IllegalStateException e) {
		}
	}
	
	@Test
	public void fetchOutputFileAsZip() throws FileNotFoundException {
		prepProject();
		proj.executePipeline();

		ArrayList<LonghornFile> outputFiles = proj.getOutputFiles();
		LonghornFile firstOutputFile = outputFiles.get(0);
		InputStream zippedFile = firstOutputFile.openStreamToZip();
		assertNotNull(zippedFile);
	}
	
	private void prepProject() throws FileNotFoundException {
		proj.addBatchConfiguration(bconf);
		proj.addInputFile(inputFile, inputFile.getName());
		proj.addInputFile(inputFile, "samefile/" + inputFile.getName());
	}

	private File downloadFileToTemp(InputStream remoteFile) throws IOException {
		
		File tempFile = File.createTempFile("okapi-longhorn", "outfile");
		Util.copy(remoteFile, tempFile);
		remoteFile.close();
		return tempFile;
	}
}

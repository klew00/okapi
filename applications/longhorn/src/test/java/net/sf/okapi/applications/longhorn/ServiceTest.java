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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import net.sf.okapi.applications.longhorn.transport.XMLStringList;
import net.sf.okapi.common.Util;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Before;
import org.junit.Test;

public class ServiceTest {
	private static final String SERVICE_BASE_URL = "http://localhost:9095/okapi-longhorn";
	private HttpClient client;
	private File batchConfig;
	private ArrayList<File> inputFiles;
	
	@Before
	public void init() throws Exception {
		client = new HttpClient();

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

		// Get list of all projects
		Collection<String> projects = getList(SERVICE_BASE_URL + "/projects");
		assertNotNull(projects);
		int projCountBefore = projects.size();

		// Create project
		String projectUri = createProject();
		assertNotNull(projectUri);

		// Now there should be one project more than before...
		projects = getList(SERVICE_BASE_URL + "/projects");
		assertNotNull(projects);
		assertEquals(projCountBefore + 1, projects.size());
		
		// ... that has still 0 input and output files
		Collection<String> inputFileNames = getList(projectUri + "/inputFiles");
		Collection<String> outputFileNames = getList(projectUri + "/outputFiles");
		assertNotNull(inputFileNames);
		assertNotNull(outputFileNames);
		assertEquals(0, inputFileNames.size());
		assertEquals(0, outputFileNames.size());

		// Post batch configuration
		Part[] parts = {
				new FilePart(WorkspaceUtils.BATCH_CONF_PARAM, batchConfig.getName(), batchConfig)};
		post(projectUri + "/batchConfiguration", parts);

		// Send input files
		for (File inputFile : inputFiles) {

			String uri = projectUri + "/inputFiles/" + inputFile.getName();
			Part[] inputParts = {
					new FilePart(WorkspaceUtils.INPUT_FILE_PARAM, inputFile.getName(), inputFile)};
			put(uri, inputParts);
		}

		// Test if upload worked
		inputFileNames = getList(projectUri + "/inputFiles");
		assertNotNull(inputFileNames);
		assertEquals(inputFiles.size(), inputFileNames.size());

		// Execute pipeline
		post(projectUri + "/tasks/execute", null);

		// Get list of all output files
		outputFileNames = getList(projectUri + "/outputFiles");
		assertNotNull(outputFileNames);
		assertEquals(inputFiles.size(), outputFileNames.size());
		
		// Does the fetching of files work?
		for (String filename : outputFileNames) {
			File outputFile = downloadFileToTemp(projectUri + "/outputFiles/" + filename);
			assertNotNull(outputFile);
			assertTrue(outputFile.exists());
			assertTrue(outputFile.length() > 0);
			outputFile.delete();
		}

		// Delete project
		delete(projectUri);
		
		projects = getList(SERVICE_BASE_URL + "/projects");
		assertNotNull(projects);
		assertEquals(projCountBefore, projects.size());
	}

	private File downloadFileToTemp(String uri) throws IOException {
		
		InputStream remoteFile = new URL(uri).openStream();
		File tempFile = File.createTempFile("okapi-longhorn", "outfile");
		Util.copy(remoteFile, tempFile);
		remoteFile.close();
		return tempFile;
	}

	private void put(String uri, Part[] params) throws IOException {
		
		PutMethod putMethod = new PutMethod(uri);
		putMethod.setRequestEntity(new MultipartRequestEntity(params, putMethod.getParams()));
		int status = client.executeMethod(putMethod);
		assertEquals(HttpStatus.SC_OK, status);
		putMethod.releaseConnection();
	}

	private void delete(String uri) throws IOException {
		
		DeleteMethod delMethod = new DeleteMethod(uri);
		int status = client.executeMethod(delMethod);
		assertEquals(HttpStatus.SC_OK, status);
		delMethod.releaseConnection();
	}

	private void post(String uri, Part[] parts) throws IOException {
		
		PostMethod postMethod = new PostMethod(uri);
		if (parts != null)
			postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
		
		int status = client.executeMethod(postMethod);
		
		if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR)
			System.err.println("Error: " + postMethod.getResponseBodyAsString());
		
		assertEquals(HttpStatus.SC_OK, status);
		postMethod.releaseConnection();
	}

	private String createProject() throws IOException {
		
		PostMethod postMethod = new PostMethod(SERVICE_BASE_URL + "/projects/new");
		int status = client.executeMethod(postMethod);
		assertEquals(HttpStatus.SC_CREATED, status);
		Header projectUri = postMethod.getResponseHeader("Location");
		assertNotNull(projectUri);
		
		postMethod.releaseConnection();
		return projectUri.getValue();
	}

	private Collection<String> getList(String uri) throws IOException {
		
		GetMethod getMethod = new GetMethod(uri);
		int status = client.executeMethod(getMethod);
		String xmlList = getMethod.getResponseBodyAsString();
		assertEquals(HttpStatus.SC_OK, status);
		getMethod.releaseConnection();
		
		return XMLStringList.unmarshal(xmlList);
	}
}

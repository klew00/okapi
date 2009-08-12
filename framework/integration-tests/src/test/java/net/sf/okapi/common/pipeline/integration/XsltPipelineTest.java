package net.sf.okapi.common.pipeline.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.pipelinedriver.IPipelineDriver;
import net.sf.okapi.common.pipelinedriver.PipelineContext;
import net.sf.okapi.common.pipelinedriver.PipelineDriver;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.steps.common.FilterEventsWriterStep;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class XsltPipelineTest {

	private IPipelineDriver driver;
	private IFilterConfigurationMapper fcMapper;
	
	@Before
	public void setUp() throws Exception {
		fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.xml.XMLFilter");
		driver = new PipelineDriver();
		((PipelineContext)driver.getPipeline().getContext()).setFilterConfigurationMapper(fcMapper);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void runXsltPipeline() throws URISyntaxException,
		UnsupportedEncodingException, FileNotFoundException, IOException
	{
		driver.clearItems();

		// Input resource
		URL inputXml = XsltPipelineTest.class.getResource("test.xml");

		// Make copy of input
		InputStream in1 = XsltPipelineTest.class.getResourceAsStream("identity.xsl");
		driver.addStep(new XsltTransformStep(in1));

		// Remove b tags from input
		InputStream in2 = XsltPipelineTest.class.getResourceAsStream("remove_b_tags.xsl");
		driver.addStep(new XsltTransformStep(in2));

		// Filtering step - converts resource to events
		driver.addStep(new RawDocumentToFilterEventsStep());

		// Writer step - converts events to a resource
		driver.addStep(new FilterEventsWriterStep());

		RawDocument rd = new RawDocument(inputXml.toURI(), "UTF-8", "en");
		rd.setFilterConfigId("okf_xml");
		File outFile = new File("output.xml");
		driver.addBatchItem(rd, outFile.toURI(), "UTF-8");
		driver.processBatch();

		// Read the result and compare
		StringBuilder tmp = new StringBuilder();
		BufferedReader reader;
		reader = new BufferedReader(
			new InputStreamReader(new FileInputStream(outFile), "UTF-8"));
		char[] buf = new char[2048];
		int count = 0;
		while (( count = reader.read(buf)) != -1 ) {
			tmp.append(buf, 0, count);
		}
		assertEquals(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<start fileID=\"02286_000_000\"><para id=\"1\">This is a test with .</para></start>",
			tmp.toString());
	}

}

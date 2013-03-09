package net.sf.okapi.filters;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.pipelinedriver.PipelineDriver;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;

public class XmlMemoryLeakTest {

	private static FilterConfigurationMapper fcMapper;	
	private static LocaleId locEN = LocaleId.fromString("EN");
	
	public static void setUp() throws Exception {
		// Create the mapper
		fcMapper = new FilterConfigurationMapper();
		// Fill it with the default configurations of several filters
		fcMapper.addConfigurations("net.sf.okapi.filters.openxml.OpenXMLFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.xml.XMLFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.html.HtmlFilter");
	}

	private static PipelineDriver simplePipeline() throws Exception {
		// Create the driver
		PipelineDriver driver = new PipelineDriver();
		driver.setFilterConfigurationMapper(fcMapper);
		driver.addStep(new RawDocumentToFilterEventsStep());
		return driver;
	}
	
	private static URI getUri(String fileName) throws URISyntaxException {
		URL url = XmlMemoryLeakTest.class.getResource(fileName);
		return url.toURI();
	}
		
	public static void main(String[] args) throws Exception {
		setUp();
		
		PipelineDriver pd = simplePipeline();
		for (int i = 0; i <= 10000000L; i++) {
			RawDocument rd = new RawDocument(getUri("/xml/input.xml"), "UTF-8", locEN);
			rd.setFilterConfigId("okf_xml");
			pd.addBatchItem(rd, (new File("genericOutput.txt")).toURI(), "UTF-8");
			pd.processBatch();
			pd.clearItems();
			System.out.println(i);
		}				
	}
}

package net.sf.okapi.common.pipeline;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.pipelinedriver.PipelineDriver;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;
import net.sf.okapi.steps.segmentation.Parameters;
import net.sf.okapi.steps.segmentation.SegmentationStep;

public class DocxMemoryLeakTest {

	private static FilterConfigurationMapper fcMapper;	
	private static LocaleId locEN = LocaleId.fromString("EN");
	private static LocaleId locES = LocaleId.fromString("ES");

	public static void setUp() throws Exception {
		// Create the mapper
		fcMapper = new FilterConfigurationMapper();
		// Fill it with the default configurations of several filters
		fcMapper.addConfigurations("net.sf.okapi.filters.openxml.OpenXMLFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.xml.XMLFilter");
	}

	private static PipelineDriver simplePipeline() throws Exception {
		// Create the driver
		PipelineDriver driver = new PipelineDriver();
		driver.setFilterConfigurationMapper(fcMapper);

		driver.addStep(new RawDocumentToFilterEventsStep());
		
//		SegmentationStep ss = new SegmentationStep();
//		Parameters sp = (Parameters)ss.getParameters();
//		sp.setSourceSrxPath(new File(getUri("/test.srx")).getAbsolutePath());
//		driver.addStep(ss);initialize
									
		return driver;
	}
	
	private static URI getUri(String fileName) throws URISyntaxException {
		URL url = DocxMemoryLeakTest.class.getResource(fileName);
		return url.toURI();
	}
		
	public static void main(String[] args) throws Exception {
		setUp();
		
		PipelineDriver pd = simplePipeline();
		for (int i = 0; i <= 10000000L; i++) {
			RawDocument rd = new RawDocument(getUri("/docx/OpenXML_text_reference_v1_2.docx"), "UTF-8", locEN);
			rd.setFilterConfigId("okf_openxml");
			pd.addBatchItem(rd, (new File("genericOutput.txt")).toURI(), "UTF-8");
			pd.processBatch();
			pd.clearItems();
			System.out.println(i);
		}				
	}
}

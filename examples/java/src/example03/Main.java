package example03;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;
import net.sf.okapi.steps.common.FilterEventsWriterStep;
import net.sf.okapi.common.pipeline.IPipelineDriver;
import net.sf.okapi.common.pipeline.PipelineDriver;
import net.sf.okapi.common.resource.RawDocument;

/**
 * Example showing an Okapi pipeline running XSLT transformations on a file. The
 * transformed file is then run through the XML filter (broken down into Okapi
 * Events) and then re-written using the generic Event writer.
 */
public class Main {

	public static void main(String[] args)
		throws URISyntaxException, UnsupportedEncodingException
	{
		IPipelineDriver driver = new PipelineDriver();
		
		IFilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.xml.XMLFilter");
		driver.getPipeline().getContext().setFilterConfigurationMapper(fcMapper);

		// Input resource as URL
		URL inputXml = Main.class.getResource("test.xml");

		// Make copy of input using identity XSLT
		InputStream in = Main.class.getResourceAsStream("identity.xsl");
		driver.addStep(new XsltTransformStep(in));

		// Remove b tags from input using remove_b_tags XSLT
		in = Main.class.getResourceAsStream("remove_b_tags.xsl");
		driver.addStep(new XsltTransformStep(in));

		// Filtering step - converts raw resource to events
		driver.addStep(new RawDocumentToFilterEventsStep());

		// Writer step - converts events to a raw resource
		driver.addStep(new FilterEventsWriterStep());

		// Set the info for the input and output
		driver.addBatchItem(
			new RawDocument(inputXml.toURI(), "UTF-8", "en", "fr"),
			"okf_xml", (new File("output.xml")).toURI(), "UTF-8");
		
		// Run the pipeline:
		// (1) XSLT identity conversion step
		// (2) XSLT replace b tag conversion step
		// (3) XML filtering step creates IResource Events
		// (4) Writer step takes Events and writes them out to outStream
		driver.processBatch();

	}
}

package net.sf.okapi.examples.java.example03;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.pipeline.FilterEventsWriterStep;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.pipeline.PipelineDriver;
import net.sf.okapi.common.pipeline.RawDocumentToFilterEventsStep;
import net.sf.okapi.common.pipeline.RawDocumentWriterStep;
import net.sf.okapi.common.resource.RawDocument;

/**
 * Example showing an Okapi pipeline running XSLT transformations on a file. The
 * transformed file is then run through the XML filter (broken down into Okapi
 * Events) and then re-written using the generic Event writer.
 */
public class Main {

	public static void main(String[] args) throws URISyntaxException, UnsupportedEncodingException {
		IPipeline pipeline = new Pipeline();
		pipeline.getContext().setFilterConfigurationMapper(new FilterConfigurationMapper());

		// input resource as URL
		URL inputXml = Main.class.getResource("test.xml");

		// make copy of input using identity XSLT
		InputStream in = Main.class.getResourceAsStream("identity.xsl");
		pipeline.addStep(new XsltTransformStep(in));

		// remove b tags from input using remove_b_tags XSLT
		in = Main.class.getResourceAsStream("remove_b_tags.xsl");
		pipeline.addStep(new XsltTransformStep(in));

		// filtering step - converts raw resource to events
		pipeline.addStep(new RawDocumentToFilterEventsStep());

		// writer step - converts events to a raw resource
		pipeline.addStep(new FilterEventsWriterStep());

		// writer of the final file
		//TODO: This is not right: we should not have to write an output twice
		// the previous step should be able to write the output, somehow
		pipeline.addStep(new RawDocumentWriterStep());
		
		// run the pipeline:
		// (1) XSLT identity conversion step
		// (2) XSLT replace b tag conversion step
		// (3) XML filtering step creates IResource Events
		// (4) Writer step takes Events and writes them out to outStream
		
		PipelineDriver driver = new PipelineDriver();
		driver.setPipeline(pipeline);
		driver.addBatchItem(
			new RawDocument(inputXml.toURI(), "UTF-8", "en", "fr"),
			"okapi.xml", "output.xml", "UTF-8");
		driver.processBatch();

		// destroy the pipeline and all steps - clean up resources
		pipeline.destroy();

	}
}

package example03;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.pipeline.RawDocumentToEventsStep;
import net.sf.okapi.common.pipeline.EventsWriterStep;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.xml.XMLFilter;

/**
 * Example showing an Okapi pipeline running XSLT transformations on a file. The
 * transformed file is then run through the XML filter (broken down into Okapi
 * Events) and then re-written using the generic Event writer.
 */
public class Main {

	public static void main(String[] args) throws URISyntaxException, UnsupportedEncodingException {
		IPipeline pipeline = new Pipeline();

		// input resource as URL
		URL inputXml = Main.class.getResource("test.xml");

		// make copy of input using identity XSLT
		InputStream in = Main.class.getResourceAsStream("identity.xsl");
		pipeline.addStep(new XsltTransformStep(in));

		// remove b tags from input using remove_b_tags XSLT
		in = Main.class.getResourceAsStream("remove_b_tags.xsl");
		pipeline.addStep(new XsltTransformStep(in));

		// filtering step - converts raw resource to events
		IFilter filter = new XMLFilter();
		IPipelineStep filterStep = new RawDocumentToEventsStep(filter);
		pipeline.addStep(filterStep);

		// writer step - converts events to a raw resource
		IFilterWriter writer = filter.createFilterWriter();
		writer.setOptions("en", "UTF-8");
		pipeline.addStep(new EventsWriterStep(writer));

		// buffer for the writer step
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		writer.setOutput(outStream);

		// run the pipeline:
		// (1) XSLT identity conversion step
		// (2) XSLT replace b tag conversion step
		// (3) XML filtering step creates IResource Events
		// (4) Writer step takes Events and writes them out to outStream
		pipeline.process(new RawDocument(inputXml.toURI(), "UTF-8", "en"));

		// destroy the pipeline and all steps - clean up resources
		pipeline.destroy();

		// print out resulting XML file from the writer
		System.out.println(new String(outStream.toByteArray(), "UTF-8"));

	}
}

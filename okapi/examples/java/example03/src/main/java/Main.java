/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;
import net.sf.okapi.steps.common.FilterEventsWriterStep;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipelinedriver.IPipelineDriver;
import net.sf.okapi.common.pipelinedriver.PipelineDriver;
import net.sf.okapi.common.resource.RawDocument;

/**
 * Example showing an Okapi pipeline running XSLT transformations on a file. The
 * transformed file is then run through the XML filter (broken down into Okapi
 * Events) and then re-written using the generic Event writer.
 */
public class Main {

	public static void main (String[] args)
		throws URISyntaxException, UnsupportedEncodingException
	{
		IPipelineDriver driver = new PipelineDriver();
		
		IFilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.xml.XMLFilter");
		driver.setFilterConfigurationMapper(fcMapper);

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
		RawDocument rawDoc = new RawDocument(inputXml.toURI(), "UTF-8",
			new LocaleId("en"), new LocaleId("fr"));
		rawDoc.setFilterConfigId("okf_xml");
		driver.addBatchItem(rawDoc, (new File("output.xml")).toURI(), "UTF-8");
		
		// Run the pipeline:
		// (1) XSLT identity conversion step
		// (2) XSLT replace b tag conversion step
		// (3) XML filtering step creates IResource Events
		// (4) Writer step takes Events and writes them out to outStream
		driver.processBatch();
	}
}

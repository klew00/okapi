/*===========================================================================*/
/* Copyright (C) 2008 Jim Hargrave                                           */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.pipeline.tests;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.pipeline.FilterPipelineStepAdaptor;
import net.sf.okapi.common.pipeline.FilterWriterPipelineStepAdaptor;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.resource.InputResource;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.filters.html.HtmlFilter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FilterRoundtripTest {

	@Before
	public void setUp() {

	}

	@Test
	public void runPipelineFromString() {
		IPipeline pipeline = new Pipeline();

		HtmlFilter htmlFilter = new HtmlFilter();
		htmlFilter.setOptions("en", "UTF-8", true);

		GenericSkeletonWriter genericSkeletonWriter = new GenericSkeletonWriter();
		GenericFilterWriter genericFilterWriter = new GenericFilterWriter(
				genericSkeletonWriter);
		genericFilterWriter.setOptions("es", "UTF-16LE");
		genericFilterWriter.setOutput("genericOutput.txt");

		pipeline.addStep(new FilterPipelineStepAdaptor(htmlFilter));
		pipeline.addStep(new FilterWriterPipelineStepAdaptor(
				genericFilterWriter));

		InputResource fr = new InputResource(
				"<p>Before <input type=\"radio\" name=\"FavouriteFare\" value=\"spam\" checked=\"checked\"/> after.</p>",
				"en");
		pipeline.process(fr);
	}

	@Test
	public void runPipelineFromStream() {
		IPipeline pipeline = new Pipeline();

		HtmlFilter htmlFilter = new HtmlFilter();
		htmlFilter.setOptions("en", "UTF-8", true);

		GenericSkeletonWriter genericSkeletonWriter = new GenericSkeletonWriter();
		GenericFilterWriter genericFilterWriter = new GenericFilterWriter(
				genericSkeletonWriter);
		genericFilterWriter.setOptions("es", "UTF-8");
		genericFilterWriter.setOutput("genericOutput.txt");

		pipeline.addStep(new FilterPipelineStepAdaptor(htmlFilter));
		pipeline.addStep(new FilterWriterPipelineStepAdaptor(
				genericFilterWriter));
		// HtmlFullFileTest.class.getResourceAsStream("/okapi_intro_test.html")
		InputResource fr = new InputResource("\r\nX\rY\n", "en");
		pipeline.process(fr);
		pipeline.destroy();
	}
	
	@Test
	public void runPipelineTwice() throws UnsupportedEncodingException {
		IPipeline pipeline = new Pipeline();

		HtmlFilter htmlFilter = new HtmlFilter();
		htmlFilter.setOptions("en", "UTF-8", true);

		GenericSkeletonWriter genericSkeletonWriter = new GenericSkeletonWriter();
		GenericFilterWriter genericFilterWriter = new GenericFilterWriter(
				genericSkeletonWriter);
		genericFilterWriter.setOptions("es", "UTF-8");
		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		genericFilterWriter.setOutput(outStream);

		pipeline.addStep(new FilterPipelineStepAdaptor(htmlFilter));
		pipeline.addStep(new FilterWriterPipelineStepAdaptor(
				genericFilterWriter));

		InputResource fr = new InputResource("<b>TEST ME</b>", "en");
		
		// first pass
		pipeline.process(fr);
		assertEquals("<b>TEST ME</b>",	new String(outStream.toByteArray(), "UTF-8"));
		
		// second pass - reset writer output for good measure
		outStream = new ByteArrayOutputStream();
		genericFilterWriter.setOutput(outStream);
		pipeline.process(fr);
		
		assertEquals("<b>TEST ME</b>",	new String(outStream.toByteArray(), "UTF-8"));
		
		pipeline.destroy();
	}

	@After
	public void cleanUp() {
	}

}

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

package net.sf.okapi.common.simplepipeline.tests;

import java.io.InputStream;

import net.sf.okapi.common.pipeline.FilterPipelineStepAdaptor;
import net.sf.okapi.common.pipeline.FilterWriterPipelineStepAdaptor;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.simplepipeline.SimplePipeline;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.writer.GenericFilterWriter;
import net.sf.okapi.filters.html.HtmlFilter;
import net.sf.okapi.filters.html.tests.HtmlParserTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HtmlFilterRoundtripTest {

	@Before
	public void setUp() {

	}
	
	@Test
	public void runPipeline() {
		IPipeline pipeline = new SimplePipeline();
		
		HtmlFilter htmlFilter = new HtmlFilter();
		InputStream htmlStream = HtmlParserTest.class.getResourceAsStream("/simpleTest.html");
		htmlFilter.setOptions("en", "UTF-8", true);
		htmlFilter.open(htmlStream);
		
		GenericSkeletonWriter genericSkeletonWriter = new GenericSkeletonWriter();
		GenericFilterWriter genericFilterWriter = new GenericFilterWriter(genericSkeletonWriter);
		genericFilterWriter.setOptions("es", "windows-1252");
		genericFilterWriter.setOutput("genericOutput.txt");

		
		pipeline.addStep(new FilterPipelineStepAdaptor(htmlFilter));
		pipeline.addStep(new FilterWriterPipelineStepAdaptor(genericFilterWriter));

		System.out.println("START PIPELINE");
		pipeline.execute();		
	}

	@After
	public void cleanUp() {
		System.out.println("CLEANUP PIPELINE");
	}

}

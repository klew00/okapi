/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.common;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.resource.RawDocument;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ExtractionVerificationStepTest {
	
	private Pipeline pipeline;
	private ExtractionVerificationStep verifier;

	@Before
	public void setUp() throws Exception {
		
		// create pipeline
		pipeline = new Pipeline();

		// add ExtractionVerificationStep
		verifier = new ExtractionVerificationStep();

		ExtractionVerificationStepParameters p = new ExtractionVerificationStepParameters();
		verifier.setParameters(p);
		FilterConfigurationMapper fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.filters.html.HtmlFilter");
		verifier.setFilterConfigurationMapper(fcMapper);
		verifier.setFilterConfigurationId("okf_html");

		pipeline.addStep(verifier);
	}

	@After
	public void tearDown() throws Exception {
		pipeline.destroy();
	}
	
	@Test
	public void testExtractionVerification () throws URISyntaxException, IOException {

		pipeline.startBatch();

		pipeline.process(new RawDocument(this.getClass().getResource("aa324.html").toURI(), "UTF-8", LocaleId.ENGLISH));
		pipeline.process(new RawDocument(this.getClass().getResource("form.html").toURI(), "UTF-8", LocaleId.ENGLISH));
		pipeline.process(new RawDocument(this.getClass().getResource("W3CHTMHLTest1.html").toURI(), "UTF-8", LocaleId.ENGLISH));
		
		/*
		FYI: getResourceAsStream does not allow reopening a filter
		pipeline.process(new RawDocument(this.getClass().getResourceAsStream("aa324.html"), "UTF-8", LocaleId.ENGLISH));
		pipeline.process(new RawDocument(this.getClass().getResourceAsStream("form.html"), "UTF-8", LocaleId.ENGLISH));
		pipeline.process(new RawDocument(this.getClass().getResourceAsStream("W3CHTMHLTest1.html"), "UTF-8", LocaleId.ENGLISH));*/
		
		pipeline.endBatch();
	}
}

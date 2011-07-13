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

		pipeline.addStep(verifier);
		
	}
	
	public void setUpFilter(boolean compareSkeleton, String configurationId){

		ExtractionVerificationStepParameters p = new ExtractionVerificationStepParameters();
		p.setCompareSkeleton(compareSkeleton);
		verifier.setParameters(p);

		FilterConfigurationMapper fcMapper = new FilterConfigurationMapper();

		fcMapper.addConfigurations("net.sf.okapi.filters.html.HtmlFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.xliff.XLIFFFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.tmx.TmxFilter");
		
		verifier.setFilterConfigurationMapper(fcMapper);
		verifier.setFilterConfigurationId(configurationId);		
	}

	@After
	public void tearDown() throws Exception {
		pipeline.destroy();
	}
	
	@Test
	public void testExtractionVerificationTmx () throws URISyntaxException, IOException {

		setUpFilter(true, "okf_tmx");
		
		pipeline.startBatch();

		pipeline.process(new RawDocument(this.getClass().getResource("html_test.tmx").toURI(), "UTF-8", LocaleId.fromBCP47("en-US"), LocaleId.fromBCP47("fr-FR")));
		pipeline.process(new RawDocument(this.getClass().getResource("ImportTest2A.tmx").toURI(), "UTF-8", LocaleId.fromBCP47("en-US"), LocaleId.fromBCP47("fr-CA")));
		pipeline.process(new RawDocument(this.getClass().getResource("ImportTest2B.tmx").toURI(), "UTF-8", LocaleId.fromBCP47("en-US"), LocaleId.fromBCP47("fr-CA")));
		pipeline.process(new RawDocument(this.getClass().getResource("ImportTest2C.tmx").toURI(), "UTF-8", LocaleId.fromBCP47("en-US"), LocaleId.fromBCP47("fr-FR")));
		
		pipeline.endBatch();
	}

	
	@Test
	public void testExtractionVerificationHtml () throws URISyntaxException, IOException {

		setUpFilter(true, "okf_html");
		
		pipeline.startBatch();

		pipeline.process(new RawDocument(this.getClass().getResource("aa324.html").toURI(), "UTF-8", LocaleId.ENGLISH));
		pipeline.process(new RawDocument(this.getClass().getResource("form.html").toURI(), "UTF-8", LocaleId.ENGLISH));
		pipeline.process(new RawDocument(this.getClass().getResource("W3CHTMHLTest1.html").toURI(), "UTF-8", LocaleId.ENGLISH));
		
		pipeline.endBatch();
	}
	
	@Test
	public void testExtractionVerificationXlf () throws URISyntaxException, IOException {

		setUpFilter(false, "okf_xliff");
		
		pipeline.startBatch();

		pipeline.process(new RawDocument(this.getClass().getResource("test1_es.xlf").toURI(), "UTF-8", LocaleId.ENGLISH, LocaleId.SPANISH));
		pipeline.process(new RawDocument(this.getClass().getResource("test2_es.xlf").toURI(), "UTF-8", LocaleId.fromBCP47("en-US"), LocaleId.fromBCP47("es-ES")));

		pipeline.process(new RawDocument(this.getClass().getResource("RB-11-Test01.xlf").toURI(), "UTF-8", LocaleId.ENGLISH, LocaleId.SPANISH));
		pipeline.process(new RawDocument(this.getClass().getResource("SF-12-Test01.xlf").toURI(), "UTF-8", LocaleId.ENGLISH, LocaleId.FRENCH));
		pipeline.process(new RawDocument(this.getClass().getResource("SF-12-Test02.xlf").toURI(), "UTF-8", LocaleId.ENGLISH, LocaleId.FRENCH));
		pipeline.process(new RawDocument(this.getClass().getResource("SF-12-Test03.xlf").toURI(), "UTF-8", LocaleId.ENGLISH, LocaleId.SPANISH));		
		pipeline.process(new RawDocument(this.getClass().getResource("BinUnitTest01.xlf").toURI(), "UTF-8", LocaleId.ENGLISH, LocaleId.SPANISH));
		pipeline.process(new RawDocument(this.getClass().getResource("JMP-11-Test01.xlf").toURI(), "UTF-8", LocaleId.ENGLISH, LocaleId.SPANISH));
		pipeline.process(new RawDocument(this.getClass().getResource("Manual-12-AltTrans.xlf").toURI(), "UTF-8", LocaleId.ENGLISH, LocaleId.SPANISH));
		
		/*
		FYI: getResourceAsStream does not allow reopening a filter
		pipeline.process(new RawDocument(this.getClass().getResourceAsStream("aa324.html"), "UTF-8", LocaleId.ENGLISH));
		pipeline.process(new RawDocument(this.getClass().getResourceAsStream("form.html"), "UTF-8", LocaleId.ENGLISH));
		pipeline.process(new RawDocument(this.getClass().getResourceAsStream("W3CHTMHLTest1.html"), "UTF-8", LocaleId.ENGLISH));*/
		
		pipeline.endBatch();
	}
}

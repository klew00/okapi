package net.sf.okapi.common.pipeline.integration;

import java.net.URL;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.connectors.google.GoogleMTConnector;
import net.sf.okapi.connectors.pensieve.PensieveTMConnector;
import net.sf.okapi.filters.tmx.TmxFilter;
import net.sf.okapi.lib.translation.QueryManager;
import net.sf.okapi.tm.pensieve.tmx.OkapiTmxImporter;
import net.sf.okapi.tm.pensieve.writer.ITmWriter;
import net.sf.okapi.tm.pensieve.writer.TmWriterFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class QueryManagerTest {
	private QueryManager qm;
	private LocaleId locENUS = LocaleId.fromString("EN-US");
	private LocaleId locFRFR = LocaleId.fromString("FR-FR");

	@Before
	public void setUp() throws Exception {
		URL url = QueryManagerTest.class.getResource("/mytm.tmx");
		String rootDir = Util.getDirectoryName(url.getPath());
		
		// create local pensieve TM		
		TmxFilter tmxFilter = new TmxFilter();
		ITmWriter tmWriter = TmWriterFactory.createFileBasedTmWriter(rootDir, true);
		OkapiTmxImporter tmxHandler = new OkapiTmxImporter(locENUS, tmxFilter);				
		tmxHandler.importTmx(url.toURI(), locFRFR, tmWriter);
		tmxFilter.close();
		tmWriter.close();
		
		// load up connectors in QueryManager
		qm = new QueryManager();
		
		qm.setRootDirectory(rootDir);
		qm.setLanguages(locENUS, locFRFR);
		
		PensieveTMConnector pensieveConnector = new PensieveTMConnector();
		net.sf.okapi.connectors.pensieve.Parameters p = new net.sf.okapi.connectors.pensieve.Parameters();
		p.setDbDirectory("${rootDir}");		
		qm.addAndInitializeResource(
				pensieveConnector, 
				"Pensieve",
				p);
		
		/*
		qm.addAndInitializeResource(
				new GoogleMTConnector(), 
				"Google MT",
				null);
		*/
	}
	
	@After
	public void tearDown () {
		if ( qm != null ) {
			qm.close();
			qm = null;
		}
	}
	
	@Test
	public void query() {
		qm.query("Elephants cannot fly.");
		Assert.assertTrue(qm.hasNext());		
		Assert.assertEquals("Elephants cannot fly.", qm.next().source.toText());
		// FIXME: Pensieve should return the match without codes first!!!
		Assert.assertEquals("Les éléphants <b>ne peuvent pas</b> voler.", qm.next().target.toText());
	}
	
	@Test
	public void leverageNoFill() {
		TextUnit tu = new TextUnit("1");
		tu.setSourceContent(new TextFragment("Elephants cannot fly."));
		qm.leverage(tu, false);
		
		Assert.assertEquals("", tu.getTarget(locFRFR).toString());
		
		AltTranslationsAnnotation a = tu.getTarget(locFRFR).getAnnotation(AltTranslationsAnnotation.class);
		Assert.assertNotNull(a);		
		Assert.assertEquals("Les éléphants ne peuvent pas voler.", a.getFirst().getTarget().toString());
	}
	
	@Test
	public void leverageFill() {
		TextUnit tu = new TextUnit("1");
		tu.setSourceContent(new TextFragment("Elephants cannot fly."));
		qm.leverage(tu, true);
		
		Assert.assertEquals("Les éléphants ne peuvent pas voler.", tu.getTarget(locFRFR).toString());
		
		AltTranslationsAnnotation a = tu.getTarget(locFRFR).getAnnotation(AltTranslationsAnnotation.class);
		Assert.assertNotNull(a);		
		Assert.assertEquals("Les éléphants ne peuvent pas voler.", a.getFirst().getTarget().toString());
	}
}

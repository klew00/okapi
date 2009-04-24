package net.sf.okapi.filters.html.tests.integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

@RunWith(Suite.class)
@SuiteClasses( { HtmlFullFileTest.class, ExtractionComparisionTest.class })
public class AllHtmlIntegrationTests {

	public static Test suite() {
		return new JUnit4TestAdapter(AllHtmlIntegrationTests.class);
	}

}

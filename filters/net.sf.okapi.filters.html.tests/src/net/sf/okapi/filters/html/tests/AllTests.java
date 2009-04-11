package net.sf.okapi.filters.html.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

@RunWith(Suite.class)
@SuiteClasses( { HtmlEventTest.class, HtmlSnippetsTest.class, HtmlFullFileTest.class, HtmlConfigurationTest.class, HtmlDetectBomTest.class })
public class AllTests {

	public static Test suite() {
		return new JUnit4TestAdapter(AllTests.class);
	}

}

package net.sf.okapi.common.pipeline.tests.integration;

import net.sf.okapi.common.pipeline.tests.integration.XsltPipelineTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

@RunWith(Suite.class)
@SuiteClasses( { XsltPipelineTest.class, FilterEventsToRawDocumentStepTest.class, FilterRoundtripTest.class })
public class AllPipelineIntegrationTests {

	public static Test suite() {
		return new JUnit4TestAdapter(AllPipelineIntegrationTests.class);
	}

}

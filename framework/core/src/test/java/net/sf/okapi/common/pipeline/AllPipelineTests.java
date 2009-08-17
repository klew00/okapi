package net.sf.okapi.common.pipeline;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

@RunWith(Suite.class)
@SuiteClasses( { FilebasedPipelineTest.class, SimplePipelineTest.class, SimplePipelineWithCancelTest.class })
public class AllPipelineTests {

	public static Test suite() {
		return new JUnit4TestAdapter(AllPipelineTests.class);
	}

}

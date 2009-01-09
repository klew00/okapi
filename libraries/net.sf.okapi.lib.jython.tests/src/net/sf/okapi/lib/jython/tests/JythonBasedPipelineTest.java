package net.sf.okapi.lib.jython.tests;

import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.python.util.PythonInterpreter;

public class JythonBasedPipelineTest {
	private PythonInterpreter interp;
	
	@Before
	public void setUp() throws Exception {
		interp = new PythonInterpreter();
	}
	
	@Test
	public void testPipeline() {
		InputStream jythonScript = JythonBasedPipelineTest.class.getResourceAsStream("pipeline_test1.py");
		interp.execfile(jythonScript);
	}
	
	@After
	public void tearDown() throws Exception {		
	}
}

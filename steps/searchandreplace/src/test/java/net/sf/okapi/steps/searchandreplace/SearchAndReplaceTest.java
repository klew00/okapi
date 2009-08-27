package net.sf.okapi.steps.searchandreplace;

import static org.junit.Assert.*;
import net.sf.okapi.common.pipelinedriver.PipelineContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SearchAndReplaceTest {
	private SearchAndReplaceStep searchAndReplace;

	@Before
	public void setUp() throws Exception {
		searchAndReplace = new SearchAndReplaceStep();
		searchAndReplace.setContext(new PipelineContext());
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void constructor() {
		assertNotNull("Step constructor creates a default Parameters", searchAndReplace.getParameters());
	}
	
	@Test
	public void getContext() {
		assertNotNull("Step context is not null", searchAndReplace.getContext());
	}
	
	@Test
	public void getDescription() {
		assertNotNull("Step message is not null", searchAndReplace.getDescription());
		assertTrue("Step message is a string", searchAndReplace.getDescription() instanceof String);
		assertTrue("Step message is not zero length", searchAndReplace.getDescription().length() >= 1);
	}
}

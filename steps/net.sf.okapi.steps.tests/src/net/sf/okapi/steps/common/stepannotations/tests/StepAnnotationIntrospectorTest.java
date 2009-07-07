package net.sf.okapi.steps.common.stepannotations.tests;

import java.util.ArrayList;
import java.util.HashMap;

import net.sf.okapi.steps.common.stepannotations.ExternalParameterType;
import net.sf.okapi.steps.common.stepannotations.StepAnnotationIntrospector;
import net.sf.okapi.steps.common.stepannotations.StepParameter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class StepAnnotationIntrospectorTest {	

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getAnnotationsFromXsltTransformStep() {
		HashMap<String, StepParameter> parameters = StepAnnotationIntrospector.getStepParameters(new XSLTransformStep());
		
		StepParameter p = parameters.get("xsltPath");
		assertFalse(p.isStepConfigurationParameter());
		assertTrue(p.getJavaType() == String.class);
		assertNotNull(p.getRequiredStep());
		assertEquals("", p.getRequiredStep());
		assertEquals("Path to the xslt file used by this step", p.getDescription());
		assertEquals(ExternalParameterType.INPUT, p.getType());
		
		p = parameters.get("errorLogPath");
		assertFalse(p.isStepConfigurationParameter());
		assertTrue(p.getJavaType() == String.class);
		assertNotNull(p.getRequiredStep());
		assertEquals("", p.getRequiredStep());
		assertEquals("Path to the xslt error log file", p.getDescription());
		assertEquals(ExternalParameterType.OUTPUT, p.getType());
	}
	
	@Test
	public void getAnnotationsFromSearchAndReplaceStep() {
		HashMap<String, StepParameter> parameters = StepAnnotationIntrospector.getStepParameters(new SearchAndReplaceStep());
		
		StepParameter p = parameters.get("plainText");
		assertTrue(p.isStepConfigurationParameter());
		assertTrue(p.getJavaType() == Boolean.class);
		assertEquals(null, p.getRequiredStep());
		assertEquals("If true the input is a plain text file, not a filtered event", p.getDescription());
		
		p = parameters.get("regEx");
		assertTrue(p.isStepConfigurationParameter());
		assertTrue(p.getJavaType() == String.class);
		assertEquals(null, p.getRequiredStep());
		assertEquals("Java Regex used to search", p.getDescription());
		
		p = parameters.get("dotAll");
		assertTrue(p.isStepConfigurationParameter());
		assertTrue(p.getJavaType() == Boolean.class);
		assertEquals(null, p.getRequiredStep());
		assertEquals("?????", p.getDescription());
		
		p = parameters.get("ignoreCase");
		assertTrue(p.isStepConfigurationParameter());
		assertTrue(p.getJavaType() == Boolean.class);
		assertEquals(null, p.getRequiredStep());
		assertEquals("If true the search is case insensitive", p.getDescription());
		
		p = parameters.get("multiLine");
		assertTrue(p.isStepConfigurationParameter());
		assertTrue(p.getJavaType() == Boolean.class);
		assertEquals(null, p.getRequiredStep());
		assertEquals("Search across line boundries", p.getDescription());	
		
		p = parameters.get("rules");
		assertTrue(p.isStepConfigurationParameter());
		// why can't I test this statically as below??
		ArrayList<String[]> al = new ArrayList<String[]>();
		assertTrue(p.getJavaType() == al.getClass());
		assertTrue(p.getJavaType() == ArrayList.class);
		assertNull(p.getRequiredStep());
		assertEquals("List of rules", p.getDescription());						
	}
}

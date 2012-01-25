package net.sf.okapi.filters.tmx;

import static org.junit.Assert.*;

import net.sf.okapi.filters.tmx.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ParametersTest {

	Parameters p;
	
	@Before
	public void setUp() throws Exception {
		p = new Parameters();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParameters() {
		assertFalse("escapeGT should default to false", p.escapeGT);
		assertTrue("processAllTargets should default to true", p.processAllTargets);
		assertTrue("consolidateDpSkeleton should default to true", p.consolidateDpSkeleton);
		assertFalse("exitOnInvalid should default to false", p.exitOnInvalid);
	}

	@Test
	public void testReset() {
		p.fromString("#v1\nescapeGT.b=true\nprocessAllTargets.b=false\nconsolidateDpSkeleton.b=false\nexitOnInvalid.b=true");
		p.reset();
		assertFalse("escapeGT should be false", p.escapeGT);
		assertTrue("processAllTargets should be true", p.processAllTargets);
		assertTrue("consolidateDpSkeleton should be true", p.consolidateDpSkeleton);
		assertFalse("exitOnInvalid should default to false", p.exitOnInvalid);
	}

	@Test
	public void testFromString() {
		p.fromString("#v1\nescapeGT.b=true\nprocessAllTargets.b=false\nconsolidateDpSkeleton.b=false\nexitOnInvalid.b=true");
		assertTrue("escapeGT should be true", p.escapeGT);
		assertFalse("processAllTargets should be false", p.processAllTargets);
		assertFalse("consolidateDpSkeleton should be false", p.consolidateDpSkeleton);
		assertTrue("exitOnInvalid should to true", p.exitOnInvalid);
	}

	@Test
	public void testToString() {
		assertEquals("Incorrect format","#v1\nescapeGT.b=false\nprocessAllTargets.b=true\nconsolidateDpSkeleton.b=true\nexitOnInvalid.b=false",p.toString());
	}
}

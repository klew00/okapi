package net.sf.okapi.common;

import static org.junit.Assert.*;

import java.security.InvalidParameterException;

import org.junit.Test;

public class IdGeneratorTest {
	
	private IdGenerator idGen;
	
	@Test
	public void testresultNotNull () {
		idGen = new IdGenerator("test");
		assertNotNull(idGen.createNextId());
		assertNotNull(idGen.createNextId());
	}

	@Test
	public void testresultNotEmpty () {
		idGen = new IdGenerator("test");
		assertTrue(idGen.createNextId().length()>0);
		assertTrue(idGen.createNextId().length()>0);
	}

	@Test (expected=InvalidParameterException.class)
	public void testCreationWithNullRoot () {
		idGen = new IdGenerator(null);
	}
	
	@Test (expected=InvalidParameterException.class)
	public void testCreationWithEmptyRoot () {
		idGen = new IdGenerator("");
	}
	
	@Test
	public void testWithNullPrefix () {
   	idGen = new IdGenerator("test");
   	String id1 = idGen.createNextId();
   	idGen = new IdGenerator("test", null);
   	assertEquals(id1, idGen.createNextId());
	}

	@Test
	public void testWithEmptyPrefix () {
   	idGen = new IdGenerator("test");
   	String id1 = idGen.createNextId();
   	idGen = new IdGenerator("test", "");
   	assertEquals(id1, idGen.createNextId());
	}

	@Test
	public void testSameIdForSameRoot () {
   	idGen = new IdGenerator("test");
   	String id1 = idGen.createNextId();
   	String id2 = idGen.createNextId();
   	idGen = new IdGenerator("test");
   	assertTrue(id1.equals(idGen.createNextId()));
   	assertTrue(id2.equals(idGen.createNextId()));
   }

	@Test
	public void testDifferentIdForDifferentRoot () {
   	idGen = new IdGenerator("test");
   	String id1 = idGen.createNextId();
   	String id2 = idGen.createNextId();
   	idGen = new IdGenerator("Test"); // Case difference
   	// Should get different IDs
   	assertFalse(id1.equals(idGen.createNextId()));
   	assertFalse(id2.equals(idGen.createNextId()));
   }

	@Test
   public void testresultNotNullWithPrefix () {
   	idGen = new IdGenerator("test", "p");
   	assertNotNull(idGen.createNextId());
   	assertNotNull(idGen.createNextId());
   }

   @Test
   public void testresultNotEmptyWithPrefix () {
   	idGen = new IdGenerator("test", "p");
   	assertTrue(idGen.createNextId().length()>0);
   	assertTrue(idGen.createNextId().length()>0);
   }

   @Test
   public void testSameIdForSameRootAndPrefix () {
   	idGen = new IdGenerator("test", "p");
   	String id1 = idGen.createNextId();
   	String id2 = idGen.createNextId();
   	idGen = new IdGenerator("test", "p");
   	assertTrue(id1.equals(idGen.createNextId()));
   	assertTrue(id2.equals(idGen.createNextId()));
   }

	@Test
	public void testDifferentIdForDifferentRootSamePrefix () {
   	idGen = new IdGenerator("test", "p");
   	String id1 = idGen.createNextId();
   	String id2 = idGen.createNextId();
   	idGen = new IdGenerator("Test", "p"); // Case difference
   	// Should get different IDs
   	assertFalse(id1.equals(idGen.createNextId()));
   	assertFalse(id2.equals(idGen.createNextId()));
   }

	@Test
	public void testDifferentIdForSameRootDifferentPrefix () {
   	idGen = new IdGenerator("test", "p");
   	String id1 = idGen.createNextId();
   	String id2 = idGen.createNextId();
   	idGen = new IdGenerator("test", "P"); // Case difference
   	// Should get different IDs
   	assertFalse(id1.equals(idGen.createNextId()));
   	assertFalse(id2.equals(idGen.createNextId()));
   }

	@Test
	public void testDifferentIdForDifferentRootAndPrefix () {
   	idGen = new IdGenerator("test", "p");
   	String id1 = idGen.createNextId();
   	String id2 = idGen.createNextId();
   	idGen = new IdGenerator("Test", "P"); // Case difference
   	// Should get different IDs
   	assertFalse(id1.equals(idGen.createNextId()));
   	assertFalse(id2.equals(idGen.createNextId()));
   }

	@Test
	public void testCanReproduceValue () {
		// The String.hashCode() is the same across platform normally
   	idGen = new IdGenerator("test", "p");
   	String id1 = idGen.createNextId();
   	String id2 = idGen.createNextId();
   	assertEquals("P364492-p1", id1);
   	assertEquals("P364492-p2", id2);
   	idGen = new IdGenerator("test/A/b/C");
   	id1 = idGen.createNextId();
   	id2 = idGen.createNextId();
   	assertEquals("P269F9F4B-1", id1);
   	assertEquals("P269F9F4B-2", id2);
   }

}

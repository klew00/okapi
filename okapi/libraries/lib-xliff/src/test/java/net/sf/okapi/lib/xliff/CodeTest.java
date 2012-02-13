package net.sf.okapi.lib.xliff;

import static org.junit.Assert.*;

import org.junit.Test;
import org.oasisopen.xliff.v2.ICode;
import org.oasisopen.xliff.v2.InlineType;

public class CodeTest {

	@Test
	public void testSimple () {
		ICode code = new Code(InlineType.OPENING, "1", null);
		assertEquals(InlineType.OPENING, code.getInlineType());
		assertEquals("1", code.getId());
		assertNull(code.getOriginalData());
	}
	
	@Test
	public void testAllInlineTypes () {
		ICode code = new Code(InlineType.OPENING, "1", null);
		assertEquals(InlineType.OPENING, code.getInlineType());
		code.setInlineType(InlineType.CLOSING);
		assertEquals(InlineType.CLOSING, code.getInlineType());
		code.setInlineType(InlineType.PLACEHOLDER);
		assertEquals(InlineType.PLACEHOLDER, code.getInlineType());
	}
	
	@Test
	public void testOriginalData () {
		ICode code = new Code(InlineType.PLACEHOLDER, "1", null);
		assertEquals(null, code.getOriginalData());
		assertFalse(code.hasOriginalData());
		code = new Code(InlineType.PLACEHOLDER, "1", "");
		assertEquals("", code.getOriginalData());
		assertFalse(code.hasOriginalData());
		code = new Code(InlineType.PLACEHOLDER, "1", "z");
		assertEquals("z", code.getOriginalData());
		assertTrue(code.hasOriginalData());
	}
	
	@Test
	public void testHintsDefaults () {
		ICode code = new Code(InlineType.PLACEHOLDER, "1", null);
		assertTrue(code.canDelete());
		assertTrue(code.canCopy());
		assertTrue(code.canReorder());
	}
	
	@Test
	public void testHintsCanDelete () {
		ICode code = new Code(InlineType.PLACEHOLDER, "1", null);
		code.setCanDelete(false);
		assertFalse(code.canDelete());
		assertTrue(code.canCopy());
		assertTrue(code.canReorder());
		code.setCanDelete(true);
		assertTrue(code.canDelete());
		assertTrue(code.canCopy());
		assertTrue(code.canReorder());
	}
	
	@Test
	public void testHintsCanReplicate () {
		ICode code = new Code(InlineType.PLACEHOLDER, "1", null);
		code.setCanCopy(false);
		assertTrue(code.canDelete());
		assertFalse(code.canCopy());
		assertTrue(code.canReorder());
		code.setCanCopy(true);
		assertTrue(code.canDelete());
		assertTrue(code.canCopy());
		assertTrue(code.canReorder());
	}
	
	@Test
	public void testHintsCanReorder () {
		ICode code = new Code(InlineType.PLACEHOLDER, "1", null);
		code.setCanReorder(false);
		assertTrue(code.canDelete());
		assertTrue(code.canCopy());
		assertFalse(code.canReorder());
		code.setCanReorder(true);
		assertTrue(code.canDelete());
		assertTrue(code.canCopy());
		assertTrue(code.canReorder());
	}

	@Test
	public void testEquals () {
		assertTrue(new Code(InlineType.PLACEHOLDER, "1", null).equals(
			new Code(InlineType.PLACEHOLDER, "1", null)));
		assertFalse(new Code(InlineType.PLACEHOLDER, "1", null).equals(
			new Code(InlineType.PLACEHOLDER, "2", null)));
		assertFalse(new Code(InlineType.PLACEHOLDER, "1", null).equals(
			new Code(InlineType.OPENING, "1", null)));
		assertFalse(new Code(InlineType.PLACEHOLDER, "1", null).equals(
			new Code(InlineType.PLACEHOLDER, "1", "data")));
		
		ICode code1 = new Code(InlineType.PLACEHOLDER, "1", "d1");
		code1.setDisp("di1");
		code1.setEquiv("eq1");
		code1.setSubFlows("sf1");
		code1.setCanDelete(false);
		code1.setType("ty1");

		ICode code2 = new Code(InlineType.PLACEHOLDER, "1", "d1");
		code2.setDisp("di1");
		code2.setEquiv("eq1");
		code2.setSubFlows("sf1");
		code2.setCanDelete(false);
		code2.setType("ty1");
		
		assertTrue(code1.equals(code2));
		
		code2.setType("ty2");
		assertFalse(code1.equals(code2));
		code2.setType("ty1");
		assertTrue(code1.equals(code2));
		
		code2.setEquiv("eq2");
		assertFalse(code1.equals(code2));
		code2.setEquiv("eq1");
		assertTrue(code1.equals(code2));
		
		code2.setDisp("di2");
		assertFalse(code1.equals(code2));
		code2.setDisp("di1");
		assertTrue(code1.equals(code2));
		
		code2.setSubFlows("sf2");
		assertFalse(code1.equals(code2));
		code2.setSubFlows("sf1");
		assertTrue(code1.equals(code2));
		
		code2.setCanCopy(false);
		assertFalse(code1.equals(code2));
		code2.setCanCopy(true);
		assertTrue(code1.equals(code2));
		
		
	}
}

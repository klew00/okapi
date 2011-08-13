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
		assertTrue(code.canReplicate());
		assertTrue(code.canReorder());
		assertTrue(code.canChangeParent());
	}
	
	@Test
	public void testHintsCanDelete () {
		ICode code = new Code(InlineType.PLACEHOLDER, "1", null);
		code.setCanDelete(false);
		assertFalse(code.canDelete());
		assertTrue(code.canReplicate());
		assertTrue(code.canReorder());
		assertTrue(code.canChangeParent());
		code.setCanDelete(true);
		assertTrue(code.canDelete());
		assertTrue(code.canReplicate());
		assertTrue(code.canReorder());
		assertTrue(code.canChangeParent());
	}
	
	@Test
	public void testHintsCanReplicate () {
		ICode code = new Code(InlineType.PLACEHOLDER, "1", null);
		code.setCanReplicate(false);
		assertTrue(code.canDelete());
		assertFalse(code.canReplicate());
		assertTrue(code.canReorder());
		assertTrue(code.canChangeParent());
		code.setCanReplicate(true);
		assertTrue(code.canDelete());
		assertTrue(code.canReplicate());
		assertTrue(code.canReorder());
		assertTrue(code.canChangeParent());
	}
	
	@Test
	public void testHintsCanReorder () {
		ICode code = new Code(InlineType.PLACEHOLDER, "1", null);
		code.setCanReorder(false);
		assertTrue(code.canDelete());
		assertTrue(code.canReplicate());
		assertFalse(code.canReorder());
		assertTrue(code.canChangeParent());
		code.setCanReorder(true);
		assertTrue(code.canDelete());
		assertTrue(code.canReplicate());
		assertTrue(code.canReorder());
		assertTrue(code.canChangeParent());
	}

	@Test
	public void testHintsCanChangeParent () {
		ICode code = new Code(InlineType.PLACEHOLDER, "1", null);
		code.setCanChangeParent(false);
		assertTrue(code.canDelete());
		assertTrue(code.canReplicate());
		assertTrue(code.canReorder());
		assertFalse(code.canChangeParent());
		code.setCanChangeParent(true);
		assertTrue(code.canDelete());
		assertTrue(code.canReplicate());
		assertTrue(code.canReorder());
		assertTrue(code.canChangeParent());
	}

}

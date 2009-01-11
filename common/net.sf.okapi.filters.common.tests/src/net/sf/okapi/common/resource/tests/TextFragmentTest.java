/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.common.resource.tests;

import java.util.List;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.InlineAnnotation;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.writer.GenericInlines;
import junit.framework.*;

public class TextFragmentTest extends TestCase {

	private GenericInlines fmt;
	
	@Override
	public void setUp () throws Exception {
		super.setUp();
		fmt = new GenericInlines();
	}
	
	public void testConstructors () {
		TextFragment tf1 = new TextFragment();
		assertTrue(tf1.isEmpty());
		assertNotNull(tf1.toString());
		assertNotNull(tf1.getCodedText());
		tf1 = new TextFragment("text");
		assertFalse(tf1.isEmpty());
		TextFragment tf2 = new TextFragment(tf1);
		assertEquals(tf1.toString(), tf2.toString());
		assertNotSame(tf1, tf2);
	}
	
	public void testAppend () {
		TextFragment tf1 = new TextFragment();
		tf1.append('c');
		assertEquals(tf1.toString(), "c");
		tf1 = new TextFragment();
		tf1.append("string");
		assertEquals(tf1.toString(), "string");
		tf1.append('c');
		assertEquals(tf1.toString(), "stringc");
		TextFragment tf2 = new TextFragment();
		tf2.append(tf1);
		assertEquals(tf2.toString(), "stringc");
		assertNotSame(tf1, tf2);
		assertFalse(tf1.hasCode());
		
		tf1 = new TextFragment("string");
		tf1.append(TagType.PLACEHOLDER, "br", "<br/>");
		String s1 = tf1.getCodedText();
		s1 = s1.toUpperCase();
		assertEquals(tf1.toString(), "string<br/>");
		tf1.setCodedText(s1);
		assertEquals(tf1.toString(), "STRING<br/>");
		
		// Test with in-line codes
		tf1 = new TextFragment();
		tf1.append(TagType.PLACEHOLDER, "br", "<br/>");
		assertTrue(tf1.hasCode());
		Code code = tf1.getCode(0);
		assertEquals(code.getData(), "<br/>");
		assertEquals(tf1.toString(), "<br/>"); 
	}
	
	public void testInsert () {
		TextFragment tf1 = new TextFragment();
		tf1.insert(0, new TextFragment("[ins1]"));
		assertEquals(tf1.toString(), "[ins1]");
		tf1.insert(4, new TextFragment("ertion"));
		assertEquals(tf1.toString(), "[insertion1]");
		tf1.insert(0, new TextFragment("<"));
		assertEquals(tf1.toString(), "<[insertion1]");
		tf1.insert(13, new TextFragment(">"));
		assertEquals(tf1.toString(), "<[insertion1]>");
		tf1.insert(-1, new TextFragment("$"));
		assertEquals(tf1.toString(), "<[insertion1]>$");
		// Test with in-line codes
		tf1 = new TextFragment();
		tf1.insert(0, new TextFragment("abc"));
		TextFragment tf2 = new TextFragment();
		tf2.append(TagType.PLACEHOLDER, "br", "<br/>");
		tf1.insert(1, tf2);
		Code code = tf1.getCode(0);
		assertEquals(code.getData(), "<br/>");
		assertEquals(fmt.setContent(tf1).toString(true), "a<br/>bc");
		tf2 = new TextFragment();
		tf2.append(TagType.OPENING, "b", "<b>");
		tf1.insert(4, tf2);
		tf2 = new TextFragment();
		tf2.append(TagType.CLOSING, "b", "</b>");
		tf1.insert(7, tf2);
		tf2 = new TextFragment();
		tf2.append(TagType.PLACEHOLDER, "x", "<x/>");
		tf1.insert(-1, tf2);
		assertEquals(tf1.toString(), "a<br/>b<b>c</b><x/>");
	}

	public void testRemove () {
		TextFragment tf1 = makeFragment();
		assertEquals(fmt.setContent(tf1).toString(true), "<b>A<br/>B</b>C");
		tf1.remove(2, 3); // xxAxxBxxC -> xxxxBxxC
		tf1.remove(4, 5); // xxxxBxxC -> xxxxxxC
		tf1.remove(6, 7); // xxxxxxC -> xxxxxx
		assertFalse(tf1.hasText(true));
		assertEquals(tf1.getCodedText().length(), 3*2);
		assertEquals(tf1.toString(), "<b><br/></b>");

		tf1 = makeFragment();
		tf1.remove(0, 2); // xxAxxBxxC -> AxxBxxC
		tf1.remove(1, 3); // AxxBxxC -> ABxxC
		tf1.remove(2, 4); // ABxxC -> ABC
		assertFalse(tf1.hasCode());
		assertEquals(tf1.getCodedText().length(), 3);
		assertEquals(tf1.toString(), "ABC");
	}
	
	public void testInlines () {
		TextFragment tf1 = makeFragment();
		assertTrue(tf1.hasCode());
		assertEquals(tf1.toString(), "<b>A<br/>B</b>C");
		assertEquals(tf1.getCode(0).getData(), "<b>");
		assertEquals(tf1.getCode(1).getData(), "<br/>");
		assertEquals(tf1.getCode(2).getData(), "</b>");
		assertEquals(fmt.setContent(tf1).toString(false), "<1>A<2/>B</1>C");
		tf1.remove(0, 2);
		//TODO: assertEquals(display.setContent(tf1).toString(false), "A<2/>B<1/>C");
		assertEquals(tf1.toString(), "A<br/>B</b>C");
		TextFragment tf2 = new TextFragment();
		tf2.append(TagType.OPENING, "b", "<b>");
		tf1.insert(0, tf2);
		//TODO: assertEquals(display.setContent(tf1).toString(false), "<1/>A<2/>B</1>C");
		
		Code code1 = new Code(TagType.PLACEHOLDER, "type", "data");
		code1.setHasReference(true);
		code1.setId(100);
		code1.setOuterData("outer");
		assertEquals(code1.getType(), "type");
		assertEquals(code1.getData(), "data");
		assertEquals(code1.getOuterData(), "outer");
		assertEquals(code1.getId(), 100);
		assertEquals(code1.getTagType(), TagType.PLACEHOLDER);

		tf1 = new TextFragment();
		Code code2 = tf1.append(code1);
		Code code3 = tf1.getCode(0);
		assertSame(code1, code2);
		assertSame(code2, code3);
		code1 = null;
		assertEquals(code2.getType(), "type");
		assertEquals(code2.getData(), "data");
		assertEquals(code2.getOuterData(), "outer");
		assertEquals(code2.getId(), 1); // ID automatically adjusted to 1
		assertEquals(code2.getTagType(), TagType.PLACEHOLDER);
		
		Code code4 = code2.clone();
		assertNotSame(code4, code2);
		assertEquals(code4.getType(), "type");
		assertEquals(code4.getData(), "data");
		assertEquals(code4.getOuterData(), "outer");
		assertEquals(code4.getId(), 1); // ID automatically adjusted to 1
		assertEquals(code4.getTagType(), TagType.PLACEHOLDER);
		
		code1 = new Code(TagType.PLACEHOLDER, "t", "d");
		assertFalse(code1.hasReference());
		assertTrue(code1.isCloneable());
		assertTrue(code1.isDeleteable());
		code2 = code1.clone();
		assertFalse(code2.hasReference());
		assertTrue(code2.isCloneable());
		assertTrue(code2.isDeleteable());
		
		code1.setHasReference(true);
		code1.setIsCloneable(true);
		code1.setIsDeleteable(true);
		assertTrue(code1.hasReference());
		assertTrue(code1.isCloneable());
		assertTrue(code1.isDeleteable());
		
		code1.setHasReference(false);
		assertFalse(code1.hasReference());
		assertTrue(code1.isCloneable());
		assertTrue(code1.isDeleteable());
		
		code1.setIsCloneable(false);
		assertFalse(code1.hasReference());
		assertFalse(code1.isCloneable());
		assertTrue(code1.isDeleteable());

		code1.setIsDeleteable(false);
		assertFalse(code1.hasReference());
		assertFalse(code1.isCloneable());
		assertFalse(code1.isDeleteable());

		code1.setHasReference(true);
		code1.setIsDeleteable(true);
		assertTrue(code1.hasReference());
		assertFalse(code1.isCloneable());
		assertTrue(code1.isDeleteable());
		
		tf1 = new TextFragment();
		tf1.append(code1);
		String codesStorage1 = Code.codesToString(tf1.getCodes());
		String textStorage1 = tf1.getCodedText();
		assertNotNull(codesStorage1);
		assertNotNull(textStorage1);
		tf2 = new TextFragment();
		tf2.setCodedText(textStorage1, Code.stringToCodes(codesStorage1));
		assertEquals(tf1.toString(), tf2.toString());
		String codesStorage2 = Code.codesToString(tf2.getCodes());
		String textStorage2 = tf2.getCodedText();
		assertEquals(codesStorage1, codesStorage2);
		assertEquals(textStorage1, textStorage2);
	}
	
	public void testCodedText () {
		TextFragment tf1 = makeFragment();
		assertEquals(tf1.getCodedText().length(), (2*3)+3); // 2 per code + 3 chars
		assertEquals(tf1.getCodedText(3, 5).length(), 2); // code length for <br/>
		
		String codedText = tf1.getCodedText();
		List<Code> codes = tf1.getCodes();
		TextFragment tf2 = new TextFragment();
		tf2.setCodedText(codedText, codes);
		assertEquals(tf1.toString(), tf2.toString());
		assertEquals(fmt.setContent(tf1).toString(false), fmt.setContent(tf2).toString(false));
		assertNotSame(tf1, tf2);

		codes = null;
		codes = tf1.getCodes(0, 5); // xxAxxBxxC
		assertNotNull(codes);
		assertEquals(codes.size(), 2);
		assertEquals(codes.get(0).getData(), "<b>");
		assertEquals(codes.get(1).getData(), "<br/>");
	}

	public void testHasText () {
		TextFragment tf1 = new TextFragment();
		assertFalse(tf1.hasText(true));
		assertFalse(tf1.hasText(false));
		tf1.append(TagType.PLACEHOLDER, "br", "<br/>");
		assertFalse(tf1.hasText(true));
		assertFalse(tf1.hasText(false));
		tf1.append('\t');
		assertTrue(tf1.hasText(true));
		assertFalse(tf1.hasText(false));
		tf1 = new TextFragment();
		tf1.append(TagType.PLACEHOLDER, "br", "<br/>");
		tf1.append('c');
		assertTrue(tf1.hasText(true));
		assertTrue(tf1.hasText(false));
	}
	
	public void testHasCode () {
		TextFragment tf1 = new TextFragment();
		assertFalse(tf1.hasCode());
		tf1.append('\t');
		assertFalse(tf1.hasCode());
		tf1.append('c');
		assertFalse(tf1.hasCode());
		tf1.append(TagType.PLACEHOLDER, "br", "<br/>");
		assertTrue(tf1.hasCode());
	}

	public void testTextCodesChanges () {
		TextFragment tf1 = new TextFragment("<b>New file:</b> %s");

		// Change the codes
		int diff = tf1.changeToCode(0, 3, TagType.OPENING, "b");
		diff += tf1.changeToCode(12+diff, 16+diff, TagType.CLOSING, "b");
		List<Code> list1 = tf1.getCodes();
		assertEquals(list1.get(0).getData(), "<b>");
		assertEquals(list1.get(1).getData(), "</b>");
		assertEquals(tf1.toString(), "<b>New file:</b> %s");
		assertEquals(fmt.setContent(tf1).toString(false), "<1>New file:</1> %s");

		// Add an annotation: "%s" (use diff because %s is after both added codes) 
		tf1.annotate(17+diff, 19+diff, "protected", null);
		assertEquals(tf1.toString(), "<b>New file:</b> %s");
		list1 = tf1.getCodes();
		assertTrue(list1.get(2).hasAnnotation());
		assertTrue(list1.get(2).hasAnnotation("protected"));
		assertEquals(fmt.setContent(tf1).toString(true), "<b>New file:</b> %s");
		assertEquals(fmt.setContent(tf1).toString(false), "<1>New file:</1> <2>%s</2>");
		
		// Test if we can rebuild the annotation from the storage string
		String codesStorage1 = Code.codesToString(tf1.getCodes());
		String textStorage1 = tf1.getCodedText();
		assertNotNull(codesStorage1);
		assertNotNull(textStorage1);
		TextFragment tf2 = new TextFragment();
		tf2.setCodedText(textStorage1, Code.stringToCodes(codesStorage1));
		assertEquals(tf1.toString(), tf2.toString());
		List<Code> list2 = tf2.getCodes();
		assertTrue(list1.get(2).hasAnnotation());
		assertTrue(list1.get(2).hasAnnotation("protected"));
		assertEquals(fmt.setContent(tf1).toString(true), "<b>New file:</b> %s");
		assertEquals(fmt.setContent(tf1).toString(false), "<1>New file:</1> <2>%s</2>");

		// Add an annotation for "New" (don't use diff, correct manually: xxNew file:</b>
		tf1.annotate(2, 5, "term", new InlineAnnotation("Nouveau"));
		assertEquals(fmt.setContent(tf1).toString(true), "<b>New file:</b> %s");
		assertEquals(fmt.setContent(tf1).toString(false), "<1><3>New</3> file:</1> <2>%s</2>");
		// Test if we can rebuild the annotation from the storage string
		tf2 = new TextFragment();
		tf2.setCodedText(tf1.getCodedText(),
			Code.stringToCodes(Code.codesToString(tf1.getCodes())));
		assertEquals(tf1.toString(), tf2.toString());
		list2 = tf2.getCodes();
		assertTrue(list2.get(2).hasAnnotation());
		assertTrue(list2.get(2).hasAnnotation("protected"));
		assertTrue(list2.get(4).hasAnnotation("term"));
		InlineAnnotation annotation = list2.get(4).getAnnotation("term");
		assertEquals(annotation.getData(), "Nouveau");
		
		// Test annotation change
		annotation.setData("Neue");
		// Get the codes of tf1
		list1 = tf1.getCodes();
		// Check if the same annotation is now change like in tf2:
		// It should not as tf2 is a clone.
		assertEquals(list1.get(4).getAnnotation("term").getData(), "Nouveau");
		assertEquals(list2.get(4).getAnnotation("term").getData(), "Neue");
	}
	
	/**
	 * Makes a fragment <code>[b]A[br/]B[/b]C<code>
	 * @return the new fragment.
	 */
	private TextFragment makeFragment () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("A");
		tf.append(TagType.PLACEHOLDER, "br", "<br/>");
		tf.append("B");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append("C");
		return tf;
	}

}

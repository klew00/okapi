/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.common.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonPart;

import org.junit.Test;

public class TextUnitUtilTest {

	private GenericContent fmt = new GenericContent();
	private LocaleId locTrg = LocaleId.fromString("trg");

	@Test
	public void testAdjustTargetFragment () {
		TextFragment toTransSrc = makeFragment1();
		TextFragment proposalTrg = makeFragment1Bis("trg");
		assertEquals("{B}A{/B}B{BR/}C trg", proposalTrg.toText());
		TextUnitUtil.adjustTargetCodes(toTransSrc, proposalTrg, true, true, null, null);
		assertEquals("[b]A[/b]B[br/]C trg", proposalTrg.toText());
	}
	
	@Test
	public void testAdjustIncompleteTargetFragmentAutoAdded () {
		TextFragment toTransSrc = makeFragment1();
		TextFragment proposalTrg = makeFragment1Bis("trg");
		proposalTrg.remove(6, 8); // "xxAxxBxxC trg"
		assertEquals("{B}A{/B}BC trg", proposalTrg.toText());
		TextUnitUtil.adjustTargetCodes(toTransSrc, proposalTrg, true, true, null, null);
		assertEquals("[b]A[/b]BC trg[br/]", proposalTrg.toText());
	}
	
	@Test
	public void testAdjustIncompleteTargetFragmentNoAddition () {
		TextFragment toTransSrc = makeFragment1();
		TextFragment proposalTrg = makeFragment1Bis("with warning");
		proposalTrg.remove(6, 8); // "xxAxxBxxC with warning"
		assertEquals("{B}A{/B}BC with warning", proposalTrg.toText());
		TextUnitUtil.adjustTargetCodes(toTransSrc, proposalTrg, true, false, null, null);
		assertEquals("[b]A[/b]BC with warning", proposalTrg.toText());
	}
	
	@Test
	public void testAdjustNoCodes () {
		ITextUnit tu = new TextUnit("1", "src");
		TextFragment newSrc = new TextFragment("src");
		TextFragment newTrg = new TextFragment("trg");
		TextUnitUtil.adjustTargetCodes(tu.getSource().getSegments().getFirstContent(), newTrg, true, false, newSrc, tu);
		assertEquals(locTrg, newTrg.toText());
	}
	
	@Test
	public void testAdjustSameMarkers () {
		ITextUnit tu = createTextUnit1();
		TextFragment tf = new TextFragment("T ");
		tf.append(TagType.OPENING, "b", "<T>");
		tf.append("BOLD");
		tf.append(TagType.CLOSING, "b", "</T>");
		tf.append(" T ");
		tf.append(TagType.PLACEHOLDER, "br", "<PH/>");
		TextUnitUtil.adjustTargetCodes(tu.getSource().getSegments().getFirstContent(), tf, true, false, null, tu);
		assertEquals("T <b>BOLD</b> T <br/>", tf.toText());
		fmt.setContent(tf);
		assertEquals("T <1>BOLD</1> T <2/>", fmt.toString());
	}

	@Test
	public void testAdjustExtraMarkers () {
		ITextUnit tu = createTextUnit1();
		TextFragment tf = new TextFragment("T ");
		tf.append(TagType.OPENING, "b", "<T>");
		tf.append("BOLD");
		tf.append(TagType.CLOSING, "b", "</T>");
		tf.append(" T ");
		tf.append(TagType.PLACEHOLDER, "br", "<PH/>");
		tf.append(TagType.PLACEHOLDER, "extra", "<EXTRA/>");
		TextUnitUtil.adjustTargetCodes(tu.getSource().getSegments().getFirstContent(), tf, true, false, null, tu);
		assertEquals("T <b>BOLD</b> T <br/><EXTRA/>", tf.toText());
		fmt.setContent(tf);
		assertEquals("T <1>BOLD</1> T <2/><3/>", fmt.toString());
	}
	
	@Test
	public void testAdjustMissingMarker () {
		ITextUnit tu = createTextUnit1();
		TextFragment tf = new TextFragment("T ");
		tf.append(TagType.OPENING, "b", "<T>");
		tf.append("BOLD");
		tf.append(" T ");
		tf.append(TagType.PLACEHOLDER, "br", "<PH/>");
		tf.append(TagType.PLACEHOLDER, "extra", "<EXTRA/>");
		TextUnitUtil.adjustTargetCodes(tu.getSource().getSegments().getFirstContent(), tf, true, false, null, tu);
		assertEquals("T <b>BOLD T <br/><EXTRA/>", tf.toText());
		fmt.setContent(tf);
		assertEquals("T <b1/>BOLD T <2/><3/>", fmt.toString());
	}
	
	@Test
	public void testAdjustDifferentTextSameMarkers () {
		ITextUnit tu = createTextUnit1();
		TextFragment tf = new TextFragment("U ");
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("BOLD");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(" U ");
		tf.append(TagType.PLACEHOLDER, "br", "<br/>");
		// Fuzzy match but codes are the same
		TextUnitUtil.adjustTargetCodes(tu.getSource().getFirstContent(), tf, true, false, null, tu);
		assertEquals("U <b>BOLD</b> U <br/>", tf.toText());
		assertEquals("U <1>BOLD</1> U <2/>", fmt.setContent(tf).toString());
	}

	@Test
	public void testMovedCodes () {
		TextFragment oriFrag = new TextFragment("s1 ");
		oriFrag.append(TagType.PLACEHOLDER, "c1", "[c1]");
		oriFrag.append(" s2 ");
		oriFrag.append(TagType.OPENING, "c2", "[c2>]");
		oriFrag.append(" s3 ");
		oriFrag.append(TagType.CLOSING, "c2", "[<c2]");
		TextFragment trgFrag = fmt.fromLetterCodedToFragment("<g2>t3</g2> t1 <x1/> t2", null);
		TextUnitUtil.adjustTargetCodes(oriFrag, trgFrag, true, false, null, null);
		assertEquals("[c2>]t3[<c2] t1 [c1] t2", fmt.setContent(trgFrag).toString(true));
	}
	
	@Test
	public void testUtils() {
		String st = "12345678";
		assertEquals("45678", Util.trimStart(st, "123"));
		assertEquals("12345", Util.trimEnd(st, "678"));
		assertEquals("12345678", Util.trimEnd(st, "9"));

		st = "     ";
		assertEquals("", Util.trimStart(st, " "));
		assertEquals("", Util.trimEnd(st, " "));

		st = "  1234   ";
		TextFragment tf = new TextFragment(st);
		TextUnitUtil.trimLeading(tf, null);
		assertEquals("1234   ", tf.toText());
		TextUnitUtil.trimTrailing(tf, null);
		assertEquals("1234", tf.toText());

		st = "     ";
		tf = new TextFragment(st);
		TextUnitUtil.trimLeading(tf, null);
		assertEquals("", tf.toText());
		TextUnitUtil.trimTrailing(tf, null);
		assertEquals("", tf.toText());

		st = "     ";
		tf = new TextFragment(st);
		TextUnitUtil.trimTrailing(tf, null);
		assertEquals("", tf.toText());

		TextFragment tc = new TextFragment("test");

		Code c = new Code(TagType.PLACEHOLDER, "code");
		tc.append(c);

		tc.append(" string");
		// debug System.out.println(tc.toString());
		// debug System.out.println(tc.getCodedText());

		// --------------------
		// TextContainer tcc = new TextContainer("    123456  ");
		TextFragment tcc = new TextFragment();
		Code c2 = new Code(TagType.PLACEHOLDER, "code");
		tcc.append("   ");
		tcc.append(c2);
		tcc.append("    123456  ");

		GenericSkeleton skel = new GenericSkeleton();
		TextUnitUtil.trimLeading(tcc, skel);

		ITextUnit tu1 = new TextUnit("tu1");
		tu1.setSourceContent(tcc);
		assertEquals("    123456  ", tu1.toString());
		assertEquals("   ", skel.toString());

		// --------------------
		TextFragment tcc2 = new TextFragment("    123456  ");
		Code c3 = new Code(TagType.PLACEHOLDER, "code");
		tcc2.append(c3);

		GenericSkeleton skel2 = new GenericSkeleton();
		TextUnitUtil.trimTrailing(tcc2, skel2);

		tu1.setSourceContent(tcc2);		
		assertEquals("    123456  ", tu1.toString());
		assertEquals("", skel2.toString());

		// --------------------
		TextFragment tcc4 = new TextFragment("    123456  ");
		Code c4 = new Code(TagType.PLACEHOLDER, "code");
		tcc4.append(c4);

		char ch = TextUnitUtil.getLastChar(tcc4);
		assertEquals('6', ch);

		// --------------------
		TextFragment tcc5 = new TextFragment("    123456  ");

		TextUnitUtil.deleteLastChar(tcc5);
		assertEquals("    12345  ", tcc5.getCodedText());

		// --------------------
		TextFragment tcc6 = new TextFragment("123456_    ");

		assertTrue(TextUnitUtil.endsWith(tcc6, "_"));
		assertTrue(TextUnitUtil.endsWith(tcc6, "6_"));
		assertFalse(TextUnitUtil.endsWith(tcc6, "  "));

		TextFragment tcc7 = new TextFragment("123456<splicer>    ");
		assertTrue(TextUnitUtil.endsWith(tcc7, "<splicer>"));
		assertTrue(TextUnitUtil.endsWith(tcc7, "6<splicer>"));
		assertFalse(TextUnitUtil.endsWith(tcc7, "  "));
	}

	@Test
	public void testGetText() {

		// Using real fragment (not just coded text string, to have hasCode() working properly
		TextFragment tf = new TextFragment("ab");
		tf.append(TagType.OPENING, "type1", "z");
		tf.append("cde");
		tf.append(TagType.PLACEHOLDER, "type2", "z");
		tf.append("fgh");
		tf.append(TagType.PLACEHOLDER, "type3", "z");
		tf.append("ijklm");
		tf.append(TagType.CLOSING, "type1", "z");

		assertEquals("abcdefghijklm", TextUnitUtil.getText(tf));

		ArrayList<Integer> positions = new ArrayList<Integer>();
		assertEquals("abcdefghijklm", TextUnitUtil.getText(tf, positions));

		assertEquals(4, positions.size());

		assertEquals(2, (int) positions.get(0));
		assertEquals(7, (int) positions.get(1));
		assertEquals(12, (int) positions.get(2));
		assertEquals(19, (int) positions.get(3));

		tf = new TextFragment("ab");
		tf.append(TagType.OPENING, "type1", "z");
		tf.append("cde");
		tf.append(TagType.PLACEHOLDER, "type2", "z");
		tf.append("fgh");
		tf.append(TagType.PLACEHOLDER, "type3", "z");
		tf.append("ijklm");
		tf.append(TagType.CLOSING, "type1", "z");
		tf.append("n");

		assertEquals("abcdefghijklmn", TextUnitUtil.getText(tf));

		positions = new ArrayList<Integer>();
		assertEquals("abcdefghijklmn", TextUnitUtil.getText(tf, positions));

		assertEquals(4, positions.size());

		assertEquals(2, (int) positions.get(0));
		assertEquals(7, (int) positions.get(1));
		assertEquals(12, (int) positions.get(2));
		assertEquals(19, (int) positions.get(3));

		String st = "abcdefghijklmn";
		assertEquals(st, TextUnitUtil.getText(new TextFragment(st)));
		
		//-------------
		tf = new TextFragment("abcde");
		tf.append(TagType.PLACEHOLDER, "iso", "z");
		tf.append(TagType.PLACEHOLDER, "iso", "z");
		tf.append("fghijklm");
		tf.append(TagType.PLACEHOLDER, "iso", "z");
		tf.append(TagType.PLACEHOLDER, "iso", "z");
		tf.append("n");

		assertEquals("abcdefghijklmn", TextUnitUtil.getText(tf));

		positions = new ArrayList<Integer>();
		assertEquals("abcdefghijklmn", TextUnitUtil.getText(tf, positions));

		assertEquals(4, positions.size());

		assertEquals(5, (int) positions.get(0));
		assertEquals(7, (int) positions.get(1));
		assertEquals(17, (int) positions.get(2));
		assertEquals(19, (int) positions.get(3));

		st = "abcdefghijklmn";
		assertEquals(st, TextUnitUtil.getText(new TextFragment(st)));
	}

	@Test
	public void testRemoveQualifiers() {

		ITextUnit tu = TextUnitUtil.buildTU("\"qualified text\"");
		TextUnitUtil.removeQualifiers(tu, "\"");
		assertEquals("qualified text", tu.getSource().toString());

		tu.setSourceContent(new TextFragment("((({[qualified text]})))"));
		assertEquals("((({[qualified text]})))", tu.getSource().toString());
		TextUnitUtil.removeQualifiers(tu, "((({", "})))");
		assertEquals("[qualified text]", tu.getSource().toString());

		GenericSkeleton tuSkel = (GenericSkeleton) tu.getSkeleton();
		assertNotNull(tuSkel);
		List<GenericSkeletonPart> parts = tuSkel.getParts();
		assertEquals(5, parts.size());

		String tuRef = TextFragment.makeRefMarker("$self$");

		assertEquals("\"", parts.get(0).toString());
		assertEquals("((({", parts.get(1).toString());
		assertEquals(tuRef, parts.get(2).toString());
		assertEquals("})))", parts.get(3).toString());
		assertEquals("\"", parts.get(4).toString());
	}
	
	@Test
	public void testSimplifyCodes() {
		TextFragment tf = new TextFragment();
		tf.append(TagType.PLACEHOLDER, "x1", "<x1/>");
		tf.append("T1");
		tf.append(TagType.PLACEHOLDER, "x2", "<x2/>");
		
		TextUnit tu = new TextUnit("tu1");
		tu.setSourceContent(tf);
		
		assertEquals("<x1/>T1<x2/>", tu.getSource().toString());		
		TextUnitUtil.simplifyCodes(tu, true);		
		assertEquals("T1", tu.getSource().toString());
		
		GenericSkeleton tuSkel = (GenericSkeleton) tu.getSkeleton();
		assertNotNull(tuSkel);
		List<GenericSkeletonPart> parts = tuSkel.getParts();
		assertEquals(3, parts.size());
		
		assertEquals("<x1/>", parts.get(0).toString());		
		String tuRef = TextFragment.makeRefMarker("$self$");
		assertEquals(tuRef, parts.get(1).toString());
		assertEquals("<x2/>", parts.get(2).toString());
	}
	
	@Test
	public void testSimplifyCodes_segmentedTU() {
		TextContainer tc = new TextContainer();
		tc.append(new Segment("s1", new TextFragment("[seg 1]")));
		tc.append(new TextPart("[text part 1]"));
		tc.append(new Segment("s2", new TextFragment("[seg 2]")));
		tc.append(new TextPart("[text part 2]"));
		tc.append(new TextPart("[text part 3]"));
		tc.append(new Segment("s3", new TextFragment("[seg 3]")));
		tc.append(new Segment("s4", new TextFragment("[seg 4]")));
		
		String[] res = TextUnitUtil.simplifyCodes(tc, false);
		assertEquals("[seg 1][text part 1][seg 2][text part 2][text part 3][seg 3][seg 4]", tc.toString());
		assertNull(res);
	}
	
	@Test
	public void testSimplifyCodes_segmentedTU2() {
		TextContainer tc = new TextContainer();
		tc.append(new Segment("s1", new TextFragment("[seg 1]")));
		
		String[] res = TextUnitUtil.simplifyCodes(tc, false);		
		assertEquals("[seg 1]", tc.toString());
		assertNull(res);
	}
	
	@Test
	public void testSimplifyCodes_segmentedTU3() {
		TextFragment tf1 = new TextFragment();		
		tf1.append(TagType.PLACEHOLDER, "x11", "<x11/>");
		tf1.append(TagType.PLACEHOLDER, "x12", "<x12/>");
		tf1.append("[seg 1]");
		tf1.append(TagType.PLACEHOLDER, "x13", "<x13/>");
		tf1.append(TagType.PLACEHOLDER, "x14", "<x14/>");
		
		TextContainer tc = new TextContainer();
		tc.append(new Segment("s1", tf1));
		
		String[] res = TextUnitUtil.simplifyCodes(tc, true);		
		assertEquals("[seg 1]", tc.toString());
		assertNotNull(res);
		assertEquals("<x11/><x12/>", res[0]);
		assertEquals("<x13/><x14/>", res[1]);
	}
	
	@Test
	public void testSimplifyCodes_segmentedTU4() {
		TextFragment tf1 = new TextFragment();		
		tf1.append(TagType.PLACEHOLDER, "x11", "<x11/>");
		tf1.append(TagType.PLACEHOLDER, "x12", "<x12/>");
		tf1.append("   [seg 1]");
		tf1.append(TagType.PLACEHOLDER, "x13", "<x13/>");
		tf1.append(TagType.PLACEHOLDER, "x14", "<x14/>");
		
		TextContainer tc = new TextContainer();
		tc.append(new Segment("s1", tf1));
		
		String[] res = TextUnitUtil.simplifyCodes(tc, true);		
		assertEquals("[seg 1]", tc.toString());
		assertNotNull(res);
		assertEquals("<x11/><x12/>   ", res[0]);
		assertEquals("<x13/><x14/>", res[1]);
	}
	
	@Test
	public void testSimplifyCodes_segmentedTU5() {
		TextFragment tf1 = new TextFragment();		
		tf1.append(TagType.PLACEHOLDER, "x11", "<x11/>");
		tf1.append(TagType.PLACEHOLDER, "x12", "<x12/>");
		tf1.append("   [seg 1]");
		tf1.append(TagType.PLACEHOLDER, "x13", "<x13/>");
		tf1.append(TagType.PLACEHOLDER, "x14", "<x14/>");
		
		TextFragment tf2 = new TextFragment();		
		tf2.append(TagType.PLACEHOLDER, "x21", "<x21/>");
		tf2.append(TagType.PLACEHOLDER, "x22", "<x22/>");
		tf2.append("   [seg 2]");
		tf2.append(TagType.PLACEHOLDER, "x23", "<x23/>");
		tf2.append(TagType.PLACEHOLDER, "x24", "<x24/>");
		
		TextContainer tc = new TextContainer();
		tc.append(new Segment("s1", tf1));
		tc.append(new Segment("s2", tf2));
		
		String[] res = TextUnitUtil.simplifyCodes(tc, true);		
		assertEquals("[seg 1]<x13/><x14/><x21/><x22/>   [seg 2]", tc.toString());
		assertNotNull(res);
		assertEquals("<x11/><x12/>   ", res[0]);
		assertEquals("<x23/><x24/>", res[1]);
		
		assertEquals(5, tc.count());
		assertTrue(tc.get(0).isSegment());
		assertFalse(tc.get(1).isSegment());
		assertFalse(tc.get(2).isSegment());
		assertFalse(tc.get(3).isSegment());
		assertTrue(tc.get(4).isSegment());
		
		assertEquals("[seg 1]", tc.get(0).toString());
		assertEquals("<x13/><x14/>", tc.get(1).toString());
		assertEquals("<x21/><x22/>", tc.get(2).toString());
		assertEquals("<x21/><x22/>", tc.get(2).toString());
		assertEquals("   ", tc.get(3).toString());
		assertEquals("[seg 2]", tc.get(4).toString());
		
		ISegments segs = tc.getSegments();
		assertEquals(2, segs.count());
	}
	
	@Test
	public void testStoreSegmentation () {		
		TextContainer tc = new TextContainer();
		tc.append(new Segment("s1", new TextFragment("[seg 1]")));
		tc.append(new TextPart("[text part 1]"));
		tc.append(new Segment("s2", new TextFragment("[seg 2]")));
		tc.append(new TextPart("[text part 2]"));
		tc.append(new TextPart("[text part 3]"));
		tc.append(new Segment("s3", new TextFragment("[seg 3]")));
		
		TextFragment tf = TextUnitUtil.storeSegmentation(tc);
		assertEquals("<1/>[seg 1]<2/><3/>[text part 1]<4/><5/>[seg 2]<6/><7/>[text part 2]<8/>" +
				"<9/>[text part 3]<10/><11/>[seg 3]<12/>", 
				fmt.setContent(tf).toString());
		
		List<Code> codes = tf.getCodes();
		assertEquals(12, codes.size());
		
		assertEquals("[#$s1@%$seg_start$]", codes.get(0).toString()); // <1/>
		assertEquals("[#$s1@%$seg_end$]", codes.get(1).toString()); // <2/>
		
		assertEquals("$tp_start$", codes.get(2).toString()); // <3/>
		assertEquals("$tp_end$", codes.get(3).toString()); // <4/>
		
		assertEquals("[#$s2@%$seg_start$]", codes.get(4).toString()); // <5/>
		assertEquals("[#$s2@%$seg_end$]", codes.get(5).toString()); // <6/>
		
		assertEquals("$tp_start$", codes.get(6).toString()); // <7/>
		assertEquals("$tp_end$", codes.get(7).toString()); // <8/>
		
		assertEquals("$tp_start$", codes.get(8).toString()); // <9/>
		assertEquals("$tp_end$", codes.get(9).toString()); // <10/>
		
		assertEquals("[#$s3@%$seg_start$]", codes.get(10).toString()); // <11/>
		assertEquals("[#$s3@%$seg_end$]", codes.get(11).toString()); // <12/>		
	}

	
	
	@Test
	public void testTreeSet() {
		TreeSet<Integer> set = new TreeSet<Integer>();
		set.add(5);
		set.add(1);
		set.add(5);
		set.add(3);
		set.add(9);
		
		assertEquals(4, set.size()); // 5 is repeated
		assertEquals("[1, 3, 5, 9]", set.toString());
	}
	
	@Test
	public void testTreeMap() {
		TreeMap<Integer, String> map = new TreeMap<Integer, String>();
		map.put(5, "");
		map.put(1, "");
		map.put(5, "");
		map.put(3, "");
		map.put(9, "");
		
		assertEquals(4, map.size()); // 5 is repeated
		//assertEquals("[1, 3, 5, 9]", set.toString());
	}
	
	@Test
	public void testHashtableSort() {
		Hashtable<String, String> h = new Hashtable<String, String>();
	    h.put("a", "b");	    
	    h.put("c", "d");
	    h.put("e", "f");
	    h.put("a", "bb");
	    List<String> v = new ArrayList<String>(h.keySet());
	    Collections.sort(v);
	}
	
	@Test
	public void testRestoreSegmentation () {		
		TextContainer tc = new TextContainer();
		tc.append(new Segment("s1", new TextFragment("[seg 1]")));
		tc.append(new TextPart("[text part 1]"));
		tc.append(new Segment("s2", new TextFragment("[seg 2]")));
		tc.append(new TextPart("[text part 2]"));
		tc.append(new TextPart("[text part 3]"));
		tc.append(new Segment("s3", new TextFragment("[seg 3]")));
		tc.append(new Segment("s4", new TextFragment("[seg 4]")));
		
		TextFragment tf = TextUnitUtil.storeSegmentation(tc);
		assertEquals("<1/>[seg 1]<2/><3/>[text part 1]<4/><5/>[seg 2]<6/><7/>[text part 2]<8/>" +
				"<9/>[text part 3]<10/><11/>[seg 3]<12/><13/>[seg 4]<14/>", 
				fmt.setContent(tf).toString());

		TextUnitUtil.restoreSegmentation(tc, tf);
		assertEquals("(2: seg_start s1) (9: seg_end s1) (13: tp_start) (26: tp_end) (30: seg_start s2) (37: seg_end s2) " +
				"(41: tp_start) (54: tp_end) (58: tp_start) (71: tp_end) (75: seg_start s3) (82: seg_end s3) " +
				"(86: seg_start s4) (93: seg_end s4)", TextUnitUtil.testMarkers());
		
		assertEquals("[seg 1][text part 1][seg 2][text part 2][text part 3][seg 3][seg 4]", tc.toString());
		
		
		Iterator<TextPart> it = tc.iterator();
		
		TextPart part = null; 
		
		if (it.hasNext()) {
			part = it.next();
			assertTrue(part.isSegment());
			assertEquals("[seg 1]", part.toString());
		}
				
		if (it.hasNext()) {
			part = it.next();
			assertFalse(part.isSegment());
			assertEquals("[text part 1]", part.toString());
		}
		
		if (it.hasNext()) {
			part = it.next();
			assertTrue(part.isSegment());
			assertEquals("[seg 2]", part.toString());
		}
		
		if (it.hasNext()) {
			part = it.next();
			assertFalse(part.isSegment());
			assertEquals("[text part 2]", part.toString());
		}
		
		if (it.hasNext()) {
			part = it.next();
			assertFalse(part.isSegment());
			assertEquals("[text part 3]", part.toString());
		}
		
		if (it.hasNext()) {
			part = it.next();
			assertTrue(part.isSegment());
			assertEquals("[seg 3]", part.toString());
		}		
		
		if (it.hasNext()) {
			part = it.next();
			assertTrue(part.isSegment());
			assertEquals("[seg 4]", part.toString());
		}
	}
	
	@Test
	public void testRestoreSegmentation2 () {		
		TextContainer tc = new TextContainer();
		tc.append(new Segment("s1", new TextFragment("[seg 1]")));
		tc.append(new TextPart("[text part 1]"));
		tc.append(new Segment("s2", new TextFragment("[seg 2]")));
		tc.append(new TextPart("[text part 2]"));
		tc.append(new TextPart("[text part 3]"));
		tc.append(new Segment("s3", new TextFragment("[seg 3]")));
		tc.append(new TextPart("[text part 4]"));
		
		TextFragment tf = TextUnitUtil.storeSegmentation(tc);
		assertEquals("<1/>[seg 1]<2/><3/>[text part 1]<4/><5/>[seg 2]<6/><7/>[text part 2]<8/>" +
				"<9/>[text part 3]<10/><11/>[seg 3]<12/><13/>[text part 4]<14/>", 
				fmt.setContent(tf).toString());

		TextUnitUtil.restoreSegmentation(tc, tf);
		assertEquals("[seg 1][text part 1][seg 2][text part 2][text part 3][seg 3][text part 4]", tc.toString());
		
		Iterator<TextPart> it = tc.iterator();		
		TextPart part = null; 
		
		if (it.hasNext()) {
			part = it.next();
			assertTrue(part.isSegment());
			assertEquals("[seg 1]", part.toString());
		}
				
		if (it.hasNext()) {
			part = it.next();
			assertFalse(part.isSegment());
			assertEquals("[text part 1]", part.toString());
		}
		
		if (it.hasNext()) {
			part = it.next();
			assertTrue(part.isSegment());
			assertEquals("[seg 2]", part.toString());
		}
		
		if (it.hasNext()) {
			part = it.next();
			assertFalse(part.isSegment());
			assertEquals("[text part 2]", part.toString());
		}
		
		if (it.hasNext()) {
			part = it.next();
			assertFalse(part.isSegment());
			assertEquals("[text part 3]", part.toString());
		}
		
		if (it.hasNext()) {
			part = it.next();
			assertTrue(part.isSegment());
			assertEquals("[seg 3]", part.toString());
		}		
		
		if (it.hasNext()) {
			part = it.next();
			assertFalse(part.isSegment());
			assertEquals("[text part 4]", part.toString());
		}
	}
	
	@Test
	public void testRestoreSegmentation3 () {		
		TextFragment tf1 = new TextFragment();		
		tf1.append(TagType.PLACEHOLDER, "x11", "<x11/>");
		tf1.append(TagType.PLACEHOLDER, "x12", "<x12/>");
		tf1.append(TagType.PLACEHOLDER, "x13", "<x13/>");
		tf1.append(TagType.PLACEHOLDER, "x14", "<x14/>");
		
		TextFragment tf2 = new TextFragment();		
		tf2.append(TagType.PLACEHOLDER, "x21", "<x21/>");
		tf2.append(TagType.PLACEHOLDER, "x22", "<x22/>");
		tf2.append(TagType.PLACEHOLDER, "x23", "<x23/>");
		tf2.append(TagType.PLACEHOLDER, "x24", "<x24/>");
		
		TextFragment tf3 = new TextFragment();		
		tf3.append(TagType.PLACEHOLDER, "x31", "<x31/>");
		tf3.append(TagType.PLACEHOLDER, "x32", "<x32/>");
		tf3.append(TagType.PLACEHOLDER, "x33", "<x33/>");
		tf3.append(TagType.PLACEHOLDER, "x34", "<x34/>");
				
		assertEquals("<1/><2/><3/><4/>", fmt.setContent(tf1).toString());		
		
		TextContainer tc = new TextContainer();
		tc.append(new Segment("s1", tf1));
		tc.append(new TextPart("[text part 1]"));
		tc.append(new Segment("s2", tf2));
		tc.append(new TextPart("[text part 2]"));
		tc.append(new TextPart("[text part 3]"));
		tc.append(new Segment("s3", tf3));
		tc.append(new TextPart("[text part 4]"));
		
		//String saveTc = tc.toString();
		
		TextFragment tf = TextUnitUtil.storeSegmentation(tc);
		assertEquals("<1/><1/><2/><3/><4/><2/><3/>[text part 1]<4/><5/><1/><2/><3/><4/><6/><7/>[text part 2]<8/>" +
				"<9/>[text part 3]<10/><11/><1/><2/><3/><4/><12/><13/>[text part 4]<14/>", 
				fmt.setContent(tf).toString());

		TextUnitUtil.simplifyCodes(tf, false);
		assertEquals("<1/>[text part 1]<2/>[text part 2]<3/>[text part 3]<4/>[text part 4]<5/>", 
				fmt.setContent(tf).toString());
		
		List<Code> codes = tf.getCodes();
		assertEquals("[#$s1@%$seg_start$]<x11/><x12/><x13/><x14/>[#$s1@%$seg_end$]$tp_start$", codes.get(0).toString()); // <1/>
		
		assertEquals("$tp_end$[#$s2@%$seg_start$]<x21/><x22/><x23/><x24/>[#$s2@%$seg_end$]$tp_start$", codes.get(1).toString()); // <2/>
		
		assertEquals("$tp_end$$tp_start$", codes.get(2).toString()); // <3/>
		
		assertEquals("$tp_end$[#$s3@%$seg_start$]<x31/><x32/><x33/><x34/>[#$s3@%$seg_end$]$tp_start$", codes.get(3).toString()); // <4/>
		
		assertEquals("$tp_end$", codes.get(4).toString()); // <5/>
				
		TextUnitUtil.restoreSegmentation(tc, tf);
		assertEquals("<x11/><x12/><x13/><x14/>[text part 1]<x21/><x22/><x23/><x24/>[text part 2][text part 3]<x31/><x32/><x33/><x34/>[text part 4]", tc.toString());
		
		Iterator<TextPart> it = tc.iterator();		
		TextPart part = null;
		if (it.hasNext()) {
			part = it.next();
			assertTrue(part.isSegment());
			assertEquals("<x11/><x12/><x13/><x14/>", part.toString());
		}
				
		if (it.hasNext()) {
			part = it.next();
			assertFalse(part.isSegment());
			assertEquals("[text part 1]", part.toString());
		}
		
		if (it.hasNext()) {
			part = it.next();
			assertTrue(part.isSegment());
			assertEquals("<x21/><x22/><x23/><x24/>", part.toString());
		}
		
		if (it.hasNext()) {
			part = it.next();
			assertFalse(part.isSegment());
			assertEquals("[text part 2]", part.toString());
		}
		
		if (it.hasNext()) {
			part = it.next();
			assertFalse(part.isSegment());
			assertEquals("[text part 3]", part.toString());
		}
		
		if (it.hasNext()) {
			part = it.next();
			assertTrue(part.isSegment());
			assertEquals("<x31/><x32/><x33/><x34/>", part.toString());
		}
		
		if (it.hasNext()) {
			part = it.next();
			assertFalse(part.isSegment());
			assertEquals("[text part 4]", part.toString());
		}
	}
	
	@Test
	public void testRestoreSegmentation3_2 () {		
		TextFragment tf1 = new TextFragment();		
		tf1.append(TagType.PLACEHOLDER, "x11", "<x11/>");
		tf1.append(TagType.PLACEHOLDER, "x12", "<x12/>");
		tf1.append("[seg 1]");
		tf1.append(TagType.PLACEHOLDER, "x13", "<x13/>");
		tf1.append(TagType.PLACEHOLDER, "x14", "<x14/>");
		
		TextFragment tf2 = new TextFragment();
		tf2.append("[seg 2]");
		tf2.append(TagType.PLACEHOLDER, "x21", "<x21/>");
		tf2.append(TagType.PLACEHOLDER, "x22", "<x22/>");
		tf2.append(TagType.PLACEHOLDER, "x23", "<x23/>");
		tf2.append(TagType.PLACEHOLDER, "x24", "<x24/>");
		
		TextFragment tf3 = new TextFragment();		
		tf3.append(TagType.PLACEHOLDER, "x31", "<x31/>");
		tf3.append(TagType.PLACEHOLDER, "x32", "<x32/>");
		tf3.append(TagType.PLACEHOLDER, "x33", "<x33/>");
		tf3.append("[seg 3]");
		tf3.append(TagType.PLACEHOLDER, "x34", "<x34/>");
				
		assertEquals("<1/><2/>[seg 1]<3/><4/>", fmt.setContent(tf1).toString());		
		assertEquals("[seg 2]<1/><2/><3/><4/>", fmt.setContent(tf2).toString());
		assertEquals("<1/><2/><3/>[seg 3]<4/>", fmt.setContent(tf3).toString());
		
		TextContainer tc = new TextContainer();
		tc.append(new Segment("s1", tf1));
		tc.append(new TextPart("[text part 1]"));
		tc.append(new Segment("s2", tf2));
		tc.append(new TextPart("[text part 2]"));
		tc.append(new TextPart("[text part 3]"));
		tc.append(new Segment("s3", tf3));
		tc.append(new TextPart("[text part 4]"));
		
		//String saveTc = tc.toString();
		
		TextFragment tf = TextUnitUtil.storeSegmentation(tc);
		assertEquals("<1/><1/><2/>[seg 1]<3/><4/><2/><3/>[text part 1]<4/><5/>[seg 2]<1/><2/><3/><4/><6/><7/>" +
				"[text part 2]<8/><9/>[text part 3]<10/><11/><1/><2/><3/>[seg 3]<4/><12/><13/>[text part 4]<14/>", 
				fmt.setContent(tf).toString());
		
		assertEquals("[#$s1@%$seg_start$]<x11/><x12/>[seg 1]<x13/><x14/>[#$s1@%$seg_end$]$tp_start$[text part 1]$tp_end$" +
				"[#$s2@%$seg_start$][seg 2]<x21/><x22/><x23/><x24/>[#$s2@%$seg_end$]$tp_start$[text part 2]$tp_end$" +
				"$tp_start$[text part 3]$tp_end$[#$s3@%$seg_start$]<x31/><x32/><x33/>[seg 3]<x34/>[#$s3@%$seg_end$]" +
				"$tp_start$[text part 4]$tp_end$", 
				tf.toText());
				
		TextUnitUtil.simplifyCodes(tf, false);
		assertEquals("<1/>[seg 1]<2/>[text part 1]<3/>[seg 2]<4/>[text part 2]<5/>[text part 3]<6/>[seg 3]<7/>[text part 4]<8/>", 
				fmt.setContent(tf).toString());				

		// Codes after simplification
		List<Code> codes = tf.getCodes();
		assertEquals(8, codes.size());
		
		assertEquals("[#$s1@%$seg_start$]<x11/><x12/>", codes.get(0).toString()); // <1/>
		
		assertEquals("<x13/><x14/>[#$s1@%$seg_end$]$tp_start$", codes.get(1).toString()); // <2/>
		
		assertEquals("$tp_end$[#$s2@%$seg_start$]", codes.get(2).toString()); // <3/>
		
		assertEquals("<x21/><x22/><x23/><x24/>[#$s2@%$seg_end$]$tp_start$", codes.get(3).toString()); // <4/>
		
		assertEquals("$tp_end$$tp_start$", codes.get(4).toString()); // <5/>
		
		assertEquals("$tp_end$[#$s3@%$seg_start$]<x31/><x32/><x33/>", codes.get(5).toString()); // <6/>
		
		assertEquals("<x34/>[#$s3@%$seg_end$]$tp_start$", codes.get(6).toString()); // <7/>
		
		assertEquals("$tp_end$", codes.get(7).toString()); // <8/>
		
		TextUnitUtil.restoreSegmentation(tc, tf);
		assertEquals("(0-19: tp_start) (0-31: tp_end) (2: seg_start s1) (9: seg_end s1) (9-0: tp_start) (9-12: tp_end) " +
				"(11: tp_start) (24: tp_end) (26: seg_start s2) (33: seg_end s2) (33-0: tp_start) (33-24: tp_end) " +
				"(35: tp_start) (48: tp_end) (50: tp_start) (63: tp_end) (63-27: tp_start) (63-45: tp_end) " +
				"(65: seg_start s3) (72: seg_end s3) (72-0: tp_start) (72-6: tp_end) (74: tp_start) (87: tp_end)", 
				TextUnitUtil.testMarkers());
		
		assertEquals("<x11/><x12/>[seg 1]<x13/><x14/>[text part 1][seg 2]<x21/><x22/><x23/><x24/>[text part 2]" +
				"[text part 3]<x31/><x32/><x33/>[seg 3]<x34/>[text part 4]", tc.toString());
		
		assertEquals(12, tc.count());
		
		assertTrue(!tc.get(0).isSegment());
		assertTrue(tc.get(1).isSegment());
		assertTrue(!tc.get(2).isSegment());
		assertTrue(!tc.get(3).isSegment());
		assertTrue(tc.get(4).isSegment());
		assertTrue(!tc.get(5).isSegment());
		assertTrue(!tc.get(6).isSegment());
		assertTrue(!tc.get(7).isSegment());
		assertTrue(!tc.get(8).isSegment());
		assertTrue(tc.get(9).isSegment());
		assertTrue(!tc.get(10).isSegment());
		assertTrue(!tc.get(11).isSegment());
		
		assertEquals("<x11/><x12/>", tc.get(0).toString());
		assertEquals("[seg 1]", tc.get(1).toString());
		assertEquals("<x13/><x14/>", tc.get(2).toString());
		assertEquals("[text part 1]", tc.get(3).toString());
		assertEquals("[seg 2]", tc.get(4).toString());
		assertEquals("<x21/><x22/><x23/><x24/>", tc.get(5).toString());
		assertEquals("[text part 2]", tc.get(6).toString());
		assertEquals("[text part 3]", tc.get(7).toString());
		assertEquals("<x31/><x32/><x33/>", tc.get(8).toString());
		assertEquals("[seg 3]", tc.get(9).toString());
		assertEquals("<x34/>", tc.get(10).toString());
		assertEquals("[text part 4]", tc.get(11).toString());
	}
	
	@Test
	public void testRestoreSegmentation3_3 () {		
		TextFragment tf1 = new TextFragment();		
		tf1.append(TagType.PLACEHOLDER, "x11", "<x11/>");
		tf1.append(TagType.PLACEHOLDER, "x12", "<x12/>");
		tf1.append("[seg 1]");
		tf1.append(TagType.PLACEHOLDER, "x13", "<x13/>");
		tf1.append(TagType.PLACEHOLDER, "x14", "<x14/>");
		
		TextFragment tf2 = new TextFragment();
		tf2.append("[seg 2]");
		tf2.append(TagType.PLACEHOLDER, "x21", "<x21/>");
		tf2.append(TagType.PLACEHOLDER, "x22", "<x22/>");
		tf2.append(TagType.PLACEHOLDER, "x23", "<x23/>");
		tf2.append(TagType.PLACEHOLDER, "x24", "<x24/>");
		
		TextFragment tf3 = new TextFragment();		
		tf3.append(TagType.PLACEHOLDER, "x31", "<x31/>");
		tf3.append(TagType.PLACEHOLDER, "x32", "<x32/>");
		tf3.append(TagType.PLACEHOLDER, "x33", "<x33/>");
		tf3.append("[seg 3]");
		tf3.append(TagType.PLACEHOLDER, "x34", "<x34/>");
				
		assertEquals("<1/><2/>[seg 1]<3/><4/>", fmt.setContent(tf1).toString());		
		assertEquals("[seg 2]<1/><2/><3/><4/>", fmt.setContent(tf2).toString());
		assertEquals("<1/><2/><3/>[seg 3]<4/>", fmt.setContent(tf3).toString());
		
		TextContainer tc = new TextContainer();
		tc.append(new Segment("s1", tf1));
		tc.append(new TextPart("[text part 1]"));
		tc.append(new Segment("s2", tf2));
		tc.append(new TextPart("[text part 2]"));
		tc.append(new TextPart("[text part 3]"));
		tc.append(new Segment("s3", tf3));
		tc.append(new TextPart("[text part 4]"));
		
		//String saveTc = tc.toString();
		
		TextFragment tf = TextUnitUtil.storeSegmentation(tc);
		assertEquals("<1/><1/><2/>[seg 1]<3/><4/><2/><3/>[text part 1]<4/><5/>[seg 2]<1/><2/><3/><4/><6/><7/>" +
				"[text part 2]<8/><9/>[text part 3]<10/><11/><1/><2/><3/>[seg 3]<4/><12/><13/>[text part 4]<14/>", 
				fmt.setContent(tf).toString());
		
		assertEquals("[#$s1@%$seg_start$]<x11/><x12/>[seg 1]<x13/><x14/>[#$s1@%$seg_end$]$tp_start$[text part 1]$tp_end$" +
				"[#$s2@%$seg_start$][seg 2]<x21/><x22/><x23/><x24/>[#$s2@%$seg_end$]$tp_start$[text part 2]$tp_end$" +
				"$tp_start$[text part 3]$tp_end$[#$s3@%$seg_start$]<x31/><x32/><x33/>[seg 3]<x34/>[#$s3@%$seg_end$]" +
				"$tp_start$[text part 4]$tp_end$", 
				tf.toText());
				
		String[] res = TextUnitUtil.simplifyCodes(tf, true);		
		assertEquals("[seg 1]<2/>[text part 1]<3/>[seg 2]<4/>[text part 2]<5/>[text part 3]<6/>[seg 3]<7/>[text part 4]", 
				fmt.setContent(tf).toString());
		assertEquals("[seg 1]<x13/><x14/>[#$s1@%$seg_end$]$tp_start$[text part 1]$tp_end$" +
				"[#$s2@%$seg_start$][seg 2]<x21/><x22/><x23/><x24/>[#$s2@%$seg_end$]$tp_start$[text part 2]$tp_end$" +
				"$tp_start$[text part 3]$tp_end$[#$s3@%$seg_start$]<x31/><x32/><x33/>[seg 3]<x34/>[#$s3@%$seg_end$]" +
				"$tp_start$[text part 4]", 
				tf.toText());
		assertEquals("[#$s1@%$seg_start$]<x11/><x12/>", res[0]);
		assertEquals("$tp_end$", res[1]);

		// Codes after simplification
		List<Code> codes = tf.getCodes();
		assertEquals(6, codes.size());
		
		assertEquals("<x13/><x14/>[#$s1@%$seg_end$]$tp_start$", codes.get(0).toString()); // <1/>
				
		assertEquals("$tp_end$[#$s2@%$seg_start$]", codes.get(1).toString()); // <2/>
		
		assertEquals("<x21/><x22/><x23/><x24/>[#$s2@%$seg_end$]$tp_start$", codes.get(2).toString()); // <3/>
		
		assertEquals("$tp_end$$tp_start$", codes.get(3).toString()); // <4/>
		
		assertEquals("$tp_end$[#$s3@%$seg_start$]<x31/><x32/><x33/>", codes.get(4).toString()); // <5/>
		
		assertEquals("<x34/>[#$s3@%$seg_end$]$tp_start$", codes.get(5).toString()); // <6/>
		
		TextUnitUtil.restoreSegmentation(tc, tf);
		assertEquals("(7: seg_end s1) (7-0: tp_start) (7-12: tp_end) (9: tp_start) (22: tp_end) (24: seg_start s2) " +
				"(31: seg_end s2) (31-0: tp_start) (31-24: tp_end) (33: tp_start) (46: tp_end) (48: tp_start) (61: tp_end) " +
				"(61-27: tp_start) (61-45: tp_end) (63: seg_start s3) (70: seg_end s3) (70-0: tp_start) (70-6: tp_end) (72: tp_start)", 
				TextUnitUtil.testMarkers());
		
		assertEquals("<x13/><x14/>[text part 1][seg 2]<x21/><x22/><x23/><x24/>[text part 2][text part 3]" +
				"<x31/><x32/><x33/>[seg 3]<x34/>", tc.toString());
		
		TextPart part = null;
		assertEquals(9, tc.count());
		
		part = tc.get(0);
		assertFalse(part.isSegment());
		assertEquals("<x13/><x14/>", part.toString());
				
		part = tc.get(1);
		assertFalse(part.isSegment());
		assertEquals("[text part 1]", part.toString());
	
		part = tc.get(2);
		assertTrue(part.isSegment());
		assertEquals("[seg 2]", part.toString());
	
		part = tc.get(3);
		assertFalse(part.isSegment());
		assertEquals("<x21/><x22/><x23/><x24/>", part.toString());
		
		part = tc.get(4);
		assertFalse(part.isSegment());
		assertEquals("[text part 2]", part.toString());
	
		part = tc.get(5);
		assertFalse(part.isSegment());
		assertEquals("[text part 3]", part.toString());
	
		part = tc.get(6);
		assertFalse(part.isSegment());
		assertEquals("<x31/><x32/><x33/>", part.toString());
		
		part = tc.get(7);
		assertTrue(part.isSegment());
		assertEquals("[seg 3]", part.toString());
	
		part = tc.get(8);
		assertFalse(part.isSegment());
		assertEquals("<x34/>", part.toString());
	}
	
	@Test
	public void testRestoreSegmentation3_4 () {		
		TextFragment tf1 = new TextFragment();		
		tf1.append(TagType.PLACEHOLDER, "x11", "<x11/>");
		tf1.append(TagType.PLACEHOLDER, "x12", "<x12/>");
		tf1.append("[seg 1]");
		tf1.append(TagType.PLACEHOLDER, "x13", "<x13/>");
		tf1.append(TagType.PLACEHOLDER, "x14", "<x14/>");
		
		TextFragment tf2 = new TextFragment();
		tf2.append("[seg 2]");
		tf2.append(TagType.PLACEHOLDER, "x21", "<x21/>");
		tf2.append(TagType.PLACEHOLDER, "x22", "<x22/>");
		tf2.append(TagType.PLACEHOLDER, "x23", "<x23/>");
		tf2.append(TagType.PLACEHOLDER, "x24", "<x24/>");
		
		TextFragment tf3 = new TextFragment();		
		tf3.append(TagType.PLACEHOLDER, "x31", "<x31/>");
		tf3.append(TagType.PLACEHOLDER, "x32", "<x32/>");
		tf3.append(TagType.PLACEHOLDER, "x33", "<x33/>");
		tf3.append("[seg 3]");
		tf3.append(TagType.PLACEHOLDER, "x34", "<x34/>");
				
		assertEquals("<1/><2/>[seg 1]<3/><4/>", fmt.setContent(tf1).toString());		
		assertEquals("[seg 2]<1/><2/><3/><4/>", fmt.setContent(tf2).toString());
		assertEquals("<1/><2/><3/>[seg 3]<4/>", fmt.setContent(tf3).toString());
		
		TextContainer tc = new TextContainer();
		tc.append(new Segment("s1", tf1));
		tc.append(new TextPart("[text part 1]"));
		tc.append(new Segment("s2", tf2));
		tc.append(new TextPart("[text part 2]"));
		tc.append(new TextPart("[text part 3]"));
		tc.append(new Segment("s3", tf3));
		tc.append(new TextPart("[text part 4]"));
		
		//String saveTc = tc.toString();
		
		TextFragment tf = TextUnitUtil.storeSegmentation(tc);
		assertEquals("<1/><1/><2/>[seg 1]<3/><4/><2/><3/>[text part 1]<4/><5/>[seg 2]<1/><2/><3/><4/><6/><7/>" +
				"[text part 2]<8/><9/>[text part 3]<10/><11/><1/><2/><3/>[seg 3]<4/><12/><13/>[text part 4]<14/>", 
				fmt.setContent(tf).toString());
		
		assertEquals("[#$s1@%$seg_start$]<x11/><x12/>[seg 1]<x13/><x14/>[#$s1@%$seg_end$]$tp_start$[text part 1]$tp_end$" +
				"[#$s2@%$seg_start$][seg 2]<x21/><x22/><x23/><x24/>[#$s2@%$seg_end$]$tp_start$[text part 2]$tp_end$" +
				"$tp_start$[text part 3]$tp_end$[#$s3@%$seg_start$]<x31/><x32/><x33/>[seg 3]<x34/>[#$s3@%$seg_end$]" +
				"$tp_start$[text part 4]$tp_end$", 
				tf.toText());
				
		String[] res = TextUnitUtil.simplifyCodes(tc, true);		
		assertEquals("<1/><1/><2/>[seg 1]<3/><4/><2/><3/>[text part 1]<4/><5/>[seg 2]<1/><2/><3/><4/><6/><7/>[text part 2]" +
				"<8/><9/>[text part 3]<10/><11/><1/><2/><3/>[seg 3]<4/><12/><13/>[text part 4]<14/>", 
				fmt.setContent(tf).toString());
		assertEquals("[#$s1@%$seg_start$]<x11/><x12/>[seg 1]<x13/><x14/>[#$s1@%$seg_end$]$tp_start$[text part 1]$tp_end$" +
				"[#$s2@%$seg_start$][seg 2]<x21/><x22/><x23/><x24/>[#$s2@%$seg_end$]$tp_start$[text part 2]$tp_end$" +
				"$tp_start$[text part 3]$tp_end$[#$s3@%$seg_start$]<x31/><x32/><x33/>[seg 3]<x34/>[#$s3@%$seg_end$]" +
				"$tp_start$[text part 4]$tp_end$", 
				tf.toText());
		assertEquals("<x11/><x12/>", res[0]);
		assertEquals("", res[1]);
		
		TextPart part = null;
		assertEquals(11, tc.count());
		
		part = tc.get(0);
		assertTrue(part.isSegment());
		assertEquals("[seg 1]", part.toString());
		
		part = tc.get(1);
		assertFalse(part.isSegment());
		assertEquals("<x13/><x14/>", part.toString());
		
		part = tc.get(2);
		assertFalse(part.isSegment());
		assertEquals("[text part 1]", part.toString());
	
		part = tc.get(3);
		assertTrue(part.isSegment());
		assertEquals("[seg 2]", part.toString());
	
		part = tc.get(4);
		assertFalse(part.isSegment());
		assertEquals("<x21/><x22/><x23/><x24/>", part.toString());
		
		part = tc.get(5);
		assertFalse(part.isSegment());
		assertEquals("[text part 2]", part.toString());
	
		part = tc.get(6);
		assertFalse(part.isSegment());
		assertEquals("[text part 3]", part.toString());
	
		part = tc.get(7);
		assertFalse(part.isSegment());
		assertEquals("<x31/><x32/><x33/>", part.toString());
		
		part = tc.get(8);
		assertTrue(part.isSegment());
		assertEquals("[seg 3]", part.toString());
		
		part = tc.get(9);
		assertFalse(part.isSegment());
		assertEquals("<x34/>", part.toString());
		
		part = tc.get(10);
		assertFalse(part.isSegment());
		assertEquals("[text part 4]", part.toString());
	}
	
	@Test
	public void testExtractSegMarkers() {
		String st = "$tp_end$[#$s3@%$seg_start$]<x31/><x32/><x33/><x34/>[#$s3@%$seg_end$]$tp_start$";
		String res;
		TextFragment tf = new TextFragment(); 
		res = TextUnitUtil.extractSegMarkers(tf, st, false);
		
		assertEquals("<1/><2/><3/><4/>", fmt.setContent(tf).toString());
		assertEquals("$tp_end$[#$s3@%$seg_start$]<x31/><x32/><x33/><x34/>[#$s3@%$seg_end$]$tp_start$", res);
		
		List<Code> codes = tf.getCodes();
		assertEquals(4, codes.size());
		
		assertEquals("$tp_end$", codes.get(0).toString()); // <1/>
		assertEquals("[#$s3@%$seg_start$]", codes.get(1).toString()); // <2/>
		assertEquals("[#$s3@%$seg_end$]", codes.get(2).toString()); // <3/>
		assertEquals("$tp_start$", codes.get(3).toString()); // <4/>
		
		tf = new TextFragment();
		res = TextUnitUtil.extractSegMarkers(tf, st, true);
		
		assertEquals("<1/><2/><3/><4/>", fmt.setContent(tf).toString());
		assertEquals("<x31/><x32/><x33/><x34/>", res);
		
		codes = tf.getCodes();
		assertEquals(4, codes.size());
		
		assertEquals("$tp_end$", codes.get(0).toString()); // <1/>
		assertEquals("[#$s3@%$seg_start$]", codes.get(1).toString()); // <2/>
		assertEquals("[#$s3@%$seg_end$]", codes.get(2).toString()); // <3/>
		assertEquals("$tp_start$", codes.get(3).toString()); // <4/>
	}
	
	@Test
	public void testRestoreSegmentation4 () {		
		TextFragment tf1 = new TextFragment();		
		tf1.append(TagType.PLACEHOLDER, "x11", "<x11/>");
		tf1.append(TagType.PLACEHOLDER, "x12", "<x12/>");
		tf1.append(TagType.PLACEHOLDER, "x13", "<x13/>");
		tf1.append(TagType.PLACEHOLDER, "x14", "<x14/>");
		
		TextFragment tf2 = new TextFragment();		
		tf2.append(TagType.PLACEHOLDER, "x21", "<x21/>");
		tf2.append(TagType.PLACEHOLDER, "x22", "<x22/>");
		tf2.append(TagType.PLACEHOLDER, "x23", "<x23/>");
		tf2.append(TagType.PLACEHOLDER, "x24", "<x24/>");
		
		TextFragment tf3 = new TextFragment();		
		tf3.append(TagType.PLACEHOLDER, "x31", "<x31/>");
		tf3.append(TagType.PLACEHOLDER, "x32", "<x32/>");
		tf3.append(TagType.PLACEHOLDER, "x33", "<x33/>");
		tf3.append(TagType.PLACEHOLDER, "x34", "<x34/>");
				
		assertEquals("<1/><2/><3/><4/>", fmt.setContent(tf1).toString());		
		
		TextContainer tc = new TextContainer();
		tc.append(new Segment("s1", tf1));
		tc.append(new TextPart("[text part 1]"));
		tc.append(new Segment("s2", tf2));
		tc.append(new TextPart("[text part 2]"));
		tc.append(new TextPart("[text part 3]"));
		tc.append(new Segment("s3", tf3));
		tc.append(new TextPart("[text part 4]"));
		
//		String saveTc = tc.toString();
		
		TextFragment tf = TextUnitUtil.storeSegmentation(tc);
		assertEquals("<1/><1/><2/><3/><4/><2/><3/>[text part 1]<4/><5/><1/><2/><3/><4/><6/><7/>[text part 2]<8/>" +
				"<9/>[text part 3]<10/><11/><1/><2/><3/><4/><12/><13/>[text part 4]<14/>", 
				fmt.setContent(tf).toString());

		TextUnitUtil.restoreSegmentation(tc, tf);
		assertEquals("<x11/><x12/><x13/><x14/>[text part 1]<x21/><x22/><x23/><x24/>[text part 2]" +
				"[text part 3]<x31/><x32/><x33/><x34/>[text part 4]", tc.toString());
		
	}
	
	@Test
	public void testRestoreSegmentation5 () {
		TextFragment tf1 = new TextFragment();		
		tf1.append(TagType.PLACEHOLDER, "x1", "[#$0@%$seg_start$]");
		tf1.append("T1");
		tf1.append(TagType.PLACEHOLDER, "x2", "[#$0@%$seg_end$]$tp_start$ $tp_end$[#$1@%$seg_start$]<br /> <br />");
		tf1.append(" T2");
		tf1.append(TagType.PLACEHOLDER, "x3", "[#$1@%$seg_end$]");
		
		TextContainer tc = new TextContainer();
		TextUnitUtil.restoreSegmentation(tc, tf1);
		assertEquals("T1 <br /> <br /> T2", tc.toString());
		assertEquals(4, tc.count());
		Segment s1 = tc.getSegments().get(0);
		Segment s2 = tc.getSegments().get(1);
		assertEquals("T1", s1.getContent().toText());
		assertEquals(" T2", s2.getContent().toText());
		
		Iterator<TextPart> it = tc.iterator();		
		TextPart part = null;
		if (it.hasNext()) {
			part = it.next();
			assertTrue(part.isSegment());
			assertEquals("T1", part.toString());
			assertEquals("0", ((Segment) part).getId());
		}
		
		if (it.hasNext()) {
			part = it.next();
			assertFalse(part.isSegment());
			assertEquals(" ", part.toString());
		}
		
		if (it.hasNext()) {
			part = it.next();
			assertFalse(part.isSegment());
			assertEquals("<br /> <br />", part.toString());
		}
		
		if (it.hasNext()) {
			part = it.next();
			assertTrue(part.isSegment());
			assertEquals(" T2", part.toString());
			assertEquals("1", ((Segment) part).getId());
		}
	}
	
	@Test
	public void testRestoreSegmentation6 () {
		TextFragment tf1 = new TextFragment();		
		tf1.append(TagType.PLACEHOLDER, "x1", 
				"[#$0@%$seg_start$]   [#$0@%$seg_end$]$tp_start$  $tp_end$$tp_start$ $tp_end$$tp_start$  $tp_end$[#$1@%$seg_start$]");
		tf1.append("T1");
		tf1.append(TagType.PLACEHOLDER, "x2", "[#$1@%$seg_end$]");
		
		TextContainer tc = new TextContainer();
		TextUnitUtil.restoreSegmentation(tc, tf1);
		assertEquals("        T1", tc.toString());
		
		assertEquals(5, tc.count());
		assertEquals(2, tc.getSegments().count());
		
		Segment s1 = tc.getSegments().get(0);
		Segment s2 = tc.getSegments().get(1);
		assertEquals("   ", s1.getContent().toText());
		assertEquals("T1", s2.getContent().toText());
	}
	
	@Test
	public void testRestoreSegmentation7 () {
		TextFragment tf1 = new TextFragment();
		
		tf1.append(TagType.PLACEHOLDER, "x1", 
				"[<br1> <br2> [#$0@%$seg_end$][#$1@%$seg_start$][#$1@%$seg_end$]$tp_start$ T1 $tp_end$$tp_start$ T2 $tp_end$[#$2@%$seg_start$]<br /> <br />]");
		
		TextContainer tc = new TextContainer();
		TextUnitUtil.restoreSegmentation(tc, tf1);
		assertEquals("[<br1> <br2>  T1  T2 <br /> <br />]", tc.toString());
		
		assertEquals(5, tc.count());
		
		TextPart tp1 = tc.get(0);
		assertEquals("[<br1> <br2> ", tp1.getContent().toText()); // non-open segment closed with seg_end at pos 0
		
		TextPart tp2 = tc.get(1);
		assertEquals("", tp2.getContent().toText());
		
		TextPart tp4 = tc.get(2);
		assertEquals(" T1 ", tp4.getContent().toText());
		
		TextPart tp5 = tc.get(3);
		assertEquals(" T2 ", tp5.getContent().toText());
		
		TextPart tp6 = tc.get(4);
		assertEquals("<br /> <br />]", tp6.getContent().toText());
		
		assertEquals(1, tc.getSegments().count());		
		Segment s1 = tc.getSegments().get(0);
		assertEquals("", s1.getContent().toText());
	}
	
//	@Test
//	public void testCreateBilingualTextUnit4() {
//		TextUnit ori = new TextUnit4("id", "Seg1. Seg2");
//		ori.createTarget(LocaleId.ITALIAN, true, IResource.COPY_ALL);
//		TextContainer tc = ori.getSource();
//		tc.createSegment(6, 10);
//		tc.createSegment(0, 5);
//		tc = ori.getTarget(LocaleId.ITALIAN);
//		tc.createSegment(6, 10);
//		tc.createSegment(0, 5);
//
//		TextUnit res = TextUnitUtil.createBilingualTextUnit4(ori, ori.getSource().getSegment(0), ori
//				.getTarget(LocaleId.ITALIAN).getSegment(0), LocaleId.ITALIAN);
//		assertEquals(ori.getId(), res.getId());
//		assertEquals("Seg1.", res.getSource().toString());
//		assertEquals("Seg1.", res.getTarget(LocaleId.ITALIAN).toString());
//	}

//	@Test
//	public void testCreateBilingualTextUnitNM() {
//		ArrayList<Segment> srcSegs = new ArrayList<Segment>();
//		srcSegs.add(new Segment("1", new TextFragment("sSeg1.")));
//		srcSegs.add(new Segment("2", new TextFragment("sSeg2.")));
//		ArrayList<Segment> trgSegs = new ArrayList<Segment>();
//		trgSegs.add(new Segment("2", new TextFragment("tSeg2.")));
//		trgSegs.add(new Segment("1", new TextFragment("tSeg1.")));
//		trgSegs.add(new Segment("3", new TextFragment("tSeg3.")));
//		TextUnit ori = new TextUnit4("id", "text");
//		ori.createTarget(LocaleId.ARABIC, true, IResource.COPY_ALL);
//
//		TextUnit res = TextUnitUtil.createBilingualTextUnit4(ori, srcSegs, trgSegs,
//				LocaleId.ITALIAN, "__");
//
//		assertEquals(ori.getId(), res.getId());
//		assertEquals("[sSeg1.]__[sSeg2.]", fmt.printSegmentedContent(res.getSource(), true));
//		assertEquals("[tSeg2.]__[tSeg1.]__[tSeg3.]", fmt.printSegmentedContent(res
//				.getTarget(LocaleId.ITALIAN), true));
//
//		assertEquals(2, res.getSource().getSegmentCount());
//		assertEquals("sSeg1.", res.getSource().getSegment(0).toString());
//
//		assertEquals(3, res.getTarget(LocaleId.ITALIAN).getSegmentCount());
//		Segment seg = res.getTarget(LocaleId.ITALIAN).getSegment(0);
//		assertEquals("tSeg2.", seg.toString());
//		assertEquals("2", seg.id);
//	}


	@Test
	public void testTrimSegments() {
		TextFragment tf1 = new TextFragment();		
		tf1.append(" [seg 1]");
		
		TextFragment tf2 = new TextFragment();		
		tf2.append("  [seg 2]   ");
		
		TextFragment tf3 = new TextFragment();		
		tf3.append("    [seg 3]     ");
		
		TextFragment tf4 = new TextFragment();		
		tf4.append("[seg 4]");
		
		TextContainer tc = new TextContainer();
		tc.append(new Segment("s1", tf1));
		tc.append(new Segment("s2", tf2));
		tc.append(new Segment("s3", tf3));
		tc.append(new Segment("s4", tf4));
		
		assertEquals(" [seg 1]  [seg 2]       [seg 3]     [seg 4]", tc.toString());
		assertEquals(4, tc.count());
		
		assertTrue(tc.get(0).isSegment());
		assertTrue(tc.get(1).isSegment());
		assertTrue(tc.get(2).isSegment());
		assertTrue(tc.get(3).isSegment());
		
		assertEquals(" [seg 1]", tc.get(0).toString());
		assertEquals("  [seg 2]   ", tc.get(1).toString());
		assertEquals("    [seg 3]     ", tc.get(2).toString());
		assertEquals("[seg 4]", tc.get(3).toString());
		
		TextUnitUtil.trimSegments(tc);
		assertEquals(9, tc.count());
		
		assertTrue(!tc.get(0).isSegment());
		assertTrue(tc.get(1).isSegment());
		assertTrue(!tc.get(2).isSegment());
		assertTrue(tc.get(3).isSegment());
		assertTrue(!tc.get(4).isSegment());
		assertTrue(!tc.get(5).isSegment());
		assertTrue(tc.get(6).isSegment());
		assertTrue(!tc.get(7).isSegment());
		assertTrue(tc.get(8).isSegment());
		
		assertEquals(" ", tc.get(0).toString());
		assertEquals("[seg 1]", tc.get(1).toString());
		assertEquals("  ", tc.get(2).toString());
		assertEquals("[seg 2]", tc.get(3).toString());
		assertEquals("   ", tc.get(4).toString());
		assertEquals("    ", tc.get(5).toString());
		assertEquals("[seg 3]", tc.get(6).toString());
		assertEquals("     ", tc.get(7).toString());
		assertEquals("[seg 4]", tc.get(8).toString());
	}
	
	@Test
	public void testTrimSegments_leading() {
		TextFragment tf1 = new TextFragment();		
		tf1.append(" [seg 1]");
		
		TextFragment tf2 = new TextFragment();		
		tf2.append("  [seg 2]   ");
		
		TextFragment tf3 = new TextFragment();		
		tf3.append("    [seg 3]     ");
		
		TextFragment tf4 = new TextFragment();		
		tf4.append("[seg 4]");
		
		TextContainer tc = new TextContainer();
		tc.append(new Segment("s1", tf1));
		tc.append(new Segment("s2", tf2));
		tc.append(new Segment("s3", tf3));
		tc.append(new Segment("s4", tf4));
		
		assertEquals(" [seg 1]  [seg 2]       [seg 3]     [seg 4]", tc.toString());
		assertEquals(4, tc.count());
		
		assertTrue(tc.get(0).isSegment());
		assertTrue(tc.get(1).isSegment());
		assertTrue(tc.get(2).isSegment());
		assertTrue(tc.get(3).isSegment());
		
		assertEquals(" [seg 1]", tc.get(0).toString());
		assertEquals("  [seg 2]   ", tc.get(1).toString());
		assertEquals("    [seg 3]     ", tc.get(2).toString());
		assertEquals("[seg 4]", tc.get(3).toString());
		
		TextUnitUtil.trimSegments(tc, true, false);
		assertEquals(7, tc.count());
		
		assertTrue(!tc.get(0).isSegment());
		assertTrue(tc.get(1).isSegment());
		assertTrue(!tc.get(2).isSegment());
		assertTrue(tc.get(3).isSegment());
		assertTrue(!tc.get(4).isSegment());
		assertTrue(tc.get(5).isSegment());
		assertTrue(tc.get(6).isSegment());
		
		assertEquals(" ", tc.get(0).toString());
		assertEquals("[seg 1]", tc.get(1).toString());
		assertEquals("  ", tc.get(2).toString());
		assertEquals("[seg 2]   ", tc.get(3).toString());
		assertEquals("    ", tc.get(4).toString());
		assertEquals("[seg 3]     ", tc.get(5).toString());
		assertEquals("[seg 4]", tc.get(6).toString());
	}
	
	@Test
	public void testTrimSegments_trailing() {
		TextFragment tf1 = new TextFragment();		
		tf1.append(" [seg 1]");
		
		TextFragment tf2 = new TextFragment();		
		tf2.append("  [seg 2]   ");
		
		TextFragment tf3 = new TextFragment();		
		tf3.append("    [seg 3]     ");
		
		TextFragment tf4 = new TextFragment();		
		tf4.append("[seg 4]");
		
		TextContainer tc = new TextContainer();
		tc.append(new Segment("s1", tf1));
		tc.append(new Segment("s2", tf2));
		tc.append(new Segment("s3", tf3));
		tc.append(new Segment("s4", tf4));
		
		assertEquals(" [seg 1]  [seg 2]       [seg 3]     [seg 4]", tc.toString());
		assertEquals(4, tc.count());
		
		assertTrue(tc.get(0).isSegment());
		assertTrue(tc.get(1).isSegment());
		assertTrue(tc.get(2).isSegment());
		assertTrue(tc.get(3).isSegment());
		
		assertEquals(" [seg 1]", tc.get(0).toString());
		assertEquals("  [seg 2]   ", tc.get(1).toString());
		assertEquals("    [seg 3]     ", tc.get(2).toString());
		assertEquals("[seg 4]", tc.get(3).toString());
		
		TextUnitUtil.trimSegments(tc, false, true);
		
		assertEquals(6, tc.count());
		
		assertTrue(tc.get(0).isSegment());
		assertTrue(tc.get(1).isSegment());
		assertTrue(!tc.get(2).isSegment());
		assertTrue(tc.get(3).isSegment());
		assertTrue(!tc.get(4).isSegment());
		assertTrue(tc.get(5).isSegment());
		
		assertEquals(" [seg 1]", tc.get(0).toString());
		assertEquals("  [seg 2]", tc.get(1).toString());
		assertEquals("   ", tc.get(2).toString());
		assertEquals("    [seg 3]", tc.get(3).toString());
		assertEquals("     ", tc.get(4).toString());
		assertEquals("[seg 4]", tc.get(5).toString());
	}
	
	/**
	 * Makes a fragment <code>[b]A[br/]B[/b]C<code>
	 * @return the new fragment.
	 */
	private TextFragment makeFragment1 () {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "b", "[b]");
		tf.append("A");
		tf.append(TagType.PLACEHOLDER, "br", "[br/]");
		tf.append("B");
		tf.append(TagType.CLOSING, "b", "[/b]");
		tf.append("C");
		return tf;
	}

	/**
	 * Makes a fragment <code>{B}A{/B}B{BR/}C extra<code>
	 * @return the new fragment.
	 */
	private TextFragment makeFragment1Bis (String extra) {
		TextFragment tf = new TextFragment();
		tf.append(TagType.OPENING, "b", "{B}");
		tf.append("A");
		tf.append(TagType.CLOSING, "b", "{/B}");
		tf.append("B");
		tf.append(TagType.PLACEHOLDER, "br", "{BR/}");
		tf.append("C "+extra);
		return tf;
	}

	private ITextUnit createTextUnit1 () {
		ITextUnit tu = new TextUnit("1", "t ");
		TextFragment tf = tu.getSource().getSegments().getFirstContent();
		tf.append(TagType.OPENING, "b", "<b>");
		tf.append("bold");
		tf.append(TagType.CLOSING, "b", "</b>");
		tf.append(" t ");
		tf.append(TagType.PLACEHOLDER, "br", "<br/>");
		return tu;
	}

}

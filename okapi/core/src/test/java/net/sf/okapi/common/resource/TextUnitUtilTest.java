/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
		assertEquals("1234   ", tf.toString());
		TextUnitUtil.trimTrailing(tf, null);
		assertEquals("1234", tf.toString());

		st = "     ";
		tf = new TextFragment(st);
		TextUnitUtil.trimLeading(tf, null);
		assertEquals("", tf.toString());
		TextUnitUtil.trimTrailing(tf, null);
		assertEquals("", tf.toString());

		st = "     ";
		tf = new TextFragment(st);
		TextUnitUtil.trimTrailing(tf, null);
		assertEquals("", tf.toString());

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
		tcc.append(c2);
		tcc.append("    123456  ");

		GenericSkeleton skel = new GenericSkeleton();
		TextUnitUtil.trimLeading(tcc, skel);

		assertEquals("123456  ", tcc.getCodedText());
		assertEquals("    ", skel.toString());

		// --------------------
		TextFragment tcc2 = new TextFragment("    123456  ");
		Code c3 = new Code(TagType.PLACEHOLDER, "code");
		tcc2.append(c3);

		GenericSkeleton skel2 = new GenericSkeleton();
		TextUnitUtil.trimTrailing(tcc2, skel2);

		assertEquals("    123456", tcc2.getCodedText());
		assertEquals("  ", skel2.toString());

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

		StringBuilder sb = new StringBuilder();

		sb.append("ab");
		sb.append((char) TextFragment.MARKER_OPENING);
		sb.append((char) (TextFragment.CHARBASE + 1));
		sb.append("cde");
		sb.append((char) TextFragment.MARKER_ISOLATED);
		sb.append((char) (TextFragment.CHARBASE + 2));
		sb.append("fgh");
		sb.append((char) TextFragment.MARKER_ISOLATED);
		sb.append((char) (TextFragment.CHARBASE + 3));
		sb.append("ijklm");
		sb.append((char) TextFragment.MARKER_CLOSING);
		sb.append((char) (TextFragment.CHARBASE + 4));

		String st = sb.toString();

		assertEquals("abcdefghijklm", TextUnitUtil.getText(new TextFragment(st)));

		ArrayList<Integer> positions = new ArrayList<Integer>();
		assertEquals("abcdefghijklm", TextUnitUtil.getText(new TextFragment(st), positions));

		assertEquals(4, positions.size());

		assertEquals(2, (int) positions.get(0));
		assertEquals(7, (int) positions.get(1));
		assertEquals(12, (int) positions.get(2));
		assertEquals(19, (int) positions.get(3));

		sb = new StringBuilder();

		sb.append("ab");
		sb.append((char) TextFragment.MARKER_OPENING);
		sb.append((char) (TextFragment.CHARBASE + 1));
		sb.append("cde");
		sb.append((char) TextFragment.MARKER_ISOLATED);
		sb.append((char) (TextFragment.CHARBASE + 2));
		sb.append("fgh");
		sb.append((char) TextFragment.MARKER_ISOLATED);
		sb.append((char) (TextFragment.CHARBASE + 3));
		sb.append("ijklm");
		sb.append((char) TextFragment.MARKER_CLOSING);
		sb.append((char) (TextFragment.CHARBASE + 4));
		sb.append("n");

		st = sb.toString();

		assertEquals("abcdefghijklmn", TextUnitUtil.getText(new TextFragment(st)));

		positions = new ArrayList<Integer>();
		assertEquals("abcdefghijklmn", TextUnitUtil.getText(new TextFragment(st), positions));

		assertEquals(4, positions.size());

		assertEquals(2, (int) positions.get(0));
		assertEquals(7, (int) positions.get(1));
		assertEquals(12, (int) positions.get(2));
		assertEquals(19, (int) positions.get(3));

		st = "abcdefghijklmn";
		assertEquals(st, TextUnitUtil.getText(new TextFragment(st)));
	}

	@Test
	public void testRemoveQualifiers() {

		TextUnit tu = TextUnitUtil.buildTU("\"qualified text\"");
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

//	@Test
//	public void testCreateBilingualTextUnit() {
//		TextUnit ori = new TextUnit("id", "Seg1. Seg2");
//		ori.createTarget(LocaleId.ITALIAN, true, IResource.COPY_ALL);
//		TextContainer tc = ori.getSource();
//		tc.createSegment(6, 10);
//		tc.createSegment(0, 5);
//		tc = ori.getTarget(LocaleId.ITALIAN);
//		tc.createSegment(6, 10);
//		tc.createSegment(0, 5);
//
//		TextUnit res = TextUnitUtil.createBilingualTextUnit(ori, ori.getSource().getSegment(0), ori
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
//		TextUnit ori = new TextUnit("id", "text");
//		ori.createTarget(LocaleId.ARABIC, true, IResource.COPY_ALL);
//
//		TextUnit res = TextUnitUtil.createBilingualTextUnit(ori, srcSegs, trgSegs,
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
	public void createMultilingualTextUnit() {
		TextUnit otu = new TextUnit("id1");
		List<TextPart> sourceParts = new LinkedList<TextPart>();
		List<TextPart> targetParts = new LinkedList<TextPart>();

		sourceParts.add(new Segment("id", new TextFragment("sSentence one.")));
		sourceParts.add(new TextPart(" "));
		sourceParts.add(new Segment("id", new TextFragment("sSentence two.")));
		sourceParts.add(new TextPart(" "));

		targetParts.add(new Segment("id", new TextFragment("tSentence one.")));
		targetParts.add(new TextPart(" "));
		targetParts.add(new Segment("id", new TextFragment("tSentence two.")));
		targetParts.add(new TextPart(" "));
		
		List<AlignedPair> alignedPairs = new LinkedList<AlignedPair>();
		alignedPairs.add(new AlignedPair(sourceParts, targetParts, LocaleId.SPANISH));
		
		TextUnit mtu = TextUnitUtil.createMultilingualTextUnit(otu, alignedPairs, LocaleId.SPANISH);
		
		assertEquals(otu.getId(), mtu.getId());
		assertEquals("[sSentence one. sSentence two.] ", fmt.printSegmentedContent(mtu.getSource(), true));
		assertEquals("[tSentence one. tSentence two.] ", fmt.printSegmentedContent(mtu.getTarget(LocaleId.SPANISH), true));

		assertEquals(1, mtu.getSource().getSegments().count());
		assertEquals("sSentence one. sSentence two.", mtu.getSource().getSegment(0).toString());

		assertEquals(1, mtu.getTarget(LocaleId.SPANISH).getSegments().count());
		Segment tseg = mtu.getTarget(LocaleId.SPANISH).getSegment(0);
		Segment sseg = mtu.getSource().getSegment(0);
		assertEquals("tSentence one. tSentence two.", tseg.toString());
				
		assertEquals(sseg.id, tseg.id);
	}
}

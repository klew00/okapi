/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.common.skeleton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.MultiEvent;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;

import org.junit.Test;

public class ResourceSimplifierTest {

	private static final LocaleId ENUS = new LocaleId("en", "us");
	private static final LocaleId ESES = new LocaleId("es", "es");
	private static final LocaleId FRFR = new LocaleId("fr", "fr");
	
	@Test
	public void testMonolingual() {
		ResourceSimplifier conv = new ResourceSimplifier("UTF-8", ESES);
		
		ITextUnit tu1 = new TextUnit("tu1");
		tu1.setSource(new TextContainer("text1"));
		GenericSkeleton skel1 = new GenericSkeleton();
		tu1.setSkeleton(skel1);
		skel1.add("title='");
		skel1.addContentPlaceholder(tu1);
		skel1.add("'");
		
		ITextUnit tu2 = new TextUnit("tu2");
		tu2.setSource(new TextContainer("text2"));
		GenericSkeleton skel2 = new GenericSkeleton();
		tu2.setSkeleton(skel2);
		skel2.add("<p ");
		skel2.addReference(tu1); 
		tu1.setIsReferent(true); // TODO Automatically call from addReference()
		skel2.add(">");
		skel2.addContentPlaceholder(tu2);
		skel2.add("</p>");
		
		Event e = conv.convert(new Event(EventType.TEXT_UNIT, tu1));
//		assertEquals(EventType.TEXT_UNIT, e.getEventType());
//		assertTrue(tu1.isReferent());
		assertEquals(EventType.NO_OP, e.getEventType());
		
		e = conv.convert(new Event(EventType.TEXT_UNIT, tu2));
		assertEquals(EventType.MULTI_EVENT, e.getEventType());
		MultiEvent packedME = (MultiEvent) e.getResource(); 
		Iterator<Event> itr = packedME.iterator();
		
		assertTrue(itr.hasNext());
		Event event = itr.next();
		assertEquals(EventType.DOCUMENT_PART, event.getEventType());
		IResource res = event.getResource();
		assertEquals("<p title='", res.getSkeleton().toString());
		
		assertTrue(itr.hasNext());
		event = itr.next();
		assertEquals(EventType.TEXT_UNIT, event.getEventType());
		res = event.getResource();
		assertEquals("text1", res.toString());
		
		assertTrue(itr.hasNext());
		event = itr.next();
		assertEquals(EventType.DOCUMENT_PART, event.getEventType());
		res = event.getResource();
		assertEquals("'>", res.getSkeleton().toString());
		
		assertTrue(itr.hasNext());
		event = itr.next();
		assertEquals(EventType.TEXT_UNIT, event.getEventType());
		res = event.getResource();
		assertEquals("text2", res.toString());
		
		assertTrue(itr.hasNext());
		event = itr.next();
		assertEquals(EventType.DOCUMENT_PART, event.getEventType());
		res = event.getResource();
		assertEquals("</p>", res.getSkeleton().toString());
		
		assertFalse(itr.hasNext());
	}
	
	@Test
	public void testPackMultiEvent() {
		// No TU
		MultiEvent me = new MultiEvent();
		GenericSkeleton skel1 = new GenericSkeleton("dp1");
		GenericSkeleton skel2 = new GenericSkeleton("dp2");
		GenericSkeleton skel3 = new GenericSkeleton("dp3");
		GenericSkeleton skel4 = new GenericSkeleton("tu1");
		
		me.addEvent(new Event(EventType.DOCUMENT_PART, new DocumentPart("dp1", false, skel1)));
		me.addEvent(new Event(EventType.DOCUMENT_PART, new DocumentPart("dp2", false, skel2)));
		me.addEvent(new Event(EventType.DOCUMENT_PART, new DocumentPart("dp3", false, skel3)));
		
		MultiEvent packedME = ResourceSimplifier.packMultiEvent(me);
		
		Iterator<Event> itr = packedME.iterator(); 
		assertTrue(itr.hasNext());
		Event event = itr.next();
		assertTrue(event.getEventType() == EventType.DOCUMENT_PART);
		IResource res = event.getResource();
		assertEquals("dp1dp2dp3", res.getSkeleton().toString());
		assertFalse(itr.hasNext());
		
		// TU in the middle
		me = new MultiEvent();
		skel1 = new GenericSkeleton("dp1");
		skel2 = new GenericSkeleton("dp2");
		skel3 = new GenericSkeleton("dp3");
		skel4 = new GenericSkeleton("tu1");
		
		me.addEvent(new Event(EventType.DOCUMENT_PART, new DocumentPart("dp1", false, skel1)));
		me.addEvent(new Event(EventType.DOCUMENT_PART, new DocumentPart("dp2", false, skel2)));
		ITextUnit tu1 = new TextUnit("tu1");
		tu1.setSkeleton(skel4);
		me.addEvent(new Event(EventType.TEXT_UNIT, tu1));
		me.addEvent(new Event(EventType.DOCUMENT_PART, new DocumentPart("dp3", false, skel3)));
		
		packedME = ResourceSimplifier.packMultiEvent(me);
		itr = packedME.iterator();
		assertTrue(itr.hasNext());
		event = itr.next(); 
		res = event.getResource();
		assertEquals("dp1dp2", res.getSkeleton().toString());
		assertTrue(itr.hasNext());
		event = itr.next();
		res = event.getResource();
		assertEquals("tu1", res.getSkeleton().toString());
		assertTrue(itr.hasNext());
		event = itr.next();
		res = event.getResource();
		assertEquals("dp3", res.getSkeleton().toString());
		assertFalse(itr.hasNext());
		
		// TU at head
		me = new MultiEvent();
		skel1 = new GenericSkeleton("dp1");
		skel2 = new GenericSkeleton("dp2");
		skel3 = new GenericSkeleton("dp3");
		skel4 = new GenericSkeleton("tu1");
		
		tu1 = new TextUnit("tu1");
		tu1.setSkeleton(skel4);
		me.addEvent(new Event(EventType.TEXT_UNIT, tu1));
		me.addEvent(new Event(EventType.DOCUMENT_PART, new DocumentPart("dp1", false, skel1)));
		me.addEvent(new Event(EventType.DOCUMENT_PART, new DocumentPart("dp2", false, skel2)));		
		me.addEvent(new Event(EventType.DOCUMENT_PART, new DocumentPart("dp3", false, skel3)));
		
		packedME = ResourceSimplifier.packMultiEvent(me);
		itr = packedME.iterator();
		assertTrue(itr.hasNext());
		event = itr.next(); 
		res = event.getResource();
		assertEquals("tu1", res.getSkeleton().toString());		
		assertTrue(itr.hasNext());
		event = itr.next();
		res = event.getResource();
		assertEquals("dp1dp2dp3", res.getSkeleton().toString());
		assertFalse(itr.hasNext());
		
		// TU at tail
		me = new MultiEvent();
		skel1 = new GenericSkeleton("dp1");
		skel2 = new GenericSkeleton("dp2");
		skel3 = new GenericSkeleton("dp3");
		skel4 = new GenericSkeleton("tu1");
		
		tu1 = new TextUnit("tu1");
		tu1.setSkeleton(skel4);		
		me.addEvent(new Event(EventType.DOCUMENT_PART, new DocumentPart("dp1", false, skel1)));
		me.addEvent(new Event(EventType.DOCUMENT_PART, new DocumentPart("dp2", false, skel2)));		
		me.addEvent(new Event(EventType.DOCUMENT_PART, new DocumentPart("dp3", false, skel3)));
		me.addEvent(new Event(EventType.TEXT_UNIT, tu1));
		
		packedME = ResourceSimplifier.packMultiEvent(me);
		itr = packedME.iterator();
		assertTrue(itr.hasNext());
		event = itr.next(); 
		res = event.getResource();
		assertEquals("dp1dp2dp3", res.getSkeleton().toString());			
		assertTrue(itr.hasNext());
		event = itr.next();
		res = event.getResource();
		assertEquals("tu1", res.getSkeleton().toString());
		assertFalse(itr.hasNext());
		
		// No DP
		me = new MultiEvent();
		skel1 = new GenericSkeleton("dp1");
		skel2 = new GenericSkeleton("dp2");
		skel3 = new GenericSkeleton("dp3");
		skel4 = new GenericSkeleton("tu1");
		
		tu1 = new TextUnit("tu1");
		tu1.setSkeleton(skel4);
		me.addEvent(new Event(EventType.TEXT_UNIT, tu1));
		
		packedME = ResourceSimplifier.packMultiEvent(me);
		itr = packedME.iterator();
		assertTrue(itr.hasNext());
		event = itr.next(); 
		res = event.getResource();
		assertEquals("tu1", res.getSkeleton().toString());		
		assertFalse(itr.hasNext());
		
		// Empty
		me = new MultiEvent();		
		packedME = ResourceSimplifier.packMultiEvent(me);
		itr = packedME.iterator();
		assertFalse(itr.hasNext());
	}

	@Test
	public void testConversion() {
		GenericSkeleton skel = new GenericSkeleton();
		ITextUnit tu1 = new TextUnit("tu1");
		tu1.setSource(new TextContainer("English text"));
		tu1.setTarget(FRFR, new TextContainer("Texte en langue Francaise"));
		tu1.setSkeleton(skel);
		
		ITextUnit tu2 = new TextUnit("tu2");
		tu2.setSource(new TextContainer("English text"));
		tu2.setTarget(ESES, new TextContainer("Texto en Espanol"));
		tu2.setIsReferent(true);
		
		skel.add("part1");
		skel.addContentPlaceholder(tu1);
		skel.add("part2");
		skel.addContentPlaceholder(tu1, FRFR);
		skel.add("part3");
		skel.addContentPlaceholder(tu2, ESES);
		skel.add("part4");
		
		ResourceSimplifier rs = new ResourceSimplifier(ENUS);
		Event event = rs.convert(new Event(EventType.TEXT_UNIT, tu1));
		assertNotNull(event);
		assertTrue(event.getResource() instanceof MultiEvent);
		MultiEvent me = (MultiEvent) event.getResource();
		Iterator<Event> itr = me.iterator();
		
		assertTrue(itr.hasNext());
		event = itr.next();
		IResource res = event.getResource();
		assertEquals("dp_tu1", res.getId());
		assertTrue(res instanceof DocumentPart);
		assertEquals("part1", res.getSkeleton().toString());
		
		assertTrue(itr.hasNext());
		event = itr.next();
		res = event.getResource();
		assertEquals("tu1", res.getId());
		assertTrue(res instanceof ITextUnit);
		assertNull(res.getSkeleton());
		
		assertTrue(itr.hasNext());
		event = itr.next();
		res = event.getResource();
		assertEquals("dp_tu1_2", res.getId());
		assertTrue(res instanceof DocumentPart);
		assertEquals("part2Texte en langue Francaisepart3English textpart4", res.getSkeleton().toString());
		
		assertFalse(itr.hasNext());
	}		
	
	@Test
	public void testConversion2() {
		GenericSkeleton skel = new GenericSkeleton();
		ITextUnit tu1 = new TextUnit("tu1");
		tu1.setSource(new TextContainer("English text"));
		tu1.setTarget(FRFR, new TextContainer("Texte en langue Francaise"));
		tu1.setSkeleton(skel);
		
		ITextUnit tu2 = new TextUnit("tu2");
		tu2.setSource(new TextContainer("English text"));
		tu2.setTarget(ESES, new TextContainer("Texto en Espanol"));
		tu2.setIsReferent(true);
		
		skel.add("part1");
		skel.addContentPlaceholder(tu1);
		skel.add("part2");
		skel.addContentPlaceholder(tu1, FRFR);
		skel.add("part3");
		skel.addReference(tu2);
		skel.add("part4");
		
		ResourceSimplifier rs = new ResourceSimplifier(ENUS);
		Event complexEvent = new Event(EventType.TEXT_UNIT, tu1);
		rs.convert(new Event(EventType.TEXT_UNIT, tu2)); // Ref
		Event simpleEvent = rs.convert(complexEvent);
		assertNotNull(simpleEvent);
		assertTrue(simpleEvent.getResource() instanceof MultiEvent);
		MultiEvent me = (MultiEvent) simpleEvent.getResource();
		Iterator<Event> itr = me.iterator();
		
		assertTrue(itr.hasNext());
		Event event = itr.next();
		IResource res = event.getResource();
		assertEquals("dp_tu1", res.getId());
		assertTrue(res instanceof DocumentPart);
		assertEquals("part1", res.getSkeleton().toString());
		
		assertTrue(itr.hasNext());
		event = itr.next();
		res = event.getResource();
		assertEquals("tu1", res.getId());
		assertTrue(res instanceof ITextUnit);
		assertNull(res.getSkeleton());
		
		assertTrue(itr.hasNext());
		event = itr.next();
		res = event.getResource();
		assertEquals("dp_tu1_2", res.getId());
		assertTrue(res instanceof DocumentPart);
		assertEquals("part2Texte en langue Francaisepart3English textpart4", res.getSkeleton().toString());
		
		assertFalse(itr.hasNext());
		
		GenericSkeletonWriter gsw = new GenericSkeletonWriter();
		StartDocument sd = new StartDocument("sd");
		gsw.processStartDocument(ENUS, "UTF-8", null, null, sd);
		gsw.processTextUnit(tu2); // Ref
		checkTUConversion(tu1, simpleEvent, gsw);
	}
	
	private void checkTUConversion(ITextUnit tu, Event simpleEvent, GenericSkeletonWriter gsw) {
		assertNotNull(simpleEvent);
		assertTrue(simpleEvent.getResource() instanceof MultiEvent);
		MultiEvent me = (MultiEvent) simpleEvent.getResource();

		String st1 = gsw.processTextUnit(tu);
		StringBuilder sb = new StringBuilder();
		for (Event event : me) {
			switch (event.getEventType()) {
			case DOCUMENT_PART:
				sb.append(gsw.processDocumentPart((DocumentPart) event.getResource()));
				break;
			case TEXT_UNIT:
				sb.append(gsw.processTextUnit(event.getTextUnit()));
				break;
			}			
		}		
		assertEquals(st1, sb.toString());
	}
	
	private ZipSkeleton createZipSkeleton() throws IOException {
		File tempZip = File.createTempFile("~temp.zip", null);
		tempZip.deleteOnExit();
		
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(tempZip));
		zos.putNextEntry(new ZipEntry("test"));
		zos.close();
		
		return new ZipSkeleton(new ZipFile(tempZip), new ZipEntry("test"));
	}
	
	@Test // + No refs, the original DP is returned
	public void testDp_GenericSkeleton() {
		DocumentPart dp1 = new DocumentPart("P1C1D3-dp1", true);
		GenericSkeleton skel = new GenericSkeleton();
		dp1.setSkeleton(skel);
		
		ResourceSimplifier rs = new ResourceSimplifier(ENUS);
		Event event = rs.convert(new Event(EventType.DOCUMENT_PART, dp1));
		assertNotNull(event);
		assertEquals(EventType.NO_OP, event.getEventType());
		assertNull(event.getResource());
	}
		
	@Test // + No refs, the original DP is returned
	public void testDp_NonGenericSkeleton() throws IOException {
		DocumentPart dp1 = new DocumentPart("P1C1D3-dp1", true);				
		dp1.setSkeleton(createZipSkeleton());
		
		ResourceSimplifier rs = new ResourceSimplifier(ENUS);
		Event event = rs.convert(new Event(EventType.DOCUMENT_PART, dp1));
		assertNotNull(event);
		assertEquals(EventType.NO_OP, event.getEventType());
		assertNull(event.getResource());
	}
	
	@Test // No refs, the original TU is returned
	public void testTu_NoSkeleton() {
		ITextUnit tu1 = new TextUnit("P1C1D3-tu1");
		tu1.setSource(new TextContainer("Source"));
		
		ResourceSimplifier rs = new ResourceSimplifier(ENUS);
		Event event = rs.convert(new Event(EventType.TEXT_UNIT, tu1));
		assertNotNull(event);
		assertTrue(event.getResource() instanceof ITextUnit);
	}
	
	@Test // No refs, the original TU is returned
	public void testTu_GenericSkeleton() {
		ITextUnit tu1 = new TextUnit("P1C1D3-tu1");
		tu1.setSource(new TextContainer("Source"));
		
		GenericSkeleton skel2 = new GenericSkeleton();
		tu1.setSkeleton(skel2);
		skel2.add("Prefix");
		skel2.addContentPlaceholder(tu1);
		skel2.add("Suffix");
		
		ResourceSimplifier rs = new ResourceSimplifier(ENUS);
		Event event = rs.convert(new Event(EventType.TEXT_UNIT, tu1));
		assertNotNull(event);
		assertTrue(event.getResource() instanceof MultiEvent);
		MultiEvent me = (MultiEvent) event.getResource();
		assertEquals(3, me.size());
		Iterator<Event> itr = me.iterator();		
		Event ev;
		
		ev = itr.next();
		assertTrue(ev.getResource() instanceof DocumentPart);
		assertEquals("Prefix", ev.getResource().toString());
		
		ev = itr.next();
		assertTrue(ev.getResource() instanceof ITextUnit);
		assertEquals("Source", ev.getResource().toString());
		
		ev = itr.next();
		assertTrue(ev.getResource() instanceof DocumentPart);
		assertEquals("Suffix", ev.getResource().toString());
	}
	
	@Test // No refs, the original TU is returned
	public void testTu_NonGenericSkeleton() throws IOException {
		ITextUnit tu1 = new TextUnit("P1C1D3-tu1");
		tu1.setSource(new TextContainer("Source"));		
		tu1.setSkeleton(createZipSkeleton());
		
		ResourceSimplifier rs = new ResourceSimplifier(ENUS);
		Event event = rs.convert(new Event(EventType.TEXT_UNIT, tu1));
		assertNotNull(event);
		assertTrue(event.getResource() instanceof ITextUnit);
	}
	
	@Test
	public void testReferencesInTuCodes_NoSkeleton() {
		DocumentPart dp1 = new DocumentPart("P1C1D3-dp1", true);
		GenericSkeleton skel = new GenericSkeleton();
		dp1.setSkeleton(skel);
		skel.add("<a id=\"jsHelplink\" ");
		skel.add("href=\"");
		skel.addValuePlaceholder(dp1, "href", null);
		skel.add("\"");
		skel.add(">");
		dp1.setSourceProperty(new Property("href", "{0}"));
		
		ITextUnit tu1 = new TextUnit("P1C1D3-tu1");
		TextContainer tc = new TextContainer();
		tu1.setSource(tc);
		TextFragment tf = new TextFragment();
		tc.setContent(tf);
		tf.append(TagType.OPENING, Code.TYPE_BOLD, "<b>", 1);
		tf.append("Javascript is not enabled on your internet browser.");
		tf.append(TagType.CLOSING, Code.TYPE_BOLD, "</b>", 1);
		tf.append(TagType.PLACEHOLDER, "br", "<br/>", 2);
		tf.append(TagType.PLACEHOLDER, "br", "<br/>", 3);
		tf.append("FamilySearch Indexing requires Javascript to operate properly.  Please click");
		tf.append(TagType.OPENING, Code.TYPE_LINK, "", 4).appendReference(dp1.getId());
		tf.append("here ");
		tf.append(TagType.CLOSING, Code.TYPE_LINK, "</a>", 4);
		tf.append(" for instructions on enabling this browser feature.");
		
		ResourceSimplifier rs = new ResourceSimplifier(ENUS);
		rs.convert(new Event(EventType.DOCUMENT_PART, dp1)); // To register the referent
		Event event = rs.convert(new Event(EventType.TEXT_UNIT, tu1));
		assertNotNull(event);
		assertTrue(event.getResource() instanceof ITextUnit);
		ITextUnit tu = event.getTextUnit();
		assertEquals("<b>Javascript is not enabled on your internet browser.</b>" +
				"<br/><br/>FamilySearch Indexing requires Javascript to operate properly.  Please click" +
				"<a id=\"jsHelplink\" href=\"{0}\">here </a> for instructions on enabling this browser feature.", 
				tu.toString());
		assertEquals(6, tu.getSource().getUnSegmentedContentCopy().getCodes().size());
		Code code = tu.getSource().getUnSegmentedContentCopy().getCode(4); // The a-element start tag
		assertEquals(TagType.OPENING, code.getTagType());
	}
	
	@Test
	public void testReferencesInTuCodes_WithSkeleton() {
		DocumentPart dp1 = new DocumentPart("P1C1D3-dp1", true);
		GenericSkeleton skel1 = new GenericSkeleton();
		dp1.setSkeleton(skel1);
		skel1.add("<a id=\"jsHelplink\" ");
		skel1.add("href=\"");
		skel1.addValuePlaceholder(dp1, "href", null);
		skel1.add("\"");
		skel1.add(">");
		dp1.setSourceProperty(new Property("href", "{0}"));
		
		ITextUnit tu1 = new TextUnit("P1C1D3-tu1");
		GenericSkeleton skel2 = new GenericSkeleton();
		tu1.setSkeleton(skel2);
		skel2.add("Prefix");
		skel2.addContentPlaceholder(tu1);
		skel2.add("Suffix");
		
		TextContainer tc = new TextContainer();
		tu1.setSource(tc);
		TextFragment tf = new TextFragment();
		tc.setContent(tf);
		tf.append(TagType.OPENING, Code.TYPE_BOLD, "<b>", 1);
		tf.append("Javascript is not enabled on your internet browser.");
		tf.append(TagType.CLOSING, Code.TYPE_BOLD, "</b>", 1);
		tf.append(TagType.PLACEHOLDER, "br", "<br/>", 2);
		tf.append(TagType.PLACEHOLDER, "br", "<br/>", 3);
		tf.append("FamilySearch Indexing requires Javascript to operate properly.  Please click");
		tf.append(TagType.OPENING, Code.TYPE_LINK, "", 4).appendReference(dp1.getId());
		tf.append("here ");
		tf.append(TagType.CLOSING, Code.TYPE_LINK, "</a>", 4);
		tf.append(" for instructions on enabling this browser feature.");
		
		ResourceSimplifier rs = new ResourceSimplifier(ENUS);
		rs.convert(new Event(EventType.DOCUMENT_PART, dp1)); // To register the referent
		Event event = rs.convert(new Event(EventType.TEXT_UNIT, tu1));
		assertNotNull(event);
		assertTrue(event.getResource() instanceof MultiEvent);
		MultiEvent me = (MultiEvent) event.getResource();
		assertEquals(3, me.size());
		Iterator<Event> itr = me.iterator();		
		Event ev;
		
		ev = itr.next();
		assertTrue(ev.getResource() instanceof DocumentPart);
		assertEquals("Prefix", ev.getResource().toString());
		
		ev = itr.next();
		assertTrue(ev.getResource() instanceof ITextUnit);
		assertEquals("<b>Javascript is not enabled on your internet browser.</b>" +
				"<br/><br/>FamilySearch Indexing requires Javascript to operate properly.  Please click" +
				"<a id=\"jsHelplink\" href=\"{0}\">here </a> for instructions on enabling this browser feature.", 
				ev.getResource().toString());
		
		ev = itr.next();
		assertTrue(ev.getResource() instanceof DocumentPart);
		assertEquals("Suffix", ev.getResource().toString());
	}

	public static String getDpInfo(DocumentPart dp, LocaleId srcLoc) {
		StringBuilder sb = new StringBuilder();
		fillSB(sb, dp, srcLoc);
		return sb.toString();
	}
	
	public static String getTuInfo(ITextUnit tu, LocaleId srcLoc) {
		StringBuilder sb = new StringBuilder();
		fillSB(sb, tu, srcLoc);
		return sb.toString();
	}
	
	private static void fillSB(StringBuilder sb, DocumentPart dp, LocaleId srcLoc) {
		sb.append(dp.getId());		
		sb.append(":");
		if (dp.isReferent()) sb.append(" referent");
		sb.append("\n");
		
		if (dp.getAnnotations() != null) {
//			sb.append("             ");
//			sb.append("Source annotations:");
//			sb.append("\n");
			for (IAnnotation annot : dp.getAnnotations()) {
				sb.append("                    ");
				sb.append(annot.getClass().getName());
				sb.append(" ");
				sb.append(annot.toString());
				sb.append("\n");
			}		
		}
		
		if (dp.getPropertyNames() != null && dp.getPropertyNames().size() > 0) {
			sb.append("             ");
			sb.append("Properties:");
			sb.append("\n");
			for (String name : dp.getPropertyNames()) {
				sb.append("                    ");
				sb.append(name);
				sb.append(" ");
				sb.append(dp.getProperty(name).toString());
				sb.append("\n");
			}		
		}
		
		if (dp.getSourcePropertyNames() != null && dp.getSourcePropertyNames().size() > 0) {
			sb.append("             ");
			sb.append("Source properties:");
			sb.append("\n");
			
			for (String name : dp.getSourcePropertyNames()) {
				sb.append("                    ");
				sb.append(name);
				sb.append(" ");
				sb.append(dp.getSourceProperty(name).toString());
				sb.append("\n");
			}		
		}
				
		for (LocaleId locId : dp.getTargetLocales()) {
			if (dp.getTargetPropertyNames(locId) != null && dp.getTargetPropertyNames(locId).size() > 0) {
				sb.append("             ");
				sb.append("Target properties:");
				sb.append("\n");
				
				for (String name : dp.getTargetPropertyNames(locId)) {
					sb.append("                    ");
					sb.append(name);
					sb.append(" ");
					sb.append(dp.getTargetProperty(locId, name).toString());
					sb.append("\n");
				}		
			}
		}		
		
		if (dp.getSkeleton() != null) {
			sb.append(String.format("      Skeleton: %s", dp.getSkeleton().toString()));
			sb.append("\n");
		}
	}
	
	private static void fillSB(StringBuilder sb, ITextUnit tu, LocaleId srcLoc) {
		sb.append(tu.getId());
		sb.append(":");
		if (tu.isReferent()) sb.append(" referent");
		sb.append("\n");
		
		if (tu.getAnnotations() != null) {
//			sb.append("             ");
//			sb.append("Source annotations:");
//			sb.append("\n");
			for (IAnnotation annot : tu.getAnnotations()) {
				sb.append("                    ");
				sb.append(annot.getClass().getName());
				sb.append(" ");
				sb.append(annot.toString());
				sb.append("\n");
			}		
		}
		
		if (tu.getSkeleton() != null) {
			sb.append(String.format("      Skeleton: %s", tu.getSkeleton().toString()));
			sb.append("\n");
		}		
		
		sb.append(String.format("      Source (%s): %s", srcLoc, tu.getSource()));
		sb.append("\n");
		
		TextContainer source = tu.getSource();
		if (source.getAnnotations() != null) {
//			sb.append("             ");
//			sb.append("Source annotations:");
//			sb.append("\n");
			for (IAnnotation annot : source.getAnnotations()) {
				sb.append("                    ");
				sb.append(annot.getClass().getName());
				sb.append(" ");
				sb.append(annot.toString());
				sb.append("\n");
			}		
		}
		
		ISegments segs = source.getSegments(); 
		for (Segment seg : segs) {
			sb.append(String.format("         %s: %s", seg.getId(), seg.getContent().toText()));
			sb.append("\n");
			if (seg.getAnnotations() != null) {
//				sb.append("Source annotations:");
//				sb.append("\n");
				for (IAnnotation annot : seg.getAnnotations()) {
					sb.append("                    ");
					sb.append(annot.getClass().getName());
					sb.append(" ");
					sb.append(annot.toString());
					sb.append("\n");
				}		
			}
		}
		
		for (LocaleId locId : tu.getTargetLocales()) {
			sb.append(String.format("      Target (%s): %s", locId.toString(), tu.getTarget(locId)));
			sb.append("\n");
			
			TextContainer target = tu.getTarget(locId);
			if (source.getAnnotations() != null) {
//				sb.append("             ");
//				sb.append("Target annotations:");
//				sb.append("\n");
				for (IAnnotation annot : target.getAnnotations()) {
					sb.append("                    ");
					sb.append(annot.getClass().getName());
					sb.append(" ");
					sb.append(annot.toString());
					sb.append("\n");
				}		
			}
			
			segs = target.getSegments(); 
			for (Segment seg : segs) {
				sb.append(String.format("         %s: %s", seg.getId(), seg.getContent().toText()));
				sb.append("\n");
				if (seg.getAnnotations() != null) {
//					sb.append("Target annotations:");
//					sb.append("\n");
					for (IAnnotation annot : seg.getAnnotations()) {
						sb.append("                    ");
						sb.append(annot.getClass().getName());
						sb.append(" ");
						sb.append(annot.toString());
						sb.append("\n");
					}		
				}
			}
		}
	}

	@Test
	public void testAltTextRef() {
		// See events of /okapi-step-common/src/test/resources/net/sf/okapi/steps/common/msg00058.html
		// in /okapi-step-common/src/test/resources/net/sf/okapi/steps/common/res_simpl1.txt
		
		// dp6
		GenericSkeleton dp6Skel = new GenericSkeleton();		
		DocumentPart dp6 = new DocumentPart("dp6", true, dp6Skel);
		dp6.setSourceProperty(new Property("href", "http://osdir.com/ml/"));
		dp6Skel.add("<a href=\"");
		dp6Skel.addValuePlaceholder(dp6, "href", null);
		dp6Skel.add("\">");
		
		assertEquals("<a href=\"[#$$self$@%href]\">", dp6.getSkeleton().toString());
		assertEquals("http://osdir.com/ml/", dp6.getSourceProperty("href").getValue());
		
		// tu3
		TextUnit tu3 = new TextUnit("tu3", "logo", true);
		GenericSkeleton tu3Skel = new GenericSkeleton();
		tu3Skel.add("alt=\"");
		tu3Skel.addContentPlaceholder(tu3);
		tu3Skel.add("\"");
		tu3.setSkeleton(tu3Skel);
		
		assertEquals("alt=\"[#$$self$]\"", tu3.getSkeleton().toString());
		assertEquals("logo", tu3.toString());
		
		// dp7
		GenericSkeleton dp7Skel = new GenericSkeleton();		
		DocumentPart dp7 = new DocumentPart("dp7", true, dp7Skel);
		dp7.setSourceProperty(new Property("src", "msg00058_files/MLnewosdirlogo.gif"));
		dp7Skel.add("<img src=\"");
		dp7Skel.addValuePlaceholder(dp7, "src", null);
		dp7Skel.add("\" ");
		dp7Skel.addReference(tu3);
		dp7Skel.add(" border=\"0\">");
		
		assertEquals("<img src=\"[#$$self$@%src]\" [#$tu3] border=\"0\">", dp7.getSkeleton().toString());
		assertEquals("msg00058_files/MLnewosdirlogo.gif", dp7.getSourceProperty("src").getValue());
		
		// tu2
		TextUnit tu2 = new TextUnit("tu2");
		GenericSkeleton tu2Skel = new GenericSkeleton();
		tu2.setSkeleton(tu2Skel);
		
		tu2Skel.add("<td valign=\"middle\">");
		tu2Skel.addContentPlaceholder(tu2);
		
		TextFragment tf = new TextFragment();
		Code c1 = new Code(TagType.PLACEHOLDER, null);
		c1.appendReference("dp6");		
		tf.append(c1);
		
		Code c2 = new Code(TagType.PLACEHOLDER, null);
		c2.appendReference("dp7");		
		tf.append(c2);
		
		tf.append("</a>");
		
		TextContainer tc = new TextContainer();
		tc.append(new Segment("0", tf));
		
		tu2.setSource(tc);
		
		assertEquals("<td valign=\"middle\">[#$$self$]", tu2.getSkeleton().toString());
		assertEquals("[#$dp6][#$dp7]</a>", tu2.toString());
		
		// Conversion
		ResourceSimplifier rs = new ResourceSimplifier(ENUS);
		Event event;
		event = rs.convert(new Event(EventType.DOCUMENT_PART, dp6));
		assertNotNull(event);
		assertEquals(EventType.NO_OP, event.getEventType());
		assertNull(event.getResource());
		
		ITextUnit tu;
		DocumentPart dp;
		
		event = rs.convert(new Event(EventType.TEXT_UNIT, tu3));
		assertNotNull(event);
		assertEquals(EventType.NO_OP, event.getEventType());
		assertNull(event.getResource());
		
		event = rs.convert(new Event(EventType.DOCUMENT_PART, dp7));
		assertNotNull(event);
		assertEquals(EventType.NO_OP, event.getEventType());
		assertNull(event.getResource());
		
		event = rs.convert(new Event(EventType.TEXT_UNIT, tu2));
		assertTrue(event.getResource() instanceof MultiEvent);
		MultiEvent me = event.getMultiEvent();
		assertEquals(2, me.size());
		
		Iterator<Event> itr = me.iterator();
		
		dp = itr.next().getDocumentPart();
		assertEquals("dp_tu2", dp.getId());
		assertFalse(dp.isReferent());
		
		
		tu = itr.next().getTextUnit();
		assertEquals("tu2", tu.getId());
		assertFalse(tu.isReferent());
		
//		System.out.println(getDpInfo(dp, ENUS));
//		System.out.println(getTuInfo(tu, ENUS));
		
		assertEquals("<td valign=\"middle\">", dp.getSkeleton().toString());
		assertEquals("<a href=\"http://osdir.com/ml/\"><img src=\"msg00058_files/MLnewosdirlogo.gif\" " +
				"alt=\"logo\" border=\"0\"></a>", tu.toString());
	}
	
}

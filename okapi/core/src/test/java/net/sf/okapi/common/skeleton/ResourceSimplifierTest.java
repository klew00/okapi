/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

import java.util.Iterator;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.MultiEvent;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;

import org.junit.Test;

public class ResourceSimplifierTest {

	private static final LocaleId ENUS = new LocaleId("en", "us");
	private static final LocaleId ESES = new LocaleId("es", "es");
	private static final LocaleId FRFR = new LocaleId("fr", "fr");
	
	@Test
	public void testMonolingual() {
		ResourceSimplifier conv = new ResourceSimplifier(false, ESES);
		
		TextUnit tu1 = new TextUnit("tu1");
		tu1.setSource(new TextContainer("text1"));
		GenericSkeleton skel1 = new GenericSkeleton();
		tu1.setSkeleton(skel1);
		skel1.add("title='");
		skel1.addContentPlaceholder(tu1);
		skel1.add("'");
		
		TextUnit tu2 = new TextUnit("tu2");
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
		assertEquals(EventType.TEXT_UNIT, e.getEventType());
		assertTrue(tu1.isReferent());
		
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
		TextUnit tu1 = new TextUnit("tu1");
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
		TextUnit tu1 = new TextUnit("tu1");
		tu1.setSource(new TextContainer("English text"));
		tu1.setTarget(FRFR, new TextContainer("Texte en langue Francaise"));
		tu1.setSkeleton(skel);
		
		TextUnit tu2 = new TextUnit("tu2");
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
		
		ResourceSimplifier rs = new ResourceSimplifier(false, ENUS);
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
		assertTrue(res instanceof TextUnit);
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
		TextUnit tu1 = new TextUnit("tu1");
		tu1.setSource(new TextContainer("English text"));
		tu1.setTarget(FRFR, new TextContainer("Texte en langue Francaise"));
		tu1.setSkeleton(skel);
		
		TextUnit tu2 = new TextUnit("tu2");
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
		
		ResourceSimplifier rs = new ResourceSimplifier(false, ENUS);
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
		assertTrue(res instanceof TextUnit);
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
	
	private void checkTUConversion(TextUnit tu, Event simpleEvent, GenericSkeletonWriter gsw) {
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
				sb.append(gsw.processTextUnit((TextUnit) event.getResource()));
				break;
			}			
		}		
		assertEquals(st1, sb.toString());
	}
}

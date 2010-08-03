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
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.MultiEvent;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;

import org.junit.Test;

public class ResourceConverterTest {

	private static final LocaleId ENUS = new LocaleId("en", "us");
	private static final LocaleId ESES = new LocaleId("es", "es");
	private static final LocaleId FRFR = new LocaleId("fr", "fr");
	
	@Test
	public void testMonolingual() {
		ResourceConverter conv = new ResourceConverter(false, ESES, "UTF-8");
		
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
		
		MultiEvent packedME = ResourceConverter.packMultiEvent(me);
		
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
		
		packedME = ResourceConverter.packMultiEvent(me);
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
		
		packedME = ResourceConverter.packMultiEvent(me);
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
		
		packedME = ResourceConverter.packMultiEvent(me);
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
		
		packedME = ResourceConverter.packMultiEvent(me);
		itr = packedME.iterator();
		assertTrue(itr.hasNext());
		event = itr.next(); 
		res = event.getResource();
		assertEquals("tu1", res.getSkeleton().toString());		
		assertFalse(itr.hasNext());
		
		// Empty
		me = new MultiEvent();		
		packedME = ResourceConverter.packMultiEvent(me);
		itr = packedME.iterator();
		assertFalse(itr.hasNext());
	}
}

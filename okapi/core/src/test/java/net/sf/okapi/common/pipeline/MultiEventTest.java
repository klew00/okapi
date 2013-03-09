/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.pipeline;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.observer.IObservable;

public class MultiEventTest {

	@Test
	public void pipelineObserverWithMultiEvent() {		
		IPipeline p = new Pipeline();
		
		// add our event observer
		EventObserver o = new EventObserver();
		((IObservable)p).addObserver(o);
		
		p.addStep(new DummyMultiCustomEventStep());
		
		p.startBatch();		
		p.process(new Event(EventType.CUSTOM));				
		p.endBatch();
		
		// test we observed the correct events
		List<Event> el = o.getResult();  
		assertEquals(EventType.START_BATCH, el.remove(0).getEventType());
		assertEquals(EventType.START_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.CUSTOM, el.remove(0).getEventType());
		assertEquals(EventType.CUSTOM, el.remove(0).getEventType());		
		assertEquals(EventType.END_BATCH_ITEM, el.remove(0).getEventType());
		assertEquals(EventType.END_BATCH, el.remove(0).getEventType());
	}
}

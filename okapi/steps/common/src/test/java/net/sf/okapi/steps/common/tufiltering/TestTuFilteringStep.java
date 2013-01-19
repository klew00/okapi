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

package net.sf.okapi.steps.common.tufiltering;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextUnit;

import org.junit.Before;
import org.junit.Test;

public class TestTuFilteringStep {

	private TextUnit tu1; 
	private TextUnit tu2;
	private TextUnit tu3;
	
	private Event tue1;
	private Event tue2;
	private Event tue3;
	
	private Event sbe;
	
	@Before
	public void startUp() {
		tu1 = new TextUnit("tu1");
		tu2 = new TextUnit("tu2");
		tu3 = new TextUnit("tu3");
		
		tue1 = new Event(EventType.TEXT_UNIT, tu1);
		tue2 = new Event(EventType.TEXT_UNIT, tu2);
		tue3 = new Event(EventType.TEXT_UNIT, tu3);
		
		sbe = new Event(EventType.START_BATCH);
	}
	
	@Test
	public void testEmptyParameters() {
		TuFilteringStep tfs = new TuFilteringStep();
		
		try {
			tfs.handleEvent(sbe);
			tfs.handleEvent(tue1);
			tfs.handleEvent(tue2);
			tfs.handleEvent(tue3);
		} catch (Exception e) {
			// Correct behavior is to throw an exception here 
			return;
		}
		fail("Exception should have been thrown");
	}
	
	@Test
	public void testNullParameters() {
		TuFilteringStep tfs = new TuFilteringStep();
		tfs.setParameters(null);
		
		try {
			tfs.handleEvent(sbe);
			tfs.handleEvent(tue1);
			tfs.handleEvent(tue2);
			tfs.handleEvent(tue3);
		} catch (Exception e) {
			// Correct behavior is to throw an exception here 
			return;
		}
		fail("Exception should have been thrown");
	}
	
	@Test
	public void testDefaultFiltering() {
		TuFilteringStep tfs = new TuFilteringStep(new ITextUnitFilter() {
			
			@Override
			public boolean accept(ITextUnit tu) {
				return false;
			}
		});
		
		assertTrue(tu1.isTranslatable());
		assertTrue(tu2.isTranslatable());
		assertTrue(tu3.isTranslatable());
		
		tfs.handleEvent(sbe);
		tfs.handleEvent(tue1);
		tfs.handleEvent(tue2);
		tfs.handleEvent(tue3);
		
		assertTrue(tu1.isTranslatable());
		assertTrue(tu2.isTranslatable());
		assertTrue(tu3.isTranslatable());
	}
	
	@Test
	public void testCustomFiltering() {
		TuFilteringStep tfs = new TuFilteringStep(new ITextUnitFilter() {
			
			@Override
			public boolean accept(ITextUnit tu) {
				return "tu1".equals(tu.getId()) || "tu3".equals(tu.getId());
			}
		});
		
		tfs.handleEvent(sbe);
		tfs.handleEvent(tue1);
		tfs.handleEvent(tue2);
		tfs.handleEvent(tue3);
		
		assertFalse(tu1.isTranslatable());
		assertTrue(tu2.isTranslatable());
		assertFalse(tu3.isTranslatable());
	}
	
	@Test
	public void testCustomFiltering_with_params() {
		TuFilteringStep tfs = new TuFilteringStep();
		Parameters params = (Parameters) tfs.getParameters();
		params.setTuFilterClassName(TestTuFilter.class.getName());
		
		tfs.handleEvent(sbe);
		tfs.handleEvent(tue1);
		tfs.handleEvent(tue2);
		tfs.handleEvent(tue3);
		
		assertTrue(tu1.isTranslatable());
		assertFalse(tu2.isTranslatable());
		assertTrue(tu3.isTranslatable());
	}
}


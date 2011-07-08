/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.wordcount;

import static org.junit.Assert.assertEquals;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.steps.wordcount.common.GMX;
import net.sf.okapi.steps.wordcount.common.MetricsAnnotation;

import org.junit.Test;

public class TestSimpleWordCountStep {

	@Test
	public void testTextUnitCounts() {
		ITextUnit tu = new TextUnit("tu");
		TextContainer tc = tu.getSource();
		ISegments segments = tc.getSegments();
		segments.append(new TextFragment("The number of words in this segment is 9."));
		segments.append(new TextFragment("The number of words in this second segment is 10."));
		segments.append(new TextFragment("And the number of words in this third segment is 11."));
		
		SimpleWordCountStep step = new SimpleWordCountStep();
		step.setSourceLocale(LocaleId.ENGLISH);
		step.setTargetLocale(LocaleId.SPANISH);
		step.handleEvent(new Event(EventType.TEXT_UNIT, tu));
		
		//assertEquals(30, getCount(tu, null));		
	}
	
	private long getCount(ITextUnit tu, LocaleId trgLoc) {
		MetricsAnnotation ma;
		
		if (trgLoc == null) {
			ma = TextUnitUtil.getSourceAnnotation(tu, MetricsAnnotation.class);
		} else {
			ma = TextUnitUtil.getTargetAnnotation(tu, trgLoc, MetricsAnnotation.class);
		}
		return ma.getMetrics().getMetric(GMX.TotalWordCount);
	}
}

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

package net.sf.okapi.steps.wordcount.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnitUtil;

abstract public class BaseCounter {

	private static BaseCounter counter = null;	
	private static final Logger logger = LoggerFactory.getLogger(BaseCounter.class.getName());
	
	abstract protected long doCount(String text, LocaleId language);
	
	protected static long count(Class<? extends BaseCounter> classRef, Object text, LocaleId language) {
	
		if (text == null) return 0L;
		if (Util.isNullOrEmpty(language)) return 0L;
		
		if (text instanceof ITextUnit) {		
			ITextUnit tu = (ITextUnit)text;
			
//			if (tu.hasTarget(language))
//				return count(classRef, tu.getTarget(language), language);
//			else
			// Only words in the source are counted
			return count(classRef, tu.getSource(), language);
		} 
		else if (text instanceof Segment) {
			Segment seg = (Segment) text;
			return count(classRef, seg.getContent(), language);
		}
		else if (text instanceof TextContainer) {
			// This work on segments' content (vs. parts' content)
			TextContainer tc = (TextContainer) text;
			long res = 0;
			for ( Segment seg : tc.getSegments() ) {
				res += count(classRef, seg, language);
			}
			return res;
		}
		else if (text instanceof TextFragment) {			
			TextFragment tf = (TextFragment) text;
			
			return count(classRef, TextUnitUtil.getText(tf), language);
		}
		else if (text instanceof String) {			
			instantiateCounter(classRef);
			if (counter == null) return 0L;
			
			return counter.doCount((String) text, language);
		}
		
		return 0;		
	}

	protected static void instantiateCounter(Class<? extends BaseCounter> classRef) {
		
		if (counter != null) return; // Already instantiated
		
		try {
			counter = (BaseCounter) classRef.newInstance();
			
		} catch (InstantiationException e) {
			
			logger.debug("Counter instantiation failed: " + e.getMessage());
			
		} catch (IllegalAccessException e) {
			
			logger.debug("Counter instantiation failed: " + e.getMessage());
		}
	}

	private static long getValue(MetricsAnnotation ma, String metricName) {
		if (ma == null) return 0;
		
		Metrics m = ma.getMetrics();
		if (m == null) return 0;
		
		return m.getMetric(metricName);
	}
	
	public static long getCount(Segment segment, String metricName) {
		return getValue(segment.getAnnotation(MetricsAnnotation.class), metricName);
	}
	
	public static long getCount(TextContainer tc, String metricName) {
		return getValue(tc.getAnnotation(MetricsAnnotation.class), metricName);
	}
	
	public static long getCount(IResource res, String metricName) {
		return getValue(res.getAnnotation(MetricsAnnotation.class), metricName);
	}
}

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

package net.sf.okapi.lib.beans.v1;

import java.util.Hashtable;

import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceBean;
import net.sf.okapi.steps.wordcount.common.Metrics;
import net.sf.okapi.steps.wordcount.common.MetricsAnnotation;

public class MetricsAnnotationBean extends PersistenceBean<MetricsAnnotation> {

	private Hashtable<String, Long> metrics = new Hashtable<String, Long>();
	
	@Override
	protected MetricsAnnotation createObject(IPersistenceSession session) {
		return new MetricsAnnotation();
	}

	@Override
	protected void fromObject(MetricsAnnotation obj, IPersistenceSession session) {
		Metrics m = obj.getMetrics();
		for (String name : m)
			metrics.put(name, m.getMetric(name));
	}

	@Override
	protected void setObject(MetricsAnnotation obj, IPersistenceSession session) {
		Metrics m = obj.getMetrics();
		m.resetMetrics();
		for (String name : metrics.keySet())
			m.setMetric(name, metrics.get(name));
	}

	public void setMetrics(Hashtable<String, Long> metrics) {
		this.metrics = metrics;
	}

	public Hashtable<String, Long> getMetrics() {
		return metrics;
	}

}

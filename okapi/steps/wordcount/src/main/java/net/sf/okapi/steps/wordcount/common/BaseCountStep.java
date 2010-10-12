/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.IdGenerator;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.extra.steps.AbstractPipelineStep;

/**
 * Base abstract class for different counter steps (word count step, character count step, etc.).
 * 
 * @version 0.1 08.07.2009
 */

public abstract class BaseCountStep extends AbstractPipelineStep {

	private Parameters params;
	private IdGenerator gen = new IdGenerator("ending"); 
	
	private long batchCount;
	private long batchItemCount;
	private long documentCount;
	private long subDocumentCount;
	private long groupCount;
	private long textUnitCount;
	private long segmentCount;
	
	public BaseCountStep() {
		super();
		params = new Parameters();
		setParameters(params);
		setName(getName());
		setDescription(getName());
	}
	
	@Override
	protected void component_init() {
		params = getParameters(Parameters.class);
		
		// Reset counters
		batchCount = 0;
		batchItemCount = 0;
		documentCount = 0;
		subDocumentCount = 0;
		groupCount = 0;
		textUnitCount = 0;
		segmentCount = 0;
	}

	//-------------------------
//	abstract protected long getCount(TextUnit textUnit);
//	abstract protected void saveCount(Metrics metrics, long count);
	
//	abstract protected String getToken();
	abstract public String getName();
	abstract public String getDescription();
	abstract protected String getMetric();
	abstract protected long count(TextUnit textUnit);
	abstract protected long count(Segment segment);
	abstract protected boolean countOnlyTranslatable();

	protected void saveCount(Metrics metrics, long count) {
		if (metrics == null) return;
		
		metrics.setMetric(getMetric(), count);
	}

	public long getBatchCount() {
		return batchCount;
	}

	public long getBatchItemCount() {
		return batchItemCount;
	}

	public long getDocumentCount() {
		return documentCount;
	}

	public long getSubDocumentCount() {
		return subDocumentCount;
	}

	public long getGroupCount() {
		return groupCount;
	}

	public long getTextUnitCount() {
		return textUnitCount;
	}
	
	public long getSegmentCount() {
		return segmentCount;
	}
	
	protected void saveToMetrics(Event event, long count) {
		if (event == null) return;
		if (count == 0) return;
		
		IResource res = event.getResource();
		if (res == null) {
			res = createResource(event);
		}
		if (res == null) return;
		
		MetricsAnnotation ma = res.getAnnotation(MetricsAnnotation.class);
		
		if (ma == null) {			
			ma = new MetricsAnnotation();
			res.setAnnotation(ma);
		}
		
		if (ma == null) return;
		
		Metrics m = ma.getMetrics();		
		if (m == null) return;
					
		saveCount(m, count);
	}
	
	private IResource createResource(Event event) {
		if (event == null) return null;
		
		IResource res = event.getResource();
		if (res != null) return res;
		
		switch (event.getEventType()) {
		case END_BATCH:
		case END_BATCH_ITEM:
		case END_DOCUMENT:
		case END_SUBDOCUMENT:
		case END_GROUP:
			res = new Ending(gen.createId());
			event.setResource(res);
			break;			
		}
		
		return res;
	}

	protected void saveToMetrics(TextContainer textContainer, long count) {
		if (textContainer == null) return;
		if (count == 0) return;
				
		MetricsAnnotation ma = textContainer.getAnnotation(MetricsAnnotation.class);
		
		if (ma == null) {			
			ma = new MetricsAnnotation();
			textContainer.setAnnotation(ma);
		}
		
		if (ma == null) return;
		
		Metrics m = ma.getMetrics();		
		if (m == null) return;
					
		saveCount(m, count);
	}
	
	protected void saveToMetrics(Segment seg, long count) {		
		if (seg == null) return;
		if (count == 0) return;
		
		MetricsAnnotation ma = seg.getAnnotation(MetricsAnnotation.class);
		
		if (ma == null) {			
			ma = new MetricsAnnotation();
			seg.setAnnotation(ma);
		}
		
		if (ma == null) return;
		
		Metrics m = ma.getMetrics();		
		if (m == null) return;
					
		saveCount(m, count);
	}
	
	//-------------------------	
	@Override
	protected Event handleStartBatch(Event event) {
		batchCount = 0;
		return event;
	}
	
	@Override
	protected Event handleEndBatch(Event event) {
		if (!params.countInBatch) return event;
		if (batchCount == 0) return event;
		
		saveToMetrics(event, batchCount);
		return event;
	}

	//-------------------------	
	@Override
	protected Event handleStartBatchItem(Event event) {
		batchItemCount = 0;
		return event;
	}
	
	@Override
	protected Event handleEndBatchItem(Event event) {
		if (!params.countInBatchItems) return event;
		if (batchItemCount == 0) return event;
		
		saveToMetrics(event, batchItemCount);
		return event;
	}

	//-------------------------	
	@Override
	protected Event handleStartDocument(Event event) {
		documentCount = 0;
		return super.handleStartDocument(event); // Sets language						
	}
	
	@Override
	protected Event handleEndDocument(Event event) {
		if (!params.countInDocuments) return event;
		if (documentCount == 0) return event;
		
		saveToMetrics(event, documentCount);
		return event;
	}

	//-------------------------
	@Override
	protected Event handleStartSubDocument(Event event) {
		subDocumentCount = 0;
		return event;
	}
	
	@Override
	protected Event handleEndSubDocument(Event event) {
		if (!params.countInSubDocuments) return event;
		if (subDocumentCount == 0) return event;
		
		saveToMetrics(event, subDocumentCount);
		return event;
	}
	
	//-------------------------
	@Override
	protected Event handleStartGroup(Event event) {
		groupCount = 0;
		return event;
	}
	
	@Override
	protected Event handleEndGroup(Event event) {		
		if (!params.countInGroups) return event;
		if (groupCount == 0) return event;
		
		saveToMetrics(event, groupCount);
		return event;
	}

	//-------------------------
	@Override
	protected Event handleTextUnit(Event event) {		
		TextUnit tu = (TextUnit) event.getResource();
		
		if (tu.isEmpty()) return event;
		if (!tu.isTranslatable() && countOnlyTranslatable()) return event;
		
		TextContainer source = tu.getSource();
		// Individual segments metrics
		ISegments segs = source.getSegments();
		if (segs != null) {
			for (Segment seg : segs) {
				segmentCount = count(seg);
				saveToMetrics(seg, segmentCount);
			}
		}
		
		// Whole TU metrics
		textUnitCount = count(tu);
		if (textUnitCount == 0) return event;
				
		saveToMetrics(source, textUnitCount);
				
		if (params.countInBatch) batchCount += textUnitCount;
		if (params.countInBatchItems) batchItemCount += textUnitCount;
		if (params.countInDocuments) documentCount += textUnitCount;
		if (params.countInSubDocuments) subDocumentCount += textUnitCount;
		if (params.countInGroups) groupCount += textUnitCount;
		return event;
	}		
}
